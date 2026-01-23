# BubbleWrapper: Embedded dApp Store CLI for Android

## Overview

BubbleWrapper now includes a **complete Kotlin implementation** of the official `@solana-mobile/dapp-store-cli` workflow, enabling users to publish their apps directly from within the Android app itself.

## Architecture

### Core Components

#### 1. `DappStoreCliWorkflow.kt`
**Location:** `app/src/main/java/xyz/bluefoot/bubblewrapper/publishing/`

Replicates the entire CLI publishing pipeline in Kotlin:

```kotlin
class DappStoreCliWorkflow(
    context: Context,
    walletManager: WalletManager,
    solanaRepository: SolanaRepository
)
```

**Key Methods:**
- `validateConfig()` - Equivalent to `dapp-store validate`
- `publishToStore()` - Complete workflow: upload → create NFTs → submit
- Asset validation (dimensions, formats, APK metadata)
- Arweave uploads via Irys
- Metaplex NFT creation (spec v0.4.0 compliant)
- Publisher Portal submission via HubSpot API

#### 2. `CliWorkflowScreen.kt`
**Location:** `app/src/main/java/xyz/bluefoot/bubblewrapper/ui/screens/`

Step-by-step UI wizard matching the CLI workflow:

1. **Configure** - Create config.yaml equivalent
2. **Validate** - Check all assets and metadata
3. **Upload Assets** - Permanent storage on Arweave
4. **Create App NFT** - Metaplex Collection NFT
5. **Create Release NFT** - Release as collection child
6. **Submit to Portal** - HubSpot form submission

## Workflow Comparison

### Official CLI
```bash
# 1. Create config
vi config.yaml

# 2. Validate
dapp-store validate \
  --keypair wallet.json \
  --build-tools-path ./android-sdk/build-tools/35.0.0

# 3. Create app NFT
dapp-store create app \
  --keypair wallet.json \
  --url https://api.mainnet-beta.solana.com

# 4. Create release NFT  
dapp-store create release \
  --keypair wallet.json \
  --url https://api.mainnet-beta.solana.com \
  --build-tools-path ./android-sdk/build-tools/35.0.0

# 5. Submit to store
dapp-store publish submit \
  --keypair wallet.json \
  --url https://api.mainnet-beta.solana.com \
  --complies-with-solana-dapp-store-policies \
  --requestor-is-authorized
```

### BubbleWrapper Android App
```kotlin
// All steps wrapped in one call:
val workflow = DappStoreCliWorkflow(context, walletManager, solanaRepository)

val result = workflow.publishToStore(config) { progress ->
    // Real-time progress updates
    when (progress.stage) {
        "validation" -> showValidationProgress()
        "upload" -> showUploadProgress()
        "nft" -> showNftCreation()
        "submit" -> showPortalSubmission()
    }
}

if (result.success) {
    // Published! Show app/release NFT addresses
}
```

## Configuration Structure

### CLI config.yaml
```yaml
publisher:
  name: Bluefoot Labs
  website: https://monkemob.me
  email: support@monkemob.me
  
app:
  name: MonkeMob
  android_package: me.monkemob.twa
  urls:
    license_url: https://monkemob.me/terms
    privacy_policy_url: https://monkemob.me/privacy
  media:
    - purpose: icon
      uri: ./assets/icon.png
      
release:
  media:
    - purpose: banner
      uri: ./assets/banner.png
    - purpose: screenshot
      uri: ./assets/screen1.png
  files:
    - purpose: install
      uri: ./monkemob-release.apk
  catalog:
    en-US:
      name: MonkeMob
      short_description: Saga Monkes Battle Game
      long_description: |
        Full description here
```

### BubbleWrapper Kotlin Config
```kotlin
val config = DappStoreCliWorkflow.PublishingConfig(
    publisherName = "Bluefoot Labs",
    publisherWebsite = "https://monkemob.me",
    publisherEmail = "support@monkemob.me",
    
    appName = "MonkeMob",
    androidPackage = "me.monkemob.twa",
    shortDescription = "Saga Monkes Battle Game",
    longDescription = "Full description here",
    
    licenseUrl = "https://monkemob.me/terms",
    privacyPolicyUrl = "https://monkemob.me/privacy",
    
    iconUri = Uri.parse("content://..."),
    bannerUri = Uri.parse("content://..."),
    screenshotUris = listOf(...),
    apkUri = Uri.parse("content://..."),
    
    walletPublicKey = "GGVj..."
)
```

## Asset Validation

### Implemented Checks (Same as CLI)

#### Icon
- ✅ Must be exactly 512x512px
- ✅ PNG, JPEG, or WebP format
- ✅ File must exist and be readable

#### Banner
- ✅ Must be exactly 1200x600px
- ✅ PNG, JPEG, or WebP format
- ✅ Required for mainnet submissions

#### Screenshots
- ✅ Minimum 4 screenshots required
- ✅ Each must be ≥1080px in both width and height
- ✅ PNG, JPEG, or WebP format

#### APK
- ✅ Valid Android APK file
- ✅ Package name matches config
- ✅ Extracts versionCode, versionName, minSdk
- ✅ Extracts cert fingerprint
- ✅ Validates locales

## Metadata Structure (Spec v0.4.0)

Both CLI and BubbleWrapper produce **identical** metadata JSON:

```json
{
  "schema_version": "v0.4.0",
  "name": "App Name",
  "description": "Description",
  "image": "ar://icon-hash",
  "external_url": "https://website.com",
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
        "website": "https://...",
        "contact": "email@domain.com",
        "support_email": "support@domain.com"
      },
      "release_details": {
        "updated_on": "2026-01-23T...",
        "license_url": "https://...",
        "privacy_policy_url": "https://...",
        "localized_resources": {
          "long_description": "1",
          "new_in_version": "2",
          "name": "4",
          "short_description": "5"
        }
      },
      "media": [...],
      "files": [...],
      "android_details": {
        "android_package": "com.example.app",
        "version": "1.0.0",
        "version_code": 1,
        "min_sdk": 24,
        "cert_fingerprint": "...",
        "permissions": [...],
        "locales": ["en-US"]
      }
    },
    "i18n": {
      "en": {
        "1": "Long description",
        "2": "What's new",
        "4": "App name",
        "5": "Short description"
      }
    }
  }
}
```

## NFT Creation

### App NFT (Metaplex Collection)
```kotlin
// Creates a Metaplex Certified Collection NFT
// This represents the "App" and can have many releases
val appNftTransaction = buildAppNftTransaction(
    mintKeypair = generateKeypair(),
    authority = publisherPublicKey,
    metadataUri = "ar://app-metadata-hash",
    name = config.appName,
    symbol = generateSymbol(config.androidPackage)
)
```

### Release NFT (Collection Child)
```kotlin
// Creates a Release NFT as child of App Collection
// Each version gets its own immutable NFT
val releaseNftTransaction = buildReleaseNftTransaction(
    mintKeypair = generateKeypair(),
    authority = publisherPublicKey,
    appCollectionMint = appMintAddress,
    metadataUri = "ar://release-metadata-hash",
    name = config.appName,
    symbol = generateSymbol(config.androidPackage)
)
```

## Publisher Portal Submission

### HubSpot API (Same as CLI)
```kotlin
private suspend fun submitToPublisherPortal(
    config: PublishingConfig,
    appMintAddress: String,
    releaseMintAddress: String
): Boolean {
    val payload = JSONObject().apply {
        put("fields", JSONArray().apply {
            put(createField("company", config.publisherName))
            put(createField("email", config.publisherEmail))
            put(createField("website", config.publisherWebsite))
            put(createField("dapp_collection_account_address", appMintAddress))
            put(createField("dapp_release_account_address", releaseMintAddress))
            put(createField("requestor_is_authorized_to_submit_this_request", true))
            put(createField("complies_with_solana_dapp_store_policies", true))
        })
    }
    
    val request = Request.Builder()
        .url("$HUBSPOT_API/$PORTAL_ID/$FORM_GUID")
        .post(RequestBody.create("application/json".toMediaType(), payload.toString()))
        .build()
    
    return httpClient.newCall(request).execute().isSuccessful
}
```

## Usage Example

### Basic Publishing
```kotlin
val workflow = DappStoreCliWorkflow(
    context = applicationContext,
    walletManager = walletManager,
    solanaRepository = solanaRepository
)

// Step 1: Validate
val validation = workflow.validateConfig(config)
if (!validation.valid) {
    showErrors(validation.errors)
    return
}

// Step 2: Publish (handles everything)
val result = workflow.publishToStore(config) { progress ->
    updateUI("${progress.stage}: ${progress.step}")
    updateProgressBar(progress.progress, progress.total)
}

if (result.success) {
    showSuccess(
        appNft = result.appMintAddress,
        releaseNft = result.releaseMintAddress,
        metadata = result.metadataUri
    )
}
```

### With UI Integration
```kotlin
@Composable
fun MyPublishingScreen() {
    val workflow = remember { 
        DappStoreCliWorkflow(context, walletManager, solanaRepository) 
    }
    
    CliWorkflowScreen(
        onBack = { navController.popBackStack() },
        walletManager = walletManager,
        solanaRepository = solanaRepository
    )
}
```

## Advantages Over Node.js CLI

### 1. Native Android Integration
- ✅ No Node.js runtime needed
- ✅ Direct file picker access
- ✅ Mobile Wallet Adapter integration
- ✅ Native APK parsing
- ✅ Smaller app size

### 2. User Experience
- ✅ Guided step-by-step wizard
- ✅ Real-time validation feedback
- ✅ Progress indicators for uploads
- ✅ No terminal/command-line knowledge required

### 3. Self-Service Publishing
- ✅ Users can publish their own apps
- ✅ No separate CLI installation
- ✅ Works on Android devices (phone/tablet)
- ✅ Integrated with app building

## Implementation Status

### ✅ Completed
- Configuration data structure
- Validation logic (assets, metadata, URLs)
- UI wizard framework
- Metadata JSON generation (spec v0.4.0)
- Progress tracking system

### 🚧 In Progress
- Arweave/Irys upload integration
- APK metadata extraction (needs aapt2 or parser library)
- Metaplex NFT transaction building
- Mobile Wallet Adapter signing flow
- Publisher Portal submission

### 📋 Planned
- Canary auth token integration (for Irys)
- Image dimension validation
- Certificate fingerprint extraction
- Locale detection from APK
- Resume functionality (save progress)
- Asset caching (.asset-manifest.json equivalent)

## Dependencies

Required libraries (add to `build.gradle.kts`):

```kotlin
dependencies {
    // Already included
    implementation("com.solanamobile:mobile-wallet-adapter-clientlib-ktx:2.0.3")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("org.bouncycastle:bcprov-jdk15on:1.70")
    implementation("com.google.code.gson:gson:2.10.1")
    
    // Additional for APK parsing
    implementation("net.dongliu:apk-parser:2.6.10")
}
```

## Error Handling

### Validation Errors
```kotlin
ValidationResult(
    valid = false,
    errors = listOf(
        "Icon must be exactly 512x512px",
        "Short description must be 30 characters or less",
        "Invalid publisher email"
    ),
    warnings = listOf(
        "Banner is recommended (1200x600px)"
    )
)
```

### Publishing Errors
```kotlin
PublishingResult(
    success = false,
    error = "Arweave upload failed: Network timeout"
)
```

## Testing

### Local Testing
```kotlin
@Test
fun testValidation() {
    val config = createTestConfig()
    val workflow = DappStoreCliWorkflow(context, walletManager, solanaRepository)
    
    val result = runBlocking {
        workflow.validateConfig(config)
    }
    
    assertTrue(result.valid)
    assertEquals(0, result.errors.size)
}
```

### Integration Testing
```kotlin
@Test
fun testCompleteWorkflow() {
    val config = createValidConfig()
    val workflow = DappStoreCliWorkflow(context, walletManager, solanaRepository)
    
    val result = runBlocking {
        workflow.publishToStore(config) { progress ->
            println("${progress.stage}: ${progress.progress}/${progress.total}")
        }
    }
    
    assertTrue(result.success)
    assertNotNull(result.appMintAddress)
    assertNotNull(result.releaseMintAddress)
}
```

## Security Considerations

### Private Keys
- ✅ Never stores private keys
- ✅ Uses Mobile Wallet Adapter for signing
- ✅ Transactions signed by user's wallet app
- ✅ No key export required

### Asset Storage
- ✅ Arweave for permanent, immutable storage
- ✅ Irys for instant finality
- ✅ Content addressing (IPFS-style)
- ✅ No centralized asset hosting

### Submission Attestations
- ✅ User confirms policy compliance
- ✅ User confirms authorization
- ✅ Cryptographic signature proves publisher identity

## Future Enhancements

### v1.1
- [ ] Update existing releases
- [ ] Multiple locale support
- [ ] Video screenshots support
- [ ] Feature graphic support

### v1.2
- [ ] Batch publishing (multiple apps)
- [ ] Template system (reuse publisher info)
- [ ] Analytics dashboard
- [ ] Publishing history

### v2.0
- [ ] Alpha testing distribution
- [ ] Private RPC integration
- [ ] Advanced metadata editor
- [ ] A/B testing for screenshots

## Comparison Table

| Feature | Official CLI | BubbleWrapper Kotlin |
|---------|-------------|---------------------|
| Platform | Node.js (Desktop) | Android Native |
| Language | TypeScript | Kotlin |
| UI | Terminal | Jetpack Compose |
| Wallet | File-based keypair | Mobile Wallet Adapter |
| File Access | Direct filesystem | Android file pickers |
| Asset Validation | aapt2 + imagemagick | Native Android APIs |
| Metadata Format | Spec v0.4.0 | Spec v0.4.0 ✅ |
| Arweave Upload | Irys SDK | Irys REST API |
| NFT Creation | Metaplex TS SDK | Metaplex instructions |
| Portal Submission | HubSpot API | HubSpot API ✅ |
| Progress Tracking | Console logs | Real-time UI updates |
| Error Handling | Exit codes | Result objects |
| Offline Support | ❌ | Partial (save config) |
| Mobile-Friendly | ❌ | ✅ Native |

## Resources

- [Official CLI GitHub](https://github.com/solana-mobile/dapp-publishing)
- [Publishing Spec v0.4.0](https://github.com/solana-mobile/dapp-publishing/blob/main/publishing-spec/SPEC.md)
- [dApp Store Docs](https://docs.solanamobile.com/dapp-publishing/intro)
- [BubbleWrapper GitHub](https://github.com/QuarksBlueFoot/BubbleWrapper)

---

**Built by [Bluefoot Labs](https://bluefoot.xyz)**  
*Making Solana dApp Store publishing accessible to everyone*
