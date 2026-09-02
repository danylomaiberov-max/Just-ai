package com.example.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "chat_sessions")
data class ChatSessionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val modelId: String,
    val systemPrompt: String,
    val mode: String = "CHAT", // CHAT, VISION, AUDIO, TRANSLATE, VIDEO, CODE
    val ragEnabled: Boolean = false,
    val activePluginId: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "chat_messages")
data class ChatMessageEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val sessionId: Long,
    val role: String, // "user", "assistant", "system", "tool"
    val content: String,
    val thoughtTrace: String? = null, // Collapsible <think> reasoning
    val mediaUri: String? = null,
    val mediaType: String? = null, // "image", "audio", "video", "code"
    val language: String? = null,
    val tokensGenerated: Int = 0,
    val tokPerSec: Float = 0f,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "ai_models")
data class AiModelEntity(
    @PrimaryKey val id: String, // e.g. "llama-3.2-3b-instruct-q4"
    val name: String,
    val architecture: String, // "Llama-3", "DeepSeek-R1", "Qwen-2.5", "Whisper", "StableDiffusion"
    val parameterSize: String, // "1.5B", "3B", "7B", "8B", "14B"
    val quantization: String, // "Q4_K_M", "Q5_K_M", "Q8_0", "FP16"
    val fileSizeMb: Long,
    val filePath: String? = null,
    val source: String, // "HUGGING_FACE", "LOCAL_STORAGE", "OLLAMA_PULL", "BUILTIN"
    val hfRepoId: String? = null,
    val contextWindow: Int = 4096,
    val isDownloaded: Boolean = false,
    val downloadProgress: Int = 0,
    val isLoadedInRam: Boolean = false,
    val gpuOffloadLayers: Int = 32,
    val memoryUsageMb: Int = 1850,
    val description: String = ""
)

@Entity(tableName = "vector_collections")
data class VectorCollectionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val description: String,
    val documentCount: Int = 0,
    val totalChunks: Int = 0,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "vector_documents")
data class VectorDocumentEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val collectionId: Long,
    val title: String,
    val content: String,
    val chunkCount: Int,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "vector_chunks")
data class VectorChunkEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val documentId: Long,
    val collectionId: Long,
    val chunkIndex: Int,
    val text: String,
    val embeddingJson: String // Stored float array vector
)

@Entity(tableName = "code_snippets")
data class CodeSnippetEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val language: String, // "cpp", "c", "rust", "python", "html", "java"
    val code: String,
    val lastOutput: String = "",
    val executionTimeMs: Long = 0,
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "server_logs")
data class ServerLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val method: String,
    val endpoint: String,
    val clientIp: String,
    val statusCode: Int,
    val latencyMs: Long,
    val tokensProcessed: Int,
    val timestamp: Long = System.currentTimeMillis()
)
