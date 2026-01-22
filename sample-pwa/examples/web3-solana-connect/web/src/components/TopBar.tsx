import React from "react";

export function TopBar(props: { title: string; subtitle?: string }) {
  return (
    <header className="safe safeTop" style={{ paddingBottom: 10 }}>
      <div style={{ display: "flex", alignItems: "center", justifyContent: "space-between", gap: 12 }}>
        <div style={{ minWidth: 0 }}>
          <div style={{ fontSize: 20, fontWeight: 780, letterSpacing: -0.2, whiteSpace: "nowrap", overflow: "hidden", textOverflow: "ellipsis" }}>
            {props.title}
          </div>
          {props.subtitle ? (
            <div style={{ marginTop: 4, fontSize: 13, color: "var(--muted)", whiteSpace: "nowrap", overflow: "hidden", textOverflow: "ellipsis" }}>
              {props.subtitle}
            </div>
          ) : null}
        </div>
        <div className="pill" title="Offline-ready via service worker">
          <span aria-hidden>🛰️</span>
          <span style={{ fontSize: 12, color: "var(--muted)" }}>PWA</span>
        </div>
      </div>
    </header>
  );
}
