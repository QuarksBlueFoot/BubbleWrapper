import { motion } from 'framer-motion';
import { Section, CodeBlock, Callout } from '../components/UI';
import { ArrowLeft } from 'lucide-react';
import { Link } from 'react-router-dom';

export function PublishingGuide() {
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
          <span className="text-4xl">🚀</span>
          <h1 className="text-3xl sm:text-4xl font-bold gradient-text">
            Publishing Guide
          </h1>
        </div>
        <p className="text-lg text-[#8B92A5]">
          Complete guide for submitting your app to the Solana dApp Store
        </p>
      </motion.header>

      <div className="space-y-2">
        <Section id="prerequisites" title="Prerequisites">
          <div className="grid gap-4 sm:grid-cols-2">
            {[
              { icon: '☕', title: 'Java 17+', desc: 'Required for Gradle builds' },
              { icon: '📱', title: 'Android SDK', desc: 'API 33 or higher' },
              { icon: '🔑', title: 'Release Keystore', desc: 'For signing APKs' },
              { icon: '🌐', title: 'HTTPS Hosting', desc: 'For PWA deployment' },
            ].map((item) => (
              <div key={item.title} className="p-4 rounded-xl glass">
                <div className="text-2xl mb-2">{item.icon}</div>
                <h4 className="font-semibold">{item.title}</h4>
                <p className="text-sm text-[#8B92A5]">{item.desc}</p>
              </div>
            ))}
          </div>
        </Section>

        <Section id="keystore" title="Creating a Release Keystore">
          <Callout type="warning" title="Keep Your Keystore Safe!">
            Store your keystore password securely. If lost, you cannot update your app.
          </Callout>

          <CodeBlock language="bash">{`keytool -genkeypair -v \\
  -keystore release-keystore.jks \\
  -keyalg RSA -keysize 2048 \\
  -validity 10000 \\
  -alias my-app-key`}</CodeBlock>

          <p className="text-[#8B92A5] mt-4">
            You'll be prompted for passwords and certificate information.
          </p>
        </Section>

        <Section id="building" title="Building Release APK">
          <h3 className="font-semibold mb-4">1. Configure signing in build.gradle.kts</h3>
          <CodeBlock language="kotlin">{`android {
    signingConfigs {
        create("release") {
            storeFile = file("release-keystore.jks")
            storePassword = System.getenv("KEYSTORE_PASSWORD")
            keyAlias = "my-app-key"
            keyPassword = System.getenv("KEY_PASSWORD")
        }
    }
    
    buildTypes {
        release {
            signingConfig = signingConfigs.getByName("release")
            isMinifyEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"))
        }
    }
}`}</CodeBlock>

          <h3 className="font-semibold mb-4 mt-8">2. Build the APK</h3>
          <CodeBlock language="bash">{`./gradlew assembleRelease`}</CodeBlock>

          <p className="text-[#8B92A5] mt-4">
            Output: <code className="text-[#14F195]">app/build/outputs/apk/release/app-release.apk</code>
          </p>
        </Section>

        <Section id="checklist" title="Submission Checklist">
          <div className="space-y-3">
            {[
              { done: true, text: 'App icon (512x512 PNG)' },
              { done: true, text: 'Feature graphic (1024x500 PNG)' },
              { done: true, text: 'Screenshots (phone & tablet)' },
              { done: true, text: 'App description (short & full)' },
              { done: true, text: 'Privacy policy URL' },
              { done: true, text: 'Release APK signed with release key' },
              { done: true, text: 'Tested on real device' },
              { done: true, text: 'Digital Asset Links configured' },
            ].map((item, i) => (
              <motion.div
                key={i}
                initial={{ opacity: 0, x: -20 }}
                whileInView={{ opacity: 1, x: 0 }}
                viewport={{ once: true }}
                transition={{ delay: i * 0.05 }}
                className="flex items-center gap-3 p-4 rounded-xl glass"
              >
                <span className="text-[#14F195]">✓</span>
                <span className="text-[#8B92A5]">{item.text}</span>
              </motion.div>
            ))}
          </div>
        </Section>

        <Section id="submission" title="Submitting to dApp Store">
          <Callout type="info" title="dApp Store Publisher Portal">
            Submit your app at{' '}
            <a href="https://publisher.solanamobile.com" className="text-[#9945FF] hover:underline">
              publisher.solanamobile.com
            </a>
          </Callout>

          <div className="space-y-4 mt-6">
            <div className="p-4 rounded-xl glass">
              <div className="flex items-center gap-3 mb-2">
                <span className="w-8 h-8 rounded-full bg-[#9945FF]/20 flex items-center justify-center text-[#9945FF] font-semibold">1</span>
                <h4 className="font-semibold">Create Publisher Account</h4>
              </div>
              <p className="text-sm text-[#8B92A5] ml-11">Connect your wallet and register as a publisher</p>
            </div>

            <div className="p-4 rounded-xl glass">
              <div className="flex items-center gap-3 mb-2">
                <span className="w-8 h-8 rounded-full bg-[#9945FF]/20 flex items-center justify-center text-[#9945FF] font-semibold">2</span>
                <h4 className="font-semibold">Upload APK</h4>
              </div>
              <p className="text-sm text-[#8B92A5] ml-11">Upload your signed release APK</p>
            </div>

            <div className="p-4 rounded-xl glass">
              <div className="flex items-center gap-3 mb-2">
                <span className="w-8 h-8 rounded-full bg-[#9945FF]/20 flex items-center justify-center text-[#9945FF] font-semibold">3</span>
                <h4 className="font-semibold">Add Store Listing</h4>
              </div>
              <p className="text-sm text-[#8B92A5] ml-11">Fill in app details, screenshots, and descriptions</p>
            </div>

            <div className="p-4 rounded-xl glass">
              <div className="flex items-center gap-3 mb-2">
                <span className="w-8 h-8 rounded-full bg-[#14F195]/20 flex items-center justify-center text-[#14F195] font-semibold">4</span>
                <h4 className="font-semibold">Submit for Review</h4>
              </div>
              <p className="text-sm text-[#8B92A5] ml-11">Submit and wait for approval (typically 1-3 days)</p>
            </div>
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
