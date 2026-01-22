import { motion } from 'framer-motion';
import type { ReactNode } from 'react';

interface CodeBlockProps {
  children: string;
  language?: string;
  title?: string;
}

export function CodeBlock({ children, language = 'kotlin', title }: CodeBlockProps) {
  return (
    <div className="my-4 rounded-xl overflow-hidden border border-white/10">
      {title && (
        <div className="bg-[#1a1f2e] px-4 py-2 text-sm text-[#8B92A5] border-b border-white/10">
          {title}
        </div>
      )}
      <pre className="p-4 overflow-x-auto text-sm leading-relaxed !bg-[#0d1117] !border-0 !rounded-none">
        <code className={`language-${language}`}>{children}</code>
      </pre>
    </div>
  );
}

interface CalloutProps {
  type?: 'info' | 'warning' | 'error' | 'success';
  title?: string;
  children: ReactNode;
}

export function Callout({ type = 'info', title, children }: CalloutProps) {
  const styles = {
    info: 'border-[#9945FF]/50 bg-[#9945FF]/10',
    warning: 'border-yellow-500/50 bg-yellow-500/10',
    error: 'border-red-500/50 bg-red-500/10',
    success: 'border-[#14F195]/50 bg-[#14F195]/10',
  };

  const icons = {
    info: '💡',
    warning: '⚠️',
    error: '🚨',
    success: '✅',
  };

  return (
    <motion.div
      initial={{ opacity: 0, x: -20 }}
      whileInView={{ opacity: 1, x: 0 }}
      viewport={{ once: true }}
      className={`my-6 p-4 rounded-xl border ${styles[type]}`}
    >
      {title && (
        <div className="flex items-center gap-2 font-semibold mb-2">
          <span>{icons[type]}</span>
          <span>{title}</span>
        </div>
      )}
      <div className="text-[#8B92A5]">{children}</div>
    </motion.div>
  );
}

interface SectionProps {
  id?: string;
  title: string;
  children: ReactNode;
}

export function Section({ id, title, children }: SectionProps) {
  return (
    <motion.section
      id={id}
      initial={{ opacity: 0, y: 20 }}
      whileInView={{ opacity: 1, y: 0 }}
      viewport={{ once: true, margin: '-100px' }}
      className="py-8 border-t border-white/5 first:border-0 first:pt-0"
    >
      <h2 className="text-xl font-bold mb-6 flex items-center gap-2">
        <span className="gradient-text">{title}</span>
      </h2>
      {children}
    </motion.section>
  );
}

interface CardProps {
  icon: string;
  title: string;
  description: string;
  href: string;
  tag?: string;
  featured?: boolean;
}

export function Card({ icon, title, description, href, tag, featured }: CardProps) {
  return (
    <motion.a
      href={href}
      whileHover={{ y: -4 }}
      whileTap={{ scale: 0.98 }}
      className="block p-5 rounded-2xl glass hover:border-[#9945FF]/50 transition-colors"
    >
      <div className="text-3xl mb-3">{icon}</div>
      <h3 className="font-semibold text-lg mb-2">{title}</h3>
      <p className="text-[#8B92A5] text-sm leading-relaxed mb-3">{description}</p>
      {tag && (
        <span
          className={`inline-block px-3 py-1 rounded-full text-xs font-medium ${
            featured
              ? 'bg-[#14F195]/20 text-[#14F195]'
              : 'bg-[#9945FF]/20 text-[#9945FF]'
          }`}
        >
          {tag}
        </span>
      )}
    </motion.a>
  );
}
