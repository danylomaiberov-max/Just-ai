package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.ai.engine.GenerationMetrics
import com.example.ai.engine.InferenceConfig
import com.example.ai.engine.LocalAiEngine
import com.example.ai.multimodal.AudioSynthesisResult
import com.example.ai.multimodal.ImageGenerationResult
import com.example.ai.multimodal.MultiModalStudioEngine
import com.example.ai.multimodal.TranslationResult
import com.example.ai.multimodal.VideoGenerationResult
import com.example.compiler.ExecutionResult
import com.example.compiler.MultiLangCompilerEngine
import com.example.data.database.AiModelEntity
import com.example.data.database.AppDatabase
import com.example.data.database.ChatMessageEntity
import com.example.data.database.ChatSessionEntity
import com.example.data.database.CodeSnippetEntity
import com.example.data.database.ServerLogEntity
import com.example.data.database.VectorCollectionEntity
import com.example.data.repository.AppRepository
import com.example.hardware.DeviceHardwareEngine
import com.example.hardware.HardwareBackend
import com.example.hardware.HardwareBenchmarkResult
import com.example.hardware.HardwareRealtimeStats
import com.example.hardware.HardwareSpecs
import com.example.hardware.HardwareStressTestProgress
import com.example.models.HuggingFaceModelCard
import com.example.models.ModelManager
import com.example.plugins.PluginSystem
import com.example.privacy.PrivacyShieldManager
import com.example.privacy.PrivacyTelemetry
import com.example.server.AasServerManager
import com.example.server.ServerStatus
import com.example.vectordb.SearchResult
import com.example.vectordb.VectorDatabaseEngine
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class AppTab {
    CHAT,
    HARDWARE_RUNNER,
    STUDIO_MULTIMODAL,
    MODELS_HUB,
    AAS_SERVER,
    VECTOR_RAG,
    CODE_IDE,
    SETTINGS_PRIVACY
}

enum class StudioSubTab {
    PHOTO,
    VIDEO,
    VOICE_TTS,
    TRANSLATE
}

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: AppRepository = AppRepository(AppDatabase.getDatabase(application))

    // Hardware Engine & Real-time Specs
    val hardwareSpecs: HardwareSpecs = DeviceHardwareEngine.getDeviceHardwareSpecs(application)

    private val _inferenceConfig = MutableStateFlow(InferenceConfig())
    val inferenceConfig: StateFlow<InferenceConfig> = _inferenceConfig.asStateFlow()

    private val _hardwareRealtimeStats = MutableStateFlow(
        DeviceHardwareEngine.getRealtimeStats(application, HardwareBackend.GPU_VULKAN)
    )
    val hardwareRealtimeStats: StateFlow<HardwareRealtimeStats> = _hardwareRealtimeStats.asStateFlow()

    private val _benchmarkProgress = MutableStateFlow(HardwareStressTestProgress())
    val benchmarkProgress: StateFlow<HardwareStressTestProgress> = _benchmarkProgress.asStateFlow()

    private val _tensorTestResult = MutableStateFlow<Pair<Long, Float>?>(null)
    val tensorTestResult: StateFlow<Pair<Long, Float>?> = _tensorTestResult.asStateFlow()

    private val _isTensorTesting = MutableStateFlow(false)
    val isTensorTesting: StateFlow<Boolean> = _isTensorTesting.asStateFlow()

    // Active Navigation
    private val _currentTab = MutableStateFlow(AppTab.CHAT)
    val currentTab: StateFlow<AppTab> = _currentTab.asStateFlow()

    private val _studioSubTab = MutableStateFlow(StudioSubTab.PHOTO)
    val studioSubTab: StateFlow<StudioSubTab> = _studioSubTab.asStateFlow()

    // Data Flows from Repository
    val allSessions: StateFlow<List<ChatSessionEntity>> = repository.allSessions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allModels: StateFlow<List<AiModelEntity>> = repository.allModels
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allCollections: StateFlow<List<VectorCollectionEntity>> = repository.allCollections
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allSnippets: StateFlow<List<CodeSnippetEntity>> = repository.allSnippets
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val recentLogs: StateFlow<List<ServerLogEntity>> = repository.recentServerLogs
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val serverStatus: StateFlow<ServerStatus> = AasServerManager.serverStatus
    val privacyTelemetry: StateFlow<PrivacyTelemetry> = PrivacyShieldManager.telemetry

    // Active Chat Session State
    private val _currentSessionId = MutableStateFlow<Long?>(null)
    val currentSessionId: StateFlow<Long?> = _currentSessionId.asStateFlow()

    private val _currentMessages = MutableStateFlow<List<ChatMessageEntity>>(emptyList())
    val currentMessages: StateFlow<List<ChatMessageEntity>> = _currentMessages.asStateFlow()

    private val _isGenerating = MutableStateFlow(false)
    val isGenerating: StateFlow<Boolean> = _isGenerating.asStateFlow()

    private val _currentStreamingText = MutableStateFlow("")
    val currentStreamingText: StateFlow<String> = _currentStreamingText.asStateFlow()

    private val _currentThoughtTrace = MutableStateFlow("")
    val currentThoughtTrace: StateFlow<String> = _currentThoughtTrace.asStateFlow()

    private val _liveMetrics = MutableStateFlow<GenerationMetrics?>(null)
    val liveMetrics: StateFlow<GenerationMetrics?> = _liveMetrics.asStateFlow()

    private var generationJob: Job? = null

    init {
        viewModelScope.launch {
            repository.seedInitialDataIfEmpty()
        }
        viewModelScope.launch {
            while (true) {
                val currentBackend = _inferenceConfig.value.hardwareBackend
                _hardwareRealtimeStats.value = DeviceHardwareEngine.getRealtimeStats(application, currentBackend)
                delay(2500)
            }
        }
    }

    fun setTab(tab: AppTab) {
        _currentTab.value = tab
    }

    fun setStudioSubTab(subTab: StudioSubTab) {
        _studioSubTab.value = subTab
    }

    fun updateInferenceConfig(config: InferenceConfig) {
        _inferenceConfig.value = config
    }

    // Chat Session Management
    fun selectSession(sessionId: Long) {
        _currentSessionId.value = sessionId
        viewModelScope.launch {
            repository.getMessagesForSession(sessionId).collect { messages ->
                _currentMessages.value = messages
            }
        }
    }

    fun startNewChat(title: String = "New Session", modelId: String = "deepseek-r1-1.5b-q4", ragEnabled: Boolean = false) {
        viewModelScope.launch {
            val id = repository.createSession(
                title = title,
                modelId = modelId,
                ragEnabled = ragEnabled
            )
            selectSession(id)
        }
    }

    fun deleteSession(sessionId: Long) {
        viewModelScope.launch {
            repository.deleteSession(sessionId)
            if (_currentSessionId.value == sessionId) {
                _currentSessionId.value = null
                _currentMessages.value = emptyList()
            }
        }
    }

    fun sendMessage(userText: String, activeModelId: String = "deepseek-r1-1.5b-q4", pluginId: String? = null) {
        if (userText.isBlank() || _isGenerating.value) return

        val sId = _currentSessionId.value
        viewModelScope.launch {
            val sessionId = sId ?: repository.createSession("Chat: ${userText.take(24)}", activeModelId)
            if (sId == null) {
                _currentSessionId.value = sessionId
            }

            // Save User Message
            repository.insertMessage(
                ChatMessageEntity(
                    sessionId = sessionId,
                    role = "user",
                    content = userText
                )
            )

            // Vector RAG Context Search if enabled
            var ragContext: String? = null
            val chunks = repository.getAllChunks()
            if (chunks.isNotEmpty()) {
                val results = VectorDatabaseEngine.search(userText, chunks, topK = 2)
                if (results.isNotEmpty()) {
                    ragContext = results.joinToString("\n\n") { it.chunkText }
                }
            }

            // Plugin Execution if active
            var pluginOutput: String? = null
            if (pluginId != null) {
                pluginOutput = PluginSystem.executePlugin(pluginId, userText)
            }

            // Begin Streaming Assistant Generation
            _isGenerating.value = true
            _currentStreamingText.value = ""
            _currentThoughtTrace.value = ""
            _liveMetrics.value = null

            generationJob = launch {
                val model = repository.getModelById(activeModelId)
                val modelName = model?.name ?: "DeepSeek-R1"

                LocalAiEngine.generateStream(
                    prompt = userText,
                    modelName = modelName,
                    config = _inferenceConfig.value,
                    ragContext = ragContext,
                    pluginOutput = pluginOutput
                ).collect { chunk ->
                    if (chunk.isThinking) {
                        _currentThoughtTrace.value += chunk.text
                    } else {
                        _currentStreamingText.value += chunk.text
                    }
                    if (chunk.metrics != null) {
                        _liveMetrics.value = chunk.metrics
                    }
                    if (chunk.isComplete) {
                        // Persist complete message
                        val finalMsg = ChatMessageEntity(
                            sessionId = sessionId,
                            role = "assistant",
                            content = _currentStreamingText.value,
                            thoughtTrace = _currentThoughtTrace.value.ifBlank { null },
                            tokensGenerated = chunk.metrics?.totalTokens ?: 0,
                            tokPerSec = chunk.metrics?.tokensPerSecond ?: 0f
                        )
                        repository.insertMessage(finalMsg)

                        // Check Auto-Compile for code blocks
                        if (privacyTelemetry.value.autoCompileCode && _currentStreamingText.value.contains("```")) {
                            autoCompileExtractedCode(_currentStreamingText.value)
                        }

                        // Record in AAS Server Telemetry
                        val log = AasServerManager.recordIncomingRequest(
                            method = "POST",
                            endpoint = "/api/chat",
                            tokens = chunk.metrics?.totalTokens ?: 120,
                            latencyMs = chunk.metrics?.timeToFirstTokenMs ?: 180
                        )
                        repository.logServerRequest(log)

                        _isGenerating.value = false
                        _currentStreamingText.value = ""
                        _currentThoughtTrace.value = ""
                    }
                }
            }
        }
    }

    fun stopGeneration() {
        generationJob?.cancel()
        _isGenerating.value = false
    }

    // Auto-compilation hook
    private fun autoCompileExtractedCode(text: String) {
        val codeBlockRegex = Regex("```(\\w+)?\\n([\\s\\S]*?)```")
        val match = codeBlockRegex.find(text)
        if (match != null) {
            val rawLang = match.groupValues[1].lowercase()
            val code = match.groupValues[2]
            val lang = when (rawLang) {
                "cpp", "c++" -> "cpp"
                "c" -> "c"
                "rust", "rs" -> "rust"
                "python", "py" -> "python"
                "html", "htm" -> "html"
                "java" -> "java"
                else -> "python"
            }
            viewModelScope.launch {
                val res = MultiLangCompilerEngine.compileAndRun(lang, code)
                _codeExecutionResult.value = res
                _codeBuffer.value = code
                _selectedCodeLang.value = lang
            }
        }
    }

    // Multi-Modal Studio States
    private val _imageGenResult = MutableStateFlow<ImageGenerationResult?>(null)
    val imageGenResult: StateFlow<ImageGenerationResult?> = _imageGenResult.asStateFlow()

    private val _isImageGenerating = MutableStateFlow(false)
    val isImageGenerating: StateFlow<Boolean> = _isImageGenerating.asStateFlow()

    fun generatePhoto(prompt: String, style: String, aspect: String) {
        if (prompt.isBlank() || _isImageGenerating.value) return
        viewModelScope.launch {
            _isImageGenerating.value = true
            val res = MultiModalStudioEngine.generateImage(prompt, style, aspect)
            _imageGenResult.value = res
            _isImageGenerating.value = false
        }
    }

    private val _videoGenResult = MutableStateFlow<VideoGenerationResult?>(null)
    val videoGenResult: StateFlow<VideoGenerationResult?> = _videoGenResult.asStateFlow()

    private val _isVideoGenerating = MutableStateFlow(false)
    val isVideoGenerating: StateFlow<Boolean> = _isVideoGenerating.asStateFlow()

    fun generateVideo(script: String, durationSec: Float = 10f) {
        if (script.isBlank() || _isVideoGenerating.value) return
        viewModelScope.launch {
            _isVideoGenerating.value = true
            val res = MultiModalStudioEngine.generateVideoStoryboard(script, durationSec)
            _videoGenResult.value = res
            _isVideoGenerating.value = false
        }
    }

    private val _audioGenResult = MutableStateFlow<AudioSynthesisResult?>(null)
    val audioGenResult: StateFlow<AudioSynthesisResult?> = _audioGenResult.asStateFlow()

    private val _isAudioGenerating = MutableStateFlow(false)
    val isAudioGenerating: StateFlow<Boolean> = _isAudioGenerating.asStateFlow()

    fun synthesizeAudio(text: String, voiceId: String, speed: Float, pitch: Float) {
        if (text.isBlank() || _isAudioGenerating.value) return
        viewModelScope.launch {
            _isAudioGenerating.value = true
            val res = MultiModalStudioEngine.synthesizeAudio(text, voiceId, speed, pitch)
            _audioGenResult.value = res
            _isAudioGenerating.value = false
        }
    }

    private val _translateResult = MutableStateFlow<TranslationResult?>(null)
    val translateResult: StateFlow<TranslationResult?> = _translateResult.asStateFlow()

    private val _isTranslating = MutableStateFlow(false)
    val isTranslating: StateFlow<Boolean> = _isTranslating.asStateFlow()

    fun translate(text: String, targetLang: String, tone: String) {
        if (text.isBlank() || _isTranslating.value) return
        viewModelScope.launch {
            _isTranslating.value = true
            val res = MultiModalStudioEngine.translateText(text, targetLang, tone = tone)
            _translateResult.value = res
            _isTranslating.value = false
        }
    }

    // Model Management States
    private val _downloadingModels = MutableStateFlow<Map<String, Int>>(emptyMap())
    val downloadingModels: StateFlow<Map<String, Int>> = _downloadingModels.asStateFlow()

    fun downloadHuggingFaceModel(card: HuggingFaceModelCard, quantization: String) {
        val modelEntity = ModelManager.convertHuggingFaceToEntity(card, quantization, isDownloaded = false)
        viewModelScope.launch {
            repository.insertModel(modelEntity)
            _downloadingModels.value = _downloadingModels.value + (modelEntity.id to 0)

            ModelManager.downloadModelSimulation(card, quantization).collect { progress ->
                _downloadingModels.value = _downloadingModels.value + (modelEntity.id to progress)
                if (progress >= 100) {
                    repository.updateModel(
                        modelEntity.copy(
                            isDownloaded = true,
                            downloadProgress = 100,
                            filePath = "/data/user/0/models/${modelEntity.id}.gguf"
                        )
                    )
                    _downloadingModels.value = _downloadingModels.value - modelEntity.id
                }
            }
        }
    }

    fun importLocalModel(fileName: String, fileSizeMb: Long, filePath: String) {
        viewModelScope.launch {
            val entity = ModelManager.createLocalImportedModel(fileName, fileSizeMb, filePath)
            repository.insertModel(entity)
        }
    }

    fun loadModel(modelId: String) {
        viewModelScope.launch {
            repository.loadModelToRam(modelId)
        }
    }

    fun deleteModel(modelId: String) {
        viewModelScope.launch {
            repository.deleteModel(modelId)
        }
    }

    // Vector Database RAG States
    private val _ragSearchResults = MutableStateFlow<List<SearchResult>>(emptyList())
    val ragSearchResults: StateFlow<List<SearchResult>> = _ragSearchResults.asStateFlow()

    fun createVectorCollection(name: String, desc: String) {
        viewModelScope.launch {
            repository.createVectorCollection(name, desc)
        }
    }

    fun deleteVectorCollection(collectionId: Long) {
        viewModelScope.launch {
            repository.deleteVectorCollection(collectionId)
        }
    }

    fun addDocumentToVectorStore(collectionId: Long, title: String, content: String) {
        viewModelScope.launch {
            val rawChunks = VectorDatabaseEngine.chunkText(content)
            val chunkPairs = rawChunks.map { chunk ->
                chunk to VectorDatabaseEngine.computeEmbedding(chunk)
            }
            repository.addDocumentToCollection(collectionId, title, content, chunkPairs)
        }
    }

    fun testVectorSearch(query: String, collectionId: Long?) {
        viewModelScope.launch {
            val chunks = if (collectionId != null) {
                repository.getChunksForCollection(collectionId)
            } else {
                repository.getAllChunks()
            }
            val results = VectorDatabaseEngine.search(query, chunks, topK = 4)
            _ragSearchResults.value = results
        }
    }

    // Code Sandbox & Compiler IDE States
    private val _selectedCodeLang = MutableStateFlow("cpp")
    val selectedCodeLang: StateFlow<String> = _selectedCodeLang.asStateFlow()

    private val _codeBuffer = MutableStateFlow(MultiLangCompilerEngine.getTemplateForLanguage("cpp"))
    val codeBuffer: StateFlow<String> = _codeBuffer.asStateFlow()

    private val _codeExecutionResult = MutableStateFlow<ExecutionResult?>(null)
    val codeExecutionResult: StateFlow<ExecutionResult?> = _codeExecutionResult.asStateFlow()

    private val _isCompiling = MutableStateFlow(false)
    val isCompiling: StateFlow<Boolean> = _isCompiling.asStateFlow()

    fun setCodeLanguage(lang: String) {
        _selectedCodeLang.value = lang
        _codeBuffer.value = MultiLangCompilerEngine.getTemplateForLanguage(lang)
        _codeExecutionResult.value = null
    }

    fun updateCodeBuffer(code: String) {
        _codeBuffer.value = code
    }

    fun runCode() {
        if (_isCompiling.value) return
        viewModelScope.launch {
            _isCompiling.value = true
            val result = MultiLangCompilerEngine.compileAndRun(_selectedCodeLang.value, _codeBuffer.value)
            _codeExecutionResult.value = result
            _isCompiling.value = false
        }
    }

    // AAS Server Control
    fun toggleAasServer(enable: Boolean) {
        AasServerManager.toggleServer(enable)
    }

    fun updateServerConfig(port: Int, requireApiKey: Boolean, allowLan: Boolean) {
        AasServerManager.updateConfig(port, requireApiKey, allowLan)
    }

    fun clearServerLogs() {
        viewModelScope.launch {
            repository.clearLogs()
        }
    }

    // Privacy & Settings Actions
    fun toggleOfflineMode(enforce: Boolean) {
        PrivacyShieldManager.toggleOfflineMode(enforce)
    }

    fun toggleAutoCompile(enable: Boolean) {
        PrivacyShieldManager.toggleAutoCompile(enable)
    }

    fun setCpuThreads(threads: Int) {
        PrivacyShieldManager.setCpuThreads(threads)
    }

    fun toggleGpuVulkan(active: Boolean) {
        PrivacyShieldManager.toggleGpuVulkan(active)
    }

    // Hardware Acceleration & Silicon Execution
    fun setHardwareBackend(backend: HardwareBackend) {
        _inferenceConfig.value = _inferenceConfig.value.copy(hardwareBackend = backend)
        _hardwareRealtimeStats.value = DeviceHardwareEngine.getRealtimeStats(getApplication(), backend)
    }

    fun runHardwareBenchmark() {
        if (_benchmarkProgress.value.isRunning) return
        viewModelScope.launch {
            DeviceHardwareEngine.runHardwareBenchmark(
                context = getApplication(),
                backend = _inferenceConfig.value.hardwareBackend,
                matrixDim = 384,
                threads = _inferenceConfig.value.cpuThreads
            ).collect { progress ->
                _benchmarkProgress.value = progress
            }
        }
    }

    fun runTensorSiliconPass(dim: Int, quantization: String) {
        if (_isTensorTesting.value) return
        viewModelScope.launch {
            _isTensorTesting.value = true
            val res = DeviceHardwareEngine.executeOnDeviceTensorPass(
                dimension = dim,
                quantization = quantization,
                backend = _inferenceConfig.value.hardwareBackend
            )
            _tensorTestResult.value = res
            _isTensorTesting.value = false
        }
    }

    fun emergencyDataWipe() {
        viewModelScope.launch {
            repository.emergencyWipeAllData()
            _currentSessionId.value = null
            _currentMessages.value = emptyList()
            _imageGenResult.value = null
            _videoGenResult.value = null
            _audioGenResult.value = null
            _translateResult.value = null
            _ragSearchResults.value = null ?: emptyList()
        }
    }
}
