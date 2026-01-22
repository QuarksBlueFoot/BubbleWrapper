import React, { useEffect, useMemo, useState } from "react";
import { BottomNav, Tab } from "./components/BottomNav";
import { TopBar } from "./components/TopBar";

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

function Section(props: { title: string; children: React.ReactNode }) {
  return (
    <section className="card" style={{ padding: 16, borderRadius: 20 }}>
      <div style={{ fontSize: 14, fontWeight: 750, marginBottom: 10 }}>{props.title}</div>
      {props.children}
    </section>
  );
}

function Row(props: { label: string; value: string; hint?: string; tone?: "good" | "warn" }) {
  const tone = props.tone ?? "good";
  const dot = tone === "good" ? "var(--accent)" : "var(--danger)";
  return (
    <div style={{ display: "flex", alignItems: "flex-start", justifyContent: "space-between", gap: 12, padding: "10px 0", borderTop: "1px solid rgba(255,255,255,.06)" }}>
      <div style={{ minWidth: 0 }}>
        <div style={{ fontSize: 13, color: "var(--muted)" }}>{props.label}</div>
        {props.hint ? <div style={{ fontSize: 12, color: "rgba(154,165,191,.9)", marginTop: 3 }}>{props.hint}</div> : null}
      </div>
      <div style={{ display: "flex", alignItems: "center", gap: 8, flexShrink: 0 }}>
        <span style={{ width: 8, height: 8, borderRadius: 999, background: dot, boxShadow: "0 0 0 3px rgba(124,255,178,.15)" }} />
        <div style={{ fontSize: 13, fontWeight: 700 }}>{props.value}</div>
      </div>
    </div>
  );
}

export default function App() {
  const [tab, setTab] = useState<Tab>("home");
  const isStandalone = useStandaloneHint();

  useEffect(() => {
    document.body.style.overscrollBehavior = "none";
  }, []);

  const status = useMemo(() => {
    const ua = navigator.userAgent;
    const isAndroid = /Android/i.test(ua);
    const isIOS = /iPhone|iPad|iPod/i.test(ua);
    const isChrome = /Chrome\//i.test(ua) && !/Edg\//i.test(ua) && !/OPR\//i.test(ua);
    return { isAndroid, isIOS, isChrome };
  }, []);

  return (
    <div className="safe" style={{ height: "100%", paddingBottom: 98 }}>
      {tab === "home" ? (
        <>
          <TopBar title="Mobile-Optimized PWA" subtitle="Built to feel like a real app • Wrapped via Bubblewrap (TWA)" />
          {!isStandalone ? (
            <div className="card" style={{ marginTop: 12, padding: 14, borderRadius: 20 }}>
              <div style={{ fontSize: 13, color: "var(--muted)" }}>
                Tip: Install this PWA for the best experience. (No app store drama. Just vibes.)
              </div>
            </div>
          ) : null}

          <main style={{ marginTop: 14, display: "grid", gap: 12 }}>
            <Section title="Mobile UX Wins (sample)">
              <Row label="Bottom navigation" value="Enabled" hint="Thumb-friendly; reduces back-button dependency." />
              <Row label="Safe-area padding" value="Enabled" hint="Looks right on notches + gesture bars." />
              <Row label="Standalone mode" value={isStandalone ? "Yes" : "No"} hint="Install to remove browser chrome." tone={isStandalone ? "good" : "warn"} />
              <Row label="Android detected" value={status.isAndroid ? "Yes" : "No"} />
              <Row label="iOS detected" value={status.isIOS ? "Yes" : "No"} />
              <Row label="Chrome detected" value={status.isChrome ? "Yes" : "No"} hint="TWA will prefer Chrome via Custom Tabs." />
            </Section>

            <Section title="Demo content">
              <div style={{ fontSize: 13, color: "var(--muted)", lineHeight: 1.5 }}>
                This is intentionally simple: swap in your own routes, deep links, and content.
                The important part is the mobile shell, splash styling, and Bubblewrap/TWA wrapper configuration.
              </div>

              <div style={{ marginTop: 12, display: "grid", gap: 10 }}>
                <button
                  className="card"
                  style={{
                    padding: 14,
                    borderRadius: 18,
                    border: "1px solid rgba(124,255,178,.18)",
                    background: "linear-gradient(180deg, rgba(124,255,178,.10), rgba(255,255,255,.02))",
                    color: "var(--text)",
                    cursor: "pointer",
                    textAlign: "left"
                  }}
                  onClick={() => setTab("docs")}
                >
                  <div style={{ fontWeight: 800 }}>Open the setup guide</div>
                  <div style={{ marginTop: 4, fontSize: 12, color: "rgba(154,165,191,.95)" }}>
                    Includes Bubblewrap + “Prefer Chrome, fallback to default” sample code.
                  </div>
                </button>
              </div>
            </Section>


            <Section title="Web3 quick demo (Solana)">
              <div style={{ fontSize: 13, color: "var(--muted)", lineHeight: 1.55 }}>
                This section is intentionally minimal and functional. If a Solana provider is present (for example Phantom),
                you can connect and sign a message. In a TWA, this may depend on your wallet and deep link setup.
              </div>

              <div style={{ marginTop: 12, display: "grid", gap: 10 }}>
                <button
                  className="card"
                  style={{
                    padding: 14,
                    borderRadius: 18,
                    border: "1px solid rgba(255,255,255,.10)",
                    background: "rgba(255,255,255,.03)",
                    color: "var(--text)",
                    cursor: "pointer",
                    textAlign: "left"
                  }}
                  onClick={async () => {
                    const sol = (window as any).solana;
                    if (!sol?.isPhantom || !sol?.connect) {
                      alert("No Solana wallet provider found. Open in a wallet-enabled browser or install a compatible wallet.");
                      return;
                    }
                    await sol.connect();
                    alert("Connected: " + (sol.publicKey?.toString?.() ?? "ok"));
                  }}
                >
                  <div style={{ fontWeight: 800 }}>Connect wallet</div>
                  <div style={{ marginTop: 4, fontSize: 12, color: "rgba(154,165,191,.95)" }}>
                    Uses window.solana.connect() when available
                  </div>
                </button>

                <button
                  className="card"
                  style={{
                    padding: 14,
                    borderRadius: 18,
                    border: "1px solid rgba(124,255,178,.18)",
                    background: "linear-gradient(180deg, rgba(124,255,178,.10), rgba(255,255,255,.02))",
                    color: "var(--text)",
                    cursor: "pointer",
                    textAlign: "left"
                  }}
                  onClick={async () => {
                    const sol = (window as any).solana;
                    if (!sol?.publicKey || !sol?.signMessage) {
                      alert("Connect first, and make sure your wallet supports signMessage.");
                      return;
                    }
                    const msg = new TextEncoder().encode("Sign-in to the dApp Store sample (no transaction).");
                    const signed = await sol.signMessage(msg, "utf8");
                    const sig = signed?.signature ? btoa(String.fromCharCode(...new Uint8Array(signed.signature))) : "ok";
                    alert("Signed message. Signature (base64): " + sig);
                  }}
                >
                  <div style={{ fontWeight: 800 }}>Sign message</div>
                  <div style={{ marginTop: 4, fontSize: 12, color: "rgba(154,165,191,.95)" }}>
                    Wallet-only action, no on-chain fee
                  </div>
                </button>
              </div>
            </Section>

          </main>
        </>
      ) : tab === "docs" ? (
        <>
          <TopBar title="Guide" subtitle="Bubblewrap + mobile optimizations (the checklist you actually use)" />
          <main style={{ marginTop: 14, display: "grid", gap: 12 }}>
            <Section title="1) PWA essentials">
              <ul style={{ margin: 0, paddingLeft: 18, color: "var(--muted)", fontSize: 13, lineHeight: 1.55 }}>
                <li>Manifest with <span className="kbd">display: standalone</span>, theme + background colors.</li>
                <li>Maskable icon (prevents ugly crops on Android).</li>
                <li>Service worker for offline and fast repeat loads.</li>
                <li>Prevent white flash: set document background to match theme.</li>
              </ul>
            </Section>

            <Section title="2) Bubblewrap / TWA wrapper">
              <div style={{ fontSize: 13, color: "var(--muted)", lineHeight: 1.55 }}>
                Bubblewrap wraps your hosted PWA into an Android Trusted Web Activity (TWA). Your PWA must be served over HTTPS.
              </div>
              <div style={{ marginTop: 10, fontSize: 13, color: "var(--muted)", lineHeight: 1.55 }}>
                See repo root <span className="kbd">docs/bubblewrap.md</span> for copy-paste commands.
              </div>
            </Section>

            <Section title="3) Prefer Chrome, fallback to system default">
              <div style={{ fontSize: 13, color: "var(--muted)", lineHeight: 1.55 }}>
                The Android wrapper can request Chrome Custom Tabs first. If Chrome isn't installed, it will fall back to the default browser.
                This sample repo includes a tiny patch you can apply to the generated TWA launcher.
              </div>
              <div style={{ marginTop: 10, fontSize: 13, color: "var(--muted)", lineHeight: 1.55 }}>
                Look for <span className="kbd">android-twa/patches/ChromePreferredLauncher.java</span>.
              </div>
            </Section>

            <Section title="4) Mobile-intuitive navigation">
              <div style={{ fontSize: 13, color: "var(--muted)", lineHeight: 1.55 }}>
                Bottom nav + safe-area padding + large tap targets. The bare minimum for not feeling like a “website cosplaying as an app.”
              </div>
            </Section>


            <Section title="Web3 quick demo (Solana)">
              <div style={{ fontSize: 13, color: "var(--muted)", lineHeight: 1.55 }}>
                This section is intentionally minimal and functional. If a Solana provider is present (for example Phantom),
                you can connect and sign a message. In a TWA, this may depend on your wallet and deep link setup.
              </div>

              <div style={{ marginTop: 12, display: "grid", gap: 10 }}>
                <button
                  className="card"
                  style={{
                    padding: 14,
                    borderRadius: 18,
                    border: "1px solid rgba(255,255,255,.10)",
                    background: "rgba(255,255,255,.03)",
                    color: "var(--text)",
                    cursor: "pointer",
                    textAlign: "left"
                  }}
                  onClick={async () => {
                    const sol = (window as any).solana;
                    if (!sol?.isPhantom || !sol?.connect) {
                      alert("No Solana wallet provider found. Open in a wallet-enabled browser or install a compatible wallet.");
                      return;
                    }
                    await sol.connect();
                    alert("Connected: " + (sol.publicKey?.toString?.() ?? "ok"));
                  }}
                >
                  <div style={{ fontWeight: 800 }}>Connect wallet</div>
                  <div style={{ marginTop: 4, fontSize: 12, color: "rgba(154,165,191,.95)" }}>
                    Uses window.solana.connect() when available
                  </div>
                </button>

                <button
                  className="card"
                  style={{
                    padding: 14,
                    borderRadius: 18,
                    border: "1px solid rgba(124,255,178,.18)",
                    background: "linear-gradient(180deg, rgba(124,255,178,.10), rgba(255,255,255,.02))",
                    color: "var(--text)",
                    cursor: "pointer",
                    textAlign: "left"
                  }}
                  onClick={async () => {
                    const sol = (window as any).solana;
                    if (!sol?.publicKey || !sol?.signMessage) {
                      alert("Connect first, and make sure your wallet supports signMessage.");
                      return;
                    }
                    const msg = new TextEncoder().encode("Sign-in to the dApp Store sample (no transaction).");
                    const signed = await sol.signMessage(msg, "utf8");
                    const sig = signed?.signature ? btoa(String.fromCharCode(...new Uint8Array(signed.signature))) : "ok";
                    alert("Signed message. Signature (base64): " + sig);
                  }}
                >
                  <div style={{ fontWeight: 800 }}>Sign message</div>
                  <div style={{ marginTop: 4, fontSize: 12, color: "rgba(154,165,191,.95)" }}>
                    Wallet-only action, no on-chain fee
                  </div>
                </button>
              </div>
            </Section>

          </main>
        </>
      ) : (
        <>
          <TopBar title="Settings" subtitle="A couple toggles to demonstrate app-like UX" />
          <main style={{ marginTop: 14, display: "grid", gap: 12 }}>
            <Section title="Install + updates">
              <div style={{ fontSize: 13, color: "var(--muted)", lineHeight: 1.55 }}>
                This sample uses <span className="kbd">registerType: autoUpdate</span>, so service worker updates apply quickly.
              </div>
            </Section>
            <Section title="About">
              <div style={{ fontSize: 13, color: "var(--muted)", lineHeight: 1.55 }}>
                Goal: a clean, mobile-first PWA that wraps nicely with Bubblewrap into a store-submittable Android app.
              </div>
            </Section>


            <Section title="Web3 quick demo (Solana)">
              <div style={{ fontSize: 13, color: "var(--muted)", lineHeight: 1.55 }}>
                This section is intentionally minimal and functional. If a Solana provider is present (for example Phantom),
                you can connect and sign a message. In a TWA, this may depend on your wallet and deep link setup.
              </div>

              <div style={{ marginTop: 12, display: "grid", gap: 10 }}>
                <button
                  className="card"
                  style={{
                    padding: 14,
                    borderRadius: 18,
                    border: "1px solid rgba(255,255,255,.10)",
                    background: "rgba(255,255,255,.03)",
                    color: "var(--text)",
                    cursor: "pointer",
                    textAlign: "left"
                  }}
                  onClick={async () => {
                    const sol = (window as any).solana;
                    if (!sol?.isPhantom || !sol?.connect) {
                      alert("No Solana wallet provider found. Open in a wallet-enabled browser or install a compatible wallet.");
                      return;
                    }
                    await sol.connect();
                    alert("Connected: " + (sol.publicKey?.toString?.() ?? "ok"));
                  }}
                >
                  <div style={{ fontWeight: 800 }}>Connect wallet</div>
                  <div style={{ marginTop: 4, fontSize: 12, color: "rgba(154,165,191,.95)" }}>
                    Uses window.solana.connect() when available
                  </div>
                </button>

                <button
                  className="card"
                  style={{
                    padding: 14,
                    borderRadius: 18,
                    border: "1px solid rgba(124,255,178,.18)",
                    background: "linear-gradient(180deg, rgba(124,255,178,.10), rgba(255,255,255,.02))",
                    color: "var(--text)",
                    cursor: "pointer",
                    textAlign: "left"
                  }}
                  onClick={async () => {
                    const sol = (window as any).solana;
                    if (!sol?.publicKey || !sol?.signMessage) {
                      alert("Connect first, and make sure your wallet supports signMessage.");
                      return;
                    }
                    const msg = new TextEncoder().encode("Sign-in to the dApp Store sample (no transaction).");
                    const signed = await sol.signMessage(msg, "utf8");
                    const sig = signed?.signature ? btoa(String.fromCharCode(...new Uint8Array(signed.signature))) : "ok";
                    alert("Signed message. Signature (base64): " + sig);
                  }}
                >
                  <div style={{ fontWeight: 800 }}>Sign message</div>
                  <div style={{ marginTop: 4, fontSize: 12, color: "rgba(154,165,191,.95)" }}>
                    Wallet-only action, no on-chain fee
                  </div>
                </button>
              </div>
            </Section>

          </main>
        </>
      )}

      <BottomNav tab={tab} setTab={setTab} />
    </div>
  );
}
