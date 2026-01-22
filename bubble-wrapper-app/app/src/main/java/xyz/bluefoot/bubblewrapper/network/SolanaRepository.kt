package xyz.bluefoot.bubblewrapper.network

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.util.concurrent.TimeUnit

/**
 * Solana RPC response types
 */
data class RpcResponse<T>(
    val jsonrpc: String,
    val id: Int,
    val result: T?,
    val error: RpcError?
)

data class RpcError(
    val code: Int,
    val message: String
)

data class BalanceResult(
    val value: Long
)

data class AccountInfo(
    val lamports: Long,
    val owner: String,
    val executable: Boolean,
    val rentEpoch: Long,
    val data: Any?
)

data class RecentBlockhashResult(
    val blockhash: String,
    val feeCalculator: FeeCalculator
)

data class FeeCalculator(
    val lamportsPerSignature: Long
)

data class SendTransactionResult(
    val value: String
)

data class TransactionStatus(
    val slot: Long,
    val confirmations: Int?,
    val err: Any?,
    val confirmationStatus: String?
)

/**
 * Repository for Solana blockchain interactions
 */
class SolanaRepository(
    // MAINNET: Production ready with real Arweave URIs
    private val rpcUrl: String = "https://api.mainnet-beta.solana.com"
    // DEVNET: For testing only
    // private val rpcUrl: String = "https://api.devnet.solana.com"
) {
    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()
    
    private val gson = Gson()
    private val jsonMediaType = "application/json".toMediaType()
    
    companion object {
        const val LAMPORTS_PER_SOL = 1_000_000_000L
        
        // Common RPC endpoints
        const val MAINNET_RPC = "https://api.mainnet-beta.solana.com"
        const val DEVNET_RPC = "https://api.devnet.solana.com"
        
        // Recommended private RPCs for dApp Store publishing
        val RECOMMENDED_RPCS = listOf(
            "Helius" to "https://rpc.helius.xyz/?api-key=YOUR_KEY",
            "QuickNode" to "https://YOUR_ENDPOINT.solana-mainnet.quiknode.pro/YOUR_KEY/",
            "Alchemy" to "https://solana-mainnet.g.alchemy.com/v2/YOUR_KEY",
            "Triton" to "https://YOUR_PROJECT.rpcpool.com"
        )
    }
    
    /**
     * Get SOL balance for a public key
     */
    suspend fun getBalance(publicKey: String): Result<Double> = withContext(Dispatchers.IO) {
        try {
            val request = createRpcRequest(
                method = "getBalance",
                params = listOf(publicKey)
            )
            
            val response = executeRequest<BalanceResult>(request)
            response.result?.let {
                Result.success(it.value.toDouble() / LAMPORTS_PER_SOL)
            } ?: Result.failure(Exception(response.error?.message ?: "Unknown error"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Get recent blockhash for transaction building
     */
    suspend fun getRecentBlockhash(): Result<String> = withContext(Dispatchers.IO) {
        try {
            val request = createRpcRequest(
                method = "getLatestBlockhash",
                params = listOf(mapOf("commitment" to "finalized"))
            )
            
            val response = executeRequest<Map<String, Any>>(request)
            val result = response.result
            if (result != null) {
                val value = result["value"] as? Map<*, *>
                val blockhash = value?.get("blockhash") as? String
                if (blockhash != null) {
                    Result.success(blockhash)
                } else {
                    Result.failure(Exception("Blockhash not found in response"))
                }
            } else {
                Result.failure(Exception(response.error?.message ?: "Unknown error"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Send a signed transaction
     */
    suspend fun sendTransaction(signedTransaction: String): Result<String> = withContext(Dispatchers.IO) {
        try {
            val request = createRpcRequest(
                method = "sendTransaction",
                params = listOf(
                    signedTransaction,
                    mapOf(
                        "encoding" to "base64",
                        "preflightCommitment" to "confirmed"
                    )
                )
            )
            
            val response = executeRequest<String>(request)
            response.result?.let {
                Result.success(it)
            } ?: Result.failure(Exception(response.error?.message ?: "Transaction failed"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Confirm a transaction
     */
    suspend fun confirmTransaction(
        signature: String,
        maxRetries: Int = 30,
        delayMs: Long = 1000
    ): Result<Boolean> = withContext(Dispatchers.IO) {
        repeat(maxRetries) {
            try {
                val request = createRpcRequest(
                    method = "getSignatureStatuses",
                    params = listOf(listOf(signature))
                )
                
                val response = executeRequest<Map<String, Any>>(request)
                val value = response.result?.get("value") as? List<*>
                val status = value?.firstOrNull() as? Map<*, *>
                
                if (status != null) {
                    val confirmationStatus = status["confirmationStatus"] as? String
                    if (confirmationStatus == "confirmed" || confirmationStatus == "finalized") {
                        return@withContext Result.success(true)
                    }
                    val err = status["err"]
                    if (err != null) {
                        return@withContext Result.failure(Exception("Transaction failed: $err"))
                    }
                }
                
                kotlinx.coroutines.delay(delayMs)
            } catch (e: Exception) {
                // Continue retrying
            }
        }
        Result.failure(Exception("Transaction confirmation timeout"))
    }
    
    /**
     * Get minimum rent for account
     */
    suspend fun getMinimumBalanceForRentExemption(dataSize: Int): Result<Long> = withContext(Dispatchers.IO) {
        try {
            val request = createRpcRequest(
                method = "getMinimumBalanceForRentExemption",
                params = listOf(dataSize)
            )
            
            val response = executeRequest<Long>(request)
            response.result?.let {
                Result.success(it)
            } ?: Result.failure(Exception(response.error?.message ?: "Unknown error"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Check if RPC endpoint is healthy
     */
    suspend fun checkHealth(): Result<Boolean> = withContext(Dispatchers.IO) {
        try {
            val request = createRpcRequest(
                method = "getHealth",
                params = emptyList<Any>()
            )
            
            val response = executeRequest<String>(request)
            Result.success(response.result == "ok")
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Get current slot
     */
    suspend fun getSlot(): Result<Long> = withContext(Dispatchers.IO) {
        try {
            val request = createRpcRequest(
                method = "getSlot",
                params = emptyList<Any>()
            )
            
            val response = executeRequest<Long>(request)
            response.result?.let {
                Result.success(it)
            } ?: Result.failure(Exception(response.error?.message ?: "Unknown error"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    private fun createRpcRequest(method: String, params: List<Any>): Request {
        val body = mapOf(
            "jsonrpc" to "2.0",
            "id" to 1,
            "method" to method,
            "params" to params
        )
        
        return Request.Builder()
            .url(rpcUrl)
            .post(gson.toJson(body).toRequestBody(jsonMediaType))
            .addHeader("Content-Type", "application/json")
            .build()
    }
    
    private inline fun <reified T> executeRequest(request: Request): RpcResponse<T> {
        val response = client.newCall(request).execute()
        val responseBody = response.body?.string() ?: throw Exception("Empty response")
        
        // Parse as generic map first to handle dynamic result types
        val rawResponse = gson.fromJson(responseBody, Map::class.java)
        
        val error = rawResponse["error"]?.let {
            val errorMap = it as? Map<*, *>
            RpcError(
                code = (errorMap?.get("code") as? Number)?.toInt() ?: 0,
                message = errorMap?.get("message") as? String ?: "Unknown error"
            )
        }
        
        val result = try {
            val resultJson = gson.toJson(rawResponse["result"])
            gson.fromJson(resultJson, T::class.java)
        } catch (e: Exception) {
            null
        }
        
        return RpcResponse(
            jsonrpc = rawResponse["jsonrpc"] as? String ?: "2.0",
            id = (rawResponse["id"] as? Number)?.toInt() ?: 1,
            result = result,
            error = error
        )
    }
    
    /**
     * Update RPC endpoint
     */
    fun withRpcUrl(newUrl: String): SolanaRepository {
        return SolanaRepository(newUrl)
    }
}
