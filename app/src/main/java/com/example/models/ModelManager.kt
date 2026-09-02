package com.example.models

import com.example.data.database.AiModelEntity
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlin.random.Random

data class ModelDownloadProgress(
    val modelId: String = "",
    val progressPercent: Int = 0,
    val downloadedMb: Float = 0f,
    val totalSizeMb: Float = 0f,
    val writeSpeedMbps: Float = 0f,
    val etaSeconds: Int = 0,
    val statusPhase: String = "Инициализация сокета...",
    val isComplete: Boolean = false
)

data class HuggingFaceModelCard(
    val repoId: String,
    val title: String,
    val author: String,
    val category: String, // "LLM", "Diffusion", "Audio", "Vision", "Code"
    val downloads: String,
    val likes: Int,
    val availableQuantizations: List<String>,
    val defaultSizeMb: Long,
    val description: String
)

object ModelManager {

    val popularHuggingFaceModels = listOf(
        // LLMs
        HuggingFaceModelCard(
            repoId = "deepseek-ai/DeepSeek-R1-Distill-Qwen-1.5B-GGUF",
            title = "DeepSeek-R1 Distill (1.5B)",
            author = "deepseek-ai",
            category = "LLM",
            downloads = "4.2M",
            likes = 8400,
            availableQuantizations = listOf("Q4_K_M", "Q5_K_M", "Q8_0", "FP16"),
            defaultSizeMb = 1120,
            description = "Флагманская локальная модель рассуждений с явной цепочкой мыслей (Reasoning Trace) для мобильных устройств."
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
            description = "Высокоэффективная модель Meta с поддержкой русского языка, диалогов и вызова внешних функций."
        ),
        HuggingFaceModelCard(
            repoId = "Qwen/Qwen2.5-Coder-1.5B-Instruct-GGUF",
            title = "Qwen 2.5 Coder (1.5B)",
            author = "Qwen",
            category = "LLM",
            downloads = "2.1M",
            likes = 5100,
            availableQuantizations = listOf("Q4_K_M", "Q8_0"),
            defaultSizeMb = 1650,
            description = "Обучена на 5.5T токенов кода для генерации C++, Rust, Python, Kotlin и отладки алгоритмов."
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
            description = "Мощная модель от Microsoft с контекстным окном 128k и высокими баллами в математике и логике."
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
            description = "Универсальная 7B модель с высокой производительностью и адаптивным вниманием Sliding Window."
        ),

        // Local Image Generation / Diffusion Models
        HuggingFaceModelCard(
            repoId = "stabilityai/sd-turbo-mobile-gguf",
            title = "SD-Turbo Mobile (1-Step Realtime)",
            author = "stabilityai",
            category = "Diffusion",
            downloads = "3.8M",
            likes = 9200,
            availableQuantizations = listOf("Q4_0", "Q8_0", "FP16"),
            defaultSizeMb = 980,
            description = "Одношаговая сверхбыстрая диффузионная модель для генерации фото и артов прямо на процессоре и GPU смартфона."
        ),
        HuggingFaceModelCard(
            repoId = "runwayml/stable-diffusion-v1-5-gguf",
            title = "Stable Diffusion 1.5 (Local GGUF)",
            author = "runwayml",
            category = "Diffusion",
            downloads = "28.4M",
            likes = 34500,
            availableQuantizations = listOf("Q4_K_M", "Q8_0", "FP16"),
            defaultSizeMb = 1240,
            description = "Классический проверенный генератор фото, концепт-артов, аниме и портретов с поддержкой LoRA и стилей."
        ),
        HuggingFaceModelCard(
            repoId = "black-forest-labs/FLUX.1-schnell-mobile-gguf",
            title = "FLUX.1 Schnell Mobile (4-bit)",
            author = "black-forest-labs",
            category = "Diffusion",
            downloads = "5.6M",
            likes = 18700,
            availableQuantizations = listOf("Q4_0", "Q8_0"),
            defaultSizeMb = 2380,
            description = "Трансформерная диффузия нового поколения: фотореализм высшего качества, четкий текст и анатомия за 4 шага."
        ),
        HuggingFaceModelCard(
            repoId = "latent-consistency/lcm-sd15-mobile",
            title = "LCM SD-1.5 (Latent Consistency)",
            author = "latent-consistency",
            category = "Diffusion",
            downloads = "1.9M",
            likes = 4300,
            availableQuantizations = listOf("INT8", "FP16"),
            defaultSizeMb = 850,
            description = "Генерация за 2-4 шага с низкой нагрузкой на батарею и мгновенным откликом на мобильном Vulkan GPU."
        ),
        HuggingFaceModelCard(
            repoId = "PixArt-alpha/PixArt-sigma-mobile-gguf",
            title = "PixArt-Sigma (1K Diffusion)",
            author = "PixArt-alpha",
            category = "Diffusion",
            downloads = "940K",
            likes = 2800,
            availableQuantizations = listOf("Q4_K_M", "Q8_0"),
            defaultSizeMb = 1450,
            description = "Легковесный трансформер изображений с нативной поддержкой 4K детализации и кинематографичного освещения."
        ),

        // Audio & Vision
        HuggingFaceModelCard(
            repoId = "openai/whisper-tiny-ggml",
            title = "Whisper Tiny (Audio Transcriber)",
            author = "openai",
            category = "Audio",
            downloads = "6.8M",
            likes = 4300,
            availableQuantizations = listOf("Q8_0", "FP16"),
            defaultSizeMb = 75,
            description = "Сверхбыстрое локальное распознавание речи и транскрибация аудио на 99+ языках без интернета."
        ),
        HuggingFaceModelCard(
            repoId = "vikhyatk/moondream2-gguf",
            title = "Moondream 2 (Vision LLM)",
            author = "vikhyatk",
            category = "Vision",
            downloads = "1.4M",
            likes = 3100,
            availableQuantizations = listOf("Q4_K_M", "Q8_0"),
            defaultSizeMb = 890,
            description = "Компактная зрительная модель (1.86B) для локального анализа фото, распознавания объектов и OCR."
        )
    )

    fun downloadModelSimulation(
        modelCard: HuggingFaceModelCard,
        quantization: String
    ): Flow<ModelDownloadProgress> = flow {
        val modelId = "${modelCard.repoId.substringAfter("/")}-${quantization.lowercase()}"
        val quantMultiplier = when (quantization.uppercase()) {
            "Q4_0", "Q4_K_M" -> 0.95f
            "Q5_K_M" -> 1.15f
            "Q6_K" -> 1.35f
            "Q8_0" -> 1.75f
            "FP16" -> 2.6f
            else -> 1.0f
        }
        val targetSizeMb = (modelCard.defaultSizeMb * quantMultiplier).coerceAtLeast(60f)

        emit(
            ModelDownloadProgress(
                modelId = modelId,
                progressPercent = 0,
                downloadedMb = 0f,
                totalSizeMb = targetSizeMb,
                writeSpeedMbps = 0f,
                etaSeconds = (targetSizeMb / 25f).toInt().coerceAtLeast(5),
                statusPhase = "Подключение к репозиторию Hugging Face...",
                isComplete = false
            )
        )
        delay(250)

        var downloadedMb = 0f
        val startTime = System.currentTimeMillis()

        while (downloadedMb < targetSizeMb) {
            val chunk = (targetSizeMb * Random.nextFloat().coerceIn(0.04f, 0.12f)).coerceAtMost(targetSizeMb - downloadedMb)
            downloadedMb += chunk
            val elapsedSec = (System.currentTimeMillis() - startTime) / 1000f
            val currentSpeed = if (elapsedSec > 0) downloadedMb / elapsedSec else 26.5f
            val remainingMb = targetSizeMb - downloadedMb
            val etaSec = if (currentSpeed > 0) (remainingMb / currentSpeed).toInt().coerceAtLeast(1) else 1
            val percent = ((downloadedMb / targetSizeMb) * 100).toInt().coerceIn(0, 100)

            val phase = when {
                percent < 20 -> "Чтение метаданных GGUF тензоров..."
                percent < 80 -> "Запись весов в локальное хранилище..."
                percent < 95 -> "Верификация CRC контрольных сумм..."
                else -> "Кэширование слоев в память устройства..."
            }

            emit(
                ModelDownloadProgress(
                    modelId = modelId,
                    progressPercent = percent,
                    downloadedMb = String.format("%.1f", downloadedMb).toFloatOrNull() ?: downloadedMb,
                    totalSizeMb = String.format("%.1f", targetSizeMb).toFloatOrNull() ?: targetSizeMb,
                    writeSpeedMbps = String.format("%.1f", currentSpeed).toFloatOrNull() ?: currentSpeed,
                    etaSeconds = if (percent >= 100) 0 else etaSec,
                    statusPhase = phase,
                    isComplete = percent >= 100
                )
            )

            delay(140 + Random.nextLong(90))
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
                card.category == "Diffusion" -> "Diffusion"
                else -> "1B"
            },
            quantization = quantization,
            fileSizeMb = card.defaultSizeMb,
            filePath = if (isDownloaded) "/data/user/0/models/$modelId.gguf" else null,
            source = "HUGGING_FACE",
            hfRepoId = card.repoId,
            contextWindow = if (card.category == "Diffusion") 512 else 8192,
            isDownloaded = isDownloaded,
            downloadProgress = if (isDownloaded) 100 else 0,
            isLoadedInRam = false,
            gpuOffloadLayers = 28,
            memoryUsageMb = (card.defaultSizeMb * 1.1).toInt(),
            description = card.description
        )
    }

    fun createCustomHuggingFaceEntity(
        repoId: String,
        quantization: String = "Q4_K_M",
        estimatedSizeMb: Long = 1800L,
        category: String = "LLM"
    ): AiModelEntity {
        val shortName = repoId.substringAfterLast("/")
        val modelId = "hf-${shortName.lowercase().replace("[^a-z0-9]".toRegex(), "-")}-${quantization.lowercase()}"
        return AiModelEntity(
            id = modelId,
            name = "$shortName ($quantization)",
            architecture = category,
            parameterSize = if (estimatedSizeMb > 3000) "7B" else "3B",
            quantization = quantization,
            fileSizeMb = estimatedSizeMb,
            filePath = "/data/user/0/models/$modelId.gguf",
            source = "HUGGING_FACE_CUSTOM",
            hfRepoId = repoId,
            contextWindow = 8192,
            isDownloaded = true,
            downloadProgress = 100,
            isLoadedInRam = false,
            gpuOffloadLayers = 24,
            memoryUsageMb = (estimatedSizeMb * 1.05).toInt(),
            description = "Модель загружена напрямую из Hugging Face репозитория: $repoId"
        )
    }

    fun createLocalImportedModel(
        fileName: String,
        fileSizeMb: Long,
        filePath: String,
        quantizationGuess: String = "Q4_K_M"
    ): AiModelEntity {
        val cleanName = fileName.substringBeforeLast(".")
        val isDiffusion = fileName.contains("sd", ignoreCase = true) ||
                fileName.contains("flux", ignoreCase = true) ||
                fileName.contains("diffusion", ignoreCase = true)

        return AiModelEntity(
            id = "local-" + cleanName.lowercase().replace("[^a-z0-9]".toRegex(), "-"),
            name = cleanName,
            architecture = if (isDiffusion) "Diffusion" else "Custom GGUF",
            parameterSize = if (fileSizeMb > 3000) "7B" else "3B",
            quantization = quantizationGuess,
            fileSizeMb = fileSizeMb,
            filePath = filePath,
            source = "LOCAL_STORAGE",
            hfRepoId = null,
            contextWindow = if (isDiffusion) 512 else 4096,
            isDownloaded = true,
            downloadProgress = 100,
            isLoadedInRam = false,
            gpuOffloadLayers = 24,
            memoryUsageMb = (fileSizeMb * 1.05).toInt(),
            description = "Локальная модель, импортированная из хранилища устройства: $filePath"
        )
    }
}
