export function TWAGuide() {
  return (
    <div className="space-y-8">
        <div className="text-center mb-12">
          <h1 className="text-4xl font-bold text-white mb-4">
            📱 Solana dApp Store Publishing Guide
          </h1>
          <p className="text-xl text-gray-300">
            Publish your PWA to the Solana Mobile dApp Store
          </p>
        </div>

        {/* Overview */}
        <section className="bg-gray-800/50 rounded-xl p-6 border border-gray-700">
          <h2 className="text-2xl font-bold text-white mb-4">📋 Overview</h2>
          <p className="text-gray-300 mb-4">
            The <strong>Solana dApp Store</strong> is a decentralized app store for Solana mobile devices 
            (Saga, Seeker). Unlike Google Play, it has no 30% fees and is designed for web3 apps.
          </p>
          <div className="grid md:grid-cols-3 gap-4 mt-6">
            <div className="bg-gray-900/50 rounded-lg p-4 border border-gray-600">
              <div className="text-3xl mb-2">💸</div>
              <h3 className="font-semibold text-white">0% Fees</h3>
              <p className="text-gray-400 text-sm">No platform fees - keep 100% of your revenue</p>
            </div>
            <div className="bg-gray-900/50 rounded-lg p-4 border border-gray-600">
              <div className="text-3xl mb-2">🔐</div>
              <h3 className="font-semibold text-white">Web3 Native</h3>
              <p className="text-gray-400 text-sm">Built for crypto apps with MWA support</p>
            </div>
            <div className="bg-gray-900/50 rounded-lg p-4 border border-gray-600">
              <div className="text-3xl mb-2">⚡</div>
              <h3 className="font-semibold text-white">Fast Publishing</h3>
              <p className="text-gray-400 text-sm">On-chain publishing, no app review delays</p>
            </div>
          </div>
        </section>

        {/* Step 1: PWA Requirements */}
        <section className="bg-gray-800/50 rounded-xl p-6 border border-gray-700">
          <h2 className="text-2xl font-bold text-white mb-4">
            Step 1: PWA Requirements ✅
          </h2>
          <p className="text-gray-300 mb-4">
            Your website must be a valid Progressive Web App. Modern frameworks like Vite make this easy with plugins.
          </p>

          <div className="space-y-6">
            {/* Option 1: Vite PWA Plugin (Recommended) */}
            <div>
              <h3 className="text-lg font-semibold text-purple-400 mb-2">
                1.1 Vite + PWA Plugin (Recommended)
              </h3>
              <pre className="bg-gray-900 rounded-lg p-4 overflow-x-auto text-sm">
                <code className="text-green-400">{`npm install -D vite-plugin-pwa

// vite.config.ts
import { VitePWA } from 'vite-plugin-pwa'

export default defineConfig({
  plugins: [
    VitePWA({
      registerType: 'autoUpdate',
      manifest: {
        name: 'Your dApp Name',
        short_name: 'dApp',
        description: 'Your Solana dApp description',
        start_url: '/',
        display: 'standalone',
        theme_color: '#0B0F1A',
        background_color: '#0B0F1A',
        icons: [
          { src: '/icons/icon-192.png', sizes: '192x192', type: 'image/png' },
          { src: '/icons/icon-512.png', sizes: '512x512', type: 'image/png', purpose: 'any maskable' }
        ]
      },
      workbox: {
        runtimeCaching: [
          { urlPattern: /^https:\\/\\/fonts\\./, handler: 'CacheFirst' }
        ]
      }
    })
  ]
})`}</code>
              </pre>
              <p className="text-gray-400 text-sm mt-2">
                ✅ Used in MonkeMob sample - auto-generates manifest and service worker with Workbox.
              </p>
            </div>

            {/* Option 2: Manual Manifest */}
            <div>
              <h3 className="text-lg font-semibold text-purple-400 mb-2">
                1.2 Manual <code className="bg-gray-900 px-2 py-1 rounded">/manifest.json</code> (Alternative)
              </h3>
              <pre className="bg-gray-900 rounded-lg p-4 overflow-x-auto text-sm">
                <code className="text-green-400">{`{
  "name": "Your dApp Name",
  "short_name": "dApp",
  "description": "Your Solana dApp description",
  "start_url": "/",
  "display": "standalone",
  "orientation": "portrait",
  "background_color": "#000000",
  "theme_color": "#9945FF",
  "icons": [
    {
      "src": "/icons/icon-192.png",
      "sizes": "192x192",
      "type": "image/png",
      "purpose": "any maskable"
    },
    {
      "src": "/icons/icon-512.png",
      "sizes": "512x512",
      "type": "image/png",
      "purpose": "any maskable"
    }
  ]
}`}</code>
              </pre>
            </div>

            {/* Link manifest in HTML */}
            <div>
              <h3 className="text-lg font-semibold text-purple-400 mb-2">
                1.3 Link manifest in your HTML <code className="bg-gray-900 px-2 py-1 rounded">&lt;head&gt;</code>
              </h3>
              <pre className="bg-gray-900 rounded-lg p-4 overflow-x-auto text-sm">
                <code className="text-green-400">{`<link rel="manifest" href="/manifest.json">
<meta name="theme-color" content="#9945FF">`}</code>
              </pre>
              <p className="text-gray-400 text-sm mt-2">
                (Vite PWA plugin handles this automatically)
              </p>
            </div>

            {/* Service Worker Notes */}
            <div>
              <h3 className="text-lg font-semibold text-purple-400 mb-2">
                1.4 Service Worker (Automatic with VitePWA)
              </h3>
              <p className="text-gray-400 text-sm">
                VitePWA generates a Workbox-powered service worker with runtime caching, offline support, and precaching. 
                No manual setup required! The plugin handles registration and updates automatically with <code className="bg-gray-900 px-2 py-1 rounded text-green-400">registerType: 'autoUpdate'</code>.
              </p>
            </div>
          </div>
        </section>

        {/* Step 2: Generate TWA with Bubblewrap */}
        <section className="bg-gray-800/50 rounded-xl p-6 border border-gray-700">
          <h2 className="text-2xl font-bold text-white mb-4">
            Step 2: Generate Android App with Bubblewrap 🤖
          </h2>
          <p className="text-gray-300 mb-4">
            Use Google's Bubblewrap CLI to wrap your PWA as an Android app. MonkeMob sample uses this exact workflow.
          </p>

          <div className="space-y-4">
            <div>
              <h3 className="text-lg font-semibold text-purple-400 mb-2">2.1 Install Bubblewrap</h3>
              <pre className="bg-gray-900 rounded-lg p-4 overflow-x-auto text-sm">
                <code className="text-green-400">npm install -g @googlechrome/bubblewrap</code>
              </pre>
            </div>

            <div>
              <h3 className="text-lg font-semibold text-purple-400 mb-2">2.2 Initialize Project</h3>
              <pre className="bg-gray-900 rounded-lg p-4 overflow-x-auto text-sm">
                <code className="text-green-400">{`mkdir my-dapp-android && cd my-dapp-android
bubblewrap init --manifest=https://yourdomain.com/manifest.json`}</code>
              </pre>
              <p className="text-gray-400 text-sm mt-2">
                Follow the prompts for package name (<code className="bg-gray-800 px-2 py-0.5 rounded text-green-400">me.monkemob.twa</code>), signing key, theme colors, etc. See <code className="bg-gray-800 px-2 py-0.5 rounded text-purple-400">sample-pwa/BUBBLEWRAP_PROMPTS.md</code> for exact answers used in MonkeMob.
              </p>
            </div>

            <div>
              <h3 className="text-lg font-semibold text-purple-400 mb-2">2.3 Build the APK</h3>
              <pre className="bg-gray-900 rounded-lg p-4 overflow-x-auto text-sm">
                <code className="text-green-400">bubblewrap build</code>
              </pre>
              <p className="text-gray-400 text-sm mt-2">
                Creates a signed APK in <code className="bg-gray-800 px-2 py-0.5 rounded text-green-400">app-release-signed.apk</code>. The generated Android project is in <code className="bg-gray-800 px-2 py-0.5 rounded text-purple-400">sample-pwa/android-twa-generated/</code>.
              </p>
            </div>
          </div>
        </section>

        {/* Step 3: Digital Asset Links */}
        <section className="bg-gray-800/50 rounded-xl p-6 border border-gray-700">
          <h2 className="text-2xl font-bold text-white mb-4">
            Step 3: Digital Asset Links 🔐
          </h2>
          <p className="text-gray-300 mb-4">
            This file proves you own the domain and enables fullscreen mode (no browser bar).
          </p>

          <div className="space-y-4">
            <div>
              <h3 className="text-lg font-semibold text-purple-400 mb-2">
                3.1 Get your signing key fingerprint
              </h3>
              <pre className="bg-gray-900 rounded-lg p-4 overflow-x-auto text-sm">
                <code className="text-green-400">{`keytool -list -v -keystore android.keystore -alias android

# Copy the SHA256 fingerprint`}</code>
              </pre>
            </div>

            <div>
              <h3 className="text-lg font-semibold text-purple-400 mb-2">
                3.2 Create <code className="bg-gray-900 px-2 py-1 rounded">/.well-known/assetlinks.json</code>
              </h3>
              <pre className="bg-gray-900 rounded-lg p-4 overflow-x-auto text-sm">
                <code className="text-green-400">{`[
  {
    "relation": ["delegate_permission/common.handle_all_urls"],
    "target": {
      "namespace": "android_app",
      "package_name": "com.yourdomain.app",
      "sha256_cert_fingerprints": [
        "YOUR:SHA256:FINGERPRINT:HERE"
      ]
    }
  }
]`}</code>
              </pre>
            </div>
          </div>
        </section>

        {/* Step 4: Solana dApp Store */}
        <section className="bg-gradient-to-r from-[#9945FF]/20 to-[#14F195]/20 rounded-xl p-6 border border-[#9945FF]/50">
          <h2 className="text-2xl font-bold text-white mb-4">
            Step 4: Publish to Solana dApp Store 🚀
          </h2>
          
          <div className="space-y-6">
            <div className="p-4 bg-gray-900/50 rounded-lg border border-gray-600">
              <h3 className="font-semibold text-[#14F195] mb-2">Prerequisites</h3>
              <ul className="list-disc list-inside text-gray-300 space-y-1">
                <li>A Solana wallet with some SOL for transaction fees</li>
                <li>Your signed APK file</li>
                <li>App icons and screenshots</li>
              </ul>
            </div>

            <div className="flex items-start space-x-3">
              <span className="bg-[#9945FF] text-white rounded-full w-8 h-8 flex items-center justify-center flex-shrink-0">1</span>
              <div>
                <h3 className="font-semibold text-white">Install the dApp Store CLI</h3>
                <pre className="bg-gray-900 rounded-lg p-3 mt-2 overflow-x-auto text-sm">
                  <code className="text-green-400">npm install -g @solana-mobile/dapp-store-cli</code>
                </pre>
              </div>
            </div>

            <div className="flex items-start space-x-3">
              <span className="bg-[#9945FF] text-white rounded-full w-8 h-8 flex items-center justify-center flex-shrink-0">2</span>
              <div>
                <h3 className="font-semibold text-white">Create Publisher Account</h3>
                <pre className="bg-gray-900 rounded-lg p-3 mt-2 overflow-x-auto text-sm">
                  <code className="text-green-400">{`npx dapp-store create publisher`}</code>
                </pre>
                <p className="text-gray-400 text-sm mt-2">
                  This creates an on-chain publisher NFT linked to your wallet.
                </p>
              </div>
            </div>

            <div className="flex items-start space-x-3">
              <span className="bg-[#9945FF] text-white rounded-full w-8 h-8 flex items-center justify-center flex-shrink-0">3</span>
              <div>
                <h3 className="font-semibold text-white">Create App Entry</h3>
                <pre className="bg-gray-900 rounded-lg p-3 mt-2 overflow-x-auto text-sm">
                  <code className="text-green-400">{`npx dapp-store create app`}</code>
                </pre>
                <p className="text-gray-400 text-sm mt-2">
                  Follow prompts for app name, description, category, and icons.
                </p>
              </div>
            </div>

            <div className="flex items-start space-x-3">
              <span className="bg-[#9945FF] text-white rounded-full w-8 h-8 flex items-center justify-center flex-shrink-0">4</span>
              <div>
                <h3 className="font-semibold text-white">Publish Release</h3>
                <pre className="bg-gray-900 rounded-lg p-3 mt-2 overflow-x-auto text-sm">
                  <code className="text-green-400">{`npx dapp-store publish release \\
  --keypair <your-wallet.json> \\
  --build-path <your-app.apk>`}</code>
                </pre>
                <p className="text-gray-400 text-sm mt-2">
                  Uploads your APK and creates an on-chain release.
                </p>
              </div>
            </div>
          </div>

          <div className="mt-6 p-4 bg-[#14F195]/10 border border-[#14F195]/50 rounded-lg">
            <p className="text-[#14F195] text-sm">
              📖 <strong>Full Documentation:</strong>{' '}
              <a 
                href="https://github.com/solana-mobile/dapp-publishing" 
                target="_blank"
                className="underline hover:no-underline"
              >
                github.com/solana-mobile/dapp-publishing
              </a>
            </p>
          </div>
        </section>

        {/* dApp Store Config */}
        <section className="bg-gray-800/50 rounded-xl p-6 border border-gray-700">
          <h2 className="text-2xl font-bold text-white mb-4">
            📄 dApp Store Configuration Files
          </h2>
          <p className="text-gray-300 mb-4">
            The CLI creates these config files in your project:
          </p>

          <div className="space-y-4">
            <div>
              <h3 className="text-lg font-semibold text-purple-400 mb-2">
                config.yaml (App metadata)
              </h3>
              <pre className="bg-gray-900 rounded-lg p-4 overflow-x-auto text-sm">
                <code className="text-green-400">{`publisher:
  name: "Your Company"
  website: "https://yourdomain.com"
  
app:
  name: "Your dApp Name"
  description: "A Solana dApp that does amazing things"
  category: "defi"  # defi, nft, gaming, social, tools, etc.
  
android_details:
  package_name: "com.yourdomain.app"
  min_sdk: 24
  
solana_mobile_dapp_publisher_portal:
  google_store_package: "com.yourdomain.app"
  testing_instructions: "Connect wallet and tap Sign In"`}</code>
              </pre>
            </div>
          </div>
        </section>

        {/* Quick Checklist */}
        <section className="bg-gradient-to-r from-purple-900/50 to-[#14F195]/20 rounded-xl p-6 border border-purple-500">
          <h2 className="text-2xl font-bold text-white mb-4">📝 Publishing Checklist</h2>
          <div className="grid md:grid-cols-2 gap-4">
            <div>
              <h3 className="font-semibold text-purple-300 mb-2">Website Requirements</h3>
              <ul className="space-y-2 text-gray-300">
                <li>☐ HTTPS enabled</li>
                <li>☐ manifest.json created & linked</li>
                <li>☐ Service worker registered</li>
                <li>☐ 192x192 and 512x512 icons</li>
                <li>☐ assetlinks.json at /.well-known/</li>
                <li>☐ MWA wallet integration working</li>
              </ul>
            </div>
            <div>
              <h3 className="font-semibold text-[#14F195] mb-2">dApp Store Requirements</h3>
              <ul className="space-y-2 text-gray-300">
                <li>☐ Bubblewrap project generated</li>
                <li>☐ Signing keystore created</li>
                <li>☐ APK built & tested on device</li>
                <li>☐ Publisher account created</li>
                <li>☐ App icons (512x512)</li>
                <li>☐ Screenshots (1080x1920)</li>
              </ul>
            </div>
          </div>
        </section>

        {/* Solana Mobile Resources */}
        <section className="bg-gray-800/50 rounded-xl p-6 border border-gray-700">
          <h2 className="text-2xl font-bold text-white mb-4">🔗 Solana Mobile Resources</h2>
          <div className="grid md:grid-cols-2 gap-4">
            <a 
              href="https://github.com/solana-mobile/dapp-publishing" 
              target="_blank"
              className="flex items-center gap-3 p-4 bg-gray-900/50 rounded-lg border border-gray-600 hover:border-purple-500 transition-colors"
            >
              <span className="text-2xl">📦</span>
              <div>
                <div className="font-semibold text-white">dApp Publishing CLI</div>
                <div className="text-gray-400 text-sm">Official publishing tools</div>
              </div>
            </a>
            <a 
              href="https://github.com/solana-mobile/mobile-wallet-adapter" 
              target="_blank"
              className="flex items-center gap-3 p-4 bg-gray-900/50 rounded-lg border border-gray-600 hover:border-purple-500 transition-colors"
            >
              <span className="text-2xl">🔗</span>
              <div>
                <div className="font-semibold text-white">Mobile Wallet Adapter</div>
                <div className="text-gray-400 text-sm">Connect to Solana wallets</div>
              </div>
            </a>
            <a 
              href="https://docs.solanamobile.com" 
              target="_blank"
              className="flex items-center gap-3 p-4 bg-gray-900/50 rounded-lg border border-gray-600 hover:border-purple-500 transition-colors"
            >
              <span className="text-2xl">📚</span>
              <div>
                <div className="font-semibold text-white">Solana Mobile Docs</div>
                <div className="text-gray-400 text-sm">Full documentation</div>
              </div>
            </a>
            <a 
              href="https://discord.gg/solanamobile" 
              target="_blank"
              className="flex items-center gap-3 p-4 bg-gray-900/50 rounded-lg border border-gray-600 hover:border-purple-500 transition-colors"
            >
              <span className="text-2xl">💬</span>
              <div>
                <div className="font-semibold text-white">Solana Mobile Discord</div>
                <div className="text-gray-400 text-sm">Get help from the community</div>
              </div>
            </a>
          </div>
        </section>

        {/* Need Help */}
        <section className="text-center py-8">
          <p className="text-gray-400">
            Need wallet integration? Check the{' '}
            <a href="/mwa-guide" className="text-purple-400 hover:underline">
              MWA Integration Guide
            </a>
            {' '}or{' '}
            <a href="/digital-asset-links" className="text-purple-400 hover:underline">
              Digital Asset Links Setup
            </a>
          </p>
        </section>
      </div>
  );
}
