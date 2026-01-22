import React from "react";

export function TopBar(props: { title?: string; subtitle?: string }) {
  return (
    <header className="safe safeTop" style={{ paddingBottom: 8 }}>
      <div style={{ display: "flex", alignItems: "center", justifyContent: "space-between", gap: 12 }}>
        <div style={{ minWidth: 0 }}>
          {props.title && (
            <div style={{ 
              fontSize: 22, 
              fontWeight: 800, 
              letterSpacing: -0.3, 
              whiteSpace: "nowrap", 
              overflow: "hidden", 
              textOverflow: "ellipsis",
              background: props.title ? 'var(--text)' : 'linear-gradient(135deg, #9945FF, #14F195)',
              WebkitBackgroundClip: 'text',
              WebkitTextFillColor: 'transparent',
              backgroundClip: 'text'
            }}>
              {props.title}
            </div>
          )}
          {props.subtitle && (
            <div style={{ marginTop: 4, fontSize: 12, color: "var(--muted)", whiteSpace: "nowrap", overflow: "hidden", textOverflow: "ellipsis" }}>
              {props.subtitle}
            </div>
          )}
        </div>
        <div 
          className="pill" 
          title="Offline-ready via service worker"
          style={{
            background: 'linear-gradient(135deg, rgba(153, 69, 255, 0.15), rgba(20, 241, 149, 0.1))',
            border: '1px solid rgba(153, 69, 255, 0.2)'
          }}
        >
          <span style={{ 
            width: 8, 
            height: 8, 
            borderRadius: '50%', 
            background: 'var(--secondary)',
            boxShadow: '0 0 8px var(--secondary)'
          }} />
          <span style={{ fontSize: 11, fontWeight: 600, color: "var(--text)" }}>PWA</span>
        </div>
      </div>
    </header>
  );
}
