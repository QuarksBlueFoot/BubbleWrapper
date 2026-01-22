package xyz.bluefoot.bubblewrapper

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import xyz.bluefoot.bubblewrapper.ui.theme.*
import xyz.bluefoot.bubblewrapper.ui.screens.*

// Guide screen types for navigation
sealed class GuideScreen {
    object None : GuideScreen()
    object WhatIsPwa : GuideScreen()
    object GettingStarted : GuideScreen()
    object DappStore : GuideScreen()
    object AssetLinks : GuideScreen()
    object Styling : GuideScreen()
    object Keystore : GuideScreen()
    object Troubleshooting : GuideScreen()
}

// Data class for PWA configuration
data class PwaConfig(
    val manifestUrl: String = "",
    val domain: String = "",
    val appName: String = "",
    val shortName: String = "",
    val packageId: String = "",
    val themeColor: String = "#0B0F1A",
    val splashColor: String = "#0B0F1A",
    val orientation: String = "portrait",
    val displayMode: String = "standalone"
)

// Additional screen navigation for wizard flows
sealed class WizardScreen {
    object None : WizardScreen()
    object KeystoreGenerator : WizardScreen()
    object TwaBuildWizard : WizardScreen()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BubbleWrapperApp() {
    var currentScreen by remember { mutableStateOf(0) }
    var pwaConfig by remember { mutableStateOf(PwaConfig()) }
    var currentGuide by remember { mutableStateOf<GuideScreen>(GuideScreen.None) }
    var currentWizard by remember { mutableStateOf<WizardScreen>(WizardScreen.None) }
    
    // State for keystore/TWA wizard flow
    var selectedKeystorePath by remember { mutableStateOf("") }
    var selectedFingerprint by remember { mutableStateOf("") }
    
    // Handle wizard screens first
    when (currentWizard) {
        is WizardScreen.KeystoreGenerator -> {
            KeystoreScreen(
                onBack = { currentWizard = WizardScreen.None },
                onKeystoreCreated = { path, fingerprint ->
                    selectedKeystorePath = path
                    selectedFingerprint = fingerprint
                    // Go back to TWA wizard or main screen
                    currentWizard = WizardScreen.TwaBuildWizard
                }
            )
            return
        }
        is WizardScreen.TwaBuildWizard -> {
            TwaBuildWizardScreen(
                onBack = { currentWizard = WizardScreen.None },
                onNavigateToKeystore = { currentWizard = WizardScreen.KeystoreGenerator },
                onNavigateToPublish = {
                    currentWizard = WizardScreen.None
                    currentScreen = 2 // Go to Publish screen
                },
                currentKeystorePath = selectedKeystorePath,
                currentFingerprint = selectedFingerprint
            )
            return
        }
        is WizardScreen.None -> { /* Continue to normal flow */ }
    }
    
    // If a guide is selected, show the guide screen
    when (currentGuide) {
        is GuideScreen.WhatIsPwa -> {
            WhatIsPwaGuide(onBack = { currentGuide = GuideScreen.None })
            return
        }
        is GuideScreen.GettingStarted -> {
            GettingStartedGuide(onBack = { currentGuide = GuideScreen.None })
            return
        }
        is GuideScreen.DappStore -> {
            DappStoreGuide(onBack = { currentGuide = GuideScreen.None })
            return
        }
        is GuideScreen.AssetLinks -> {
            AssetLinksGuide(onBack = { currentGuide = GuideScreen.None })
            return
        }
        is GuideScreen.Styling -> {
            StylingGuide(onBack = { currentGuide = GuideScreen.None })
            return
        }
        is GuideScreen.Keystore -> {
            KeystoreGuide(onBack = { currentGuide = GuideScreen.None })
            return
        }
        is GuideScreen.Troubleshooting -> {
            TroubleshootingGuide(onBack = { currentGuide = GuideScreen.None })
            return
        }
        is GuideScreen.None -> { /* Show main app */ }
    }
    
    Scaffold(
        containerColor = BgPrimary,
        bottomBar = {
            BottomNavBar(
                selectedIndex = currentScreen,
                onItemSelected = { currentScreen = it }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (currentScreen) {
                0 -> HomeScreen(
                    onStartWizard = { currentWizard = WizardScreen.TwaBuildWizard },
                    onGenerateKeystore = { currentWizard = WizardScreen.KeystoreGenerator }
                )
                1 -> ConfigureScreen(
                    config = pwaConfig,
                    onConfigChange = { pwaConfig = it },
                    onNext = { currentScreen = 3 }
                )
                2 -> PublishScreen(
                    onBack = { currentScreen = 0 }
                )
                3 -> BuildScreen(
                    config = pwaConfig,
                    onBack = { currentScreen = 1 }
                )
                4 -> DocsScreen(
                    onGuideSelected = { currentGuide = it }
                )
            }
        }
    }
}

@Composable
fun BottomNavBar(
    selectedIndex: Int,
    onItemSelected: (Int) -> Unit
) {
    val items = listOf(
        NavItem("Home", Icons.Filled.Home, Icons.Outlined.Home),
        NavItem("Configure", Icons.Filled.Settings, Icons.Outlined.Settings),
        NavItem("Publish", Icons.Filled.CloudUpload, Icons.Outlined.CloudUpload),
        NavItem("Build", Icons.Filled.Build, Icons.Outlined.Build),
        NavItem("Docs", Icons.Filled.MenuBook, Icons.Outlined.MenuBook)
    )
    
    NavigationBar(
        containerColor = BgSecondary,
        contentColor = TextPrimary,
        tonalElevation = 0.dp
    ) {
        items.forEachIndexed { index, item ->
            NavigationBarItem(
                icon = {
                    Icon(
                        imageVector = if (selectedIndex == index) item.selectedIcon else item.icon,
                        contentDescription = item.label,
                        tint = if (selectedIndex == index) SolanaPurple else TextMuted
                    )
                },
                label = {
                    Text(
                        text = item.label,
                        fontSize = 11.sp,
                        color = if (selectedIndex == index) SolanaPurple else TextMuted
                    )
                },
                selected = selectedIndex == index,
                onClick = { onItemSelected(index) },
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = SolanaPurple.copy(alpha = 0.15f)
                )
            )
        }
    }
}

data class NavItem(
    val label: String,
    val selectedIcon: ImageVector,
    val icon: ImageVector
)

@Composable
fun HomeScreen(
    onStartWizard: () -> Unit,
    onGenerateKeystore: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
    ) {
        Spacer(modifier = Modifier.height(20.dp))
        
        // Hero Section
        GlassCard(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(24.dp)
            ) {
                // Logo placeholder
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(
                            Brush.linearGradient(
                                colors = listOf(SolanaPurple, SolanaGreen)
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text("🫧", fontSize = 40.sp)
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = "Bubble Wrapper",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                
                Text(
                    text = "PWA → Android in minutes",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary,
                    modifier = Modifier.padding(top = 4.dp)
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                GradientButton(
                    text = "Start Publishing Wizard",
                    onClick = onStartWizard
                )
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Features
        Text(
            text = "FEATURES",
            style = MaterialTheme.typography.labelMedium,
            color = SolanaGreen,
            letterSpacing = 1.sp
        )
        
        Spacer(modifier = Modifier.height(12.dp))
        
        FeatureCard(
            icon = Icons.Filled.Language,
            title = "PWA Manifest Parser",
            description = "Automatically fetches and parses your manifest.webmanifest"
        )
        
        Spacer(modifier = Modifier.height(12.dp))
        
        FeatureCard(
            icon = Icons.Filled.Android,
            title = "Bubblewrap CLI",
            description = "Generates ready-to-use Bubblewrap commands"
        )
        
        Spacer(modifier = Modifier.height(12.dp))
        
        FeatureCard(
            icon = Icons.Filled.Store,
            title = "Solana Mobile Store",
            description = "Optimized for Solana dApp Store submission"
        )
        
        Spacer(modifier = Modifier.height(12.dp))
        
        FeatureCard(
            icon = Icons.Filled.Verified,
            title = "Digital Asset Links",
            description = "Auto-generates assetlinks.json for verification"
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Quick Actions
        Text(
            text = "QUICK ACTIONS",
            style = MaterialTheme.typography.labelMedium,
            color = SolanaGreen,
            letterSpacing = 1.sp
        )
        
        Spacer(modifier = Modifier.height(12.dp))
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            ActionCard(
                icon = Icons.Filled.Key,
                title = "Generate\nKeystore",
                onClick = onGenerateKeystore,
                modifier = Modifier.weight(1f)
            )
            ActionCard(
                icon = Icons.Filled.Build,
                title = "Build\nTWA",
                onClick = onStartWizard,
                modifier = Modifier.weight(1f)
            )
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Quick Links
        Text(
            text = "QUICK LINKS",
            style = MaterialTheme.typography.labelMedium,
            color = SolanaGreen,
            letterSpacing = 1.sp
        )
        
        Spacer(modifier = Modifier.height(12.dp))
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            QuickLinkCard(
                icon = Icons.Filled.Code,
                title = "GitHub",
                modifier = Modifier.weight(1f)
            )
            QuickLinkCard(
                icon = Icons.Filled.Description,
                title = "Solana Docs",
                modifier = Modifier.weight(1f)
            )
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        // Credits Section
        CreditsSection()
        
        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
fun ConfigureScreen(
    config: PwaConfig,
    onConfigChange: (PwaConfig) -> Unit,
    onNext: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
    ) {
        Text(
            text = "Configure PWA",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )
        
        Text(
            text = "Enter your PWA details for Android conversion",
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary,
            modifier = Modifier.padding(top = 4.dp)
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Step 1: Manifest URL
        StepCard(stepNumber = 1, title = "Manifest URL") {
            ConfigTextField(
                value = config.manifestUrl,
                onValueChange = { onConfigChange(config.copy(manifestUrl = it)) },
                label = "Manifest URL",
                placeholder = "https://example.com/manifest.webmanifest",
                leadingIcon = Icons.Filled.Link
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            OutlinedButton(
                onClick = { /* TODO: Fetch manifest */ },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = SolanaPurple
                ),
                border = BorderStroke(1.dp, SolanaPurple.copy(alpha = 0.5f))
            ) {
                Icon(Icons.Filled.Download, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Fetch & Parse Manifest")
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Step 2: App Info
        StepCard(stepNumber = 2, title = "App Information") {
            ConfigTextField(
                value = config.domain,
                onValueChange = { onConfigChange(config.copy(domain = it)) },
                label = "Domain",
                placeholder = "bubblewrapper.bluefoot.xyz",
                leadingIcon = Icons.Filled.Language
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            ConfigTextField(
                value = config.appName,
                onValueChange = { onConfigChange(config.copy(appName = it)) },
                label = "App Name",
                placeholder = "My Awesome PWA",
                leadingIcon = Icons.Filled.Label
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            ConfigTextField(
                value = config.shortName,
                onValueChange = { onConfigChange(config.copy(shortName = it)) },
                label = "Short Name (max 12 chars)",
                placeholder = "MyPWA",
                leadingIcon = Icons.Filled.ShortText
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            ConfigTextField(
                value = config.packageId,
                onValueChange = { onConfigChange(config.copy(packageId = it)) },
                label = "Package ID (reverse DNS)",
                placeholder = "xyz.bluefoot.bubblewrapper.sample",
                leadingIcon = Icons.Filled.Fingerprint
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Step 3: Theme Colors
        StepCard(stepNumber = 3, title = "Theme & Colors") {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Theme Color",
                        style = MaterialTheme.typography.labelMedium,
                        color = TextSecondary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    ColorPickerButton(
                        color = config.themeColor,
                        onClick = { /* TODO: Color picker */ }
                    )
                }
                
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Splash Color",
                        style = MaterialTheme.typography.labelMedium,
                        color = TextSecondary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    ColorPickerButton(
                        color = config.splashColor,
                        onClick = { /* TODO: Color picker */ }
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                DropdownSelector(
                    label = "Orientation",
                    value = config.orientation,
                    options = listOf("portrait", "landscape", "any"),
                    modifier = Modifier.weight(1f)
                )
                
                DropdownSelector(
                    label = "Display",
                    value = config.displayMode,
                    options = listOf("standalone", "fullscreen", "minimal-ui"),
                    modifier = Modifier.weight(1f)
                )
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        GradientButton(
            text = "Generate Build Commands →",
            onClick = onNext,
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
fun BuildScreen(
    config: PwaConfig,
    onBack: () -> Unit
) {
    var copied by remember { mutableStateOf(false) }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
    ) {
        Text(
            text = "Build Commands",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )
        
        Text(
            text = "Run these commands to create your Android TWA",
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary,
            modifier = Modifier.padding(top = 4.dp)
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Step 1: Install Bubblewrap
        CommandCard(
            stepNumber = 1,
            title = "Install Bubblewrap CLI",
            command = "npm install -g @bubblewrap/cli"
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Step 2: Create project
        CommandCard(
            stepNumber = 2,
            title = "Initialize Project",
            command = buildString {
                appendLine("mkdir android-twa && cd android-twa")
                append("bubblewrap init --manifest=https://${config.domain.ifEmpty { "YOUR_DOMAIN" }}/manifest.webmanifest")
            }
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Step 3: CLI Prompts
        GlassCard(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    StepBadge(3)
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "CLI Prompt Values",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimary
                    )
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                PromptRow("Domain", config.domain.ifEmpty { "bubblewrapper.bluefoot.xyz" })
                PromptRow("App Name", config.appName.ifEmpty { "My PWA" })
                PromptRow("Short Name", config.shortName.ifEmpty { "PWA" })
                PromptRow("Package ID", config.packageId.ifEmpty { "xyz.bluefoot.bubblewrapper.sample" })
                PromptRow("Theme Color", config.themeColor)
                PromptRow("Splash Color", config.splashColor)
                PromptRow("Orientation", config.orientation)
                PromptRow("Display Mode", config.displayMode)
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Step 4: Build
        CommandCard(
            stepNumber = 4,
            title = "Build Signed APK",
            command = "bubblewrap build"
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Step 5: Install
        CommandCard(
            stepNumber = 5,
            title = "Install on Device",
            command = "bubblewrap install"
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Digital Asset Links
        GlassCard(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Filled.Verified,
                        contentDescription = null,
                        tint = SolanaGreen
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Digital Asset Links",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimary
                    )
                }
                
                Spacer(modifier = Modifier.height(12.dp))
                
                Text(
                    text = "After building, run this command to get your fingerprint:",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                CodeBlock(code = "bubblewrap fingerprint")
                
                Spacer(modifier = Modifier.height(12.dp))
                
                Text(
                    text = "Host the generated assetlinks.json at:",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = "https://${config.domain.ifEmpty { "YOUR_DOMAIN" }}/.well-known/assetlinks.json",
                    style = MaterialTheme.typography.bodySmall,
                    color = SolanaPurple
                )
            }
        }
        
        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
fun DocsScreen(onGuideSelected: (GuideScreen) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
    ) {
        Text(
            text = "Documentation",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )
        
        Text(
            text = "Guides and resources for publishing",
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary,
            modifier = Modifier.padding(top = 4.dp)
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        DocCard(
            icon = Icons.Outlined.Info,
            title = "What is a PWA?",
            description = "Understanding Progressive Web Apps",
            onClick = { onGuideSelected(GuideScreen.WhatIsPwa) }
        )
        
        Spacer(modifier = Modifier.height(12.dp))
        
        DocCard(
            icon = Icons.Filled.RocketLaunch,
            title = "Getting Started",
            description = "Quick start guide for creating your first Android TWA from a PWA",
            onClick = { onGuideSelected(GuideScreen.GettingStarted) }
        )
        
        Spacer(modifier = Modifier.height(12.dp))
        
        DocCard(
            icon = Icons.Filled.Store,
            title = "Solana dApp Store",
            description = "Requirements and process for submitting to the Solana Mobile store",
            onClick = { onGuideSelected(GuideScreen.DappStore) }
        )
        
        Spacer(modifier = Modifier.height(12.dp))
        
        DocCard(
            icon = Icons.Filled.Verified,
            title = "Asset Links Setup",
            description = "How to configure Digital Asset Links for full verification",
            onClick = { onGuideSelected(GuideScreen.AssetLinks) }
        )
        
        Spacer(modifier = Modifier.height(12.dp))
        
        DocCard(
            icon = Icons.Filled.Palette,
            title = "Styling & Theming",
            description = "Best practices for splash screens, icons, and theme colors",
            onClick = { onGuideSelected(GuideScreen.Styling) }
        )
        
        Spacer(modifier = Modifier.height(12.dp))
        
        DocCard(
            icon = Icons.Filled.Security,
            title = "Signing & Keys",
            description = "Managing your release keystore and app signing",
            onClick = { onGuideSelected(GuideScreen.Keystore) }
        )
        
        Spacer(modifier = Modifier.height(12.dp))
        
        DocCard(
            icon = Icons.Filled.BugReport,
            title = "Troubleshooting",
            description = "Common issues and solutions",
            onClick = { onGuideSelected(GuideScreen.Troubleshooting) }
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // External Links
        Text(
            text = "EXTERNAL RESOURCES",
            style = MaterialTheme.typography.labelMedium,
            color = SolanaGreen,
            letterSpacing = 1.sp
        )
        
        Spacer(modifier = Modifier.height(12.dp))
        
        ExternalLinkCard(
            title = "Bubblewrap GitHub",
            url = "github.com/nicholasmorgan/nicholasmorgan"
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        ExternalLinkCard(
            title = "Solana Mobile Docs",
            url = "docs.solanamobile.com"
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        ExternalLinkCard(
            title = "PWA Best Practices",
            url = "web.dev/progressive-web-apps"
        )
        
        Spacer(modifier = Modifier.height(32.dp))
    }
}

// Reusable Components

@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = GlassBg
        ),
        border = BorderStroke(1.dp, GlassBorder)
    ) {
        Column(content = content)
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
fun FeatureCard(
    icon: ImageVector,
    title: String,
    description: String
) {
    GlassCard(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(SolanaPurple.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = SolanaPurple,
                    modifier = Modifier.size(24.dp)
                )
            }
            
            Spacer(modifier = Modifier.width(14.dp))
            
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
                    color = TextSecondary,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }
        }
    }
}

@Composable
fun QuickLinkCard(
    icon: ImageVector,
    title: String,
    modifier: Modifier = Modifier
) {
    GlassCard(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = SolanaPurple,
                modifier = Modifier.size(28.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium,
                color = TextPrimary
            )
        }
    }
}

@Composable
fun ActionCard(
    icon: ImageVector,
    title: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    GlassCard(modifier = modifier.clickable(onClick = onClick)) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(
                            colors = listOf(SolanaPurple, SolanaPurpleDark)
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = TextPrimary,
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium,
                color = TextPrimary,
                textAlign = TextAlign.Center
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConfigTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    leadingIcon: ImageVector
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        placeholder = { Text(placeholder, color = TextMuted) },
        leadingIcon = {
            Icon(leadingIcon, contentDescription = null, tint = TextMuted)
        },
        modifier = Modifier.fillMaxWidth(),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = SolanaPurple,
            unfocusedBorderColor = GlassBorder,
            focusedLabelColor = SolanaPurple,
            unfocusedLabelColor = TextSecondary,
            cursorColor = SolanaPurple,
            focusedTextColor = TextPrimary,
            unfocusedTextColor = TextPrimary
        ),
        shape = RoundedCornerShape(12.dp),
        singleLine = true
    )
}

@Composable
fun StepCard(
    stepNumber: Int,
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    GlassCard(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                StepBadge(stepNumber)
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            content()
        }
    }
}

@Composable
fun StepBadge(number: Int) {
    Box(
        modifier = Modifier
            .size(28.dp)
            .clip(CircleShape)
            .background(SolanaPurple),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = number.toString(),
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
    }
}

@Composable
fun ColorPickerButton(
    color: String,
    onClick: () -> Unit
) {
    val parsedColor = try {
        Color(android.graphics.Color.parseColor(color))
    } catch (e: Exception) {
        BgPrimary
    }
    
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .clip(RoundedCornerShape(12.dp))
            .border(1.dp, GlassBorder, RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(24.dp)
                .clip(CircleShape)
                .background(parsedColor)
                .border(1.dp, GlassBorder, CircleShape)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = color,
            style = MaterialTheme.typography.bodyMedium,
            color = TextPrimary
        )
    }
}

@Composable
fun DropdownSelector(
    label: String,
    value: String,
    options: List<String>,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    
    Column(modifier = modifier) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = TextSecondary
        )
        Spacer(modifier = Modifier.height(8.dp))
        
        Box {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .border(1.dp, GlassBorder, RoundedCornerShape(12.dp))
                    .clickable { expanded = true }
                    .padding(horizontal = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = value,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextPrimary
                )
                Icon(
                    Icons.Filled.ArrowDropDown,
                    contentDescription = null,
                    tint = TextMuted
                )
            }
            
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                options.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option) },
                        onClick = { expanded = false }
                    )
                }
            }
        }
    }
}

@Composable
fun CommandCard(
    stepNumber: Int,
    title: String,
    command: String
) {
    GlassCard(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                StepBadge(stepNumber)
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            CodeBlock(code = command)
        }
    }
}

@Composable
fun CodeBlock(code: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(BgPrimary)
            .border(1.dp, GlassBorder, RoundedCornerShape(10.dp))
            .padding(12.dp)
    ) {
        Text(
            text = code,
            style = MaterialTheme.typography.bodySmall,
            color = SolanaGreen,
            fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
        )
    }
}

@Composable
fun PromptRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = TextSecondary
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall,
            color = TextPrimary,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun DocCard(
    icon: ImageVector,
    title: String,
    description: String,
    onClick: () -> Unit = {}
) {
    GlassCard(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onClick)
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(SolanaPurple.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = SolanaPurple,
                    modifier = Modifier.size(24.dp)
                )
            }
            
            Spacer(modifier = Modifier.width(14.dp))
            
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
                    color = TextSecondary,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }
            
            Icon(
                Icons.Filled.ChevronRight,
                contentDescription = null,
                tint = TextMuted
            )
        }
    }
}

@Composable
fun ExternalLinkCard(
    title: String,
    url: String
) {
    val uriHandler = LocalUriHandler.current
    
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(GlassBg)
            .border(1.dp, GlassBorder, RoundedCornerShape(12.dp))
            .clickable { uriHandler.openUri(url) }
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            Icons.Filled.OpenInNew,
            contentDescription = null,
            tint = SolanaPurple,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = TextPrimary
            )
            Text(
                text = url,
                style = MaterialTheme.typography.bodySmall,
                color = TextMuted
            )
        }
    }
}

@Composable
fun CreditsSection() {
    val uriHandler = LocalUriHandler.current
    
    GlassCard(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // BubbleWrapper App title
            Text(
                text = "BubbleWrapper App",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Built by moonmanquark
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { uriHandler.openUri("https://x.com/moonmanquark") }
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                // Profile image with gradient border
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                colors = listOf(SolanaPurple, SolanaGreen)
                            )
                        )
                        .padding(2.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape)
                            .background(BgSecondary),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "🌙",
                            fontSize = 24.sp
                        )
                    }
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "Built by",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextMuted
                    )
                    Text(
                        text = "@moonmanquark",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = SolanaPurple,
                        textDecoration = TextDecoration.Underline
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(20.dp))
            
            // Divider
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.5f)
                    .height(1.dp)
                    .background(GlassBorder)
            )
            
            Spacer(modifier = Modifier.height(20.dp))
            
            // BF Labs
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                // BF Labs logo placeholder
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(
                            Brush.linearGradient(
                                colors = listOf(SolanaGreen, SolanaPurple)
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "BF",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "Powered by",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextMuted
                    )
                    Text(
                        text = "BF Labs",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Solana Mobile badge
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(
                                SolanaPurple.copy(alpha = 0.2f),
                                SolanaGreen.copy(alpha = 0.2f)
                            )
                        )
                    )
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Filled.PhoneAndroid,
                    contentDescription = null,
                    tint = SolanaGreen,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Built for Solana Mobile",
                    style = MaterialTheme.typography.labelMedium,
                    color = TextPrimary
                )
            }
        }
    }
}
