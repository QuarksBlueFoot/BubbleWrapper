# 📚 BubbleWrapper Documentation Index

**Complete guide to all documentation, keystores, and build artifacts**

---

## 🎯 Quick Start Guide

**New to BubbleWrapper?** Start here:

1. **[PROJECT_README.md](PROJECT_README.md)** - Main project overview and quick start
2. **[KEYSTORES_SUMMARY.txt](KEYSTORES_SUMMARY.txt)** - Visual keystore reference
3. **[MOBILE_OPTIMIZATIONS.md](sample-pwa/MOBILE_OPTIMIZATIONS.md)** - See the mobile-optimized sample
4. **[PUBLISHING_GUIDE.md](bubble-wrapper-app/PUBLISHING_GUIDE.md)** - Publish to Solana dApp Store

---

## 📖 Core Documentation

### 🏠 Root Directory

| Document | Size | Description |
|----------|------|-------------|
| **[PROJECT_README.md](PROJECT_README.md)** | 13K | **START HERE** - Complete project overview, quick start, architecture |
| **[README.md](README.md)** | 5.8K | Original README with basic info |
| **[KEYSTORE_GUIDE.md](KEYSTORE_GUIDE.md)** | 8.5K | **Complete keystore reference** - Commands, security, Digital Asset Links |
| **[KEYSTORES_SUMMARY.txt](KEYSTORES_SUMMARY.txt)** | 7.4K | **Visual keystore summary** - Quick reference for both apps |

### 📱 BubbleWrapper App (Publishing Tool)

| Document | Size | Description |
|----------|------|-------------|
| **[PUBLISHING_GUIDE.md](bubble-wrapper-app/PUBLISHING_GUIDE.md)** | 13K | **Complete publishing walkthrough** - Step-by-step Solana dApp Store guide |
| **[APP_DOCUMENTATION.md](bubble-wrapper-app/APP_DOCUMENTATION.md)** | 10K | App architecture, features, API reference |
| **[README.md](bubble-wrapper-app/README.md)** | 5.3K | App-specific quick start |
| **[BACKEND_SETUP.md](bubble-wrapper-app/BACKEND_SETUP.md)** | 3.3K | Helius RPC configuration |

### 🎨 MonkeMob TWA Sample (Mobile-Optimized PWA)

| Document | Size | Description |
|----------|------|-------------|
| **[MOBILE_OPTIMIZATIONS.md](sample-pwa/MOBILE_OPTIMIZATIONS.md)** | 5.9K | **Mobile best practices** - Splash screen, Chrome preference, safe areas |
| **[README.md](sample-pwa/README.md)** | 2.9K | Sample app overview |
| **[SETUP.md](sample-pwa/SETUP.md)** | 5.7K | Local development setup |
| **[BUBBLEWRAP_PROMPTS.md](sample-pwa/BUBBLEWRAP_PROMPTS.md)** | 4.1K | Bubblewrap CLI configuration reference |

### 🚀 Production Status

| Document | Size | Description |
|----------|------|-------------|
| **[PRODUCTION_MAINNET_READY.md](PRODUCTION_MAINNET_READY.md)** | 4.1K | Mainnet production readiness checklist |
| **[PUBLISHING_FINAL_STATUS.md](PUBLISHING_FINAL_STATUS.md)** | 3.9K | Final publishing status report |
| **[READY_TO_PUBLISH.md](READY_TO_PUBLISH.md)** | 5.3K | Pre-publishing validation |
| **[MONKEMOB_PUBLISHING.md](MONKEMOB_PUBLISHING.md)** | 4.4K | MonkeMob-specific publishing notes |

### 🧪 Development & Testing

| Document | Size | Description |
|----------|------|-------------|
| **[DEVNET_TESTING.md](DEVNET_TESTING.md)** | 8.8K | Devnet testing procedures |
| **[DEVNET_TEST_SUMMARY.txt](DEVNET_TEST_SUMMARY.txt)** | 4.7K | Test results summary |
| **[ARWEAVE_UPLOAD.md](ARWEAVE_UPLOAD.md)** | 4.7K | Arweave asset upload guide |

---

## 🔐 Keystores

### MonkeMob TWA

**Primary Location:**
```
sample-pwa/android-twa-generated/app/monkemob-release.keystore
```

**Details:**
- Alias: `android`
- Password: `monkemob123`
- SHA256: `09:4A:9F:4E:BE:0E:D1:A9:E6:A2:F6:2A:E2:05:B8:50...`
- Valid until: June 10, 2053

### BubbleWrapper App

**Location:**
```
bubble-wrapper-app/app/bubblewrapper-release.keystore
```

**Details:**
- Alias: `bubblewrapper`
- Password: `bubblewrapper123`
- SHA256: `98:D0:CA:5E:E7:66:1C:F7:B1:66:E6:DD:C9:E5:28:36...`
- Valid until: June 10, 2053

📖 **Complete Guide**: [KEYSTORE_GUIDE.md](KEYSTORE_GUIDE.md) | **Quick Reference**: [KEYSTORES_SUMMARY.txt](KEYSTORES_SUMMARY.txt)

---

## 📦 Pre-Built APKs

Located in **[DOWNLOADS/](DOWNLOADS/)**:

| APK | Size | Package | Description |
|-----|------|---------|-------------|
| **bubblewrapper-release.apk** | 9.7 MB | xyz.bluefoot.bubblewrapper | Publishing tool with NFT creation |
| **monkemob-release.apk** | 1.1 MB | me.monkemob.twa | Mobile-optimized TWA sample |

**Install via ADB:**
```bash
adb install DOWNLOADS/bubblewrapper-release.apk
adb install DOWNLOADS/monkemob-release.apk
```

---

## 🏗️ Source Code Structure

### BubbleWrapper App (Kotlin + Jetpack Compose)

```
bubble-wrapper-app/
├── app/src/main/java/xyz/bluefoot/bubblewrapper/
│   ├── MainActivity.kt                    # Main entry point
│   ├── network/
│   │   └── DappStorePublisher.kt         # ⭐ Metaplex NFT + Arweave upload
│   ├── ui/screens/
│   │   └── PublishScreen.kt              # ⭐ Category dropdown + file picker
│   └── solana/
│       └── MobileWalletAdapter.kt        # Transaction signing
├── app/bubblewrapper-release.keystore    # 🔐 Signing key
└── app/build.gradle.kts                  # Build configuration
```

**Key Files:**
- **[DappStorePublisher.kt](bubble-wrapper-app/app/src/main/java/xyz/bluefoot/bubblewrapper/network/DappStorePublisher.kt)** - NFT metadata creation, spec v0.4.0 compliant
- **[PublishScreen.kt](bubble-wrapper-app/app/src/main/java/xyz/bluefoot/bubblewrapper/ui/screens/PublishScreen.kt)** - Publishing UI with 10 category options

### MonkeMob TWA (Java + XML)

```
sample-pwa/android-twa-generated/
├── app/src/main/
│   ├── java/me/monkemob/twa/
│   │   └── LauncherActivity.java         # ⭐ Mobile optimizations
│   └── res/
│       └── drawable/
│           └── splash_terminal.xml       # ⭐ Pixel terminal splash
├── app/monkemob-release.keystore         # 🔐 Signing key
└── twa-manifest.json                     # TWA configuration
```

**Key Files:**
- **[splash_terminal.xml](sample-pwa/android-twa-generated/app/src/main/res/drawable/splash_terminal.xml)** - Pixel splash with falling bananas
- **[LauncherActivity.java](sample-pwa/android-twa-generated/app/src/main/java/me/monkemob/twa/LauncherActivity.java)** - Portrait orientation handling
- **[twa-manifest.json](sample-pwa/android-twa-generated/twa-manifest.json)** - Theme colors, display mode, fallback

---

## 🎓 Learning Path

### For Publishing Apps

1. **[PROJECT_README.md](PROJECT_README.md)** - Understand the project
2. **[KEYSTORE_GUIDE.md](KEYSTORE_GUIDE.md)** - Learn about keystores
3. **[PUBLISHING_GUIDE.md](bubble-wrapper-app/PUBLISHING_GUIDE.md)** - Follow publishing steps
4. **[APP_DOCUMENTATION.md](bubble-wrapper-app/APP_DOCUMENTATION.md)** - Understand the tool

### For Building TWAs

1. **[PROJECT_README.md](PROJECT_README.md)** - Project overview
2. **[MOBILE_OPTIMIZATIONS.md](sample-pwa/MOBILE_OPTIMIZATIONS.md)** - Mobile best practices
3. **[SETUP.md](sample-pwa/SETUP.md)** - Local development
4. **[BUBBLEWRAP_PROMPTS.md](sample-pwa/BUBBLEWRAP_PROMPTS.md)** - CLI configuration

### For Understanding Code

1. **[APP_DOCUMENTATION.md](bubble-wrapper-app/APP_DOCUMENTATION.md)** - BubbleWrapper architecture
2. **[DappStorePublisher.kt](bubble-wrapper-app/app/src/main/java/xyz/bluefoot/bubblewrapper/network/DappStorePublisher.kt)** - NFT creation logic
3. **[PublishScreen.kt](bubble-wrapper-app/app/src/main/java/xyz/bluefoot/bubblewrapper/ui/screens/PublishScreen.kt)** - UI implementation
4. **[MOBILE_OPTIMIZATIONS.md](sample-pwa/MOBILE_OPTIMIZATIONS.md)** - Mobile optimizations explained

---

## 🔍 Quick Reference

### Build Commands

**BubbleWrapper:**
```bash
cd bubble-wrapper-app
export ANDROID_HOME=/path/to/android-sdk
./gradlew assembleRelease
# Output: app/build/outputs/apk/release/app-release.apk
```

**MonkeMob TWA:**
```bash
cd sample-pwa/android-twa-generated
export ANDROID_HOME=/path/to/android-sdk
./gradlew assembleRelease
# Output: app/build/outputs/apk/release/app-release.apk
```

### Keystore Commands

**View keystore details:**
```bash
keytool -list -v -keystore KEYSTORE_FILE -storepass PASSWORD
```

**Export certificate:**
```bash
keytool -export -rfc -keystore KEYSTORE_FILE -storepass PASSWORD -alias ALIAS -file cert.pem
```

**Create backup:**
```bash
gpg -c KEYSTORE_FILE
```

### Publishing Checklist

- [ ] App APK ready (signed and tested)
- [ ] Icon 512x512 PNG prepared
- [ ] Screenshots 1080x1920 PNG (3-5)
- [ ] Wallet funded (0.1+ SOL)
- [ ] Metadata fields filled
- [ ] Keystore backed up
- [ ] assetlinks.json deployed (TWA only)

---

## 📞 Support & Resources

### Documentation Files

- **General**: [PROJECT_README.md](PROJECT_README.md)
- **Keystores**: [KEYSTORE_GUIDE.md](KEYSTORE_GUIDE.md), [KEYSTORES_SUMMARY.txt](KEYSTORES_SUMMARY.txt)
- **Publishing**: [PUBLISHING_GUIDE.md](bubble-wrapper-app/PUBLISHING_GUIDE.md)
- **Mobile**: [MOBILE_OPTIMIZATIONS.md](sample-pwa/MOBILE_OPTIMIZATIONS.md)
- **Development**: [APP_DOCUMENTATION.md](bubble-wrapper-app/APP_DOCUMENTATION.md)

### External Resources

- [Solana Mobile Docs](https://docs.solanamobile.com/)
- [dApp Store Spec](https://github.com/solana-mobile/dapp-publishing)
- [TWA Guide](https://developer.chrome.com/docs/android/trusted-web-activity/)
- [Android Signing](https://developer.android.com/studio/publish/app-signing)

---

## 📊 Document Statistics

- **Total Documentation**: 18 files, 120+ KB
- **Code Documentation**: Inline comments + KDoc in Kotlin files
- **Guides**: 7 comprehensive guides
- **References**: 4 quick reference documents
- **Status Reports**: 5 production status documents

---

<div align="center">

**📚 All documentation is up-to-date as of January 23, 2026**

[Keystores](KEYSTORE_GUIDE.md) • [Publishing](bubble-wrapper-app/PUBLISHING_GUIDE.md) • [Mobile](sample-pwa/MOBILE_OPTIMIZATIONS.md) • [Quick Start](PROJECT_README.md)

</div>
