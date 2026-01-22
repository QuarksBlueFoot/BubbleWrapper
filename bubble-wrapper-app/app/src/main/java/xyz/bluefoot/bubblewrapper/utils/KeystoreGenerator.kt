package xyz.bluefoot.bubblewrapper.utils

import android.content.Context
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.security.KeyPairGenerator
import java.security.KeyStore
import java.security.cert.X509Certificate
import java.util.Date
import java.math.BigInteger
import javax.security.auth.x500.X500Principal
import org.bouncycastle.x509.X509V3CertificateGenerator
import org.bouncycastle.jce.provider.BouncyCastleProvider
import java.security.Security
import java.security.MessageDigest

/**
 * On-device keystore generator for Android app signing
 * Creates PKCS12 keystores compatible with Android signing
 */
class KeystoreGenerator(private val context: Context) {
    
    companion object {
        private const val TAG = "KeystoreGenerator"
        private const val KEY_SIZE = 2048
        private const val ALGORITHM = "RSA"
        private const val SIGNATURE_ALGORITHM = "SHA256withRSA"
        
        init {
            // Add BouncyCastle provider for certificate generation
            if (Security.getProvider(BouncyCastleProvider.PROVIDER_NAME) == null) {
                Security.addProvider(BouncyCastleProvider())
            }
        }
    }
    
    /**
     * Configuration for keystore generation
     */
    data class KeystoreConfig(
        val keystoreName: String,       // Filename without extension
        val keystorePassword: String,
        val alias: String,              // Key alias
        val aliasPassword: String,      // Usually same as keystore password
        val validityYears: Int = 25,
        val commonName: String = "",    // CN - App/company name
        val organization: String = "",  // O - Organization
        val organizationalUnit: String = "", // OU - Department
        val locality: String = "",      // L - City
        val state: String = "",         // ST - State/Province
        val country: String = "US"      // C - Country code (2 letters)
    )
    
    /**
     * Result of keystore generation
     */
    data class KeystoreResult(
        val success: Boolean,
        val keystorePath: String? = null,
        val sha256Fingerprint: String? = null,
        val sha1Fingerprint: String? = null,
        val error: String? = null
    )
    
    /**
     * Information about a keystore
     */
    data class KeystoreInfo(
        val path: String,
        val alias: String,
        val sha256Fingerprint: String,
        val sha1Fingerprint: String,
        val validFrom: Date? = null,
        val validUntil: Date? = null,
        val subject: String = ""
    )
    
    /**
     * Generate a new keystore with self-signed certificate
     */
    suspend fun generateKeystore(config: KeystoreConfig): KeystoreResult = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "Generating keystore: ${config.keystoreName}")
            
            // Generate RSA key pair
            val keyPairGenerator = KeyPairGenerator.getInstance(ALGORITHM)
            keyPairGenerator.initialize(KEY_SIZE)
            val keyPair = keyPairGenerator.generateKeyPair()
            
            Log.d(TAG, "Key pair generated")
            
            // Create self-signed X.509 certificate
            val certificate = generateCertificate(keyPair, config)
            
            Log.d(TAG, "Certificate generated")
            
            // Create keystore
            val keyStore = KeyStore.getInstance("PKCS12")
            keyStore.load(null, config.keystorePassword.toCharArray())
            
            // Store key entry
            keyStore.setKeyEntry(
                config.alias,
                keyPair.private,
                config.aliasPassword.toCharArray(),
                arrayOf(certificate)
            )
            
            // Save to file
            val keystoreDir = File(context.filesDir, "keystores")
            keystoreDir.mkdirs()
            
            val fileName = "${config.keystoreName}.keystore"
            val keystoreFile = File(keystoreDir, fileName)
            FileOutputStream(keystoreFile).use { fos ->
                keyStore.store(fos, config.keystorePassword.toCharArray())
            }
            
            Log.d(TAG, "Keystore saved to: ${keystoreFile.absolutePath}")
            
            // Calculate fingerprints
            val sha256 = getCertificateFingerprint(certificate, "SHA-256")
            val sha1 = getCertificateFingerprint(certificate, "SHA-1")
            
            Log.d(TAG, "SHA-256: $sha256")
            Log.d(TAG, "SHA-1: $sha1")
            
            // Save metadata for later retrieval without password
            saveKeystoreMetadata(keystoreFile.absolutePath, config.alias, sha256, sha1)
            
            KeystoreResult(
                success = true,
                keystorePath = keystoreFile.absolutePath,
                sha256Fingerprint = sha256,
                sha1Fingerprint = sha1
            )
            
        } catch (e: Exception) {
            Log.e(TAG, "Keystore generation failed", e)
            KeystoreResult(
                success = false,
                error = e.message ?: "Unknown error"
            )
        }
    }
    
    /**
     * Generate self-signed X.509 certificate
     */
    @Suppress("DEPRECATION")
    private fun generateCertificate(
        keyPair: java.security.KeyPair,
        config: KeystoreConfig
    ): X509Certificate {
        val now = System.currentTimeMillis()
        val startDate = Date(now)
        val endDate = Date(now + config.validityYears * 365L * 24L * 60L * 60L * 1000L)
        
        // Build DN string, only including non-empty fields
        val dnParts = mutableListOf<String>()
        if (config.commonName.isNotBlank()) dnParts.add("CN=${config.commonName}")
        if (config.organizationalUnit.isNotBlank()) dnParts.add("OU=${config.organizationalUnit}")
        if (config.organization.isNotBlank()) dnParts.add("O=${config.organization}")
        if (config.locality.isNotBlank()) dnParts.add("L=${config.locality}")
        if (config.state.isNotBlank()) dnParts.add("ST=${config.state}")
        dnParts.add("C=${config.country.ifBlank { "US" }}")
        
        // If CN is empty, use the keystore name
        if (config.commonName.isBlank()) {
            dnParts.add(0, "CN=${config.keystoreName}")
        }
        
        val dnName = X500Principal(dnParts.joinToString(", "))
        
        // Use BouncyCastle X509V3CertificateGenerator
        val certGen = X509V3CertificateGenerator()
        certGen.setSerialNumber(BigInteger.valueOf(now))
        certGen.setIssuerDN(dnName)
        certGen.setNotBefore(startDate)
        certGen.setNotAfter(endDate)
        certGen.setSubjectDN(dnName)
        certGen.setPublicKey(keyPair.public)
        certGen.setSignatureAlgorithm(SIGNATURE_ALGORITHM)
        
        return certGen.generate(keyPair.private, "BC")
    }
    
    /**
     * Get certificate fingerprint
     */
    private fun getCertificateFingerprint(cert: X509Certificate, algorithm: String): String {
        val md = MessageDigest.getInstance(algorithm)
        val digest = md.digest(cert.encoded)
        return digest.joinToString(":") { "%02X".format(it) }
    }
    
    /**
     * List all generated keystores
     */
    fun listKeystores(): List<File> {
        val keystoreDir = File(context.filesDir, "keystores")
        return keystoreDir.listFiles()?.filter { 
            it.extension == "keystore" || it.extension == "jks" || it.extension == "p12"
        } ?: emptyList()
    }
    
    /**
     * Get keystore info without requiring password (reads metadata file if available)
     * For full info including fingerprints, use getKeystoreInfoWithPassword
     */
    fun getKeystoreInfo(fileName: String): KeystoreInfo? {
        val keystoreDir = File(context.filesDir, "keystores")
        val keystoreFile = File(keystoreDir, fileName)
        val metadataFile = File("${keystoreFile.absolutePath}.meta")
        
        if (!keystoreFile.exists()) return null
        
        // Try to read cached metadata
        if (metadataFile.exists()) {
            try {
                val lines = metadataFile.readLines()
                return KeystoreInfo(
                    path = keystoreFile.absolutePath,
                    alias = lines.getOrNull(0) ?: "android",
                    sha256Fingerprint = lines.getOrNull(1) ?: "",
                    sha1Fingerprint = lines.getOrNull(2) ?: ""
                )
            } catch (e: Exception) {
                Log.e(TAG, "Failed to read metadata", e)
            }
        }
        
        // Return basic info without fingerprints
        return KeystoreInfo(
            path = keystoreFile.absolutePath,
            alias = "android",
            sha256Fingerprint = "Fingerprint unavailable",
            sha1Fingerprint = "Fingerprint unavailable"
        )
    }
    
    /**
     * Get keystore info with password (can read fingerprints)
     */
    suspend fun getKeystoreInfoWithPassword(
        keystorePath: String,
        password: String,
        alias: String
    ): KeystoreInfo? = withContext(Dispatchers.IO) {
        try {
            val keyStore = KeyStore.getInstance("PKCS12")
            File(keystorePath).inputStream().use { fis ->
                keyStore.load(fis, password.toCharArray())
            }
            
            val cert = keyStore.getCertificate(alias) as? X509Certificate
            cert?.let {
                KeystoreInfo(
                    path = keystorePath,
                    alias = alias,
                    sha256Fingerprint = getCertificateFingerprint(it, "SHA-256"),
                    sha1Fingerprint = getCertificateFingerprint(it, "SHA-1"),
                    validFrom = it.notBefore,
                    validUntil = it.notAfter,
                    subject = it.subjectDN.name
                )
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to read keystore info", e)
            null
        }
    }
    
    /**
     * Save keystore metadata (fingerprints) for later retrieval without password
     */
    private fun saveKeystoreMetadata(keystorePath: String, alias: String, sha256: String, sha1: String) {
        try {
            val metadataFile = File("$keystorePath.meta")
            metadataFile.writeText("$alias\n$sha256\n$sha1")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to save metadata", e)
        }
    }
    
    /**
     * Delete a keystore
     */
    fun deleteKeystore(fileName: String): Boolean {
        val keystoreDir = File(context.filesDir, "keystores")
        val file = File(keystoreDir, fileName)
        val metaFile = File("${file.absolutePath}.meta")
        metaFile.delete()
        return file.delete()
    }
    
    /**
     * Export keystore to external storage (for backup)
     */
    suspend fun exportKeystore(
        keystorePath: String,
        destinationUri: android.net.Uri
    ): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val sourceFile = File(keystorePath)
            context.contentResolver.openOutputStream(destinationUri)?.use { output ->
                sourceFile.inputStream().use { input ->
                    input.copyTo(output)
                }
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
