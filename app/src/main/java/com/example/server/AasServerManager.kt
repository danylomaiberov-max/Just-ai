package com.example.server

import com.example.data.database.ServerLogEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.random.Random

data class ServerStatus(
    val isRunning: Boolean = true,
    val host: String = "127.0.0.1",
    val port: Int = 11434,
    val activeModelId: String = "deepseek-r1-1.5b-q4",
    val totalRequests: Int = 42,
    val totalTokensServed: Long = 18450,
    val averageLatencyMs: Long = 142,
    val activeConnections: Int = 1,
    val requireApiKey: Boolean = false,
    val apiKey: String = "sk-aether-local-key-9921",
    val allowLanAccess: Boolean = true
)

object AasServerManager {

    private val _serverStatus = MutableStateFlow(ServerStatus())
    val serverStatus: StateFlow<ServerStatus> = _serverStatus.asStateFlow()

    fun toggleServer(enable: Boolean) {
        _serverStatus.value = _serverStatus.value.copy(isRunning = enable)
    }

    fun updateConfig(port: Int, requireApiKey: Boolean, allowLan: Boolean) {
        _serverStatus.value = _serverStatus.value.copy(
            port = port,
            requireApiKey = requireApiKey,
            allowLanAccess = allowLan
        )
    }

    fun recordIncomingRequest(
        method: String,
        endpoint: String,
        tokens: Int,
        latencyMs: Long,
        clientIp: String = "127.0.0.1"
    ): ServerLogEntity {
        val current = _serverStatus.value
        _serverStatus.value = current.copy(
            totalRequests = current.totalRequests + 1,
            totalTokensServed = current.totalTokensServed + tokens,
            averageLatencyMs = (current.averageLatencyMs * 4 + latencyMs) / 5
        )

        return ServerLogEntity(
            method = method,
            endpoint = endpoint,
            clientIp = clientIp,
            statusCode = 200,
            latencyMs = latencyMs,
            tokensProcessed = tokens
        )
    }

    fun generateCurlCommand(endpoint: String, payloadJson: String): String {
        val st = _serverStatus.value
        val host = if (st.allowLanAccess) "192.168.1.104" else "localhost"
        val authHeader = if (st.requireApiKey) " -H \"Authorization: Bearer ${st.apiKey}\"" else ""
        return """
            curl -X POST http://$host:${st.port}$endpoint \
              -H "Content-Type: application/json"$authHeader \
              -d '$payloadJson'
        """.trimIndent()
    }

    fun getSampleOllamaCurl(): String {
        return generateCurlCommand(
            "/api/chat",
            """{"model": "deepseek-r1:1.5b", "messages": [{"role": "user", "content": "Write a quicksort in C++"}], "stream": false}"""
        )
    }

    fun getSampleOpenAiCurl(): String {
        return generateCurlCommand(
            "/v1/chat/completions",
            """{"model": "llama-3.2-3b", "messages": [{"role": "user", "content": "Hello Aether!"}]}"""
        )
    }
}
