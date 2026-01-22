package xyz.bluefoot.bubblewrapper.ui.screens

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import xyz.bluefoot.bubblewrapper.ui.theme.*
import xyz.bluefoot.bubblewrapper.utils.KeystoreGenerator
import xyz.bluefoot.bubblewrapper.utils.TwaConfigGenerator

/**
 * Keystore Generation Screen
 * Allows users to create Android signing keystores from within the app
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KeystoreScreen(
    onBack: () -> Unit,
    onKeystoreCreated: (String, String) -> Unit = { _, _ -> } // path, sha256
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val clipboardManager = LocalClipboardManager.current
    
    val keystoreGenerator = remember { KeystoreGenerator(context) }
    
    // Form state
    var keystoreName by remember { mutableStateOf("") }
    var keystorePassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var alias by remember { mutableStateOf("android") }
    var commonName by remember { mutableStateOf("") }
    var organization by remember { mutableStateOf("") }
    var organizationalUnit by remember { mutableStateOf("") }
    var locality by remember { mutableStateOf("") }
    var state by remember { mutableStateOf("") }
    var country by remember { mutableStateOf("US") }
    var validityYears by remember { mutableStateOf("25") }
    
    // UI state
    var showPassword by remember { mutableStateOf(false) }
    var isGenerating by remember { mutableStateOf(false) }
    var generatedResult by remember { mutableStateOf<KeystoreGenerator.KeystoreResult?>(null) }
    var showExistingKeystores by remember { mutableStateOf(false) }
    var existingKeystores by remember { mutableStateOf<List<KeystoreGenerator.KeystoreInfo>>(emptyList()) }
    
    // Load existing keystores
    LaunchedEffect(Unit) {
        existingKeystores = keystoreGenerator.listKeystores().mapNotNull { 
            keystoreGenerator.getKeystoreInfo(it.name) 
        }
    }
    
    // Password validation
    val passwordsMatch = keystorePassword == confirmPassword && keystorePassword.isNotEmpty()
    val passwordValid = keystorePassword.length >= 6
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        "Generate Keystore",
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
                actions = {
                    if (existingKeystores.isNotEmpty()) {
                        IconButton(onClick = { showExistingKeystores = !showExistingKeystores }) {
                            Icon(
                                Icons.Filled.Folder,
                                contentDescription = "View Keystores",
                                tint = SolanaGreen
                            )
                        }
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
                .verticalScroll(rememberScrollState())
                .padding(20.dp)
        ) {
            // Success result
            if (generatedResult?.success == true) {
                KeystoreSuccessCard(
                    result = generatedResult!!,
                    onCopyFingerprint = { fingerprint ->
                        clipboardManager.setText(AnnotatedString(fingerprint))
                        Toast.makeText(context, "Fingerprint copied!", Toast.LENGTH_SHORT).show()
                    },
                    onDone = {
                        onKeystoreCreated(
                            generatedResult!!.keystorePath ?: "",
                            generatedResult!!.sha256Fingerprint ?: ""
                        )
                    }
                )
                return@Column
            }
            
            // Show existing keystores panel
            AnimatedVisibility(visible = showExistingKeystores) {
                ExistingKeystoresCard(
                    keystores = existingKeystores,
                    onSelect = { info ->
                        generatedResult = KeystoreGenerator.KeystoreResult(
                            success = true,
                            keystorePath = info.path,
                            sha256Fingerprint = info.sha256Fingerprint,
                            sha1Fingerprint = info.sha1Fingerprint
                        )
                    },
                    onDelete = { info ->
                        keystoreGenerator.deleteKeystore(info.path.substringAfterLast("/"))
                        existingKeystores = keystoreGenerator.listKeystores().mapNotNull {
                            keystoreGenerator.getKeystoreInfo(it.name)
                        }
                    },
                    onCopyFingerprint = { fingerprint ->
                        clipboardManager.setText(AnnotatedString(fingerprint))
                        Toast.makeText(context, "Fingerprint copied!", Toast.LENGTH_SHORT).show()
                    }
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
            
            // Info card
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Filled.Info,
                            contentDescription = null,
                            tint = SolanaGreen,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "About Android Signing Keys",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold,
                            color = TextPrimary
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "A keystore is required to sign your Android APK. The SHA-256 fingerprint from your keystore must be added to your website's assetlinks.json file for TWA verification.",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary,
                        lineHeight = 18.sp
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(20.dp))
            
            // Keystore Details Section
            SectionHeader(
                icon = Icons.Filled.Key,
                title = "Keystore Details"
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    OutlinedTextField(
                        value = keystoreName,
                        onValueChange = { keystoreName = it.filter { c -> c.isLetterOrDigit() || c == '-' || c == '_' } },
                        label = { Text("Keystore Name") },
                        placeholder = { Text("my-app-release", color = TextMuted) },
                        leadingIcon = {
                            Icon(Icons.Filled.Badge, contentDescription = null, tint = TextMuted)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = keystoreTextFieldColors(),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    OutlinedTextField(
                        value = alias,
                        onValueChange = { alias = it },
                        label = { Text("Key Alias") },
                        placeholder = { Text("android", color = TextMuted) },
                        leadingIcon = {
                            Icon(Icons.Filled.Label, contentDescription = null, tint = TextMuted)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = keystoreTextFieldColors(),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    OutlinedTextField(
                        value = keystorePassword,
                        onValueChange = { keystorePassword = it },
                        label = { Text("Password") },
                        placeholder = { Text("Min 6 characters", color = TextMuted) },
                        leadingIcon = {
                            Icon(Icons.Filled.Lock, contentDescription = null, tint = TextMuted)
                        },
                        trailingIcon = {
                            IconButton(onClick = { showPassword = !showPassword }) {
                                Icon(
                                    if (showPassword) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                                    contentDescription = "Toggle password",
                                    tint = TextMuted
                                )
                            }
                        },
                        visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth(),
                        colors = keystoreTextFieldColors(),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true,
                        isError = keystorePassword.isNotEmpty() && !passwordValid
                    )
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    OutlinedTextField(
                        value = confirmPassword,
                        onValueChange = { confirmPassword = it },
                        label = { Text("Confirm Password") },
                        leadingIcon = {
                            Icon(Icons.Filled.LockOpen, contentDescription = null, tint = TextMuted)
                        },
                        trailingIcon = {
                            if (confirmPassword.isNotEmpty()) {
                                Icon(
                                    if (passwordsMatch) Icons.Filled.CheckCircle else Icons.Filled.Cancel,
                                    contentDescription = null,
                                    tint = if (passwordsMatch) SolanaGreen else Color(0xFFEF4444)
                                )
                            }
                        },
                        visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth(),
                        colors = keystoreTextFieldColors(),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true,
                        isError = confirmPassword.isNotEmpty() && !passwordsMatch
                    )
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    OutlinedTextField(
                        value = validityYears,
                        onValueChange = { validityYears = it.filter { c -> c.isDigit() } },
                        label = { Text("Validity (Years)") },
                        placeholder = { Text("25", color = TextMuted) },
                        leadingIcon = {
                            Icon(Icons.Filled.Schedule, contentDescription = null, tint = TextMuted)
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth(),
                        colors = keystoreTextFieldColors(),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(20.dp))
            
            // Certificate Details Section
            SectionHeader(
                icon = Icons.Filled.VerifiedUser,
                title = "Certificate Details (Optional)"
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    OutlinedTextField(
                        value = commonName,
                        onValueChange = { commonName = it },
                        label = { Text("Common Name (CN)") },
                        placeholder = { Text("Your Name or App Name", color = TextMuted) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = keystoreTextFieldColors(),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        OutlinedTextField(
                            value = organization,
                            onValueChange = { organization = it },
                            label = { Text("Organization (O)") },
                            placeholder = { Text("Company", color = TextMuted) },
                            modifier = Modifier.weight(1f),
                            colors = keystoreTextFieldColors(),
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true
                        )
                        
                        OutlinedTextField(
                            value = organizationalUnit,
                            onValueChange = { organizationalUnit = it },
                            label = { Text("Unit (OU)") },
                            placeholder = { Text("Dept", color = TextMuted) },
                            modifier = Modifier.weight(1f),
                            colors = keystoreTextFieldColors(),
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        OutlinedTextField(
                            value = locality,
                            onValueChange = { locality = it },
                            label = { Text("City (L)") },
                            placeholder = { Text("City", color = TextMuted) },
                            modifier = Modifier.weight(1f),
                            colors = keystoreTextFieldColors(),
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true
                        )
                        
                        OutlinedTextField(
                            value = state,
                            onValueChange = { state = it },
                            label = { Text("State (ST)") },
                            placeholder = { Text("State", color = TextMuted) },
                            modifier = Modifier.weight(1f),
                            colors = keystoreTextFieldColors(),
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    OutlinedTextField(
                        value = country,
                        onValueChange = { country = it.take(2).uppercase() },
                        label = { Text("Country Code (C)") },
                        placeholder = { Text("US", color = TextMuted) },
                        modifier = Modifier.fillMaxWidth(0.5f),
                        colors = keystoreTextFieldColors(),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Generate Button
            Button(
                onClick = {
                    scope.launch {
                        isGenerating = true
                        
                        val config = KeystoreGenerator.KeystoreConfig(
                            keystoreName = keystoreName.ifEmpty { "app-release" },
                            keystorePassword = keystorePassword,
                            alias = alias.ifEmpty { "android" },
                            aliasPassword = keystorePassword,
                            validityYears = validityYears.toIntOrNull() ?: 25,
                            commonName = commonName.ifEmpty { keystoreName.ifEmpty { "App" } },
                            organization = organization,
                            organizationalUnit = organizationalUnit,
                            locality = locality,
                            state = state,
                            country = country.ifEmpty { "US" }
                        )
                        
                        generatedResult = keystoreGenerator.generateKeystore(config)
                        isGenerating = false
                        
                        if (generatedResult?.success == true) {
                            // Refresh keystores list
                            existingKeystores = keystoreGenerator.listKeystores().mapNotNull {
                                keystoreGenerator.getKeystoreInfo(it.name)
                            }
                        } else {
                            Toast.makeText(
                                context,
                                "Failed: ${generatedResult?.error}",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                },
                enabled = passwordsMatch && passwordValid && !isGenerating,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent,
                    disabledContainerColor = Color.Transparent
                ),
                contentPadding = PaddingValues()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            if (passwordsMatch && passwordValid && !isGenerating) {
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
                            Icon(
                                Icons.Filled.VpnKey,
                                contentDescription = null,
                                tint = TextPrimary
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Generate Keystore",
                                fontWeight = FontWeight.SemiBold,
                                color = TextPrimary,
                                fontSize = 16.sp
                            )
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Warning
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF422006).copy(alpha = 0.5f))
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Filled.Warning,
                    contentDescription = null,
                    tint = Color(0xFFFBBF24),
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Store your keystore password securely. You will need it to update your app. Lost keystores cannot be recovered!",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFFFBBF24),
                    lineHeight = 18.sp
                )
            }
            
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun SectionHeader(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            icon,
            contentDescription = null,
            tint = SolanaPurple,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = TextPrimary
        )
    }
}

@Composable
private fun KeystoreSuccessCard(
    result: KeystoreGenerator.KeystoreResult,
    onCopyFingerprint: (String) -> Unit,
    onDone: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        // Success Icon
        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
                .background(
                    Brush.linearGradient(listOf(SolanaGreen, SolanaGreen.copy(alpha = 0.7f)))
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Filled.CheckCircle,
                contentDescription = null,
                tint = BgPrimary,
                modifier = Modifier.size(48.dp)
            )
        }
        
        Spacer(modifier = Modifier.height(20.dp))
        
        Text(
            text = "Keystore Generated!",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = "Your signing key has been created successfully",
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Fingerprint Cards
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
                        .clickable { onCopyFingerprint(result.sha256Fingerprint ?: "") }
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = result.sha256Fingerprint ?: "",
                        style = MaterialTheme.typography.bodySmall,
                        fontFamily = FontFamily.Monospace,
                        color = TextPrimary,
                        modifier = Modifier.weight(1f),
                        lineHeight = 16.sp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        Icons.Filled.ContentCopy,
                        contentDescription = "Copy",
                        tint = SolanaPurple,
                        modifier = Modifier.size(18.dp)
                    )
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = "⚡ Use this in your assetlinks.json",
                    style = MaterialTheme.typography.labelSmall,
                    color = SolanaGreen
                )
            }
        }
        
        Spacer(modifier = Modifier.height(12.dp))
        
        GlassCard(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "SHA-1 FINGERPRINT",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextMuted,
                    letterSpacing = 1.sp
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(BgPrimary)
                        .clickable { onCopyFingerprint(result.sha1Fingerprint ?: "") }
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = result.sha1Fingerprint ?: "",
                        style = MaterialTheme.typography.bodySmall,
                        fontFamily = FontFamily.Monospace,
                        color = TextSecondary,
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        Icons.Filled.ContentCopy,
                        contentDescription = "Copy",
                        tint = TextMuted,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(12.dp))
        
        // Path info
        GlassCard(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "KEYSTORE LOCATION",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextMuted,
                    letterSpacing = 1.sp
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = result.keystorePath ?: "",
                    style = MaterialTheme.typography.bodySmall,
                    fontFamily = FontFamily.Monospace,
                    color = TextSecondary
                )
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Continue Button
        Button(
            onClick = onDone,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
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
                        Brush.horizontalGradient(listOf(SolanaPurple, SolanaPurpleDark))
                    ),
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Continue",
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        Icons.Filled.ArrowForward,
                        contentDescription = null,
                        tint = TextPrimary
                    )
                }
            }
        }
    }
}

@Composable
private fun ExistingKeystoresCard(
    keystores: List<KeystoreGenerator.KeystoreInfo>,
    onSelect: (KeystoreGenerator.KeystoreInfo) -> Unit,
    onDelete: (KeystoreGenerator.KeystoreInfo) -> Unit,
    onCopyFingerprint: (String) -> Unit
) {
    GlassCard(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Filled.Folder,
                    contentDescription = null,
                    tint = SolanaGreen,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Existing Keystores",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            keystores.forEach { keystore ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(BgPrimary)
                        .clickable { onSelect(keystore) }
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
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
                    
                    IconButton(onClick = { onCopyFingerprint(keystore.sha256Fingerprint) }) {
                        Icon(
                            Icons.Filled.ContentCopy,
                            contentDescription = "Copy fingerprint",
                            tint = SolanaPurple,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    
                    IconButton(onClick = { onDelete(keystore) }) {
                        Icon(
                            Icons.Filled.Delete,
                            contentDescription = "Delete",
                            tint = Color(0xFFEF4444),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun keystoreTextFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = SolanaPurple,
    unfocusedBorderColor = GlassBorder,
    focusedLabelColor = SolanaPurple,
    unfocusedLabelColor = TextSecondary,
    cursorColor = SolanaPurple,
    focusedTextColor = TextPrimary,
    unfocusedTextColor = TextPrimary
)

// GlassCard from main app - duplicated here for self-containment
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
