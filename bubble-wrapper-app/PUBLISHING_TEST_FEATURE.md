# 🧪 Publishing Test Feature

## Overview
The Publishing Test Screen is a complete testing and demonstration interface for the Solana Mobile dApp Store publishing workflow. It allows you to test the entire publishing process with both simulation and real production modes.

## Location
- **File**: `/app/src/main/java/xyz/bluefoot/bubblewrapper/testing/PublishingTestScreen.kt`
- **Navigation**: Publish Tab → "Open Publishing Test Screen" button

## Features

### 🎛️ Mode Toggle
- **Simulation Mode** (Default): Safe testing with mock data
  - No real transactions
  - No SOL spent
  - Returns mock NFT addresses
  - Perfect for development and testing

- **Production Mode** (⚠️ Dangerous): Real publishing
  - Creates actual NFTs on-chain
  - Uploads to Arweave/Irys
  - Submits to real Publisher Portal
  - **Costs real SOL!**

### 🔧 Component Controls
Fine-grained control over which parts of the workflow to execute:

1. **Arweave Uploads**
   - Toggle uploads to permanent storage
   - Uses Irys network (https://node2.irys.xyz)
   - Simulation: Returns mock URIs

2. **NFT Creation**
   - Toggle on-chain App and Release NFT minting
   - Uses Mobile Wallet Adapter for signing
   - Simulation: Returns mock mint addresses

3. **Portal Submission**
   - Toggle Publisher Portal (HubSpot) submission
   - Submits with all required attestations
   - Simulation: Skips actual submission

### 📊 Progress Tracking
Real-time updates during publishing:
- Current stage display
- Progress bar (X/Y steps)
- Step-by-step descriptions
- Visual feedback

### ✅ Result Display
After publishing, shows:
- **Success/Failure status**
- **App NFT address** (e.g., `ADZWhSTQ...`)
- **Release NFT address** (e.g., `HjfgUUfy...`)
- **Metadata URI** (Arweave link)
- **Portal submission status**
- Review timeline (3-4 business days)

## Usage

### Testing Mode (Safe)
```kotlin
1. Keep "Simulation Mode" toggle ON
2. Configure component toggles as needed
3. Click "Test Publish (Simulated)"
4. Review mock results
```

### Production Mode (Real Publishing)
```kotlin
1. ⚠️ Toggle OFF "Simulation Mode"
2. Enable desired components (Arweave, NFTs, Portal)
3. Ensure wallet has sufficient SOL (~0.15 SOL)
4. Click "PUBLISH TO STORE (REAL)"
5. Confirm transaction in wallet
6. Wait for completion
```

## Test Configuration
The screen uses a built-in test config:
- **App Name**: "Test App"
- **Package**: "com.test.app"
- **Category**: "Tools"
- **Wallet**: MonkeMob's mainnet address
- **RPC**: https://api.mainnet-beta.solana.com

In production, these values would come from user input through the full publishing wizard.

## Architecture

### Dependencies
```kotlin
DappStoreCliWorkflow
  ├── DappStorePublisher (NFT creation)
  ├── WalletManager (Mobile Wallet Adapter)
  └── SolanaRepository (RPC calls)
```

### Workflow Steps
1. **Validate Config** - Check all assets meet requirements
2. **Upload to Arweave** - Permanent storage via Irys
3. **Create Metadata** - Metaplex Token Metadata v0.4.0
4. **Create App NFT** - Mint parent NFT on-chain
5. **Create Release NFT** - Mint release NFT on-chain
6. **Submit to Portal** - HubSpot form with attestations

## Integration

### Navigation Setup
```kotlin
// In BubbleWrapperApp.kt
sealed class WizardScreen {
    object PublishingTest : WizardScreen()
}

// Navigation handler
when (currentWizard) {
    is WizardScreen.PublishingTest -> {
        PublishingTestScreen(
            onBack = { currentWizard = WizardScreen.None },
            walletManager = walletManager,
            solanaRepository = solanaRepository
        )
        return
    }
}
```

### From Publish Screen
```kotlin
// In PublishScreen.kt
fun PublishScreen(
    onBack: () -> Unit,
    onTestPublishing: () -> Unit = {}
) {
    // ...
    Button(onClick = onTestPublishing) {
        Text("Open Publishing Test Screen")
    }
}
```

## Safety Features

### Visual Warnings
- Simulation mode: 🧪 Teal/purple theme
- Production mode: ⚠️ Red warning theme
- Component status: Clear on/off indicators
- Real mode reminder: "This will create actual NFTs..."

### Confirmation Flow
1. Must explicitly toggle OFF simulation
2. Clear visual distinction (colors)
3. Button text changes ("REAL" vs "Simulated")
4. Warning text appears in production mode

## Testing Checklist

### Simulation Mode Tests
- ✅ Toggle components on/off individually
- ✅ Run with all components enabled
- ✅ Run with only Arweave enabled
- ✅ Run with only NFT creation enabled
- ✅ Verify progress updates
- ✅ Check mock result display

### Production Mode Tests (Use with caution!)
- ✅ Verify wallet connection required
- ✅ Check balance warning (<0.1 SOL)
- ✅ Test actual Arweave upload
- ✅ Test NFT creation with real signing
- ✅ Verify Explorer links work
- ✅ Confirm Portal submission

## Cost Estimates

### Arweave Storage (via Irys)
- Icon (512x512 PNG): ~$0.0001
- Banner (1200x600 PNG): ~$0.0002
- Screenshots (4x 1080p): ~$0.0005
- APK (~10MB): ~$0.02
- **Total**: ~$0.02

### Solana NFT Minting
- App NFT mint: ~0.005 SOL
- Release NFT mint: ~0.005 SOL
- Metadata accounts: ~0.01 SOL
- Transaction fees: ~0.00001 SOL
- **Total**: ~0.02 SOL

### Publisher Portal
- Free (HubSpot form submission)

**Grand Total**: ~0.04 SOL + ~$0.02 (~$4-5 USD at current prices)

## Troubleshooting

### "Unable to connect wallet"
- Ensure wallet app is installed
- Try reconnecting from Publish tab
- Check MWA compatibility

### "Insufficient balance"
- Need at least 0.15 SOL for publishing
- Check balance in wallet connection card

### "Upload failed"
- Check internet connection
- Verify Irys service status
- Try toggling Arweave uploads off

### "NFT creation failed"
- Wallet rejected transaction
- Insufficient SOL for fees
- RPC endpoint issues

### "Portal submission failed"
- Check all required fields filled
- Verify NFTs were created first
- Network connectivity issues

## Future Enhancements

### Planned Features
- [ ] Custom config editor (not hardcoded test data)
- [ ] Save/load test configurations
- [ ] History of past test runs
- [ ] Cost calculator with live SOL prices
- [ ] Asset picker for real files
- [ ] APK metadata auto-extraction
- [ ] Multi-wallet testing
- [ ] Devnet testing mode

### Improvements
- [ ] Better error messages
- [ ] Retry failed steps
- [ ] Cancel in-progress publishing
- [ ] Export results as JSON
- [ ] Share test results
- [ ] Analytics/metrics tracking

## Related Documentation
- [EMBEDDED_CLI_DOCUMENTATION.md](../EMBEDDED_CLI_DOCUMENTATION.md) - Complete CLI workflow docs
- [DappStoreCliWorkflow.kt](app/src/main/java/xyz/bluefoot/bubblewrapper/publishing/DappStoreCliWorkflow.kt) - Core workflow implementation
- [DappStorePublisher.kt](app/src/main/java/xyz/bluefoot/bubblewrapper/network/DappStorePublisher.kt) - NFT publishing logic

## Real-World Example
The MonkeMob app was successfully published using this exact workflow:
- **App NFT**: `ADZWhSTQJoppJhapEbUS69yLg5HM2nJ322nenGxQqhMs`
- **Release NFT**: `HjfgUUfyvwm7EWLcaxcxLdhn8A5tDZvbEkpDC4N7Kpx`
- **Status**: Submitted, awaiting review (3-4 business days)

This proves the workflow works on mainnet with real assets! 🎉
