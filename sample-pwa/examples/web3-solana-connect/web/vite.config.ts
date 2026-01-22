import { defineConfig } from "vite";
import react from "@vitejs/plugin-react";
import { VitePWA } from "vite-plugin-pwa";

export default defineConfig({
  plugins: [
    react(),
    VitePWA({
      registerType: "autoUpdate",
      includeAssets: [
        "icons/icon-192.png",
        "icons/icon-512.png",
        "icons/icon-512-maskable.png",
        "icons/apple-touch-icon.png"
      ],
      manifest: {
        name: "Mobile-Optimized PWA Sample",
        short_name: "PWA Sample",
        description: "A highly mobile-optimized PWA designed to be wrapped with Bubblewrap (TWA).",
        start_url: "/",
        scope: "/",
        display: "standalone",
        background_color: "#0B0F1A",
        theme_color: "#0B0F1A",
        orientation: "portrait",
        icons: [
          { "src": "/icons/icon-192.png", "sizes": "192x192", "type": "image/png" },
          { "src": "/icons/icon-512.png", "sizes": "512x512", "type": "image/png" },
          { "src": "/icons/icon-512-maskable.png", "sizes": "512x512", "type": "image/png", "purpose": "maskable" }
        ]
      },
      workbox: {
        navigateFallback: "/index.html",
        globPatterns: ["**/*.{js,css,html,png,svg,ico}"]
      }
    })
  ],
  server: { port: 5173, strictPort: true },
  preview: { port: 4173, strictPort: true }
});
