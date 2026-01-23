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

- **Glassmorphism UI** - Frosted glass cards with `backdrop-filter: blur(20px)`, animated gradient borders
- **Solana Brand Colors** - `#9945FF` (purple), `#14F195` (green)
- **Animated Splash Screen** - Dark theme (#0B0F1A) with gradient orbs and glow effects
- **Chrome Browser Preference** - Custom helper prefers Chrome with graceful fallback
- **Mobile-First Navigation** - Bottom nav bar with safe-area padding and large tap targets

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

This repository includes a full example PWA called **MonkeMob** located in `sample-pwa/`. The project contains:
- `sample-pwa/web/` — production-ready PWA (Vite + React + TypeScript).
- `sample-pwa/android-twa/` — Bubblewrap/TWA wrapper source and example manifests (MonkeMob-specific configs).
- `sample-pwa/android-twa-generated/` — generated Android project and resources (icons, splash, launcher).

Use the MonkeMob sample as a template for building and publishing your own TWA apps.

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
- ✅ Service Worker with offline support (Workbox)
- ✅ Web App Manifest with proper icons
- ✅ Maskable icons (adaptive on Android)
- ✅ Theme color consistency
- ✅ Install prompt ready

### Premium Mobile UX
- ✅ Bottom navigation (Material Design 3)
- ✅ Safe-area insets (notch/island support)
- ✅ Large touch targets (48dp+)
- ✅ Glassmorphism card components
- ✅ Animated gradient backgrounds
- ✅ Hash-based back button navigation

### TWA Optimizations
- ✅ Premium animated splash screen
- ✅ Chrome preference with fallback
- ✅ Digital Asset Links ready
- ✅ Release keystore setup guide

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
