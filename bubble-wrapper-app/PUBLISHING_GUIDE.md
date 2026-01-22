# Publishing Your App to Solana dApp Store via Android Studio

This guide walks you through building and publishing your Android app using Android Studio.

---

## Prerequisites

### Required Software

| Software | Version | Download |
|----------|---------|----------|
| Android Studio | Hedgehog (2023.1.1) or newer | [developer.android.com](https://developer.android.com/studio) |
| JDK | 17 or newer | Included with Android Studio |
| Solana Wallet | Any (Phantom, Solflare, Backpack) | Mobile app stores |

### Required Accounts

- **Solana dApp Store Publisher Account** at [publish.solanamobile.com](https://publish.solanamobile.com)
- **Solana Wallet** with ~0.2 SOL for transaction fees

---

## Step 1: Open Project in Android Studio

1. Launch **Android Studio**
2. Select **File → Open**
3. Navigate to your project folder (e.g., `/MyAwesomeApp/`)
4. Click **Open**
5. Wait for Gradle sync to complete (may take a few minutes)

### Configure API Keys (Optional but Recommended)

For better RPC reliability, set up a Helius API key:

1. Copy `local.properties.template` to `local.properties`
2. Sign up at [helius.dev](https://www.helius.dev/) (free tier available)
3. Add your API key:

```properties
HELIUS_API_KEY=your-api-key-here
```

> ⚠️ `local.properties` is gitignored and never committed. Keep your API key private.

### Verify Project Structure

```
your-app/
├── app/
│   ├── build.gradle.kts
│   ├── src/main/
│   │   ├── AndroidManifest.xml
│   │   ├── java/com/example/myapp/
│   │   │   ├── MainActivity.kt
│   │   │   ├── MyApp.kt
│   │   │   └── ui/
│   │   │       ├── theme/
│   │   │       └── screens/
│   │   └── res/
│   │       ├── values/
│   │       ├── drawable/
│   │       └── mipmap-*/
├── build.gradle.kts
├── settings.gradle.kts
└── gradle.properties
```

---

## Step 2: Configure App for Release

### Update Version Info

Edit `app/build.gradle.kts`:

```kotlin
android {
    defaultConfig {
        applicationId = "com.example.myapp"  // Use your own reverse-DNS package name
        versionCode = 1        // Increment for each release
        versionName = "1.0.0"  // User-facing version
    }
}
```

### Configure Language Support

**IMPORTANT**: Add supported locales to prevent rejection:

```kotlin
android {
    defaultConfig {
        // Add this line - specify ONLY languages you support
        resConfigs("en")  // English only
        // Or for multiple: resConfigs("en", "es", "pt")
    }
}
```

### Verify ProGuard Rules

Check `app/proguard-rules.pro` includes:

```proguard
# Keep Compose
-keep class androidx.compose.** { *; }

# Keep Kotlin serialization
-keepattributes *Annotation*
-keep class kotlin.Metadata { *; }
```

---

## Step 3: Generate Signing Key

### Create New Keystore

> ⚠️ **CRITICAL**: Create a NEW keystore specifically for the Solana dApp Store. Do NOT use a Google Play signing key.

1. In Android Studio: **Build → Generate Signed Bundle / APK**
2. Select **APK** → Next
3. Click **Create new...**

### Keystore Configuration

| Field | Value | Notes |
|-------|-------|-------|
| Key store path | `dappstore.keystore` | Save outside project folder |
| Password | Strong, unique | **SAVE THIS SECURELY** |
| Alias | `dappstore` | Or any memorable name |
| Key password | Same as above | Or different for extra security |
| Validity | 10000+ days | ~27+ years |
| Name | Your name or company | |
| Organization | Your organization | |
| Country | 2-letter code (US, UK, etc.) | |

4. Click **OK** to create keystore

### ⚠️ Keystore Security

| DO | DON'T |
|----|-------|
| ✅ Store in secure, encrypted location | ❌ Commit to git |
| ✅ Back up to multiple locations | ❌ Share passwords in plaintext |
| ✅ Document passwords in password manager | ❌ Use same key as Google Play |
| ✅ Keep offline backup copy | ❌ Store on shared drives |

> **WARNING**: Losing your keystore means you cannot update your app. There is NO recovery.

---

## Step 4: Build Signed APK

### Using Build Menu

1. **Build → Generate Signed Bundle / APK**
2. Select **APK** → Next
3. Choose your keystore:
   - Key store path: `path/to/dappstore.keystore`
   - Key store password: Your password
   - Key alias: `dappstore`
   - Key password: Your key password
4. Click **Next**
5. Select **release** build variant
6. Check **V1 (Jar Signature)** and **V2 (Full APK Signature)**
7. Click **Create**

### Output Location

The signed APK will be at:
```
bubble-wrapper-app/app/release/app-release.apk
```

### Alternative: Command Line

```bash
cd bubble-wrapper-app
./gradlew assembleRelease
```

APK location: `app/build/outputs/apk/release/app-release.apk`

---

## Step 5: Verify APK

### Check Signature

```bash
# Verify APK is signed correctly
apksigner verify --print-certs app/release/app-release.apk
```

Expected output should show your certificate info.

### Test on Device

```bash
# Install on connected device
adb install app/release/app-release.apk
```

Or drag-and-drop the APK onto an emulator.

### Verification Checklist

- [ ] App launches correctly
- [ ] All screens navigate properly
- [ ] Guide content displays correctly
- [ ] Theme colors match (dark purple theme)
- [ ] No crashes or errors

---

## Step 6: Prepare Store Assets

### Required Assets

Create these graphics for your listing:

| Asset | Dimensions | Format | File |
|-------|------------|--------|------|
| App Icon | 512 x 512 px | PNG | `icon-512.png` |
| Banner | 1200 x 600 px | PNG/JPG | `banner.png` |
| Screenshot 1 | 1920 x 1080 px | PNG | `screenshot-1.png` |
| Screenshot 2 | 1920 x 1080 px | PNG | `screenshot-2.png` |
| Screenshot 3 | 1920 x 1080 px | PNG | `screenshot-3.png` |
| Screenshot 4 | 1920 x 1080 px | PNG | `screenshot-4.png` |

### Screenshot Recommendations

1. **Home Screen** - Show the main interface
2. **Guide Screen** - Show the in-app documentation
3. **Build Screen** - Show command generation
4. **Docs Screen** - Show available guides

### Taking Screenshots

Using Android Studio:
1. Run app on emulator (Pixel 6 Pro recommended)
2. Navigate to desired screen
3. Click camera icon in **Logcat** toolbar
4. Save as PNG

---

## Step 7: Submit to dApp Store

### Access Publisher Portal

1. Navigate to [publish.solanamobile.com](https://publish.solanamobile.com)
2. Connect your Solana wallet
3. Complete publisher verification (if first time)

### Create New App Listing

1. Click **"Add new app"**
2. Fill in app details:

| Field | Value |
|-------|-------|
| App Name | Your App Name |
| Short Description | Brief description of your app (max 80 chars) |
| Full Description | Detailed description of your app features and benefits. |
| Category | Choose appropriate category |
| Support Email | your-email@domain.com |
| Privacy Policy URL | https://your-domain.com/privacy |

### Upload Assets

1. Upload app icon (512x512)
2. Upload banner (1200x600)
3. Upload 4+ screenshots
4. Add optional promotional video

### Submit Release

1. Click **"New Version"**
2. Upload `app-release.apk`
3. Add release notes:
   ```
   Initial Release v1.0.0
   - Your main feature 1
   - Your main feature 2
   - Bug fixes and improvements
   ```
4. Click **Submit**

### Sign Transactions

> ⚠️ **IMPORTANT**: Approve ALL wallet signing requests!

The submission process requires multiple signatures:
1. App metadata transaction
2. Asset upload transactions
3. Release NFT minting

**Skipping any signature may cause missing assets or failed submission.**

---

## Step 8: Post-Submission

### Review Timeline

- **Typical review**: 1-3 business days
- **Email notification**: Sent on approval/rejection
- **Portal status**: Check anytime at publisher portal

### If Rejected

Common reasons:
1. Debug build submitted (use release)
2. Missing screenshots (need 4+)
3. Policy violation
4. Incorrect locale configuration

Fix issues and resubmit.

### If Approved 🎉

- App appears in Solana dApp Store
- Available for Saga and Seeker devices
- Monitor ratings and feedback
- Plan regular updates

---

## Updating Your App

### Version Bump

1. Increment `versionCode` (must be higher than previous)
2. Update `versionName` for users

```kotlin
defaultConfig {
    versionCode = 2        // Was 1
    versionName = "1.1.0"  // Was 1.0.0
}
```

### Build New APK

1. **Build → Generate Signed Bundle / APK**
2. Use **SAME keystore** as original release
3. Select release variant
4. Generate APK

### Submit Update

1. Go to Publisher Portal
2. Select your app
3. Click **"New Version"**
4. Upload new APK
5. Add release notes
6. Submit with **same wallet**

---

## Troubleshooting### "Main Class Not Found" or "Application" Config Only
If you see an error like `Error: Could not find or load main class com.example.myapp`, you are trying to run the app as a Java Application instead of an Android App. This usually means Android Studio hasn't finished syncing or you are trying to run the wrong module.

**Fix:**
1. **Wait for Gradle Sync**: Look at the bottom right status bar. If it says "Gradle Sync", wait for it to finish.
2. **Sync with Gradle Files**: File → Sync Project with Gradle Files.
3. **Select 'app' configuration**: In the top toolbar, the dropdown should say "app". If it helps "Edit Configurations", click the **+ (Plus)** button and select **Android App**.
   - If **Android App** is missing from the list, the project did not import correctly. Close Android Studio and reopen the project by selecting the `bubble-wrapper-app` folder specifically (containing `build.gradle.kts`).
4. **Select a Device**: Ensure an Emulator or Physical Device is selected next to the Run button.

### "Build was configured to prefer settings repositories"
If you see `Build was configured to prefer settings repositories over project repositories but repository 'Google' was added by build file`, it means `build.gradle.kts` is trying to define repositories that are already handled centrally.

**Fix:**
Open `build.gradle.kts` (the one in the root folder, not inside `app/`) and remove the `allprojects { repositories { ... } }` block entirely. The `settings.gradle.kts` file already handles this.

### "Indexing..." / "Scanning files to index..." takes forever
You likely opened a parent folder that includes extra files (like web assets, `node_modules`, etc.) which Android Studio tries to index.

**Fix:**
1. **File → Close Project**.
2. **File → Open**.
3. Select only your Android app folder (containing `build.gradle.kts`).
4. Click **Open**.

This will only load the Android project and ignore other files.

### "Task 'wrapper' not found in project ':app'"
This error occurs if the Gradle Wrapper task is missing from the build configuration or you are trying to run it specifically on the app module.

**Fix:**
We have added the wrapper task to the root `build.gradle.kts`.
1. **Sync Project**: File → Sync Project with Gradle Files.
2. If running from command line, ensure you use the root project: `./gradlew wrapper` (not inside the app folder).

### Gradle Sync Failed

```bash
# Clear caches
./gradlew clean
./gradlew --stop

# Invalidate Android Studio caches
# File → Invalidate Caches → Invalidate and Restart
```

### Build Errors

| Error | Solution |
|-------|----------|
| SDK not found | Install via SDK Manager |
| JDK version mismatch | Use JDK 17 (Preferences → Build → Gradle) |
| Dependency resolution | Check internet, try `--refresh-dependencies` |

### Signing Errors

| Error | Solution |
|-------|----------|
| Keystore not found | Check file path |
| Wrong password | Verify password spelling |
| Key not found | Check alias name |

### App Rejected

| Reason | Fix |
|--------|-----|
| Debug build | Build release variant |
| Missing assets | Add 4+ screenshots, banner |
| Wrong key | Use separate key from Play Store |
| Locale config | Add `resConfigs` in build.gradle |

---

## Quick Reference

### Key Files

| File | Purpose |
|------|---------|
| `app/build.gradle.kts` | App configuration, dependencies |
| `AndroidManifest.xml` | App permissions, components |
| `dappstore.keystore` | Release signing key (keep secure!) |
| `app-release.apk` | Uploadable signed APK |

### Key Commands

```bash
# Build debug APK
./gradlew assembleDebug

# Build release APK
./gradlew assembleRelease

# Install on device
adb install app/release/app-release.apk

# View connected devices
adb devices

# Clean build
./gradlew clean
```

### Useful Links

- [Solana dApp Store Docs](https://docs.solanamobile.com/dapp-publishing/intro)
- [Android Studio Download](https://developer.android.com/studio)
- [Publisher Portal](https://publish.solanamobile.com)

---

*Guide by [Bluefoot Labs](https://bluefoot.xyz) - Created with the Bubble Wrapper project*
