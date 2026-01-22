package xyz.bluefoot.bubblewrapper.network

import android.content.Context
import android.net.Uri
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.util.concurrent.TimeUnit

/**
 * dApp Store submission configuration
 */
data class DappStoreConfig(
    val publisher: PublisherInfo,
    val app: AppInfo,
    val release: ReleaseInfo,
    val solanaConfig: SolanaConfig
)

data class PublisherInfo(
    val name: String,
    val email: String,
    val website: String? = null,
    val address: String  // Solana public key
)

data class AppInfo(
    val name: String,
    val shortDescription: String,
    val longDescription: String,
    val packageId: String,
    val category: String,
    val iconUri: String,
    val heroUri: String? = null,
    val screenshotUris: List<String> = emptyList(),
    val privacyPolicyUrl: String? = null,
    val termsOfServiceUrl: String? = null
)

data class ReleaseInfo(
    val version: String,
    val versionCode: Int,
    val apkPath: String,
    val releaseNotes: String
)

data class SolanaConfig(
    val rpcUrl: String,
    val cluster: String = "mainnet-beta"
)

/**
 * Response types from dApp Store API
 */
data class UploadResponse(
    val success: Boolean,
    val uri: String?,
    val error: String?
)

data class PublishResponse(
    val success: Boolean,
    val transactionSignature: String?,
    val appMintAddress: String?,
    val releaseAddress: String?,
    val error: String?
)

data class AppMetadata(
    val address: String,
    val name: String,
    val publisher: String,
    val category: String,
    val latestVersion: String,
    val iconUrl: String
)

/**
 * Service for interacting with the Solana dApp Store
 * 
 * The dApp Store uses on-chain NFTs for app listings.
 * This service handles:
 * - Asset uploads to decentralized storage (Shadow Drive, Arweave, etc.)
 * - Metadata generation following Metaplex standards
 * - Transaction building for NFT minting
 */
class DappStoreService(
    private val context: Context,
    private val solanaRepository: SolanaRepository
) {
    
    private val client = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(120, TimeUnit.SECONDS)
        .build()
    
    private val gson = Gson()
    
    companion object {
        // dApp Store API endpoints
        private const val DAPP_STORE_API = "https://api.dappstore.solanamobile.com"
        private const val SHADOW_DRIVE_API = "https://shadow-storage.genesysgo.net"
        
        // NFT Standards
        private const val METAPLEX_STANDARD = "metaplex"
        
        // Categories
        val CATEGORIES = listOf(
            "DeFi",
            "NFTs",
            "Gaming",
            "Social",
            "Utilities",
            "Finance",
            "Productivity",
            "Developer Tools",
            "Education",
            "Entertainment",
            "Other"
        )
    }
    
    /**
     * Upload an asset file to decentralized storage
     * Returns the URI of the uploaded file
     */
    suspend fun uploadAsset(
        filePath: String,
        mimeType: String
    ): Result<String> = withContext(Dispatchers.IO) {
        try {
            val file = File(filePath)
            if (!file.exists()) {
                return@withContext Result.failure(Exception("File not found: $filePath"))
            }
            
            val requestBody = MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart(
                    "file",
                    file.name,
                    file.asRequestBody(mimeType.toMediaType())
                )
                .build()
            
            val request = Request.Builder()
                .url("$SHADOW_DRIVE_API/upload")
                .post(requestBody)
                .build()
            
            val response = client.newCall(request).execute()
            val responseBody = response.body?.string()
            
            if (response.isSuccessful && responseBody != null) {
                val uploadResponse = gson.fromJson(responseBody, UploadResponse::class.java)
                if (uploadResponse.success && uploadResponse.uri != null) {
                    Result.success(uploadResponse.uri)
                } else {
                    Result.failure(Exception(uploadResponse.error ?: "Upload failed"))
                }
            } else {
                Result.failure(Exception("Upload failed: ${response.code}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Upload asset from Android content URI
     */
    suspend fun uploadAssetFromUri(
        uri: Uri,
        mimeType: String
    ): Result<String> = withContext(Dispatchers.IO) {
        try {
            val inputStream = context.contentResolver.openInputStream(uri)
                ?: return@withContext Result.failure(Exception("Cannot open file"))
            
            val tempFile = File.createTempFile("upload_", ".tmp", context.cacheDir)
            tempFile.outputStream().use { output ->
                inputStream.copyTo(output)
            }
            inputStream.close()
            
            val result = uploadAsset(tempFile.absolutePath, mimeType)
            tempFile.delete()
            
            result
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Generate app metadata JSON following Metaplex standard
     */
    fun generateAppMetadata(config: DappStoreConfig): String {
        val metadata = mapOf(
            "name" to config.app.name,
            "symbol" to config.app.packageId.takeLast(10).uppercase(),
            "description" to config.app.longDescription,
            "seller_fee_basis_points" to 0,
            "image" to config.app.iconUri,
            "external_url" to (config.publisher.website ?: ""),
            "attributes" to listOf(
                mapOf("trait_type" to "category", "value" to config.app.category),
                mapOf("trait_type" to "package_id", "value" to config.app.packageId),
                mapOf("trait_type" to "version", "value" to config.release.version),
                mapOf("trait_type" to "version_code", "value" to config.release.versionCode.toString())
            ),
            "properties" to mapOf(
                "files" to listOf(
                    mapOf(
                        "uri" to config.app.iconUri,
                        "type" to "image/png"
                    )
                ),
                "category" to "application",
                "creators" to listOf(
                    mapOf(
                        "address" to config.publisher.address,
                        "share" to 100
                    )
                )
            ),
            "android_details" to mapOf(
                "package_id" to config.app.packageId,
                "version_name" to config.release.version,
                "version_code" to config.release.versionCode,
                "min_sdk" to 24,
                "apk_uri" to "", // Will be filled after APK upload
                "permissions" to emptyList<String>()
            ),
            "release" to mapOf(
                "catalog" to mapOf(
                    "en-US" to mapOf(
                        "name" to config.app.name,
                        "short_description" to config.app.shortDescription,
                        "long_description" to config.app.longDescription,
                        "whats_new" to config.release.releaseNotes
                    )
                ),
                "media" to mapOf(
                    "icon" to config.app.iconUri,
                    "hero" to (config.app.heroUri ?: ""),
                    "screenshots" to config.app.screenshotUris
                )
            )
        )
        
        return gson.toJson(metadata)
    }
    
    /**
     * Generate config.yaml content for CLI compatibility
     */
    fun generateConfigYaml(config: DappStoreConfig): String {
        return """
# Bubble Wrapper Generated Config
# Generated for: ${config.app.name}
# Publisher: ${config.publisher.name}

publisher:
  name: "${config.publisher.name}"
  email: "${config.publisher.email}"
  website: "${config.publisher.website ?: ""}"
  address: "${config.publisher.address}"

app:
  name: "${config.app.name}"
  package_id: "${config.app.packageId}"
  category: "${config.app.category}"
  
  catalog:
    en-US:
      name: "${config.app.name}"
      short_description: "${config.app.shortDescription}"
      long_description: |
        ${config.app.longDescription.lines().joinToString("\n        ")}

release:
  version: "${config.release.version}"
  version_code: ${config.release.versionCode}
  release_notes: |
    ${config.release.releaseNotes.lines().joinToString("\n    ")}

solana:
  rpc_url: "${config.solanaConfig.rpcUrl}"
  cluster: "${config.solanaConfig.cluster}"
""".trimIndent()
    }
    
    /**
     * Get estimated transaction cost
     */
    suspend fun estimatePublishingCost(): Result<Double> {
        // dApp Store publishing typically costs:
        // - NFT minting: ~0.01 SOL
        // - Metadata upload: ~0.002 SOL per KB
        // - Transaction fees: ~0.000005 SOL per signature
        // Total estimate: ~0.05-0.2 SOL
        return Result.success(0.15) // Conservative estimate
    }
    
    /**
     * Validate config before publishing
     */
    fun validateConfig(config: DappStoreConfig): List<String> {
        val errors = mutableListOf<String>()
        
        if (config.app.name.isBlank()) {
            errors.add("App name is required")
        }
        if (config.app.name.length > 50) {
            errors.add("App name must be 50 characters or less")
        }
        if (config.app.shortDescription.isBlank()) {
            errors.add("Short description is required")
        }
        if (config.app.shortDescription.length > 80) {
            errors.add("Short description must be 80 characters or less")
        }
        if (config.app.longDescription.length < 100) {
            errors.add("Long description must be at least 100 characters")
        }
        if (config.app.packageId.isBlank()) {
            errors.add("Package ID is required")
        }
        if (!config.app.packageId.matches(Regex("^[a-z][a-z0-9_]*(\\.[a-z0-9_]+)+$"))) {
            errors.add("Invalid package ID format (e.g., com.example.app)")
        }
        if (config.app.category !in CATEGORIES) {
            errors.add("Invalid category")
        }
        if (config.publisher.email.isBlank()) {
            errors.add("Publisher email is required")
        }
        if (!config.publisher.email.contains("@")) {
            errors.add("Invalid email format")
        }
        if (config.publisher.address.isBlank()) {
            errors.add("Publisher wallet address is required")
        }
        if (config.release.version.isBlank()) {
            errors.add("Version is required")
        }
        if (config.release.versionCode < 1) {
            errors.add("Version code must be at least 1")
        }
        
        return errors
    }
    
    /**
     * Get CLI commands for publishing
     */
    fun getPublishCommands(config: DappStoreConfig, configPath: String): List<Pair<String, String>> {
        val commands = mutableListOf<Pair<String, String>>()
        
        commands.add(
            "Initialize project" to """
mkdir -p dapp-store-publish
cd dapp-store-publish
pnpm init -y
pnpm add -D @solana-mobile/dapp-store-cli
""".trim()
        )
        
        commands.add(
            "Validate config" to """
npx dapp-store validate -c $configPath
""".trim()
        )
        
        commands.add(
            "Create publisher (first time only)" to """
npx dapp-store create publisher \
  -k /path/to/keypair.json \
  -u ${config.solanaConfig.rpcUrl}
""".trim()
        )
        
        commands.add(
            "Create app" to """
npx dapp-store create app \
  -c $configPath \
  -k /path/to/keypair.json \
  -u ${config.solanaConfig.rpcUrl}
""".trim()
        )
        
        commands.add(
            "Create release" to """
npx dapp-store create release \
  -c $configPath \
  -k /path/to/keypair.json \
  -u ${config.solanaConfig.rpcUrl} \
  --requestor-is-authorized
""".trim()
        )
        
        commands.add(
            "Submit for review" to """
npx dapp-store publish submit \
  -c $configPath \
  -k /path/to/keypair.json \
  -u ${config.solanaConfig.rpcUrl}
""".trim()
        )
        
        return commands
    }
}
