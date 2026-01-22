# 🫧 Mobile-Optimized PWA Sample (Bubblewrap / TWA)

<div align="center">

**A premium sample app showcasing a highly mobile-optimized PWA for Solana Mobile's dApp Store**

[![Solana Mobile](https://img.shields.io/badge/Solana%20Mobile-Ready-9945FF?style=flat-square&logo=solana)](https://solanamobile.com)
[![PWA](https://img.shields.io/badge/PWA-Enabled-14F195?style=flat-square)](https://web.dev/progressive-web-apps/)

</div>

---

## ✅ Deliverables

### Sample App
- `web/` is a production-ready PWA (Vite + React + TypeScript)
- Premium 2025 UI with glassmorphism and Solana brand colors
- Thumb-friendly bottom navigation with active indicators
- Safe-area padding for notches and gesture bars

### Optimizations Included

| Feature | Implementation |
|---------|---------------|
| **Improved Splash Screen** | Animated gradient orbs, floating logo, dark theme (#0B0F1A) |
| **Chrome Browser Preference** | `android-twa/patches/ChromePreferredCustomTabs.java` |
| **Mobile Navigation** | Bottom nav, hash-based back button, 48dp tap targets |
| **Glassmorphism UI** | Frosted cards, gradient borders, Solana purple/green |

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
