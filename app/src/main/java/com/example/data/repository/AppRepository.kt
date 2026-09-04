package com.example.data.repository

import com.example.data.database.AiModelEntity
import com.example.data.database.AppDatabase
import com.example.data.database.ChatMessageEntity
import com.example.data.database.ChatSessionEntity
import com.example.data.database.CodeSnippetEntity
import com.example.data.database.ServerLogEntity
import com.example.data.database.VectorChunkEntity
import com.example.data.database.VectorCollectionEntity
import com.example.data.database.VectorDocumentEntity
import kotlinx.coroutines.flow.Flow

class AppRepository(private val database: AppDatabase) {

    val allSessions: Flow<List<ChatSessionEntity>> = database.chatDao().getAllSessions()
    val allModels: Flow<List<AiModelEntity>> = database.modelDao().getAllModels()
    val allCollections: Flow<List<VectorCollectionEntity>> = database.vectorDao().getAllCollections()
    val allSnippets: Flow<List<CodeSnippetEntity>> = database.codeDao().getAllSnippets()
    val recentServerLogs: Flow<List<ServerLogEntity>> = database.serverLogDao().getRecentLogs()

    suspend fun getSessionById(id: Long): ChatSessionEntity? = database.chatDao().getSessionById(id)

    suspend fun createSession(
        title: String,
        modelId: String,
        systemPrompt: String = "You are Aether AI, a powerful, privacy-first local on-device intelligence model.",
        mode: String = "CHAT",
        ragEnabled: Boolean = false,
        activePluginId: String? = null
    ): Long {
        val session = ChatSessionEntity(
            title = title,
            modelId = modelId,
            systemPrompt = systemPrompt,
            mode = mode,
            ragEnabled = ragEnabled,
            activePluginId = activePluginId
        )
        return database.chatDao().insertSession(session)
    }

    suspend fun updateSession(session: ChatSessionEntity) {
        database.chatDao().updateSession(session.copy(updatedAt = System.currentTimeMillis()))
    }

    suspend fun deleteSession(id: Long) {
        database.chatDao().deleteMessagesForSession(id)
        database.chatDao().deleteSession(id)
    }

    fun getMessagesForSession(sessionId: Long): Flow<List<ChatMessageEntity>> {
        return database.chatDao().getMessagesForSession(sessionId)
    }

    suspend fun insertMessage(message: ChatMessageEntity): Long {
        return database.chatDao().insertMessage(message)
    }

    suspend fun updateModel(model: AiModelEntity) {
        database.modelDao().updateModel(model)
    }

    suspend fun insertModel(model: AiModelEntity) {
        database.modelDao().insertModel(model)
    }

    suspend fun deleteModel(id: String) {
        database.modelDao().deleteModel(id)
    }

    suspend fun loadModelToRam(id: String) {
        database.modelDao().unloadAllModels()
        database.modelDao().setModelLoaded(id, true)
    }

    suspend fun unloadAllModels() {
        database.modelDao().unloadAllModels()
    }

    suspend fun getModelById(id: String): AiModelEntity? {
        return database.modelDao().getModelById(id)
    }

    // Vector Store Operations
    suspend fun createVectorCollection(name: String, description: String): Long {
        val collection = VectorCollectionEntity(name = name, description = description)
        return database.vectorDao().insertCollection(collection)
    }

    suspend fun deleteVectorCollection(id: Long) {
        database.vectorDao().deleteChunksForCollection(id)
        database.vectorDao().deleteDocumentsForCollection(id)
        database.vectorDao().deleteCollection(id)
    }

    suspend fun addDocumentToCollection(
        collectionId: Long,
        title: String,
        content: String,
        chunks: List<Pair<String, List<Float>>>
    ) {
        val docId = database.vectorDao().insertDocument(
            VectorDocumentEntity(
                collectionId = collectionId,
                title = title,
                content = content,
                chunkCount = chunks.size
            )
        )

        val chunkEntities = chunks.mapIndexed { index, pair ->
            VectorChunkEntity(
                documentId = docId,
                collectionId = collectionId,
                chunkIndex = index,
                text = pair.first,
                embeddingJson = pair.second.joinToString(",")
            )
        }
        database.vectorDao().insertChunks(chunkEntities)
    }

    suspend fun getChunksForCollection(collectionId: Long): List<VectorChunkEntity> {
        return database.vectorDao().getChunksForCollection(collectionId)
    }

    suspend fun getAllChunks(): List<VectorChunkEntity> {
        return database.vectorDao().getAllChunks()
    }

    // Code Snippet Operations
    suspend fun insertSnippet(snippet: CodeSnippetEntity): Long = database.codeDao().insertSnippet(snippet)
    suspend fun updateSnippet(snippet: CodeSnippetEntity) = database.codeDao().updateSnippet(snippet)
    suspend fun deleteSnippet(id: Long) = database.codeDao().deleteSnippet(id)

    // Server Logs
    suspend fun logServerRequest(log: ServerLogEntity) = database.serverLogDao().insertLog(log)
    suspend fun clearLogs() = database.serverLogDao().clearLogs()

    // Emergency Privacy Wipe
    suspend fun emergencyWipeAllData() {
        database.chatDao().clearAllMessages()
        database.chatDao().clearAllSessions()
        database.vectorDao().clearAllCollections()
        database.serverLogDao().clearLogs()
        database.modelDao().unloadAllModels()
    }

    // Seed Pre-configured Models & Sample Code
    suspend fun seedInitialDataIfEmpty() {
        val initialModels = listOf(
            AiModelEntity(
                id = "deepseek-r1-1.5b-q4",
                name = "DeepSeek-R1 (1.5B)",
                architecture = "DeepSeek-R1",
                parameterSize = "1.5B",
                quantization = "Q4_K_M",
                fileSizeMb = 1120,
                filePath = null,
                source = "HUGGING_FACE",
                hfRepoId = "deepseek-ai/DeepSeek-R1-Distill-Qwen-1.5B-GGUF",
                contextWindow = 8192,
                isDownloaded = false,
                downloadProgress = 0,
                isLoadedInRam = false,
                gpuOffloadLayers = 28,
                memoryUsageMb = 1450,
                description = "High-speed reasoning model with deep <think> cognitive traces for math, logic, and code."
            ),
            AiModelEntity(
                id = "llama-3.2-3b-q4",
                name = "Llama 3.2 (3B Instruct)",
                architecture = "Llama-3",
                parameterSize = "3.2B",
                quantization = "Q4_K_M",
                fileSizeMb = 1980,
                filePath = null,
                source = "HUGGING_FACE",
                hfRepoId = "meta-llama/Llama-3.2-3B-Instruct-GGUF",
                contextWindow = 8192,
                isDownloaded = false,
                downloadProgress = 0,
                isLoadedInRam = false,
                gpuOffloadLayers = 32,
                memoryUsageMb = 2100,
                description = "Meta's state-of-the-art mobile LLM with exceptional dialogue, coding, and general knowledge."
            ),
            AiModelEntity(
                id = "qwen2.5-coder-1.5b-q8",
                name = "Qwen 2.5 Coder (1.5B)",
                architecture = "Qwen-2.5",
                parameterSize = "1.5B",
                quantization = "Q8_0",
                fileSizeMb = 1650,
                filePath = null,
                source = "HUGGING_FACE",
                hfRepoId = "Qwen/Qwen2.5-Coder-1.5B-Instruct-GGUF",
                contextWindow = 16384,
                isDownloaded = false,
                downloadProgress = 0,
                isLoadedInRam = false,
                gpuOffloadLayers = 24,
                memoryUsageMb = 1750,
                description = "Specialized code generation & debugging model with high syntax precision."
            ),
            AiModelEntity(
                id = "phi-3.5-mini-q4",
                name = "Phi-3.5 Mini (3.8B)",
                architecture = "Phi-3.5",
                parameterSize = "3.8B",
                quantization = "Q4_K_M",
                fileSizeMb = 2300,
                filePath = null,
                source = "HUGGING_FACE",
                hfRepoId = "microsoft/Phi-3.5-mini-instruct-GGUF",
                contextWindow = 128000,
                isDownloaded = false,
                downloadProgress = 0,
                isLoadedInRam = false,
                gpuOffloadLayers = 32,
                memoryUsageMb = 2450,
                description = "Microsoft's compact powerhouse featuring massive context and reasoning capacity."
            ),
            AiModelEntity(
                id = "mistral-7b-instruct-v0.3-q4",
                name = "Mistral Instruct (7B)",
                architecture = "Mistral",
                parameterSize = "7.3B",
                quantization = "Q4_K_M",
                fileSizeMb = 4300,
                filePath = null,
                source = "OLLAMA_PULL",
                hfRepoId = "mistralai/Mistral-7B-Instruct-v0.3-GGUF",
                contextWindow = 32768,
                isDownloaded = false,
                downloadProgress = 0,
                isLoadedInRam = false,
                gpuOffloadLayers = 36,
                memoryUsageMb = 4600,
                description = "Industry standard 7B open model with broad reasoning and multilingual fluency."
            ),
            AiModelEntity(
                id = "whisper-tiny-en-q8",
                name = "Whisper Neural Audio Transcriber",
                architecture = "Whisper",
                parameterSize = "39M",
                quantization = "Q8_0",
                fileSizeMb = 75,
                filePath = null,
                source = "BUILTIN",
                hfRepoId = "openai/whisper-tiny",
                contextWindow = 1024,
                isDownloaded = false,
                downloadProgress = 0,
                isLoadedInRam = false,
                gpuOffloadLayers = 4,
                memoryUsageMb = 120,
                description = "Ultra-lightweight on-device speech-to-text recognition & neural audio transcription."
            ),
            AiModelEntity(
                id = "sd-mobile-v1.5",
                name = "Stable Diffusion Mobile (Vision)",
                architecture = "StableDiffusion",
                parameterSize = "860M",
                quantization = "FP16",
                fileSizeMb = 980,
                filePath = null,
                source = "BUILTIN",
                hfRepoId = "stabilityai/sd-turbo-mobile",
                contextWindow = 512,
                isDownloaded = false,
                downloadProgress = 0,
                isLoadedInRam = false,
                gpuOffloadLayers = 16,
                memoryUsageMb = 950,
                description = "On-device latent diffusion for photorealistic rendering, cyberpunk & anime concept art."
            )
        )
        database.modelDao().insertModels(initialModels)

        // Seed initial code snippets
        val initialSnippets = listOf(
            CodeSnippetEntity(
                title = "Fast Fibonacci (C++)",
                language = "cpp",
                code = """
                    #include <iostream>
                    #include <vector>

                    unsigned long long fib(int n) {
                        if (n <= 1) return n;
                        std::vector<unsigned long long> dp(n + 1, 0);
                        dp[1] = 1;
                        for (int i = 2; i <= n; i++) {
                            dp[i] = dp[i-1] + dp[i-2];
                        }
                        return dp[n];
                    }

                    int main() {
                        std::cout << "🚀 Running C++ on Aether AI Local Engine\n";
                        for (int i = 0; i <= 10; ++i) {
                            std::cout << "Fib(" << i << ") = " << fib(i) << "\n";
                        }
                        return 0;
                    }
                """.trimIndent()
            ),
            CodeSnippetEntity(
                title = "Rust Vector Multiplier",
                language = "rust",
                code = """
                    fn main() {
                        println!("⚡ Compiling and running Rust on-device!");
                        let numbers = vec![1, 2, 3, 4, 5, 6, 7, 8];
                        let squares: Vec<i32> = numbers.iter().map(|&x| x * x).collect();
                        println!("Original: {:?}", numbers);
                        println!("Squared:  {:?}", squares);
                    }
                """.trimIndent()
            ),
            CodeSnippetEntity(
                title = "Python Neural Sim",
                language = "python",
                code = """
                    # Aether AI Python Sandbox
                    import math

                    def sigmoid(x):
                        return 1 / (1 + math.exp(-x))

                    print("🧠 Computing neuron activations:")
                    inputs = [-2.0, -1.0, 0.0, 1.0, 2.0]
                    for x in inputs:
                        print(f"Activation({x:4.1f}) = {sigmoid(x):.4f}")
                """.trimIndent()
            ),
            CodeSnippetEntity(
                title = "Cyber HUD Interface (HTML/JS)",
                language = "html",
                code = """
                    <!DOCTYPE html>
                    <html>
                    <head>
                      <style>
                        body { background: #070A10; color: #00E5FF; font-family: monospace; padding: 20px; }
                        .card { border: 1px solid #00E5FF; padding: 15px; border-radius: 8px; box-shadow: 0 0 15px rgba(0,229,255,0.3); }
                        .btn { background: #8C52FF; color: white; border: none; padding: 8px 16px; border-radius: 4px; cursor: pointer; }
                      </style>
                    </head>
                    <body>
                      <div class="card">
                        <h2>⚡ AETHER AI HUD</h2>
                        <p>Status: Local Neural Core ONLINE</p>
                        <button class="btn" onclick="document.getElementById('pulse').innerText = 'Heartbeat: ' + Date.now()">Pulse Ping</button>
                        <p id="pulse">Ready</p>
                      </div>
                    </body>
                    </html>
                """.trimIndent()
            ),
            CodeSnippetEntity(
                title = "Java Data Matrix",
                language = "java",
                code = """
                    public class Main {
                        public static void main(String[] args) {
                            System.out.println("☕ Java JIT Sandbox on Aether AI");
                            int[][] matrix = {{1, 2}, {3, 4}};
                            int sum = 0;
                            for (int[] row : matrix) {
                                for (int val : row) sum += val;
                            }
                            System.out.println("Matrix elements total sum: " + sum);
                        }
                    }
                """.trimIndent()
            ),
            CodeSnippetEntity(
                title = "C Memory Pointer Demo",
                language = "c",
                code = """
                    #include <stdio.h>
                    #include <stdlib.h>

                    int main() {
                        printf("⚙️ C Execution Engine\n");
                        int *arr = (int*)malloc(5 * sizeof(int));
                        for(int i = 0; i < 5; i++) {
                            arr[i] = (i + 1) * 10;
                            printf("arr[%d] = %d (address: %p)\n", i, arr[i], (void*)&arr[i]);
                        }
                        free(arr);
                        return 0;
                    }
                """.trimIndent()
            )
        )
        for (snip in initialSnippets) {
            database.codeDao().insertSnippet(snip)
        }
    }

    suspend fun resetUnverifiedDownloadedModels() {
        try {
            val models = database.modelDao().getModelsList()
            models.forEach { model ->
                val isRealFile = model.filePath != null && java.io.File(model.filePath).exists()
                if (!isRealFile && (model.isDownloaded || model.isLoadedInRam)) {
                    database.modelDao().updateModel(
                        model.copy(
                            isDownloaded = false,
                            downloadProgress = 0,
                            isLoadedInRam = false,
                            filePath = null
                        )
                    )
                }
            }
        } catch (_: Exception) {}
    }
}
