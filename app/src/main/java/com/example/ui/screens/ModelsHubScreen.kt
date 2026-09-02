package com.example.ui.screens

import android.net.Uri
import android.provider.OpenableColumns
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.NetworkCheck
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ai.engine.AiRuntimeEngineMode
import com.example.ai.engine.OllamaEngineBridge
import com.example.data.database.AiModelEntity
import com.example.models.HuggingFaceModelCard
import com.example.models.ModelDownloadProgress
import com.example.models.ModelManager
import com.example.ui.theme.AmberWarning
import com.example.ui.theme.CrimsonNeon
import com.example.ui.theme.CyanNeon
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkSurface1
import com.example.ui.theme.DarkSurface2
import com.example.ui.theme.DarkSurface3
import com.example.ui.theme.DarkVoid
import com.example.ui.theme.EmeraldAi
import com.example.ui.theme.PurpleNeon
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun ModelsHubScreen(
    installedModels: List<AiModelEntity>,
    downloadingMap: Map<String, ModelDownloadProgress>,
    engineMode: AiRuntimeEngineMode = AiRuntimeEngineMode.POCKET_PAL_STANDALONE,
    ollamaHost: String = "http://127.0.0.1:11434",
    onSelectEngineMode: (AiRuntimeEngineMode) -> Unit = {},
    onUpdateOllamaHost: (String) -> Unit = {},
    onDownloadModel: (HuggingFaceModelCard, String) -> Unit,
    onPullOllamaModel: (String) -> Unit = {},
    onImportLocalModel: (String, Long, String) -> Unit,
    onLoadModel: (String) -> Unit,
    onDeleteModel: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var selectedTab by remember { mutableIntStateOf(0) } // 0: Установленные, 1: Hugging Face, 2: Ollama Library, 3: Diffusion Фото
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("Все") }
    var showManualHfDialog by remember { mutableStateOf(false) }
    var showOllamaPullDialog by remember { mutableStateOf(false) }
    var showExplanationDialog by remember { mutableStateOf(false) }

    // System File Picker for local GGUF / SafeTensors files
    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            var fileName = "imported_model.gguf"
            var fileSize = 1450L

            try {
                context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                    val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    val sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE)
                    if (cursor.moveToFirst()) {
                        if (nameIndex != -1) fileName = cursor.getString(nameIndex)
                        if (sizeIndex != -1) {
                            val bytes = cursor.getLong(sizeIndex)
                            if (bytes > 0) fileSize = bytes / (1024 * 1024)
                        }
                    }
                }
            } catch (_: Exception) {}

            onImportLocalModel(fileName, fileSize, uri.toString())
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkVoid)
            .testTag("models_hub_screen")
    ) {
        // Architecture Notice & Explanation Strip
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 6.dp)
                .clickable { showExplanationDialog = true }
                .testTag("architecture_notice_banner"),
            colors = CardDefaults.cardColors(containerColor = DarkSurface1),
            shape = RoundedCornerShape(10.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, CrimsonNeon.copy(alpha = 0.45f))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Security,
                        contentDescription = null,
                        tint = CrimsonNeon,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "Движок: ${engineMode.title}",
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        )
                        Text(
                            text = "100% On-Device • llama.cpp & Ollama API • Без облака",
                            color = CrimsonNeon,
                            fontSize = 9.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(CrimsonNeon.copy(alpha = 0.15f))
                        .border(0.5.dp, CrimsonNeon, RoundedCornerShape(6.dp))
                        .padding(horizontal = 6.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = "О ЛОКАЛЬНОСТИ",
                        color = CrimsonNeon,
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // Top Tab Switcher
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = DarkSurface1,
            contentColor = CrimsonNeon,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                    color = CrimsonNeon
                )
            }
        ) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = { Text("Установлено (${installedModels.size})", fontWeight = FontWeight.Bold, fontSize = 10.sp) },
                icon = { Icon(Icons.Default.Storage, contentDescription = null, modifier = Modifier.size(15.dp)) },
                modifier = Modifier.testTag("tab_local_models")
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = { Text("Hugging Face", fontWeight = FontWeight.Bold, fontSize = 10.sp) },
                icon = { Icon(Icons.Default.CloudDownload, contentDescription = null, modifier = Modifier.size(15.dp)) },
                modifier = Modifier.testTag("tab_huggingface_hub")
            )
            Tab(
                selected = selectedTab == 2,
                onClick = { selectedTab = 2 },
                text = { Text("Ollama Hub", fontWeight = FontWeight.Bold, fontSize = 10.sp) },
                icon = { Icon(Icons.Default.ElectricBolt, contentDescription = null, modifier = Modifier.size(15.dp)) },
                modifier = Modifier.testTag("tab_ollama_hub")
            )
            Tab(
                selected = selectedTab == 3,
                onClick = { selectedTab = 3 },
                text = { Text("Фото SD/FLUX", fontWeight = FontWeight.Bold, fontSize = 10.sp) },
                icon = { Icon(Icons.Default.Image, contentDescription = null, modifier = Modifier.size(15.dp)) },
                modifier = Modifier.testTag("tab_photo_models")
            )
        }

        // Search & Import Actions Row
        Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Поиск моделей (Llama 3.2, DeepSeek-R1, Qwen, GGUF)...", fontSize = 11.sp, color = TextMuted) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(16.dp)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("model_search_input"),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = CrimsonNeon,
                    unfocusedBorderColor = DarkBorder,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary,
                    focusedContainerColor = DarkSurface2,
                    unfocusedContainerColor = DarkSurface2
                ),
                shape = RoundedCornerShape(10.dp),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Action buttons: Import from Phone, HuggingFace, & Ollama pull
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Button(
                    onClick = { filePickerLauncher.launch("*/*") },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("import_phone_file_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = DarkSurface2),
                    shape = RoundedCornerShape(8.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, CrimsonNeon.copy(alpha = 0.5f))
                ) {
                    Icon(Icons.Default.FolderOpen, contentDescription = null, tint = CrimsonNeon, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(".GGUF с памяти", color = CrimsonNeon, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = { showManualHfDialog = true },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("custom_hf_download_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = DarkSurface2),
                    shape = RoundedCornerShape(8.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, CrimsonNeon.copy(alpha = 0.5f))
                ) {
                    Icon(Icons.Default.CloudDownload, contentDescription = null, tint = CrimsonNeon, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("HF Репозиторий", color = CrimsonNeon, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = { showOllamaPullDialog = true },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("ollama_pull_dialog_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = DarkSurface2),
                    shape = RoundedCornerShape(8.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, CrimsonNeon.copy(alpha = 0.5f))
                ) {
                    Icon(Icons.Default.ElectricBolt, contentDescription = null, tint = CrimsonNeon, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("ollama pull", color = CrimsonNeon, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Active Global Downloads Bar if any
        if (downloadingMap.isNotEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                downloadingMap.forEach { (modelKey, progress) ->
                    DownloadLiveMetricsCard(
                        modelKey = modelKey,
                        progress = progress
                    )
                }
            }
        }

        // Main List Content
        when (selectedTab) {
            0 -> {
                // Installed Local Models
                val filteredInstalled = installedModels.filter {
                    it.name.contains(searchQuery, ignoreCase = true) ||
                            it.architecture.contains(searchQuery, ignoreCase = true)
                }

                if (filteredInstalled.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.Storage, contentDescription = null, tint = TextMuted, modifier = Modifier.size(48.dp))
                            Spacer(modifier = Modifier.height(12.dp))
                            Text("Нет сохраненных моделей", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("Скачайте модель из вкладки Hugging Face / Ollama или выберите .gguf с телефона", color = TextSecondary, fontSize = 12.sp)
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 14.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(filteredInstalled) { model ->
                            val currentDownload = downloadingMap[model.id]
                            InstalledModelCard(
                                model = model,
                                liveDownload = currentDownload,
                                onActiveClick = { onLoadModel(model.id) },
                                onDeleteClick = { onDeleteModel(model.id) }
                            )
                        }
                    }
                }
            }

            1 -> {
                // Hugging Face Catalog
                val filteredHf = ModelManager.popularHuggingFaceModels.filter {
                    val matchesQuery = it.title.contains(searchQuery, ignoreCase = true) ||
                            it.repoId.contains(searchQuery, ignoreCase = true) ||
                            it.description.contains(searchQuery, ignoreCase = true)
                    matchesQuery && (selectedCategory == "Все" || it.category == selectedCategory)
                }

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 14.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filteredHf) { card ->
                        val isInstalled = installedModels.any { it.hfRepoId == card.repoId || it.name.startsWith(card.title) }
                        val matchingDownload = downloadingMap.entries.firstOrNull {
                            it.key.contains(card.repoId.substringAfter("/")) || it.key.contains(card.title)
                        }?.value

                        HuggingFaceCardView(
                            card = card,
                            isInstalled = isInstalled,
                            downloadProgress = matchingDownload,
                            onDownload = { quant -> onDownloadModel(card, quant) }
                        )
                    }
                }
            }

            2 -> {
                // Ollama Hub / Library Tab
                val ollamaModels = OllamaEngineBridge.defaultOllamaModels.filter {
                    it.name.contains(searchQuery, ignoreCase = true) || it.family.contains(searchQuery, ignoreCase = true)
                }

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    item {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = DarkSurface2),
                            shape = RoundedCornerShape(12.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, CrimsonNeon.copy(alpha = 0.5f))
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.ElectricBolt, contentDescription = null, tint = CrimsonNeon, modifier = Modifier.size(20.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Ollama Registry & Local API Daemon", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    "Интеграция с официальным протоколом Ollama. Загружайте модели по коротким тегам и запускайте их локально.",
                                    color = TextSecondary,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }

                    items(ollamaModels) { item ->
                        val isInstalled = installedModels.any { it.name.contains(item.name, true) }
                        val cleanId = "ollama-" + item.name.replace(":", "-").replace("/", "-")
                        val matchingDownload = downloadingMap[cleanId]

                        OllamaModelCardView(
                            tag = item,
                            isInstalled = isInstalled,
                            downloadProgress = matchingDownload,
                            onPull = { onPullOllamaModel(item.name) }
                        )
                    }
                }
            }

            3 -> {
                // Photo Generation (Diffusion) Models
                val diffusionModels = ModelManager.popularHuggingFaceModels.filter { it.category == "Diffusion" }

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 14.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = DarkSurface2),
                            shape = RoundedCornerShape(12.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, CrimsonNeon.copy(alpha = 0.5f))
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = CrimsonNeon, modifier = Modifier.size(24.dp))
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text("Локальные диффузионные нейросети", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    Text("Генерация фото, артов и графики прямо на Vulkan GPU смартфона без интернета", color = TextSecondary, fontSize = 11.sp)
                                }
                            }
                        }
                    }

                    items(diffusionModels) { card ->
                        val isInstalled = installedModels.any { it.hfRepoId == card.repoId || it.name.startsWith(card.title) }
                        val matchingDownload = downloadingMap.entries.firstOrNull {
                            it.key.contains(card.repoId.substringAfter("/")) || it.key.contains(card.title)
                        }?.value

                        HuggingFaceCardView(
                            card = card,
                            isInstalled = isInstalled,
                            downloadProgress = matchingDownload,
                            onDownload = { quant -> onDownloadModel(card, quant) }
                        )
                    }
                }
            }
        }
    }

    // Explanation Dialog: Is this really a local LLM? (PocketPal vs Ollama architecture)
    if (showExplanationDialog) {
        AlertDialog(
            onDismissRequest = { showExplanationDialog = false },
            containerColor = DarkSurface2,
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Security, contentDescription = null, tint = CrimsonNeon, modifier = Modifier.size(22.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Это действительно локальная LLM?", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                }
            },
            text = {
                Column(
                    modifier = Modifier.padding(vertical = 4.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "ДА, приложение выполняет вычисления 100% локально на аппаратных ресурсах смартфона!",
                        color = CrimsonNeon,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )

                    Text(
                        text = "Архитектура построена на двух проверенных стандартах:\n\n" +
                                "1. PocketPal Core (llama.cpp Standalone):\n" +
                                "Бинарные веса моделей в формате .GGUF загружаются прямо в оперативную память (RAM) вашего устройства. Матричные вычисления выполняются ядром llama.cpp с аппаратным ускорением ARM NEON, GPU Vulkan и NPU без единого сетевого запроса.\n\n" +
                                "2. Ollama Local Engine:\n" +
                                "Поддерживается протокол демона Ollama для бесшовного импорта моделей из реестра (ollama pull) и работы с локальным API 127.0.0.1:11434.\n\n" +
                                "Конфиденциальность: все ваши диалоги, фото и код не покидают телефон.",
                        color = TextSecondary,
                        fontSize = 11.sp,
                        lineHeight = 16.sp
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = { showExplanationDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = CrimsonNeon),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Понятно", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        )
    }

    // Manual Hugging Face Repo Download Dialog
    if (showManualHfDialog) {
        var customRepoInput by remember { mutableStateOf("") }
        var customQuant by remember { mutableStateOf("Q4_K_M") }

        AlertDialog(
            onDismissRequest = { showManualHfDialog = false },
            containerColor = DarkSurface2,
            title = {
                Text("Загрузка с Hugging Face", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 15.sp)
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        "Введите название репозитория модели (например: TheBloke/Mistral-7B-Instruct-v0.2-GGUF или deepseek-ai/DeepSeek-R1-Distill-Qwen-1.5B-GGUF):",
                        color = TextSecondary,
                        fontSize = 11.sp
                    )

                    OutlinedTextField(
                        value = customRepoInput,
                        onValueChange = { customRepoInput = it },
                        placeholder = { Text("author/model-name-gguf", color = TextMuted, fontSize = 12.sp) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = CrimsonNeon,
                            unfocusedBorderColor = DarkBorder,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            focusedContainerColor = DarkSurface3,
                            unfocusedContainerColor = DarkSurface3
                        ),
                        shape = RoundedCornerShape(8.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Квантование:", color = TextSecondary, fontSize = 11.sp)
                        val quants = listOf("Q4_K_M", "Q5_K_M", "Q8_0", "FP16")
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            quants.forEach { q ->
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(if (customQuant == q) CrimsonNeon else DarkSurface3)
                                        .clickable { customQuant = q }
                                        .padding(horizontal = 6.dp, vertical = 3.dp)
                                ) {
                                    Text(
                                        text = q,
                                        color = if (customQuant == q) Color.White else TextSecondary,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (customRepoInput.isNotBlank()) {
                            val card = HuggingFaceModelCard(
                                repoId = customRepoInput.trim(),
                                title = customRepoInput.substringAfterLast("/"),
                                author = customRepoInput.substringBefore("/", "huggingface"),
                                category = if (customRepoInput.contains("sd", true) || customRepoInput.contains("flux", true)) "Diffusion" else "LLM",
                                downloads = "100K+",
                                likes = 1200,
                                availableQuantizations = listOf(customQuant),
                                defaultSizeMb = 1800,
                                description = "Пользовательская модель из Hugging Face: $customRepoInput"
                            )
                            onDownloadModel(card, customQuant)
                            showManualHfDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CrimsonNeon),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Начать загрузку", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showManualHfDialog = false }) {
                    Text("Отмена", color = TextSecondary)
                }
            }
        )
    }

    // Ollama Pull Command Dialog
    if (showOllamaPullDialog) {
        var customTagInput by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showOllamaPullDialog = false },
            containerColor = DarkSurface2,
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.ElectricBolt, contentDescription = null, tint = CrimsonNeon, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Команда: ollama pull", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        "Введите тег модели из библиотеки Ollama (например: llama3.2:3b, deepseek-r1:1.5b, mistral:7b, qwen2.5-coder:1.5b):",
                        color = TextSecondary,
                        fontSize = 11.sp
                    )

                    OutlinedTextField(
                        value = customTagInput,
                        onValueChange = { customTagInput = it },
                        placeholder = { Text("llama3.2:3b", color = TextMuted, fontSize = 12.sp) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = CrimsonNeon,
                            unfocusedBorderColor = DarkBorder,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            focusedContainerColor = DarkSurface3,
                            unfocusedContainerColor = DarkSurface3
                        ),
                        shape = RoundedCornerShape(8.dp)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (customTagInput.isNotBlank()) {
                            onPullOllamaModel(customTagInput.trim())
                            showOllamaPullDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CrimsonNeon),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Загрузить через Ollama", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showOllamaPullDialog = false }) {
                    Text("Отмена", color = TextSecondary)
                }
            }
        )
    }
}

@Composable
fun DownloadLiveMetricsCard(
    modelKey: String,
    progress: ModelDownloadProgress
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = DarkSurface2),
        shape = RoundedCornerShape(10.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, CrimsonNeon.copy(alpha = 0.8f))
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            // Header Row: Model Title & Percentage
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.CloudDownload,
                        contentDescription = null,
                        tint = CrimsonNeon,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = progress.modelId.ifBlank { modelKey },
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        maxLines = 1
                    )
                }

                Text(
                    text = "${progress.progressPercent}%",
                    color = CrimsonNeon,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    fontFamily = FontFamily.Monospace
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Animated Progress Bar
            LinearProgressIndicator(
                progress = { progress.progressPercent / 100f },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(CircleShape),
                color = CrimsonNeon,
                trackColor = DarkSurface3
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Telemetry Grid: Скорость записи, Общий объем, Сколько записано, Примерное время
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("Скорость записи:", color = TextMuted, fontSize = 9.sp)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Speed, contentDescription = null, tint = CrimsonNeon, modifier = Modifier.size(11.dp))
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = "${progress.writeSpeedMbps} МБ/с",
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }

                Column {
                    Text("Записано / Объем:", color = TextMuted, fontSize = 9.sp)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Storage, contentDescription = null, tint = CrimsonNeon, modifier = Modifier.size(11.dp))
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = "${progress.downloadedMb} / ${progress.totalSizeMb} МБ",
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }

                Column {
                    Text("Осталось времени:", color = TextMuted, fontSize = 9.sp)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Timer, contentDescription = null, tint = CrimsonNeon, modifier = Modifier.size(11.dp))
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = if (progress.etaSeconds > 0) "~${progress.etaSeconds} сек" else "Финал...",
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Current Phase
            Text(
                text = progress.statusPhase,
                color = TextSecondary,
                fontSize = 10.sp,
                fontFamily = FontFamily.Monospace
            )
        }
    }
}

@Composable
fun InstalledModelCard(
    model: AiModelEntity,
    liveDownload: ModelDownloadProgress?,
    onActiveClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = DarkSurface1),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (model.isLoadedInRam) CrimsonNeon else DarkBorder
        )
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(if (model.isLoadedInRam) CrimsonNeon else EmeraldAi)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = model.name,
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }

                if (model.isLoadedInRam) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(CrimsonNeon.copy(alpha = 0.2f))
                            .border(0.5.dp, CrimsonNeon, RoundedCornerShape(4.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "АКТИВНА В RAM",
                            color = CrimsonNeon,
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = model.description,
                color = TextSecondary,
                fontSize = 11.sp,
                maxLines = 2
            )

            if (liveDownload != null && !liveDownload.isComplete) {
                Spacer(modifier = Modifier.height(8.dp))
                DownloadLiveMetricsCard(modelKey = model.id, progress = liveDownload)
            } else {
                Spacer(modifier = Modifier.height(8.dp))

                // Specs Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${model.architecture} • ${model.quantization} • ${model.fileSizeMb} МБ",
                        color = TextMuted,
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        if (!model.isLoadedInRam) {
                            OutlinedButton(
                                onClick = onActiveClick,
                                shape = RoundedCornerShape(8.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, CrimsonNeon.copy(alpha = 0.6f)),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = CrimsonNeon)
                            ) {
                                Text("Загрузить в RAM", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        IconButton(
                            onClick = onDeleteClick,
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(Icons.Default.Delete, contentDescription = "Удалить", tint = TextMuted, modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun HuggingFaceCardView(
    card: HuggingFaceModelCard,
    isInstalled: Boolean,
    downloadProgress: ModelDownloadProgress?,
    onDownload: (String) -> Unit
) {
    var selectedQuant by remember { mutableStateOf(card.availableQuantizations.firstOrNull() ?: "Q4_K_M") }
    var quantDropdownExpanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = DarkSurface1),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = card.title,
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                    Text(
                        text = card.repoId,
                        color = CrimsonNeon,
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(DarkSurface3)
                        .padding(horizontal = 6.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = "${card.downloads} скачиваний",
                        color = TextMuted,
                        fontSize = 9.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = card.description,
                color = TextSecondary,
                fontSize = 11.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Download telemetry or Action button
            if (downloadProgress != null && !downloadProgress.isComplete) {
                DownloadLiveMetricsCard(modelKey = card.title, progress = downloadProgress)
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Quantization Selector
                    Box {
                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(DarkSurface3)
                                .clickable { quantDropdownExpanded = true }
                                .padding(horizontal = 8.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Квант: $selectedQuant",
                                color = TextPrimary,
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        }

                        DropdownMenu(
                            expanded = quantDropdownExpanded,
                            onDismissRequest = { quantDropdownExpanded = false },
                            modifier = Modifier.background(DarkSurface2)
                        ) {
                            card.availableQuantizations.forEach { q ->
                                DropdownMenuItem(
                                    text = { Text(q, color = TextPrimary, fontSize = 12.sp) },
                                    onClick = {
                                        selectedQuant = q
                                        quantDropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    // Download Action Button
                    if (isInstalled) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = EmeraldAi, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Установлено", color = EmeraldAi, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    } else {
                        Button(
                            onClick = { onDownload(selectedQuant) },
                            colors = ButtonDefaults.buttonColors(containerColor = CrimsonNeon),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(Icons.Default.CloudDownload, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Скачать (${card.defaultSizeMb} МБ)", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun OllamaModelCardView(
    tag: com.example.ai.engine.OllamaModelTag,
    isInstalled: Boolean,
    downloadProgress: ModelDownloadProgress?,
    onPull: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = DarkSurface1),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = tag.name,
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                    Text(
                        text = "Семейство: ${tag.family} • Квантование: ${tag.quantizationLevel}",
                        color = CrimsonNeon,
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(DarkSurface3)
                        .padding(horizontal = 6.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = "${tag.sizeMb} МБ",
                        color = TextPrimary,
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            if (downloadProgress != null && !downloadProgress.isComplete) {
                DownloadLiveMetricsCard(modelKey = tag.name, progress = downloadProgress)
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Хэш: ${tag.digest}",
                        color = TextMuted,
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace
                    )

                    if (isInstalled) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = EmeraldAi, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("В реестре", color = EmeraldAi, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    } else {
                        Button(
                            onClick = onPull,
                            colors = ButtonDefaults.buttonColors(containerColor = CrimsonNeon),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(Icons.Default.ElectricBolt, contentDescription = null, tint = Color.White, modifier = Modifier.size(15.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("ollama pull", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}
