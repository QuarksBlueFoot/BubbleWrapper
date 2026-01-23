const Irys = require("@irys/sdk");
const fs = require("fs");
const path = require("path");

const PRIVATE_KEY = "5kRe8gdPCMsTTEwsRvFGVZszo8GfpfNJ7Q76MKfTPVF3Xv5UGZcMh2Wye9MLjGbFQts4GdsLiERzGfkiPfMS4qLb";
const DIRECTORY = "bubble-wrapper-app/monkemob_publish";
const APK_PATH = "DOWNLOADS/monkemob-release.apk"; // Real MonkeMob APK
const PACKAGE_ID = "me.monkemob.twa"; // Correct MonkeMob package
const PUBLISHER_NAME = "Bluefoot Labs";
const PUBLISHER_WEBSITE = "https://monkemob.me";
const PUBLISHER_EMAIL = "support@monkemob.me";

async function main() {
    try {
        console.log("Initializing Irys for REAL upload...");
        const irys = new Irys.default({
            url: "https://node2.irys.xyz", // Using node2 for Mainnet
            token: "solana",
            key: PRIVATE_KEY,
        });
        
        console.log(`Connected wallet address: ${irys.address}`);
        
        const files = [
            { name: "icon.png", path: path.join(DIRECTORY, "icon.png") },
            { name: "banner.png", path: path.join(DIRECTORY, "banner.png") },
            { name: "screen1.png", path: path.join(DIRECTORY, "screen1.png") },
            { name: "screen2.png", path: path.join(DIRECTORY, "screen2.png") },
            { name: "screen3.png", path: path.join(DIRECTORY, "screen3.png") },
            { name: "screen4.png", path: path.join(DIRECTORY, "screen4.png") },
            { name: "app-release.apk", path: APK_PATH }
        ];
        
        // We will process files and store URIs
        const uris = {};

        // Calculate total size and verify files exist
        let totalSize = 0;
        for (const file of files) {
            try {
                const stats = fs.statSync(file.path);
                totalSize += stats.size;
                console.log(`  ✓ ${file.name}: ${(stats.size / 1024).toFixed(2)} KB`);
            } catch (e) {
                console.error(`Missing file: ${file.name} at ${file.path}`);
                process.exit(1);
            }
        }

        console.log(`\nTotal upload size: ${(totalSize / 1024 / 1024).toFixed(2)} MB`);
        
        // Check balance and price
        const balanceAtomic = await irys.getLoadedBalance();
        const balance = irys.utils.fromAtomic(balanceAtomic);
        console.log(`Current Irys Balance: ${balance} SOL`);

        const priceAtomic = await irys.getPrice(totalSize);
        const price = irys.utils.fromAtomic(priceAtomic);
        console.log(`Estimated cost: ${price} SOL`);

        if (balanceAtomic.isLessThan(priceAtomic)) {
            console.log("\nFunding Irys node...");
            const fundAmount = priceAtomic.minus(balanceAtomic).plus(irys.utils.toAtomic(0.002));
            try {
                const fundTx = await irys.fund(fundAmount);
                console.log(`Funded: ${irys.utils.fromAtomic(fundTx.quantity)} SOL`);
            } catch (e) {
                console.error("Error funding node:", e.message);
            }
        }

        // Use existing Arweave URIs (already uploaded)
        console.log("\n📦 Using existing Arweave URIs...\n");
        uris["icon.png"] = "https://arweave.net/ZhJDiUDv0x4BFevw3IbRY_2dEigC5iqwaxTkK-uuICI";
        uris["banner.png"] = "https://arweave.net/6YzBi5Ly-BaQu-MrxIAim1tLRbpB_AIUq3nchjtodns";
        uris["screen1.png"] = "https://arweave.net/9XwqcmiMGVJvuKxvMMMLeJx5YkPaVfTNLyxL54uupg4";
        uris["screen2.png"] = "https://arweave.net/wFePz6umxZE1Hp-7x2kpYAhTdeZqLaVOI7quSeZWdig";
        uris["screen3.png"] = "https://arweave.net/irJ64h0i-xvvQ1UUq_YdE_7GF2PeJHnl6PCDP7ZJ4zg";
        uris["screen4.png"] = "https://arweave.net/VHrbZDcWuhwtTDlXlIOi0p5E346XivnAKYNXiegwOZk";
        uris["app-release.apk"] = "https://arweave.net/9ealqZqXgwchznt4lO2dabuKbOujSf3z8gtgnbVaPGs";
        
        console.log("  ✓ All assets already on Arweave (reusing)");

        // Load site manifest if present and create Metadata JSON
        let siteManifest = null;
        try {
            const manifestRaw = fs.readFileSync(path.join(__dirname, 'monkemob_manifest.webmanifest'), 'utf8');
            siteManifest = JSON.parse(manifestRaw);
            console.log('Loaded site manifest:', siteManifest.short_name || siteManifest.name);
        } catch (e) {
            console.log('No local manifest file found, falling back to defaults.');
        }

        const siteBase = 'https://www.monkemob.me';
        const manifestIcon = (() => {
            if (!siteManifest || !siteManifest.icons) return uris['icon.png'];
            // prefer 512px non-maskable icon then maskable then first
            const pick = siteManifest.icons.find(i => i.sizes && i.sizes.includes('512') && (!i.purpose || i.purpose === 'any'))
                || siteManifest.icons.find(i => i.sizes && i.sizes.includes('512'))
                || siteManifest.icons[0];
            if (!pick) return uris['icon.png'];
            return pick.src.startsWith('http') ? pick.src : `${siteBase}${pick.src}`;
        })();

        const metadata = {
            "name": (siteManifest && (siteManifest.name || siteManifest.short_name)) || "MonkeMob",
            "symbol": "MONKE",
            "description": (siteManifest && siteManifest.description) || "MonkeMob is a community-driven Solana NFT collection featuring unique monkey characters. Join the mob and explore Web3 gaming!",
            "image": uris["icon.png"],
            "external_url": "https://monkemob.me",
            "attributes": [
                {"trait_type": "Category", "value": "Game"},
                {"trait_type": "Blockchain", "value": "Solana"},
                {"trait_type": "Platform", "value": "Mobile"}
            ],
            "properties": {
                "files": [
                    {"uri": uris["icon.png"], "type": "image/png"},
                    {"uri": uris["banner.png"], "type": "image/png"},
                    {"uri": uris["screen1.png"], "type": "image/png"},
                    {"uri": uris["screen2.png"], "type": "image/png"},
                    {"uri": uris["screen3.png"], "type": "image/png"},
                    {"uri": uris["screen4.png"], "type": "image/png"},
                    {"uri": uris["app-release.apk"], "type": "application/vnd.android.package-archive"}
                ],
                "category": "image",
                "creators": [
                    {
                        "address": "GGVjQqnriuUdeLPoadfrV6CrpToYPmDquSBqdFpYhBts",
                        "share": 100
                    }
                ]
            },
            "collection": {
                "name": "MonkeMob",
                "family": "MonkeMob"
            },
            // Solana dApp Store Specification (v0.4.0)
            "appMetadata": {
                "packageId": PACKAGE_ID,
                "version": "1.0.0",
                "versionCode": 1,
                "category": "Games",
                "publisher": PUBLISHER_NAME,
                "developer": PUBLISHER_NAME,
                "publisher_details": {
                    "name": PUBLISHER_NAME,
                    "website": PUBLISHER_WEBSITE,
                    "contact": PUBLISHER_EMAIL,
                    "support_email": PUBLISHER_EMAIL
                },
                "screenshots": [
                    uris["screen1.png"],
                    uris["screen2.png"],
                    uris["screen3.png"],
                    uris["screen4.png"]
                ],
                "icon": uris["icon.png"],
                "banner": uris["banner.png"],
                "shortDescription": (siteManifest && (siteManifest.short_name || siteManifest.name)) || "MonkeMob - Join the Mob!",
                "longDescription": (siteManifest && siteManifest.description) || "MonkeMob isn't just a meme, it's a movement. We are building the future of social finance and gaming on Solana. This app provides access to the MonkeMob ecosystem, including our secure Canary Messenger and exclusive content.",
                "website": PUBLISHER_WEBSITE,
                "apk": uris["app-release.apk"],
                "android_package": PACKAGE_ID,
                "min_sdk": 24,
                "target_sdk": 34,
                "google_play_url": "",
                "privacy_policy_url": `${PUBLISHER_WEBSITE}/privacy`,
                "license_url": `${PUBLISHER_WEBSITE}/license`,
                "support_url": PUBLISHER_WEBSITE,
                "supported_devices": ["phone", "tablet"],
                "locales": ["en"],
                "release_date": new Date().toISOString().split('T')[0]
            }
        };

        const metadataStr = JSON.stringify(metadata, null, 2);
        const metadataPath = path.join(DIRECTORY, "metadata.json");
        fs.writeFileSync(metadataPath, metadataStr);
        console.log("\n📝 Metadata JSON saved to:", metadataPath);

        // Upload Metadata
        console.log("\n📤 Uploading metadata.json...");
        const receipt = await irys.upload(metadataStr, { 
            tags: [{ name: "Content-Type", value: "application/json" }] 
        });
        
        const metadataUri = `https://arweave.net/${receipt.id}`;
        console.log("\n" + "=".repeat(60));
        console.log("✅ UPLOAD COMPLETE!");
        console.log("=".repeat(60));
        console.log("\nMetadata URI:", metadataUri);
        console.log("\nAll assets uploaded to Arweave:");
        console.log("  • Icon:", uris["icon.png"]);
        console.log("  • Banner:", uris["banner.png"]);
        console.log("  • Screenshots:", Object.keys(uris).filter(k => k.startsWith('screen')).length);
        console.log("  • APK:", uris["app-release.apk"]);
        console.log("\n" + "=".repeat(60));
        console.log("⚠️  READY FOR ON-CHAIN PUBLISHING");
        console.log("=".repeat(60));
        console.log("\nNext step: Run publish script with this metadata URI");
        console.log("Metadata URI saved to: last_metadata_uri.txt\n");
        
        // Save the metadata URI to a file for the publish script
        fs.writeFileSync("last_metadata_uri.txt", metadataUri);

    } catch (e) {
        console.error("Error in upload script:", e);
    }
}

function getContentType(filename) {
    if (filename.endsWith(".png")) return "image/png";
    if (filename.endsWith(".jpg")) return "image/jpeg";
    if (filename.endsWith(".apk")) return "application/vnd.android.package-archive";
    return "application/octet-stream";
}

main();
