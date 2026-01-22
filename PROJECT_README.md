# 🫧 BubbleWrapper

**Complete Android TWA toolkit for publishing PWAs to Solana Mobile dApp Store**

<div align="center">

[![Solana Mobile](https://img.shields.io/badge/Solana%20Mobile-dApp%20Store-9945FF?style=for-the-badge&logo=solana)](https://solanamobile.com)
[![Android](https://img.shields.io/badge/Android-7.0%2B-3DDC84?style=for-the-badge&logo=android)](https://developer.android.com)
[![TWA](https://img.shields.io/badge/TWA-Enabled-4285F4?style=for-the-badge&logo=googlechrome)](https://developer.chrome.com/docs/android/trusted-web-activity/)

*Transform your Progressive Web App into a native Android app and publish to Solana dApp Store - all from your Android device!*

</div>

---

## 🎯 What's Inside

### 📱 BubbleWrapper App (9.8 MB)
On-device publishing tool with full Metaplex NFT creation:
- ✅ **On-Device Keystore Generation** - Generate signing keys directly on your phone
- ✅ **TWA Build Wizard** - Step-by-step TWA configuration
- ✅ **SHA-256 Fingerprint Extraction** - For Digital Asset Links
- ✅ **Manifest Auto-Fill** - Fetch PWA manifest to auto-fill app details
- ✅ **APK Metadata Extraction** - Read package info from APK files
- ✅ Create Solana dApp Store listings
- ✅ Upload assets to Arweave
- ✅ Mint NFT with Mobile Wallet Adapter
- ✅ Full spec v0.4.0 compliance
- ✅ Content URI file picker support
- ✅ Category selection dropdown


### 🎨 MonkeMob TWA Sample (1.1 MB)
Highly mobile-optimized PWA showcase:
- ✅ Pixel terminal splash screen with falling bananas
- ✅ Chrome browser preference with fallback
- ✅ Portrait-first mobile layout
- ✅ Safe area inset handling
- ✅ Glassmorphism UI design
- ✅ Touch-optimized interactions

---

## 🚀 Quick Start

### Option 1: Use Pre-Built APKs

Download from [`DOWNLOADS/`](DOWNLOADS/):
- **bubblewrapper-release.apk** (9.7 MB) - Publishing tool
- **monkemob-release.apk** (1.1 MB) - TWA sample app

Install on Android device via ADB:
```bash
adb install DOWNLOADS/bubblewrapper-release.apk
adb install DOWNLOADS/monkemob-release.apk
```

### Option 2: Build From Source

**BubbleWrapper:**
```bash
cd bubble-wrapper-app
./gradlew assembleRelease
# Output: app/build/outputs/apk/release/app-release.apk
```

**MonkeMob TWA:**
```bash
cd sample-pwa/android-twa-generated
./gradlew assembleRelease
# Output: app/build/outputs/apk/release/app-release.apk
```

---

## 📂 Project Structure

```
BubbleWrapper/
├── bubble-wrapper-app/              # 📱 Publishing Tool (Kotlin + Jetpack Compose)
│   ├── app/
│   │   ├── src/main/java/xyz/bluefoot/bubblewrapper/
│   │   │   ├── network/
│   │   │   │   └── DappStorePublisher.kt    # NFT creation + Arweave upload
│   │   │   ├── ui/screens/
│   │   │   │   ├── PublishScreen.kt         # Category dropdown + file picker
│   │   │   │   ├── KeystoreScreen.kt        # 🆕 On-device keystore generation
│   │   │   │   └── TwaBuildWizardScreen.kt  # 🆕 TWA configuration wizard
│   │   │   ├── utils/
│   │   │   │   ├── KeystoreGenerator.kt     # 🆕 X.509 cert generation
│   │   │   │   └── TwaConfigGenerator.kt    # 🆕 TWA manifest generation
│   │   │   └── MainActivity.kt
│   │   ├── bubblewrapper-release.keystore   # Signing key
│   │   └── build.gradle.kts                 # Kotlin DSL config
│   ├── PUBLISHING_GUIDE.md          # Complete publishing walkthrough
│   └── README.md
│
├── sample-pwa/                      # 🎨 Mobile-Optimized PWA Sample
│   ├── android-twa-generated/       # Built TWA project
│   │   ├── app/
│   │   │   ├── src/main/
│   │   │   │   ├── java/me/monkemob/twa/
│   │   │   │   │   └── LauncherActivity.java    # Mobile optimizations
│   │   │   │   └── res/
│   │   │   │       └── drawable/
│   │   │   │           └── splash_terminal.xml  # Pixel splash screen
│   │   │   ├── monkemob-release.keystore        # Signing key
│   │   │   └── build.gradle                     # TWA config
│   │   └── twa-manifest.json        # TWA configuration
│   ├── web/                         # PWA source (optional - use your own)
│   │   ├── src/
│   │   │   ├── App.tsx              # React app
│   │   │   └── styles.css           # Glassmorphism styles
│   │   ├── index.html
│   │   └── vite.config.ts
│   ├── MOBILE_OPTIMIZATIONS.md      # Mobile best practices guide
│   └── README.md
│
├── DOWNLOADS/                       # ⬇️ Pre-built APKs
│   ├── bubblewrapper-release.apk    # 9.8 MB - Publishing tool (with keystore gen)
│   └── monkemob-release.apk         # 1.1 MB - TWA sample
│
├── docs-website/                    # 📚 Documentation site
│
├── KEYSTORE_GUIDE.md               # 🔐 Complete keystore reference
├── README.md                        # 👈 You are here
└── android-sdk/                     # Android SDK (for building)
```

---

## 🔐 Keystores & Signing

### MonkeMob TWA
- **File**: `sample-pwa/android-twa-generated/app/monkemob-release.keystore`
- **Alias**: `android`
- **Password**: `monkemob123`
- **SHA256**: `09:4A:9F:4E:BE:0E:D1:A9:E6:A2:F6:2A:E2:05:B8:50:7A:43:3A:6A:53:35:63:B2:37:87:B5:B5:C7:D9:B4:CF`
- **Valid Until**: June 10, 2053

### BubbleWrapper App
- **File**: `bubble-wrapper-app/app/bubblewrapper-release.keystore`
- **Alias**: `bubblewrapper`
- **Password**: `bubblewrapper123`
- **SHA256**: `98:D0:CA:5E:E7:66:1C:F7:B1:66:E6:DD:C9:E5:28:36:A3:64:12:7A:7A:D6:94:8E:23:72:3B:A3:1C:1C:C9:D6`
- **Valid Until**: June 10, 2053

📖 **Full Details**: See [KEYSTORE_GUIDE.md](KEYSTORE_GUIDE.md) for commands, security best practices, and certificate management.

---

## 📱 Mobile Optimizations

The MonkeMob TWA showcases best practices for mobile PWAs:

### ✨ Enhanced Splash Screen
- Pixel terminal aesthetic with Press Start 2P font
- Circular masked icon (120dp) in gold frame (140dp)
- Saga MonkeMob logo (180dp) at top
- 6 falling banana animations
- Fast 300ms fade-out transition

### 🎨 Glassmorphism Design
- Frosted glass panels with `backdrop-filter: blur(20px)`
- Soft shadows and glow effects
- High contrast text on dark gradients
- Touch-optimized interactions (no tap highlight flash)

### 📐 Safe Area Support
```css
.safe {
  padding-left: max(16px, env(safe-area-inset-left));
  padding-right: max(16px, env(safe-area-inset-right));
}
```

### 🔄 Chrome Browser Preference
- Automatic Chrome selection via androidbrowserhelper 2.6.2
- Fallback to other Chromium browsers
- Custom tabs support for older devices

📖 **Full Details**: See [sample-pwa/MOBILE_OPTIMIZATIONS.md](sample-pwa/MOBILE_OPTIMIZATIONS.md)

---

## 📖 Documentation

### Getting Started
- **[Quick Start](#-quick-start)** - Build and install APKs
- **[Project Structure](#-project-structure)** - Navigate the codebase

### Guides
- **[KEYSTORE_GUIDE.md](KEYSTORE_GUIDE.md)** - Complete keystore reference with commands and security
- **[MOBILE_OPTIMIZATIONS.md](sample-pwa/MOBILE_OPTIMIZATIONS.md)** - Mobile-first PWA best practices
- **[PUBLISHING_GUIDE.md](bubble-wrapper-app/PUBLISHING_GUIDE.md)** - Step-by-step Solana dApp Store publishing

### API Reference
- **[DappStorePublisher.kt](bubble-wrapper-app/app/src/main/java/xyz/bluefoot/bubblewrapper/network/DappStorePublisher.kt)** - Metaplex NFT creation
- **[PublishScreen.kt](bubble-wrapper-app/app/src/main/java/xyz/bluefoot/bubblewrapper/ui/screens/PublishScreen.kt)** - Publishing UI with category dropdown

### Sample App
- **[LauncherActivity.java](sample-pwa/android-twa-generated/app/src/main/java/me/monkemob/twa/LauncherActivity.java)** - Mobile optimizations
- **[splash_terminal.xml](sample-pwa/android-twa-generated/app/src/main/res/drawable/splash_terminal.xml)** - Pixel splash screen
- **[twa-manifest.json](sample-pwa/android-twa-generated/twa-manifest.json)** - TWA configuration

---

## 🛠️ Development

### Prerequisites
- Java 17 or higher
- Android SDK (included in `android-sdk/`)
- Gradle (wrapper included)
- Node.js 18+ (for PWA development)

### Environment Setup

**1. Set Android SDK path:**
```bash
export ANDROID_HOME=/workspaces/BubbleWrapper/android-sdk
```

**2. Configure Helius API Key (BubbleWrapper only):**
```bash
cd bubble-wrapper-app
cp local.properties.template local.properties
# Edit local.properties and add your Helius API key
```

**3. Build:**
```bash
# BubbleWrapper
cd bubble-wrapper-app && ./gradlew assembleRelease

# MonkeMob TWA
cd sample-pwa/android-twa-generated && ./gradlew assembleRelease
```

### PWA Development

```bash
cd sample-pwa/web
npm install
npm run dev       # Dev server at localhost:5173
npm run build     # Production build
npm run preview   # Preview production build
```

---

## 📋 Publishing to Solana dApp Store

### Step 1: Install BubbleWrapper
```bash
adb install DOWNLOADS/bubblewrapper-release.apk
```

### Step 2: Prepare Assets
- App APK (your TWA or Android app)
- Icon (512x512 PNG)
- Banner (1200x600 PNG, optional)
- Screenshots (1080x1920 PNG, 3-5 recommended)

### Step 3: Launch BubbleWrapper
1. Open app on Android device
2. Import Solana wallet (or create new)
3. Ensure wallet has 0.1+ SOL for transaction fees

### Step 4: Fill in Details
- **App Name**: Display name for dApp Store
- **Package ID**: Must match APK (e.g., `me.monkemob.twa`)
- **Category**: Select from dropdown (DeFi, NFT, Gaming, etc.)
- **Version**: Code (integer) and Name (e.g., 1.0.0)
- **Description**: Short (160 chars) and Full (4000 chars)
- **Publisher Info**: Email and website
- **Assets**: Upload icon, banner, screenshots

### Step 5: Publish
1. Tap "Publish to dApp Store"
2. Review NFT metadata
3. Sign transaction via Mobile Wallet Adapter
4. Wait for confirmation
5. Submit to https://publish.solanamobile.com

📖 **Detailed Walkthrough**: [bubble-wrapper-app/PUBLISHING_GUIDE.md](bubble-wrapper-app/PUBLISHING_GUIDE.md)

---

## 🔍 Specification Compliance

BubbleWrapper implements **Solana dApp Store Spec v0.4.0** with 100% compliance:

✅ **Release NFT JSON Structure**
- schema_version, name, description, image
- properties (category, creators[])
- extensions.solana_dapp_store.publisher_details (name, website, contact, support_email)
- extensions.solana_dapp_store.release_details (timestamps, URLs, localized_resources)
- extensions.solana_dapp_store.media[] (icon, banner, screenshots with dimensions)
- extensions.solana_dapp_store.files[] (APK with mime, purpose, uri, size, sha256)
- extensions.solana_dapp_store.android_details (package, version, min_sdk, cert, permissions, locales)
- i18n structure with UID mappings

✅ **Metaplex NFT Structure**
- Token mint (SPL Token)
- Metadata account (Metaplex Token Metadata)
- Master Edition (1/1 unique NFT)

✅ **Mobile Wallet Adapter Integration**
- Transaction signing via MWA 2.0.3
- ActivityResultSender support

---

## 📊 Technical Specs

### BubbleWrapper App
- **Size**: 9.7 MB
- **Package**: xyz.bluefoot.bubblewrapper
- **Min SDK**: 24 (Android 7.0 Nougat)
- **Target SDK**: 34 (Android 14)
- **Language**: Kotlin
- **UI**: Jetpack Compose
- **Libraries**: Mobile Wallet Adapter, Ktor (HTTP), Kotlinx Serialization

### MonkeMob TWA
- **Size**: 1.1 MB
- **Package**: me.monkemob.twa
- **Min SDK**: 24 (Android 7.0 Nougat)
- **Target SDK**: 35 (Android 15)
- **Language**: Java
- **Library**: androidbrowserhelper 2.6.2

---

## 🔗 Resources

### Solana Mobile
- [Solana Mobile Docs](https://docs.solanamobile.com/)
- [dApp Store Publishing Spec](https://github.com/solana-mobile/dapp-publishing)
- [Mobile Wallet Adapter](https://github.com/solana-mobile/mobile-wallet-adapter)

### Trusted Web Activities
- [TWA Documentation](https://developer.chrome.com/docs/android/trusted-web-activity/)
- [Bubblewrap CLI](https://github.com/GoogleChromeLabs/bubblewrap)
- [Digital Asset Links](https://developers.google.com/digital-asset-links)

### Android Development
- [Android Signing](https://developer.android.com/studio/publish/app-signing)
- [Jetpack Compose](https://developer.android.com/jetpack/compose)
- [Material 3](https://m3.material.io/)

---

## 🤝 Contributing

Contributions welcome! This project demonstrates:
- Solana Mobile dApp Store integration
- Mobile-optimized PWA patterns
- Android TWA best practices
- On-device Metaplex NFT creation

Feel free to open issues or pull requests.

---

## 📄 License

See [LICENSE](LICENSE) and [NOTICE](NOTICE) files.

---

## 🎉 Acknowledgments

- **Solana Mobile** - dApp Store and Mobile Wallet Adapter
- **Google Chrome Team** - Trusted Web Activities and Bubblewrap
- **Metaplex** - NFT standards
- **Helius** - Solana RPC infrastructure

---

<div align="center">

**Built with 💜 for Solana Mobile**

[Documentation](KEYSTORE_GUIDE.md) • [Mobile Guide](sample-pwa/MOBILE_OPTIMIZATIONS.md) • [Publishing](bubble-wrapper-app/PUBLISHING_GUIDE.md)

</div>
