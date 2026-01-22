# Mobile Optimizations - MonkeMob TWA Sample

This sample app showcases a **highly mobile-optimized PWA** using the Bubblewrap template.

## ✨ Key Optimizations

### 1. Enhanced Splash Screen Styling
- **Pixel terminal aesthetic** with retro font (Press Start 2P)
- **Circular masked icon** (120dp) in gold frame (140dp)
- **Brand logo** (180dp) positioned at top
- **6 falling banana animations** (30-36dp each) with varying opacity
- **Black background** (#000000) with green scanline effect (#001a0d)
- **Fast fade-out** (300ms) for smooth transition

**Implementation**: [splash_terminal.xml](android-twa-generated/app/src/main/res/drawable/splash_terminal.xml)

### 2. Chrome Browser Preference with Fallback
- **Default TWA behavior** automatically prefers Chrome for best performance
- **Automatic fallback** to other Chromium-based browsers if Chrome unavailable
- **Custom tabs** fallback for devices without TWA support
- **No explicit browser selection needed** - handled by androidbrowserhelper library

**Implementation**: The `com.google.androidbrowserhelper:androidbrowserhelper:2.6.2` library automatically manages browser selection, preferring Chrome when available.

**Configuration**: [twa-manifest.json](android-twa-generated/twa-manifest.json)

```json
{
  "fallbackType": "customtabs",
  "display": "standalone"
}
```

### 3. Mobile-Intuitive Navigation & Layouts

#### Portrait-First Orientation
- **Portrait mode default** for mobile-first experience
- **Adaptive orientation handling** for Android 8.0+ compatibility
- **User-controlled rotation** allowed

#### Safe Area Support
- **Safe area insets** for notched/punch-hole displays
- **Viewport-fit: cover** for edge-to-edge display
- **Bottom navigation padding** respects gesture areas

#### Touch-Optimized UI
- **No tap highlight flash** (`-webkit-tap-highlight-color: transparent`)
- **Touch feedback** on interactive elements (scale 0.98)
- **Minimum 44x44dp touch targets**

#### Glassmorphism Design
- **Frosted glass panels** with backdrop blur
- **Soft shadows** and glow effects
- **Gradient backgrounds** with smooth animations
- **High contrast text** (#E6E9F2 on dark backgrounds)

**Implementation**: [styles.css](web/src/styles.css)

```css
:root {
  --glass-bg: rgba(18, 26, 42, 0.65);
  --glass-border: rgba(255, 255, 255, 0.08);
  --glass-blur: blur(20px);
}

.safe {
  padding-left: max(16px, env(safe-area-inset-left));
  padding-right: max(16px, env(safe-area-inset-right));
}
```

### 4. PWA Performance Features
- **Standalone display mode** for app-like experience
- **Notification delegation** enabled for native notifications
- **Web manifest** with all app metadata
- **Maskable icons** for adaptive icon support
- **Theme colors** for status/navigation bars (light & dark modes)

## 📱 Mobile-First Features Checklist

✅ **Visual Polish**
- Custom splash screen with animations
- Circular app icon with gold frame
- Brand-consistent colors (gold #FFD700, black #000000)

✅ **Browser Optimization**
- Chrome preferred for TWA reliability
- Automatic fallback to system browser
- Custom tabs support for older devices

✅ **Touch & Gestures**
- No tap highlight flashing
- Active state feedback (scale animation)
- Safe area inset handling

✅ **Layout & Navigation**
- Portrait orientation default
- Responsive glassmorphism cards
- Bottom sheet safe area padding
- Smooth scrolling with overscroll prevention

✅ **Performance**
- Fast splash fade-out (300ms)
- Hardware-accelerated animations
- Lazy loading ready
- Background task optimization

## 🛠️ Configuration Files

| File | Purpose |
|------|---------|
| [twa-manifest.json](android-twa-generated/twa-manifest.json) | TWA configuration (package, theme, display) |
| [AndroidManifest.xml](android-twa-generated/app/src/main/AndroidManifest.xml) | Android app manifest with TWA metadata |
| [LauncherActivity.java](android-twa-generated/app/src/main/java/me/monkemob/twa/LauncherActivity.java) | Custom launcher with Chrome preference |
| [splash_terminal.xml](android-twa-generated/app/src/main/res/drawable/splash_terminal.xml) | Pixel terminal splash screen |
| [index.html](web/index.html) | PWA HTML with mobile meta tags |
| [styles.css](web/src/styles.css) | Mobile-optimized glassmorphism styles |

## 🚀 Building the APK

```bash
cd android-twa-generated
./gradlew assembleRelease
```

Output: `app/build/outputs/apk/release/app-release.apk` (~1.1 MB)

## 📦 Package Info

- **Package**: me.monkemob.twa
- **Version**: 1.0.0 (code: 1)
- **Min SDK**: 24 (Android 7.0 Nougat)
- **Target SDK**: 35 (Android 15)
- **Size**: ~1.1 MB

## 🎨 Design System

**Colors**
- Primary: `#FFD700` (Gold)
- Background: `#000000` (Black)
- Text: `#E6E9F2` (Light Gray)
- Accent: `#9945FF` (Solana Purple)
- Success: `#14F195` (Solana Green)

**Typography**
- Splash: Press Start 2P (pixel font)
- App: System UI (iOS/Android native)

**Spacing**
- Safe area: `max(16px, env(safe-area-inset-*))`
- Card radius: 20px
- Button radius: 999px (pill)

## 📖 Best Practices Applied

1. **Chrome-First Strategy**: Prefer Chrome (com.android.chrome) for most reliable TWA experience, with automatic fallback
2. **Splash Screen UX**: Fast fade (300ms) with visually appealing pixel terminal aesthetic
3. **Safe Area Handling**: Proper insets for notched displays and gesture navigation
4. **Touch Optimization**: No highlight flash, active state feedback, minimum touch targets
5. **Glassmorphism**: Modern 2025 design with frosted glass, blur, and glow effects
6. **Portrait Orientation**: Mobile-first portrait mode with user rotation control
7. **Fallback Strategy**: Custom tabs for devices without full TWA support

## 📚 References

- [Bubblewrap Documentation](https://github.com/GoogleChromeLabs/bubblewrap)
- [Trusted Web Activity Guide](https://developer.chrome.com/docs/android/trusted-web-activity/)
- [PWA Best Practices](https://web.dev/pwa-checklist/)
- [Android Safe Area Insets](https://developer.android.com/develop/ui/views/layout/edge-to-edge)
