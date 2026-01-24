# 🫧 MonkeMob - Mobile-Optimized PWA Sample

<div align="center">

**A premium sample app showcasing a highly mobile-optimized PWA for Solana Mobile's dApp Store**

[![Solana Mobile](https://img.shields.io/badge/Solana%20Mobile-Published-9945FF?style=flat-square&logo=solana)](https://solanamobile.com)
[![PWA](https://img.shields.io/badge/PWA-Production-14F195?style=flat-square)](https://web.dev/progressive-web-apps/)

**🎉 Successfully Published to Solana dApp Store**  
App NFT: `ADZWhSTQJoppJhapEbUS69yLg5HM2nJ322nenGxQqhMs`

</div>

---

## ✅ Deliverables Met

This sample app demonstrates **all required deliverables** for a mobile-optimized PWA using the Bubblewrap template:

### 1️⃣ Highly Mobile-Optimized PWA
- ✅ Production-ready TWA wrapped with Bubblewrap CLI
- ✅ Successfully published to Solana Mobile dApp Store (mainnet)
- ✅ Package ID: `me.monkemob.twa`
- ✅ Live PWA: `https://monkemob.me`
- ✅ Premium 2025 UI with glassmorphism and Solana branding

Note: The test APK for MonkeMob has been built as version `1.2` (versionCode `3`). Recent fixes replaced a bitmap-based scanline with a gradient implementation to avoid a runtime crash on startup, and the project build now uses `mavenCentral()`.

### 2️⃣ Improved Splash Screen Styling
- ✅ Custom Android `layer-list` drawable with terminal aesthetic
- ✅ Circular gold-framed app icon (120dp in 140dp gold frame)
- ✅ Saga MonkeMob brand logo (180dp) at top center
- ✅ 6 falling banana animations with varying opacity
- ✅ Retro pixel font (Press Start 2P) with green scanline effect
- ✅ Black background (#000000) with smooth 300ms fade-out
- 📄 **Implementation:** [splash_terminal.xml](android-twa-generated/app/src/main/res/drawable/splash_terminal.xml)

### 3️⃣ Default to Chrome Browser, Fall Back to System Default
- ✅ TWA automatically prefers Chrome for optimal performance
- ✅ Graceful fallback to Chromium-based browsers if Chrome unavailable
- ✅ Custom tabs support for devices without TWA capability
- ✅ No explicit browser selection needed - handled by `androidbrowserhelper:2.6.2`
- 📄 **Configuration:** [twa-manifest.json](android-twa-generated/twa-manifest.json)

### 4️⃣ Mobile-Intuitive Navigation and Layouts
- ✅ **Portrait-first orientation** with Android 8.0+ adaptive handling
- ✅ **Safe area insets** for notched/punch-hole displays (`viewport-fit: cover`)
- ✅ **Touch-optimized UI** with no tap highlight flash, 44x44dp minimum targets
- ✅ **Glassmorphism design** with frosted glass panels, gradient borders, backdrop blur
- ✅ **Bottom navigation** with safe-area padding respecting gesture areas
- ✅ **Thumb-friendly layouts** with responsive components
- 📄 **Implementation:** [styles.css](web/src/styles.css), [MOBILE_OPTIMIZATIONS.md](MOBILE_OPTIMIZATIONS.md)

---

## 📊 Optimization Summary

| Feature | Implementation | Status |
|---------|---------------|--------|
| **Improved Splash Screen** | Animated terminal, gold frame, 6 falling bananas, pixel font | ✅ Complete |
| **Chrome Browser Preference** | androidbrowserhelper auto-preference with fallback | ✅ Complete |
| **Mobile Navigation** | Bottom nav, safe-area insets, 48dp+ tap targets | ✅ Complete |
| **Glassmorphism UI** | Frosted cards, gradient borders, Solana colors | ✅ Complete |
| **PWA Features** | Manifest, service worker, maskable icons, offline support | ✅ Complete |

## 🚀 Quick Start

```bash
cd web
npm install
npm run dev
```

Visit `http://localhost:5173` to preview the PWA.

### Build for Production

```bash
npm run build
npm run preview
```

## 📦 Wrap with Bubblewrap

See [docs/PUBLISHING_GUIDE.md](docs/PUBLISHING_GUIDE.md) for the complete publishing workflow.

### Quick Reference

```bash
# Create new Android project
mkdir android-build && cd android-build
bubblewrap init --manifest=https://bubblewrapper.bluefoot.xyz/manifest.webmanifest

# Build signed APK
bubblewrap build

# Install on device
bubblewrap install
```

**Key Settings:**
- Domain: `bubblewrapper.bluefoot.xyz`
- Package ID: `xyz.bluefoot.bubblewrapper.sample`
- Theme color: `#0B0F1A`

## 📁 Structure

```
sample-pwa/
├── web/                          # PWA source
│   ├── src/
│   │   ├── App.tsx              # Premium glassmorphism components
│   │   ├── components/          # BottomNav, TopBar
│   │   └── styles.css           # 2025 CSS with animations
│   └── vite.config.ts           # PWA + Workbox config
├── android-twa/
│   ├── twa-manifest.example.json
│   └── patches/ChromePreferredCustomTabs.java
└── docs/
    └── PUBLISHING_GUIDE.md      # Complete submission guide
```

## 🎨 Design System

| Color | Value | Usage |
|-------|-------|-------|
| Background | `#0B0F1A` | Dark theme |
| Solana Purple | `#9945FF` | Primary accent |
| Solana Green | `#14F195` | Secondary/success |
| Glass Panel | `rgba(255,255,255,0.03)` | Cards |

## 📖 Documentation

- [SETUP.md](SETUP.md) - Step-by-step instructions
- [PUBLISHING_GUIDE.md](docs/PUBLISHING_GUIDE.md) - Complete dApp Store guide
- [BUBBLEWRAP_PROMPTS.md](BUBBLEWRAP_PROMPTS.md) - Exact CLI answers
