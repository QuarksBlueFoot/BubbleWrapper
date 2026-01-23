const { Keypair, Connection, PublicKey } = require("@solana/web3.js");
const { Metaplex, keypairIdentity, bundlrStorage } = require("@metaplex-foundation/js");
const bs58 = require("bs58");
const fs = require("fs");

// MonkeMob wallet
const SECRET_KEY = "5kRe8gdPCMsTTEwsRvFGVZszo8GfpfNJ7Q76MKfTPVF3Xv5UGZcMh2Wye9MLjGbFQts4GdsLiERzGfkiPfMS4qLb";

// Metadata URI
const METADATA_URI = "https://arweave.net/gj9Z9bdz2VkjH8uJfkbDwvXIHl3gUDnwFk7JvCMNNf4";

async function main() {
    console.log("=" .repeat(70));
    console.log("🚀 MonkeMob Publishing Tool (Metaplex SDK)");
    console.log("=".repeat(70));
    console.log();

    // Connect to Solana Mainnet
    const connection = new Connection("https://api.mainnet-beta.solana.com", "confirmed");
    console.log("Network: Mainnet Beta");
    console.log();

    // Load wallet
    const walletKeypair = Keypair.fromSecretKey(bs58.decode(SECRET_KEY));
    console.log(`Wallet: ${walletKeypair.publicKey.toBase58()}`);

    // Check balance
    const balance = await connection.getBalance(walletKeypair.publicKey);
    console.log(`💰 Balance: ${(balance / 1e9).toFixed(6)} SOL`);
    
    if (balance < 2000000) { // 0.002 SOL
        console.log("   ❌ Insufficient balance");
        return;
    }
    console.log();

    // Initialize Metaplex
    const metaplex = Metaplex.make(connection)
        .use(keypairIdentity(walletKeypair))
        .use(bundlrStorage());

    console.log("📦 Creating NFT...");
    console.log(`   Name: MonkeMob`);
    console.log(`   Symbol: MONKE`);
    console.log(`   URI: ${METADATA_URI}`);
    console.log();

    try {
        // Create NFT
        const { nft } = await metaplex.nfts().create({
            uri: METADATA_URI,
            name: "MonkeMob",
            symbol: "MONKE",
            sellerFeeBasisPoints: 0,
            isMutable: true,
            maxSupply: 0, // Unlimited supply for dApp Store
        });

        console.log("=".repeat(70));
        console.log("✅ NFT CREATED SUCCESSFULLY!");
        console.log("=".repeat(70));
        console.log();
        console.log(`🔑 Mint Address: ${nft.address.toBase58()}`);
        console.log(`📊 Metadata: ${nft.metadataAddress.toBase58()}`);
        console.log(`🎨 Master Edition: ${nft.edition?.address?.toBase58() || 'N/A'}`);
        console.log();
        console.log("🔍 View on Explorer:");
        console.log(`   https://explorer.solana.com/address/${nft.address.toBase58()}`);
        console.log();
        console.log("📋 Next Steps:");
        console.log("1. Submit to: https://publish.solanamobile.com");
        console.log(`2. Provide mint address: ${nft.address.toBase58()}`);
        console.log("3. Wait for review (3-4 business days)");
        console.log();

        // Save result
        fs.writeFileSync("nft_publish_result.json", JSON.stringify({
            mint: nft.address.toBase58(),
            metadata: nft.metadataAddress.toBase58(),
            metadataUri: METADATA_URI,
            timestamp: new Date().toISOString()
        }, null, 2));
        console.log("✅ Results saved to: nft_publish_result.json");

    } catch (error) {
        console.error("❌ Error creating NFT:", error.message);
        console.error(error);
    }
}

main();
