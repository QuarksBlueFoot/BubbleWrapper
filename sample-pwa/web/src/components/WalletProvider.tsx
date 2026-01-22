import React, { FC, ReactNode, useMemo } from 'react';
import { ConnectionProvider, WalletProvider } from '@solana/wallet-adapter-react';
import { WalletModalProvider } from '@solana/wallet-adapter-react-ui';
import { SolanaMobileWalletAdapter } from '@solana-mobile/wallet-adapter-mobile';
import { clusterApiUrl } from '@solana/web3.js';

// Import wallet adapter CSS
import '@solana/wallet-adapter-react-ui/styles.css';

interface Props {
  children: ReactNode;
}

export const SolanaWalletProvider: FC<Props> = ({ children }) => {
  // Use mainnet for production
  const endpoint = useMemo(() => clusterApiUrl('mainnet-beta'), []);

  // Configure wallets - prioritize Mobile Wallet Adapter for Solana Mobile
  const wallets = useMemo(
    () => [
      new SolanaMobileWalletAdapter({
        addressSelector: {
          // Select first account by default
          select: async (addresses) => addresses[0],
        },
        appIdentity: {
          name: 'BubbleWrapper Sample',
          uri: 'https://bubblewrapper.bluefoot.xyz',
          icon: '/icons/icon-192.png',
        },
        authorizationResultCache: {
          // Simple in-memory cache
          get: async () => null,
          set: async () => {},
          clear: async () => {},
        },
        cluster: 'mainnet-beta',
      }),
    ],
    []
  );

  return (
    <ConnectionProvider endpoint={endpoint}>
      <WalletProvider wallets={wallets} autoConnect>
        <WalletModalProvider>
          {children}
        </WalletModalProvider>
      </WalletProvider>
    </ConnectionProvider>
  );
};
