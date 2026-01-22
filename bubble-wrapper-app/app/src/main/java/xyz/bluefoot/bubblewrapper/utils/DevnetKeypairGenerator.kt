package xyz.bluefoot.bubblewrapper.utils

import org.bitcoinj.core.Base58
import org.bouncycastle.crypto.params.Ed25519PrivateKeyParameters
import org.bouncycastle.crypto.params.Ed25519PublicKeyParameters
import org.bouncycastle.crypto.generators.Ed25519KeyPairGenerator
import org.bouncycastle.crypto.KeyGenerationParameters
import java.security.SecureRandom
import android.util.Log

/**
 * Utility to generate Solana keypairs for devnet testing
 */
object DevnetKeypairGenerator {
    private const val TAG = "DevnetKeypairGen"
    
    data class SolanaKeypair(
        val publicKey: String,
        val secretKey: ByteArray,
        val secretKeyBase58: String
    ) {
        fun toJson(): String {
            val secretKeyArray = secretKey.joinToString(",")
            return "[$secretKeyArray]"
        }
    }
    
    /**
     * Generate a new Ed25519 keypair for Solana
     */
    fun generateKeypair(): SolanaKeypair {
        val generator = Ed25519KeyPairGenerator()
        generator.init(KeyGenerationParameters(SecureRandom(), 256))
        val keyPair = generator.generateKeyPair()
        
        val privateKeyParams = keyPair.private as Ed25519PrivateKeyParameters
        val publicKeyParams = keyPair.public as Ed25519PublicKeyParameters
        
        val privateKey = privateKeyParams.encoded
        val publicKey = publicKeyParams.encoded
        
        // Solana format: [private_key(32), public_key(32)]
        val secretKey = ByteArray(64)
        System.arraycopy(privateKey, 0, secretKey, 0, 32)
        System.arraycopy(publicKey, 0, secretKey, 32, 32)
        
        val publicKeyBase58 = Base58.encode(publicKey)
        val secretKeyBase58 = Base58.encode(secretKey)
        
        Log.d(TAG, """
            ════════════════════════════════════════════════════
            🔑 NEW DEVNET KEYPAIR GENERATED
            ════════════════════════════════════════════════════
            
            Public Key (Base58):
            $publicKeyBase58
            
            ────────────────────────────────────────────────────
            
            🚰 Fund this address with devnet SOL:
            https://faucet.solana.com/
            
            Or use CLI:
            solana airdrop 2 $publicKeyBase58 --url devnet
            
            ────────────────────────────────────────────────────
            
            Secret Key (for import, keep secure!):
            $secretKeyBase58
            
            ════════════════════════════════════════════════════
        """.trimIndent())
        
        return SolanaKeypair(
            publicKey = publicKeyBase58,
            secretKey = secretKey,
            secretKeyBase58 = secretKeyBase58
        )
    }
    
    /**
     * Generate and save to logcat for easy copying
     */
    fun generateAndLog() {
        val keypair = generateKeypair()
        
        println("═══════════════════════════════════════════════════════════")
        println("🔑 DEVNET TEST KEYPAIR")
        println("═══════════════════════════════════════════════════════════")
        println()
        println("Public Key:")
        println(keypair.publicKey)
        println()
        println("Fund at: https://faucet.solana.com/")
        println()
        println("Secret Key Base58:")
        println(keypair.secretKeyBase58)
        println()
        println("Secret Key JSON (for solana CLI):")
        println(keypair.toJson())
        println()
        println("═══════════════════════════════════════════════════════════")
    }
}
