package xyz.bluefoot.bubblewrapper.ui.screens

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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import xyz.bluefoot.bubblewrapper.ui.theme.*

data class GuideStep(
    val number: Int,
    val title: String,
    val description: String,
    val tips: List<String> = emptyList(),
    val warnings: List<String> = emptyList(),
    val command: String? = null
)

@Composable
fun GettingStartedGuide(onBack: () -> Unit) {
    val steps = listOf(
        GuideStep(
            number = 1,
            title = "What is a PWA?",
            description = "A Progressive Web App (PWA) is a website that behaves like a native app. It uses modern web capabilities to deliver an app-like experience.",
            tips = listOf(
                "Installable on mobile/desktop",
                "Works offline via Service Workers",
                "Fast loading and responsive",
                "Updates automatically via web"
            )
        ),
        GuideStep(
            number = 2,
            title = "Prerequisites",
            description = "Before you begin, make sure you have the following installed:",
            tips = listOf(
                "Node.js 14.15.0 or higher",
                "A hosted PWA with a web manifest",
                "HTTPS hosting (required for TWA)",
                "Your PWA must pass Lighthouse PWA audit"
            )
        ),
        GuideStep(
            number = 3,
            title = "Install Bubblewrap CLI",
            description = "Install the Bubblewrap command-line tool globally using npm:",
            command = "npm install -g @bubblewrap/cli",
            tips = listOf(
                "Requires Node.js 14.15.0+",
                "May need sudo on macOS/Linux"
            )
        ),
        GuideStep(
            number = 4,
            title = "Create Web Manifest",
            description = "Your PWA needs a valid manifest.webmanifest file. Required fields:",
            tips = listOf(
                "name: Full app name",
                "short_name: Max 12 characters",
                "start_url: Usually '/'",
                "display: 'standalone' recommended",
                "icons: 192x192 and 512x512 PNG",
                "theme_color: Your brand color",
                "background_color: Splash screen color"
            )
        ),
        GuideStep(
            number = 5,
            title = "Initialize TWA Project",
            description = "Run the init command with your manifest URL:",
            command = "bubblewrap init --manifest=https://your-domain.com/manifest.webmanifest",
            tips = listOf(
                "Bubblewrap will download your manifest",
                "Answer prompts for app configuration",
                "Creates twa-manifest.json and Android project"
            ),
            warnings = listOf(
                "Keep your keystore password secure!",
                "The same keystore is needed for all updates"
            )
        ),
        GuideStep(
            number = 6,
            title = "Configure Languages",
            description = "IMPORTANT: Add supported languages to build.gradle before building:",
            command = "android {\n  defaultConfig {\n    resConfigs \"en\", \"es\"\n  }\n}",
            warnings = listOf(
                "Do NOT skip this step!",
                "Default incorrectly declares all locales",
                "This affects your dApp Store listing"
            )
        ),
        GuideStep(
            number = 7,
            title = "Build Signed APK",
            description = "Build your release APK with the following command:",
            command = "bubblewrap build",
            tips = listOf(
                "Outputs app-release-signed.apk",
                "Uses keystore from init step",
                "APK is ready for dApp Store submission"
            )
        ),
        GuideStep(
            number = 7,
            title = "Digital Asset Links",
            description = "Set up DAL to enable fullscreen mode (hide browser UI):",
            command = "bubblewrap fingerprint generateAssetLinks",
            tips = listOf(
                "Host assetlinks.json at /.well-known/",
                "Must be accessible over HTTPS",
                "Verify with Google's DAL tool"
            ),
            warnings = listOf(
                "Without DAL, browser URL bar will show!",
                "This is a common issue for new developers"
            )
        ),
        GuideStep(
            number = 8,
            title = "Test Your App",
            description = "Install on a device or emulator to test:",
            command = "bubblewrap install",
            tips = listOf(
                "Check splash screen colors match",
                "Verify no browser UI is showing",
                "Test offline functionality",
                "Check back button behavior"
            )
        )
    )
    
    GuideContent(
        title = "Getting Started",
        subtitle = "Convert your PWA to an Android TWA",
        steps = steps,
        onBack = onBack
    )
}

@Composable
fun DappStoreGuide(onBack: () -> Unit) {
    val steps = listOf(
        GuideStep(
            number = 1,
            title = "Before You Begin",
            description = "Ensure you have the following ready for submission:",
            tips = listOf(
                "Release-signed APK (not debug build)",
                "APK signed with a NEW unique key",
                "App icon (512x512 PNG)",
                "Banner graphic (1200x600)",
                "4+ screenshots (1080p recommended)",
                "App description and metadata"
            ),
            warnings = listOf(
                "Cannot use same key as Google Play!",
                "Debug builds will be rejected"
            )
        ),
        GuideStep(
            number = 2,
            title = "Create Publisher Account",
            description = "Sign up at the Solana dApp Publisher Portal:",
            tips = listOf(
                "Navigate to publish.solanamobile.com",
                "Fill out publisher profile",
                "Complete KYC/KYB verification",
                "This may take a few days"
            )
        ),
        GuideStep(
            number = 3,
            title = "Connect Publisher Wallet",
            description = "Connect a Solana wallet that will be your publisher identity:",
            tips = listOf(
                "Use Phantom, Solflare, or Backpack",
                "Need ~0.2 SOL for fees",
                "This wallet is required for ALL future updates",
                "Use a private RPC for reliability"
            ),
            warnings = listOf(
                "DO NOT lose access to this wallet!",
                "You cannot change publisher wallet later"
            )
        ),
        GuideStep(
            number = 4,
            title = "Choose Storage Provider",
            description = "Select where your app assets will be stored:",
            tips = listOf(
                "ArDrive is recommended",
                "Use cost calculator to estimate SOL needed",
                "Assets are stored permanently on-chain",
                "Larger apps = higher storage cost"
            )
        ),
        GuideStep(
            number = 5,
            title = "Add App Details",
            description = "Fill out your app listing information:",
            tips = listOf(
                "App name and description",
                "Upload icon and screenshots",
                "Add feature descriptions",
                "Select categories",
                "Provide testing instructions if needed"
            )
        ),
        GuideStep(
            number = 6,
            title = "Submit Release",
            description = "Upload your APK and submit for review:",
            tips = listOf(
                "Press 'New Version' button",
                "Upload your signed APK",
                "Sign multiple wallet transactions",
                "This mints your Release NFT"
            ),
            warnings = listOf(
                "Approve ALL signing requests!",
                "Skipping may cause missing assets"
            )
        ),
        GuideStep(
            number = 7,
            title = "After Submission",
            description = "Your app enters the review queue:",
            tips = listOf(
                "Review typically takes 1-3 business days",
                "You'll be notified of approval/rejection",
                "Check publisher portal for status",
                "Contact support if issues arise"
            )
        )
    )
    
    GuideContent(
        title = "Solana dApp Store",
        subtitle = "Complete submission guide",
        steps = steps,
        onBack = onBack
    )
}

@Composable
fun AssetLinksGuide(onBack: () -> Unit) {
    val steps = listOf(
        GuideStep(
            number = 1,
            title = "What are Digital Asset Links?",
            description = "DALs establish trust between your website and Android app:",
            tips = listOf(
                "Proves you own both the website and app",
                "Enables fullscreen TWA experience",
                "Required to hide browser URL bar",
                "Uses SHA256 certificate fingerprint"
            )
        ),
        GuideStep(
            number = 2,
            title = "Get Your Fingerprint",
            description = "Extract SHA256 fingerprint from your keystore:",
            command = "keytool -list -v -keystore android.keystore",
            tips = listOf(
                "Look for 'SHA256:' in output",
                "Copy the full fingerprint",
                "Keep this value secure"
            )
        ),
        GuideStep(
            number = 3,
            title = "Add to TWA Manifest",
            description = "Register the fingerprint with Bubblewrap:",
            command = "bubblewrap fingerprint add <SHA256_FINGERPRINT>"
        ),
        GuideStep(
            number = 4,
            title = "Generate assetlinks.json",
            description = "Create the Digital Asset Links file:",
            command = "bubblewrap fingerprint generateAssetLinks",
            tips = listOf(
                "Creates assetlinks.json file",
                "Contains your package ID and fingerprint"
            )
        ),
        GuideStep(
            number = 5,
            title = "Host the File",
            description = "Upload assetlinks.json to your web server at:",
            tips = listOf(
                "https://your-domain.com/.well-known/assetlinks.json",
                "Must be accessible over HTTPS",
                "No redirects allowed",
                "Content-Type: application/json"
            ),
            warnings = listOf(
                "Path must be exactly /.well-known/assetlinks.json",
                "File must be publicly accessible"
            )
        ),
        GuideStep(
            number = 6,
            title = "Verify Setup",
            description = "Test that DAL is working correctly:",
            tips = listOf(
                "Use Google's DAL verification tool",
                "Install app and check for browser bar",
                "Clear Chrome cache if still showing bar",
                "May take time for Chrome to recognize"
            )
        )
    )
    
    GuideContent(
        title = "Digital Asset Links",
        subtitle = "Enable fullscreen TWA experience",
        steps = steps,
        onBack = onBack
    )
}

@Composable
fun KeystoreGuide(onBack: () -> Unit) {
    val steps = listOf(
        GuideStep(
            number = 1,
            title = "Why a Separate Key?",
            description = "The dApp Store requires a unique signing key:",
            tips = listOf(
                "Cannot use Google Play signing key",
                "Each store needs its own identity",
                "Prevents conflicts between stores",
                "No 30% fee on dApp Store transactions!"
            ),
            warnings = listOf(
                "CRITICAL: Never use same key as Play Store"
            )
        ),
        GuideStep(
            number = 2,
            title = "Generate New Keystore",
            description = "Create a new keystore for dApp Store releases:",
            command = "keytool -genkey -v -keystore dappstore.keystore \\\n  -alias dappstore \\\n  -keyalg RSA \\\n  -keysize 2048 \\\n  -validity 10000",
            tips = listOf(
                "Use a strong password",
                "Remember the alias name",
                "Validity of 10000 days (~27 years)"
            )
        ),
        GuideStep(
            number = 3,
            title = "Secure Your Keystore",
            description = "Protect your keystore and credentials:",
            tips = listOf(
                "Store in secure location (not git!)",
                "Back up to encrypted cloud storage",
                "Document passwords securely",
                "Consider using a password manager"
            ),
            warnings = listOf(
                "Losing keystore = cannot update app!",
                "No recovery option available"
            )
        ),
        GuideStep(
            number = 4,
            title = "Verify APK Signature",
            description = "Confirm your APK is properly signed:",
            command = "apksigner verify --print-certs app-release.apk",
            tips = listOf(
                "Should show your certificate info",
                "Verify before each submission"
            )
        )
    )
    
    GuideContent(
        title = "Signing & Keystores",
        subtitle = "Managing your release keys",
        steps = steps,
        onBack = onBack
    )
}

@Composable
fun TroubleshootingGuide(onBack: () -> Unit) {
    val steps = listOf(
        GuideStep(
            number = 1,
            title = "Browser Bar Showing",
            description = "If the URL bar appears in your TWA:",
            tips = listOf(
                "Check Digital Asset Links setup",
                "Verify assetlinks.json is accessible",
                "Confirm SHA256 fingerprint matches",
                "Clear Chrome app cache",
                "Wait a few hours for Chrome to update"
            )
        ),
        GuideStep(
            number = 2,
            title = "White Splash Screen",
            description = "If splash screen shows wrong colors:",
            tips = listOf(
                "Check theme_color in manifest.webmanifest",
                "Verify background_color matches",
                "Update twa-manifest.json colors",
                "Rebuild with: bubblewrap update && bubblewrap build"
            )
        ),
        GuideStep(
            number = 3,
            title = "App Rejected",
            description = "Common rejection reasons:",
            tips = listOf(
                "Debug build submitted (use release)",
                "Wrong signing key used",
                "Missing required screenshots",
                "Policy violation detected",
                "Check rejection email for details"
            )
        ),
        GuideStep(
            number = 4,
            title = "Build Failures",
            description = "If bubblewrap build fails:",
            tips = listOf(
                "Update Node.js to latest LTS",
                "Run: npm cache clean --force",
                "Delete node_modules and reinstall",
                "Check Java/JDK is installed (17+)",
                "Verify Android SDK path"
            )
        ),
        GuideStep(
            number = 5,
            title = "Transaction Failures",
            description = "If wallet transactions fail during submission:",
            tips = listOf(
                "Ensure sufficient SOL balance",
                "Use a private RPC endpoint",
                "Try during off-peak hours",
                "Check network connection",
                "Try a different browser"
            )
        ),
        GuideStep(
            number = 6,
            title = "Need More Help?",
            description = "Additional resources:",
            tips = listOf(
                "Solana Mobile Discord community",
                "Email: concerns@dappstore.solanamobile.com",
                "GitHub issues on Bubblewrap repo",
                "Stack Overflow with 'twa' tag"
            )
        )
    )
    
    GuideContent(
        title = "Troubleshooting",
        subtitle = "Common issues and solutions",
        steps = steps,
        onBack = onBack
    )
}

@Composable
fun StylingGuide(onBack: () -> Unit) {
    val steps = listOf(
        GuideStep(
            number = 1,
            title = "Splash Screen",
            description = "Customize the splash screen in your PWA manifest or twa-manifest.json",
            tips = listOf(
                "background_color: Sets splash background",
                "theme_color: Sets status bar color",
                "icon: Up to 512px center icon (maskable)",
                "Ensure contrast between icon and background"
            )
        ),
        GuideStep(
            number = 2,
            title = "Browser Preference",
            description = "Ensure best experience by prioritizing Chrome or modern browsers",
            tips = listOf(
                "Bubblewrap defaults to best-available TWA provider",
                "Chrome offers the most stable TWA support",
                "Fallbacks handle devices without Chrome",
                "Use 'ChromePreferredCustomTabs.java' helper for external links"
            )
        ),
        GuideStep(
            number = 3,
            title = "Glassmorphism UI",
            description = "Add premium glass effects for a modern Solana feel:",
            tips = listOf(
                "Use semi-transparent backgrounds (alpha 0.05-0.1)",
                "Add subtle borders (1px, alpha 0.1)",
                "Apply backdrop-filter: blur(20px)",
                "Use Solana gradients for accents"
            )
        ),
        GuideStep(
            number = 4,
            title = "Navigation Bar",
            description = "Make your PWA feel native with bottom navigation:",
            tips = listOf(
                "Fixed position bottom bar",
                "CSS env(safe-area-inset-bottom) padding",
                "Active states with glow effects",
                "Hide web browser URL bar (display: standalone)"
            )
        ),
        GuideStep(
            number = 5,
            title = "Dark Mode",
            description = "Integrate with system dark mode settings:",
            tips = listOf(
                "Use CSS variables for theme colors",
                "@media (prefers-color-scheme: dark)",
                "Solana Dark: #0B0F1A background",
                "Test contrast ratios for accessibility"
            )
        )
    )
    
    GuideContent(
        title = "Styling & Optimization",
        subtitle = "Optimization guide for mobile experience",
        steps = steps,
        onBack = onBack
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GuideContent(
    title: String,
    subtitle: String,
    steps: List<GuideStep>,
    onBack: () -> Unit
) {
    Scaffold(
        containerColor = BgPrimary,
        topBar = {
            TopAppBar(
                title = { Text(title, color = TextPrimary) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = TextPrimary
                        )
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
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            steps.forEach { step ->
                GuideStepCard(step)
                Spacer(modifier = Modifier.height(16.dp))
            }
            
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun GuideStepCard(step: GuideStep) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0x0DFFFFFF)
        ),
        border = BorderStroke(1.dp, Color(0x1AFFFFFF))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Step header
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(SolanaPurple),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = step.number.toString(),
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = step.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Description
            Text(
                text = step.description,
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary
            )
            
            // Command if present
            step.command?.let { cmd ->
                Spacer(modifier = Modifier.height(12.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(BgPrimary)
                        .border(1.dp, Color(0x1AFFFFFF), RoundedCornerShape(8.dp))
                        .padding(12.dp)
                ) {
                    Text(
                        text = cmd,
                        style = MaterialTheme.typography.bodySmall,
                        color = SolanaGreen,
                        fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                    )
                }
            }
            
            // Tips
            if (step.tips.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                step.tips.forEach { tip ->
                    Row(
                        modifier = Modifier.padding(vertical = 3.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Icon(
                            Icons.Filled.Check,
                            contentDescription = null,
                            tint = SolanaGreen,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = tip,
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary
                        )
                    }
                }
            }
            
            // Warnings
            if (step.warnings.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                step.warnings.forEach { warning ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0x20FF5C7A))
                            .padding(10.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Icon(
                            Icons.Filled.Warning,
                            contentDescription = null,
                            tint = Color(0xFFFF5C7A),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = warning,
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFFFF5C7A),
                            fontWeight = FontWeight.Medium
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                }
            }
        }
    }
}
