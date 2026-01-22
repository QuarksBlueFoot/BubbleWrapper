# 🔐 Keystore Management Guide

Complete guide to managing keystores for BubbleWrapper and TWA applications.

## 📍 Keystore Locations

### MonkeMob TWA Keystore

**Primary Location:**
```
/workspaces/BubbleWrapper/sample-pwa/android-twa-generated/app/monkemob-release.keystore
```

**Backup Locations:**
- `/workspaces/BubbleWrapper/sample-pwa/android-twa/monkemob-release.keystore`
- `/workspaces/BubbleWrapper/bubble-wrapper-app/app/monkemob-release.keystore`

**Configuration:**
```gradle
signingConfigs {
    release {
        storeFile = file("monkemob-release.keystore")
        storePassword = "monkemob123"
        keyAlias = "android"
        keyPassword = "monkemob123"
    }
}
```

**Certificate Details:**
- **Alias**: android
- **Owner**: CN=MonkeMob, OU=Development, O=MonkeMob, L=Unknown, ST=Unknown, C=US
- **Valid From**: Jan 23, 2026
- **Valid Until**: Jun 10, 2053 (27 years)
- **Algorithm**: SHA256withRSA (2048-bit RSA)
- **SHA1**: `C8:1A:DF:76:B1:F4:23:5E:07:75:82:91:30:8A:89:03:1D:7D:B4:9D`
- **SHA256**: `09:4A:9F:4E:BE:0E:D1:A9:E6:A2:F6:2A:E2:05:B8:50:7A:43:3A:6A:53:35:63:B2:37:87:B5:B5:C7:D9:B4:CF`

---

### BubbleWrapper App Keystore

**Location:**
```
/workspaces/BubbleWrapper/bubble-wrapper-app/app/bubblewrapper-release.keystore
```

**Configuration:**
```kotlin
signingConfigs {
    create("release") {
        storeFile = file("bubblewrapper-release.keystore")
        storePassword = "bubblewrapper123"
        keyAlias = "bubblewrapper"
        keyPassword = "bubblewrapper123"
    }
}
```

**Certificate Details:**
- **Alias**: bubblewrapper
- **Owner**: CN=BubbleWrapper, OU=Development, O=BlueFoot, L=Unknown, ST=Unknown, C=US
- **Valid From**: Jan 23, 2026
- **Valid Until**: Jun 10, 2053 (27 years)
- **Algorithm**: SHA256withRSA (2048-bit RSA)
- **SHA1**: `5C:64:A7:39:BD:08:E7:8D:33:AB:2F:E3:D1:B2:E3:FC:8D:0D:D6:82`
- **SHA256**: `98:D0:CA:5E:E7:66:1C:F7:B1:66:E6:DD:C9:E5:28:36:A3:64:12:7A:7A:D6:94:8E:23:72:3B:A3:1C:1C:C9:D6`

---

## 🔍 Keystore Information Commands

### View Keystore Details

**MonkeMob:**
```bash
keytool -list -v \
  -keystore sample-pwa/android-twa-generated/app/monkemob-release.keystore \
  -storepass monkemob123
```

**BubbleWrapper:**
```bash
keytool -list -v \
  -keystore bubble-wrapper-app/app/bubblewrapper-release.keystore \
  -storepass bubblewrapper123
```

### Get SHA256 Fingerprint

**MonkeMob:**
```bash
keytool -list -v \
  -keystore sample-pwa/android-twa-generated/app/monkemob-release.keystore \
  -storepass monkemob123 \
  -alias android | grep SHA256
```

**BubbleWrapper:**
```bash
keytool -list -v \
  -keystore bubble-wrapper-app/app/bubblewrapper-release.keystore \
  -storepass bubblewrapper123 \
  -alias bubblewrapper | grep SHA256
```

### Export Certificate

**MonkeMob:**
```bash
keytool -export -rfc \
  -keystore sample-pwa/android-twa-generated/app/monkemob-release.keystore \
  -storepass monkemob123 \
  -alias android \
  -file monkemob-cert.pem
```

**BubbleWrapper:**
```bash
keytool -export -rfc \
  -keystore bubble-wrapper-app/app/bubblewrapper-release.keystore \
  -storepass bubblewrapper123 \
  -alias bubblewrapper \
  -file bubblewrapper-cert.pem
```

---

## 🆕 Creating New Keystores

### For TWA Applications

```bash
keytool -genkey -v \
  -keystore my-app-release.keystore \
  -alias android \
  -keyalg RSA \
  -keysize 2048 \
  -validity 10000 \
  -storepass YOUR_STORE_PASSWORD \
  -keypass YOUR_KEY_PASSWORD \
  -dname "CN=YourApp, OU=Development, O=YourOrg, L=City, ST=State, C=US"
```

### For Regular Android Apps

```bash
keytool -genkey -v \
  -keystore my-app-release.keystore \
  -alias myapp \
  -keyalg RSA \
  -keysize 2048 \
  -validity 10000 \
  -storepass YOUR_STORE_PASSWORD \
  -keypass YOUR_KEY_PASSWORD \
  -dname "CN=YourApp, OU=Development, O=YourOrg, L=City, ST=State, C=US"
```

**Parameters Explained:**
- `-keystore`: Output file path
- `-alias`: Key identifier (use "android" for TWAs)
- `-keyalg`: Algorithm (RSA recommended)
- `-keysize`: Key size (2048 minimum, 4096 for extra security)
- `-validity`: Days valid (10000 = ~27 years)
- `-storepass`: Keystore password
- `-keypass`: Key password (can be same as store password)
- `-dname`: Certificate distinguished name

---

## 🔒 Keystore Security Best Practices

### ✅ DO:
- **Store keystores in secure, backed-up location**
- **Keep passwords in secure password manager**
- **Never commit keystores to public repositories**
- **Create encrypted backups** (use GPG or similar)
- **Document certificate fingerprints separately**
- **Use strong passwords** (20+ characters, mixed case, numbers, symbols)
- **Set validity to 25+ years** for production apps

### ❌ DON'T:
- **Never share keystore passwords in plain text**
- **Don't commit keystores to version control** (unless private repo with encryption)
- **Don't use weak passwords** ("password123", "test", etc.)
- **Don't lose your keystore** - there's NO recovery
- **Don't reuse keystores across unrelated apps**

---

## 📦 Build Configuration

### TWA (Gradle)

```gradle
android {
    signingConfigs {
        release {
            storeFile file("path/to/keystore.keystore")
            storePassword "YOUR_STORE_PASSWORD"
            keyAlias "android"
            keyPassword "YOUR_KEY_PASSWORD"
        }
    }
    
    buildTypes {
        release {
            minifyEnabled true
            signingConfig signingConfigs.release
        }
    }
}
```

### Android App (Kotlin DSL)

```kotlin
android {
    signingConfigs {
        create("release") {
            storeFile = file("path/to/keystore.keystore")
            storePassword = "YOUR_STORE_PASSWORD"
            keyAlias = "myapp"
            keyPassword = "YOUR_KEY_PASSWORD"
        }
    }
    
    buildTypes {
        getByName("release") {
            isMinifyEnabled = true
            signingConfig = signingConfigs.getByName("release")
        }
    }
}
```

---

## 🔄 Digital Asset Links (TWA)

For TWAs to work, you need to configure Digital Asset Links on your website.

### Generate assetlinks.json

**MonkeMob Example:**
```json
[{
  "relation": ["delegate_permission/common.handle_all_urls"],
  "target": {
    "namespace": "android_app",
    "package_name": "me.monkemob.twa",
    "sha256_cert_fingerprints": [
      "09:4A:9F:4E:BE:0E:D1:A9:E6:A2:F6:2A:E2:05:B8:50:7A:43:3A:6A:53:35:63:B2:37:87:B5:B5:C7:D9:B4:CF"
    ]
  }
}]
```

**Deployment:**
```bash
# Place at: https://monkemob.me/.well-known/assetlinks.json
# Must be accessible via HTTPS
# Must have Content-Type: application/json
```

**Verification:**
```bash
# Test your assetlinks.json
curl -I https://monkemob.me/.well-known/assetlinks.json

# Should return:
# HTTP/2 200
# content-type: application/json
```

---

## 🆘 Emergency Recovery

### If You Lose Your Keystore:

**For Play Store Apps:**
- ❌ **Cannot update existing app** - you lost the signing key
- ✅ Can publish as new app with different package name
- ⚠️ Users cannot upgrade, must uninstall/reinstall

**For Solana dApp Store:**
- ✅ Can publish new version with different certificate
- ⚠️ Must update NFT metadata with new fingerprint
- ✅ Users can still upgrade

### Prevention is Key:
```bash
# Create encrypted backup
gpg -c monkemob-release.keystore
# Output: monkemob-release.keystore.gpg

# Store in multiple locations:
# 1. Local secure folder (encrypted)
# 2. Password manager (as attachment)
# 3. Cloud backup (encrypted)
# 4. USB drive (encrypted)
# 5. Company vault/safe
```

---

## 📋 Quick Reference

| App | Keystore | Alias | Store Pass | Key Pass | SHA256 (first 16 chars) |
|-----|----------|-------|------------|----------|-------------------------|
| **MonkeMob TWA** | `monkemob-release.keystore` | `android` | `monkemob123` | `monkemob123` | `09:4A:9F:4E:BE:0E...` |
| **BubbleWrapper** | `bubblewrapper-release.keystore` | `bubblewrapper` | `bubblewrapper123` | `bubblewrapper123` | `98:D0:CA:5E:E7:66...` |

---

## 🔗 Related Documentation

- [Digital Asset Links Guide](sample-pwa/docs/DIGITAL_ASSET_LINKS.md)
- [TWA Publishing Guide](bubble-wrapper-app/PUBLISHING_GUIDE.md)
- [Mobile Optimizations](sample-pwa/MOBILE_OPTIMIZATIONS.md)
- [Android Signing](https://developer.android.com/studio/publish/app-signing)
- [TWA Documentation](https://developer.chrome.com/docs/android/trusted-web-activity/)

---

## 📞 Support

For keystore issues:
1. Check this guide first
2. Review build logs for signing errors
3. Verify keystore exists and is readable
4. Confirm passwords match configuration
5. Test with `keytool -list` command

**Remember**: Keystore loss is permanent. Always maintain secure backups!
