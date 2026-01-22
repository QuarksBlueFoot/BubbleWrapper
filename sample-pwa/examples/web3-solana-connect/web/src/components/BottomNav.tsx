import React from "react";

export type Tab = "home" | "docs" | "settings";

export function BottomNav(props: { tab: Tab; setTab: (t: Tab) => void }) {
  const { tab, setTab } = props;

  const Item = (p: { id: Tab; label: string; emoji: string }) => (
    <button
      onClick={() => setTab(p.id)}
      aria-current={tab === p.id ? "page" : undefined}
      style={{
        flex: 1,
        padding: "12px 10px",
        border: "none",
        background: "transparent",
        color: tab === p.id ? "var(--text)" : "var(--muted)",
        display: "flex",
        flexDirection: "column",
        alignItems: "center",
        gap: 6,
        cursor: "pointer",
        minWidth: 0
      }}
    >
      <span style={{ fontSize: 18, lineHeight: 1 }}>{p.emoji}</span>
      <span style={{ fontSize: 12, fontWeight: 650, letterSpacing: 0.2, whiteSpace: "nowrap", overflow: "hidden", textOverflow: "ellipsis" }}>
        {p.label}
      </span>
    </button>
  );

  return (
    <nav
      className="card safe safeBottom"
      style={{
        position: "fixed",
        left: 0,
        right: 0,
        bottom: 0,
        margin: 12,
        borderRadius: 24,
        backdropFilter: "blur(16px)",
        background: "rgba(18,26,42,.72)",
        display: "flex",
        zIndex: 20
      }}
    >
      <Item id="home" label="Home" emoji="🏠" />
      <Item id="docs" label="Guide" emoji="📱" />
      <Item id="settings" label="Settings" emoji="⚙️" />
    </nav>
  );
}
