package xyz.bluefoot.bubblewrapper.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import xyz.bluefoot.bubblewrapper.LocalActivityResultSender
import xyz.bluefoot.bubblewrapper.network.DappStoreService
import xyz.bluefoot.bubblewrapper.network.SolanaRepository
import xyz.bluefoot.bubblewrapper.ui.theme.*
import xyz.bluefoot.bubblewrapper.wallet.WalletManager
import xyz.bluefoot.bubblewrapper.wallet.WalletState

/**
 * Enhanced Wallet Connection Widget
 * Shows wallet status and allows connect/disconnect
 */
@Composable
fun WalletConnectionCard(
    walletManager: WalletManager,
    solanaRepository: SolanaRepository,
    onConnected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val walletState by walletManager.walletState.collectAsState()
    val balance by walletManager.balance.collectAsState()
    
    var isLoadingBalance by remember { mutableStateOf(false) }
    var balanceValue by remember { mutableStateOf<Double?>(null) }
    
    // Load balance when connected
    LaunchedEffect(walletState) {
        if (walletState is WalletState.Connected) {
            isLoadingBalance = true
            val publicKey = (walletState as WalletState.Connected).publicKey
            solanaRepository.getBalance(publicKey).onSuccess {
                balanceValue = it
            }
            isLoadingBalance = false
            onConnected(publicKey)
        }
    }
    
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = when (walletState) {
                is WalletState.Connected -> SolanaGreen.copy(alpha = 0.08f)
                is WalletState.Connecting -> SolanaPurple.copy(alpha = 0.08f)
                is WalletState.Error -> Color(0xFFFF5C7A).copy(alpha = 0.08f)
                else -> Color(0x0DFFFFFF)
            }
        ),
        border = BorderStroke(
            1.dp,
            when (walletState) {
                is WalletState.Connected -> SolanaGreen.copy(alpha = 0.3f)
                is WalletState.Connecting -> SolanaPurple.copy(alpha = 0.3f)
                is WalletState.Error -> Color(0xFFFF5C7A).copy(alpha = 0.3f)
                else -> GlassBorder
            }
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Wallet icon with status indicator
                Box {
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(
                                when (walletState) {
                                    is WalletState.Connected -> SolanaGreen.copy(alpha = 0.15f)
                                    is WalletState.Connecting -> SolanaPurple.copy(alpha = 0.15f)
                                    else -> Color(0x15FFFFFF)
                                }
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        if (walletState is WalletState.Connecting) {
                            val infiniteTransition = rememberInfiniteTransition(label = "spin")
                            val rotation by infiniteTransition.animateFloat(
                                initialValue = 0f,
                                targetValue = 360f,
                                animationSpec = infiniteRepeatable(
                                    animation = tween(1000, easing = LinearEasing),
                                    repeatMode = RepeatMode.Restart
                                ),
                                label = "rotation"
                            )
                            Icon(
                                Icons.Filled.Sync,
                                contentDescription = null,
                                tint = SolanaPurple,
                                modifier = Modifier
                                    .size(28.dp)
                                    .rotate(rotation)
                            )
                        } else {
                            Icon(
                                Icons.Filled.AccountBalanceWallet,
                                contentDescription = null,
                                tint = when (walletState) {
                                    is WalletState.Connected -> SolanaGreen
                                    else -> TextMuted
                                },
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }
                    
                    // Status dot
                    if (walletState is WalletState.Connected) {
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .offset(x = 4.dp, y = (-4).dp)
                                .size(14.dp)
                                .clip(CircleShape)
                                .background(BgPrimary)
                                .padding(2.dp)
                                .clip(CircleShape)
                                .background(SolanaGreen)
                        )
                    }
                }
                
                Spacer(modifier = Modifier.width(16.dp))
                
                // Wallet info
                Column(modifier = Modifier.weight(1f)) {
                    when (val state = walletState) {
                        is WalletState.Connected -> {
                            Text(
                                text = state.walletName,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = TextPrimary
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = walletManager.formatPublicKey(state.publicKey),
                                style = MaterialTheme.typography.bodySmall,
                                fontFamily = FontFamily.Monospace,
                                color = SolanaGreen
                            )
                        }
                        is WalletState.Connecting -> {
                            Text(
                                text = "Connecting...",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = TextPrimary
                            )
                            Text(
                                text = "Approve in your wallet app",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextMuted
                            )
                        }
                        is WalletState.Error -> {
                            Text(
                                text = "Connection Failed",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFFFF5C7A)
                            )
                            Text(
                                text = state.message,
                                style = MaterialTheme.typography.bodySmall,
                                color = TextMuted,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                        else -> {
                            Text(
                                text = "No Wallet Connected",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = TextPrimary
                            )
                            Text(
                                text = "Connect to publish on-chain",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextMuted
                            )
                        }
                    }
                }
                
                // Balance (if connected)
                if (walletState is WalletState.Connected) {
                    Column(horizontalAlignment = Alignment.End) {
                        if (isLoadingBalance) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                strokeWidth = 2.dp,
                                color = SolanaPurple
                            )
                        } else {
                            Text(
                                text = String.format("%.4f", balanceValue ?: 0.0),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Text(
                                text = "SOL",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextMuted
                            )
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Connect/Disconnect button
            when (walletState) {
                is WalletState.Connected -> {
                    OutlinedButton(
                        onClick = {
                            scope.launch {
                                walletManager.disconnect()
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = TextMuted
                        ),
                        border = BorderStroke(1.dp, GlassBorder)
                    ) {
                        Icon(
                            Icons.Filled.LinkOff,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Disconnect Wallet")
                    }
                }
                is WalletState.Connecting -> {
                    Button(
                        onClick = { },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = false,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = SolanaPurple.copy(alpha = 0.5f)
                        )
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(18.dp),
                            strokeWidth = 2.dp,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Connecting...")
                    }
                }
                else -> {
                    // Get the ActivityResultSender from CompositionLocal
                    val activityResultSender = LocalActivityResultSender.current
                    
                    Button(
                        onClick = {
                            scope.launch {
                                if (activityResultSender != null) {
                                    walletManager.connect(activityResultSender)
                                } else {
                                    Toast.makeText(
                                        context,
                                        "Unable to connect: Wallet adapter not initialized",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Transparent
                        ),
                        contentPadding = PaddingValues()
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .background(
                                    Brush.horizontalGradient(
                                        colors = listOf(SolanaPurple, SolanaPurpleDark)
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Filled.Link,
                                    contentDescription = null,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    "Connect Solana Wallet",
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                }
            }
            
            // Low balance warning
            if (walletState is WalletState.Connected && balanceValue != null && balanceValue!! < 0.1) {
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFFFF5C7A).copy(alpha = 0.1f))
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Filled.Warning,
                        contentDescription = null,
                        tint = Color(0xFFFF5C7A),
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Low balance! Publishing requires ~0.15 SOL",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFFFF5C7A)
                    )
                }
            }
        }
    }
}

/**
 * Publishing Progress/Status Card
 */
@Composable
fun PublishingStatusCard(
    currentStep: Int,
    steps: List<String>,
    isPublishing: Boolean,
    error: String?,
    transactionSignature: String?,
    modifier: Modifier = Modifier
) {
    val uriHandler = LocalUriHandler.current
    
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = when {
                error != null -> Color(0xFFFF5C7A).copy(alpha = 0.08f)
                transactionSignature != null -> SolanaGreen.copy(alpha = 0.08f)
                else -> Color(0x0DFFFFFF)
            }
        ),
        border = BorderStroke(1.dp, GlassBorder)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = when {
                        error != null -> "Publishing Failed"
                        transactionSignature != null -> "Published Successfully! 🎉"
                        isPublishing -> "Publishing..."
                        else -> "Ready to Publish"
                    },
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = when {
                        error != null -> Color(0xFFFF5C7A)
                        transactionSignature != null -> SolanaGreen
                        else -> TextPrimary
                    }
                )
                
                Spacer(modifier = Modifier.weight(1f))
                
                if (isPublishing) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp,
                        color = SolanaPurple
                    )
                }
            }
            
            if (error != null) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = error,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFFFF5C7A)
                )
            }
            
            if (transactionSignature != null) {
                Spacer(modifier = Modifier.height(12.dp))
                
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(SolanaGreen.copy(alpha = 0.1f))
                        .clickable {
                            uriHandler.openUri("https://explorer.solana.com/tx/$transactionSignature")
                        }
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Filled.OpenInNew,
                        contentDescription = null,
                        tint = SolanaGreen,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "View on Solana Explorer",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.SemiBold,
                            color = SolanaGreen
                        )
                        Text(
                            text = "${transactionSignature.take(16)}...",
                            style = MaterialTheme.typography.bodySmall,
                            fontFamily = FontFamily.Monospace,
                            color = TextMuted
                        )
                    }
                    Icon(
                        Icons.Filled.ChevronRight,
                        contentDescription = null,
                        tint = SolanaGreen
                    )
                }
            }
            
            if (isPublishing && steps.isNotEmpty()) {
                Spacer(modifier = Modifier.height(16.dp))
                
                steps.forEachIndexed { index, step ->
                    Row(
                        modifier = Modifier.padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        when {
                            index < currentStep -> Icon(
                                Icons.Filled.CheckCircle,
                                contentDescription = null,
                                tint = SolanaGreen,
                                modifier = Modifier.size(18.dp)
                            )
                            index == currentStep -> {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(18.dp),
                                    strokeWidth = 2.dp,
                                    color = SolanaPurple
                                )
                            }
                            else -> Icon(
                                Icons.Filled.RadioButtonUnchecked,
                                contentDescription = null,
                                tint = TextMuted,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = step,
                            style = MaterialTheme.typography.bodyMedium,
                            color = when {
                                index <= currentStep -> TextPrimary
                                else -> TextMuted
                            }
                        )
                    }
                }
            }
        }
    }
}

/**
 * RPC Endpoint Selector
 */
@Composable
fun RpcSelector(
    selectedRpc: String,
    onRpcSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    
    // Check if Helius API key is available (injected via BuildConfig)
    val heliusApiKey = try {
        xyz.bluefoot.bubblewrapper.BuildConfig.HELIUS_API_KEY
    } catch (e: Exception) {
        ""
    }
    val hasHeliusKey = heliusApiKey.isNotBlank()
    
    val rpcOptions = buildList {
        add("Mainnet (Public)" to "https://api.mainnet-beta.solana.com")
        if (hasHeliusKey) {
            add("Helius (Recommended)" to "https://mainnet.helius-rpc.com/?api-key=$heliusApiKey")
        }
        add("Devnet (Testing)" to "https://api.devnet.solana.com")
        add("Custom RPC" to "")
    }
    
    var customRpc by remember { mutableStateOf("") }
    var showCustomInput by remember { mutableStateOf(false) }
    
    Column(modifier = modifier) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = !expanded },
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0x0DFFFFFF)
            ),
            border = BorderStroke(1.dp, GlassBorder)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Filled.Cloud,
                    contentDescription = null,
                    tint = SolanaPurple,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "RPC Endpoint",
                        style = MaterialTheme.typography.labelMedium,
                        color = TextMuted
                    )
                    Text(
                        text = selectedRpc.take(40) + if (selectedRpc.length > 40) "..." else "",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Icon(
                    if (expanded) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                    contentDescription = null,
                    tint = TextMuted
                )
            }
        }
        
        AnimatedVisibility(visible = expanded) {
            Column {
                Spacer(modifier = Modifier.height(8.dp))
                
                rpcOptions.forEach { (name, url) ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .clickable {
                                if (url.isEmpty()) {
                                    showCustomInput = true
                                } else {
                                    onRpcSelected(url)
                                    expanded = false
                                }
                            }
                            .background(
                                if (selectedRpc == url) SolanaPurple.copy(alpha = 0.15f)
                                else Color.Transparent
                            )
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = selectedRpc == url,
                            onClick = {
                                if (url.isEmpty()) {
                                    showCustomInput = true
                                } else {
                                    onRpcSelected(url)
                                    expanded = false
                                }
                            },
                            colors = RadioButtonDefaults.colors(
                                selectedColor = SolanaPurple,
                                unselectedColor = TextMuted
                            )
                        )
                        Text(
                            text = name,
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextPrimary
                        )
                    }
                }
                
                if (showCustomInput) {
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = customRpc,
                        onValueChange = { customRpc = it },
                        placeholder = { Text("https://your-rpc.com", color = TextMuted) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = SolanaPurple,
                            unfocusedBorderColor = GlassBorder,
                            cursorColor = SolanaPurple,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        ),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true,
                        trailingIcon = {
                            IconButton(
                                onClick = {
                                    if (customRpc.isNotBlank()) {
                                        onRpcSelected(customRpc)
                                        expanded = false
                                        showCustomInput = false
                                    }
                                }
                            ) {
                                Icon(
                                    Icons.Filled.Check,
                                    contentDescription = "Apply",
                                    tint = SolanaGreen
                                )
                            }
                        }
                    )
                }
            }
        }
    }
}

/**
 * Supported Wallets Info
 */
@Composable
fun SupportedWalletsInfo(modifier: Modifier = Modifier) {
    val uriHandler = LocalUriHandler.current
    
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Supports: ",
            style = MaterialTheme.typography.bodySmall,
            color = TextMuted
        )
        
        listOf("Phantom", "Solflare", "Backpack").forEachIndexed { index, wallet ->
            if (index > 0) {
                Text(
                    text = " • ",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextMuted
                )
            }
            Text(
                text = wallet,
                style = MaterialTheme.typography.bodySmall,
                color = SolanaPurple,
                modifier = Modifier.clickable {
                    val url = when (wallet) {
                        "Phantom" -> "https://phantom.app"
                        "Solflare" -> "https://solflare.com"
                        "Backpack" -> "https://backpack.app"
                        else -> ""
                    }
                    if (url.isNotEmpty()) uriHandler.openUri(url)
                }
            )
        }
    }
}

/**
 * Estimated Cost Card
 */
@Composable
fun EstimatedCostCard(modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = SolanaPurple.copy(alpha = 0.08f)
        ),
        border = BorderStroke(1.dp, SolanaPurple.copy(alpha = 0.2f))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Filled.Paid,
                contentDescription = null,
                tint = SolanaPurple,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Estimated Publishing Cost",
                    style = MaterialTheme.typography.labelMedium,
                    color = TextMuted
                )
                Text(
                    text = "~0.1 - 0.2 SOL",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "Includes:",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextMuted
                )
                Text(
                    text = "NFT mint + storage",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
            }
        }
    }
}
