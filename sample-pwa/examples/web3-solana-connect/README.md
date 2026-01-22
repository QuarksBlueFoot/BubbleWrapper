# Web3 Solana Connect Example (separate folder)

This is a separate example folder intended to be treated like its own mini repo.

## What it demonstrates

- A mobile-first PWA shell
- Optional Solana wallet connect and signMessage using window.solana when available

## Run

```bash
cd web
npm i
npm run dev
```

## Notes

- Connect and sign depend on the environment. In a regular Chrome browser without a wallet provider, the demo will show a friendly message.
- For store submission, keep your production dApp logic in your main PWA and use this folder as a reference for wallet friendly UX patterns.
