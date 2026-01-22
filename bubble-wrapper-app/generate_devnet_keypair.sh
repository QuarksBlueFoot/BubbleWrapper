#!/bin/bash
echo "Generating devnet keypair using Kotlin script..."

# Run a simple Kotlin script that generates keypair
cat > /tmp/GenKeypair.kt << 'KOTLIN'
import org.bouncycastle.crypto.params.Ed25519PrivateKeyParameters
import org.bouncycastle.crypto.params.Ed25519PublicKeyParameters
import org.bouncycastle.crypto.generators.Ed25519KeyPairGenerator
import org.bouncycastle.crypto.KeyGenerationParameters
import java.security.SecureRandom
import java.util.Base64

fun main() {
    val generator = Ed25519KeyPairGenerator()
    generator.init(KeyGenerationParameters(SecureRandom(), 256))
    val keyPair = generator.generateKeyPair()
    
    val privateKeyParams = keyPair.private as Ed25519PrivateKeyParameters
    val publicKeyParams = keyPair.public as Ed25519PublicKeyParameters
    
    val privateKey = privateKeyParams.encoded
    val publicKey = publicKeyParams.encoded
    
    // Solana format: [private_key(32), public_key(32)]
    val secretKey = ByteArray(64)
    System.arraycopy(privateKey, 0, secretKey, 0, 32)
    System.arraycopy(publicKey, 0, secretKey, 32, 32)
    
    val publicKeyBase58 = base58Encode(publicKey)
    val secretKeyBase58 = base58Encode(secretKey)
    
    println("═══════════════════════════════════════════════════════════")
    println("🔑 DEVNET TEST KEYPAIR")
    println("═══════════════════════════════════════════════════════════")
    println()
    println("Public Key:")
    println(publicKeyBase58)
    println()
    println("Fund at: https://faucet.solana.com/")
    println("Or run: solana airdrop 2 $publicKeyBase58 --url devnet")
    println()
    println("Secret Key Base58:")
    println(secretKeyBase58)
    println()
    println("═══════════════════════════════════════════════════════════")
}

fun base58Encode(bytes: ByteArray): String {
    val alphabet = "123456789ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz"
    var num = java.math.BigInteger(1, bytes)
    val result = StringBuilder()
    while (num > java.math.BigInteger.ZERO) {
        val remainder = num.mod(java.math.BigInteger.valueOf(58))
        result.insert(0, alphabet[remainder.toInt()])
        num = num.divide(java.math.BigInteger.valueOf(58))
    }
    for (b in bytes) {
        if (b.toInt() == 0) result.insert(0, '1') else break
    }
    return result.toString()
}
KOTLIN

echo "Script created. Building with gradle..."
