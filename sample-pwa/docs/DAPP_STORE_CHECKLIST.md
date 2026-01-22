# Complete dApp Store Submission Checklist

This is a comprehensive checklist based on [Solana Mobile's official documentation](https://docs.solanamobile.com) with additional tips and clarifications that the official docs don't always make clear.

---

## Pre-Submission Requirements

### ✅ APK Requirements

- [ ] **Release-signed APK** (not debug build)
- [ ] **Unique signing key** - MUST be different from Google Play
- [ ] **Target SDK 33+** for modern Android compatibility
- [ ] **Language locales configured** (see below)

### ✅ App Assets (REQUIRED)

| Asset | Dimensions | Format | Notes |
|-------|------------|--------|-------|
| App Icon | **512 x 512 px** | PNG | Square, no rounded corners (system applies) |
| Banner Graphic | **1200 x 600 px** | PNG/JPG | Required for all apps |
| Screenshots | **1920 x 1080 px** | PNG/JPG | Minimum **4 images**, all same orientation |

### ✅ Optional Assets (Recommended)

| Asset | Dimensions | Format | Notes |
|-------|------------|--------|-------|
| Feature Graphic | **1200 x 1200 px** | PNG/JPG | Required for Editor's Choice consideration |
| Videos | **720p+ min** | MP4 only | Must be `.mp4` format |

---

## Critical Configuration: Language Locales

> ⚠️ **Do NOT skip this step!** - Solana Mobile Official Docs

By default, Bubblewrap CLI incorrectly declares that your app supports **all** locales. This must be fixed before submission.

### The Fix

Add `resConfigs` to your `build.gradle` (inside `android {}` block):

```gradle
android {
    defaultConfig {
        applicationId "xyz.bluefoot.bubblewrapper.sample"
        minSdkVersion 19
        targetSdkVersion 34
        versionCode 1
        versionName "1.0"
        
        // 👇 ADD THIS - specify ONLY the languages you support
        resConfigs "en"  // English only
        // OR for multiple languages:
        // resConfigs "en", "es", "pt", "ja"
    }
}
```

### Why This Matters

- Apps with incorrect locale declarations may be rejected
- Users see your app as supporting languages you don't actually support
- Affects search visibility in those locales

---

## Signing Key Requirements

### Why a Separate Key?

The Solana dApp Store **requires** a unique signing key separate from Google Play because:

1. **No 30% fee** on dApp Store transactions (unlike Play Store)
2. **Decentralized identity** - Your key = your publisher identity
3. **NFT-based releases** - Each release is minted as an NFT
4. **No conflict** with Play Store managed signing

### Generate a New Keystore

```bash
keytool -genkey -v \
  -keystore dappstore.keystore \
  -alias dappstore \
  -keyalg RSA \
  -keysize 2048 \
  -validity 10000
```

### 🔒 Keystore Security Best Practices

| Do | Don't |
|----|-------|
| ✅ Store in encrypted cloud backup | ❌ Commit to git repository |
| ✅ Document password in password manager | ❌ Store password in plaintext |
| ✅ Keep offline backup copy | ❌ Share with unauthorized parties |
| ✅ Use strong, unique password | ❌ Use same key for multiple stores |

> **⚠️ WARNING**: Losing your keystore means you **cannot update your app**. There is no recovery process.

---

## Publisher Wallet Setup

### Requirements

- **Solana wallet** (Phantom, Solflare, or Backpack recommended)
- **~0.2 SOL minimum** for transaction and storage fees
- **Use private RPC** for reliability (public endpoints may timeout)

### Critical Warning

> **⚠️ DO NOT lose access to your publisher wallet!**
> 
> This wallet is your publisher identity. You cannot:
> - Transfer ownership to another wallet
> - Recover if you lose access
> - Submit updates without it

### Recommended Setup

1. Create a **dedicated publisher wallet** (separate from personal)
2. Use a **hardware wallet** (Ledger) for maximum security
3. Back up seed phrase in **multiple secure locations**
4. Use a **private RPC endpoint** (Helius, Triton, QuickNode)

---

## Storage Provider Selection

When submitting, you'll choose where your app assets are stored on-chain:

### ArDrive (Recommended)

- ✅ Lower cost
- ✅ Simpler setup
- ✅ Permanent storage
- ✅ Built-in cost calculator

### Shadow Drive

- ✅ Alternative option
- ⚠️ May require more SOL
- ⚠️ Additional configuration

### Estimating Costs

Use the **cost calculator** in the Publisher Portal before submission:
- Upload your APK
- See estimated SOL required
- Ensure wallet has sufficient balance

---

## Submission Process (Step by Step)

### 1. Create Publisher Account

1. Navigate to [publish.solanamobile.com](https://publish.solanamobile.com)
2. Connect your Solana wallet
3. Fill out publisher profile
4. Complete KYC/KYB verification (may take 1-3 days)

### 2. Create App Listing

1. Click "Add new app"
2. Fill in metadata:
   - App name and description
   - Category and subcategory
   - Support email/URL
   - Privacy policy URL

### 3. Upload Assets

1. Upload app icon (512x512)
2. Upload banner (1200x600)
3. Upload 4+ screenshots
4. Add optional videos/feature graphic

### 4. Submit Release

1. Press "New Version" button
2. Upload your signed APK
3. Add release notes
4. **Sign multiple wallet transactions**

> **⚠️ IMPORTANT**: Approve ALL signing requests! Skipping may cause missing assets.

### 5. Wait for Review

- Typical review time: **1-3 business days**
- You'll receive email notification
- Check Publisher Portal for status updates

---

## Post-Submission

### If Approved 🎉

- App appears in dApp Store
- Monitor downloads and ratings
- Plan regular updates

### If Rejected ❌

Common rejection reasons:
1. Debug build submitted instead of release
2. Wrong signing key used
3. Missing required screenshots (minimum 4)
4. Policy violation
5. Incorrect locale configuration

Check rejection email for specific feedback and resubmit.

---

## Updating Your App

1. Make changes to your PWA
2. Update version in `twa-manifest.json`:
   ```json
   {
     "appVersionCode": 2,
     "appVersionName": "1.1.0"
   }
   ```
3. Rebuild:
   ```bash
   bubblewrap update
   bubblewrap build
   ```
4. Submit update via Publisher Portal
5. Sign transactions with **same publisher wallet**

---

## FAQ

### Can I use the same APK as Google Play?

**No.** The dApp Store requires a separate signing key. This is intentional to maintain separate distribution channels.

### How much SOL do I need?

Approximately **0.2 SOL** for initial submission, varying based on APK size and assets. Use the cost calculator for exact amounts.

### What if my wallet runs out of SOL during submission?

The submission may fail partially. Ensure adequate balance before starting. If it fails, add more SOL and try again.

### Can I transfer my app to another wallet?

**No.** Publisher wallet ownership is permanent. Plan accordingly.

### How long does review take?

Typically **1-3 business days**. Complex apps may take longer.

### What happens to my app fees?

Unlike Google Play's 30%, the Solana dApp Store has **no transaction fees**. You keep 100% of in-app revenue.

---

## Support & Resources

- **Email**: concerns@dappstore.solanamobile.com
- **Discord**: [Solana Mobile Discord](https://discord.gg/solanamobile)
- **Docs**: [docs.solanamobile.com](https://docs.solanamobile.com)
- **GitHub**: [solana-mobile/solana-mobile-doc-site](https://github.com/solana-mobile/solana-mobile-doc-site)

---

**Last Updated**: Based on Solana Mobile documentation as of 2024
