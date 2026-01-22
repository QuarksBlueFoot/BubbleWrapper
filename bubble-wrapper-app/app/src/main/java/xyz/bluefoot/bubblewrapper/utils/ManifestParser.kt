package xyz.bluefoot.bubblewrapper.utils

import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.net.Uri
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.BufferedInputStream
import java.io.File
import java.io.FileOutputStream
import java.net.URL
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream

/**
 * Utility class for parsing PWA manifests and APK metadata
 * Used to auto-fill publishing configuration
 */
class ManifestParser(private val context: Context) {
    
    companion object {
        private const val TAG = "ManifestParser"
    }
    
    /**
     * Data extracted from a PWA manifest
     */
    data class ManifestData(
        val name: String = "",
        val shortName: String = "",
        val description: String = "",
        val startUrl: String = "",
        val display: String = "",
        val backgroundColor: String = "",
        val themeColor: String = "",
        val orientation: String = "",
        val icons: List<IconInfo> = emptyList(),
        val categories: List<String> = emptyList()
    )
    
    data class IconInfo(
        val src: String,
        val sizes: String,
        val type: String = "",
        val purpose: String = ""
    )
    
    /**
     * Data extracted from an APK file
     */
    data class ApkMetadata(
        val packageName: String = "",
        val versionName: String = "",
        val versionCode: Long = 1,
        val appName: String = "",
        val minSdk: Int = 24,
        val targetSdk: Int = 34,
        val iconPath: String? = null,
        val twaManifest: ManifestData? = null // Embedded TWA/PWA manifest if found
    )
    
    /**
     * Fetch and parse a PWA manifest from a URL
     */
    suspend fun fetchManifest(manifestUrl: String): Result<ManifestData> = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "Fetching manifest from: $manifestUrl")
            
            val url = URL(manifestUrl)
            val connection = url.openConnection()
            connection.connectTimeout = 10000
            connection.readTimeout = 10000
            connection.setRequestProperty("User-Agent", "BubbleWrapper/1.0")
            
            val jsonText = connection.getInputStream().bufferedReader().use { it.readText() }
            Log.d(TAG, "Manifest fetched, length: ${jsonText.length}")
            
            val manifest = parseManifestJson(jsonText, manifestUrl)
            Result.success(manifest)
            
        } catch (e: Exception) {
            Log.e(TAG, "Failed to fetch manifest", e)
            Result.failure(e)
        }
    }
    
    /**
     * Parse manifest JSON string
     */
    private fun parseManifestJson(jsonText: String, baseUrl: String): ManifestData {
        val json = JSONObject(jsonText)
        
        // Parse icons
        val icons = mutableListOf<IconInfo>()
        json.optJSONArray("icons")?.let { iconsArray ->
            for (i in 0 until iconsArray.length()) {
                val iconObj = iconsArray.getJSONObject(i)
                icons.add(
                    IconInfo(
                        src = resolveUrl(iconObj.optString("src", ""), baseUrl),
                        sizes = iconObj.optString("sizes", ""),
                        type = iconObj.optString("type", ""),
                        purpose = iconObj.optString("purpose", "")
                    )
                )
            }
        }
        
        // Parse categories
        val categories = mutableListOf<String>()
        json.optJSONArray("categories")?.let { catArray ->
            for (i in 0 until catArray.length()) {
                categories.add(catArray.getString(i))
            }
        }
        
        return ManifestData(
            name = json.optString("name", ""),
            shortName = json.optString("short_name", ""),
            description = json.optString("description", ""),
            startUrl = json.optString("start_url", "/"),
            display = json.optString("display", "standalone"),
            backgroundColor = json.optString("background_color", "#000000"),
            themeColor = json.optString("theme_color", "#000000"),
            orientation = json.optString("orientation", "any"),
            icons = icons,
            categories = categories
        )
    }
    
    /**
     * Resolve a relative URL to an absolute URL
     */
    private fun resolveUrl(relativeUrl: String, baseUrl: String): String {
        if (relativeUrl.startsWith("http://") || relativeUrl.startsWith("https://")) {
            return relativeUrl
        }
        
        return try {
            val base = URL(baseUrl)
            URL(base, relativeUrl).toString()
        } catch (e: Exception) {
            relativeUrl
        }
    }
    
    /**
     * Read APK metadata from a file URI
     * Also attempts to extract TWA/PWA manifest from assets if present
     */
    suspend fun readApkMetadata(apkUri: Uri): Result<ApkMetadata> = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "Reading APK metadata from: $apkUri")
            
            // Copy APK to temp file for PackageManager to read
            val tempFile = File(context.cacheDir, "temp_apk_${System.currentTimeMillis()}.apk")
            context.contentResolver.openInputStream(apkUri)?.use { input ->
                FileOutputStream(tempFile).use { output ->
                    input.copyTo(output)
                }
            }
            
            val packageManager = context.packageManager
            val packageInfo = packageManager.getPackageArchiveInfo(
                tempFile.absolutePath,
                PackageManager.GET_META_DATA
            )
            
            // Try to extract TWA manifest from APK assets
            val twaManifest = extractTwaManifestFromApk(tempFile)
            
            val metadata = if (packageInfo != null) {
                // Get app label
                packageInfo.applicationInfo?.let { appInfo ->
                    appInfo.sourceDir = tempFile.absolutePath
                    appInfo.publicSourceDir = tempFile.absolutePath
                }
                
                val appLabel = packageInfo.applicationInfo?.let {
                    try {
                        packageManager.getApplicationLabel(it).toString()
                    } catch (e: Exception) {
                        ""
                    }
                } ?: ""
                
                // Use TWA manifest data to supplement APK metadata
                val finalAppName = when {
                    appLabel.isNotEmpty() -> appLabel
                    twaManifest?.name?.isNotEmpty() == true -> twaManifest.name
                    twaManifest?.shortName?.isNotEmpty() == true -> twaManifest.shortName
                    else -> ""
                }
                
                ApkMetadata(
                    packageName = packageInfo.packageName ?: "",
                    versionName = packageInfo.versionName ?: "1.0.0",
                    versionCode = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
                        packageInfo.longVersionCode
                    } else {
                        @Suppress("DEPRECATION")
                        packageInfo.versionCode.toLong()
                    },
                    appName = finalAppName,
                    minSdk = packageInfo.applicationInfo?.minSdkVersion ?: 24,
                    targetSdk = packageInfo.applicationInfo?.targetSdkVersion ?: 34,
                    twaManifest = twaManifest
                )
            } else {
                throw Exception("Could not parse APK")
            }
            
            // Clean up temp file
            tempFile.delete()
            
            Log.d(TAG, "APK metadata: $metadata")
            if (twaManifest != null) {
                Log.d(TAG, "Found embedded TWA manifest with name: ${twaManifest.name}")
            }
            Result.success(metadata)
            
        } catch (e: Exception) {
            Log.e(TAG, "Failed to read APK metadata", e)
            Result.failure(e)
        }
    }
    
    /**
     * Extract TWA manifest from APK assets folder
     * Looks for twa-manifest.json, manifest.json, or manifest.webmanifest
     */
    private fun extractTwaManifestFromApk(apkFile: File): ManifestData? {
        val manifestPaths = listOf(
            "assets/twa-manifest.json",
            "assets/manifest.json",
            "assets/manifest.webmanifest",
            "res/raw/twa_manifest.json"
        )
        
        try {
            ZipInputStream(apkFile.inputStream()).use { zipStream ->
                var entry: ZipEntry? = zipStream.nextEntry
                while (entry != null) {
                    val entryName = entry.name.lowercase()
                    
                    // Check if this is a manifest file we're looking for
                    if (manifestPaths.any { entryName.equals(it, ignoreCase = true) } ||
                        entryName.contains("twa-manifest") ||
                        (entryName.startsWith("assets/") && entryName.endsWith("manifest.json"))) {
                        
                        Log.d(TAG, "Found potential manifest in APK: ${entry.name}")
                        
                        try {
                            val jsonText = zipStream.bufferedReader().use { it.readText() }
                            val manifest = parseManifestJson(jsonText, "")
                            
                            // Check if it looks like a valid manifest
                            if (manifest.name.isNotEmpty() || manifest.shortName.isNotEmpty()) {
                                Log.d(TAG, "Successfully parsed manifest: ${manifest.name}")
                                return manifest
                            }
                        } catch (e: Exception) {
                            Log.w(TAG, "Failed to parse manifest ${entry.name}: ${e.message}")
                        }
                    }
                    
                    zipStream.closeEntry()
                    entry = zipStream.nextEntry
                }
            }
        } catch (e: Exception) {
            Log.w(TAG, "Error scanning APK for manifest: ${e.message}")
        }
        
        return null
    }

    /**
     * Extract the largest icon from a manifest
     */
    fun getLargestIcon(manifest: ManifestData): IconInfo? {
        return manifest.icons
            .filter { it.sizes.isNotEmpty() }
            .maxByOrNull { 
                // Parse size like "512x512" or "192x192"
                val size = it.sizes.split("x").firstOrNull()?.toIntOrNull() ?: 0
                size
            }
    }
    
    /**
     * Map PWA category to Solana dApp Store category
     */
    fun mapToSolanaCategory(pwaCategories: List<String>): String {
        val categoryMap = mapOf(
            "finance" to "Finance",
            "games" to "Gaming",
            "entertainment" to "Entertainment",
            "productivity" to "Utility",
            "social" to "Social",
            "utilities" to "Utility",
            "music" to "Entertainment",
            "news" to "Info",
            "shopping" to "Marketplace",
            "education" to "Education",
            "health" to "Health & Fitness",
            "fitness" to "Health & Fitness",
            "nft" to "NFT",
            "defi" to "DeFi",
            "crypto" to "Finance",
            "blockchain" to "Finance",
            "wallet" to "Wallets"
        )
        
        for (cat in pwaCategories) {
            val mapped = categoryMap[cat.lowercase()]
            if (mapped != null) {
                return mapped
            }
        }
        
        return "Utility" // Default
    }
    
    /**
     * Download an icon from URL to local storage
     */
    suspend fun downloadIcon(iconUrl: String, fileName: String): Result<String> = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "Downloading icon from: $iconUrl")
            
            val url = URL(iconUrl)
            val connection = url.openConnection()
            connection.connectTimeout = 10000
            connection.readTimeout = 30000
            
            val iconDir = File(context.filesDir, "icons")
            iconDir.mkdirs()
            
            val iconFile = File(iconDir, fileName)
            
            BufferedInputStream(connection.getInputStream()).use { input ->
                FileOutputStream(iconFile).use { output ->
                    input.copyTo(output)
                }
            }
            
            Log.d(TAG, "Icon saved to: ${iconFile.absolutePath}")
            Result.success(iconFile.absolutePath)
            
        } catch (e: Exception) {
            Log.e(TAG, "Failed to download icon", e)
            Result.failure(e)
        }
    }
}
