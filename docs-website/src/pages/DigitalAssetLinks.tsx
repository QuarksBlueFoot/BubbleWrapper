import { motion } from 'framer-motion';
import { Section, CodeBlock, Callout } from '../components/UI';
import { ArrowLeft } from 'lucide-react';
import { Link } from 'react-router-dom';

export function DigitalAssetLinks() {
  return (
    <div className="max-w-4xl mx-auto px-4 py-8">
      <Link
        to="/"
        className="inline-flex items-center gap-2 text-[#8B92A5] hover:text-white transition-colors mb-8"
      >
        <ArrowLeft size={20} />
        <span>Back</span>
      </Link>

      <motion.header
        initial={{ opacity: 0, y: 20 }}
        animate={{ opacity: 1, y: 0 }}
        className="mb-10"
      >
        <div className="flex items-center gap-3 mb-4">
          <span className="text-4xl">🔗</span>
          <h1 className="text-3xl sm:text-4xl font-bold gradient-text">
            Digital Asset Links
          </h1>
        </div>
        <p className="text-lg text-[#8B92A5]">
          Configure DAL for fullscreen TWA mode without browser UI elements
        </p>
      </motion.header>

      <div className="space-y-2">
        <Section id="what" title="What is DAL?">
          <p className="text-[#8B92A5] mb-4">
            Digital Asset Links (DAL) creates a verified connection between your website and Android app, enabling fullscreen TWA mode without browser UI elements.
          </p>
          
          <Callout type="info" title="MonkeMob Example">
            The MonkeMob sample includes a pre-configured DAL file at <code className="text-[#9945FF]">sample-pwa/android-twa/assetlinks-monkemob.json</code> that you can use as a template.
          </Callout>

          <div className="grid gap-3 sm:grid-cols-2 mt-6">
            {[
              { icon: '📱', text: 'Fullscreen TWA (no browser bar)' },
              { icon: '🔒', text: 'Verified app-website association' },
              { icon: '🚀', text: 'Better user experience' },
              { icon: '✅', text: 'Required for dApp Store' },
            ].map((item) => (
              <div key={item.text} className="flex items-center gap-3 p-4 rounded-xl glass">
                <span className="text-2xl">{item.icon}</span>
                <span className="text-[#8B92A5]">{item.text}</span>
              </div>
            ))}
          </div>
        </Section>

        <Section id="fingerprint" title="Get Your Signing Certificate">
          <p className="text-[#8B92A5] mb-4">
            Extract the SHA-256 fingerprint from your keystore. BubbleWrapper app can do this automatically, or use keytool:
          </p>

          <Callout type="success" title="Easy Way: BubbleWrapper App">
            The BubbleWrapper app includes a built-in tool to extract SHA-256 fingerprints from keystores - no command line needed!
          </Callout>

          <h3 className="font-semibold mb-4 mt-6">Debug keystore:</h3>
          <CodeBlock language="bash">{`keytool -list -v \\
  -keystore ~/.android/debug.keystore \\
  -alias androiddebugkey \\
  -storepass android`}</CodeBlock>

          <h3 className="font-semibold mb-4 mt-6">Release keystore:</h3>
          <CodeBlock language="bash">{`keytool -list -v \\
  -keystore release-keystore.jks \\
  -alias my-app-key`}</CodeBlock>

          <Callout type="info" title="Find the SHA-256 fingerprint">
            Look for the line starting with <code>SHA256:</code> in the output. It looks like:
            <code className="block mt-2 text-xs break-all">
              AB:CD:12:34:56:78:90:AB:CD:EF:12:34:56:78:90:AB:CD:EF:12:34:56:78:90:AB:CD:EF:12:34:56:78:90:AB
            </code>
          </Callout>
        </Section>

        <Section id="create" title="Create assetlinks.json">
          <p className="text-[#8B92A5] mb-4">
            Create the following file at <code className="text-[#14F195]">/.well-known/assetlinks.json</code> on your domain:
          </p>

          <CodeBlock language="json">{`[{
  "relation": ["delegate_permission/common.handle_all_urls"],
  "target": {
    "namespace": "android_app",
    "package_name": "com.example.myapp",
    "sha256_cert_fingerprints": [
      "AB:CD:12:34:56:78:90:AB:CD:EF:12:34:56:78:90:AB:CD:EF:12:34:56:78:90:AB:CD:EF:12:34:56:78:90:AB"
    ]
  }
}]`}</CodeBlock>

          <Callout type="warning" title="Include both debug and release fingerprints">
            For development, add both fingerprints to the array so the app works with both builds.
          </Callout>
        </Section>

        <Section id="hosting" title="Hosting Requirements">
          <div className="space-y-4">
            <div className="p-4 rounded-xl glass">
              <h4 className="font-semibold flex items-center gap-2 mb-2">
                <span className="text-[#14F195]">✓</span> Must be HTTPS
              </h4>
              <p className="text-sm text-[#8B92A5]">The file must be served over HTTPS (not HTTP)</p>
            </div>

            <div className="p-4 rounded-xl glass">
              <h4 className="font-semibold flex items-center gap-2 mb-2">
                <span className="text-[#14F195]">✓</span> Correct Content-Type
              </h4>
              <p className="text-sm text-[#8B92A5]">
                Must be <code>application/json</code>
              </p>
            </div>

            <div className="p-4 rounded-xl glass">
              <h4 className="font-semibold flex items-center gap-2 mb-2">
                <span className="text-[#14F195]">✓</span> Exact path
              </h4>
              <p className="text-sm text-[#8B92A5]">
                Must be at <code>https://yourdomain.com/.well-known/assetlinks.json</code>
              </p>
            </div>

            <div className="p-4 rounded-xl glass">
              <h4 className="font-semibold flex items-center gap-2 mb-2">
                <span className="text-[#14F195]">✓</span> No redirects
              </h4>
              <p className="text-sm text-[#8B92A5]">The URL must not redirect</p>
            </div>
          </div>
        </Section>

        <Section id="verify" title="Verify Your Setup">
          <p className="text-[#8B92A5] mb-4">
            Use Google's verification tool to check your configuration:
          </p>

          <a
            href="https://developers.google.com/digital-asset-links/tools/generator"
            target="_blank"
            rel="noopener noreferrer"
            className="inline-flex items-center gap-2 px-5 py-3 rounded-xl bg-gradient-to-r from-[#9945FF] to-[#14F195] text-white font-semibold hover:opacity-90 transition-opacity"
          >
            Open DAL Verification Tool →
          </a>

          <Callout type="success" title="Testing on device">
            After installing your APK, the app should open in fullscreen mode without the browser address bar if DAL is configured correctly.
          </Callout>
        </Section>

        <Section id="troubleshooting" title="Troubleshooting">
          <div className="space-y-4">
            <motion.div
              initial={{ opacity: 0, x: -20 }}
              whileInView={{ opacity: 1, x: 0 }}
              viewport={{ once: true }}
              className="p-5 rounded-xl border border-red-500/30 bg-red-500/5"
            >
              <h4 className="font-mono text-sm text-red-400 mb-2">
                Browser bar still visible
              </h4>
              <p className="text-[#8B92A5] text-sm">
                <strong className="text-[#14F195]">Fix:</strong> Clear Chrome's cache and app data, then reinstall the APK
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
                Verification tool shows error
              </h4>
              <p className="text-[#8B92A5] text-sm">
                <strong className="text-[#14F195]">Fix:</strong> Check that the package name and fingerprint match exactly
              </p>
            </motion.div>
          </div>
        </Section>
      </div>

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
