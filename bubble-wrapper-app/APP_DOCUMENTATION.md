# BubbleWrapper Android App - NFT Publishing Documentation

## 📱 Android App Architecture

### Core Components

#### 1. DappStorePublisher
**Location:** `/app/src/main/java/xyz/bluefoot/bubblewrapper/network/DappStorePublisher.kt`

**Purpose:** Handles complete NFT publishing flow

**Features:**
- Asset upload management (icon, banner, screenshots)
- Metaplex metadata JSON generation (spec v0.4.0)
- Solana transaction building
- Mobile Wallet Adapter integration
- Transaction confirmation with retry

**Key Methods:**
```kotlin
suspend fun publishApp(
    config: PublishConfig,
    onProgress: (UploadProgress) -> Unit
): PublishResult

private suspend fun buildDappStoreTransaction(
    config: PublishConfig,
    metadataUri: String
): ByteArray

private suspend fun createAndSignDappStoreTransactions(
    config: PublishConfig,
    metadataUri: String
): TransactionResult
```

#### 2. WalletManager
**Location:** `/app/src/main/java/xyz/bluefoot/bubblewrapper/wallet/WalletManager.kt`

**Purpose:** Mobile Wallet Adapter integration

**Features:**
- Wallet connection/disconnection
- Balance checking
- Transaction signing via MWA
- Message signing
- Session persistence

**Key Methods:**
```kotlin
suspend fun connect(activityResultSender: ActivityResultSender): Result<String>
suspend fun signAndSendTransaction(
    sender: ActivityResultSender,
    transaction: ByteArray,
    cluster: String
): Result<String>
```

#### 3. SolanaRepository
**Location:** `/app/src/main/java/xyz/bluefoot/bubblewrapper/network/SolanaRepository.kt`

**Purpose:** Solana RPC interactions

**Features:**
- Balance queries
- Recent blockhash fetching
- Transaction submission
- Transaction confirmation
- Token account queries

**Key Methods:**
```kotlin
suspend fun getBalance(publicKey: String): Result<Double>
suspend fun getRecentBlockhash(): Result<String>
suspend fun confirmTransaction(signature: String, commitment: String): Result<TransactionStatus>
```

---

## 🏗️ Transaction Building

### Instruction Flow

```
1. CreateAccount (System Program)
   ├─ Allocates 82 bytes for mint
   ├─ Funds with 1,461,600 lamports
   └─ Owner: Token Program

2. InitializeMint (Token Program)
   ├─ Decimals: 0 (NFT)
   ├─ Mint Authority: User wallet
   └─ Freeze Authority: User wallet

3. CreateMetadataAccountV3 (Metaplex)
   ├─ Derives metadata PDA
   ├─ Name, Symbol, URI
   ├─ Creator shares
   └─ Seller fee basis points

4. CreateMasterEditionV3 (Metaplex)
   ├─ Derives edition PDA
   ├─ MaxSupply: 0
   └─ Creates NFT edition
```

### Account Metas

Each instruction specifies:
- **Public Key** - 32-byte account address
- **isSigner** - Whether account must sign
- **isWritable** - Whether account is modified

### PDA Derivation

```kotlin
// Metadata PDA
seeds = ["metadata", METADATA_PROGRAM_ID, mint_pubkey]
metadata_pda = findProgramAddress(seeds, METADATA_PROGRAM_ID)

// Master Edition PDA  
seeds = ["metadata", METADATA_PROGRAM_ID, mint_pubkey, "edition"]
edition_pda = findProgramAddress(seeds, METADATA_PROGRAM_ID)
```

---

## 🔐 Security Architecture

### Key Management

1. **No Private Keys Stored**
   - All keys managed by user's wallet
   - App never sees private keys
   - Signing happens in wallet app

2. **Transaction Approval**
   - User reviews every transaction
   - Shows network (devnet/mainnet)
   - Displays instruction details
   - Requires explicit approval

3. **Session Management**
   - Auth tokens stored securely
   - Automatic reauthorization
   - Session expiry handling

### Network Security

1. **HTTPS Only**
   - All RPC calls over HTTPS
   - Certificate validation
   - No insecure connections

2. **Input Validation**
   - Public key format validation
   - Transaction size limits
   - Metadata field sanitization

---

## 📊 Metadata Structure

### Spec v0.4.0 Compliance

```json
{
  "schema_version": "v0.4.0",
  "name": "App Name (32 char limit)",
  "description": "App description",
  "image": "ar://icon-uri",
  "external_url": "https://publisher-website.com",
  "properties": {
    "category": "dApp",
    "creators": [{
      "address": "PublisherPublicKey",
      "share": 100
    }]
  },
  "extensions": {
    "solana_dapp_store": {
      "publisher_details": {
        "name": "Publisher Name",
        "website": "https://site.com",
        "contact": "email@domain.com"
      },
      "release_details": {
        "updated_on": "2026-01-23T...",
        "license_url": "https://...",
        "privacy_policy_url": "https://...",
        "localized_resources": {
          "long_description": "uid_1",
          "new_in_version": "uid_2",
          "name": "uid_4",
          "short_description": "uid_5"
        }
      },
      "media": [
        {
          "mime": "image/png",
          "purpose": "icon",
          "uri": "ar://...",
          "width": 512,
          "height": 512
        }
      ],
      "android_details": {
        "android_package": "com.example.app",
        "version": "1.0.0",
        "version_code": 1,
        "min_sdk": 24,
        "permissions": ["android.permission.INTERNET"]
      }
    }
  },
  "i18n": {
    "en": {
      "1": "Full description",
      "2": "What's new",
      "4": "App name",
      "5": "Short description"
    }
  }
}
```

---

## 🚀 Usage Guide

### Basic Publishing Flow

```kotlin
// 1. Connect wallet
val walletManager = WalletManager.getInstance(context)
walletManager.connect(activityResultSender)

// 2. Configure publishing
val config = PublishConfig(
    appName = "My dApp",
    shortDescription = "A Solana dApp",
    fullDescription = "Full description here...",
    packageId = "com.mydomain.app",
    category = "defi",
    walletAddress = walletManager.getPublicKey()!!,
    rpcUrl = "https://api.mainnet-beta.solana.com",
    iconPath = iconFile.absolutePath,
    bannerPath = bannerFile.absolutePath,
    screenshots = listOf(screenshot1.absolutePath),
    publisherWebsite = "https://mydomain.com",
    publisherEmail = "contact@mydomain.com",
    versionName = "1.0.0",
    versionCode = 1,
    whatsNew = "Initial release"
)

// 3. Publish
val publisher = DappStorePublisher(walletManager, solanaRepository)
val result = publisher.publishApp(config) { progress ->
    when (progress.step) {
        "Validating configuration" -> // Update UI
        "Uploading app icon" -> // Update UI
        "Uploading banner" -> // Update UI
        "Uploading screenshots" -> // Update UI
        "Creating metadata" -> // Update UI
        "Submitting on-chain" -> // Update UI (wallet opens here)
    }
}

// 4. Handle result
when {
    result.success -> {
        println("NFT created!")
        println("Signature: ${result.transactionSignature}")
        println("Explorer: https://explorer.solana.com/tx/${result.transactionSignature}")
    }
    else -> {
        println("Error: ${result.error}")
    }
}
```

### Network Configuration

#### Devnet (Testing)
```kotlin
val config = PublishConfig(
    // ...
    rpcUrl = "https://api.devnet.solana.com",
    // ...
)
```

#### Mainnet (Production)
```kotlin
val config = PublishConfig(
    // ...
    rpcUrl = "https://api.mainnet-beta.solana.com",
    // or use premium RPC
    // rpcUrl = "https://mainnet.helius-rpc.com/?api-key=YOUR_KEY",
    // ...
)
```

---

## 🔧 Configuration

### Build Configuration

**File:** `app/build.gradle.kts`

```kotlin
android {
    compileSdk = 34
    defaultConfig {
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0.0"
    }
}

dependencies {
    // Mobile Wallet Adapter
    implementation("com.solanamobile:mobile-wallet-adapter-clientlib-ktx:2.0.3")
    
    // Crypto
    implementation("org.bouncycastle:bcprov-jdk15on:1.70")
    implementation("org.bitcoinj:bitcoinj-core:0.16.2")
    
    // Networking
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.google.code.gson:gson:2.10.1")
}
```

### ProGuard Rules

**File:** `app/proguard-rules.pro`

```proguard
# Kotlin Serialization
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.**
-keep,includedescriptorclasses class com.yourcompany.yourapp.**$$serializer { *; }

# Mobile Wallet Adapter
-keep class com.solana.mobilewalletadapter.** { *; }
-keep interface com.solana.mobilewalletadapter.** { *; }

# BouncyCastle
-keep class org.bouncycastle.** { *; }
-dontwarn org.bouncycastle.**

# Solana
-keep class org.bitcoinj.** { *; }
-dontwarn org.bitcoinj.**
```

---

## 📈 Performance Considerations

### Transaction Size
- **Average:** ~1.2 KB
- **Maximum:** 1.2 KB (Solana limit)
- **Instructions:** 4 per NFT

### Timing
- **Transaction Build:** <100ms
- **Wallet Signing:** User-dependent
- **Network Send:** ~500ms
- **Confirmation:** 400ms-30s
- **Total:** ~5-45 seconds typical

### Cost (Mainnet)
- **Rent:** 0.0014 SOL (~$0.14)
- **Fee:** ~0.000005 SOL (~$0.0005)
- **Total:** ~0.0014 SOL (~$0.14)

---

## 🐛 Error Handling

### Common Errors

```kotlin
try {
    val result = publisher.publishApp(config) { progress -> }
} catch (e: Exception) {
    when {
        e.message?.contains("Wallet not connected") == true ->
            // Prompt user to connect wallet
        
        e.message?.contains("Insufficient balance") == true ->
            // Show balance and required amount
        
        e.message?.contains("Transaction failed") == true ->
            // Show retry option
        
        e.message?.contains("Network error") == true ->
            // Check connectivity, retry
        
        else ->
            // Generic error handling
    }
}
```

### Retry Logic

Built-in retry for:
- Transaction confirmation (30 attempts, 1s interval)
- RPC calls (3 attempts with exponential backoff)
- Asset uploads (TBD when Arweave integrated)

---

## 📚 Additional Resources

- **Source Code:** `/app/src/main/java/xyz/bluefoot/bubblewrapper/`
- **Tests:** `/test_devnet_nft.py`
- **Devnet Guide:** `/DEVNET_TESTING.md`
- **Production Guide:** `/PRODUCTION_READY.md`

---

**Last Updated:** January 23, 2026  
**App Version:** 1.0.0  
**SDK Version:** Android 24+ (Android 7.0+)
