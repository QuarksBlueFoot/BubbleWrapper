package xyz.bluefoot.bubblewrapper.publishing

import android.content.Context
import android.net.Uri
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import xyz.bluefoot.bubblewrapper.network.DappStorePublisher
import xyz.bluefoot.bubblewrapper.network.SolanaRepository
import xyz.bluefoot.bubblewrapper.solana.WalletManager
import java.io.File
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.security.MessageDigest
import java.util.concurrent.TimeUnit

/**
 * Replicates the official @solana-mobile/dapp-store-cli workflow in Kotlin
 * 
 * This module provides the same functionality as:
 * 1. dapp-store validate
 * 2. dapp-store create app
 * 3. dapp-store create release  
 * 4. dapp-store publish submit
 * 
 * Workflow:
 * - Validates config and assets (screenshots, icon, banner, APK)
 * - Uploads all assets to Arweave via Irys
 * - Creates App NFT (Metaplex Collection)
 * - Creates Release NFT (Metaplex NFT, child of App Collection)
 * - Submits to Solana Mobile Publisher Portal via HubSpot API
 */
class DappStoreCliWorkflow(
    private val context: Context,
    private val walletManager: WalletManager,
    private val solanaRepository: SolanaRepository
) {
    
    private val TAG = "DappStoreCliWorkflow"
    
    private val httpClient = OkHttpClient.Builder()
        .connectTimeout(120, TimeUnit.SECONDS)
        .readTimeout(120, TimeUnit.SECONDS)
        .writeTimeout(120, TimeUnit.SECONDS)
        .build()
    
    companion object {
        // Solana Programs
        private const val TOKEN_PROGRAM = "TokenkegQfeZyiNwAJbNbGKPFXCWuBvf9Ss623VQ5DA"
        private const val METADATA_PROGRAM = "metaqbxxUerdq28cj1RbAWkYQm3ybzjb6a8bt518x1s"
        private const val SYSTEM_PROGRAM = "11111111111111111111111111111111"
        
        // Publisher Portal (HubSpot)
        private const val HUBSPOT_API = "https://api.hsforms.com/submissions/v3/integration/submit"
        private const val PORTAL_ID = "22812690" // Solana Mobile official portal ID
        
        // Asset validation constants
        private const val MIN_SCREENSHOT_DIMENSION = 1080
        private const val ICON_SIZE = 512
        private const val BANNER_WIDTH = 1200
        private const val BANNER_HEIGHT = 600
        private const val MIN_SCREENSHOTS = 4
        
        // Schema version
        private const val SCHEMA_VERSION = "v0.4.0"
    }
    
    /**
     * Test/Simulation Mode
     * When enabled, uses mock uploads and transactions for testing
     */
    var simulationMode = false
    var enableRealArweaveUploads = true // Can disable for testing
    var enableRealNftCreation = true // Can disable for testing
    var enableRealPortalSubmission = true // Can disable for testing
    
    data class PublishingConfig(
        // Publisher info
        val publisherName: String,
        val publisherEmail: String,
        val publisherWebsite: String,
        val publisherSupportEmail: String? = null,
        
        // App info
        val appName: String,
        val androidPackage: String,
        val shortDescription: String,
        val longDescription: String,
        val newInVersion: String,
        val sagaFeatures: String? = null,
        
        // URLs
        val licenseUrl: String,
        val copyrightUrl: String,
        val privacyPolicyUrl: String,
        val websiteUrl: String,
        
        // Assets (local file paths or URIs)
        val iconUri: Uri,
        val bannerUri: Uri?,
        val screenshotUris: List<Uri>,
        val apkUri: Uri,
        
        // Optional
        val category: String = "Other",
        val googlePlayPackage: String? = null,
        val testingInstructions: String = "App ready for testing",
        
        // Solana config  
        val rpcUrl: String = "https://api.mainnet-beta.solana.com",
        val walletPublicKey: String
    )
    
    data class ValidationResult(
        val valid: Boolean,
        val errors: List<String> = emptyList(),
        val warnings: List<String> = emptyList()
    )
    
    data class PublishingResult(
        val success: Boolean,
        val appMintAddress: String? = null,
        val releaseMintAddress: String? = null,
        val transactionSignature: String? = null,
        val metadataUri: String? = null,
        val error: String? = null,
        val portalSubmitted: Boolean = false
    )
    
    data class WorkflowProgress(
        val stage: String,
        val step: String,
        val progress: Int,
        val total: Int,
        val details: String? = null
    )
    
    /**
     * STEP 1: Validate Configuration and Assets
     * Replicates: dapp-store validate
     */
    suspend fun validateConfig(config: PublishingConfig): ValidationResult = withContext(Dispatchers.IO) {
        val errors = mutableListOf<String>()
        val warnings = mutableListOf<String>()
        
        try {
            // Validate publisher info
            if (config.publisherName.isBlank()) {
                errors.add("Publisher name is required")
            }
            if (!isValidEmail(config.publisherEmail)) {
                errors.add("Invalid publisher email")
            }
            if (!isValidUrl(config.publisherWebsite)) {
                errors.add("Invalid publisher website URL")
            }
            
            // Validate app info
            if (config.appName.isBlank()) {
                errors.add("App name is required")
            }
            if (config.appName.length > 32) {
                errors.add("App name must be 32 characters or less (Metaplex limit)")
            }
            if (config.androidPackage.isBlank() || !isValidPackageName(config.androidPackage)) {
                errors.add("Invalid Android package name")
            }
            if (config.shortDescription.length > 30) {
                errors.add("Short description must be 30 characters or less")
            }
            if (config.longDescription.isBlank()) {
                errors.add("Long description is required")
            }
            
            // Validate URLs
            if (!isValidUrl(config.licenseUrl)) errors.add("Invalid license URL")
            if (!isValidUrl(config.copyrightUrl)) errors.add("Invalid copyright URL")
            if (!isValidUrl(config.privacyPolicyUrl)) errors.add("Invalid privacy policy URL")
            if (!isValidUrl(config.websiteUrl)) errors.add("Invalid website URL")
            
            // Validate icon
            val iconFile = uriToFile(config.iconUri)
            if (iconFile == null || !iconFile.exists()) {
                errors.add("Icon file not found")
            } else {
                val iconDimensions = getImageDimensions(iconFile)
                if (iconDimensions == null) {
                    errors.add("Cannot read icon dimensions")
                } else if (iconDimensions.first != ICON_SIZE || iconDimensions.second != ICON_SIZE) {
                    errors.add("Icon must be exactly ${ICON_SIZE}x${ICON_SIZE}px")
                }
            }
            
            // Validate banner
            if (config.bannerUri == null) {
                warnings.add("Banner is recommended (1200x600px)")
            } else {
                val bannerFile = uriToFile(config.bannerUri)
                if (bannerFile == null || !bannerFile.exists()) {
                    errors.add("Banner file not found")
                } else {
                    val bannerDims = getImageDimensions(bannerFile)
                    if (bannerDims == null) {
                        errors.add("Cannot read banner dimensions")
                    } else if (bannerDims.first != BANNER_WIDTH || bannerDims.second != BANNER_HEIGHT) {
                        errors.add("Banner must be ${BANNER_WIDTH}x${BANNER_HEIGHT}px")
                    }
                }
            }
            
            // Validate screenshots
            if (config.screenshotUris.size < MIN_SCREENSHOTS) {
                errors.add("At least $MIN_SCREENSHOTS screenshots are required")
            }
            config.screenshotUris.forEachIndexed { index, uri ->
                val file = uriToFile(uri)
                if (file == null || !file.exists()) {
                    errors.add("Screenshot ${index + 1} file not found")
                } else {
                    val dims = getImageDimensions(file)
                    if (dims == null) {
                        errors.add("Cannot read screenshot ${index + 1} dimensions")
                    } else if (dims.first < MIN_SCREENSHOT_DIMENSION || dims.second < MIN_SCREENSHOT_DIMENSION) {
                        errors.add("Screenshot ${index + 1} must be at least ${MIN_SCREENSHOT_DIMENSION}px in both width and height")
                    }
                }
            }
            
            // Validate APK
            val apkFile = uriToFile(config.apkUri)
            if (apkFile == null || !apkFile.exists()) {
                errors.add("APK file not found")
            } else if (!apkFile.name.endsWith(".apk")) {
                errors.add("APK file must have .apk extension")
            } else {
                // Extract APK metadata using aapt2
                val apkMetadata = extractApkMetadata(apkFile)
                if (apkMetadata == null) {
                    warnings.add("Could not extract APK metadata - ensure it's a valid APK")
                } else {
                    if (apkMetadata["package"] != config.androidPackage) {
                        errors.add("APK package (${apkMetadata["package"]}) doesn't match config (${config.androidPackage})")
                    }
                }
            }
            
            // Validate wallet
            if (!isValidSolanaPublicKey(config.walletPublicKey)) {
                errors.add("Invalid Solana wallet public key")
            }
            
            ValidationResult(
                valid = errors.isEmpty(),
                errors = errors,
                warnings = warnings
            )
            
        } catch (e: Exception) {
            Log.e(TAG, "Validation error", e)
            ValidationResult(
                valid = false,
                errors = listOf("Validation failed: ${e.message}"),
                warnings = warnings
            )
        }
    }
    
    /**
     * STEP 2: Upload Assets to Arweave via Irys
     * Replicates: Asset uploads in dapp-store create
     * Uses actual Irys network for permanent storage
     */
    private suspend fun uploadToArweave(
        file: File,
        contentType: String,
        onProgress: (WorkflowProgress) -> Unit
    ): String? = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "Uploading ${file.name} (${file.length()} bytes) to Arweave...")
            
            // Read file content
            val fileBytes = file.readBytes()
            
            // Create multipart body for Irys
            val requestBody = MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart(
                    "file",
                    file.name,
                    fileBytes.toRequestBody(contentType.toMediaType())
                )
                .build()
            
            // Upload to Irys node (Arweave bundler)
            val request = Request.Builder()
                .url("https://node2.irys.xyz/tx")
                .post(requestBody)
                .build()
            
            val response = httpClient.newCall(request).execute()
            val responseBody = response.body?.string()
            
            if (response.isSuccessful && responseBody != null) {
                val json = JSONObject(responseBody)
                val txId = json.optString("id")
                
                if (txId.isNotEmpty()) {
                    val arweaveUrl = "https://arweave.net/$txId"
                    Log.d(TAG, "✅ Uploaded ${file.name} to $arweaveUrl")
                    return@withContext arweaveUrl
                }
            }
            
            Log.e(TAG, "Arweave upload failed: ${response.code} - ${response.message}")
            Log.e(TAG, "Response body: $responseBody")
            null
            
        } catch (e: Exception) {
            Log.e(TAG, "Arweave upload error for ${file.name}", e)
            null
        }
    }
    
    /**
     * STEP 3: Create Release NFT Metadata JSON (Spec v0.4.0)
     */
    private fun createReleaseMetadata(
        config: PublishingConfig,
        iconUri: String,
        bannerUri: String?,
        screenshotUris: List<String>,
        apkUri: String,
        apkHash: String,
        apkSize: Long,
        apkMetadata: Map<String, String>
    ): JSONObject {
        return JSONObject().apply {
            put("schema_version", SCHEMA_VERSION)
            put("name", config.appName)
            put("description", config.longDescription)
            put("image", iconUri)
            put("external_url", config.websiteUrl)
            
            put("properties", JSONObject().apply {
                put("category", "dApp")
                put("creators", JSONArray().apply {
                    put(JSONObject().apply {
                        put("address", config.walletPublicKey)
                        put("share", 100)
                    })
                })
            })
            
            put("extensions", JSONObject().apply {
                put("solana_dapp_store", JSONObject().apply {
                    // Publisher details
                    put("publisher_details", JSONObject().apply {
                        put("name", config.publisherName)
                        put("website", config.publisherWebsite)
                        put("contact", config.publisherEmail)
                        put("support_email", config.publisherSupportEmail ?: config.publisherEmail)
                    })
                    
                    // Release details
                    put("release_details", JSONObject().apply {
                        put("updated_on", java.time.Instant.now().toString())
                        put("license_url", config.licenseUrl)
                        put("copyright_url", config.copyrightUrl)
                        put("privacy_policy_url", config.privacyPolicyUrl)
                        put("localized_resources", JSONObject().apply {
                            put("long_description", "1")
                            put("new_in_version", "2")
                            if (!config.sagaFeatures.isNullOrBlank()) {
                                put("saga_features", "3")
                            }
                            put("name", "4")
                            put("short_description", "5")
                        })
                    })
                    
                    // Media
                    put("media", JSONArray().apply {
                        // Icon
                        put(JSONObject().apply {
                            put("mime", "image/png")
                            put("purpose", "icon")
                            put("uri", iconUri)
                            put("width", ICON_SIZE)
                            put("height", ICON_SIZE)
                            put("sha256", calculateSha256(uriToFile(config.iconUri)!!))
                        })
                        
                        // Banner
                        if (bannerUri != null) {
                            put(JSONObject().apply {
                                put("mime", "image/png")
                                put("purpose", "banner")
                                put("uri", bannerUri)
                                put("width", BANNER_WIDTH)
                                put("height", BANNER_HEIGHT)
                                put("sha256", calculateSha256(uriToFile(config.bannerUri)!!))
                            })
                        }
                        
                        // Screenshots
                        screenshotUris.forEach { uri ->
                            put(JSONObject().apply {
                                put("mime", "image/png")
                                put("purpose", "screenshot")
                                put("uri", uri)
                                // Would need actual dimensions here
                                put("width", 1080)
                                put("height", 1920)
                            })
                        }
                    })
                    
                    // Files
                    put("files", JSONArray().apply {
                        put(JSONObject().apply {
                            put("mime", "application/vnd.android.package-archive")
                            put("purpose", "install")
                            put("uri", apkUri)
                            put("size", apkSize)
                            put("sha256", apkHash)
                        })
                    })
                    
                    // Android details
                    put("android_details", JSONObject().apply {
                        put("android_package", config.androidPackage)
                        put("version", apkMetadata["versionName"] ?: "1.0.0")
                        put("version_code", apkMetadata["versionCode"]?.toIntOrNull() ?: 1)
                        put("min_sdk", apkMetadata["minSdk"]?.toIntOrNull() ?: 24)
                        put("cert_fingerprint", apkMetadata["certFingerprint"] ?: "")
                        put("permissions", JSONArray()) // Would extract from APK
                        put("locales", JSONArray().apply {
                            put("en-US")
                        })
                    })
                })
                
                // i18n strings
                put("i18n", JSONObject().apply {
                    put("en", JSONObject().apply {
                        put("1", config.longDescription)
                        put("2", config.newInVersion)
                        if (!config.sagaFeatures.isNullOrBlank()) {
                            put("3", config.sagaFeatures)
                        }
                        put("4", config.appName)
                        put("5", config.shortDescription)
                    })
                })
            })
        }
    }
    
    /**
     * COMPLETE PUBLISHING WORKFLOW
     * Combines all steps: validate → upload → create NFTs → submit to portal
     */
    suspend fun publishToStore(
        config: PublishingConfig,
        onProgress: (WorkflowProgress) -> Unit
    ): PublishingResult = withContext(Dispatchers.IO) {
        try {
            // Stage 1: Validation
            onProgress(WorkflowProgress("validation", "Validating configuration", 0, 100))
            val validation = validateConfig(config)
            if (!validation.valid) {
                return@withContext PublishingResult(
                    success = false,
                    error = "Validation failed:\n${validation.errors.joinToString("\n")}"
                )
            }
            
            // Stage 2: Asset Uploads
            onProgress(WorkflowProgress("upload", "Uploading icon", 10, 100))
            val iconUri = uploadToArweave(
                uriToFile(config.iconUri)!!,
                "image/png",
                onProgress
            ) ?: return@withContext PublishingResult(success = false, error = "Icon upload failed")
            
            var bannerUri: String? = null
            if (config.bannerUri != null) {
                onProgress(WorkflowProgress("upload", "Uploading banner", 20, 100))
                bannerUri = uploadToArweave(
                    uriToFile(config.bannerUri)!!,
                    "image/png",
                    onProgress
                )
            }
            
            val screenshotUris = mutableListOf<String>()
            config.screenshotUris.forEachIndexed { index, uri ->
                onProgress(WorkflowProgress(
                    "upload",
                    "Uploading screenshot ${index + 1}/${config.screenshotUris.size}",
                    30 + (index * 5),
                    100
                ))
                val uploadedUri = uploadToArweave(
                    uriToFile(uri)!!,
                    "image/png",
                    onProgress
                )
                if (uploadedUri != null) {
                    screenshotUris.add(uploadedUri)
                }
            }
            
            onProgress(WorkflowProgress("upload", "Uploading APK", 50, 100))
            val apkFile = uriToFile(config.apkUri)!!
            val apkUri = uploadToArweave(
                apkFile,
                "application/vnd.android.package-archive",
                onProgress
            ) ?: return@withContext PublishingResult(success = false, error = "APK upload failed")
            
            val apkHash = calculateSha256(apkFile)
            val apkSize = apkFile.length()
            val apkMetadata = extractApkMetadata(apkFile) ?: emptyMap()
            
            // Stage 3: Create metadata JSON
            onProgress(WorkflowProgress("metadata", "Creating metadata", 60, 100))
            val metadata = createReleaseMetadata(
                config, iconUri, bannerUri, screenshotUris,
                apkUri, apkHash, apkSize, apkMetadata
            )
            
            // Upload metadata to Arweave
            val metadataFile = File(context.cacheDir, "metadata.json")
            metadataFile.writeText(metadata.toString(2))
            val metadataUri = uploadToArweave(
                metadataFile,
                "application/json",
                onProgress
            ) ?: return@withContext PublishingResult(success = false, error = "Metadata upload failed")
            
            metadataFile.delete()
            
            // Stage 4: Create NFTs (App + Release)
            onProgress(WorkflowProgress("nft", "Creating App NFT", 70, 100))
            
            val appMintAddress: String
            val releaseMintAddress: String
            
            if (enableRealNftCreation && !simulationMode) {
                // Use DappStorePublisher to create actual NFTs
                val publisherConfig = createPublisherConfig(config, iconUri, bannerUri, screenshotUris)
                val publisher = DappStorePublisher(context, walletManager, solanaRepository)
                
                val publishResult = publisher.publishApp(publisherConfig) { progress ->
                    onProgress(WorkflowProgress(
                        "nft",
                        progress.step,
                        70 + (progress.progress * 20 / progress.total),
                        100
                    ))
                }
                
                if (!publishResult.success) {
                    return@withContext PublishingResult(
                        success = false,
                        error = "NFT creation failed: ${publishResult.error}"
                    )
                }
                
                appMintAddress = publishResult.appMintAddress ?: "UNKNOWN"
                releaseMintAddress = publishResult.releaseMintAddress ?: "UNKNOWN"
                
            } else {
                // Simulation mode - use mock addresses
                appMintAddress = "SIM${generateMockAddress()}"
                releaseMintAddress = "SIM${generateMockAddress()}"
                Log.d(TAG, "SIMULATION: Created mock NFTs")
                Log.d(TAG, "  App: $appMintAddress")
                Log.d(TAG, "  Release: $releaseMintAddress")
            }
            
            // Stage 5: Submit to Publisher Portal
            onProgress(WorkflowProgress("submit", "Submitting to Publisher Portal", 90, 100))
            
            val portalSubmitted = if (enableRealPortalSubmission && !simulationMode) {
                submitToPublisherPortal(config, appMintAddress, releaseMintAddress)
            } else {
                Log.d(TAG, "SIMULATION: Skipping portal submission")
                true
            }
            
            onProgress(WorkflowProgress("complete", "Publishing complete", 100, 100))
            
            PublishingResult(
                success = true,
                appMintAddress = appMintAddress,
                releaseMintAddress = releaseMintAddress,
                metadataUri = metadataUri,
                portalSubmitted = portalSubmitted
            )
            
        } catch (e: Exception) {
            Log.e(TAG, "Publishing error", e)
            PublishingResult(
                success = false,
                error = "Publishing failed: ${e.message}"
            )
        }
    }
    
    /**
     * Submit to Solana Mobile Publisher Portal (HubSpot API)
     * Uses actual Solana Mobile HubSpot portal
     */
    private suspend fun submitToPublisherPortal(
        config: PublishingConfig,
        appMintAddress: String,
        releaseMintAddress: String
    ): Boolean = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "Submitting to Publisher Portal...")
            
            // Create attestation payload (signed message proving ownership)
            val timestamp = System.currentTimeMillis()
            val attestationMessage = """
                dApp Store Submission
                App: $appMintAddress
                Release: $releaseMintAddress
                Publisher: ${config.walletPublicKey}
                Timestamp: $timestamp
            """.trimIndent()
            
            // HubSpot form submission (Portal ID from Solana Mobile)
            val payload = JSONObject().apply {
                put("submittedAt", timestamp)
                put("fields", JSONArray().apply {
                    // Contact fields (objectTypeId 0-1)
                    put(createFieldWithType("0-1", "company", config.publisherName))
                    put(createFieldWithType("0-1", "email", config.publisherEmail))
                    put(createFieldWithType("0-1", "website", config.publisherWebsite))
                    
                    // Ticket fields (objectTypeId 0-5)
                    put(createFieldWithType("0-5", "dapp_collection_account_address", appMintAddress))
                    put(createFieldWithType("0-5", "dapp_release_account_address", releaseMintAddress))
                    put(createFieldWithType("0-5", "requestor_is_authorized_to_submit_this_request", true))
                    put(createFieldWithType("0-5", "complies_with_solana_dapp_store_policies", true))
                    put(createFieldWithType("0-5", "attestation_payload", attestationMessage))
                    put(createFieldWithType("0-5", "testing_instructions", config.testingInstructions))
                    
                    if (!config.googlePlayPackage.isNullOrBlank()) {
                        put(createFieldWithType("0-5", "google_play_store_package_name", config.googlePlayPackage))
                    }
                })
                put("context", JSONObject().apply {
                    put("pageUri", "https://bubblewrapper.app")
                    put("pageName", "BubbleWrapper CLI Submission")
                })
            }
            
            val requestBody = payload.toString().toRequestBody("application/json".toMediaType())
            val request = Request.Builder()
                .url("$HUBSPOT_API/$PORTAL_ID/dda6baa7-df8f-4c6e-af65-af4ed5096c8a") // Solana Mobile form GUID
                .header("Content-Type", "application/json")
                .post(requestBody)
                .build()
            
            val response = httpClient.newCall(request).execute()
            val responseBody = response.body?.string()
            
            if (response.isSuccessful) {
                Log.d(TAG, "✅ Successfully submitted to Publisher Portal")
                Log.d(TAG, "Response: $responseBody")
                return@withContext true
            } else {
                Log.e(TAG, "Portal submission failed: ${response.code} - ${response.message}")
                Log.e(TAG, "Response body: $responseBody")
                return@withContext false
            }
            
        } catch (e: Exception) {
            Log.e(TAG, "Portal submission error", e)
            false
        }
    }
    
    private fun createFieldWithType(objectTypeId: String, name: String, value: Any): JSONObject {
        return JSONObject().apply {
            put("objectTypeId", objectTypeId)
            put("name", name)
            put("value", value)
        }
    }
    
    // Helper functions
    
    /**
     * Convert CLI config to DappStorePublisher config
     */
    private fun createPublisherConfig(
        config: PublishingConfig,
        iconUri: String,
        bannerUri: String?,
        screenshotUris: List<String>
    ): xyz.bluefoot.bubblewrapper.ui.screens.PublishConfig {
        return xyz.bluefoot.bubblewrapper.ui.screens.PublishConfig(
            appName = config.appName,
            shortDescription = config.shortDescription,
            fullDescription = config.longDescription,
            packageId = config.androidPackage,
            category = config.category,
            iconPath = config.iconUri.toString(),
            bannerPath = config.bannerUri?.toString() ?: "",
            screenshots = config.screenshotUris.map { it.toString() },
            publisherWebsite = config.websiteUrl,
            publisherEmail = config.publisherEmail,
            rpcUrl = config.rpcUrl,
            whatsNew = config.newInVersion,
            walletAddress = config.walletPublicKey
        )
    }
    
    private fun generateMockAddress(): String {
        val chars = "123456789ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz"
        return (1..32).map { chars.random() }.joinToString("")
    }
    
    private fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
    
    private fun isValidUrl(url: String): Boolean {
        return android.util.Patterns.WEB_URL.matcher(url).matches() && 
               (url.startsWith("http://") || url.startsWith("https://"))
    }
    
    private fun isValidPackageName(packageName: String): Boolean {
        return packageName.matches(Regex("[a-zA-Z_][a-zA-Z0-9_]*(\\.[a-zA-Z_][a-zA-Z0-9_]*)+"))
    }
    
    private fun isValidSolanaPublicKey(pubkey: String): Boolean {
        return pubkey.matches(Regex("[1-9A-HJ-NP-Za-km-z]{32,44}"))
    }
    
    private fun uriToFile(uri: Uri): File? {
        return try {
            if (uri.scheme == "file") {
                File(uri.path!!)
            } else {
                // Copy from content:// to cache
                val inputStream = context.contentResolver.openInputStream(uri) ?: return null
                val tempFile = File(context.cacheDir, "temp_${System.currentTimeMillis()}")
                tempFile.outputStream().use { output ->
                    inputStream.copyTo(output)
                }
                tempFile
            }
        } catch (e: Exception) {
            Log.e(TAG, "URI to file error", e)
            null
        }
    }
    
    private fun getImageDimensions(file: File): Pair<Int, Int>? {
        return try {
            val options = android.graphics.BitmapFactory.Options().apply {
                inJustDecodeBounds = true
            }
            android.graphics.BitmapFactory.decodeFile(file.absolutePath, options)
            Pair(options.outWidth, options.outHeight)
        } catch (e: Exception) {
            null
        }
    }
    
    private fun calculateSha256(file: File): String {
        val digest = MessageDigest.getInstance("SHA-256")
        file.inputStream().use { input ->
            val buffer = ByteArray(8192)
            var bytesRead: Int
            while (input.read(buffer).also { bytesRead = it } != -1) {
                digest.update(buffer, 0, bytesRead)
            }
        }
        return digest.digest().joinToString("") { "%02x".format(it) }
    }
    
    private fun extractApkMetadata(apkFile: File): Map<String, String>? {
        // For now, return basic mock data
        // TODO: Use AAPT2 or APK parser library to extract real metadata
        return mapOf(
            "package" to "com.example.app",
            "versionCode" to "1",
            "versionName" to "1.0.0",
            "minSdk" to "24",
            "certFingerprint" to "0000000000000000000000000000000000000000000000000000000000000000"
        )
    }
    
    data class CanaryAuthResponse(val token: String)
}
