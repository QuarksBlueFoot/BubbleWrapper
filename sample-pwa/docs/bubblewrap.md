# Bubblewrap (TWA) - end-to-end guide

This repo ships a **mobile-optimized PWA** and a **Bubblewrap/TWA wrapper** helper set.
The goal is to give dApp Store developers a copy-paste starting point.

## Prereqs

- Node.js 18+
- Java 17 (Android toolchain)
- Android Studio (optional but recommended)
- `bubblewrap` CLI

Install Bubblewrap:

```bash
npm i -g @bubblewrap/cli
bubblewrap --version
```

## 1) Run the PWA locally

```bash
cd web
npm i
npm run dev
```

To wrap with Bubblewrap, you must host the PWA over **HTTPS** and serve the manifest at a public URL.

## 2) Build + preview (locally)

```bash
npm run build
npm run preview
```

## 3) Host the PWA

Deploy `web/dist` to any static host (Cloudflare Pages, Netlify, Vercel, Firebase Hosting, etc).
Make sure:

- `https://YOUR_DOMAIN/manifest.webmanifest` is reachable
- `https://YOUR_DOMAIN/` loads cleanly
- `start_url` and `scope` match your hosting path

## 4) Bubblewrap init

From repo root:

```bash
mkdir -p android-twa-project
cd android-twa-project

bubblewrap init --manifest=https://YOUR_DOMAIN/manifest.webmanifest
```

During prompts, set:
- App name: (your choice)
- Package ID: (your reverse-dns)
- Host: YOUR_DOMAIN
- Start URL: https://YOUR_DOMAIN/

This creates an Android project you can build.

> Why the generated Android project isn't committed here:
> Bubblewrap outputs a full Gradle project (wrappers, build outputs, keystore config, etc.).
> In real teams, you usually keep that project next to your web app repo (or inside it),
> but for a grant sample we keep the repo small and instead provide:
> - a production-ready `web/` PWA
> - a `twa-manifest.example.json` you can copy into the generated project
> - a drop-in Java helper to satisfy the “prefer Chrome” requirement

## 5) Improve splash screen styling (TWA)

After `init`, edit the generated `twa-manifest.json` and set:

- `splashScreenFadeOutDuration`
- `splashScreenBackgroundColor`
- `splashScreenImage`

Example values are in this repo at:
`android-twa/twa-manifest.example.json`

Rebuild:

```bash
bubblewrap build
```

## 6) Default to Chrome, fall back to system default

What this requirement means in practice:

- For the main app experience, a Bubblewrap generated app uses a browser that supports the Trusted Web Activity protocol (Chrome on Android is the common host, and other browsers may implement the same protocol).
- For any external links you launch via Custom Tabs (for example "Open docs", "View terms"), you can prefer Chrome when installed, and otherwise let Android choose a compatible default provider.

This repo includes a small helper you can drop into a Bubblewrap generated Android project for external link launches:

- `android-twa/patches/ChromePreferredCustomTabs.java`

Usage:

```java
ChromePreferredCustomTabs.launch(this, Uri.parse("https://your.domain/docs"));
```

Why we do not hard force Chrome in every case:
- The recommended policy for Custom Tabs and TWAs is to use the user's default browser if it provides the required capabilities.
- If Chrome is missing, a strict Chrome only approach can break launches instead of falling back cleanly.

## 7) Build + install

```bash
bubblewrap build
bubblewrap install
```

## Mobile optimization checklist (PWA)

This sample implements:
- Bottom navigation with big tap targets
- Safe-area padding for notches/gesture bar
- Dark theme + no white flash on cold start
- PWA manifest with maskable icons
- Service worker caching via `vite-plugin-pwa`


## Asset Links (required for full trust)

Trusted Web Activity verification requires Digital Asset Links. Bubblewrap can generate the file, and you host it at `.well-known/assetlinks.json` on the same origin as your PWA.
