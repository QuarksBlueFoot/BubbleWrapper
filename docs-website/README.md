# BubbleWrapper Documentation Website

A mobile-optimized React documentation site for BubbleWrapper, styled like Solana Mobile.

## Tech Stack

- **React** + TypeScript
- **Tailwind CSS** v4
- **Framer Motion** for animations
- **React Router** for navigation
- **Lucide React** for icons

## Features

- 📱 Mobile-first design with bottom navigation
- 🎨 Solana Mobile-inspired dark theme with glassmorphism
- ✨ Smooth page transitions and micro-animations
- 📲 Safe area support for notched devices
- 🚀 Fast Vite build

## Development

```bash
npm install
npm run dev
```

## Build

```bash
npm run build
```

Output in `dist/` folder.

## Deploy to Railway

1. Connect your GitHub repo to Railway
2. Set build command: `npm run build`
3. Set start command: `npx serve dist -s -l 3000`
4. Deploy!

## Pages

| Route | Description |
|-------|-------------|
| `/` | Home with guide cards |
| `/mwa-guide` | MWA Connection Guide |
| `/publishing` | dApp Store Publishing Guide |
| `/digital-asset-links` | DAL Setup Guide |

---

🫧 **BubbleWrapper** by [Bluefoot Labs](https://bluefoot.xyz)
