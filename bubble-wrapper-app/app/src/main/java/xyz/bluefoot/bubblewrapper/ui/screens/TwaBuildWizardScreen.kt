package xyz.bluefoot.bubblewrapper.ui.screens

import android.content.Intent
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
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
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import kotlinx.coroutines.launch
import xyz.bluefoot.bubblewrapper.ui.theme.*
import xyz.bluefoot.bubblewrapper.utils.KeystoreGenerator
import xyz.bluefoot.bubblewrapper.utils.TwaConfigGenerator
import java.io.File

/**
 * TWA Build Wizard Screen
 * Guides users through creating a TWA from their PWA
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TwaBuildWizardScreen(
    onBack: () -> Unit,
    onNavigateToKeystore: () -> Unit,
    onNavigateToPublish: () -> Unit,
    currentKeystorePath: String = "",
    currentFingerprint: String = ""
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val clipboardManager = LocalClipboardManager.current
    
    val twaConfigGenerator = remember { TwaConfigGenerator(context) }
    val keystoreGenerator = remember { KeystoreGenerator(context) }
    
    // Current wizard step
    var currentStep by remember { mutableStateOf(0) }
    
    // Configuration state
    var packageId by remember { mutableStateOf("") }
    var domain by remember { mutableStateOf("") }
    var appName by remember { mutableStateOf("") }
    var launcherName by remember { mutableStateOf("") }
    var themeColor by remember { mutableStateOf("#9945FF") }
    var backgroundColor by remember { mutableStateOf("#0B0F1A") }
    var iconUrl by remember { mutableStateOf("") }
    var versionName by remember { mutableStateOf("1.0.0") }
    var versionCode by remember { mutableStateOf("1") }
    var orientation by remember { mutableStateOf("portrait") }
    
    // Keystore state
    var selectedKeystorePath by remember { mutableStateOf(currentKeystorePath) }
    var sha256Fingerprint by remember { mutableStateOf(currentFingerprint) }
    
    // Generation state
    var isGenerating by remember { mutableStateOf(false) }
    var generationResult by remember { mutableStateOf<TwaConfigGenerator.GenerationResult?>(null) }
    
    // Load existing keystores
    var existingKeystores by remember { mutableStateOf<List<KeystoreGenerator.KeystoreInfo>>(emptyList()) }
    
    LaunchedEffect(Unit) {
        existingKeystores = keystoreGenerator.listKeystores().mapNotNull {
            keystoreGenerator.getKeystoreInfo(it.name)
        }
        // Auto-select first keystore if available
        if (selectedKeystorePath.isEmpty() && existingKeystores.isNotEmpty()) {
            selectedKeystorePath = existingKeystores.first().path
            sha256Fingerprint = existingKeystores.first().sha256Fingerprint
        }
    }
    
    val steps = listOf(
        "PWA Details" to Icons.Filled.Language,
        "Appearance" to Icons.Filled.Palette,
        "Keystore" to Icons.Filled.Key,
        "Generate" to Icons.Filled.Build
    )
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        "Build TWA",
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold
                    )
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
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = BgSecondary
                )
            )
        },
        containerColor = BgPrimary
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Step Indicator
            StepIndicator(
                steps = steps,
                currentStep = currentStep,
                modifier = Modifier.padding(20.dp)
            )
            
            // Content
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp)
            ) {
                when (currentStep) {
                    0 -> Step1PwaDetails(
                        packageId = packageId,
                        onPackageIdChange = { packageId = it },
                        domain = domain,
                        onDomainChange = { domain = it },
                        appName = appName,
                        onAppNameChange = { appName = it },
                        launcherName = launcherName,
                        onLauncherNameChange = { launcherName = it },
                        versionName = versionName,
                        onVersionNameChange = { versionName = it },
                        versionCode = versionCode,
                        onVersionCodeChange = { versionCode = it }
                    )
                    
                    1 -> Step2Appearance(
                        themeColor = themeColor,
                        onThemeColorChange = { themeColor = it },
                        backgroundColor = backgroundColor,
                        onBackgroundColorChange = { backgroundColor = it },
                        iconUrl = iconUrl,
                        onIconUrlChange = { iconUrl = it },
                        orientation = orientation,
                        onOrientationChange = { orientation = it }
                    )
                    
                    2 -> Step3Keystore(
                        keystores = existingKeystores,
                        selectedPath = selectedKeystorePath,
                        onSelectKeystore = { info ->
                            selectedKeystorePath = info.path
                            sha256Fingerprint = info.sha256Fingerprint
                        },
                        onCreateNew = onNavigateToKeystore,
                        sha256Fingerprint = sha256Fingerprint
                    )
                    
                    3 -> Step4Generate(
                        isGenerating = isGenerating,
                        result = generationResult,
                        config = TwaConfigGenerator.TwaConfig(
                            packageId = packageId,
                            host = domain,
                            appName = appName,
                            launcherName = launcherName.ifEmpty { appName.take(12) },
                            themeColor = themeColor,
                            backgroundColor = backgroundColor,
                            iconUrl = iconUrl,
                            versionName = versionName,
                            versionCode = versionCode.toIntOrNull() ?: 1,
                            sha256Fingerprint = sha256Fingerprint,
                            orientation = orientation
                        ),
                        onGenerate = {
                            scope.launch {
                                isGenerating = true
                                
                                generationResult = twaConfigGenerator.generateTwaConfig(
                                    TwaConfigGenerator.TwaConfig(
                                        packageId = packageId,
                                        host = domain,
                                        appName = appName,
                                        launcherName = launcherName.ifEmpty { appName.take(12) },
                                        themeColor = themeColor,
                                        backgroundColor = backgroundColor,
                                        iconUrl = iconUrl,
                                        versionName = versionName,
                                        versionCode = versionCode.toIntOrNull() ?: 1,
                                        sha256Fingerprint = sha256Fingerprint,
                                        orientation = orientation
                                    )
                                )
                                
                                isGenerating = false
                            }
                        },
                        onCopyAssetLinks = {
                            val content = twaConfigGenerator.getAssetLinksContent(packageId, sha256Fingerprint)
                            clipboardManager.setText(AnnotatedString(content))
                            Toast.makeText(context, "assetlinks.json copied!", Toast.LENGTH_SHORT).show()
                        },
                        onPublish = onNavigateToPublish
                    )
                }
                
                Spacer(modifier = Modifier.height(100.dp))
            }
            
            // Navigation buttons
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(BgSecondary)
                    .padding(20.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (currentStep > 0) {
                    OutlinedButton(
                        onClick = { currentStep-- },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = TextPrimary
                        ),
                        border = BorderStroke(1.dp, GlassBorder)
                    ) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Back")
                    }
                }
                
                if (currentStep < steps.size - 1) {
                    Button(
                        onClick = { currentStep++ },
                        modifier = Modifier.weight(if (currentStep > 0) 1f else 1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = SolanaPurple
                        )
                    ) {
                        Text("Next")
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(Icons.Filled.ArrowForward, contentDescription = null)
                    }
                }
            }
        }
    }
}

@Composable
private fun StepIndicator(
    steps: List<Pair<String, ImageVector>>,
    currentStep: Int,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        steps.forEachIndexed { index, (name, icon) ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(
                            when {
                                index < currentStep -> SolanaGreen
                                index == currentStep -> SolanaPurple
                                else -> GlassBg
                            }
                        )
                        .border(
                            1.dp,
                            if (index <= currentStep) Color.Transparent else GlassBorder,
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    if (index < currentStep) {
                        Icon(
                            Icons.Filled.Check,
                            contentDescription = null,
                            tint = BgPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    } else {
                        Icon(
                            icon,
                            contentDescription = null,
                            tint = if (index == currentStep) TextPrimary else TextMuted,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(6.dp))
                
                Text(
                    text = name,
                    style = MaterialTheme.typography.labelSmall,
                    color = if (index == currentStep) TextPrimary else TextMuted,
                    textAlign = TextAlign.Center,
                    maxLines = 1
                )
            }
            
            if (index < steps.size - 1) {
                Box(
                    modifier = Modifier
                        .weight(0.5f)
                        .height(2.dp)
                        .padding(top = 19.dp)
                        .background(
                            if (index < currentStep) SolanaGreen else GlassBorder
                        )
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Step1PwaDetails(
    packageId: String,
    onPackageIdChange: (String) -> Unit,
    domain: String,
    onDomainChange: (String) -> Unit,
    appName: String,
    onAppNameChange: (String) -> Unit,
    launcherName: String,
    onLauncherNameChange: (String) -> Unit,
    versionName: String,
    onVersionNameChange: (String) -> Unit,
    versionCode: String,
    onVersionCodeChange: (String) -> Unit
) {
    Column {
        Text(
            text = "PWA Details",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = "Enter your Progressive Web App information",
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        GlassCard(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                OutlinedTextField(
                    value = domain,
                    onValueChange = { 
                        onDomainChange(it.removePrefix("https://").removePrefix("http://"))
                    },
                    label = { Text("Website Domain") },
                    placeholder = { Text("yourapp.com", color = TextMuted) },
                    leadingIcon = {
                        Icon(Icons.Filled.Language, contentDescription = null, tint = TextMuted)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = textFieldColors(),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                OutlinedTextField(
                    value = packageId,
                    onValueChange = { 
                        onPackageIdChange(it.filter { c -> c.isLetterOrDigit() || c == '.' })
                    },
                    label = { Text("Package ID") },
                    placeholder = { Text("com.example.myapp", color = TextMuted) },
                    leadingIcon = {
                        Icon(Icons.Filled.Fingerprint, contentDescription = null, tint = TextMuted)
                    },
                    supportingText = {
                        Text("Reverse domain format (e.g., com.company.app)", color = TextMuted)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = textFieldColors(),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                OutlinedTextField(
                    value = appName,
                    onValueChange = onAppNameChange,
                    label = { Text("App Name") },
                    placeholder = { Text("My Awesome App", color = TextMuted) },
                    leadingIcon = {
                        Icon(Icons.Filled.Label, contentDescription = null, tint = TextMuted)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = textFieldColors(),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                OutlinedTextField(
                    value = launcherName,
                    onValueChange = { onLauncherNameChange(it.take(12)) },
                    label = { Text("Launcher Name (max 12 chars)") },
                    placeholder = { Text("MyApp", color = TextMuted) },
                    leadingIcon = {
                        Icon(Icons.Filled.ShortText, contentDescription = null, tint = TextMuted)
                    },
                    supportingText = {
                        Text("${launcherName.length}/12 characters", color = TextMuted)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = textFieldColors(),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = versionName,
                        onValueChange = onVersionNameChange,
                        label = { Text("Version") },
                        placeholder = { Text("1.0.0", color = TextMuted) },
                        modifier = Modifier.weight(1f),
                        colors = textFieldColors(),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )
                    
                    OutlinedTextField(
                        value = versionCode,
                        onValueChange = { onVersionCodeChange(it.filter { c -> c.isDigit() }) },
                        label = { Text("Build #") },
                        placeholder = { Text("1", color = TextMuted) },
                        modifier = Modifier.weight(1f),
                        colors = textFieldColors(),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Step2Appearance(
    themeColor: String,
    onThemeColorChange: (String) -> Unit,
    backgroundColor: String,
    onBackgroundColorChange: (String) -> Unit,
    iconUrl: String,
    onIconUrlChange: (String) -> Unit,
    orientation: String,
    onOrientationChange: (String) -> Unit
) {
    Column {
        Text(
            text = "Appearance",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = "Customize your app's look and feel",
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        GlassCard(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "COLORS",
                    style = MaterialTheme.typography.labelSmall,
                    color = SolanaGreen,
                    letterSpacing = 1.sp
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Theme Color",
                            style = MaterialTheme.typography.labelMedium,
                            color = TextSecondary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        ColorInputField(
                            value = themeColor,
                            onValueChange = onThemeColorChange
                        )
                    }
                    
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Background Color",
                            style = MaterialTheme.typography.labelMedium,
                            color = TextSecondary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        ColorInputField(
                            value = backgroundColor,
                            onValueChange = onBackgroundColorChange
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(20.dp))
                
                Text(
                    text = "ICON",
                    style = MaterialTheme.typography.labelSmall,
                    color = SolanaGreen,
                    letterSpacing = 1.sp
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                OutlinedTextField(
                    value = iconUrl,
                    onValueChange = onIconUrlChange,
                    label = { Text("Icon URL") },
                    placeholder = { Text("https://yoursite.com/icon-512.png", color = TextMuted) },
                    leadingIcon = {
                        Icon(Icons.Filled.Image, contentDescription = null, tint = TextMuted)
                    },
                    supportingText = {
                        Text("512x512 PNG recommended", color = TextMuted)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = textFieldColors(),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )
                
                Spacer(modifier = Modifier.height(20.dp))
                
                Text(
                    text = "ORIENTATION",
                    style = MaterialTheme.typography.labelSmall,
                    color = SolanaGreen,
                    letterSpacing = 1.sp
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    listOf("portrait", "landscape", "any").forEach { option ->
                        FilterChip(
                            selected = orientation == option,
                            onClick = { onOrientationChange(option) },
                            label = { Text(option.replaceFirstChar { c -> c.uppercase() }) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = SolanaPurple,
                                selectedLabelColor = TextPrimary,
                                labelColor = TextSecondary
                            )
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ColorInputField(
    value: String,
    onValueChange: (String) -> Unit
) {
    val parsedColor = try {
        Color(android.graphics.Color.parseColor(value))
    } catch (e: Exception) {
        BgPrimary
    }
    
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .clip(RoundedCornerShape(12.dp))
            .border(1.dp, GlassBorder, RoundedCornerShape(12.dp))
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
        BasicTextField(
            value = value,
            onValueChange = { 
                val newValue = it.uppercase().filter { c -> c.isLetterOrDigit() || c == '#' }
                if (newValue.length <= 7) onValueChange(newValue)
            },
            modifier = Modifier.weight(1f),
            textStyle = MaterialTheme.typography.bodyMedium.copy(
                color = TextPrimary,
                fontFamily = FontFamily.Monospace
            ),
            singleLine = true
        )
    }
}

@Composable
private fun Step3Keystore(
    keystores: List<KeystoreGenerator.KeystoreInfo>,
    selectedPath: String,
    onSelectKeystore: (KeystoreGenerator.KeystoreInfo) -> Unit,
    onCreateNew: () -> Unit,
    sha256Fingerprint: String
) {
    val clipboardManager = LocalClipboardManager.current
    val context = LocalContext.current
    
    Column {
        Text(
            text = "Signing Key",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = "Select or create a keystore for signing your APK",
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Create new button
        OutlinedButton(
            onClick = onCreateNew,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = SolanaGreen
            ),
            border = BorderStroke(1.dp, SolanaGreen.copy(alpha = 0.5f))
        ) {
            Icon(Icons.Filled.Add, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Create New Keystore")
        }
        
        if (keystores.isNotEmpty()) {
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "OR SELECT EXISTING",
                style = MaterialTheme.typography.labelSmall,
                color = TextMuted,
                letterSpacing = 1.sp
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            keystores.forEach { keystore ->
                val isSelected = keystore.path == selectedPath
                
                GlassCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onSelectKeystore(keystore) }
                        .then(
                            if (isSelected) {
                                Modifier.border(2.dp, SolanaGreen, RoundedCornerShape(20.dp))
                            } else {
                                Modifier
                            }
                        )
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(
                                    if (isSelected) SolanaGreen.copy(alpha = 0.2f)
                                    else GlassBg
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Filled.Key,
                                contentDescription = null,
                                tint = if (isSelected) SolanaGreen else TextMuted,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        
                        Spacer(modifier = Modifier.width(12.dp))
                        
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = keystore.path.substringAfterLast("/"),
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium,
                                color = TextPrimary
                            )
                            Text(
                                text = "Alias: ${keystore.alias}",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondary
                            )
                        }
                        
                        if (isSelected) {
                            Icon(
                                Icons.Filled.CheckCircle,
                                contentDescription = "Selected",
                                tint = SolanaGreen,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
        
        // Show fingerprint if selected
        if (sha256Fingerprint.isNotEmpty()) {
            Spacer(modifier = Modifier.height(16.dp))
            
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "SHA-256 FINGERPRINT",
                        style = MaterialTheme.typography.labelSmall,
                        color = SolanaGreen,
                        letterSpacing = 1.sp
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(BgPrimary)
                            .clickable {
                                clipboardManager.setText(AnnotatedString(sha256Fingerprint))
                                Toast
                                    .makeText(context, "Fingerprint copied!", Toast.LENGTH_SHORT)
                                    .show()
                            }
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = sha256Fingerprint,
                            style = MaterialTheme.typography.bodySmall,
                            fontFamily = FontFamily.Monospace,
                            color = TextPrimary,
                            modifier = Modifier.weight(1f),
                            lineHeight = 16.sp
                        )
                        Icon(
                            Icons.Filled.ContentCopy,
                            contentDescription = "Copy",
                            tint = SolanaPurple,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text(
                        text = "⚡ Add this to your assetlinks.json",
                        style = MaterialTheme.typography.labelSmall,
                        color = SolanaGreen
                    )
                }
            }
        }
    }
}

@Composable
private fun Step4Generate(
    isGenerating: Boolean,
    result: TwaConfigGenerator.GenerationResult?,
    config: TwaConfigGenerator.TwaConfig,
    onGenerate: () -> Unit,
    onCopyAssetLinks: () -> Unit,
    onPublish: () -> Unit
) {
    Column {
        Text(
            text = "Generate TWA",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = "Review configuration and generate build files",
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Config Summary
        GlassCard(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "CONFIGURATION SUMMARY",
                    style = MaterialTheme.typography.labelSmall,
                    color = SolanaGreen,
                    letterSpacing = 1.sp
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                ConfigSummaryRow("Package ID", config.packageId)
                ConfigSummaryRow("Domain", config.host)
                ConfigSummaryRow("App Name", config.appName)
                ConfigSummaryRow("Version", "${config.versionName} (${config.versionCode})")
                ConfigSummaryRow("Theme", config.themeColor)
                ConfigSummaryRow("Orientation", config.orientation)
            }
        }
        
        Spacer(modifier = Modifier.height(20.dp))
        
        if (result?.success == true) {
            // Success state
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        Icons.Filled.CheckCircle,
                        contentDescription = null,
                        tint = SolanaGreen,
                        modifier = Modifier.size(48.dp)
                    )
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    Text(
                        text = "Configuration Generated!",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text(
                        text = "TWA configuration files have been created",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary,
                        textAlign = TextAlign.Center
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // assetlinks.json button
            OutlinedButton(
                onClick = onCopyAssetLinks,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = SolanaGreen
                ),
                border = BorderStroke(1.dp, SolanaGreen.copy(alpha = 0.5f))
            ) {
                Icon(Icons.Filled.ContentCopy, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Copy assetlinks.json")
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Info about next steps
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Filled.Info,
                            contentDescription = null,
                            tint = SolanaPurple,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Next Steps",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold,
                            color = TextPrimary
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    Text(
                        text = "1. Host the assetlinks.json at:\n   https://${config.host}/.well-known/assetlinks.json\n\n" +
                                "2. Use Bubblewrap CLI to build your APK:\n   bubblewrap build\n\n" +
                                "3. Publish to the Solana dApp Store",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary,
                        fontFamily = FontFamily.Monospace,
                        lineHeight = 20.sp
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(20.dp))
            
            // Continue to Publish button
            Button(
                onClick = onPublish,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent
                ),
                contentPadding = PaddingValues(),
                shape = RoundedCornerShape(14.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.horizontalGradient(listOf(SolanaPurple, SolanaPurpleDark))
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.CloudUpload, contentDescription = null, tint = TextPrimary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "Continue to Publish",
                            fontWeight = FontWeight.SemiBold,
                            color = TextPrimary
                        )
                    }
                }
            }
            
        } else {
            // Generate button
            Button(
                onClick = onGenerate,
                enabled = !isGenerating && config.packageId.isNotEmpty() && config.host.isNotEmpty(),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent,
                    disabledContainerColor = Color.Transparent
                ),
                contentPadding = PaddingValues(),
                shape = RoundedCornerShape(14.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            if (!isGenerating && config.packageId.isNotEmpty() && config.host.isNotEmpty()) {
                                Brush.horizontalGradient(listOf(SolanaPurple, SolanaPurpleDark))
                            } else {
                                Brush.horizontalGradient(listOf(TextMuted, TextMuted))
                            }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    if (isGenerating) {
                        CircularProgressIndicator(
                            color = TextPrimary,
                            modifier = Modifier.size(24.dp),
                            strokeWidth = 2.dp
                        )
                    } else {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Filled.Build, contentDescription = null, tint = TextPrimary)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                "Generate TWA Configuration",
                                fontWeight = FontWeight.SemiBold,
                                color = TextPrimary
                            )
                        }
                    }
                }
            }
            
            // Error state
            result?.error?.let { error ->
                Spacer(modifier = Modifier.height(12.dp))
                
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFFDC2626).copy(alpha = 0.1f))
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Filled.Error,
                        contentDescription = null,
                        tint = Color(0xFFEF4444),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = error,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFFEF4444)
                    )
                }
            }
        }
    }
}

@Composable
private fun ConfigSummaryRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = TextMuted
        )
        Text(
            text = value.ifEmpty { "-" },
            style = MaterialTheme.typography.bodySmall,
            color = TextPrimary,
            fontFamily = FontFamily.Monospace
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
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

// GlassCard from main app
@Composable
private fun GlassCard(
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
