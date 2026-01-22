# PWA Hosting Guide

To function as a Trusted Web Activity (TWA) on Android, your Progressive Web App (PWA) must be hosted on a public HTTPS URL. This guide covers how to deploy the sample PWA.

## 🚀 Recommended Hosts

We recommend **Vercel** or **Netlify** for their ease of use, automatic SSL (required for TWA), and free tiers.

### Option A: Vercel (Recommended)

1. **Install Vercel CLI** (Optional, or use web UI)
   ```bash
   npm i -g vercel
   ```

2. **Deploy from CLI**
   ```bash
   cd sample-pwa/web
   vercel
   ```
   - Follow prompts to link your GitHub account
   - Keep default settings (Vite detected automatically)

3. **Get Production URL**
   - Vercel will provide a URL like `https://your-project.vercel.app`
   - Use this URL in your `manifest.webmanifest` and Bubblewrap config

### Option B: Netlify

1. **Drag & Drop**
   - Build your project first:
     ```bash
     cd sample-pwa/web
     npm install
     npm run build
     ```
   - Drag the `dist` folder to [app.netlify.com/drop](https://app.netlify.com/drop)

2. **Configure SPA Redirect**
   - Ensure a `_redirects` file exists in `public/` with:
     ```
     /*  /index.html  200
     ```
   - This prevents 404s on refresh for React apps

---

## 🔧 Server Configuration Requirements

For your PWA to work as a TWA on the Solana dApp Store, your host must support:

### 1. HTTPS
- **Mandatory**: TWA will not work over HTTP
- Must have a valid SSL certificate (free with Vercel/Netlify)

### 2. Digital Asset Links (`/.well-known/assetlinks.json`)
- You must host the verification file at:
  `https://your-domain.com/.well-known/assetlinks.json`
- Content-Type must be `application/json`
- **Vercel/Netlify**: Place this file in your project's `public/.well-known/` folder

### 3. Service Worker MIME Type
- `sw.js` must be served with `application/javascript`
- Standard hosts handle this automatically

---

## 📝 Updating Your PWA

Once deployed, updates to your web content happen automatically for users:

1. **Web Content Updates** (JS/CSS/HTML)
   - Just redeploy to your host (`vercel --prod`)
   - Users get the update seamlessly via Service Worker
   - **NO** Android app update required

2. **Native Updates** (Icon/Name/Manifest)
   - Requires rebuilding the APK/AAB
   - Must submit a new version to dApp Store

---

## 🔍 Verification

After deploying, verify your PWA is ready for Bubblewrap:

1. **Lighthouse Audit**
   - Open Chrome DevTools -> Lighthouse
   - Run audit on "Mobile"
   - Ensure "PWA" category is green/checkable

2. **Asset Links Access**
   - Visit `https://your-domain.com/.well-known/assetlinks.json`
   - Ensure it downloads/displays ensuring your SHA256 matches your keystore
