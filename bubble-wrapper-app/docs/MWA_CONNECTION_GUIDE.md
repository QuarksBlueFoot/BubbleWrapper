# Mobile Wallet Adapter (MWA) Connection Guide

A comprehensive guide to implementing Solana wallet connections in Android apps using Mobile Wallet Adapter 2.0.

---

## Table of Contents

1. [Overview](#overview)
2. [Dependencies](#dependencies)
3. [Configuration Checklist](#configuration-checklist)
4. [Implementation](#implementation)
5. [Common Errors & Solutions](#common-errors--solutions)
6. [Testing](#testing)
7. [Best Practices](#best-practices)

---

## Overview

Mobile Wallet Adapter (MWA) is the standard protocol for connecting Android apps to Solana wallets like Phantom, Solflare, and Backpack. This guide covers the critical configuration details often missed in documentation.

### Key Concepts

- **identityUri**: The absolute HTTPS URL of your dApp (e.g., `https://yourdapp.com`)
- **iconUri**: A **RELATIVE** path to your app's icon from the identityUri (e.g., `favicon.ico`)
- **identityName**: Human-readable name shown in wallet authorization prompts
- **blockchain/chain**: Network to use (`Solana.Mainnet`, `Solana.Testnet`, `Solana.Devnet`)

---

## Dependencies

Add to your `app/build.gradle.kts`:

```kotlin
dependencies {
    // Mobile Wallet Adapter 2.0
    implementation("com.solanamobile:mobile-wallet-adapter-clientlib-ktx:2.0.3")
}
```

---

## Configuration Checklist

### ✅ 1. AndroidManifest.xml - Wallet Discovery

Add `<queries>` to enable wallet discovery on Android 11+:

```xml
<manifest>
    <!-- Required for wallet discovery on Android 11+ -->
    <queries>
        <!-- Solana wallet scheme -->
        <intent>
            <action android:name="android.intent.action.VIEW" />
            <category android:name="android.intent.category.BROWSABLE" />
            <data android:scheme="solana-wallet" />
        </intent>
        
        <!-- Specific wallet packages (optional but recommended) -->
        <package android:name="app.phantom" />
        <package android:name="com.solflare.mobile" />
        <package android:name="app.backpack" />
    </queries>
    
    <application>
        <!-- Your app components -->
    </application>
</manifest>
```

**Why?** Android 11+ restricts package visibility. Without this, `NoWalletFound` errors occur even with wallets installed.

---

### ✅ 2. App Identity Configuration

```kotlin
companion object {
    // Your dApp identity (shown in wallet authorization prompt)
    private const val APP_IDENTITY_NAME = "Your App Name"
    private const val APP_IDENTITY_URI = "https://yourdomain.com"
    private const val APP_IDENTITY_ICON = "favicon.ico"  // RELATIVE path!
}
```

**Critical: The icon URI must be RELATIVE, not absolute!**

| ❌ Wrong | ✅ Correct |
|----------|-----------|
| `https://yourdomain.com/favicon.ico` | `favicon.ico` |
| `Uri.parse("$BASE_URL/$ICON")` | `Uri.parse(ICON)` |

---

### ✅ 3. MobileWalletAdapter Initialization

```kotlin
private val walletAdapter = MobileWalletAdapter(
    connectionIdentity = ConnectionIdentity(
        identityUri = Uri.parse(APP_IDENTITY_URI),
        iconUri = Uri.parse(APP_IDENTITY_ICON),  // Relative path!
        identityName = APP_IDENTITY_NAME
    )
).apply {
    // Set to Mainnet for production
    blockchain = Solana.Mainnet
}
```

**Note:** The `blockchain` property defaults to `Solana.Devnet`. You must explicitly set it for Mainnet.

---

### ✅ 4. ActivityResultSender Lifecycle

The `ActivityResultSender` must be created **BEFORE** `setContent {}` in your Activity:

```kotlin
class MainActivity : ComponentActivity() {
    
    // Create sender BEFORE setContent
    private lateinit var activityResultSender: ActivityResultSender
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Initialize BEFORE setContent - critical!
        activityResultSender = ActivityResultSender(this)
        
        setContent {
            MyApp(activityResultSender)
        }
    }
}
```

**Why?** The sender registers for activity results. If created after `setContent`, the callback won't be properly registered.

---

## Implementation

### Connect to Wallet

```kotlin
suspend fun connect(sender: ActivityResultSender): Result<WalletState.Connected> {
    return try {
        val result = walletAdapter.transact(sender) { _ ->
            // iconUri must be RELATIVE per MWA spec
            authorize(
                identityUri = Uri.parse(APP_IDENTITY_URI),
                iconUri = Uri.parse(APP_IDENTITY_ICON),  // Relative!
                identityName = APP_IDENTITY_NAME
            )
        }
        
        when (result) {
            is TransactionResult.Success -> {
                val auth = result.payload
                val publicKey = Base64.getEncoder().encodeToString(auth.publicKey)
                Result.success(WalletState.Connected(
                    publicKey = publicKey,
                    walletName = auth.walletUriBase?.host ?: "Solana Wallet",
                    authToken = auth.authToken
                ))
            }
            is TransactionResult.Failure -> {
                Result.failure(Exception(result.message))
            }
            is TransactionResult.NoWalletFound -> {
                Result.failure(Exception("No Solana wallet found"))
            }
        }
    } catch (e: Exception) {
        Result.failure(e)
    }
}
```

### Sign Transaction (with Reauthorization)

```kotlin
suspend fun signAndSendTransaction(
    sender: ActivityResultSender,
    transaction: ByteArray,
    authToken: String
): Result<String> {
    return try {
        val result = walletAdapter.transact(sender) { _ ->
            // Reauthorize with RELATIVE iconUri
            reauthorize(
                identityUri = Uri.parse(APP_IDENTITY_URI),
                iconUri = Uri.parse(APP_IDENTITY_ICON),  // Relative!
                identityName = APP_IDENTITY_NAME,
                authToken = authToken
            )
            
            signAndSendTransactions(transactions = arrayOf(transaction))
        }
        
        when (result) {
            is TransactionResult.Success -> {
                val sig = Base64.getEncoder().encodeToString(
                    result.payload.signatures.first()
                )
                Result.success(sig)
            }
            else -> Result.failure(Exception("Transaction failed"))
        }
    } catch (e: Exception) {
        Result.failure(e)
    }
}
```

---

## Common Errors & Solutions

### ❌ "If non-null, iconRelativeUri must be a relative Uri"

**Cause:** You're passing an absolute URL for `iconUri`.

**Fix:**
```kotlin
// Wrong
iconUri = Uri.parse("https://example.com/icon.png")

// Correct  
iconUri = Uri.parse("icon.png")
```

---

### ❌ "NoWalletFound" despite having wallets installed

**Cause:** Missing `<queries>` in AndroidManifest.xml

**Fix:** Add the queries section shown above in the Configuration Checklist.

---

### ❌ Wallet shows Testnet/Devnet instead of Mainnet

**Cause:** Default blockchain is Devnet

**Fix:**
```kotlin
MobileWalletAdapter(...).apply {
    blockchain = Solana.Mainnet  // Explicitly set!
}
```

---

### ❌ "ActivityResultSender" callback never fires

**Cause:** Sender created after `setContent {}`

**Fix:** Initialize in `onCreate()` BEFORE `setContent`:
```kotlin
override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    activityResultSender = ActivityResultSender(this)  // BEFORE setContent
    setContent { ... }
}
```

---

### ❌ Wallet connects but immediately disconnects

**Cause:** Usually a lifecycle issue or exception in result handling

**Fix:** 
- Check logcat for exceptions in your result handling code
- Ensure you're saving the auth token for reauthorization
- Wrap result processing in try-catch

---

### ❌ "AppsFilter: interaction BLOCKED" in logs

**Cause:** Android package visibility restrictions

**Fix:** Ensure `<queries>` includes the wallet packages and schemes.

---

## Testing

### With Phantom Wallet (Solana Mobile Seeker)

1. Install your debug APK on device
2. Ensure Phantom is installed and has a wallet set up
3. Tap "Connect Wallet" in your app
4. Phantom should open with authorization request
5. Check logs for: `Authorize request completed successfully`

### Logcat Filters

```bash
adb logcat | grep -E "MobileWallet|Phantom|Authorize|iconUri"
```

### Success Indicators in Logs

```
PhantomMWAModule: onAuthorizeRequest
Scenario: Authorize request completed successfully
AuthRepositoryImpl: Returning auth token for AuthRecord
```

---

## Best Practices

### 1. Host Your App Identity Assets

Ensure `https://yourdomain.com/favicon.ico` is accessible:
- The wallet may fetch this icon to display in the authorization prompt
- Use a proper favicon (PNG, ICO, or SVG)
- Ensure HTTPS and proper CORS headers

### 2. Handle All Result States

```kotlin
when (result) {
    is TransactionResult.Success -> { /* Happy path */ }
    is TransactionResult.Failure -> { /* Show error */ }
    is TransactionResult.NoWalletFound -> { /* Prompt to install wallet */ }
}
```

### 3. Persist Auth Tokens

Save auth tokens for reauthorization to avoid re-prompting users:

```kotlin
// Using DataStore
context.walletDataStore.edit { prefs ->
    prefs[KEY_AUTH_TOKEN] = authResult.authToken
    prefs[KEY_PUBLIC_KEY] = publicKeyBase58
}
```

### 4. Use Mainnet for Production

Always explicitly set the blockchain for production apps:

```kotlin
blockchain = Solana.Mainnet
```

### 5. Provide Wallet Installation Links

When no wallet is found:
```kotlin
is TransactionResult.NoWalletFound -> {
    // Show dialog with links to:
    // - Phantom: https://phantom.app/download
    // - Solflare: https://solflare.com/download
}
```

---

## Resources

- [MWA GitHub Repository](https://github.com/solana-mobile/mobile-wallet-adapter)
- [MWA Specification](https://github.com/solana-mobile/mobile-wallet-adapter/blob/main/spec/spec.md)
- [Solana Mobile Docs](https://docs.solanamobile.com)
- [clientlib-ktx API Reference](https://github.com/solana-mobile/mobile-wallet-adapter/tree/main/android/clientlib-ktx)

---

*Guide by [Bluefoot Labs](https://bluefoot.xyz) - Created from real-world debugging on Solana Mobile Seeker*
