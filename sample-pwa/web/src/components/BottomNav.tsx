import React from "react";

export type Tab = "home" | "docs" | "settings";

export function BottomNav(props: { tab: Tab; setTab: (t: Tab) => void }) {
  const { tab, setTab } = props;

  const Item = (p: { id: Tab; label: string; icon: string; activeIcon: string }) => {
    const isActive = tab === p.id;
    return (
      <button
        onClick={() => setTab(p.id)}
        aria-current={isActive ? "page" : undefined}
        style={{
          flex: 1,
          padding: "10px 8px 14px",
          border: "none",
          background: "transparent",
          color: isActive ? "var(--secondary)" : "var(--muted)",
          display: "flex",
          flexDirection: "column",
          alignItems: "center",
          gap: 4,
          cursor: "pointer",
          minWidth: 0,
          position: "relative",
          transition: "color 0.2s ease"
        }}
      >
        {/* Active indicator */}
        {isActive && (
          <div style={{
            position: "absolute",
            top: 0,
            left: "50%",
            transform: "translateX(-50%)",
            width: 24,
            height: 3,
            borderRadius: 2,
            background: "linear-gradient(90deg, var(--accent), var(--secondary))",
            boxShadow: "0 0 8px var(--secondary-glow)"
          }} />
        )}
        <span style={{ 
          fontSize: 22, 
          lineHeight: 1,
          filter: isActive ? "drop-shadow(0 0 6px var(--secondary-glow))" : "none",
          transition: "filter 0.2s ease"
        }}>
          {isActive ? p.activeIcon : p.icon}
        </span>
        <span style={{ 
          fontSize: 11, 
          fontWeight: isActive ? 700 : 500, 
          letterSpacing: 0.2, 
          whiteSpace: "nowrap", 
          overflow: "hidden", 
          textOverflow: "ellipsis" 
        }}>
          {p.label}
        </span>
      </button>
    );
  };

  return (
    <nav
      className="safe safeBottom"
      style={{
        position: "fixed",
        left: 12,
        right: 12,
        bottom: 12,
        borderRadius: 28,
        background: "rgba(18, 26, 42, 0.85)",
        backdropFilter: "blur(20px) saturate(180%)",
        WebkitBackdropFilter: "blur(20px) saturate(180%)",
        border: "1px solid rgba(255, 255, 255, 0.08)",
        boxShadow: "0 8px 32px rgba(0, 0, 0, 0.4), inset 0 1px 0 rgba(255, 255, 255, 0.05)",
        display: "flex",
        zIndex: 20
      }}
    >
      <Item id="home" label="Home" icon="🏠" activeIcon="🏠" />
      <Item id="docs" label="Guide" icon="📖" activeIcon="📚" />
      <Item id="settings" label="Settings" icon="⚙️" activeIcon="⚙️" />
    </nav>
  );
}

