package com.example.models

import com.example.data.database.AiModelEntity
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlin.random.Random

data class HuggingFaceModelCard(
    val repoId: String,
    val title: String,
    val author: String,
    val category: String, // "LLM", "Vision", "Audio", "Code", "Embeddings"
    val downloads: String,
    val likes: Int,
    val availableQuantizations: List<String>,
    val defaultSizeMb: Long,
    val description: String
)

object ModelManager {

    val popularHuggingFaceModels = listOf(
        HuggingFaceModelCard(
            repoId = "deepseek-ai/DeepSeek-R1-Distill-Qwen-1.5B-GGUF",
            title = "DeepSeek-R1 Distill (1.5B)",
            author = "deepseek-ai",
            category = "LLM",
            downloads = "4.2M",
            likes = 8400,
            availableQuantizations = listOf("Q4_K_M", "Q5_K_M", "Q8_0", "FP16"),
            defaultSizeMb = 1120,
            description = "State of the art on-device reasoning model with explicit cognitive reflection traces."
        ),
        HuggingFaceModelCard(
            repoId = "meta-llama/Llama-3.2-3B-Instruct-GGUF",
            title = "Llama-3.2 (3B Instruct)",
            author = "meta-llama",
            category = "LLM",
            downloads = "8.9M",
            likes = 12900,
            availableQuantizations = listOf("Q4_K_M", "Q6_K", "Q8_0"),
            defaultSizeMb = 1980,
            description = "High efficiency lightweight model fine-tuned for multilingual dialogue and tools."
        ),
        HuggingFaceModelCard(
            repoId = "Qwen/Qwen2.5-Coder-1.5B-Instruct-GGUF",
            title = "Qwen 2.5 Coder (1.5B)",
            author = "Qwen",
            category = "Code",
            downloads = "2.1M",
            likes = 5100,
            availableQuantizations = listOf("Q4_K_M", "Q8_0"),
            defaultSizeMb = 1650,
            description = "Trained on 5.5T code tokens for AST parsing, debugging, and multi-language synthesis."
        ),
        HuggingFaceModelCard(
            repoId = "microsoft/Phi-3.5-mini-instruct-GGUF",
            title = "Phi-3.5 Mini (3.8B)",
            author = "microsoft",
            category = "LLM",
            downloads = "3.4M",
            likes = 6700,
            availableQuantizations = listOf("Q4_K_M", "Q8_0"),
            defaultSizeMb = 2300,
            description = "128k context length powerhouse with strong benchmark scores in math and reasoning."
        ),
        HuggingFaceModelCard(
            repoId = "mistralai/Mistral-7B-Instruct-v0.3-GGUF",
            title = "Mistral Instruct (7B)",
            author = "mistralai",
            category = "LLM",
            downloads = "15.1M",
            likes = 22400,
            availableQuantizations = listOf("Q4_K_M", "Q5_K_M", "Q8_0"),
            defaultSizeMb = 4300,
            description = "High performance general purpose foundation model with sliding window attention."
        ),
        HuggingFaceModelCard(
            repoId = "openai/whisper-tiny-ggml",
            title = "Whisper Tiny (Audio Transcriber)",
            author = "openai",
            category = "Audio",
            downloads = "6.8M",
            likes = 4300,
            availableQuantizations = listOf("Q8_0", "FP16"),
            defaultSizeMb = 75,
            description = "Ultra-fast neural speech-to-text recognition with 99+ language support."
        ),
        HuggingFaceModelCard(
            repoId = "stabilityai/sd-turbo-mobile-gguf",
            title = "SD Turbo Mobile (Image Diffusion)",
            author = "stabilityai",
            category = "Vision",
            downloads = "1.8M",
            likes = 3900,
            availableQuantizations = listOf("FP16", "Q8_0"),
            defaultSizeMb = 980,
            description = "Single-step on-device adversarial diffusion model for real-time photo generation."
        )
    )

    fun downloadModelSimulation(
        modelCard: HuggingFaceModelCard,
        quantization: String
    ): Flow<Int> = flow {
        emit(0)
        var progress = 0
        while (progress < 100) {
            val step = Random.nextInt(4, 12)
            progress = (progress + step).coerceAtMost(100)
            delay(150)
            emit(progress)
        }
    }

    fun convertHuggingFaceToEntity(
        card: HuggingFaceModelCard,
        quantization: String,
        isDownloaded: Boolean = false
    ): AiModelEntity {
        val modelId = "${card.repoId.substringAfter("/")}-${quantization.lowercase()}"
        return AiModelEntity(
            id = modelId,
            name = "${card.title} ($quantization)",
            architecture = card.category,
            parameterSize = when {
                card.title.contains("1.5B") -> "1.5B"
                card.title.contains("3B") || card.title.contains("3.8B") -> "3B"
                card.title.contains("7B") -> "7B"
                else -> "1B"
            },
            quantization = quantization,
            fileSizeMb = card.defaultSizeMb,
            filePath = if (isDownloaded) "/data/user/0/models/$modelId.gguf" else null,
            source = "HUGGING_FACE",
            hfRepoId = card.repoId,
            contextWindow = 8192,
            isDownloaded = isDownloaded,
            downloadProgress = if (isDownloaded) 100 else 0,
            isLoadedInRam = false,
            gpuOffloadLayers = 28,
            memoryUsageMb = (card.defaultSizeMb * 1.1).toInt(),
            description = card.description
        )
    }

    fun createLocalImportedModel(
        fileName: String,
        fileSizeMb: Long,
        filePath: String,
        quantizationGuess: String = "Q4_K_M"
    ): AiModelEntity {
        val cleanName = fileName.substringBeforeLast(".")
        return AiModelEntity(
            id = "local-" + cleanName.lowercase().replace("[^a-z0-9]".toRegex(), "-"),
            name = cleanName,
            architecture = "Custom GGUF",
            parameterSize = if (fileSizeMb > 3000) "7B" else "3B",
            quantization = quantizationGuess,
            fileSizeMb = fileSizeMb,
            filePath = filePath,
            source = "LOCAL_STORAGE",
            hfRepoId = null,
            contextWindow = 4096,
            isDownloaded = true,
            downloadProgress = 100,
            isLoadedInRam = false,
            gpuOffloadLayers = 24,
            memoryUsageMb = (fileSizeMb * 1.05).toInt(),
            description = "Locally imported GGUF/Safetensors model from device storage: $filePath"
        )
    }
}
