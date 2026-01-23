import { motion } from 'framer-motion';
import { Link } from 'react-router-dom';
import { ArrowRight, Smartphone, Link2, Rocket, BookOpen, Package, Wrench } from 'lucide-react';

const guides = [
  {
    icon: Link2,
    title: 'MWA Connection Guide',
    description: 'Complete guide to implementing Solana wallet connections with Mobile Wallet Adapter 2.0',
    href: '/mwa-guide',
    tag: 'Featured',
    featured: true,
  },
  {
    icon: Wrench,
    title: 'TWA Build Guide',
    description: 'Wrap your PWA into an Android app using Bubblewrap and Trusted Web Activity',
    href: '/twa-guide',
    tag: 'TWA',
  },
  {
    icon: Rocket,
    title: 'Publishing Guide',
    description: 'End-to-end guide for submitting your app to the Solana dApp Store',
    href: '/publishing',
    tag: 'Essential',
  },
  {
    icon: BookOpen,
    title: 'Digital Asset Links',
    description: 'Set up DAL for fullscreen TWA mode without browser UI',
    href: '/digital-asset-links',
    tag: 'TWA',
  },
];

const features = [
  { icon: '📱', text: 'Mobile-First Design' },
  { icon: '🔌', text: 'MWA 2.0 Integration' },
  { icon: '🎨', text: 'Premium UI/UX' },
  { icon: '🚀', text: 'dApp Store Ready' },
];

const appCapabilities = [
  { icon: '🔑', title: 'On-Device Keystore', desc: 'Generate signing keys directly on your phone' },
  { icon: '🛠️', title: 'TWA Build Wizard', desc: 'Step-by-step TWA configuration generator' },
  { icon: '📄', title: 'Manifest Auto-Fill', desc: 'Fetch PWA manifest to auto-fill app details' },
  { icon: '📦', title: 'APK Parser', desc: 'Extract metadata from APK files automatically' },
  { icon: '💰', title: 'One-Click Publish', desc: 'Upload and mint NFT with Mobile Wallet Adapter' },
  { icon: '🔐', title: 'SHA-256 Fingerprint', desc: 'Extract fingerprint for Digital Asset Links' },
];

export function Home() {
  return (
    <div className="min-h-screen">
      {/* Hero Section */}
      <section className="relative overflow-hidden">
        {/* Animated gradient background */}
        <div className="absolute inset-0 overflow-hidden">
          <motion.div
            animate={{
              rotate: 360,
            }}
            transition={{ duration: 60, repeat: Infinity, ease: 'linear' }}
            className="absolute -top-1/2 -left-1/2 w-[200%] h-[200%]"
            style={{
              background: 'radial-gradient(circle at 30% 30%, rgba(153, 69, 255, 0.15) 0%, transparent 50%), radial-gradient(circle at 70% 70%, rgba(20, 241, 149, 0.1) 0%, transparent 50%)',
            }}
          />
        </div>

        <div className="relative max-w-4xl mx-auto px-4 py-16 sm:py-24">
          <motion.div
            initial={{ opacity: 0, y: 30 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ duration: 0.6 }}
            className="text-center"
          >
            <motion.div
              animate={{ y: [0, -10, 0] }}
              transition={{ duration: 3, repeat: Infinity, ease: 'easeInOut' }}
              className="text-6xl sm:text-7xl mb-6"
            >
              🫧
            </motion.div>
            
            <h1 className="text-4xl sm:text-5xl font-bold mb-4">
              <span className="gradient-text">BubbleWrapper</span>
            </h1>
            
            <p className="text-lg sm:text-xl text-[#8B92A5] max-w-xl mx-auto mb-8">
              Build mobile-optimized Android apps for the Solana dApp Store with TWA and Mobile Wallet Adapter
            </p>

            {/* Feature pills */}
            <div className="flex flex-wrap justify-center gap-2 mb-10">
              {features.map((feature, i) => (
                <motion.div
                  key={feature.text}
                  initial={{ opacity: 0, scale: 0.8 }}
                  animate={{ opacity: 1, scale: 1 }}
                  transition={{ delay: 0.3 + i * 0.1 }}
                  className="flex items-center gap-2 px-4 py-2 rounded-full glass text-sm"
                >
                  <span>{feature.icon}</span>
                  <span className="text-[#8B92A5]">{feature.text}</span>
                </motion.div>
              ))}
            </div>

            {/* CTA */}
            <motion.div
              initial={{ opacity: 0, y: 20 }}
              animate={{ opacity: 1, y: 0 }}
              transition={{ delay: 0.6 }}
            >
              <Link
                to="/mwa-guide"
                className="inline-flex items-center gap-2 px-6 py-3 rounded-xl bg-gradient-to-r from-[#9945FF] to-[#14F195] text-white font-semibold hover:opacity-90 transition-opacity active:scale-95"
              >
                Get Started
                <ArrowRight size={20} />
              </Link>
            </motion.div>
          </motion.div>
        </div>
      </section>

      {/* Guides Section */}
      <section className="max-w-4xl mx-auto px-4 pb-16">
        <motion.h2
          initial={{ opacity: 0 }}
          whileInView={{ opacity: 1 }}
          viewport={{ once: true }}
          className="text-2xl font-bold mb-6 flex items-center gap-3"
        >
          <span>📚</span>
          <span>Documentation</span>
        </motion.h2>

        <div className="grid gap-4">
          {guides.map((guide, i) => (
            <motion.div
              key={guide.href}
              initial={{ opacity: 0, x: -20 }}
              whileInView={{ opacity: 1, x: 0 }}
              viewport={{ once: true }}
              transition={{ delay: i * 0.1 }}
            >
              <Link
                to={guide.href}
                className="flex items-start gap-4 p-5 rounded-2xl glass hover:border-[#9945FF]/50 transition-all active:scale-[0.98] group"
              >
                <div className={`p-3 rounded-xl ${guide.featured ? 'bg-[#14F195]/20' : 'bg-[#9945FF]/20'}`}>
                  <guide.icon size={24} className={guide.featured ? 'text-[#14F195]' : 'text-[#9945FF]'} />
                </div>
                <div className="flex-1 min-w-0">
                  <div className="flex items-center gap-2 mb-1">
                    <h3 className="font-semibold truncate">{guide.title}</h3>
                    <span
                      className={`shrink-0 px-2 py-0.5 rounded-full text-xs font-medium ${
                        guide.featured
                          ? 'bg-[#14F195]/20 text-[#14F195]'
                          : 'bg-[#9945FF]/20 text-[#9945FF]'
                      }`}
                    >
                      {guide.tag}
                    </span>
                  </div>
                  <p className="text-sm text-[#8B92A5] line-clamp-2">{guide.description}</p>
                </div>
                <ArrowRight size={20} className="shrink-0 text-[#8B92A5] group-hover:text-white transition-colors mt-1" />
              </Link>
            </motion.div>
          ))}
        </div>
      </section>

      {/* Quick Info */}
      <section className="max-w-4xl mx-auto px-4 pb-12">
        <motion.div
          initial={{ opacity: 0, y: 20 }}
          whileInView={{ opacity: 1, y: 0 }}
          viewport={{ once: true }}
          className="p-6 rounded-2xl glass text-center"
        >
          <Smartphone className="mx-auto mb-4 text-[#9945FF]" size={32} />
          <h3 className="font-semibold mb-2">Built for Solana Mobile</h3>
          <p className="text-sm text-[#8B92A5] max-w-md mx-auto">
            These guides are optimized for mobile viewing. Swipe through on your Solana Seeker or any mobile device.
          </p>
        </motion.div>
      </section>

      {/* BubbleWrapper App Capabilities */}
      <section className="max-w-4xl mx-auto px-4 pb-12">
        <motion.h2
          initial={{ opacity: 0 }}
          whileInView={{ opacity: 1 }}
          viewport={{ once: true }}
          className="text-2xl font-bold mb-6 flex items-center gap-3"
        >
          <Package className="text-[#14F195]" size={24} />
          <span>BubbleWrapper App</span>
          <span className="text-xs px-2 py-1 rounded-full bg-[#14F195]/20 text-[#14F195]">9.8 MB</span>
        </motion.h2>
        
        <motion.p
          initial={{ opacity: 0 }}
          whileInView={{ opacity: 1 }}
          viewport={{ once: true }}
          className="text-[#8B92A5] mb-6"
        >
          On-device Android app for publishing to the Solana dApp Store. No desktop required!
        </motion.p>

        <div className="grid grid-cols-2 sm:grid-cols-3 gap-3">
          {appCapabilities.map((cap, i) => (
            <motion.div
              key={cap.title}
              initial={{ opacity: 0, scale: 0.9 }}
              whileInView={{ opacity: 1, scale: 1 }}
              viewport={{ once: true }}
              transition={{ delay: i * 0.05 }}
              className="p-4 rounded-xl glass"
            >
              <div className="text-2xl mb-2">{cap.icon}</div>
              <h4 className="font-medium text-sm mb-1">{cap.title}</h4>
              <p className="text-xs text-[#8B92A5]">{cap.desc}</p>
            </motion.div>
          ))}
        </div>
      </section>

      {/* Sample App Section */}
      <section className="max-w-4xl mx-auto px-4 pb-12">
        <motion.h2
          initial={{ opacity: 0 }}
          whileInView={{ opacity: 1 }}
          viewport={{ once: true }}
          className="text-2xl font-bold mb-6 flex items-center gap-3"
        >
          <span>🐵</span>
          <span>MonkeMob Sample App</span>
          <span className="text-xs px-2 py-1 rounded-full bg-[#14F195]/20 text-[#14F195]">Published ✅</span>
        </motion.h2>
        
        <motion.div
          initial={{ opacity: 0, y: 20 }}
          whileInView={{ opacity: 1, y: 0 }}
          viewport={{ once: true }}
          className="p-6 rounded-2xl glass space-y-4"
        >
          <p className="text-[#8B92A5]">
            A production-ready sample TWA app successfully published to Solana Mobile dApp Store.
            Showcases all required deliverables with premium mobile UI, glassmorphism design, and Solana branding.
          </p>
          
          {/* Live Status */}
          <div className="p-4 rounded-xl bg-[#14F195]/10 border border-[#14F195]/20">
            <div className="flex items-center gap-2 mb-2">
              <span className="text-[#14F195]">●</span>
              <span className="font-semibold text-sm">Live on Mainnet</span>
            </div>
            <div className="space-y-1 text-xs text-[#8B92A5]">
              <div className="flex items-center gap-2">
                <span className="text-[#8B92A5]">App NFT:</span>
                <code className="px-2 py-0.5 rounded bg-[#0B0F1A] text-[#14F195] font-mono">ADZWhSTQJoppJhapEbUS69yLg5HM2nJ322nenGxQqhMs</code>
              </div>
              <div className="flex items-center gap-2">
                <span className="text-[#8B92A5]">Package:</span>
                <code className="px-2 py-0.5 rounded bg-[#0B0F1A] text-[#9945FF] font-mono">me.monkemob.twa</code>
              </div>
              <div className="flex items-center gap-2">
                <span className="text-[#8B92A5]">PWA:</span>
                <a href="https://monkemob.me" target="_blank" rel="noopener noreferrer" className="px-2 py-0.5 rounded bg-[#0B0F1A] text-[#9945FF] hover:text-[#14F195] font-mono">
                  monkemob.me ↗
                </a>
              </div>
            </div>
          </div>

          {/* Deliverables Checklist */}
          <div className="space-y-2">
            <h4 className="font-medium text-sm">✅ All Deliverables Met:</h4>
            <div className="grid gap-2">
              <div className="flex items-start gap-2 text-sm">
                <span className="text-[#14F195] shrink-0">✓</span>
                <span className="text-[#8B92A5]">
                  <strong className="text-white">Improved splash screen:</strong> Custom Android layer-list with terminal aesthetic, gold-framed icon, falling banana animations
                </span>
              </div>
              <div className="flex items-start gap-2 text-sm">
                <span className="text-[#14F195] shrink-0">✓</span>
                <span className="text-[#8B92A5]">
                  <strong className="text-white">Chrome browser preference:</strong> TWA auto-prefers Chrome with fallback to system default
                </span>
              </div>
              <div className="flex items-start gap-2 text-sm">
                <span className="text-[#14F195] shrink-0">✓</span>
                <span className="text-[#8B92A5]">
                  <strong className="text-white">Mobile-intuitive navigation:</strong> Portrait-first, safe-area insets, 44dp+ touch targets, glassmorphism UI
                </span>
              </div>
            </div>
          </div>
          
          {/* Tech Stack */}
          <div className="flex flex-wrap gap-2">
            <span className="px-3 py-1 rounded-full text-xs bg-[#9945FF]/20 text-[#9945FF]">Vite + React</span>
            <span className="px-3 py-1 rounded-full text-xs bg-[#14F195]/20 text-[#14F195]">TypeScript</span>
            <span className="px-3 py-1 rounded-full text-xs bg-[#9945FF]/20 text-[#9945FF]">PWA Ready</span>
            <span className="px-3 py-1 rounded-full text-xs bg-[#14F195]/20 text-[#14F195]">TWA Wrapped</span>
            <span className="px-3 py-1 rounded-full text-xs bg-[#9945FF]/20 text-[#9945FF]">DAL Configured</span>
            <span className="px-3 py-1 rounded-full text-xs bg-[#14F195]/20 text-[#14F195]">Bubblewrap CLI</span>
          </div>

          <p className="text-xs text-[#8B92A5] italic">
            💡 Use MonkeMob as your template for building mobile-optimized TWA apps!
          </p>
        </motion.div>
      </section>

      {/* Production-Ready Optimizations */}
      <section className="max-w-4xl mx-auto px-4 pb-20">
        <motion.h2
          initial={{ opacity: 0 }}
          whileInView={{ opacity: 1 }}
          viewport={{ once: true }}
          className="text-2xl font-bold mb-6 flex items-center gap-3"
        >
          <span>⚡</span>
          <span>Production-Ready Optimizations</span>
        </motion.h2>
        
        <div className="grid gap-4">
          {/* Enhanced Splash Screen */}
          <motion.div
            initial={{ opacity: 0, x: -20 }}
            whileInView={{ opacity: 1, x: 0 }}
            viewport={{ once: true }}
            className="p-5 rounded-2xl glass"
          >
            <div className="flex items-start gap-4">
              <div className="p-3 rounded-xl bg-[#14F195]/20 shrink-0">
                <span className="text-2xl">✨</span>
              </div>
              <div className="flex-1">
                <h3 className="font-semibold mb-2">Enhanced Splash Screen</h3>
                <p className="text-sm text-[#8B92A5] mb-3">
                  Custom Android layer-list drawable with centered app icon on branded background. Configured with 300ms fade-out for smooth transitions.
                </p>
                <div className="flex flex-wrap gap-2">
                  <code className="px-2 py-1 rounded text-xs bg-[#0B0F1A] text-[#9945FF]">splash_enhanced.xml</code>
                  <code className="px-2 py-1 rounded text-xs bg-[#0B0F1A] text-[#9945FF]">splashScreenFadeOutDuration: 300</code>
                </div>
              </div>
            </div>
          </motion.div>

          {/* Chrome Browser Preference */}
          <motion.div
            initial={{ opacity: 0, x: -20 }}
            whileInView={{ opacity: 1, x: 0 }}
            viewport={{ once: true }}
            transition={{ delay: 0.1 }}
            className="p-5 rounded-2xl glass"
          >
            <div className="flex items-start gap-4">
              <div className="p-3 rounded-xl bg-[#9945FF]/20 shrink-0">
                <span className="text-2xl">🌐</span>
              </div>
              <div className="flex-1">
                <h3 className="font-semibold mb-2">Chrome Browser Preference</h3>
                <p className="text-sm text-[#8B92A5] mb-3">
                  Custom helper class that prefers Chrome for Custom Tabs when available, with graceful fallback to system-chosen provider for better consistency.
                </p>
                <div className="flex flex-wrap gap-2">
                  <code className="px-2 py-1 rounded text-xs bg-[#0B0F1A] text-[#14F195]">ChromePreferredCustomTabs.java</code>
                  <code className="px-2 py-1 rounded text-xs bg-[#0B0F1A] text-[#14F195]">setPackage("com.android.chrome")</code>
                </div>
              </div>
            </div>
          </motion.div>

          {/* Mobile-Intuitive Navigation */}
          <motion.div
            initial={{ opacity: 0, x: -20 }}
            whileInView={{ opacity: 1, x: 0 }}
            viewport={{ once: true }}
            transition={{ delay: 0.2 }}
            className="p-5 rounded-2xl glass"
          >
            <div className="flex items-start gap-4">
              <div className="p-3 rounded-xl bg-[#14F195]/20 shrink-0">
                <span className="text-2xl">📱</span>
              </div>
              <div className="flex-1">
                <h3 className="font-semibold mb-2">Mobile-Intuitive Navigation</h3>
                <p className="text-sm text-[#8B92A5] mb-3">
                  Fixed bottom navigation with safe-area padding, glassmorphism backdrop-filter, 48dp+ touch targets, and gradient active indicators for thumb-friendly UX.
                </p>
                <div className="flex flex-wrap gap-2">
                  <code className="px-2 py-1 rounded text-xs bg-[#0B0F1A] text-[#9945FF]">BottomNav.tsx</code>
                  <code className="px-2 py-1 rounded text-xs bg-[#0B0F1A] text-[#9945FF]">env(safe-area-inset-bottom)</code>
                  <code className="px-2 py-1 rounded text-xs bg-[#0B0F1A] text-[#9945FF]">backdrop-filter: blur(20px)</code>
                </div>
              </div>
            </div>
          </motion.div>
        </div>
      </section>

      {/* Footer */}
      <footer className="text-center py-8 border-t border-white/5">
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
