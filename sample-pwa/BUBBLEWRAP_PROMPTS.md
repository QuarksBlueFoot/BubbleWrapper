# Bubblewrap CLI - Exact Prompt Answers

Use these **exact values** when running `bubblewrap init` to ensure your Android TWA matches the mobile-optimized PWA theme.

## Prerequisites

```bash
npm install -g @bubblewrap/cli
cd sample-pwa/android-twa-generated
```

## Command

```bash
bubblewrap init --manifest=https://YOUR_DOMAIN/manifest.webmanifest
```

Replace `YOUR_DOMAIN` with your actual hosting domain (e.g., `bubblewrapper.bluefootlabs.com`)

---

## Section 1: Web App Details

```
Domain: bubblewrapper.bluefoot.xyz
```
> Replace with your actual domain (without https://)

```
URL path: /
```
> Root path - the PWA starts at the domain root

---

## Section 2: Android App Details

```
Application name: Mobile-Optimized PWA Sample
```
> Full name shown in app info and Play Store

```
Short name: PWA Sample
```
> Max 12 characters, shown on launcher

```
Application ID: xyz.bluefoot.bubblewrapper.sample
```
> Reverse DNS format (must be unique in Play Store)

```
Starting version code: 1
```
> Numeric version (increment for each release)

```
Display mode: standalone
```
> Hides browser UI for app-like experience

```
Orientation: portrait
```
> Mobile-first, locks to portrait mode

```
Status bar color: #0B0F1A
```
> ⚠️ CRITICAL: Must match PWA theme (dark background)

---

## Section 3: Launcher Icons and Splash Screen

```
Splash screen color: #0B0F1A
```
> ⚠️ CRITICAL: Must match PWA theme to avoid white flash

```
Icon URL: https://bubblewrapper.bluefoot.xyz/icons/icon-512.png
```
> Replace domain, keep path (512x512 px icon)

```
Maskable Icon URL: https://bubblewrapper.bluefoot.xyz/icons/icon-512-maskable.png
```
> Replace domain, keep path (adaptive icon with safe zone)

---

## Section 4: Optional Features

```
Monochrome icon URL: (leave empty, press Enter)
```
> Not required for this sample

```
Include support for Play Billing? No
```
> Not needed for dApp Store PWAs

```
Request geolocation permission? No
```
> Only enable if your PWA needs it

---

## Section 5: Signing Key Information

```
Key store location: ./android.keystore
```
> Local keystore (will be created if doesn't exist)

```
Key name: android
```
> Key alias within the keystore

### If Creating New Keystore:

```
First and Last names: Your Name
Organizational Unit: Your Team/Dept
Organization: Your Company
Country (2 letter code): US
Password for the Key Store: [create strong password]
Password for the Key: [create strong password]
```

> ⚠️ **IMPORTANT**: Save these passwords securely! You'll need them for app updates.

---

## Verification

After initialization completes, verify these settings in `twa-manifest.json`:

```json
{
  "themeColor": "#0B0F1A",
  "backgroundColor": "#0B0F1A",
  "navigationColor": "#0B0F1A",
  "orientation": "portrait",
  "display": "standalone"
}
```

All colors should be `#0B0F1A` (the dark theme) to match the PWA.

---

## Common Mistakes to Avoid

❌ **Wrong status bar color** - Using `#2196F3` (blue) instead of `#0B0F1A` (dark)
   - Causes white flash on app launch

❌ **Wrong splash color** - Not matching the PWA background
   - Creates jarring transition during startup

❌ **Forgetting HTTPS** - Domain must use https:// in production
   - Digital Asset Links require HTTPS

❌ **Wrong icon URLs** - Using placeholder instead of actual hosted icons
   - Icons won't load in the generated APK

---

## Quick Copy-Paste Block

For fastest setup, copy this entire block and paste when prompted:

```
bubblewrapper.bluefoot.xyz
/
Mobile-Optimized PWA Sample
PWA Sample
xyz.bluefoot.bubblewrapper.sample
1
standalone
portrait
#0B0F1A
#0B0F1A
https://bubblewrapper.bluefoot.xyz/icons/icon-512.png
https://bubblewrapper.bluefoot.xyz/icons/icon-512-maskable.png

No
No
./android.keystore
android
```

> Note: Adjust domain URLs to match your actual hosting

---

## After Initialization

1. **Verify colors** in `twa-manifest.json`
2. **Copy Chrome helper**: `cp ../android-twa/patches/ChromePreferredCustomTabs.java app/src/main/java/.../`
3. **Build APK**: `bubblewrap build`
4. **Test**: `bubblewrap install` or `adb install app-release-signed.apk`
