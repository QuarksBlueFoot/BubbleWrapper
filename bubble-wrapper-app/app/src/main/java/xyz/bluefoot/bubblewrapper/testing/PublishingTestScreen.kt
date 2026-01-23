package xyz.bluefoot.bubblewrapper.testing

import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import xyz.bluefoot.bubblewrapper.publishing.DappStoreCliWorkflow
import xyz.bluefoot.bubblewrapper.solana.WalletManager
import xyz.bluefoot.bubblewrapper.network.SolanaRepository

/**
 * Test/Demo screen for actual Solana dApp Store publishing
 * This screen can publish REAL apps to the REAL Solana Mobile dApp Store
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PublishingTestScreen(
    onBack: () -> Unit,
    walletManager: WalletManager,
    solanaRepository: SolanaRepository
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    
    val workflow = remember {
        DappStoreCliWorkflow(context, walletManager, solanaRepository)
    }
    
    var result by remember { mutableStateOf<DappStoreCliWorkflow.PublishingResult?>(null) }
    var progress by remember { mutableStateOf<DappStoreCliWorkflow.WorkflowProgress?>(null) }
    var isPublishing by remember { mutableStateOf(false) }
    var simulationMode by remember { mutableStateOf(true) }
    var enableArweave by remember { mutableStateOf(true) }
    var enableNFTs by remember { mutableStateOf(true) }
    var enablePortal by remember { mutableStateOf(true) }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Publishing Test") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            
            // Mode Selection
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = if (simulationMode)
                        MaterialTheme.colorScheme.tertiaryContainer
                    else
                        MaterialTheme.colorScheme.errorContainer
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = if (simulationMode) "🧪 SIMULATION MODE" else "⚠️ PRODUCTION MODE",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Switch(
                            checked = !simulationMode,
                            onCheckedChange = { 
                                simulationMode = !it
                                workflow.simulationMode = simulationMode
                            }
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text(
                        text = if (simulationMode)
                            "Uses mock data. Safe for testing. No real transactions."
                        else
                            "REAL MODE: Will create actual NFTs and submit to the real Solana Mobile dApp Store!",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Component Toggles
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Publishing Components",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    ComponentToggle(
                        label = "Arweave Uploads",
                        description = "Upload assets to permanent storage",
                        checked = enableArweave,
                        onCheckedChange = { 
                            enableArweave = it
                            workflow.enableRealArweaveUploads = it
                        }
                    )
                    
                    ComponentToggle(
                        label = "NFT Creation",
                        description = "Mint App and Release NFTs on-chain",
                        checked = enableNFTs,
                        onCheckedChange = { 
                            enableNFTs = it
                            workflow.enableRealNftCreation = it
                        }
                    )
                    
                    ComponentToggle(
                        label = "Portal Submission",
                        description = "Submit to Solana Mobile Publisher Portal",
                        checked = enablePortal,
                        onCheckedChange = { 
                            enablePortal = it
                            workflow.enableRealPortalSubmission = it
                        }
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Progress Display
            if (progress != null) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Publishing Progress",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        LinearProgressIndicator(
                            progress = progress!!.progress / progress!!.total.toFloat(),
                            modifier = Modifier.fillMaxWidth()
                        )
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        Text(
                            text = "${progress!!.stage}: ${progress!!.step}",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        
                        Text(
                            text = "${progress!!.progress}/${progress!!.total}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
            }
            
            // Result Display
            if (result != null) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = if (result!!.success)
                            MaterialTheme.colorScheme.primaryContainer
                        else
                            MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row {
                            Icon(
                                if (result!!.success) Icons.Filled.CheckCircle else Icons.Filled.Error,
                                contentDescription = null,
                                tint = if (result!!.success)
                                    MaterialTheme.colorScheme.primary
                                else
                                    MaterialTheme.colorScheme.error
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (result!!.success) "Success!" else "Failed",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        if (result!!.success) {
                            InfoItem("App NFT", result!!.appMintAddress ?: "N/A")
                            InfoItem("Release NFT", result!!.releaseMintAddress ?: "N/A")
                            InfoItem("Metadata URI", result!!.metadataUri ?: "N/A")
                            InfoItem("Portal Submitted", if (result!!.portalSubmitted) "✅ Yes" else "❌ No")
                            
                            Spacer(modifier = Modifier.height(8.dp))
                            
                            if (!simulationMode && result!!.portalSubmitted) {
                                Text(
                                    text = "🎉 Your app was submitted to the Solana Mobile dApp Store!",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Review typically takes 3-4 business days.",
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        } else {
                            Text(
                                text = "Error: ${result!!.error}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onErrorContainer
                            )
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Test Button
            Button(
                onClick = {
                    scope.launch {
                        isPublishing = true
                        result = null
                        progress = null
                        
                        // Create test config
                        val testConfig = createTestConfig()
                        
                        // Run workflow
                        result = workflow.publishToStore(testConfig) { prog ->
                            progress = prog
                        }
                        
                        isPublishing = false
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isPublishing
            ) {
                if (isPublishing) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Publishing...")
                } else {
                    Icon(Icons.Filled.Send, null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(if (simulationMode) "Test Publish (Simulated)" else "PUBLISH TO STORE (REAL)")
                }
            }
            
            if (!simulationMode) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "⚠️ This will create real NFTs and submit to the actual dApp Store",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@Composable
fun ComponentToggle(
    label: String,
    description: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
    }
}

@Composable
fun InfoItem(label: String, value: String) {
    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall,
            fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
        )
    }
}

/**
 * Create test configuration
 * In production, this would come from user input
 */
private fun createTestConfig(): DappStoreCliWorkflow.PublishingConfig {
    return DappStoreCliWorkflow.PublishingConfig(
        publisherName = "BubbleWrapper Test",
        publisherEmail = "test@bubblewrapper.app",
        publisherWebsite = "https://bubblewrapper.app",
        publisherSupportEmail = "support@bubblewrapper.app",
        
        appName = "Test App",
        androidPackage = "com.test.app",
        shortDescription = "Test dApp for Solana",
        longDescription = """
            This is a test application for the Solana Mobile dApp Store.
            
            It demonstrates the complete publishing workflow including:
            - Asset validation
            - Arweave/Irys uploads
            - Metaplex NFT creation
            - Publisher Portal submission
        """.trimIndent(),
        newInVersion = "Initial test release",
        sagaFeatures = "Optimized for Saga Mobile with MWA integration",
        
        licenseUrl = "https://bubblewrapper.app/license",
        copyrightUrl = "https://bubblewrapper.app/copyright",
        privacyPolicyUrl = "https://bubblewrapper.app/privacy",
        websiteUrl = "https://bubblewrapper.app",
        
        // Note: These would be real file URIs in production
        iconUri = Uri.parse("file:///mock/icon.png"),
        bannerUri = Uri.parse("file:///mock/banner.png"),
        screenshotUris = listOf(
            Uri.parse("file:///mock/screen1.png"),
            Uri.parse("file:///mock/screen2.png"),
            Uri.parse("file:///mock/screen3.png"),
            Uri.parse("file:///mock/screen4.png")
        ),
        apkUri = Uri.parse("file:///mock/app.apk"),
        
        category = "Tools",
        googlePlayPackage = null,
        testingInstructions = "Connect wallet and test all features",
        
        rpcUrl = "https://api.mainnet-beta.solana.com",
        walletPublicKey = "GGVjQqnriuUdeLPoadfrV6CrpToYPmDquSBqdFpYhBts" // MonkeMob wallet
    )
}
