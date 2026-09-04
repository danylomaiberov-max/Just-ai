package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Dns
import androidx.compose.material.icons.filled.FindInPage
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.AppTab
import com.example.ui.MainViewModel
import com.example.ui.components.BottomNavBar
import com.example.ui.components.ExtraFunctionsBottomSheet
import com.example.ui.components.TopBarAndSystemMonitor
import com.example.ui.screens.AasServerScreen
import com.example.ui.screens.ChatScreen
import com.example.ui.screens.CodeSandboxScreen
import com.example.ui.screens.HardwareRunnerScreen
import com.example.ui.screens.ModelsHubScreen
import com.example.ui.screens.MultiModalStudioScreen
import com.example.ui.screens.PalsScreen
import com.example.ui.screens.SettingsPrivacyScreen
import com.example.ui.screens.VectorRagScreen
import com.example.ui.theme.AetherAITheme
import com.example.ui.theme.CrimsonNeon
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkSurface1
import com.example.ui.theme.DarkVoid
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

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

@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
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

    var showToolsSheet by remember { mutableStateOf(false) }

    val loadedModel = models.firstOrNull { it.isLoadedInRam }
    val activeModelName = loadedModel?.name ?: if (models.any { it.isDownloaded }) "Модель не в ОЗУ" else "Модель не скачана"

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkVoid),
        topBar = {
            TopBarAndSystemMonitor(
                activeModelName = activeModelName,
                telemetry = privacyTelemetry,
                liveMetrics = liveMetrics,
                isGenerating = isGenerating,
                onOpenTools = { showToolsSheet = true }
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
        val isToolsActive = currentTab in listOf(
            AppTab.STUDIO_MULTIMODAL,
            AppTab.CODE_IDE,
            AppTab.VECTOR_RAG,
            AppTab.AAS_SERVER
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(DarkVoid)
        ) {
            if (isToolsActive) {
                ToolsSubNavBar(
                    currentTab = currentTab,
                    onSelectTab = { viewModel.setTab(it) }
                )
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
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
                            onSelectTemplate = { viewModel.applyPromptTemplate(it) },
                            onCreateTemplate = { name, desc, sysPrompt, temp, topP, ctx, cat ->
                                viewModel.createPromptTemplate(name, desc, sysPrompt, temp, topP, ctx, cat)
                            },
                            onDeleteTemplate = { viewModel.deletePromptTemplate(it) },
                            onExportTemplates = { viewModel.exportPromptTemplatesJson() },
                            onImportTemplates = { viewModel.importPromptTemplatesJson(it) },
                            onOpenToolsSheet = { showToolsSheet = true },
                            onNavigateToModels = { viewModel.setTab(AppTab.MODELS_HUB) }
                        )
                    }

                    AppTab.PALS -> {
                        PalsScreen(
                            templates = promptTemplates,
                            activeTemplate = activeTemplate,
                            onSelectPal = { viewModel.applyPromptTemplate(it) },
                            onCreatePal = { name, desc, sysPrompt, temp, topP, ctx, cat ->
                                viewModel.createPromptTemplate(name, desc, sysPrompt, temp, topP, ctx, cat)
                            },
                            onDeletePal = { viewModel.deletePromptTemplate(it) },
                            onOpenChatWithPal = { pal ->
                                viewModel.applyPromptTemplate(pal)
                                viewModel.setTab(AppTab.CHAT)
                            },
                            onNavigateToTab = { targetTab ->
                                viewModel.setTab(targetTab)
                            },
                            onOpenToolsSheet = { showToolsSheet = true }
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
                            onEmergencyWipe = { viewModel.emergencyDataWipe() },
                            onNavigateToTab = { viewModel.setTab(it) }
                        )
                    }
                }
            }
        }
    }

    if (showToolsSheet) {
        ExtraFunctionsBottomSheet(
            onDismissRequest = { showToolsSheet = false },
            onNavigateToTab = { targetTab ->
                showToolsSheet = false
                viewModel.setTab(targetTab)
            }
        )
    }
}
}

@Composable
fun ToolsSubNavBar(
    currentTab: AppTab,
    onSelectTab: (AppTab) -> Unit,
    modifier: Modifier = Modifier
) {
    val toolTabs = listOf(
        Triple(AppTab.STUDIO_MULTIMODAL, "Студия", Icons.Filled.AutoAwesome),
        Triple(AppTab.CODE_IDE, "Код IDE", Icons.Filled.Code),
        Triple(AppTab.VECTOR_RAG, "RAG Память", Icons.Filled.FindInPage),
        Triple(AppTab.AAS_SERVER, "API Сервер", Icons.Filled.Dns)
    )

    ScrollableTabRow(
        selectedTabIndex = toolTabs.indexOfFirst { it.first == currentTab }.coerceAtLeast(0),
        containerColor = DarkSurface1,
        contentColor = CrimsonNeon,
        edgePadding = 12.dp,
        indicator = { tabPositions ->
            val idx = toolTabs.indexOfFirst { it.first == currentTab }.coerceAtLeast(0)
            if (idx < tabPositions.size) {
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[idx]),
                    color = CrimsonNeon
                )
            }
        },
        divider = {
            HorizontalDivider(color = DarkBorder, thickness = 0.5.dp)
        },
        modifier = modifier.fillMaxWidth()
    ) {
        toolTabs.forEach { (tab, label, icon) ->
            val isSelected = currentTab == tab
            Tab(
                selected = isSelected,
                onClick = { onSelectTab(tab) },
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = icon,
                            contentDescription = label,
                            tint = if (isSelected) CrimsonNeon else TextMuted,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = label,
                            color = if (isSelected) TextPrimary else TextSecondary,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 11.sp
                        )
                    }
                }
            )
        }
    }
}

