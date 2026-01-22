# Publishing a PWA to the Solana dApp Store

This guide covers converting your Progressive Web App (PWA) into an Android app using Bubblewrap CLI and publishing it to the Solana Mobile dApp Store.

> **Based on official Solana Mobile documentation**: This guide extends and improves upon the [official Solana Mobile PWA publishing guide](https://docs.solanamobile.com/dapp-publishing/publishing-a-pwa).

## 📚 Additional Guides

- **[dApp Store Submission Checklist](./DAPP_STORE_CHECKLIST.md)** - Complete submission checklist with all requirements
- **[Digital Asset Links Guide](./DIGITAL_ASSET_LINKS.md)** - Detailed DAL setup for fullscreen TWA

---

## Overview

Progressive Web Apps (PWAs) can be published on the Solana dApp Store by wrapping them in a **Trusted Web Activity (TWA)**. TWAs use Chrome to render the web app, providing a full-screen, native-like experience without any browser UI.

### What You'll Create

1. A mobile-optimized PWA (this sample provides a ready template)
2. An Android APK wrapped via Bubblewrap CLI
3. Digital Asset Links for TWA verification
4. Submission-ready assets for the dApp Store

---

## Prerequisites

### Required Tools

- **Node.js 18+** (Bubblewrap requires Node 14.15.0+, but 18+ is recommended)
- **Java 17+** (for Android builds)
- **Android SDK Build Tools** (installed automatically by Bubblewrap if needed)

### Install Bubblewrap CLI

```bash
npm install -g @bubblewrap/cli
bubblewrap --version
```

### PWA Requirements

Your PWA must have:

- A valid [web manifest](https://developer.mozilla.org/en-US/docs/Web/Manifest) at a public HTTPS URL
- At minimum: `name`, `short_name`, `icons`, `start_url`, `display`, `theme_color`, `background_color`

---

## Step 1: Prepare Your PWA

### Web Manifest Template

Your `manifest.webmanifest` should include:

```json
{
  "name": "Solana Mobile PWA Sample",
  "short_name": "Solana PWA",
  "description": "A premium mobile-optimized PWA for the Solana dApp Store",
  "start_url": "/",
  "scope": "/",
  "display": "standalone",
  "orientation": "portrait",
  "theme_color": "#0B0F1A",
  "background_color": "#0B0F1A",
  "icons": [
    {
      "src": "/icons/icon-192.png",
      "sizes": "192x192",
      "type": "image/png"
    },
    {
      "src": "/icons/icon-512.png",
      "sizes": "512x512",
      "type": "image/png"
    },
    {
      "src": "/icons/icon-512-maskable.png",
      "sizes": "512x512",
      "type": "image/png",
      "purpose": "maskable"
    }
  ]
}
```

### Critical: Color Consistency

⚠️ **The most common mistake** is mismatched colors between your PWA and TWA manifest.

| Setting | PWA Manifest | TWA Manifest | HTML |
|---------|--------------|--------------|------|
| Theme Color | `theme_color` | `themeColor` | `<meta name="theme-color">` |
| Background | `background_color` | `backgroundColor` | CSS `body { background: }` |
| Splash | N/A | `splashScreenBackgroundColor` | Splash screen CSS |

All values should be **identical** (e.g., `#0B0F1A`) to prevent white flash on app launch.

---

## Step 2: Initialize Bubblewrap Project

Create a new directory and initialize:

```bash
mkdir my-twa-project
cd my-twa-project
bubblewrap init --manifest=https://bubblewrapper.bluefoot.xyz/manifest.webmanifest
```

### Configuration Prompts

When prompted, use these optimized values:

| Prompt | Recommended Value | Notes |
|--------|-------------------|-------|
| **Domain** | `bubblewrapper.bluefoot.xyz` | Your actual hosting domain |
| **URL path** | `/` | Root path for most apps |
| **Application name** | `Solana Mobile PWA Sample` | Full name (Play Store) |
| **Short name** | `Solana PWA` | Max 12 characters |
| **Application ID** | `xyz.bluefoot.bubblewrapper.sample` | Reverse DNS format |
| **Display mode** | `standalone` | Hides browser UI |
| **Orientation** | `portrait` | Mobile-first |
| **Status bar color** | `#0B0F1A` | **Must match PWA theme** |
| **Splash screen color** | `#0B0F1A` | **Must match PWA background** |

### Keystore Warning

> ⚠️ **CRITICAL**: Bubblewrap will create an Android Keystore for signing.
> - **Save the keystore file and password securely**
> - Losing them means you **cannot update your app**
> - Consider following [Google's keystore best practices](https://developer.android.com/studio/publish/app-signing#secure_key)

---

## Step 3: Configure Language Support

**Do not skip this step!** By default, Bubblewrap incorrectly declares support for all locales.

Edit `build.gradle` in the generated Android project:

```gradle
android {
    defaultConfig {
        ...
        resConfigs "en"  // Add your supported locales
    }
}
```

See [Android documentation](https://developer.android.com/guide/topics/resources/multilingual-support#specify-the-languages-your-app-supports) for details.

---

## Step 4: Build the APK

Build the signed release APK:

```bash
bubblewrap build
```

This creates:
- `app-release-signed.apk` - Your submission-ready APK
- `app-release-unsigned.apk` - Unsigned version

If prompted to install Android SDK components, allow it.

---

## Step 5: Configure Digital Asset Links

Digital Asset Links (DAL) establish trust between your website and Android app. **This is required for full-screen TWA without browser UI.**

### Generate Fingerprint

```bash
keytool -list -v -keystore android.keystore
```

Copy the SHA256 fingerprint.

### Add to TWA Manifest

```bash
bubblewrap fingerprint add <YOUR_SHA256_FINGERPRINT>
```

### Generate Asset Links File

```bash
bubblewrap fingerprint generateAssetLinks
```

### Host Asset Links

Upload the generated `assetlinks.json` to:

```
https://your-domain.com/.well-known/assetlinks.json
```

Example content:
```json
[{
  "relation": ["delegate_permission/common.handle_all_urls"],
  "target": {
    "namespace": "android_app",
    "package_name": "xyz.bluefoot.bubblewrapper.sample",
    "sha256_cert_fingerprints": ["YOUR_SHA256_FINGERPRINT"]
  }
}]
```

---

## Step 6: Test Your App

Install on a device or emulator:

```bash
bubblewrap install
# OR
adb install app-release-signed.apk
```

### Verification Checklist

- [ ] App launches in full-screen (no browser UI)
- [ ] Splash screen matches app theme (no white flash)
- [ ] Icons display correctly (no cropping)
- [ ] Offline functionality works
- [ ] Back button navigates correctly

> **Troubleshooting**: If you see browser UI (URL bar), your Digital Asset Links are misconfigured. Verify the SHA256 fingerprint matches your signed APK.

---

## Step 7: Prepare dApp Store Assets

The Solana dApp Store requires:

| Asset | Dimensions | Notes |
|-------|------------|-------|
| **App Icon** | 512x512 px | Follow [Google Play icon spec](https://developer.android.com/distribute/google-play/resources/icon-design-specifications) |
| **Banner Graphic** | 1200x600 px | Required for all apps |
| **Feature Graphic** | 1200x1200 px | Optional, for Editor's Choice |
| **Screenshots** | 1920x1080 px | Minimum 4 images/videos |
| **Videos** | 720p+ MP4 | Optional, must be `.mp4` |

---

## Step 8: Submit to dApp Store

1. **Visit** the [Solana dApp Store Publisher Portal](https://publisher.solanamobile.com)
2. **Connect** your Solana wallet (this becomes your publisher identity)
3. **Ensure** wallet has ~0.2 SOL for transaction and storage fees
4. **Upload** your APK and assets
5. **Fill** app metadata (description, category, etc.)
6. **Submit** for review

> **Recommendation**: Use **ArDrive** for storage (lower costs, simpler setup).

---

## Updating Your App

To release updates:

1. Edit `twa-manifest.json` with changes
2. Run `bubblewrap update` to regenerate Android project
3. Run `bubblewrap build` for new APK
4. Submit update via Publisher Portal

```bash
bubblewrap update --manifest=./twa-manifest.json
bubblewrap build
```

---

## Chrome Preference with Fallback

For external links within your TWA, use the included helper:

**File**: `android-twa/patches/ChromePreferredCustomTabs.java`

```java
// In your Activity
ChromePreferredCustomTabs.launch(this, Uri.parse("https://docs.solanamobile.com"));
```

This:
1. Attempts to launch Chrome Custom Tabs
2. Falls back to system default browser if Chrome unavailable
3. Provides smooth, native-feeling link handling

---

## Mobile Optimization Checklist

This sample implements all recommended optimizations:

### UI/UX
- [x] **Bottom navigation** - Thumb-friendly, reduces back-button dependency
- [x] **Safe-area padding** - Works with notches and gesture bars
- [x] **Large tap targets** - Minimum 48x48dp touch areas
- [x] **Glassmorphism design** - 2025 premium styling standards
- [x] **Solana brand colors** - Purple (#9945FF) and green (#14F195)

### Performance
- [x] **Service Worker** - Offline support via Workbox
- [x] **Precaching** - Critical assets cached on install
- [x] **Code splitting** - Vite-optimized bundles
- [x] **Lazy loading** - Components loaded on demand

### PWA Features
- [x] **Web manifest** - Complete with maskable icons
- [x] **Theme consistency** - No white flash on launch
- [x] **Install prompt** - Native-feeling install experience
- [x] **Auto-update** - Service worker updates automatically

---

## Resources

### Official Documentation
- [Solana Mobile: Publishing a PWA](https://docs.solanamobile.com/dapp-publishing/publishing-a-pwa)
- [Solana Mobile: Submit New App](https://docs.solanamobile.com/dapp-publishing/submit-new-app)
- [Solana Mobile: Publisher Policy](https://docs.solanamobile.com/dapp-publishing/publisher-policy)

### Tools
- [Bubblewrap CLI](https://github.com/GoogleChromeLabs/bubblewrap)
- [PWABuilder](https://www.pwabuilder.com/)
- [Maskable Icon Editor](https://maskable.app/editor)

### Guides
- [Android Digital Asset Links](https://developer.android.com/training/app-links/verify-android-applinks)
- [Web Manifest Specification](https://web.dev/articles/add-manifest)
- [Chrome TWA Overview](https://developer.chrome.com/docs/android/trusted-web-activity)

---

## Troubleshooting

### White Flash on Launch
- Ensure all color values match: PWA manifest, TWA manifest, HTML/CSS
- Verify `splashScreenBackgroundColor` equals `backgroundColor`

### Browser UI Visible (URL Bar)
- Digital Asset Links not configured correctly
- SHA256 fingerprint mismatch between assetlinks.json and APK
- Asset links file not accessible at `/.well-known/assetlinks.json`

### Icons Look Cropped
- Use maskable icons with proper safe zone
- Test with [Maskable Icon Editor](https://maskable.app/editor)

### Back Button Exits Immediately
- Implement hash-based navigation (see App.tsx in this sample)
- Use `history.pushState()` on navigation events

---

🫧 **BubbleWrapper** by [Bluefoot Labs](https://bluefoot.xyz)
