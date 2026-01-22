package xyz.bluefoot.bubblewrapper.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import xyz.bluefoot.bubblewrapper.network.DappStorePublisher
import xyz.bluefoot.bubblewrapper.network.DappStoreService
import xyz.bluefoot.bubblewrapper.network.SolanaRepository
import xyz.bluefoot.bubblewrapper.ui.theme.*
import xyz.bluefoot.bubblewrapper.utils.ManifestParser
import xyz.bluefoot.bubblewrapper.wallet.WalletManager
import xyz.bluefoot.bubblewrapper.wallet.WalletState

// Data class for publishing configuration
data class PublishConfig(
    val appName: String = "",
    val shortDescription: String = "",
    val fullDescription: String = "",
    val packageId: String = "",
    val versionCode: Int = 1,
    val versionName: String = "1.0.0",
    val category: String = "Finance",
    val apkPath: String = "",
    val iconPath: String = "",
    val bannerPath: String = "",
    val screenshots: List<String> = emptyList(),
    val keypairPath: String = "",
    val rpcUrl: String = "https://api.mainnet-beta.solana.com",
    val whatsNew: String = "",
    val publisherEmail: String = "",
    val publisherWebsite: String = "",
    val walletAddress: String = ""
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PublishScreen(onBack: () -> Unit) {
    var config by remember { mutableStateOf(PublishConfig()) }
    var currentStep by remember { mutableStateOf(0) }
    val context = LocalContext.current
    val uriHandler = LocalUriHandler.current
    val scope = rememberCoroutineScope()
    
    // Manifest parser
    val manifestParser = remember { ManifestParser(context) }
    
    // Auto-fill dialog state
    var showAutoFillDialog by remember { mutableStateOf(false) }
    var manifestUrl by remember { mutableStateOf("") }
    var isLoadingManifest by remember { mutableStateOf(false) }
    var autoFillError by remember { mutableStateOf<String?>(null) }
    
    // Wallet and Solana integration
    val walletManager = remember { WalletManager.getInstance(context) }
    val solanaRepository = remember { SolanaRepository(config.rpcUrl) }
    val walletState by walletManager.walletState.collectAsState()
    
    // Initialize wallet manager
    LaunchedEffect(Unit) {
        walletManager.initialize()
    }
    
    // Auto-fill from manifest dialog
    if (showAutoFillDialog) {
        AlertDialog(
            onDismissRequest = { 
                if (!isLoadingManifest) {
                    showAutoFillDialog = false 
                    autoFillError = null
                }
            },
            title = { 
                Text("Auto-Fill from Manifest", color = TextPrimary) 
            },
            text = {
                Column {
                    Text(
                        "Enter your PWA manifest URL to auto-fill app details:",
                        color = TextMuted,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = manifestUrl,
                        onValueChange = { 
                            manifestUrl = it
                            autoFillError = null
                        },
                        label = { Text("Manifest URL") },
                        placeholder = { Text("https://example.com/manifest.json", color = TextMuted) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = textFieldColors(),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true,
                        enabled = !isLoadingManifest,
                        leadingIcon = {
                            Icon(Icons.Default.Link, contentDescription = null, tint = TextMuted)
                        }
                    )
                    
                    autoFillError?.let { error ->
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = error,
                            color = Color(0xFFEF4444),
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                    
                    if (isLoadingManifest) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = SolanaPurple,
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                "Fetching manifest...",
                                color = TextMuted,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Text(
                        "💡 Tip: The manifest URL usually ends in .webmanifest or manifest.json",
                        color = TextMuted,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (manifestUrl.isNotBlank()) {
                            scope.launch {
                                isLoadingManifest = true
                                autoFillError = null
                                
                                val result = manifestParser.fetchManifest(manifestUrl)
                                
                                result.fold(
                                    onSuccess = { manifest ->
                                        // Update config with manifest data
                                        config = config.copy(
                                            appName = manifest.name.ifEmpty { manifest.shortName },
                                            shortDescription = manifest.description.take(80),
                                            fullDescription = manifest.description,
                                            category = manifestParser.mapToSolanaCategory(manifest.categories)
                                        )
                                        
                                        Toast.makeText(
                                            context,
                                            "✅ Manifest loaded! App details filled.",
                                            Toast.LENGTH_LONG
                                        ).show()
                                        
                                        showAutoFillDialog = false
                                        manifestUrl = ""
                                    },
                                    onFailure = { error ->
                                        autoFillError = "Failed to fetch manifest: ${error.message}"
                                    }
                                )
                                
                                isLoadingManifest = false
                            }
                        }
                    },
                    enabled = manifestUrl.isNotBlank() && !isLoadingManifest
                ) {
                    Text("Fetch & Apply", color = if (manifestUrl.isNotBlank() && !isLoadingManifest) SolanaGreen else TextMuted)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { 
                        showAutoFillDialog = false 
                        autoFillError = null
                    },
                    enabled = !isLoadingManifest
                ) {
                    Text("Cancel", color = TextMuted)
                }
            },
            containerColor = BgSecondary,
            shape = RoundedCornerShape(16.dp)
        )
    }
    
    Scaffold(
        containerColor = BgPrimary,
        topBar = {
            TopAppBar(
                title = { 
                    Text("Publish to dApp Store", color = TextPrimary) 
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = TextPrimary
                        )
                    }
                },
                actions = {
                    // Auto-fill from manifest button
                    IconButton(
                        onClick = { showAutoFillDialog = true },
                        modifier = Modifier.padding(end = 4.dp)
                    ) {
                        Icon(
                            Icons.Default.AutoAwesome,
                            contentDescription = "Auto-fill from Manifest",
                            tint = SolanaPurple
                        )
                    }
                    
                    // Wallet status indicator in top bar
                    when (walletState) {
                        is WalletState.Connected -> {
                            Row(
                                modifier = Modifier
                                    .padding(end = 12.dp)
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(SolanaGreen.copy(alpha = 0.15f))
                                    .padding(horizontal = 12.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(SolanaGreen)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = walletManager.formatPublicKey(
                                        (walletState as WalletState.Connected).publicKey
                                    ),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = SolanaGreen
                                )
                            }
                        }
                        else -> {}
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = BgPrimary
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
        ) {
            // Progress indicator
            PublishProgressBar(currentStep = currentStep)
            
            Spacer(modifier = Modifier.height(24.dp))
            
            when (currentStep) {
                0 -> WalletSetupStep(
                    config = config,
                    onConfigChange = { config = it },
                    walletManager = walletManager,
                    solanaRepository = solanaRepository,
                    onNext = { currentStep = 1 }
                )
                1 -> AssetsStep(
                    config = config,
                    onConfigChange = { config = it },
                    onNext = { currentStep = 2 },
                    onBack = { currentStep = 0 }
                )
                2 -> DetailsStep(
                    config = config,
                    onConfigChange = { config = it },
                    onNext = { currentStep = 3 },
                    onBack = { currentStep = 1 }
                )
                3 -> PublishStep(
                    config = config,
                    walletManager = walletManager,
                    solanaRepository = solanaRepository,
                    onBack = { currentStep = 2 }
                )
            }
            
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun PublishProgressBar(currentStep: Int) {
    val steps = listOf("Setup", "Assets", "Details", "Publish")
    
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        steps.forEachIndexed { index, step ->
            val isActive = index <= currentStep
            val isCompleted = index < currentStep
            
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(
                            if (isActive) {
                                Brush.linearGradient(
                                    colors = listOf(SolanaPurple, SolanaGreen)
                                )
                            } else {
                                Brush.linearGradient(
                                    colors = listOf(BgSecondary, BgSecondary)
                                )
                            }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    if (isCompleted) {
                        Icon(
                            Icons.Filled.Check,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    } else {
                        Text(
                            text = (index + 1).toString(),
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = if (isActive) Color.White else TextMuted
                        )
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = step,
                    style = MaterialTheme.typography.labelSmall,
                    color = if (isActive) TextPrimary else TextMuted
                )
            }
            
            if (index < steps.lastIndex) {
                Box(
                    modifier = Modifier
                        .height(2.dp)
                        .weight(0.5f)
                        .background(
                            if (index < currentStep) SolanaGreen else GlassBorder
                        )
                )
            }
        }
    }
}

@Composable
fun SetupStep(
    config: PublishConfig,
    onConfigChange: (PublishConfig) -> Unit,
    onNext: () -> Unit
) {
    val uriHandler = LocalUriHandler.current
    
    Column {
        SectionHeader(
            title = "1. Install dApp Store CLI",
            subtitle = "Required for publishing to Solana Mobile"
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        PublishCard {
            Text(
                text = "Prerequisites",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            ChecklistItem(text = "Node.js v18 - v21", checked = true)
            ChecklistItem(text = "pnpm package manager", checked = true)
            ChecklistItem(text = "Solana wallet with ~0.2 SOL", checked = true)
            ChecklistItem(text = "Android SDK Build Tools", checked = true)
            ChecklistItem(text = "ffmpeg (for video assets)", checked = false)
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        PublishCard {
            Text(
                text = "Install CLI",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            CommandBlock(
                command = """mkdir publishing
cd publishing
pnpm init
pnpm install --save-dev @solana-mobile/dapp-store-cli
npx dapp-store init"""
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Text(
                text = "This creates a config.yaml file for your app details.",
                style = MaterialTheme.typography.bodySmall,
                color = TextMuted
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        PublishCard {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Keypair Path",
                        style = MaterialTheme.typography.labelMedium,
                        color = TextSecondary
                    )
                    Text(
                        text = "Your Solana wallet keypair file",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextMuted
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            OutlinedTextField(
                value = config.keypairPath,
                onValueChange = { onConfigChange(config.copy(keypairPath = it)) },
                placeholder = { Text("/path/to/keypair.json", color = TextMuted) },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = SolanaPurple,
                    unfocusedBorderColor = GlassBorder,
                    cursorColor = SolanaPurple,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary
                ),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        PublishCard {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "RPC Endpoint",
                        style = MaterialTheme.typography.labelMedium,
                        color = TextSecondary
                    )
                    Text(
                        text = "Private RPC recommended for reliability",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextMuted
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            OutlinedTextField(
                value = config.rpcUrl,
                onValueChange = { onConfigChange(config.copy(rpcUrl = it)) },
                placeholder = { Text("https://api.mainnet-beta.solana.com", color = TextMuted) },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = SolanaPurple,
                    unfocusedBorderColor = GlassBorder,
                    cursorColor = SolanaPurple,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary
                ),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        GradientButton(
            text = "Next: Upload Assets →",
            onClick = onNext,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun AssetsStep(
    config: PublishConfig,
    onConfigChange: (PublishConfig) -> Unit,
    onNext: () -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val manifestParser = remember { ManifestParser(context) }
    var isReadingApk by remember { mutableStateOf(false) }
    
    // File picker launchers
    val apkPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { apkUri ->
            onConfigChange(config.copy(apkPath = apkUri.toString()))
            
            // Try to extract APK metadata and embedded manifest
            scope.launch {
                isReadingApk = true
                val result = manifestParser.readApkMetadata(apkUri)
                result.fold(
                    onSuccess = { metadata ->
                        // Get description and category from embedded TWA manifest if available
                        val twaManifest = metadata.twaManifest
                        val description = twaManifest?.description ?: ""
                        val category = if (twaManifest?.categories?.isNotEmpty() == true) {
                            manifestParser.mapToSolanaCategory(twaManifest.categories)
                        } else {
                            config.category
                        }
                        
                        // Update config with APK metadata if fields are empty
                        onConfigChange(config.copy(
                            apkPath = apkUri.toString(),
                            packageId = if (config.packageId.isEmpty()) metadata.packageName else config.packageId,
                            versionName = if (config.versionName == "1.0.0") metadata.versionName else config.versionName,
                            versionCode = if (config.versionCode == 1) metadata.versionCode.toInt() else config.versionCode,
                            appName = if (config.appName.isEmpty()) metadata.appName else config.appName,
                            shortDescription = if (config.shortDescription.isEmpty() && description.isNotEmpty()) {
                                description.take(80) // Short description limit
                            } else config.shortDescription,
                            fullDescription = if (config.fullDescription.isEmpty() && description.isNotEmpty()) {
                                description
                            } else config.fullDescription,
                            category = category
                        ))
                        
                        val toastMsg = if (twaManifest != null) {
                            "📦 APK + manifest extracted: ${metadata.packageName}"
                        } else {
                            "📦 APK info extracted: ${metadata.packageName}"
                        }
                        Toast.makeText(context, toastMsg, Toast.LENGTH_SHORT).show()
                    },
                    onFailure = { /* Silently ignore - APK path is still set */ }
                )
                isReadingApk = false
            }
        }
    }
    
    val iconPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            onConfigChange(config.copy(iconPath = it.toString()))
        }
    }
    
    val bannerPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            onConfigChange(config.copy(bannerPath = it.toString()))
        }
    }
    
    val screenshotPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents()
    ) { uris: List<Uri> ->
        onConfigChange(config.copy(screenshots = uris.map { it.toString() }))
    }
    
    Column {
        SectionHeader(
            title = "2. Upload Assets",
            subtitle = "App icon, banner, screenshots, and APK"
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // APK Upload
        AssetUploadCard(
            title = "Release APK",
            subtitle = "Signed release build (.apk)",
            icon = Icons.Filled.Android,
            selectedPath = config.apkPath,
            required = true,
            onClick = { apkPicker.launch("application/vnd.android.package-archive") }
        )
        
        Spacer(modifier = Modifier.height(12.dp))
        
        // App Icon
        AssetUploadCard(
            title = "App Icon",
            subtitle = "512 x 512 px PNG",
            icon = Icons.Filled.Image,
            selectedPath = config.iconPath,
            required = true,
            onClick = { iconPicker.launch("image/png") }
        )
        
        Spacer(modifier = Modifier.height(12.dp))
        
        // Banner
        AssetUploadCard(
            title = "Banner Graphic",
            subtitle = "1200 x 600 px PNG/JPG",
            icon = Icons.Filled.Panorama,
            selectedPath = config.bannerPath,
            required = true,
            onClick = { bannerPicker.launch("image/*") }
        )
        
        Spacer(modifier = Modifier.height(12.dp))
        
        // Screenshots
        AssetUploadCard(
            title = "Screenshots",
            subtitle = "Min 4, 1920x1080 recommended",
            icon = Icons.Filled.Collections,
            selectedPath = if (config.screenshots.isNotEmpty()) 
                "${config.screenshots.size} selected" else "",
            required = true,
            onClick = { screenshotPicker.launch("image/*") }
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Asset requirements info
        PublishCard {
            Row(verticalAlignment = Alignment.Top) {
                Icon(
                    Icons.Filled.Info,
                    contentDescription = null,
                    tint = SolanaPurple,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "Asset Requirements",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "• Icon: 512x512 PNG\n• Banner: 1200x600 (required)\n• Feature Graphic: 1200x1200 (for Editor's Choice)\n• Screenshots: Min 4, same orientation/ratio\n• Videos: 720p+ MP4 (optional)",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary,
                        lineHeight = 20.sp
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedButton(
                onClick = onBack,
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = TextSecondary
                ),
                border = BorderStroke(1.dp, GlassBorder)
            ) {
                Text("← Back")
            }
            
            Button(
                onClick = onNext,
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = SolanaPurple
                )
            ) {
                Text("Next →")
            }
        }
    }
}

@Composable
fun DetailsStep(
    config: PublishConfig,
    onConfigChange: (PublishConfig) -> Unit,
    onNext: () -> Unit,
    onBack: () -> Unit
) {
    Column {
        SectionHeader(
            title = "3. App Details",
            subtitle = "Metadata for your dApp Store listing"
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        PublishCard {
            Text(
                text = "Basic Info",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            OutlinedTextField(
                value = config.appName,
                onValueChange = { onConfigChange(config.copy(appName = it)) },
                label = { Text("App Name") },
                placeholder = { Text("My Awesome App", color = TextMuted) },
                modifier = Modifier.fillMaxWidth(),
                colors = textFieldColors(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            OutlinedTextField(
                value = config.packageId,
                onValueChange = { onConfigChange(config.copy(packageId = it)) },
                label = { Text("Package ID") },
                placeholder = { Text("com.example.myapp", color = TextMuted) },
                modifier = Modifier.fillMaxWidth(),
                colors = textFieldColors(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = config.versionName,
                    onValueChange = { onConfigChange(config.copy(versionName = it)) },
                    label = { Text("Version") },
                    modifier = Modifier.weight(1f),
                    colors = textFieldColors(),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )
                
                OutlinedTextField(
                    value = config.versionCode.toString(),
                    onValueChange = { 
                        it.toIntOrNull()?.let { code ->
                            onConfigChange(config.copy(versionCode = code))
                        }
                    },
                    label = { Text("Version Code") },
                    modifier = Modifier.weight(1f),
                    colors = textFieldColors(),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Category Dropdown
            var categoryExpanded by remember { mutableStateOf(false) }
            val categories = listOf(
                "DeFi", "NFT", "Gaming", "Social", "Utility", 
                "Finance", "Education", "Productivity", "Entertainment", "Other"
            )
            
            @OptIn(ExperimentalMaterial3Api::class)
            Column {
                Text(
                    text = "Category",
                    style = MaterialTheme.typography.labelMedium,
                    color = TextMuted
                )
                Spacer(modifier = Modifier.height(4.dp))
                ExposedDropdownMenuBox(
                    expanded = categoryExpanded,
                    onExpandedChange = { categoryExpanded = it }
                ) {
                    OutlinedTextField(
                        value = config.category,
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = {
                            Icon(
                                imageVector = if (categoryExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                contentDescription = "Select category"
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(),
                        colors = textFieldColors(),
                        shape = RoundedCornerShape(12.dp)
                    )
                    ExposedDropdownMenu(
                        expanded = categoryExpanded,
                        onDismissRequest = { categoryExpanded = false }
                    ) {
                        categories.forEach { category ->
                            DropdownMenuItem(
                                text = { Text(category) },
                                onClick = {
                                    onConfigChange(config.copy(category = category))
                                    categoryExpanded = false
                                }
                            )
                        }
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        PublishCard {
            Text(
                text = "Description",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            OutlinedTextField(
                value = config.shortDescription,
                onValueChange = { onConfigChange(config.copy(shortDescription = it)) },
                label = { Text("Short Description") },
                placeholder = { Text("Brief tagline for your app", color = TextMuted) },
                modifier = Modifier.fillMaxWidth(),
                colors = textFieldColors(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            OutlinedTextField(
                value = config.fullDescription,
                onValueChange = { onConfigChange(config.copy(fullDescription = it)) },
                label = { Text("Full Description") },
                placeholder = { Text("Detailed app description...", color = TextMuted) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                colors = textFieldColors(),
                shape = RoundedCornerShape(12.dp),
                maxLines = 5
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        PublishCard {
            Text(
                text = "What's New (for updates)",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            OutlinedTextField(
                value = config.whatsNew,
                onValueChange = { onConfigChange(config.copy(whatsNew = it)) },
                label = { Text("Release Notes") },
                placeholder = { Text("• New features\n• Bug fixes\n• Improvements", color = TextMuted) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp),
                colors = textFieldColors(),
                shape = RoundedCornerShape(12.dp),
                maxLines = 4
            )
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedButton(
                onClick = onBack,
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = TextSecondary
                ),
                border = BorderStroke(1.dp, GlassBorder)
            ) {
                Text("← Back")
            }
            
            Button(
                onClick = onNext,
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = SolanaPurple
                )
            ) {
                Text("Generate Commands →")
            }
        }
    }
}

@Composable
fun CommandsStep(
    config: PublishConfig,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val uriHandler = LocalUriHandler.current
    var showConfigYaml by remember { mutableStateOf(false) }
    
    Column {
        SectionHeader(
            title = "4. Publish Commands",
            subtitle = "Run these to submit to Solana dApp Store"
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Config.yaml Generator
        ConfigYamlCard(
            config = config,
            expanded = showConfigYaml,
            onToggle = { showConfigYaml = !showConfigYaml }
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Step 1: Validate
        PublishCommandCard(
            stepNumber = 1,
            title = "Validate Configuration",
            description = "Check your config.yaml is correct",
            command = "npx dapp-store validate -k ${config.keypairPath.ifEmpty { "<keypair_path>" }} -b <android_sdk_build_tools_path>"
        )
        
        Spacer(modifier = Modifier.height(12.dp))
        
        // Step 2: Create App NFT (first time only)
        PublishCommandCard(
            stepNumber = 2,
            title = "Create App NFT (First Time)",
            description = "Mint your app's on-chain identity",
            command = "npx dapp-store create app -k ${config.keypairPath.ifEmpty { "<keypair_path>" }} -b <build_tools_path> -u ${config.rpcUrl}"
        )
        
        Spacer(modifier = Modifier.height(12.dp))
        
        // Step 3: Create Release NFT
        PublishCommandCard(
            stepNumber = 3,
            title = "Create Release NFT",
            description = "Mint your app release (run for each version)",
            command = "npx dapp-store create release -k ${config.keypairPath.ifEmpty { "<keypair_path>" }} -b <build_tools_path> -u ${config.rpcUrl}"
        )
        
        Spacer(modifier = Modifier.height(12.dp))
        
        // Step 4: Submit
        PublishCommandCard(
            stepNumber = 4,
            title = "Submit for Review",
            description = "Submit to the Publisher Portal",
            command = """npx dapp-store publish submit \
  -k ${config.keypairPath.ifEmpty { "<keypair_path>" }} \
  -u ${config.rpcUrl} \
  --requestor-is-authorized \
  --complies-with-solana-dapp-store-policies"""
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Update command
        PublishCard {
            Row(verticalAlignment = Alignment.Top) {
                Icon(
                    Icons.Filled.Update,
                    contentDescription = null,
                    tint = SolanaGreen,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "For App Updates",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "1. Update versionCode & versionName in build.gradle\n2. Build new signed APK\n3. Run 'create release' again\n4. Use this command:",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary,
                        lineHeight = 20.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    CommandBlock(
                        command = """npx dapp-store publish update \
  -k ${config.keypairPath.ifEmpty { "<keypair_path>" }} \
  -u ${config.rpcUrl} \
  --requestor-is-authorized \
  --complies-with-solana-dapp-store-policies"""
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Publisher Portal link
        PublishCard {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { uriHandler.openUri("https://publish.solanamobile.com") },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Filled.OpenInNew,
                    contentDescription = null,
                    tint = SolanaPurple,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Publisher Portal",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimary
                    )
                    Text(
                        text = "Alternative: Submit via web interface",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                }
                Icon(
                    Icons.Filled.ChevronRight,
                    contentDescription = null,
                    tint = TextMuted
                )
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        OutlinedButton(
            onClick = onBack,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = TextSecondary
            ),
            border = BorderStroke(1.dp, GlassBorder)
        ) {
            Text("← Back to Details")
        }
    }
}

// Helper Composables

@Composable
fun SectionHeader(title: String, subtitle: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.Bold,
        color = TextPrimary
    )
    Text(
        text = subtitle,
        style = MaterialTheme.typography.bodyMedium,
        color = TextSecondary,
        modifier = Modifier.padding(top = 4.dp)
    )
}

@Composable
fun PublishCard(
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0x0DFFFFFF)
        ),
        border = BorderStroke(1.dp, Color(0x1AFFFFFF))
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            content = content
        )
    }
}

@Composable
fun ChecklistItem(text: String, checked: Boolean) {
    Row(
        modifier = Modifier.padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            if (checked) Icons.Filled.CheckCircle else Icons.Filled.RadioButtonUnchecked,
            contentDescription = null,
            tint = if (checked) SolanaGreen else TextMuted,
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = TextPrimary
        )
    }
}

@Composable
fun CommandBlock(command: String) {
    val context = LocalContext.current
    
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(BgPrimary)
            .border(1.dp, GlassBorder, RoundedCornerShape(10.dp))
            .clickable {
                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                clipboard.setPrimaryClip(ClipData.newPlainText("command", command))
                Toast.makeText(context, "Copied to clipboard", Toast.LENGTH_SHORT).show()
            }
            .padding(12.dp)
    ) {
        Row(verticalAlignment = Alignment.Top) {
            Text(
                text = command,
                style = MaterialTheme.typography.bodySmall,
                color = SolanaGreen,
                fontFamily = FontFamily.Monospace,
                modifier = Modifier.weight(1f),
                lineHeight = 18.sp
            )
            Spacer(modifier = Modifier.width(8.dp))
            Icon(
                Icons.Filled.ContentCopy,
                contentDescription = "Copy",
                tint = TextMuted,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

@Composable
fun AssetUploadCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    selectedPath: String,
    required: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0x0DFFFFFF)
        ),
        border = BorderStroke(
            1.dp, 
            if (selectedPath.isNotEmpty()) SolanaGreen.copy(alpha = 0.5f) 
            else Color(0x1AFFFFFF)
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        if (selectedPath.isNotEmpty()) 
                            SolanaGreen.copy(alpha = 0.15f) 
                        else SolanaPurple.copy(alpha = 0.15f)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    if (selectedPath.isNotEmpty()) Icons.Filled.CheckCircle else icon,
                    contentDescription = null,
                    tint = if (selectedPath.isNotEmpty()) SolanaGreen else SolanaPurple,
                    modifier = Modifier.size(24.dp)
                )
            }
            
            Spacer(modifier = Modifier.width(14.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimary
                    )
                    if (required) {
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "*",
                            color = Color(0xFFFF5C7A),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                Text(
                    text = if (selectedPath.isNotEmpty()) 
                        "✓ Selected" else subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = if (selectedPath.isNotEmpty()) SolanaGreen else TextMuted,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }
            
            Icon(
                Icons.Filled.Upload,
                contentDescription = "Upload",
                tint = TextMuted
            )
        }
    }
}

@Composable
fun PublishCommandCard(
    stepNumber: Int,
    title: String,
    description: String,
    command: String
) {
    PublishCard {
        Row(verticalAlignment = Alignment.Top) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(SolanaPurple),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stepNumber.toString(),
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
            
            Spacer(modifier = Modifier.width(12.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextMuted,
                    modifier = Modifier.padding(top = 2.dp)
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                CommandBlock(command = command)
            }
        }
    }
}

@Composable
fun GradientButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier.height(52.dp),
        shape = RoundedCornerShape(14.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent
        ),
        contentPadding = PaddingValues()
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(SolanaPurple, SolanaPurpleDark)
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary
            )
        }
    }
}

@Composable
private fun textFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = SolanaPurple,
    unfocusedBorderColor = GlassBorder,
    focusedLabelColor = SolanaPurple,
    unfocusedLabelColor = TextSecondary,
    cursorColor = SolanaPurple,
    focusedTextColor = TextPrimary,
    unfocusedTextColor = TextPrimary
)

// Config.yaml Generator
@Composable
fun ConfigYamlCard(
    config: PublishConfig,
    expanded: Boolean,
    onToggle: () -> Unit
) {
    val context = LocalContext.current
    
    val configYaml = remember(config) {
        generateConfigYaml(config)
    }
    
    PublishCard {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onToggle),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Filled.Code,
                contentDescription = null,
                tint = SolanaGreen,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "config.yaml",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary
                )
                Text(
                    text = "Generated from your app details",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextMuted
                )
            }
            Icon(
                if (expanded) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                contentDescription = if (expanded) "Collapse" else "Expand",
                tint = TextMuted
            )
        }
        
        AnimatedVisibility(visible = expanded) {
            Column {
                Spacer(modifier = Modifier.height(16.dp))
                
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(BgPrimary)
                        .border(1.dp, GlassBorder, RoundedCornerShape(10.dp))
                        .horizontalScroll(rememberScrollState())
                        .padding(12.dp)
                ) {
                    Text(
                        text = configYaml,
                        style = MaterialTheme.typography.bodySmall,
                        color = SolanaGreen,
                        fontFamily = FontFamily.Monospace,
                        lineHeight = 18.sp
                    )
                }
                
                Spacer(modifier = Modifier.height(12.dp))
                
                Button(
                    onClick = {
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        clipboard.setPrimaryClip(ClipData.newPlainText("config.yaml", configYaml))
                        Toast.makeText(context, "config.yaml copied!", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = SolanaGreen.copy(alpha = 0.15f),
                        contentColor = SolanaGreen
                    )
                ) {
                    Icon(
                        Icons.Filled.ContentCopy,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Copy config.yaml")
                }
            }
        }
    }
}

fun generateConfigYaml(config: PublishConfig): String {
    return """
# Solana dApp Store Publishing Configuration
# Generated by Bubble Wrapper

publisher:
  name: "${config.appName.ifEmpty { "Your Publisher Name" }}"
  website: "${config.publisherWebsite.ifEmpty { "https://example.com" }}"
  email: "${config.publisherEmail.ifEmpty { "contact@example.com" }}"
  address: "${config.walletAddress.ifEmpty { "<your-wallet-address>" }}"

app:
  name: "${config.appName.ifEmpty { "My App" }}"
  android_package: "${config.packageId.ifEmpty { "com.example.myapp" }}"

release:
  version_name: "${config.versionName}"
  version_code: ${config.versionCode}
  apk_path: "${config.apkPath.ifEmpty { "./app-release.apk" }}"

localized:
  en-US:
    short_description: "${config.shortDescription.ifEmpty { "A brief description of your app" }}"
    full_description: |
      ${config.fullDescription.ifEmpty { "Full description of your app.\nDescribe features and benefits." }}
    whats_new: |
      ${config.whatsNew.ifEmpty { "• Initial release" }}
    icon: "${config.iconPath.ifEmpty { "./assets/icon.png" }}"
    banner: "${config.bannerPath.ifEmpty { "./assets/banner.png" }}"
    screenshots:
${config.screenshots.ifEmpty { listOf("./assets/screenshot1.png", "./assets/screenshot2.png") }
    .mapIndexed { i, _ -> "      - \"./assets/screenshot${i + 1}.png\"" }
    .joinToString("\n")}

# Categories: Finance, NFT, Wallet, DeFi, Gaming, Social, Tools, Other
category: "${config.category}"

solana:
  rpc_url: "${config.rpcUrl}"
  cluster: "mainnet-beta"

# Optional: Solana features used
solana_mobile_stack_version: "2.0.0"
permissions:
  - android.permission.INTERNET
""".trimIndent()
}

// ============================================
// STEP 1: Wallet Setup Step (Enhanced)
// ============================================
@Composable
fun WalletSetupStep(
    config: PublishConfig,
    onConfigChange: (PublishConfig) -> Unit,
    walletManager: WalletManager,
    solanaRepository: SolanaRepository,
    onNext: () -> Unit
) {
    val walletState by walletManager.walletState.collectAsState()
    val isWalletConnected = walletState is WalletState.Connected
    
    Column {
        SectionHeader(
            title = "1. Connect Wallet & Configure",
            subtitle = "Connect your Solana wallet to publish on-chain"
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Wallet Connection Card
        WalletConnectionCard(
            walletManager = walletManager,
            solanaRepository = solanaRepository,
            onConnected = { publicKey ->
                onConfigChange(config.copy(walletAddress = publicKey))
            }
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        SupportedWalletsInfo()
        
        Spacer(modifier = Modifier.height(20.dp))
        
        // RPC Configuration
        RpcSelector(
            selectedRpc = config.rpcUrl,
            onRpcSelected = { onConfigChange(config.copy(rpcUrl = it)) }
        )
        
        Spacer(modifier = Modifier.height(20.dp))
        
        // Publisher Info
        PublishCard {
            Text(
                text = "Publisher Information",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            OutlinedTextField(
                value = config.publisherEmail,
                onValueChange = { onConfigChange(config.copy(publisherEmail = it)) },
                label = { Text("Email Address") },
                placeholder = { Text("you@example.com", color = TextMuted) },
                modifier = Modifier.fillMaxWidth(),
                colors = textFieldColors(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true,
                leadingIcon = {
                    Icon(Icons.Filled.Email, null, tint = TextMuted)
                }
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            OutlinedTextField(
                value = config.publisherWebsite,
                onValueChange = { onConfigChange(config.copy(publisherWebsite = it)) },
                label = { Text("Website (optional)") },
                placeholder = { Text("https://yourapp.com", color = TextMuted) },
                modifier = Modifier.fillMaxWidth(),
                colors = textFieldColors(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true,
                leadingIcon = {
                    Icon(Icons.Filled.Language, null, tint = TextMuted)
                }
            )
        }
        
        Spacer(modifier = Modifier.height(20.dp))
        
        // Estimated cost
        EstimatedCostCard()
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Next button - enabled only when wallet is connected
        Button(
            onClick = onNext,
            modifier = Modifier.fillMaxWidth().height(52.dp),
            enabled = isWalletConnected,
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isWalletConnected) SolanaPurple else SolanaPurple.copy(alpha = 0.3f),
                disabledContainerColor = SolanaPurple.copy(alpha = 0.3f)
            ),
            shape = RoundedCornerShape(14.dp)
        ) {
            if (isWalletConnected) {
                Text("Next: Upload Assets →", fontWeight = FontWeight.SemiBold)
            } else {
                Icon(Icons.Filled.Lock, null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Connect Wallet to Continue", fontWeight = FontWeight.SemiBold)
            }
        }
        
        if (!isWalletConnected) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "A connected wallet is required to publish to the dApp Store",
                style = MaterialTheme.typography.bodySmall,
                color = TextMuted,
                modifier = Modifier.fillMaxWidth(),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }
}

// ============================================
// STEP 4: Publish Step (Enhanced with real publishing)
// ============================================
@Composable
fun PublishStep(
    config: PublishConfig,
    walletManager: WalletManager,
    solanaRepository: SolanaRepository,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val uriHandler = LocalUriHandler.current
    val scope = rememberCoroutineScope()
    val walletState by walletManager.walletState.collectAsState()
    
    var showConfigYaml by remember { mutableStateOf(false) }
    var showCliMode by remember { mutableStateOf(false) }
    var isPublishing by remember { mutableStateOf(false) }
    var publishingStep by remember { mutableStateOf(0) }
    var publishError by remember { mutableStateOf<String?>(null) }
    var transactionSignature by remember { mutableStateOf<String?>(null) }
    
    val publishingSteps = listOf(
        "Validating configuration...",
        "Uploading assets to storage...",
        "Creating app metadata NFT...",
        "Creating release NFT...",
        "Submitting to dApp Store..."
    )
    
    Column {
        SectionHeader(
            title = "4. Publish to dApp Store",
            subtitle = "Submit your app on-chain"
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Wallet status
        if (walletState !is WalletState.Connected) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFFF5C7A).copy(alpha = 0.1f)
                ),
                border = BorderStroke(1.dp, Color(0xFFFF5C7A).copy(alpha = 0.3f))
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Filled.Warning,
                        contentDescription = null,
                        tint = Color(0xFFFF5C7A)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Wallet Disconnected",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFFFF5C7A)
                        )
                        Text(
                            text = "Go back to Step 1 to connect your wallet",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextMuted
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
        }
        
        // Publishing status card
        if (isPublishing || transactionSignature != null || publishError != null) {
            PublishingStatusCard(
                currentStep = publishingStep,
                steps = publishingSteps,
                isPublishing = isPublishing,
                error = publishError,
                transactionSignature = transactionSignature
            )
            
            Spacer(modifier = Modifier.height(16.dp))
        }
        
        // Config summary
        PublishCard {
            Text(
                text = "Publishing Summary",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            SummaryRow(label = "App Name", value = config.appName.ifEmpty { "Not set" })
            SummaryRow(label = "Package ID", value = config.packageId.ifEmpty { "Not set" })
            SummaryRow(label = "Version", value = "${config.versionName} (${config.versionCode})")
            SummaryRow(label = "Category", value = config.category)
            SummaryRow(
                label = "Wallet", 
                value = if (walletState is WalletState.Connected) 
                    walletManager.formatPublicKey((walletState as WalletState.Connected).publicKey)
                else "Not connected",
                valueColor = if (walletState is WalletState.Connected) SolanaGreen else Color(0xFFFF5C7A)
            )
            SummaryRow(label = "Assets", value = buildString {
                append(if (config.apkPath.isNotEmpty()) "✓ APK" else "✗ APK")
                append(" • ")
                append(if (config.iconPath.isNotEmpty()) "✓ Icon" else "✗ Icon")
                append(" • ")
                append(if (config.screenshots.size >= 4) "✓ ${config.screenshots.size} Screenshots" else "✗ Screenshots")
            })
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Method selection - In-app or CLI
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            MethodCard(
                title = "In-App",
                subtitle = "Publish directly",
                icon = Icons.Filled.PhoneAndroid,
                selected = !showCliMode,
                onClick = { showCliMode = false },
                modifier = Modifier.weight(1f)
            )
            MethodCard(
                title = "CLI Mode",
                subtitle = "Terminal commands",
                icon = Icons.Filled.Terminal,
                selected = showCliMode,
                onClick = { showCliMode = true },
                modifier = Modifier.weight(1f)
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        if (showCliMode) {
            // Show CLI commands (existing behavior)
            ConfigYamlCard(
                config = config,
                expanded = showConfigYaml,
                onToggle = { showConfigYaml = !showConfigYaml }
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            PublishCommandCard(
                stepNumber = 1,
                title = "Validate Configuration",
                description = "Check your config.yaml is correct",
                command = "npx dapp-store validate -k ${config.keypairPath.ifEmpty { "<keypair_path>" }} -b <android_sdk_build_tools_path>"
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            PublishCommandCard(
                stepNumber = 2,
                title = "Create App NFT",
                description = "Mint your app's on-chain identity",
                command = "npx dapp-store create app -k ${config.keypairPath.ifEmpty { "<keypair_path>" }} -u ${config.rpcUrl}"
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            PublishCommandCard(
                stepNumber = 3,
                title = "Create Release NFT",
                description = "Mint your app release",
                command = "npx dapp-store create release -k ${config.keypairPath.ifEmpty { "<keypair_path>" }} -u ${config.rpcUrl}"
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            PublishCommandCard(
                stepNumber = 4,
                title = "Submit for Review",
                description = "Submit to the Publisher Portal",
                command = """npx dapp-store publish submit \
  -k ${config.keypairPath.ifEmpty { "<keypair_path>" }} \
  -u ${config.rpcUrl} \
  --requestor-is-authorized \
  --complies-with-solana-dapp-store-policies"""
            )
        } else {
            // In-app publishing mode
            PublishCard {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Filled.RocketLaunch,
                            contentDescription = null,
                            tint = SolanaPurple,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "One-Click Publish",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.SemiBold,
                                color = TextPrimary
                            )
                            Text(
                                text = "Upload assets and submit in one step",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextMuted
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Publish button
                    Button(
                        onClick = {
                            scope.launch {
                                isPublishing = true
                                publishError = null
                                transactionSignature = null
                                
                                try {
                                    // Create publisher instance
                                    val publisher = DappStorePublisher(
                                        context = context,
                                        walletManager = walletManager,
                                        solanaRepository = solanaRepository
                                    )
                                    
                                    // Publish with progress updates
                                    val result = publisher.publishApp(
                                        config = config
                                    ) { progress ->
                                        publishingStep = progress.progress - 1
                                    }
                                    
                                    if (result.success) {
                                        transactionSignature = result.transactionSignature
                                        Toast.makeText(
                                            context,
                                            "App published successfully!",
                                            Toast.LENGTH_LONG
                                        ).show()
                                    } else {
                                        publishError = result.error ?: "Publishing failed"
                                    }
                                    
                                } catch (e: Exception) {
                                    publishError = e.message ?: "Publishing failed"
                                    Toast.makeText(
                                        context,
                                        "Error: ${e.message}",
                                        Toast.LENGTH_LONG
                                    ).show()
                                } finally {
                                    isPublishing = false
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(52.dp),
                        enabled = walletState is WalletState.Connected && !isPublishing && transactionSignature == null,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Transparent
                        ),
                        contentPadding = PaddingValues()
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.horizontalGradient(
                                        colors = if (walletState is WalletState.Connected && !isPublishing)
                                            listOf(SolanaPurple, SolanaGreen)
                                        else listOf(TextMuted, TextMuted)
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            if (isPublishing) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(20.dp),
                                        strokeWidth = 2.dp,
                                        color = Color.White
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Text("Publishing...", fontWeight = FontWeight.SemiBold)
                                }
                            } else if (transactionSignature != null) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Filled.CheckCircle, null)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Published!", fontWeight = FontWeight.SemiBold)
                                }
                            } else {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Filled.CloudUpload, null)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Publish to dApp Store", fontWeight = FontWeight.SemiBold)
                                }
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    Text(
                        text = "This will sign multiple transactions with your wallet",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextMuted,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Alternative: Open Publisher Portal
            OutlinedButton(
                onClick = { uriHandler.openUri("https://publish.solanamobile.com") },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = SolanaPurple
                ),
                border = BorderStroke(1.dp, SolanaPurple.copy(alpha = 0.5f))
            ) {
                Icon(Icons.Filled.OpenInNew, null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Open Publisher Portal Instead")
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        OutlinedButton(
            onClick = onBack,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = TextSecondary
            ),
            border = BorderStroke(1.dp, GlassBorder)
        ) {
            Text("← Back to Details")
        }
    }
}

@Composable
fun SummaryRow(
    label: String,
    value: String,
    valueColor: Color = TextPrimary
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = TextMuted
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            color = valueColor
        )
    }
}

@Composable
fun MethodCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (selected) SolanaPurple.copy(alpha = 0.15f) else Color(0x0DFFFFFF)
        ),
        border = BorderStroke(
            1.dp,
            if (selected) SolanaPurple.copy(alpha = 0.5f) else GlassBorder
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = if (selected) SolanaPurple else TextMuted,
                modifier = Modifier.size(28.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = if (selected) TextPrimary else TextSecondary
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = TextMuted
            )
        }
    }
}


