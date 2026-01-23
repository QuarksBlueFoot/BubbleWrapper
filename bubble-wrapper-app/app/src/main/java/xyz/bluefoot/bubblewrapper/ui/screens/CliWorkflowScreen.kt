package xyz.bluefoot.bubblewrapper.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.launch
import xyz.bluefoot.bubblewrapper.publishing.DappStoreCliWorkflow
import xyz.bluefoot.bubblewrapper.solana.WalletManager
import xyz.bluefoot.bubblewrapper.network.SolanaRepository

/**
 * CLI-Style Publishing Wizard
 * Guides users through the same workflow as @solana-mobile/dapp-store-cli:
 * 1. Configure (like creating config.yaml)
 * 2. Validate (dapp-store validate)
 * 3. Upload Assets
 * 4. Create App NFT (dapp-store create app)
 * 5. Create Release NFT (dapp-store create release)
 * 6. Submit to Portal (dapp-store publish submit)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CliWorkflowScreen(
    onBack: () -> Unit,
    walletManager: WalletManager,
    solanaRepository: SolanaRepository
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    
    val workflow = remember {
        DappStoreCliWorkflow(context, walletManager, solanaRepository)
    }
    
    var currentStep by remember { mutableStateOf(0) }
    var config by remember { mutableStateOf(createEmptyConfig()) }
    var validationResult by remember { mutableStateOf<DappStoreCliWorkflow.ValidationResult?>(null) }
    var publishingResult by remember { mutableStateOf<DappStoreCliWorkflow.PublishingResult?>(null) }
    var isProcessing by remember { mutableStateOf(false) }
    var progress by remember { mutableStateOf<DappStoreCliWorkflow.WorkflowProgress?>(null) }
    
    val steps = listOf(
        "Configure",
        "Validate",
        "Upload Assets",
        "Create App NFT",
        "Create Release NFT",
        "Submit to Portal"
    )
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("CLI Publishing Workflow") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
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
            
            // Progress Stepper
            StepProgressIndicator(
                currentStep = currentStep,
                steps = steps
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Current Step Content
            when (currentStep) {
                0 -> ConfigureStep(
                    config = config,
                    onConfigChange = { config = it },
                    onNext = { currentStep = 1 }
                )
                
                1 -> ValidateStep(
                    config = config,
                    validationResult = validationResult,
                    isProcessing = isProcessing,
                    onValidate = {
                        scope.launch {
                            isProcessing = true
                            validationResult = workflow.validateConfig(config)
                            isProcessing = false
                        }
                    },
                    onNext = { 
                        if (validationResult?.valid == true) {
                            currentStep = 2 
                        }
                    },
                    onBack = { currentStep = 0 }
                )
                
                2 -> UploadAssetsStep(
                    config = config,
                    progress = progress,
                    onStart = {
                        scope.launch {
                            isProcessing = true
                            // This will be handled as part of the full workflow
                            currentStep = 3
                            isProcessing = false
                        }
                    },
                    onBack = { currentStep = 1 }
                )
                
                3 -> CreateAppNftStep(
                    progress = progress,
                    onStart = {
                        scope.launch {
                            isProcessing = true
                            // Handled in full workflow
                            currentStep = 4
                            isProcessing = false
                        }
                    },
                    onBack = { currentStep = 2 }
                )
                
                4 -> CreateReleaseNftStep(
                    progress = progress,
                    onStart = {
                        scope.launch {
                            isProcessing = true
                            // Handled in full workflow
                            currentStep = 5
                            isProcessing = false
                        }
                    },
                    onBack = { currentStep = 3 }
                )
                
                5 -> SubmitToPortalStep(
                    config = config,
                    publishingResult = publishingResult,
                    isProcessing = isProcessing,
                    progress = progress,
                    onSubmit = {
                        scope.launch {
                            isProcessing = true
                            publishingResult = workflow.publishToStore(config) { prog ->
                                progress = prog
                            }
                            isProcessing = false
                        }
                    },
                    onBack = { currentStep = 4 }
                )
            }
        }
    }
}

@Composable
fun StepProgressIndicator(
    currentStep: Int,
    steps: List<String>
) {
    Column {
        LinearProgressIndicator(
            progress = (currentStep + 1).toFloat() / steps.size.toFloat(),
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = "Step ${currentStep + 1} of ${steps.size}: ${steps[currentStep]}",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun ConfigureStep(
    config: DappStoreCliWorkflow.PublishingConfig,
    onConfigChange: (DappStoreCliWorkflow.PublishingConfig) -> Unit,
    onNext: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Step 1: Configure Your App",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "This is equivalent to creating config.yaml in the CLI",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Publisher Info
            SectionHeader("Publisher Information")
            
            OutlinedTextField(
                value = config.publisherName,
                onValueChange = { onConfigChange(config.copy(publisherName = it)) },
                label = { Text("Publisher Name") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            OutlinedTextField(
                value = config.publisherEmail,
                onValueChange = { onConfigChange(config.copy(publisherEmail = it)) },
                label = { Text("Publisher Email") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            
            // App Info
            Spacer(modifier = Modifier.height(16.dp))
            SectionHeader("App Information")
            
            OutlinedTextField(
                value = config.appName,
                onValueChange = { onConfigChange(config.copy(appName = it)) },
                label = { Text("App Name (max 32 chars)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                supportingText = { Text("${config.appName.length}/32") }
            )
            
            // TODO: Add file pickers for assets
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Button(
                onClick = onNext,
                modifier = Modifier.align(Alignment.End)
            ) {
                Text("Next: Validate")
                Icon(Icons.Filled.ArrowForward, null, Modifier.padding(start = 8.dp))
            }
        }
    }
}

@Composable
fun ValidateStep(
    config: DappStoreCliWorkflow.PublishingConfig,
    validationResult: DappStoreCliWorkflow.ValidationResult?,
    isProcessing: Boolean,
    onValidate: () -> Unit,
    onNext: () -> Unit,
    onBack: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Step 2: Validate Configuration",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "Equivalent to: dapp-store validate",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            if (validationResult != null) {
                if (validationResult.valid) {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Filled.CheckCircle,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = "✓ Configuration is valid",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                } else {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer
                        )
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Validation Errors:",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.error
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            validationResult.errors.forEach { error ->
                                Text("• $error", color = MaterialTheme.colorScheme.onErrorContainer)
                            }
                        }
                    }
                }
                
                if (validationResult.warnings.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.tertiaryContainer
                        )
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Warnings:",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                            validationResult.warnings.forEach { warning ->
                                Text("• $warning", style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                OutlinedButton(onClick = onBack) {
                    Icon(Icons.Filled.ArrowBack, null, Modifier.padding(end = 8.dp))
                    Text("Back")
                }
                
                if (validationResult?.valid == true) {
                    Button(onClick = onNext) {
                        Text("Next: Upload Assets")
                        Icon(Icons.Filled.ArrowForward, null, Modifier.padding(start = 8.dp))
                    }
                } else {
                    Button(
                        onClick = onValidate,
                        enabled = !isProcessing
                    ) {
                        if (isProcessing) {
                            CircularProgressIndicator(modifier = Modifier.size(16.dp))
                        } else {
                            Text("Validate")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun UploadAssetsStep(
    config: DappStoreCliWorkflow.PublishingConfig,
    progress: DappStoreCliWorkflow.WorkflowProgress?,
    onStart: () -> Unit,
    onBack: () -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Step 3: Upload Assets to Arweave",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "Assets will be permanently stored on Arweave via Irys",
                style = MaterialTheme.typography.bodyMedium
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Asset checklist
            AssetChecklistItem("Icon (512x512px)", checked = true)
            AssetChecklistItem("Banner (1200x600px)", checked = config.bannerUri != null)
            AssetChecklistItem("Screenshots (4+, 1080px+)", checked = config.screenshotUris.size >= 4)
            AssetChecklistItem("APK file", checked = true)
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                OutlinedButton(onClick = onBack) {
                    Icon(Icons.Filled.ArrowBack, null, Modifier.padding(end = 8.dp))
                    Text("Back")
                }
                
                Button(onClick = onStart) {
                    Text("Start Upload")
                    Icon(Icons.Filled.CloudUpload, null, Modifier.padding(start = 8.dp))
                }
            }
        }
    }
}

@Composable
fun CreateAppNftStep(
    progress: DappStoreCliWorkflow.WorkflowProgress?,
    onStart: () -> Unit,
    onBack: () -> Unit
) {
    CliCommandCard(
        title = "Step 4: Create App NFT",
        cliCommand = "dapp-store create app",
        description = "Creates a Metaplex Collection NFT representing your app",
        progress = progress,
        onStart = onStart,
        onBack = onBack
    )
}

@Composable
fun CreateReleaseNftStep(
    progress: DappStoreCliWorkflow.WorkflowProgress?,
    onStart: () -> Unit,
    onBack: () -> Unit
) {
    CliCommandCard(
        title = "Step 5: Create Release NFT",
        cliCommand = "dapp-store create release",
        description = "Creates a Release NFT as child of the App Collection",
        progress = progress,
        onStart = onStart,
        onBack = onBack
    )
}

@Composable
fun SubmitToPortalStep(
    config: DappStoreCliWorkflow.PublishingConfig,
    publishingResult: DappStoreCliWorkflow.PublishingResult?,
    isProcessing: Boolean,
    progress: DappStoreCliWorkflow.WorkflowProgress?,
    onSubmit: () -> Unit,
    onBack: () -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Step 6: Submit to Publisher Portal",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "Equivalent to: dapp-store publish submit",
                style = MaterialTheme.typography.bodyMedium,
                fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
            )
            
            if (progress != null) {
                Spacer(modifier = Modifier.height(16.dp))
                LinearProgressIndicator(
                    progress = progress.progress / progress.total.toFloat(),
                    modifier = Modifier.fillMaxWidth()
                )
                Text(
                    text = "${progress.stage}: ${progress.step}",
                    style = MaterialTheme.typography.bodySmall
                )
            }
            
            if (publishingResult != null) {
                Spacer(modifier = Modifier.height(16.dp))
                if (publishingResult.success) {
                    SuccessCard(publishingResult)
                } else {
                    ErrorCard(publishingResult.error ?: "Unknown error")
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                OutlinedButton(
                    onClick = onBack,
                    enabled = !isProcessing
                ) {
                    Icon(Icons.Filled.ArrowBack, null, Modifier.padding(end = 8.dp))
                    Text("Back")
                }
                
                Button(
                    onClick = onSubmit,
                    enabled = !isProcessing
                ) {
                    if (isProcessing) {
                        CircularProgressIndicator(modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Publishing...")
                    } else {
                        Text("Publish to Store")
                        Icon(Icons.Filled.Send, null, Modifier.padding(start = 8.dp))
                    }
                }
            }
        }
    }
}

// Helper Composables

@Composable
fun SectionHeader(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.SemiBold,
        color = MaterialTheme.colorScheme.primary
    )
    Spacer(modifier = Modifier.height(8.dp))
}

@Composable
fun AssetChecklistItem(label: String, checked: Boolean) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = 4.dp)
    ) {
        Icon(
            if (checked) Icons.Filled.CheckCircle else Icons.Filled.RadioButtonUnchecked,
            contentDescription = null,
            tint = if (checked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(label)
    }
}

@Composable
fun CliCommandCard(
    title: String,
    cliCommand: String,
    description: String,
    progress: DappStoreCliWorkflow.WorkflowProgress?,
    onStart: () -> Unit,
    onBack: () -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Surface(
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = MaterialTheme.shapes.small
            ) {
                Text(
                    text = "$ $cliCommand",
                    modifier = Modifier.padding(12.dp),
                    fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(text = description)
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                OutlinedButton(onClick = onBack) {
                    Icon(Icons.Filled.ArrowBack, null, Modifier.padding(end = 8.dp))
                    Text("Back")
                }
                
                Button(onClick = onStart) {
                    Text("Create NFT")
                    Icon(Icons.Filled.ArrowForward, null, Modifier.padding(start = 8.dp))
                }
            }
        }
    }
}

@Composable
fun SuccessCard(result: DappStoreCliWorkflow.PublishingResult) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Filled.CheckCircle,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(32.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Successfully Published!",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            if (result.appMintAddress != null) {
                InfoRow("App NFT", result.appMintAddress)
            }
            if (result.releaseMintAddress != null) {
                InfoRow("Release NFT", result.releaseMintAddress)
            }
            if (result.metadataUri != null) {
                InfoRow("Metadata URI", result.metadataUri)
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "Review typically takes 3-4 business days",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun ErrorCard(error: String) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Filled.Error,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Publishing Failed",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.error
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(error, color = MaterialTheme.colorScheme.onErrorContainer)
        }
    }
}

@Composable
fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.padding(vertical = 4.dp)
    ) {
        Text(
            text = "$label: ",
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.SemiBold
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall,
            fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
        )
    }
}

private fun createEmptyConfig() = DappStoreCliWorkflow.PublishingConfig(
    publisherName = "",
    publisherEmail = "",
    publisherWebsite = "",
    appName = "",
    androidPackage = "",
    shortDescription = "",
    longDescription = "",
    newInVersion = "",
    licenseUrl = "",
    copyrightUrl = "",
    privacyPolicyUrl = "",
    websiteUrl = "",
    iconUri = android.net.Uri.EMPTY,
    bannerUri = null,
    screenshotUris = emptyList(),
    apkUri = android.net.Uri.EMPTY,
    walletPublicKey = ""
)
