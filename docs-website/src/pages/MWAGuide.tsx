import { motion } from 'framer-motion';
import { Section, CodeBlock, Callout } from '../components/UI';
import { ArrowLeft } from 'lucide-react';
import { Link } from 'react-router-dom';

export function MWAGuide() {
  return (
    <div className="max-w-4xl mx-auto px-4 py-8">
      {/* Back button */}
      <Link
        to="/"
        className="inline-flex items-center gap-2 text-[#8B92A5] hover:text-white transition-colors mb-8"
      >
        <ArrowLeft size={20} />
        <span>Back</span>
      </Link>

      {/* Header */}
      <motion.header
        initial={{ opacity: 0, y: 20 }}
        animate={{ opacity: 1, y: 0 }}
        className="mb-10"
      >
        <div className="flex items-center gap-3 mb-4">
          <span className="text-4xl">🔌</span>
          <h1 className="text-3xl sm:text-4xl font-bold gradient-text">
            MWA Connection Guide
          </h1>
        </div>
        <p className="text-lg text-[#8B92A5]">
          Complete guide to implementing Solana wallet connections with Mobile Wallet Adapter 2.0
        </p>
      </motion.header>

      {/* Table of Contents */}
      <motion.nav
        initial={{ opacity: 0 }}
        animate={{ opacity: 1 }}
        transition={{ delay: 0.2 }}
        className="p-5 rounded-2xl glass mb-10"
      >
        <h3 className="font-semibold mb-4 flex items-center gap-2">
          <span>📋</span> Quick Navigation
        </h3>
        <div className="grid grid-cols-2 sm:grid-cols-4 gap-2">
          {['Overview', 'Dependencies', 'Configuration', 'Errors'].map((item) => (
            <a
              key={item}
              href={`#${item.toLowerCase()}`}
              className="px-3 py-2 text-sm text-center rounded-lg bg-white/5 hover:bg-white/10 transition-colors text-[#8B92A5] hover:text-white"
            >
              {item}
            </a>
          ))}
        </div>
      </motion.nav>

      {/* Content */}
      <div className="space-y-2">
        <Section id="overview" title="Overview">
          <p className="text-[#8B92A5] mb-6">
            Mobile Wallet Adapter (MWA) is the standard protocol for connecting Android apps to Solana wallets like Phantom, Solflare, and Backpack.
          </p>
          
          <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
            {[
              { term: 'identityUri', desc: 'Absolute HTTPS URL of your dApp' },
              { term: 'iconUri', desc: 'RELATIVE path to your icon' },
              { term: 'identityName', desc: 'Name shown in wallet prompts' },
              { term: 'blockchain', desc: 'Network (Mainnet/Testnet/Devnet)' },
            ].map((item) => (
              <div key={item.term} className="p-4 rounded-xl glass">
                <code className="text-[#14F195] text-sm">{item.term}</code>
                <p className="text-sm text-[#8B92A5] mt-1">{item.desc}</p>
              </div>
            ))}
          </div>
        </Section>

        <Section id="dependencies" title="Dependencies">
          <p className="text-[#8B92A5] mb-4">Add to your <code className="text-[#14F195]">app/build.gradle.kts</code>:</p>
          <div className="relative">
            <CodeBlock language="kotlin">{`dependencies {
    // Mobile Wallet Adapter 2.0
    implementation("com.solanamobile:mobile-wallet-adapter-clientlib-ktx:2.0.3")
}`}</CodeBlock>
          </div>
        </Section>

        <Section id="configuration" title="Configuration Checklist">
          <Callout type="error" title="Critical: iconUri must be RELATIVE!">
            This is the most common error. The wallet validates that <code>iconUri</code> is a relative path, not an absolute URL.
          </Callout>

          <div className="overflow-x-auto mb-6">
            <table className="w-full text-sm">
              <thead>
                <tr className="border-b border-white/10">
                  <th className="text-left py-3 px-4 text-red-400">❌ Wrong</th>
                  <th className="text-left py-3 px-4 text-[#14F195]">✅ Correct</th>
                </tr>
              </thead>
              <tbody className="text-[#8B92A5]">
                <tr className="border-b border-white/10">
                  <td className="py-3 px-4"><code>https://example.com/icon.png</code></td>
                  <td className="py-3 px-4"><code>icon.png</code></td>
                </tr>
                <tr>
                  <td className="py-3 px-4"><code>Uri.parse("$URL/$ICON")</code></td>
                  <td className="py-3 px-4"><code>Uri.parse(ICON)</code></td>
                </tr>
              </tbody>
            </table>
          </div>

          <h3 className="font-semibold mb-4 flex items-center gap-2">
            <span className="text-[#14F195]">1.</span> AndroidManifest.xml
          </h3>
          <p className="text-[#8B92A5] mb-4">Add <code className="text-[#14F195]">&lt;queries&gt;</code> for wallet discovery on Android 11+:</p>
          <CodeBlock language="xml">{`<queries>
    <intent>
        <action android:name="android.intent.action.VIEW" />
        <category android:name="android.intent.category.BROWSABLE" />
        <data android:scheme="solana-wallet" />
    </intent>
    <package android:name="app.phantom" />
    <package android:name="com.solflare.mobile" />
</queries>`}</CodeBlock>

          <h3 className="font-semibold mb-4 mt-8 flex items-center gap-2">
            <span className="text-[#14F195]">2.</span> App Identity
          </h3>
          <CodeBlock language="kotlin">{`companion object {
    private const val APP_IDENTITY_NAME = "Your App"
    private const val APP_IDENTITY_URI = "https://yourdomain.com"
    private const val APP_IDENTITY_ICON = "favicon.ico"  // RELATIVE!
}`}</CodeBlock>

          <h3 className="font-semibold mb-4 mt-8 flex items-center gap-2">
            <span className="text-[#14F195]">3.</span> Wallet Adapter Init
          </h3>
          <CodeBlock language="kotlin">{`private val walletAdapter = MobileWalletAdapter(
    connectionIdentity = ConnectionIdentity(
        identityUri = Uri.parse(APP_IDENTITY_URI),
        iconUri = Uri.parse(APP_IDENTITY_ICON),  // Relative!
        identityName = APP_IDENTITY_NAME
    )
).apply {
    blockchain = Solana.Mainnet  // Default is Devnet!
}`}</CodeBlock>

          <h3 className="font-semibold mb-4 mt-8 flex items-center gap-2">
            <span className="text-[#14F195]">4.</span> ActivityResultSender
          </h3>
          <Callout type="warning" title="Lifecycle Critical">
            Create <code>ActivityResultSender</code> BEFORE <code>setContent{`{}`}</code> in onCreate()
          </Callout>
          <CodeBlock language="kotlin">{`class MainActivity : ComponentActivity() {
    private lateinit var activityResultSender: ActivityResultSender
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // BEFORE setContent!
        activityResultSender = ActivityResultSender(this)
        
        setContent { MyApp(activityResultSender) }
    }
}`}</CodeBlock>
        </Section>

        <Section id="errors" title="Common Errors">
          <div className="space-y-4">
            <motion.div
              initial={{ opacity: 0, x: -20 }}
              whileInView={{ opacity: 1, x: 0 }}
              viewport={{ once: true }}
              className="p-5 rounded-xl border border-red-500/30 bg-red-500/5"
            >
              <h4 className="font-mono text-sm text-red-400 mb-2">
                "If non-null, iconRelativeUri must be a relative Uri"
              </h4>
              <p className="text-[#8B92A5] text-sm mb-3">
                <strong className="text-white">Cause:</strong> Passing absolute URL for iconUri
              </p>
              <p className="text-[#8B92A5] text-sm">
                <strong className="text-[#14F195]">Fix:</strong> Use <code>Uri.parse("favicon.ico")</code> not <code>Uri.parse("https://...")</code>
              </p>
            </motion.div>

            <motion.div
              initial={{ opacity: 0, x: -20 }}
              whileInView={{ opacity: 1, x: 0 }}
              viewport={{ once: true }}
              transition={{ delay: 0.1 }}
              className="p-5 rounded-xl border border-red-500/30 bg-red-500/5"
            >
              <h4 className="font-mono text-sm text-red-400 mb-2">
                "NoWalletFound" despite having wallets installed
              </h4>
              <p className="text-[#8B92A5] text-sm mb-3">
                <strong className="text-white">Cause:</strong> Missing &lt;queries&gt; in AndroidManifest.xml
              </p>
              <p className="text-[#8B92A5] text-sm">
                <strong className="text-[#14F195]">Fix:</strong> Add wallet package queries for Android 11+
              </p>
            </motion.div>

            <motion.div
              initial={{ opacity: 0, x: -20 }}
              whileInView={{ opacity: 1, x: 0 }}
              viewport={{ once: true }}
              transition={{ delay: 0.2 }}
              className="p-5 rounded-xl border border-red-500/30 bg-red-500/5"
            >
              <h4 className="font-mono text-sm text-red-400 mb-2">
                Wallet shows Testnet instead of Mainnet
              </h4>
              <p className="text-[#8B92A5] text-sm mb-3">
                <strong className="text-white">Cause:</strong> Default blockchain is Devnet
              </p>
              <p className="text-[#8B92A5] text-sm">
                <strong className="text-[#14F195]">Fix:</strong> Set <code>blockchain = Solana.Mainnet</code>
              </p>
            </motion.div>
          </div>
        </Section>

        <Section id="testing" title="Testing">
          <Callout type="success" title="Success Indicators in Logcat">
            <code className="block text-xs mt-2">
              PhantomMWAModule: onAuthorizeRequest<br />
              Scenario: Authorize request completed successfully<br />
              AuthRepositoryImpl: Returning auth token
            </code>
          </Callout>

          <p className="text-[#8B92A5] mb-4">Filter logcat for MWA events:</p>
          <CodeBlock language="bash">{`adb logcat | grep -E "MobileWallet|Phantom|Authorize|iconUri"`}</CodeBlock>
        </Section>

        <Section id="resources" title="Resources">
          <div className="grid gap-3">
            {[
              { name: 'MWA GitHub', url: 'https://github.com/solana-mobile/mobile-wallet-adapter' },
              { name: 'MWA Specification', url: 'https://github.com/solana-mobile/mobile-wallet-adapter/blob/main/spec/spec.md' },
              { name: 'Solana Mobile Docs', url: 'https://docs.solanamobile.com' },
            ].map((link) => (
              <a
                key={link.name}
                href={link.url}
                target="_blank"
                rel="noopener noreferrer"
                className="flex items-center justify-between p-4 rounded-xl glass hover:border-[#9945FF]/50 transition-colors group"
              >
                <span>{link.name}</span>
                <span className="text-[#8B92A5] group-hover:text-[#9945FF] transition-colors">→</span>
              </a>
            ))}
          </div>
        </Section>
      </div>

      {/* Footer */}
      <footer className="mt-16 pt-8 border-t border-white/5 text-center">
        <p className="text-sm text-[#8B92A5]">
          🫧 <span className="font-medium text-white">BubbleWrapper</span> by{' '}
          <a href="https://bluefoot.xyz" className="text-[#9945FF] hover:underline">
            Bluefoot Labs
          </a>
        </p>
      </footer>
    </div>
  );
}
