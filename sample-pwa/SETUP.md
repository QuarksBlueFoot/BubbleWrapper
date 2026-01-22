# Bubblewrap Mobile PWA Sample - Setup Guide

This is a **production-ready, mobile-optimized PWA** built for Solana Mobile's dApp Store submission using the Bubblewrap CLI.

## ✅ What's Included

### Mobile Optimizations Implemented:
1. **Improved Splash Screen** - Dark theme (#0B0F1A) with smooth fade-out, app icon, and loading spinner
2. **Chrome Browser Preference** - Custom Tabs helper that defaults to Chrome, falls back to system browser
3. **Mobile-Intuitive Navigation** - Bottom nav bar, safe-area padding, large tap targets, gesture-friendly layout

## Prerequisites

- Node.js 18+
- Java 17+ (for Android builds)
- Android Studio (recommended)
- Bubblewrap CLI: `npm install -g @bubblewrap/cli`

## Quick Start (Development)

```bash
cd web
npm install
npm run dev
```

Visit `http://localhost:5173` to see the PWA in action.

## Build for Production

```bash
cd web
npm run build
# Output will be in web/dist/
```

Deploy `web/dist/` to your hosting provider (must be HTTPS).

## Creating the Android TWA Wrapper

### Option 1: Automated with Example Manifest

```bash
cd android-twa-generated

# Copy the example manifest with correct settings
cp ../android-twa/twa-manifest.example.json twa-manifest.json

# Edit twa-manifest.json and update:
# - host: YOUR_ACTUAL_DOMAIN
# - iconUrl: https://YOUR_ACTUAL_DOMAIN/icons/icon-512.png
# - maskableIconUrl: https://YOUR_ACTUAL_DOMAIN/icons/icon-512-maskable.png
# - webManifestUrl: https://YOUR_ACTUAL_DOMAIN/manifest.webmanifest
# - fullScopeUrl: https://YOUR_ACTUAL_DOMAIN/

# Then build directly
bubblewrap build
```

### Option 2: Interactive CLI Init

```bash
cd android-twa-generated
bubblewrap init --manifest=https://YOUR_DOMAIN/manifest.webmanifest
```

**IMPORTANT: Use these exact values when prompted:**

| Prompt | Value | Notes |
|--------|-------|-------|
| Domain | `bubblewrapper.bluefoot.xyz` | Replace with your actual domain |
| URL path | `/` | Root path |
| Application name | `Mobile-Optimized PWA Sample` | Full app name |
| Short name | `PWA Sample` | Max 12 chars |
| Application ID | `xyz.bluefoot.bubblewrapper.sample` | Reverse DNS format |
| Display mode | `standalone` | Hides browser UI |
| Orientation | `portrait` | Mobile-first |
| Status bar color | `#0B0F1A` | Dark theme (MUST match PWA) |
| Splash screen color | `#0B0F1A` | Dark theme (MUST match PWA) |
| Icon URL | `https://YOUR_DOMAIN/icons/icon-512.png` | 512x512 px |
| Maskable Icon URL | `https://YOUR_DOMAIN/icons/icon-512-maskable.png` | 512x512 px |

### Apply Chrome Preference Patch

After generating the Android project:

1. Copy the Chrome preference helper:
```bash
cp ../android-twa/patches/ChromePreferredCustomTabs.java \
   app/src/main/java/xyz/bluefoot/bubblewrapper/sample/
```

2. Use it in your launcher activity for external links:
```java
import android.net.Uri;
ChromePreferredCustomTabs.launch(this, Uri.parse("https://docs.example.com"));
```

## Building the APK

```bash
cd android-twa-generated
bubblewrap build
```

Output: `app-release-signed.apk` (or similar)

## Installing on Device

```bash
bubblewrap install
# OR
adb install app-release-signed.apk
```

## Digital Asset Links (Required for Full Trust)

Generate asset links file:
```bash
bubblewrap fingerprint
```

Host the generated `.well-known/assetlinks.json` at your domain's root:
```
https://YOUR_DOMAIN/.well-known/assetlinks.json
```

## Verification Checklist

Before submission, verify:

- [ ] PWA hosted over HTTPS
- [ ] Manifest accessible at `/manifest.webmanifest`
- [ ] All colors match (#0B0F1A for dark theme)
- [ ] Maskable icons included (no cropping issues)
- [ ] Service worker registered (offline support)
- [ ] Safe-area padding works on notched devices
- [ ] Bottom navigation is thumb-friendly
- [ ] Back button navigates between tabs
- [ ] Splash screen matches app theme
- [ ] Asset links file hosted and verified
- [ ] APK signed with release keystore

## Key Files Reference

```
sample-pwa/
├── web/                          # PWA source
│   ├── src/
│   │   ├── App.tsx              # Main app with back button handling
│   │   ├── components/
│   │   │   ├── BottomNav.tsx    # Mobile bottom navigation
│   │   │   └── TopBar.tsx       # Top app bar
│   │   └── styles.css           # Mobile-optimized styles
│   ├── index.html               # With improved splash screen
│   └── vite.config.ts           # PWA manifest config
├── android-twa/
│   ├── twa-manifest.example.json    # Pre-filled manifest
│   └── patches/
│       └── ChromePreferredCustomTabs.java  # Chrome fallback helper
└── docs/
    └── bubblewrap.md            # Detailed TWA guide
```

## Troubleshooting

### White Flash on Launch
- Ensure `backgroundColor` and `themeColor` in manifest match your CSS
- Set `<meta name="theme-color" content="#0B0F1A">` in HTML

### Icons Look Cropped
- Use maskable icons with safe zone padding
- Test with Android Adaptive Icons preview tool

### TWA Doesn't Launch
- Verify Digital Asset Links are set up correctly
- Check that manifest URL is accessible
- Ensure package ID matches in both manifest and assetlinks.json

### Back Button Exits App Immediately
- Check that hash-based routing is implemented (already done in this sample)
- Verify `window.history.pushState` is called on navigation

## Support & Resources

- [Bubblewrap CLI Docs](https://github.com/GoogleChromeLabs/bubblewrap)
- [PWA Best Practices](https://web.dev/progressive-web-apps/)
- [Android Digital Asset Links](https://developer.android.com/training/app-links/verify-android-applinks)

---

🫧 **BubbleWrapper** by [Bluefoot Labs](https://bluefoot.xyz)
