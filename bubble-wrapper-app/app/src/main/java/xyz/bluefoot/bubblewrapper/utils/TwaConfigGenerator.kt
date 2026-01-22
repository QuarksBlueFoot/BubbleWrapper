package xyz.bluefoot.bubblewrapper.utils

import android.content.Context
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File

/**
 * TWA (Trusted Web Activity) configuration generator
 * Creates all necessary config files for building a TWA
 */
class TwaConfigGenerator(private val context: Context) {
    
    companion object {
        private const val TAG = "TwaConfigGenerator"
    }
    
    private val json = Json { 
        prettyPrint = true
        encodeDefaults = true
    }
    
    @Serializable
    data class TwaManifest(
        val packageId: String,
        val host: String,
        val name: String,
        val launcherName: String,
        val display: String = "standalone",
        val themeColor: String = "#000000",
        val themeColorDark: String = "#000000",
        val navigationColor: String = "#000000",
        val navigationColorDark: String = "#000000",
        val navigationDividerColor: String = "#000000",
        val navigationDividerColorDark: String = "#000000",
        val backgroundColor: String = "#000000",
        val enableNotifications: Boolean = true,
        val startUrl: String = "/",
        val iconUrl: String = "",
        val maskableIconUrl: String = "",
        val splashScreenFadeOutDuration: Int = 300,
        val appVersionName: String = "1.0.0",
        val appVersionCode: Int = 1,
        val fallbackType: String = "customtabs",
        val enableSiteSettingsShortcut: Boolean = true,
        val orientation: String = "portrait",
        val minSdkVersion: Int = 24
    )
    
    @Serializable
    data class AssetLinksEntry(
        val relation: List<String> = listOf("delegate_permission/common.handle_all_urls"),
        val target: AssetLinksTarget
    )
    
    @Serializable
    data class AssetLinksTarget(
        val namespace: String = "android_app",
        val package_name: String,
        val sha256_cert_fingerprints: List<String>
    )
    
    data class TwaConfig(
        val packageId: String,
        val host: String,
        val appName: String,
        val launcherName: String,
        val themeColor: String = "#000000",
        val backgroundColor: String = "#000000",
        val iconUrl: String = "",
        val versionName: String = "1.0.0",
        val versionCode: Int = 1,
        val sha256Fingerprint: String = "",
        val orientation: String = "portrait"
    )
    
    data class GenerationResult(
        val success: Boolean,
        val twaManifestPath: String? = null,
        val assetLinksPath: String? = null,
        val buildGradlePath: String? = null,
        val projectDir: String? = null,
        val error: String? = null
    )
    
    /**
     * Generate all TWA configuration files
     */
    suspend fun generateTwaConfig(
        config: TwaConfig
    ): GenerationResult = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "Generating TWA config for: ${config.packageId}")
            
            // Create project directory
            val projectDir = File(context.filesDir, "twa-projects/${config.packageId}")
            projectDir.mkdirs()
            
            // 1. Generate twa-manifest.json
            val twaManifest = TwaManifest(
                packageId = config.packageId,
                host = config.host,
                name = config.appName,
                launcherName = config.launcherName,
                themeColor = config.themeColor,
                themeColorDark = config.themeColor,
                navigationColor = config.themeColor,
                navigationColorDark = config.themeColor,
                navigationDividerColor = config.themeColor,
                navigationDividerColorDark = config.themeColor,
                backgroundColor = config.backgroundColor,
                iconUrl = config.iconUrl,
                maskableIconUrl = config.iconUrl,
                appVersionName = config.versionName,
                appVersionCode = config.versionCode,
                orientation = config.orientation
            )
            
            val twaManifestFile = File(projectDir, "twa-manifest.json")
            twaManifestFile.writeText(json.encodeToString(twaManifest))
            
            // 2. Generate assetlinks.json
            val assetLinks = listOf(
                AssetLinksEntry(
                    target = AssetLinksTarget(
                        package_name = config.packageId,
                        sha256_cert_fingerprints = listOf(config.sha256Fingerprint)
                    )
                )
            )
            
            val assetLinksFile = File(projectDir, "assetlinks.json")
            assetLinksFile.writeText(json.encodeToString(assetLinks))
            
            // 3. Generate build.gradle template
            val buildGradleContent = generateBuildGradle(config)
            val buildGradleFile = File(projectDir, "build.gradle")
            buildGradleFile.writeText(buildGradleContent)
            
            // 4. Generate settings.gradle
            val settingsGradle = """
                rootProject.name = '${config.launcherName.replace(" ", "")}'
                include ':app'
            """.trimIndent()
            File(projectDir, "settings.gradle").writeText(settingsGradle)
            
            // 5. Generate gradle.properties
            val gradleProperties = """
                android.useAndroidX=true
                org.gradle.jvmargs=-Xmx2048m
            """.trimIndent()
            File(projectDir, "gradle.properties").writeText(gradleProperties)
            
            Log.d(TAG, "TWA config generated at: ${projectDir.absolutePath}")
            
            GenerationResult(
                success = true,
                twaManifestPath = twaManifestFile.absolutePath,
                assetLinksPath = assetLinksFile.absolutePath,
                buildGradlePath = buildGradleFile.absolutePath,
                projectDir = projectDir.absolutePath
            )
            
        } catch (e: Exception) {
            Log.e(TAG, "TWA config generation failed", e)
            GenerationResult(
                success = false,
                error = e.message ?: "Unknown error"
            )
        }
    }
    
    /**
     * Generate build.gradle content
     */
    private fun generateBuildGradle(config: TwaConfig): String {
        return """
/*
 * Auto-generated by BubbleWrapper
 * TWA Build Configuration for ${config.appName}
 */

plugins {
    id 'com.android.application'
}

def twaManifest = [
    applicationId: '${config.packageId}',
    hostName: '${config.host}',
    launchUrl: '/',
    name: '${config.appName}',
    launcherName: '${config.launcherName}',
    themeColor: '${config.themeColor}',
    themeColorDark: '${config.themeColor}',
    navigationColor: '${config.themeColor}',
    navigationColorDark: '${config.themeColor}',
    navigationDividerColor: '${config.themeColor}',
    navigationDividerColorDark: '${config.themeColor}',
    backgroundColor: '${config.backgroundColor}',
    enableNotifications: true,
    splashScreenFadeOutDuration: 300,
    generatorApp: 'bubblewrapper-android',
    fallbackType: 'customtabs',
    enableSiteSettingsShortcut: 'true',
    orientation: '${config.orientation}',
]

android {
    compileSdkVersion 34
    namespace "${config.packageId}"
    
    defaultConfig {
        applicationId "${config.packageId}"
        minSdkVersion 24
        targetSdkVersion 34
        versionCode ${config.versionCode}
        versionName "${config.versionName}"

        resValue "string", "appName", twaManifest.name
        resValue "string", "launcherName", twaManifest.launcherName
        
        def launchUrl = "https://" + twaManifest.hostName + twaManifest.launchUrl
        resValue "string", "launchUrl", launchUrl
        resValue "string", "hostName", twaManifest.hostName
        
        resValue "color", "colorPrimary", twaManifest.themeColor
        resValue "color", "colorPrimaryDark", twaManifest.themeColorDark
        resValue "color", "navigationColor", twaManifest.navigationColor
        resValue "color", "navigationColorDark", twaManifest.navigationColorDark
        resValue "color", "navigationDividerColor", twaManifest.navigationDividerColor
        resValue "color", "navigationDividerColorDark", twaManifest.navigationDividerColorDark
        resValue "color", "backgroundColor", twaManifest.backgroundColor
        resValue "string", "providerAuthority", twaManifest.applicationId + '.fileprovider'
        resValue "bool", "enableNotification", twaManifest.enableNotifications.toString()
        resValue "integer", "splashScreenFadeOutDuration", twaManifest.splashScreenFadeOutDuration.toString()
        resValue "string", "generatorApp", twaManifest.generatorApp
        resValue "string", "fallbackType", twaManifest.fallbackType
        resValue "bool", "enableSiteSettingsShortcut", twaManifest.enableSiteSettingsShortcut
        resValue "string", "orientation", twaManifest.orientation
    }
    
    buildTypes {
        release {
            minifyEnabled true
        }
    }
    
    compileOptions {
        sourceCompatibility JavaVersion.VERSION_1_8
        targetCompatibility JavaVersion.VERSION_1_8
    }
}

dependencies {
    implementation 'com.google.androidbrowserhelper:androidbrowserhelper:2.6.2'
}
        """.trimIndent()
    }
    
    /**
     * List all generated TWA projects
     */
    fun listProjects(): List<File> {
        val projectsDir = File(context.filesDir, "twa-projects")
        return projectsDir.listFiles()?.filter { it.isDirectory } ?: emptyList()
    }
    
    /**
     * Delete a TWA project
     */
    fun deleteProject(packageId: String): Boolean {
        val projectDir = File(context.filesDir, "twa-projects/$packageId")
        return projectDir.deleteRecursively()
    }
    
    /**
     * Get assetlinks.json content for website deployment
     */
    fun getAssetLinksContent(packageId: String, sha256Fingerprint: String): String {
        val assetLinks = listOf(
            AssetLinksEntry(
                target = AssetLinksTarget(
                    package_name = packageId,
                    sha256_cert_fingerprints = listOf(sha256Fingerprint)
                )
            )
        )
        return json.encodeToString(assetLinks)
    }
}
