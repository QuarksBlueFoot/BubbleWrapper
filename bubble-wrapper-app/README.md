# 🫧 Bubble Wrapper Android App

<div align="center">

**Native Android companion app for the Bubblewrap CLI**

[![Solana Mobile](https://img.shields.io/badge/Solana%20Mobile-Ready-9945FF?style=for-the-badge&logo=solana)](https://solanamobile.com)
[![Android](https://img.shields.io/badge/Android-Kotlin-3DDC84?style=for-the-badge&logo=android)](https://developer.android.com)
[![Jetpack Compose](https://img.shields.io/badge/Jetpack-Compose-4285F4?style=for-the-badge)](https://developer.android.com/jetpack/compose)

*Simplify PWA → Android TWA conversion for Solana Mobile dApp Store*

</div>

---

## 📱 Features

### 🔧 Configure
- Enter your PWA manifest URL
- Auto-fetch and parse manifest data
- Configure app name, package ID, colors
- Set orientation and display mode

### 🛠️ Build
- Generate ready-to-use Bubblewrap CLI commands
- Copy commands with one tap
- Step-by-step guide with exact prompt values
- Digital Asset Links generation

### 🚀 Publish (NEW - With Wallet Integration!)
- **🔗 Solana Wallet Connection** via Mobile Wallet Adapter (MWA)
  - Connect Phantom, Solflare, Backpack, or any MWA-compatible wallet
  - Real-time balance display
  - Persistent wallet session
- **📤 Asset Upload** for icons, banners, and screenshots
- **📦 APK Selection** for signed release builds
- **⚙️ config.yaml Generator** for dApp Store CLI
- **🖥️ Dual Publishing Mode**:
  - **In-App**: One-click publish with wallet signing
  - **CLI Mode**: Generate terminal commands for manual publishing
- **📊 Publishing Status** with step-by-step progress
- **💰 Cost Estimation** (~0.1-0.2 SOL)
- Direct link to Publisher Portal

### 📚 Documentation
- In-app guides for publishing
- Solana dApp Store requirements
- Troubleshooting tips
- External resource links

## 📖 Guides

- **[Publishing Guide](PUBLISHING_GUIDE.md)**: Complete walkthrough for building and publishing this app via Android Studio.
- **[Infrastructure Guide](BACKEND_SETUP.md)**: Details about the app's client-side architecture and external dependencies (No custom backend required).

### 👤 Credits
- Built by [@moonmanquark](https://x.com/moonmanquark)
- Powered by BF Labs
- Built for Solana Mobile

## 🚀 Getting Started

### Prerequisites

- Android Studio Hedgehog (2023.1.1) or later
- JDK 17+
- Kotlin 1.9+

### Build

```bash
cd bubble-wrapper-app
./gradlew assembleDebug
```

### Install

```bash
./gradlew installDebug
```

Or open the project in Android Studio and run on a device/emulator.

## 🎨 Design

The app uses:
- **Jetpack Compose** for declarative UI
- **Material 3** design system
- **Glassmorphism** effects with Solana brand colors
- Dark theme optimized for OLED displays

### Color Palette

| Color | Hex | Usage |
|-------|-----|-------|
| Solana Purple | `#9945FF` | Primary accent |
| Solana Green | `#14F195` | Secondary/success |
| Background | `#0B0F1A` | Dark theme |
| Surface | `#131B2E` | Cards/panels |

## 📂 Project Structure

```
bubble-wrapper-app/
├── app/
│   ├── src/main/
│   │   ├── java/xyz/bluefoot/bubblewrapper/
│   │   │   ├── MainActivity.kt
│   │   │   ├── BubbleWrapperApp.kt         # Main UI & Navigation
│   │   │   ├── network/
│   │   │   │   ├── SolanaRepository.kt     # Solana RPC calls
│   │   │   │   └── DappStoreService.kt     # dApp Store API
│   │   │   ├── wallet/
│   │   │   │   └── WalletManager.kt        # MWA wallet connection
│   │   │   └── ui/
│   │   │       ├── screens/
│   │   │       │   ├── GuideScreens.kt     # 6 in-app guides
│   │   │       │   ├── PublishScreen.kt    # dApp Store publishing
│   │   │       │   └── WalletComponents.kt # Wallet UI components
│   │   │       └── theme/
│   │   │           ├── Color.kt
│   │   │           ├── Theme.kt
│   │   │           └── Type.kt
│   │   ├── res/
│   │   │   ├── values/
│   │   │   └── xml/
│   │   └── AndroidManifest.xml
│   ├── build.gradle.kts
│   └── proguard-rules.pro
├── build.gradle.kts
├── settings.gradle.kts
├── PUBLISHING_GUIDE.md
├── BACKEND_SETUP.md
└── README.md
```

## 🔗 Dependencies

| Library | Purpose |
|---------|---------|
| `mobile-wallet-adapter-clientlib-ktx` | Solana Mobile Wallet Adapter |
| `okhttp` | HTTP client for RPC calls |
| `gson` | JSON parsing |
| `datastore-preferences` | Wallet session persistence |
| `webkit` | Embedded browser support |

## 🔗 Related

- [Publishing Guide](./PUBLISHING_GUIDE.md) - Complete Android Studio publishing guide
- [Backend/Infrastructure Guide](./BACKEND_SETUP.md) - Architecture documentation
- [Sample PWA](../sample-pwa/) - Example mobile-optimized PWA
- [Bubblewrap CLI](https://nicholasmorgan.github.io/nicholasmorgan/Bubblewrap.html)
- [Solana Mobile Docs](https://docs.solanamobile.com)
- [Mobile Wallet Adapter](https://github.com/solana-mobile/mobile-wallet-adapter)

## 📄 License

MIT License - See [LICENSE](../LICENSE) for details

---

<div align="center">

**Built for Solana Mobile** 🫧

*Part of the BubbleWrapper project by [Bluefoot Labs](https://bluefoot.xyz)*

</div>