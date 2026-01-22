# Digital Asset Links (DAL) Complete Guide

Digital Asset Links establish a trusted connection between your website and Android app. This is **required** for Trusted Web Activities (TWA) to work in fullscreen mode without showing the browser URL bar.

---

## How DAL Works

```
┌─────────────────┐          ┌──────────────────┐
│   Your PWA      │◄────────►│   Android App    │
│   (Website)     │          │   (TWA/APK)      │
└────────┬────────┘          └────────┬─────────┘
         │                            │
         │ assetlinks.json            │ SHA256 fingerprint
         │ /.well-known/              │ in APK signature
         │                            │
         └────────────┬───────────────┘
                      │
                      ▼
              ┌───────────────┐
              │   Chrome      │
              │   Verifies    │
              │   Trust       │
              └───────────────┘
```

When Chrome launches your TWA:
1. Reads the app's package name and signature fingerprint
2. Fetches `/.well-known/assetlinks.json` from your domain
3. Verifies the fingerprints match
4. If verified → fullscreen mode (no browser UI)
5. If failed → shows URL bar (fallback mode)

---

## Step-by-Step Setup

### Step 1: Get Your SHA256 Fingerprint

Extract the fingerprint from your keystore:

```bash
keytool -list -v -keystore android.keystore
```

Look for the `SHA256:` line in the output:

```
Certificate fingerprints:
         SHA1: D0:13:56:7A:...
         SHA256: AB:CD:EF:12:34:56:78:...
```

Copy the entire SHA256 fingerprint (including colons).

### Step 2: Add Fingerprint to TWA

Register the fingerprint with Bubblewrap:

```bash
bubblewrap fingerprint add AB:CD:EF:12:34:56:78:...
```

### Step 3: Generate assetlinks.json

```bash
bubblewrap fingerprint generateAssetLinks
```

This creates `assetlinks.json` in your project directory.

### Step 4: Host the File

Upload `assetlinks.json` to your web server at:

```
https://your-domain.com/.well-known/assetlinks.json
```

#### Requirements

| Requirement | Details |
|-------------|---------|
| **Path** | MUST be exactly `/.well-known/assetlinks.json` |
| **Protocol** | MUST be HTTPS |
| **Redirects** | NOT allowed (must serve directly) |
| **Content-Type** | `application/json` |
| **Access** | Publicly accessible (no auth) |

---

## File Format

```json
[{
  "relation": ["delegate_permission/common.handle_all_urls"],
  "target": {
    "namespace": "android_app",
    "package_name": "xyz.bluefoot.bubblewrapper.sample",
    "sha256_cert_fingerprints": [
      "AB:CD:EF:12:34:56:78:90:AB:CD:EF:12:34:56:78:90:AB:CD:EF:12:34:56:78:90:AB:CD:EF:12:34:56:78:90"
    ]
  }
}]
```

### Multiple Fingerprints

You can add multiple fingerprints for different builds:

```json
[{
  "relation": ["delegate_permission/common.handle_all_urls"],
  "target": {
    "namespace": "android_app",
    "package_name": "xyz.bluefoot.bubblewrapper.sample",
    "sha256_cert_fingerprints": [
      "AA:BB:CC:...",  // Debug key
      "DD:EE:FF:..."   // Release key
    ]
  }
}]
```

---

## Verification

### Method 1: Google's DAL Tool

Use Google's Statement List Generator and Tester:

```
https://developers.google.com/digital-asset-links/tools/generator
```

Enter your domain and package name to verify.

### Method 2: Direct URL Check

```bash
curl -I https://your-domain.com/.well-known/assetlinks.json
```

Verify:
- Status: 200 OK
- Content-Type: application/json
- No redirects

### Method 3: Install and Test

```bash
bubblewrap install
```

If fullscreen mode works → DAL is configured correctly.

---

## Common Issues & Solutions

### Issue: URL Bar Still Showing

**Causes:**
1. Fingerprint mismatch
2. File not accessible
3. Wrong path
4. Redirect in place
5. Chrome caching old validation

**Solutions:**
```bash
# Verify fingerprint matches
keytool -list -v -keystore android.keystore

# Check file accessibility
curl https://your-domain.com/.well-known/assetlinks.json

# Clear Chrome cache on device
# Settings → Apps → Chrome → Clear Cache

# Force Chrome to re-verify (may take up to 24 hours)
```

### Issue: 404 Not Found

**Check:**
- `.well-known` directory exists
- File name is exactly `assetlinks.json`
- Web server allows serving dotfiles (`.well-known`)

**Nginx fix:**
```nginx
location /.well-known/ {
    allow all;
}
```

**Apache fix:**
```apache
<Directory "/.well-known">
    Require all granted
</Directory>
```

### Issue: Wrong Content-Type

**Add MIME type:**

Nginx:
```nginx
location /.well-known/assetlinks.json {
    default_type application/json;
}
```

Apache:
```apache
<FilesMatch "assetlinks.json">
    ForceType application/json
</FilesMatch>
```

### Issue: Fingerprint Changed After Rebuild

If you rebuild with `bubblewrap build` and the fingerprint changes:
1. Re-extract fingerprint with `keytool`
2. Update `assetlinks.json`
3. Re-upload to server
4. Wait for Chrome to re-validate (or clear cache)

---

## Hosting on Different Platforms

### Vercel

Create `vercel.json`:
```json
{
  "rewrites": [
    {
      "source": "/.well-known/assetlinks.json",
      "destination": "/assetlinks.json"
    }
  ],
  "headers": [
    {
      "source": "/.well-known/assetlinks.json",
      "headers": [
        { "key": "Content-Type", "value": "application/json" }
      ]
    }
  ]
}
```

### Netlify

Create `_redirects` or use `netlify.toml`:
```toml
[[headers]]
  for = "/.well-known/assetlinks.json"
  [headers.values]
    Content-Type = "application/json"
    Access-Control-Allow-Origin = "*"
```

### GitHub Pages

Create directory structure:
```
your-repo/
├── .well-known/
│   └── assetlinks.json
└── index.html
```

Note: GitHub Pages may not serve `.well-known` by default. You may need a custom domain.

### Cloudflare Pages

Create `_headers` file:
```
/.well-known/assetlinks.json
  Content-Type: application/json
```

---

## Debugging Checklist

- [ ] SHA256 fingerprint in `assetlinks.json` matches APK signature
- [ ] File is at exactly `/.well-known/assetlinks.json`
- [ ] File is served over HTTPS
- [ ] No redirects (check with `curl -I`)
- [ ] Content-Type is `application/json`
- [ ] Package name matches exactly (case-sensitive)
- [ ] Cleared Chrome cache on test device
- [ ] Waited for Chrome verification (can take hours)

---

## Resources

- [Android App Links Verification](https://developer.android.com/training/app-links/verify-android-applinks)
- [Digital Asset Links Spec](https://developers.google.com/digital-asset-links/v1/getting-started)
- [DAL Statement Generator](https://developers.google.com/digital-asset-links/tools/generator)
- [Chrome TWA Docs](https://developer.chrome.com/docs/android/trusted-web-activity)

---

**Tip**: When in doubt, use Google's Statement List Generator tool to validate your setup. It will tell you exactly what's wrong.
