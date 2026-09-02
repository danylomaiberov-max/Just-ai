package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.example.ui.AppTab
import com.example.ui.MainViewModel
import com.example.ui.components.BottomNavBar
import com.example.ui.components.TopBarAndSystemMonitor
import com.example.ui.screens.AasServerScreen
import com.example.ui.screens.ChatScreen
import com.example.ui.screens.CodeSandboxScreen
import com.example.ui.screens.HardwareRunnerScreen
import com.example.ui.screens.ModelsHubScreen
import com.example.ui.screens.MultiModalStudioScreen
import com.example.ui.screens.SettingsPrivacyScreen
import com.example.ui.screens.VectorRagScreen
import com.example.ui.theme.AetherAITheme
import com.example.ui.theme.DarkVoid

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val themeMode by viewModel.themeMode.collectAsState()
            AetherAITheme(themeMode = themeMode) {
                AetherApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun AetherApp(viewModel: MainViewModel) {
    val themeMode by viewModel.themeMode.collectAsState()
    val promptTemplates by viewModel.promptTemplates.collectAsState()
    val activeTemplate by viewModel.activeTemplate.collectAsState()

    val currentTab by viewModel.currentTab.collectAsState()
    val studioSubTab by viewModel.studioSubTab.collectAsState()

    val models by viewModel.allModels.collectAsState()
    val messages by viewModel.currentMessages.collectAsState()
    val isGenerating by viewModel.isGenerating.collectAsState()
    val streamingText by viewModel.currentStreamingText.collectAsState()
    val thoughtTrace by viewModel.currentThoughtTrace.collectAsState()
    val liveMetrics by viewModel.liveMetrics.collectAsState()
    val privacyTelemetry by viewModel.privacyTelemetry.collectAsState()
    val inferenceConfig by viewModel.inferenceConfig.collectAsState()

    val imageResult by viewModel.imageGenResult.collectAsState()
    val isImageGenerating by viewModel.isImageGenerating.collectAsState()
    val videoResult by viewModel.videoGenResult.collectAsState()
    val isVideoGenerating by viewModel.isVideoGenerating.collectAsState()
    val audioResult by viewModel.audioGenResult.collectAsState()
    val isAudioGenerating by viewModel.isAudioGenerating.collectAsState()
    val translateResult by viewModel.translateResult.collectAsState()
    val isTranslating by viewModel.isTranslating.collectAsState()

    val downloadingModels by viewModel.downloadingModels.collectAsState()
    val engineMode by viewModel.aiRuntimeEngineMode.collectAsState()
    val ollamaHost by viewModel.ollamaHost.collectAsState()
    val collections by viewModel.allCollections.collectAsState()
    val ragSearchResults by viewModel.ragSearchResults.collectAsState()

    val selectedCodeLang by viewModel.selectedCodeLang.collectAsState()
    val codeBuffer by viewModel.codeBuffer.collectAsState()
    val codeExecutionResult by viewModel.codeExecutionResult.collectAsState()
    val isCompiling by viewModel.isCompiling.collectAsState()

    val serverStatus by viewModel.serverStatus.collectAsState()
    val recentServerLogs by viewModel.recentLogs.collectAsState()

    // Hardware State Flows
    val hardwareSpecs = viewModel.hardwareSpecs
    val hardwareRealtimeStats by viewModel.hardwareRealtimeStats.collectAsState()
    val benchmarkProgress by viewModel.benchmarkProgress.collectAsState()
    val tensorTestResult by viewModel.tensorTestResult.collectAsState()
    val isTensorTesting by viewModel.isTensorTesting.collectAsState()

    val activeModel = models.firstOrNull { it.isLoadedInRam } ?: models.firstOrNull()
    val activeModelName = activeModel?.name ?: "DeepSeek-R1 (1.5B Local)"

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkVoid),
        topBar = {
            TopBarAndSystemMonitor(
                activeModelName = activeModelName,
                telemetry = privacyTelemetry,
                liveMetrics = liveMetrics,
                isGenerating = isGenerating
            )
        },
        bottomBar = {
            BottomNavBar(
                currentTab = currentTab,
                onTabSelected = { viewModel.setTab(it) }
            )
        },
        containerColor = DarkVoid
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(DarkVoid)
        ) {
            Crossfade(targetState = currentTab, label = "tab_crossfade") { tab ->
                when (tab) {
                    AppTab.CHAT -> {
                        ChatScreen(
                            messages = messages,
                            models = models,
                            isGenerating = isGenerating,
                            streamingText = streamingText,
                            thoughtTrace = thoughtTrace,
                            onSendMessage = { text, modelId, pluginId ->
                                viewModel.sendMessage(text, modelId, pluginId)
                            },
                            onStopGeneration = { viewModel.stopGeneration() },
                            onNewChat = { viewModel.startNewChat() },
                            onClearChat = { viewModel.startNewChat("Fresh Session") },
                            onImportLocalModel = { fileName, fileSize, filePath ->
                                viewModel.importLocalModel(fileName, fileSize, filePath)
                            },
                            promptTemplates = promptTemplates,
                            activeTemplate = activeTemplate,
                            onSelectTemplate = { viewModel.applyPromptTemplate(it) }
                        )
                    }

                    AppTab.HARDWARE_RUNNER -> {
                        HardwareRunnerScreen(
                            specs = hardwareSpecs,
                            realtimeStats = hardwareRealtimeStats,
                            benchmarkProgress = benchmarkProgress,
                            onSelectBackend = { backend -> viewModel.setHardwareBackend(backend) },
                            onRunBenchmark = { viewModel.runHardwareBenchmark() },
                            onRunTensorTest = { dim, quant -> viewModel.runTensorSiliconPass(dim, quant) },
                            tensorTestResult = tensorTestResult,
                            isTensorTesting = isTensorTesting,
                            cpuThreads = inferenceConfig.cpuThreads,
                            onUpdateCpuThreads = { viewModel.setCpuThreads(it) },
                            gpuLayers = inferenceConfig.gpuOffloadLayers,
                            onUpdateGpuLayers = { viewModel.updateInferenceConfig(inferenceConfig.copy(gpuOffloadLayers = it)) }
                        )
                    }

                    AppTab.STUDIO_MULTIMODAL -> {
                        MultiModalStudioScreen(
                            imageResult = imageResult,
                            isImageGenerating = isImageGenerating,
                            onGenerateImage = { prompt, style, ratio ->
                                viewModel.generatePhoto(prompt, style, ratio)
                            },
                            videoResult = videoResult,
                            isVideoGenerating = isVideoGenerating,
                            onGenerateVideo = { script, duration ->
                                viewModel.generateVideo(script, duration)
                            },
                            audioResult = audioResult,
                            isAudioGenerating = isAudioGenerating,
                            onSynthesizeAudio = { text, voice, speed, pitch ->
                                viewModel.synthesizeAudio(text, voice, speed, pitch)
                            },
                            translateResult = translateResult,
                            isTranslating = isTranslating,
                            onTranslateText = { text, target, tone ->
                                viewModel.translate(text, target, tone)
                            }
                        )
                    }

                    AppTab.MODELS_HUB -> {
                        ModelsHubScreen(
                            installedModels = models,
                            downloadingMap = downloadingModels,
                            engineMode = engineMode,
                            ollamaHost = ollamaHost,
                            onSelectEngineMode = { viewModel.setRuntimeEngineMode(it) },
                            onUpdateOllamaHost = { viewModel.setOllamaHost(it) },
                            onDownloadModel = { card, quant ->
                                viewModel.downloadHuggingFaceModel(card, quant)
                            },
                            onPullOllamaModel = { tag ->
                                viewModel.pullOllamaModel(tag)
                            },
                            onImportLocalModel = { name, size, path ->
                                viewModel.importLocalModel(name, size, path)
                            },
                            onLoadModel = { modelId ->
                                viewModel.loadModel(modelId)
                            },
                            onDeleteModel = { modelId ->
                                viewModel.deleteModel(modelId)
                            }
                        )
                    }

                    AppTab.AAS_SERVER -> {
                        AasServerScreen(
                            serverStatus = serverStatus,
                            recentLogs = recentServerLogs,
                            onToggleServer = { enable -> viewModel.toggleAasServer(enable) },
                            onClearLogs = { viewModel.clearServerLogs() }
                        )
                    }

                    AppTab.VECTOR_RAG -> {
                        VectorRagScreen(
                            collections = collections,
                            searchResults = ragSearchResults,
                            onCreateCollection = { name, desc ->
                                viewModel.createVectorCollection(name, desc)
                            },
                            onDeleteCollection = { id ->
                                viewModel.deleteVectorCollection(id)
                            },
                            onAddDocument = { colId, title, content ->
                                viewModel.addDocumentToVectorStore(colId, title, content)
                            },
                            onTestSearch = { query, colId ->
                                viewModel.testVectorSearch(query, colId)
                            }
                        )
                    }

                    AppTab.CODE_IDE -> {
                        CodeSandboxScreen(
                            selectedLanguage = selectedCodeLang,
                            codeBuffer = codeBuffer,
                            executionResult = codeExecutionResult,
                            isCompiling = isCompiling,
                            onLanguageSelected = { lang -> viewModel.setCodeLanguage(lang) },
                            onCodeChanged = { code -> viewModel.updateCodeBuffer(code) },
                            onRunCode = { viewModel.runCode() }
                        )
                    }

                    AppTab.SETTINGS_PRIVACY -> {
                        SettingsPrivacyScreen(
                            telemetry = privacyTelemetry,
                            inferenceConfig = inferenceConfig,
                            engineMode = engineMode,
                            ollamaHost = ollamaHost,
                            themeMode = themeMode,
                            onSelectThemeMode = { viewModel.setThemeMode(it) },
                            promptTemplates = promptTemplates,
                            activeTemplate = activeTemplate,
                            onApplyTemplate = { viewModel.applyPromptTemplate(it) },
                            onCreateTemplate = { name, desc, sysPrompt, temp, topP, ctx, cat ->
                                viewModel.createPromptTemplate(name, desc, sysPrompt, temp, topP, ctx, cat)
                            },
                            onDeleteTemplate = { viewModel.deletePromptTemplate(it) },
                            onExportTemplates = { viewModel.exportPromptTemplatesJson() },
                            onImportTemplates = { viewModel.importPromptTemplatesJson(it) },
                            onSelectEngineMode = { viewModel.setRuntimeEngineMode(it) },
                            onUpdateOllamaHost = { viewModel.setOllamaHost(it) },
                            onToggleOfflineMode = { enable -> viewModel.toggleOfflineMode(enable) },
                            onToggleAutoCompile = { enable -> viewModel.toggleAutoCompile(enable) },
                            onToggleGpuVulkan = { active -> viewModel.toggleGpuVulkan(active) },
                            onSetCpuThreads = { threads -> viewModel.setCpuThreads(threads) },
                            onUpdateInferenceConfig = { cfg -> viewModel.updateInferenceConfig(cfg) },
                            onEmergencyWipe = { viewModel.emergencyDataWipe() }
                        )
                    }
                }
            }
        }
    }
}
