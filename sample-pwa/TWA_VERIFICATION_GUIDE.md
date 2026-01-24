# TWA Verification Guide - MonkeMob

This guide explains how to verify your Trusted Web Activity (TWA) so Chrome removes the address bar and treats your Android app as a verified first-party app for `monkemob.me`.

## Prerequisites

✅ **Already completed:**
- APK built and signed with release keystore: [app-release.apk](android-twa-generated/app/build/outputs/apk/release/app-release.apk) (v1.2)
- SHA-256 fingerprint extracted: `09:4A:9F:4E:BE:0E:D1:A9:E6:A2:F6:2A:E2:05:B8:50:7A:43:3A:6A:53:35:63:B2:37:87:B5:B5:C7:D9:B4:CF`
- assetlinks.json prepared: [android-twa/assetlinks-monkemob.json](android-twa/assetlinks-monkemob.json)

## Step 1: Deploy assetlinks.json to Your Website

Upload the assetlinks file to your domain at the **exact path**:

```
https://monkemob.me/.well-known/assetlinks.json
```

### File Contents

Your assetlinks file must contain:

```json
[{
  "relation": ["delegate_permission/common.handle_all_urls"],
  "target": {
    "namespace": "android_app",
    "package_name": "me.monkemob.twa",
    "sha256_cert_fingerprints": ["09:4A:9F:4E:BE:0E:D1:A9:E6:A2:F6:2A:E2:05:B8:50:7A:43:3A:6A:53:35:63:B2:37:87:B5:B5:C7:D9:B4:CF"]
  }
}]
```

### Server Requirements

- **Content-Type:** Must be `application/json` (not `text/plain`)
- **HTTPS:** Must be served over HTTPS (not HTTP)
- **No redirects:** The URL must not redirect
- **Accessible:** Must be publicly accessible (not behind auth)

### Verify Server Headers

```bash
curl -I https://monkemob.me/.well-known/assetlinks.json
```

Expected output should include:
```
HTTP/2 200
content-type: application/json
```

## Step 2: Verify assetlinks Content

```bash
curl https://monkemob.me/.well-known/assetlinks.json | jq .
```

Confirm the output matches the JSON above exactly.

## Step 3: Install the APK on a Device

### Transfer APK to Device

```bash
adb install /workspaces/BubbleWrapper/sample-pwa/android-twa-generated/app/build/outputs/apk/release/app-release.apk
```

Or share via:
- Google Drive
- Email
- USB transfer
- Direct download from GitHub Release

### Enable Installation from Unknown Sources

On your Android device:
1. Go to **Settings → Security**
2. Enable **Install unknown apps** for your browser/file manager

## Step 4: Test TWA Verification

### Launch the App

Open **MonkeMob** from your app drawer.

### Check for Verification

**✅ Verified (Success):**
- No address bar visible
- App opens directly to your PWA content
- Behaves like a native app

**❌ Not Verified (Needs Fix):**
- Chrome custom tab with address bar visible
- "..." menu button in top-right
- URL shown in address bar

### Debugging Failed Verification

If verification fails, check:

1. **assetlinks.json location:**
   ```bash
   curl -I https://monkemob.me/.well-known/assetlinks.json
   ```
   Must return `200 OK` with `content-type: application/json`

2. **Package name matches:**
   ```bash
   apksigner verify --print-certs app-release.apk | grep "package_name"
   ```
   Should show `me.monkemob.twa`

3. **Fingerprint matches:**
   ```bash
   apksigner verify --print-certs app-release.apk
   ```
   Compare SHA-256 with assetlinks.json (colons required in assetlinks!)

4. **Clear Chrome data** (sometimes Chrome caches failed verification):
   - Settings → Apps → Chrome → Storage → Clear cache
   - Uninstall and reinstall your TWA app

5. **Wait for propagation:**
   - Google may take up to 24 hours to verify after first deployment
   - Usually happens within minutes

## Step 5: Verify with Android Asset Links Tool

Google provides a testing tool at:

```
https://digitalassetlinks.googleapis.com/v1/statements:list?source.web.site=https://monkemob.me&relation=delegate_permission/common.handle_all_urls
```

Expected response should include:
```json
{
  "statements": [
    {
      "source": {
        "web": {
          "site": "https://monkemob.me"
        }
      },
      "relation": "delegate_permission/common.handle_all_urls",
      "target": {
        "androidApp": {
          "packageName": "me.monkemob.twa",
          "certificate": {
            "sha256Fingerprint": "094A9F4EBE0ED1A9E6A2F62AE205B8507A433A6A533563B23787B5B5C7D9B4CF"
          }
        }
      }
    }
  ]
}
```

## Step 6: Publish to Google Play (Optional but Recommended)

Publishing to Google Play improves verification reliability:

1. **Create Play Console account** (one-time $25 fee)
2. **Upload app-release.apk**
3. **Add store listing** with screenshots and description
4. **Submit for review**

After publishing, Google Play automatically validates assetlinks during app review.

## Troubleshooting

### Issue: "Invalid fingerprint format"

Assetlinks requires uppercase hex with colons:
- ✅ Correct: `09:4A:9F:4E:BE:0E:D1:A9:...`
- ❌ Wrong: `094a9f4ebe0ed1a9e6a2f62a...`
- ❌ Wrong: `09 4A 9F 4E BE 0E D1 A9...`

### Issue: "Statement list is empty"

Your assetlinks.json file is not accessible. Check:
- File exists at `/.well-known/assetlinks.json`
- Returns HTTP 200
- Served as `application/json`
- No CORS errors in browser console

### Issue: "Package name mismatch"

AndroidManifest.xml package must match `twa-manifest.json` packageId:
- `me.monkemob.twa` (both must be identical)

## APK Signing Certificate Details

```
Subject: CN=MonkeMob, OU=Development, O=MonkeMob, L=Unknown, ST=Unknown, C=US
SHA-256: 09:4A:9F:4E:BE:0E:D1:A9:E6:A2:F6:2A:E2:05:B8:50:7A:43:3A:6A:53:35:63:B2:37:87:B5:B5:C7:D9:B4:CF
SHA-1: C8:1A:DF:76:B1:F4:23:5E:07:75:82:91:30:8A:89:03:1D:7D:B4:9D
```

## Additional Resources

- [Android App Links verification](https://developer.android.com/training/app-links/verify-android-applinks)
- [Digital Asset Links](https://developers.google.com/digital-asset-links/v1/getting-started)
- [Bubblewrap TWA documentation](https://github.com/GoogleChromeLabs/bubblewrap/tree/main/packages/cli)

## Quick Verification Commands

```bash
# 1. Check assetlinks is live
curl -I https://monkemob.me/.well-known/assetlinks.json

# 2. Verify content
curl https://monkemob.me/.well-known/assetlinks.json | jq .

# 3. Test with Google API
curl "https://digitalassetlinks.googleapis.com/v1/statements:list?source.web.site=https://monkemob.me&relation=delegate_permission/common.handle_all_urls"

# 4. Check APK signature
apksigner verify --print-certs app-release.apk

# 5. Install on device
adb install app-release.apk
```

---

**Status:** Ready to deploy! Upload assetlinks.json to `https://monkemob.me/.well-known/assetlinks.json` and test on device.
