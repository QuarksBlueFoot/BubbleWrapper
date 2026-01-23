package xyz.bluefoot.bubblewrapper.network

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface CanaryAuthApi {
    @POST("api/auth/device/challenge")
    suspend fun getChallenge(@Body request: ChallengeRequest): Response<ChallengeResponse>

    @POST("api/auth/device/verify")
    suspend fun verifySignature(@Body request: VerifyRequest): Response<VerifyResponse>
}

interface ExternalRpcApi {
    @POST("api/external/rpc")
    suspend fun callRpc(
        @Header("Authorization") token: String,
        @Body request: ProxyRpcRequest
    ): Response<ProxyRpcResponse>
}

data class ChallengeRequest(val walletAddress: String)
data class ChallengeResponse(val message: String)

data class VerifyRequest(
    val walletAddress: String,
    val signature: String,
    val message: String
)
data class VerifyResponse(val accessToken: String)

data class ProxyRpcRequest(
    val method: String,
    val params: Any? 
)

data class ProxyRpcResponse(
    val jsonrpc: String?,
    val result: Any?,
    val error: Any?,
    val id: String?
)
