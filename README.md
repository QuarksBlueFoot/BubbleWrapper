# 🫧 BubbleWrapper

<div align="center">

**Mobile-Optimized PWA Sample for Solana Mobile dApp Store**

[![Solana Mobile](https://img.shields.io/badge/Solana%20Mobile-Ready-9945FF?style=for-the-badge&logo=solana)](https://solanamobile.com)
[![PWA](https://img.shields.io/badge/PWA-Enabled-14F195?style=for-the-badge)](https://web.dev/progressive-web-apps/)
[![Bubblewrap](https://img.shields.io/badge/Bubblewrap-CLI-FF6B35?style=for-the-badge)](https://github.com/nicholasmorgan/nicholasmorgan/blob/main/Bubblewrap.md)

*A production-ready, premium Progressive Web App designed for Solana Mobile's dApp Store using Trusted Web Activities (TWA)*

**🌐 Live Demo: [bubblewrapper.bluefoot.xyz](https://bubblewrapper.bluefoot.xyz)**

</div>

---

## ✨ Key Features

### 🎨 Premium Styling (2025 Standards)

- **Glassmorphism UI** - Frosted glass cards with `backdrop-filter: blur(20px)` and `saturate(180%)`, animated gradient borders
- **Solana Brand Colors** - `#9945FF` (purple), `#14F195` (green), `#0B0F1A` (background)
- **Enhanced Splash Screen** - Custom Android `layer-list` drawable ([splash_enhanced.xml](sample-pwa/android-twa-generated/app/src/main/res/drawable/splash_enhanced.xml)) with 300ms fade-out
- **Chrome Browser Preference** - [ChromePreferredCustomTabs.java](sample-pwa/android-twa/patches/ChromePreferredCustomTabs.java) helper prefers Chrome with graceful fallback
- **Mobile-First Navigation** - [BottomNav.tsx](sample-pwa/web/src/components/BottomNav.tsx) with safe-area padding and 48dp+ tap targets

### 🚀 Technical Implementation

- **PWA Plugin** - [VitePWA](sample-pwa/web/vite.config.ts) with Workbox runtime caching and auto-update registration
- **Service Worker** - Automatic generation with precaching and offline support
- **TWA Configuration** - [twa-manifest.json](sample-pwa/android-twa-generated/twa-manifest.json) with package ID, theme colors, and signing key path
- **Manifest Auto-Fill** - BubbleWrapper app parses uploaded APKs to extract embedded TWA manifests
- **Mobile Wallet Adapter 2.0** - Session persistence with DataStore in [WalletManager.kt](bubble-wrapper-app/app/src/main/java/xyz/bluefoot/bubblewrapper/wallet/WalletManager.kt)
- **NFT Publishing** - [DappStorePublisher.kt](bubble-wrapper-app/app/src/main/java/xyz/bluefoot/bubblewrapper/network/DappStorePublisher.kt) handles Metaplex metadata v0.4.0 compliance

## 🚀 Quick Start

```bash
cd sample-pwa/web
npm install
npm run dev
```

Visit `http://localhost:5173` to see the PWA.

## 📦 Project Structure

```
BubbleWrapper/
├── sample-pwa/
│   ├── web/                           # 📱 Mobile-optimized PWA
│   │   ├── src/
│   │   │   ├── App.tsx               # Premium glassmorphism components
│   │   │   ├── components/           # BottomNav, TopBar
│   │   │   └── styles.css            # 2025 CSS with animations
│   │   ├── public/icons/             # PWA icons (192, 512, maskable)
│   │   └── vite.config.ts            # PWA manifest + Workbox
│   ├── android-twa/
│   │   ├── twa-manifest.example.json # Pre-configured for bluefoot.xyz
│   │   └── patches/
│   │       └── ChromePreferredCustomTabs.java
│   └── docs/
│       ├── PUBLISHING_GUIDE.md       # Complete dApp Store guide
│       └── bubblewrap.md             # CLI reference
└── README.md
```

## 🧪 Sample PWA — MonkeMob

This repository includes a **production-ready sample app** called **MonkeMob** (`sample-pwa/`) that showcases all required deliverables and optimizations:

### ✅ Deliverables Met

**✨ Highly Mobile-Optimized PWA Using Bubblewrap Template**
- Production-ready TWA wrapper published to Solana Mobile dApp Store
- Successfully uploaded with App NFT: `ADZWhSTQJoppJhapEbUS69yLg5HM2nJ322nenGxQqhMs`
- Live at: `https://monkemob.me` (package: `me.monkemob.twa`)

**🎨 Required Optimizations Implemented:**

1. **Improved Splash Screen Styling**
   - Custom Android `layer-list` with animated terminal aesthetic
   - Circular gold-framed app icon (120dp in 140dp frame)
   - Saga MonkeMob brand logo (180dp) positioned at top
   - 6 falling banana animations with varying opacity
   - Retro pixel font (Press Start 2P) for branding
   - Fast 300ms fade-out for smooth transition
   - 📄 Implementation: [splash_terminal.xml](sample-pwa/android-twa-generated/app/src/main/res/drawable/splash_terminal.xml)

2. **Default to Chrome Browser, Fall Back to System Default**
   - TWA automatically prefers Chrome for best performance
   - Automatic fallback to Chromium-based browsers if Chrome unavailable
   - Custom tabs fallback for devices without TWA support
   - Handled natively by `androidbrowserhelper:2.6.2` library
   - 📄 Configuration: [twa-manifest.json](sample-pwa/android-twa-generated/twa-manifest.json)

3. **Mobile-Intuitive Navigation and Layouts**
   - **Portrait-first orientation** with adaptive handling
   - **Safe area insets** for notched/punch-hole displays (`viewport-fit: cover`)
   - **Touch-optimized UI** with no tap highlight flash and 44x44dp minimum targets
   - **Glassmorphism design** with frosted panels and gradient backgrounds
   - **Bottom navigation** with safe-area padding for gesture areas
   - **Responsive layouts** optimized for thumb-friendly interaction
   - 📄 Styles: [styles.css](sample-pwa/web/src/styles.css), [BottomNav.tsx](sample-pwa/web/src/components/BottomNav.tsx)

### 📁 Project Structure
- `sample-pwa/web/` — Production PWA (Vite + React + TypeScript)
- `sample-pwa/android-twa/` — Bubblewrap/TWA source with MonkeMob configs
- `sample-pwa/android-twa-generated/` — Generated Android project with custom splash, icons, and launcher

### 📖 Complete Documentation
- [MOBILE_OPTIMIZATIONS.md](sample-pwa/MOBILE_OPTIMIZATIONS.md) — Detailed optimization breakdown
- [README.md](sample-pwa/README.md) — Quick start and deliverables checklist
- [PUBLISHING_GUIDE.md](sample-pwa/docs/PUBLISHING_GUIDE.md) — End-to-end dApp Store submission

**Use MonkeMob as your template** for building and publishing mobile-optimized TWA apps to the Solana dApp Store.

## 📚 Local Documentation Website

The `docs-website/` folder contains the project documentation you can run locally:

```bash
cd docs-website
npm install
npm run dev
```

This site hosts guides for MWA connection, TWA building, Digital Asset Links, and publishing to the Solana dApp Store.


## 📖 Documentation

| Document | Description |
|----------|-------------|
| [PUBLISHING_GUIDE.md](sample-pwa/docs/PUBLISHING_GUIDE.md) | End-to-end dApp Store submission |
| [DAPP_STORE_CHECKLIST.md](sample-pwa/docs/DAPP_STORE_CHECKLIST.md) | Complete submission requirements checklist |
| [DIGITAL_ASSET_LINKS.md](sample-pwa/docs/DIGITAL_ASSET_LINKS.md) | Fullscreen TWA setup guide |
| [SETUP.md](sample-pwa/SETUP.md) | Complete setup with CLI prompts |
| [BUBBLEWRAP_PROMPTS.md](sample-pwa/BUBBLEWRAP_PROMPTS.md) | Exact CLI answers |

### Companion App Guides

The Bubble Wrapper companion app includes **comprehensive in-app guides** covering:
- Getting Started with TWA
- Solana dApp Store Submission
- Digital Asset Links Setup
- Styling & Theming Best Practices
- Keystore & Signing Management
- Troubleshooting Common Issues

## 🔧 Build & Deploy

### 1. Build PWA

```bash
cd sample-pwa/web
npm run build
```

Deploy `dist/` to HTTPS hosting (Cloudflare Pages, Vercel, Netlify).

### 2. Generate Android TWA

```bash
mkdir android-build && cd android-build
bubblewrap init --manifest=https://bubblewrapper.bluefoot.xyz/manifest.webmanifest
```

Use these settings:
- **Theme/Splash color:** `#0B0F1A`
- **Package ID:** `xyz.bluefoot.bubblewrapper.sample`
- **Display mode:** `standalone`

### 3. Build APK

```bash
bubblewrap build
```

Output: `app-release-signed.apk`

## ✨ Features

### PWA Features
- ✅ **VitePWA Plugin** - Auto-generates manifest and service worker ([vite.config.ts](sample-pwa/web/vite.config.ts))
- ✅ **Workbox Runtime Caching** - Google Fonts caching with `CacheFirst` strategy
- ✅ **Maskable Icons** - Adaptive icons for Android with `purpose: "any maskable"`
- ✅ **Auto-Update** - Service worker updates automatically with `registerType: "autoUpdate"`
- ✅ **Offline Support** - Precaching of critical assets on install

### Premium Mobile UX
- ✅ **Bottom Navigation** - Fixed position with glassmorphism backdrop ([BottomNav.tsx](sample-pwa/web/src/components/BottomNav.tsx))
- ✅ **Safe-Area Insets** - `env(safe-area-inset-*)` support for notches and gesture bars
- ✅ **48dp+ Touch Targets** - Thumb-friendly tap areas per Material Design 3
- ✅ **Glassmorphism Cards** - `backdrop-filter: blur(20px) saturate(180%)` with gradient borders
- ✅ **Animated Gradients** - Framer Motion background animations with gradient orbs
- ✅ **Hash Navigation** - Back button support without full-page reloads

### TWA Optimizations
- ✅ **Enhanced Splash Screen** - Android layer-list with centered icon ([splash_enhanced.xml](sample-pwa/android-twa-generated/app/src/main/res/drawable/splash_enhanced.xml))
- ✅ **Chrome Preference** - Custom helper with fallback ([ChromePreferredCustomTabs.java](sample-pwa/android-twa/patches/ChromePreferredCustomTabs.java))
- ✅ **Digital Asset Links** - Pre-configured templates for fullscreen mode
- ✅ **Release Keystore** - Setup guide in [SETUP.md](sample-pwa/SETUP.md)
- ✅ **Bubblewrap CLI** - Automated TWA generation with proper prompts

### BubbleWrapper App (9.8 MB)
- ✅ **On-Device Keystore** - Generate signing keys directly on Android
- ✅ **APK Metadata Parser** - Extract manifests from uploaded APKs ([ManifestParser.kt](bubble-wrapper-app/app/src/main/java/xyz/bluefoot/bubblewrapper/util/ManifestParser.kt))
- ✅ **Auto-Fill Forms** - Parse embedded TWA manifests to pre-populate app details
- ✅ **Wallet Persistence** - MWA 2.0 session management with DataStore ([WalletManager.kt](bubble-wrapper-app/app/src/main/java/xyz/bluefoot/bubblewrapper/wallet/WalletManager.kt))
- ✅ **NFT Publishing** - Metaplex-compliant metadata with dApp Store spec v0.4.0 ([DappStorePublisher.kt](bubble-wrapper-app/app/src/main/java/xyz/bluefoot/bubblewrapper/network/DappStorePublisher.kt))
- ✅ **SHA-256 Fingerprint** - Extract from keystores for Digital Asset Links

## 🎨 Design System

| Variable | Color | Usage |
|----------|-------|-------|
| `--bg` | `#0B0F1A` | Background |
| `--panel` | `rgba(255,255,255,0.03)` | Glass cards |
| `--accent` | `#9945FF` | Solana purple |
| `--secondary` | `#14F195` | Solana green |
| `--text` | `#E6E9F2` | Primary text |

## 📱 Testing Checklist

- [ ] PWA installs from browser
- [ ] Offline mode works
- [ ] Splash screen shows gradient animation
- [ ] Safe-area padding on notched devices
- [ ] Back button navigates tabs
- [ ] Bottom nav active states work
- [ ] APK launches correctly
- [ ] Chrome fallback tested

## 🔗 Resources

- [Solana Mobile Docs](https://docs.solanamobile.com/dapp-publishing/intro)
- [Bubblewrap CLI](https://github.com/nicholasmorgan/nicholasmorgan)
- [PWA Best Practices](https://web.dev/progressive-web-apps/)
- [MWA Connection Guide](bubble-wrapper-app/docs/MWA_CONNECTION_GUIDE.md)

## 📄 License

This project is licensed under the **Apache License 2.0** - see the [LICENSE](LICENSE) file for details.

### Attribution Required

If you use BubbleWrapper in your project, please include attribution:

> **Built with [BubbleWrapper](https://github.com/QuarksBlueFoot/BubbleWrapper) by Bluefoot Labs**

See [NOTICE](NOTICE) for full attribution requirements.

---

<div align="center">

**Premium mobile-optimized PWAs for the Solana dApp Store**

🫧 **BubbleWrapper** by [Bluefoot Labs](https://bluefoot.xyz)

[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](LICENSE)

</div>
