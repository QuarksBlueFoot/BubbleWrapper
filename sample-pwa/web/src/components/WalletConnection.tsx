import React, { FC, useCallback, useEffect, useState } from 'react';
import { useConnection, useWallet } from '@solana/wallet-adapter-react';
import { LAMPORTS_PER_SOL } from '@solana/web3.js';
import bs58 from 'bs58';

export const WalletConnection: FC = () => {
  const { connection } = useConnection();
  const { publicKey, connected, connecting, disconnect, select, wallets, wallet } = useWallet();
  const [balance, setBalance] = useState<number | null>(null);
  const [copied, setCopied] = useState(false);

  // Fetch balance when connected
  useEffect(() => {
    if (!publicKey || !connected) {
      setBalance(null);
      return;
    }

    let mounted = true;
    
    const fetchBalance = async () => {
      try {
        const bal = await connection.getBalance(publicKey);
        if (mounted) {
          setBalance(bal / LAMPORTS_PER_SOL);
        }
      } catch (e) {
        console.error('Failed to fetch balance:', e);
        if (mounted) setBalance(null);
      }
    };

    fetchBalance();
    const interval = setInterval(fetchBalance, 30000); // Refresh every 30s

    return () => {
      mounted = false;
      clearInterval(interval);
    };
  }, [publicKey, connected, connection]);

  const handleConnect = useCallback(() => {
    // Select MWA wallet (first in list)
    if (wallets.length > 0) {
      select(wallets[0].adapter.name);
    }
  }, [wallets, select]);

  const handleDisconnect = useCallback(() => {
    disconnect();
  }, [disconnect]);

  const copyAddress = useCallback(() => {
    if (publicKey) {
      navigator.clipboard.writeText(publicKey.toBase58());
      setCopied(true);
      setTimeout(() => setCopied(false), 2000);
    }
  }, [publicKey]);

  const formatAddress = (address: string): string => {
    return `${address.slice(0, 4)}...${address.slice(-4)}`;
  };

  if (connecting) {
    return (
      <div className="card card-glow" style={{ padding: 20, borderRadius: 22, textAlign: 'center' }}>
        <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'center', gap: 12 }}>
          <div className="spinner" style={{ width: 20, height: 20 }} />
          <span style={{ color: 'var(--muted)', fontSize: 14 }}>Connecting to wallet...</span>
        </div>
      </div>
    );
  }

  if (connected && publicKey) {
    return (
      <div className="card card-glow" style={{ padding: 20, borderRadius: 22 }}>
        <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', marginBottom: 16 }}>
          <div style={{ display: 'flex', alignItems: 'center', gap: 10 }}>
            <div style={{
              width: 10,
              height: 10,
              borderRadius: '50%',
              background: 'var(--secondary)',
              boxShadow: '0 0 8px var(--secondary)'
            }} />
            <span style={{ fontSize: 13, fontWeight: 600, color: 'var(--secondary)' }}>Connected</span>
          </div>
          <button
            onClick={handleDisconnect}
            style={{
              padding: '6px 12px',
              borderRadius: 8,
              background: 'rgba(255,255,255,0.05)',
              border: '1px solid rgba(255,255,255,0.1)',
              color: 'var(--muted)',
              fontSize: 12,
              cursor: 'pointer'
            }}
          >
            Disconnect
          </button>
        </div>

        <div 
          onClick={copyAddress}
          style={{
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'space-between',
            padding: 14,
            background: 'rgba(255,255,255,0.03)',
            borderRadius: 12,
            cursor: 'pointer',
            marginBottom: 12
          }}
        >
          <div>
            <div style={{ fontSize: 11, color: 'var(--muted)', marginBottom: 4 }}>WALLET ADDRESS</div>
            <div style={{ 
              fontSize: 16, 
              fontWeight: 700,
              fontFamily: 'monospace',
              background: 'linear-gradient(135deg, #9945FF, #14F195)',
              WebkitBackgroundClip: 'text',
              WebkitTextFillColor: 'transparent',
              backgroundClip: 'text'
            }}>
              {formatAddress(publicKey.toBase58())}
            </div>
          </div>
          <span style={{ fontSize: 12, color: copied ? 'var(--secondary)' : 'var(--muted)' }}>
            {copied ? '✓ Copied!' : 'Tap to copy'}
          </span>
        </div>

        <div style={{
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'space-between',
          padding: 14,
          background: 'rgba(255,255,255,0.03)',
          borderRadius: 12
        }}>
          <div>
            <div style={{ fontSize: 11, color: 'var(--muted)', marginBottom: 4 }}>BALANCE</div>
            <div style={{ fontSize: 20, fontWeight: 700 }}>
              {balance !== null ? (
                <>
                  <span style={{ color: 'var(--text)' }}>{balance.toFixed(4)}</span>
                  <span style={{ color: 'var(--muted)', fontSize: 14, marginLeft: 4 }}>SOL</span>
                </>
              ) : (
                <span style={{ color: 'var(--muted)' }}>Loading...</span>
              )}
            </div>
          </div>
          <img 
            src="/solana-logo.png" 
            alt="SOL" 
            style={{ width: 32, height: 32, opacity: 0.8 }}
          />
        </div>
      </div>
    );
  }

  return (
    <button
      onClick={handleConnect}
      className="gradient-btn"
      style={{
        width: '100%',
        padding: 16,
        borderRadius: 16,
        border: 'none',
        fontSize: 15,
        fontWeight: 700,
        cursor: 'pointer',
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'center',
        gap: 10
      }}
    >
      <img src="/solana-logo.png" alt="" style={{ width: 22, height: 22 }} />
      Connect Wallet
    </button>
  );
};
