package xyz.bluefoot.bubblewrapper.wallet

import android.content.Context
import android.net.Uri
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.solana.mobilewalletadapter.clientlib.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import org.bitcoinj.core.Base58
import java.util.Base64

// DataStore extension
val Context.walletDataStore: DataStore<Preferences> by preferencesDataStore(name = "wallet_prefs")

/**
 * Wallet connection state
 */
sealed class WalletState {
    object Disconnected : WalletState()
    object Connecting : WalletState()
    data class Connected(
        val publicKey: String,
        val walletName: String,
        val authToken: String
    ) : WalletState()
    data class Error(val message: String) : WalletState()
}

/**
 * Transaction request for signing
 */
data class TransactionRequest(
    val serializedTransaction: ByteArray,
    val description: String
)

/**
 * Manages Solana wallet connections using Mobile Wallet Adapter
 */
class WalletManager(private val context: Context) {
    
    companion object {
        private val KEY_PUBLIC_KEY = stringPreferencesKey("public_key")
        private val KEY_WALLET_NAME = stringPreferencesKey("wallet_name")
        private val KEY_AUTH_TOKEN = stringPreferencesKey("auth_token")
        
        // dApp identity for MWA
        private const val APP_IDENTITY_NAME = "Bubble Wrapper"
        private const val APP_IDENTITY_URI = "https://bubblewrapper.bluefoot.xyz"
        private const val APP_IDENTITY_ICON = "favicon.ico"
        
        // Cluster
        const val CLUSTER_MAINNET = "mainnet-beta"
        const val CLUSTER_DEVNET = "devnet"
        
        @Volatile
        private var instance: WalletManager? = null
        
        fun getInstance(context: Context): WalletManager {
            return instance ?: synchronized(this) {
                instance ?: WalletManager(context.applicationContext).also { instance = it }
            }
        }
    }
    
    private val _walletState = MutableStateFlow<WalletState>(WalletState.Disconnected)
    val walletState: StateFlow<WalletState> = _walletState.asStateFlow()
    
    private val _balance = MutableStateFlow<Double?>(null)
    val balance: StateFlow<Double?> = _balance.asStateFlow()
    
    // Store ActivityResultSender for later use
    private var activityResultSender: ActivityResultSender? = null
    
    // Mobile Wallet Adapter client
    // Note: iconUri should be RELATIVE to identityUri, not absolute
    private val walletAdapter = MobileWalletAdapter(
        connectionIdentity = ConnectionIdentity(
            identityUri = Uri.parse(APP_IDENTITY_URI),
            iconUri = Uri.parse(APP_IDENTITY_ICON),  // Relative path per MWA spec
            identityName = APP_IDENTITY_NAME
        )
    ).apply {
        // Set to Mainnet for production use
        blockchain = Solana.Mainnet
    }
    
    /**
     * Initialize and restore previous session if available
     * Only restores if not already connected (to preserve in-memory state)
     */
    suspend fun initialize() {
        // Don't overwrite if already connected - preserves current session
        if (_walletState.value is WalletState.Connected) {
            return
        }
        
        try {
            val prefs = context.walletDataStore.data.first()
            val publicKey = prefs[KEY_PUBLIC_KEY]
            val walletName = prefs[KEY_WALLET_NAME]
            val authToken = prefs[KEY_AUTH_TOKEN]
            
            if (publicKey != null && authToken != null) {
                _walletState.value = WalletState.Connected(
                    publicKey = publicKey,
                    walletName = walletName ?: "Unknown Wallet",
                    authToken = authToken
                )
            }
        } catch (e: Exception) {
            // No saved session, stay disconnected
        }
    }
    
    /**
     * Connect to a Solana wallet using Mobile Wallet Adapter
     * NOTE: sender must be created in Activity.onCreate() before setContent
     */
    suspend fun connect(
        sender: ActivityResultSender,
        cluster: String = CLUSTER_MAINNET
    ): Result<WalletState.Connected> {
        // Store sender for later use
        activityResultSender = sender
        _walletState.value = WalletState.Connecting
        
        return try {
            val result = walletAdapter.transact(sender) { _ ->
                // Authorize the dApp
                // Note: iconUri should be RELATIVE to identityUri per MWA spec
                val authResult = authorize(
                    identityUri = Uri.parse(APP_IDENTITY_URI),
                    iconUri = Uri.parse(APP_IDENTITY_ICON),  // Relative path
                    identityName = APP_IDENTITY_NAME
                )
                
                authResult
            }
            
            when (result) {
                is TransactionResult.Success -> {
                    val authResult = result.payload
                    // Use Base58 encoding for Solana public keys (NOT Base64!)
                    val publicKeyBase58 = Base58.encode(authResult.publicKey)
                    
                    val connectedState = WalletState.Connected(
                        publicKey = publicKeyBase58,
                        walletName = authResult.walletUriBase?.host ?: "Solana Wallet",
                        authToken = authResult.authToken
                    )
                    
                    // Persist session
                    context.walletDataStore.edit { prefs ->
                        prefs[KEY_PUBLIC_KEY] = publicKeyBase58
                        prefs[KEY_WALLET_NAME] = connectedState.walletName
                        prefs[KEY_AUTH_TOKEN] = authResult.authToken
                    }
                    
                    _walletState.value = connectedState
                    Result.success(connectedState)
                }
                is TransactionResult.Failure -> {
                    val error = WalletState.Error("Connection failed: ${result.message}")
                    _walletState.value = error
                    Result.failure(Exception(result.message))
                }
                is TransactionResult.NoWalletFound -> {
                    val error = WalletState.Error("No Solana wallet found. Please install Phantom, Solflare, or another Solana wallet.")
                    _walletState.value = error
                    Result.failure(Exception("No wallet found"))
                }
            }
        } catch (e: Exception) {
            val error = WalletState.Error("Connection error: ${e.message}")
            _walletState.value = error
            Result.failure(e)
        }
    }
    
    /**
     * Sign and send a transaction
     */
    suspend fun signAndSendTransaction(
        sender: ActivityResultSender,
        transaction: ByteArray,
        cluster: String = CLUSTER_MAINNET
    ): Result<String> {
        val currentState = _walletState.value
        if (currentState !is WalletState.Connected) {
            return Result.failure(Exception("Wallet not connected"))
        }
        
        return try {
            val result = walletAdapter.transact(sender) {
                // Reauthorize with current session (optional but recommended)
                // Note: iconUri must be RELATIVE per MWA spec
                try {
                    reauthorize(
                        identityUri = Uri.parse(APP_IDENTITY_URI),
                        iconUri = Uri.parse(APP_IDENTITY_ICON),  // Relative path
                        identityName = APP_IDENTITY_NAME,
                        authToken = currentState.authToken
                    )
                } catch (e: Exception) {
                    // Reauthorization optional - continue with sign if it fails
                    android.util.Log.w("WalletManager", "Reauth skipped: ${e.message}")
                }
                
                // Sign and send the transaction
                // CRITICAL: Return the result from this lambda - the wallet stays open until this completes!
                signAndSendTransactions(
                    transactions = arrayOf(transaction)
                )
            }
            
            when (result) {
                is TransactionResult.Success -> {
                    val signResult = result.payload
                    if (signResult.signatures.isNotEmpty()) {
                        // MWA returns raw signature bytes - encode as Base58 for Solana
                        val signature = Base58.encode(signResult.signatures.first())
                        Result.success(signature)
                    } else {
                        Result.failure(Exception("No signature returned"))
                    }
                }
                is TransactionResult.Failure -> {
                    Result.failure(Exception(result.message))
                }
                is TransactionResult.NoWalletFound -> {
                    Result.failure(Exception("Wallet not found"))
                }
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Sign a message (for verification)
     */
    suspend fun signMessage(
        sender: ActivityResultSender,
        message: ByteArray
    ): Result<ByteArray> {
        val currentState = _walletState.value
        if (currentState !is WalletState.Connected) {
            return Result.failure(Exception("Wallet not connected"))
        }
        
        return try {
            val result = walletAdapter.transact(sender) { _ ->
                // Note: iconUri must be RELATIVE per MWA spec
                val reauth = reauthorize(
                    identityUri = Uri.parse(APP_IDENTITY_URI),
                    iconUri = Uri.parse(APP_IDENTITY_ICON),  // Relative path
                    identityName = APP_IDENTITY_NAME,
                    authToken = currentState.authToken
                )
                
                signMessages(
                    messages = arrayOf(message),
                    // Decode Base58 public key back to bytes for MWA
                    addresses = arrayOf(Base58.decode(currentState.publicKey))
                )
            }
            
            when (result) {
                is TransactionResult.Success -> {
                    val signatures = result.payload.signedPayloads
                    if (signatures.isNotEmpty()) {
                        Result.success(signatures.first())
                    } else {
                        Result.failure(Exception("No signature returned"))
                    }
                }
                is TransactionResult.Failure -> {
                    Result.failure(Exception(result.message))
                }
                is TransactionResult.NoWalletFound -> {
                    Result.failure(Exception("Wallet not found"))
                }
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Disconnect wallet and clear session
     */
    suspend fun disconnect() {
        context.walletDataStore.edit { prefs ->
            prefs.remove(KEY_PUBLIC_KEY)
            prefs.remove(KEY_WALLET_NAME)
            prefs.remove(KEY_AUTH_TOKEN)
        }
        _walletState.value = WalletState.Disconnected
        _balance.value = null
    }
    
    /**
     * Get current public key if connected
     */
    fun getPublicKey(): String? {
        return when (val state = _walletState.value) {
            is WalletState.Connected -> state.publicKey
            else -> null
        }
    }
    
    /**
     * Get stored ActivityResultSender for transaction signing
     */
    fun getActivityResultSender(): ActivityResultSender? {
        return activityResultSender
    }
    
    /**
     * Check if wallet is connected
     */
    fun isConnected(): Boolean {
        return _walletState.value is WalletState.Connected
    }
    
    /**
     * Format public key for display (shortened)
     */
    fun formatPublicKey(publicKey: String): String {
        return if (publicKey.length > 8) {
            "${publicKey.take(4)}...${publicKey.takeLast(4)}"
        } else {
            publicKey
        }
    }
}
