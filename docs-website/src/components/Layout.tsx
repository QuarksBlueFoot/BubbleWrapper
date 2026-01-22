import { Outlet, Link, useLocation } from 'react-router-dom';
import { motion, AnimatePresence } from 'framer-motion';
import { Home, BookOpen, Link2, Rocket, Menu, X, Smartphone } from 'lucide-react';
import { useState } from 'react';

const navItems = [
  { path: '/', icon: Home, label: 'Home' },
  { path: '/twa-guide', icon: Smartphone, label: 'TWA Guide' },
  { path: '/mwa-guide', icon: Link2, label: 'MWA' },
  { path: '/publishing', icon: Rocket, label: 'Publish' },
  { path: '/digital-asset-links', icon: BookOpen, label: 'DAL' },
];

export function Layout() {
  const location = useLocation();
  const [menuOpen, setMenuOpen] = useState(false);

  return (
    <div className="min-h-screen flex flex-col bg-[#0B0F1A]">
      {/* Header - Mobile optimized */}
      <header className="sticky top-0 z-50 glass safe-top">
        <div className="max-w-4xl mx-auto px-4 h-14 flex items-center justify-between">
          <Link to="/" className="flex items-center gap-2">
            <span className="text-2xl">🫧</span>
            <span className="font-semibold text-white hidden sm:block">BubbleWrapper</span>
          </Link>
          
          {/* Desktop nav */}
          <nav className="hidden md:flex items-center gap-1">
            {navItems.map((item) => (
              <Link
                key={item.path}
                to={item.path}
                className={`px-4 py-2 rounded-lg text-sm font-medium transition-all ${
                  location.pathname === item.path
                    ? 'bg-white/10 text-white'
                    : 'text-[#8B92A5] hover:text-white hover:bg-white/5'
                }`}
              >
                {item.label}
              </Link>
            ))}
          </nav>

          {/* Mobile menu button */}
          <button
            onClick={() => setMenuOpen(!menuOpen)}
            className="md:hidden p-2 rounded-lg hover:bg-white/5 active:bg-white/10 transition-colors"
          >
            {menuOpen ? <X size={24} /> : <Menu size={24} />}
          </button>
        </div>
      </header>

      {/* Mobile menu overlay */}
      <AnimatePresence>
        {menuOpen && (
          <motion.div
            initial={{ opacity: 0 }}
            animate={{ opacity: 1 }}
            exit={{ opacity: 0 }}
            className="fixed inset-0 z-40 bg-black/80 md:hidden"
            onClick={() => setMenuOpen(false)}
          >
            <motion.nav
              initial={{ x: '100%' }}
              animate={{ x: 0 }}
              exit={{ x: '100%' }}
              transition={{ type: 'spring', damping: 25, stiffness: 200 }}
              className="absolute right-0 top-0 h-full w-72 bg-[#111827] p-6 pt-20"
              onClick={(e) => e.stopPropagation()}
            >
              {navItems.map((item) => (
                <Link
                  key={item.path}
                  to={item.path}
                  onClick={() => setMenuOpen(false)}
                  className={`flex items-center gap-3 px-4 py-4 rounded-xl text-lg font-medium transition-all mb-2 ${
                    location.pathname === item.path
                      ? 'bg-gradient-to-r from-[#9945FF]/20 to-[#14F195]/20 text-white'
                      : 'text-[#8B92A5] hover:text-white hover:bg-white/5'
                  }`}
                >
                  <item.icon size={24} />
                  {item.label}
                </Link>
              ))}
            </motion.nav>
          </motion.div>
        )}
      </AnimatePresence>

      {/* Main content - with bottom padding for mobile nav */}
      <main className="flex-1 overflow-y-auto scroll-container pb-24 md:pb-8">
        <AnimatePresence mode="wait">
          <motion.div
            key={location.pathname}
            initial={{ opacity: 0, y: 20 }}
            animate={{ opacity: 1, y: 0 }}
            exit={{ opacity: 0, y: -20 }}
            transition={{ duration: 0.2 }}
          >
            <Outlet />
          </motion.div>
        </AnimatePresence>
      </main>

      {/* Bottom navigation - Mobile */}
      <nav className="md:hidden fixed bottom-0 left-0 right-0 glass safe-bottom z-50">
        <div className="flex justify-around items-center h-16">
          {navItems.map((item) => {
            const isActive = location.pathname === item.path;
            return (
              <Link
                key={item.path}
                to={item.path}
                className={`flex flex-col items-center justify-center w-16 h-full transition-all touch-active ${
                  isActive ? 'text-[#14F195]' : 'text-[#8B92A5]'
                }`}
              >
                <motion.div
                  animate={isActive ? { scale: 1.1 } : { scale: 1 }}
                  transition={{ type: 'spring', stiffness: 400, damping: 17 }}
                >
                  <item.icon size={24} />
                </motion.div>
                <span className="text-xs mt-1 font-medium">{item.label}</span>
                {isActive && (
                  <motion.div
                    layoutId="bottomNavIndicator"
                    className="absolute bottom-1 w-8 h-1 rounded-full bg-gradient-to-r from-[#9945FF] to-[#14F195]"
                  />
                )}
              </Link>
            );
          })}
        </div>
      </nav>
    </div>
  );
}
