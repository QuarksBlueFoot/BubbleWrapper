import React, { useEffect, useMemo, useState } from "react";
import { BottomNav, Tab } from "./components/BottomNav";
import { TopBar } from "./components/TopBar";
import { WalletConnection } from "./components/WalletConnection";

function useStandaloneHint() {
  const [isStandalone, setIsStandalone] = useState(false);
  useEffect(() => {
    const mq = window.matchMedia("(display-mode: standalone)");
    const update = () => setIsStandalone(Boolean((navigator as any).standalone) || mq.matches);
    update();
    mq.addEventListener?.("change", update);
    return () => mq.removeEventListener?.("change", update);
  }, []);
  return isStandalone;
}

function GlassCard(props: { title?: string; children: React.ReactNode; glow?: boolean; className?: string }) {
  return (
    <section 
      className={`card ${props.glow ? 'card-glow' : ''} ${props.className || ''}`} 
      style={{ padding: 18, borderRadius: 22 }}
    >
      {props.title && (
        <div style={{ 
          fontSize: 13, 
          fontWeight: 700, 
          marginBottom: 14,
          color: 'var(--secondary)',
          textTransform: 'uppercase',
          letterSpacing: '0.5px'
        }}>
          {props.title}
        </div>
      )}
      {props.children}
    </section>
  );
}

function StatusRow(props: { label: string; value: string; hint?: string; status?: "success" | "warning" | "error" }) {
  const statusClass = props.status || "success";
  return (
    <div style={{ 
      display: "flex", 
      alignItems: "flex-start", 
      justifyContent: "space-between", 
      gap: 12, 
      padding: "12px 0", 
      borderTop: "1px solid rgba(255,255,255,.04)" 
    }}>
      <div style={{ minWidth: 0, flex: 1 }}>
        <div style={{ fontSize: 14, color: "var(--text)", fontWeight: 500 }}>{props.label}</div>
        {props.hint && <div style={{ fontSize: 12, color: "var(--muted)", marginTop: 4, lineHeight: 1.4 }}>{props.hint}</div>}
      </div>
      <div style={{ display: "flex", alignItems: "center", gap: 10, flexShrink: 0 }}>
        <span className={`status-dot ${statusClass}`} />
        <div style={{ fontSize: 13, fontWeight: 700, color: statusClass === 'success' ? 'var(--secondary)' : statusClass === 'warning' ? '#FFB800' : 'var(--danger)' }}>
          {props.value}
        </div>
      </div>
    </div>
  );
}

function FeatureCard(props: { emoji: string; title: string; description: string; onClick?: () => void }) {
  return (
    <button
      className="card card-glow"
      onClick={props.onClick}
      style={{
        padding: 18,
        borderRadius: 18,
        border: "1px solid rgba(153, 69, 255, 0.2)",
        background: "linear-gradient(135deg, rgba(153, 69, 255, 0.08) 0%, rgba(20, 241, 149, 0.04) 100%)",
        color: "var(--text)",
        textAlign: "left",
        width: "100%",
        transition: "all 0.2s ease"
      }}
    >
      <div style={{ display: "flex", alignItems: "center", gap: 12 }}>
        <span style={{ fontSize: 28 }}>{props.emoji}</span>
        <div>
          <div style={{ fontWeight: 700, fontSize: 15 }}>{props.title}</div>
          <div style={{ marginTop: 4, fontSize: 12, color: "var(--muted)", lineHeight: 1.4 }}>
            {props.description}
          </div>
        </div>
      </div>
    </button>
  );
}

function HeroSection() {
  return (
    <div style={{ 
      textAlign: 'center', 
      padding: '24px 0',
      position: 'relative'
    }}>
      {/* Gradient Orb Background */}
      <div style={{
        position: 'absolute',
        top: '50%',
        left: '50%',
        transform: 'translate(-50%, -50%)',
        width: 200,
        height: 200,
        background: 'radial-gradient(circle, rgba(153, 69, 255, 0.3) 0%, transparent 70%)',
        filter: 'blur(40px)',
        pointerEvents: 'none'
      }} />
      
      <div className="float" style={{ marginBottom: 16 }}>
        <img 
          src="/solana-logo.png" 
          alt="Solana" 
          style={{ 
            width: 72, 
            height: 72, 
            filter: 'drop-shadow(0 8px 32px rgba(153, 69, 255, 0.5))'
          }} 
        />
      </div>
      
      <img 
        src="/solana-mobile-logo.png" 
        alt="Solana Mobile" 
        style={{ 
          height: 28, 
          width: 'auto',
          filter: 'drop-shadow(0 2px 8px rgba(255, 255, 255, 0.1))'
        }} 
      />
      
      <h2 style={{ 
        fontSize: 18, 
        fontWeight: 700, 
        margin: '12px 0 0 0',
        background: 'linear-gradient(135deg, #9945FF, #14F195)',
        WebkitBackgroundClip: 'text',
        WebkitTextFillColor: 'transparent',
        backgroundClip: 'text'
      }}>
        PWA Sample
      </h2>
      

    </div>
  );
}

export default function App() {
  const [tab, setTabState] = useState<Tab>(() => {
    const hash = window.location.hash.replace('#', '');
    return (hash === 'docs' || hash === 'settings') ? (hash as Tab) : 'home';
  });
  
  const isStandalone = useStandaloneHint();

  useEffect(() => {
    document.body.style.overscrollBehavior = "none";

    const onPopState = () => {
      const hash = window.location.hash.replace('#', '');
      if (hash === 'docs' || hash === 'settings' || hash === 'home') {
        setTabState(hash as Tab);
      } else {
        setTabState('home');
      }
    };
    window.addEventListener('popstate', onPopState);
    return () => window.removeEventListener('popstate', onPopState);
  }, []);

  const setTab = (newTab: Tab) => {
    if (newTab === tab) return;
    setTabState(newTab);
    window.history.pushState(null, '', `#${newTab}`);
  };

  const status = useMemo(() => {
    const ua = navigator.userAgent;
    const isAndroid = /Android/i.test(ua);
    const isIOS = /iPhone|iPad|iPod/i.test(ua);
    const isChrome = /Chrome\//i.test(ua) && !/Edg\//i.test(ua) && !/OPR\//i.test(ua);
    return { isAndroid, isIOS, isChrome };
  }, []);

  return (
    <div className="safe" style={{ height: "100%", paddingBottom: 100, overflowY: "auto" }}>
      {tab === "home" ? (
        <>
          <TopBar />
          <HeroSection />
          
          {/* Wallet Connection */}
          <div style={{ marginTop: 16, marginBottom: 16 }}>
            <WalletConnection />
          </div>
          
          {!isStandalone && (
            <div className="gradient-border" style={{ marginTop: 8, marginBottom: 16 }}>
              <div style={{ padding: 16, display: 'flex', alignItems: 'center', gap: 12 }}>
                <span style={{ fontSize: 24 }}>📲</span>
                <div>
                  <div style={{ fontSize: 13, fontWeight: 600, color: 'var(--text)' }}>Install for best experience</div>
                  <div style={{ fontSize: 12, color: 'var(--muted)', marginTop: 2 }}>Add to home screen for native feel</div>
                </div>
              </div>
            </div>
          )}

          <main style={{ display: "grid", gap: 14 }}>
            <div className="gradient-border" style={{ marginTop: 0 }}>
               <div style={{ padding: 16 }}>
                 <div style={{ display: 'flex', gap: 12, alignItems: 'center', marginBottom: 12 }}>
                   <span style={{ fontSize: 24 }}>✨</span>
                   <div>
                     <div style={{ fontSize: 13, fontWeight: 700, color: 'var(--text)' }}>BUBBLEWRAP OPTIMIZED</div>
                     <div style={{ fontSize: 12, color: 'var(--muted)' }}>Premium Mobile Experience</div>
                   </div>
                 </div>
                 
                 <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 10 }}>
                   <div style={{ background: 'rgba(255,255,255,0.03)', padding: 10, borderRadius: 8 }}>
                     <div style={{ fontSize: 11, color: 'var(--secondary)', fontWeight: 600 }}>SPLASH SCREEN</div>
                     <div style={{ fontSize: 10, color: 'var(--muted)', marginTop: 4 }}>Contrast-configured manifest background & theme properties</div>
                   </div>
                   <div style={{ background: 'rgba(255,255,255,0.03)', padding: 10, borderRadius: 8 }}>
                     <div style={{ fontSize: 11, color: 'var(--secondary)', fontWeight: 600 }}>CHROME FIRST</div>
                     <div style={{ fontSize: 10, color: 'var(--muted)', marginTop: 4 }}>Prioritizes Chrome TWA, falls back to system browser</div>
                   </div>
                   <div style={{ background: 'rgba(255,255,255,0.03)', padding: 10, borderRadius: 8 }}>
                     <div style={{ fontSize: 11, color: 'var(--secondary)', fontWeight: 600 }}>NATIVE NAV</div>
                     <div style={{ fontSize: 10, color: 'var(--muted)', marginTop: 4 }}>Bottom tabs & safe-area insets for modern devices</div>
                   </div>
                   <div style={{ background: 'rgba(255,255,255,0.03)', padding: 10, borderRadius: 8 }}>
                     <div style={{ fontSize: 11, color: 'var(--secondary)', fontWeight: 600 }}>DARK MODE</div>
                     <div style={{ fontSize: 10, color: 'var(--muted)', marginTop: 4 }}>System-aware OLED black theme (#0B0F1A)</div>
                   </div>
                 </div>
               </div>
            </div>

            <div style={{ fontSize: 12, fontWeight: 600, color: 'var(--muted)', textTransform: 'uppercase', letterSpacing: '0.5px', marginTop: 8 }}>
              Quick Actions
            </div>

            <div style={{ display: "grid", gap: 10 }}>
              <FeatureCard
                emoji="📚"
                title="Setup Guide"
                description="Bubblewrap CLI, Digital Asset Links, and dApp Store submission"
                onClick={() => setTab("docs")}
              />
              <FeatureCard
                emoji="⚙️"
                title="Configuration"
                description="PWA settings, service worker, and update behavior"
                onClick={() => setTab("settings")}
              />
            </div>
          </main>
        </>
      ) : tab === "docs" ? (
        <>
          <TopBar title="Setup Guide" subtitle="Complete Bubblewrap + dApp Store walkthrough" />
          <main style={{ marginTop: 16, display: "grid", gap: 14 }}>
            <GlassCard title="1️⃣ PWA Essentials">
              <ul style={{ margin: 0, paddingLeft: 18, color: "var(--muted)", fontSize: 13, lineHeight: 1.7 }}>
                <li>Web manifest with <span className="kbd">display: standalone</span></li>
                <li>Maskable icons (512x512 with safe zone)</li>
                <li>Service worker for offline + caching</li>
                <li>Theme color matching (#0B0F1A)</li>
                <li>Viewport with <span className="kbd">viewport-fit=cover</span></li>
              </ul>
            </GlassCard>

            <GlassCard title="2️⃣ Bubblewrap Setup">
              <div style={{ fontSize: 13, color: "var(--muted)", lineHeight: 1.6 }}>
                <p style={{ margin: '0 0 12px 0' }}>
                  Install the CLI and initialize your TWA project:
                </p>
                <div className="kbd" style={{ display: 'block', padding: 12, marginBottom: 12, whiteSpace: 'pre-wrap', fontSize: 11 }}>
                  npm i -g @bubblewrap/cli{'\n'}
                  bubblewrap init --manifest=https://your-domain/manifest.webmanifest
                </div>
                <p style={{ margin: 0 }}>
                  See <span className="kbd">docs/bubblewrap.md</span> for detailed configuration.
                </p>
              </div>
            </GlassCard>

            <GlassCard title="3️⃣ Chrome Preference">
              <div style={{ fontSize: 13, color: "var(--muted)", lineHeight: 1.6 }}>
                <p style={{ margin: '0 0 12px 0' }}>
                  The Android wrapper requests Chrome Custom Tabs first, falling back to the default browser if unavailable.
                </p>
                <p style={{ margin: 0 }}>
                  Drop-in helper: <span className="kbd">ChromePreferredCustomTabs.java</span>
                </p>
              </div>
            </GlassCard>

            <GlassCard title="4️⃣ Digital Asset Links">
              <div style={{ fontSize: 13, color: "var(--muted)", lineHeight: 1.6 }}>
                <p style={{ margin: '0 0 12px 0' }}>
                  Required for full-screen TWA without browser UI:
                </p>
                <div className="kbd" style={{ display: 'block', padding: 12, whiteSpace: 'pre-wrap', fontSize: 11 }}>
                  bubblewrap fingerprint generateAssetLinks
                </div>
                <p style={{ margin: '12px 0 0 0' }}>
                  Host at <span className="kbd">/.well-known/assetlinks.json</span>
                </p>
              </div>
            </GlassCard>

            <GlassCard title="5️⃣ dApp Store Submission">
              <div style={{ fontSize: 13, color: "var(--muted)", lineHeight: 1.6 }}>
                <ol style={{ margin: 0, paddingLeft: 18 }}>
                  <li>Build signed APK: <span className="kbd">bubblewrap build</span></li>
                  <li>Prepare assets (icon 512px, banner 1200x600, 4+ screenshots)</li>
                  <li>Connect wallet to Publisher Portal</li>
                  <li>Upload APK and assets, submit for review</li>
                </ol>
              </div>
            </GlassCard>
          </main>
        </>
      ) : (
        <>
          <TopBar title="Settings" subtitle="PWA configuration and app info" />
          <main style={{ marginTop: 16, display: "grid", gap: 14 }}>
            <GlassCard title="🔄 Updates">
              <StatusRow 
                label="Auto-Update" 
                value="Enabled" 
                hint="Service worker updates apply automatically on refresh" 
                status="success" 
              />
              <StatusRow 
                label="Cache Strategy" 
                value="Workbox" 
                hint="Precaches critical assets for offline support" 
                status="success" 
              />
            </GlassCard>

            <GlassCard title="📱 App Info">
              <div style={{ display: 'grid', gap: 12 }}>
                <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                  <span style={{ fontSize: 13, color: 'var(--muted)' }}>Version</span>
                  <span style={{ fontSize: 13, fontWeight: 600 }}>1.0.0</span>
                </div>
                <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                  <span style={{ fontSize: 13, color: 'var(--muted)' }}>Template</span>
                  <span style={{ fontSize: 13, fontWeight: 600 }}>Bubblewrap PWA</span>
                </div>
                <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                  <span style={{ fontSize: 13, color: 'var(--muted)' }}>Target</span>
                  <span style={{ fontSize: 13, fontWeight: 600 }}>Solana dApp Store</span>
                </div>
              </div>
            </GlassCard>

            <GlassCard title="ℹ️ About">
              <div style={{ fontSize: 13, color: "var(--muted)", lineHeight: 1.6 }}>
                <p style={{ margin: '0 0 12px 0' }}>
                  This is a mobile-optimized PWA template demonstrating best practices for Solana Mobile dApp Store submission.
                </p>
                <p style={{ margin: 0 }}>
                  Built with React, TypeScript, Vite, and vite-plugin-pwa. Designed with 2025 glassmorphism standards and Solana brand colors.
                </p>
              </div>
            </GlassCard>

            <div style={{ marginTop: 8, padding: 16, textAlign: 'center' }}>
              <div style={{ 
                fontSize: 11, 
                color: 'var(--muted)',
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'center',
                gap: 8
              }}>
                <span>Built for</span>
                <span style={{ 
                  fontWeight: 700,
                  background: 'linear-gradient(135deg, #9945FF, #14F195)',
                  WebkitBackgroundClip: 'text',
                  WebkitTextFillColor: 'transparent',
                  backgroundClip: 'text'
                }}>
                  Solana Mobile
                </span>
              </div>
            </div>
          </main>
        </>
      )}

      <BottomNav tab={tab} setTab={setTab} />
    </div>
  );
}
