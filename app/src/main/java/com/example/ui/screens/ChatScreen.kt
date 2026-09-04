package com.example.ui.screens

import android.net.Uri
import android.provider.OpenableColumns
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Extension
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.SettingsSuggest
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.DataObject
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.FileUpload
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CheckCircle
import android.widget.Toast
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.database.AiModelEntity
import com.example.data.database.ChatMessageEntity
import com.example.data.templates.PromptTemplate
import com.example.plugins.PluginSystem
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
fun ChatScreen(
    messages: List<ChatMessageEntity>,
    models: List<AiModelEntity>,
    isGenerating: Boolean,
    streamingText: String,
    thoughtTrace: String,
    onSendMessage: (String, String, String?) -> Unit,
    onStopGeneration: () -> Unit,
    onNewChat: () -> Unit,
    onClearChat: () -> Unit,
    onImportLocalModel: (String, Long, String) -> Unit = { _, _, _ -> },
    promptTemplates: List<PromptTemplate> = emptyList(),
    activeTemplate: PromptTemplate? = null,
    onSelectTemplate: (PromptTemplate) -> Unit = {},
    onCreateTemplate: (String, String, String, Float, Float, Int, String) -> Unit = { _, _, _, _, _, _, _ -> },
    onDeleteTemplate: (String) -> Unit = {},
    onExportTemplates: () -> String = { "" },
    onImportTemplates: (String) -> Pair<Boolean, String> = { Pair(false, "") },
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var inputText by remember { mutableStateOf("") }
    var selectedModelId by remember(models) {
        mutableStateOf(models.firstOrNull { it.isLoadedInRam }?.id ?: models.firstOrNull()?.id ?: "deepseek-r1-1.5b-q4")
    }
    var selectedPluginId by remember { mutableStateOf<String?>(null) }
    var modelDropdownExpanded by remember { mutableStateOf(false) }
    var templateDropdownExpanded by remember { mutableStateOf(false) }
    var isThoughtExpanded by remember { mutableStateOf(true) }

    // In-Chat Prompt Templates (Pals) Management Dialog States
    var showTemplatesDialog by remember { mutableStateOf(false) }
    var showCreateTemplateDialog by remember { mutableStateOf(false) }
    var showExportTemplateDialog by remember { mutableStateOf(false) }
    var showImportTemplateDialog by remember { mutableStateOf(false) }
    var exportTemplateJsonString by remember { mutableStateOf("") }
    var importTemplateJsonInput by remember { mutableStateOf("") }

    var newTplName by remember { mutableStateOf("") }
    var newTplCategory by remember { mutableStateOf("Ассистент") }
    var newTplDescription by remember { mutableStateOf("") }
    var newTplSystemPrompt by remember { mutableStateOf("") }
    var newTplTemperature by remember { mutableStateOf(0.7f) }
    var newTplTopP by remember { mutableStateOf(0.9f) }
    var newTplContextSize by remember { mutableStateOf(4096) }

    val listState = rememberLazyListState()
    val clipboardManager = LocalClipboardManager.current

    // Local model file picker launcher (.gguf, .bin, .safetensors, .onnx, etc.)
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

            val cleanName = fileName.substringBeforeLast(".")
            val expectedModelId = "local-" + cleanName.lowercase().replace("[^a-z0-9]".toRegex(), "-")
            onImportLocalModel(fileName, fileSize, uri.toString())
            selectedModelId = expectedModelId
        }
    }

    LaunchedEffect(messages.size, streamingText, thoughtTrace) {
        if (messages.isNotEmpty() || streamingText.isNotEmpty()) {
            listState.animateScrollToItem(
                (messages.size + (if (isGenerating) 1 else 0)).coerceAtLeast(0)
            )
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkVoid)
            .imePadding()
            .testTag("chat_screen")
    ) {
        // Controls Row: Model Selector, File Import Button, and New Chat
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Left: Pal (Character) & Model Selectors (PocketPal Style)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                // Pal / Template Selector Pill
                if (promptTemplates.isNotEmpty()) {
                    Box {
                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(DarkSurface2)
                                .border(1.dp, PurpleNeon.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
                                .clickable { templateDropdownExpanded = true }
                                .padding(horizontal = 9.dp, vertical = 6.dp)
                                .testTag("chat_template_selector"),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("🦊", fontSize = 12.sp)
                            Spacer(modifier = Modifier.width(5.dp))
                            Text(
                                text = activeTemplate?.name?.take(12) ?: "PocketPal",
                                color = TextPrimary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1
                            )
                            Icon(
                                imageVector = Icons.Default.ExpandMore,
                                contentDescription = "Выбрать Pal",
                                tint = TextSecondary,
                                modifier = Modifier.size(14.dp)
                            )
                        }

                        DropdownMenu(
                            expanded = templateDropdownExpanded,
                            onDismissRequest = { templateDropdownExpanded = false },
                            modifier = Modifier.background(DarkSurface2)
                        ) {
                            promptTemplates.forEach { tpl ->
                                DropdownMenuItem(
                                    text = {
                                        Column {
                                            Text(
                                                text = "🦊 ${tpl.name}",
                                                color = if (tpl.id == activeTemplate?.id) PurpleNeon else TextPrimary,
                                                fontWeight = FontWeight.SemiBold,
                                                fontSize = 12.sp
                                            )
                                            Text(
                                                text = "${tpl.category} • T: ${tpl.temperature}",
                                                color = TextSecondary,
                                                fontSize = 10.sp
                                            )
                                        }
                                    },
                                    onClick = {
                                        onSelectTemplate(tpl)
                                        templateDropdownExpanded = false
                                    }
                                )
                            }

                            HorizontalDivider(color = DarkBorder, thickness = 0.5.dp)

                            DropdownMenuItem(
                                text = {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.SettingsSuggest, contentDescription = null, tint = PurpleNeon, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("Управление Pals (Шаблонами)...", color = PurpleNeon, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                    }
                                },
                                onClick = {
                                    templateDropdownExpanded = false
                                    showTemplatesDialog = true
                                }
                            )
                        }
                    }
                }

                // Model Selector Pill
                Box {
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(DarkSurface2)
                            .border(1.dp, CrimsonNeon.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
                            .clickable { modelDropdownExpanded = true }
                            .padding(horizontal = 9.dp, vertical = 6.dp)
                            .testTag("chat_model_selector"),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("📦", fontSize = 11.sp)
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(
                            text = models.find { it.id == selectedModelId }?.name?.take(13) ?: "Локальная модель",
                            color = TextPrimary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1
                        )
                        Icon(
                            imageVector = Icons.Default.ExpandMore,
                            contentDescription = "Выбрать модель",
                            tint = TextSecondary,
                            modifier = Modifier.size(14.dp)
                        )
                    }

                    DropdownMenu(
                        expanded = modelDropdownExpanded,
                        onDismissRequest = { modelDropdownExpanded = false },
                        modifier = Modifier.background(DarkSurface2)
                    ) {
                        models.forEach { model ->
                            DropdownMenuItem(
                                text = {
                                    Column {
                                        Text(
                                            text = model.name,
                                            color = if (model.id == selectedModelId) CrimsonNeon else TextPrimary,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                        Text(
                                            text = "${model.architecture} • ${model.quantization} • ${model.fileSizeMb} МБ",
                                            color = TextSecondary,
                                            fontSize = 11.sp
                                        )
                                    }
                                },
                                onClick = {
                                    selectedModelId = model.id
                                    modelDropdownExpanded = false
                                }
                            )
                        }

                        // Direct file import action in dropdown
                        DropdownMenuItem(
                            text = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.FolderOpen, contentDescription = null, tint = CrimsonNeon, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column {
                                        Text("+ Загрузить модель из файла...", color = CrimsonNeon, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                        Text("Файлы .gguf, .bin, .safetensors с памяти", color = TextSecondary, fontSize = 10.sp)
                                    }
                                }
                            },
                            onClick = {
                                modelDropdownExpanded = false
                                filePickerLauncher.launch("*/*")
                            }
                        )
                    }
                }
            }

            // Right: File Picker Shortcut & New Chat
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                // Direct file button to use neural nets from files
                Button(
                    onClick = { filePickerLauncher.launch("*/*") },
                    colors = ButtonDefaults.buttonColors(containerColor = DarkSurface2),
                    shape = RoundedCornerShape(16.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, CrimsonNeon.copy(alpha = 0.5f)),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                    modifier = Modifier.testTag("chat_file_picker_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.FolderOpen,
                        contentDescription = "Файл модели",
                        tint = CrimsonNeon,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = ".GGUF файл",
                        color = CrimsonNeon,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                IconButton(
                    onClick = onNewChat,
                    modifier = Modifier.testTag("new_chat_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Новый диалог",
                        tint = CrimsonNeon
                    )
                }
            }
        }

        // Active Plugin Ribbon
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 2.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            item {
                val isNone = selectedPluginId == null
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isNone) CrimsonNeon.copy(alpha = 0.2f) else DarkSurface2)
                        .border(1.dp, if (isNone) CrimsonNeon else DarkBorder, RoundedCornerShape(12.dp))
                        .clickable { selectedPluginId = null }
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Psychology,
                            contentDescription = null,
                            tint = if (isNone) CrimsonNeon else TextSecondary,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Базовая LLM",
                            color = if (isNone) CrimsonNeon else TextSecondary,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
            items(PluginSystem.allPlugins) { plugin ->
                val isSelected = selectedPluginId == plugin.id
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isSelected) PurpleNeon.copy(alpha = 0.25f) else DarkSurface2)
                        .border(1.dp, if (isSelected) PurpleNeon else DarkBorder, RoundedCornerShape(12.dp))
                        .clickable { selectedPluginId = if (isSelected) null else plugin.id }
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Extension,
                            contentDescription = null,
                            tint = if (isSelected) PurpleNeon else TextSecondary,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = plugin.name.take(18),
                            color = if (isSelected) PurpleNeon else TextSecondary,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }

        // Clean Chat Message List or PocketPal Welcome state
        if (messages.isEmpty() && !isGenerating) {
            PocketPalWelcomeView(
                activeModelName = models.find { it.id == selectedModelId }?.name ?: "Локальная модель",
                activePalName = activeTemplate?.name ?: "PocketPal",
                onSelectSuggestion = { suggestionText ->
                    inputText = suggestionText
                },
                modifier = Modifier.weight(1f)
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp),
                state = listState,
                contentPadding = PaddingValues(vertical = 10.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(messages) { message ->
                    MessageBubble(message = message, onCopy = { clipboardManager.setText(AnnotatedString(it)) })
                }

                if (isGenerating) {
                    item {
                        GeneratingAssistantBubble(
                            thoughtTrace = thoughtTrace,
                            streamingText = streamingText,
                            isThoughtExpanded = isThoughtExpanded,
                            onToggleThought = { isThoughtExpanded = !isThoughtExpanded }
                        )
                    }
                }
            }
        }

        // PocketPal Bottom Input Capsule
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = DarkSurface1,
            tonalElevation = 6.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Quick Pal / Templates trigger
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(DarkSurface2)
                        .border(1.dp, PurpleNeon.copy(alpha = 0.5f), CircleShape)
                        .clickable { showTemplatesDialog = true },
                    contentAlignment = Alignment.Center
                ) {
                    Text("🦊", fontSize = 18.sp)
                }

                Spacer(modifier = Modifier.width(8.dp))

                OutlinedTextField(
                    value = inputText,
                    onValueChange = { inputText = it },
                    placeholder = {
                        Text(
                            text = "Спросить PocketPal...",
                            color = TextMuted,
                            fontSize = 13.sp
                        )
                    },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("chat_text_input"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedContainerColor = DarkSurface2,
                        unfocusedContainerColor = DarkSurface2,
                        focusedBorderColor = CrimsonNeon,
                        unfocusedBorderColor = DarkBorder
                    ),
                    shape = RoundedCornerShape(24.dp),
                    maxLines = 4
                )

                Spacer(modifier = Modifier.width(8.dp))

                if (isGenerating) {
                    IconButton(
                        onClick = onStopGeneration,
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(CrimsonNeon)
                            .testTag("stop_generation_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Stop,
                            contentDescription = "Остановить генерацию",
                            tint = Color.White
                        )
                    }
                } else {
                    IconButton(
                        onClick = {
                            if (inputText.isNotBlank()) {
                                onSendMessage(inputText.trim(), selectedModelId, selectedPluginId)
                                inputText = ""
                            }
                        },
                        enabled = inputText.isNotBlank(),
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(
                                if (inputText.isNotBlank()) CrimsonNeon else DarkSurface3
                            )
                            .testTag("send_message_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Send,
                            contentDescription = "Отправить",
                            tint = if (inputText.isNotBlank()) Color.White else TextMuted
                        )
                    }
                }
            }
        }
    }

    // ==========================================
    // DIALOG 1: Main Pals & Templates Manager
    // ==========================================
    if (showTemplatesDialog) {
        AlertDialog(
            onDismissRequest = { showTemplatesDialog = false },
            containerColor = DarkSurface1,
            title = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.DataObject, contentDescription = null, tint = PurpleNeon, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "Шаблоны и Pals",
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }
                    IconButton(onClick = { showTemplatesDialog = false }) {
                        Icon(Icons.Default.Close, contentDescription = "Закрыть", tint = TextSecondary, modifier = Modifier.size(20.dp))
                    }
                }
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        "Управление персонажами, промптами и пресетами PocketPal на устройстве (работает 100% оффлайн).",
                        color = TextSecondary,
                        fontSize = 11.sp
                    )

                    // Top Action Buttons: Create, Export, Import
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Button(
                            onClick = {
                                newTplName = ""
                                newTplDescription = ""
                                newTplSystemPrompt = ""
                                newTplTemperature = 0.7f
                                newTplTopP = 0.9f
                                newTplCategory = "Ассистент"
                                showCreateTemplateDialog = true
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = CrimsonNeon),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(14.dp), tint = Color.White)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Создать", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }

                        OutlinedButton(
                            onClick = {
                                exportTemplateJsonString = onExportTemplates()
                                showExportTemplateDialog = true
                            },
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = PurpleNeon),
                            border = androidx.compose.foundation.BorderStroke(1.dp, PurpleNeon),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.FileUpload, contentDescription = null, modifier = Modifier.size(14.dp), tint = PurpleNeon)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Экспорт", color = PurpleNeon, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }

                        OutlinedButton(
                            onClick = {
                                importTemplateJsonInput = ""
                                showImportTemplateDialog = true
                            },
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = EmeraldAi),
                            border = androidx.compose.foundation.BorderStroke(1.dp, EmeraldAi),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.FileDownload, contentDescription = null, modifier = Modifier.size(14.dp), tint = EmeraldAi)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Импорт", color = EmeraldAi, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    // Template List
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(280.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        items(promptTemplates) { tpl ->
                            val isActive = activeTemplate?.id == tpl.id
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isActive) DarkSurface2 else DarkSurface3
                                ),
                                shape = RoundedCornerShape(8.dp),
                                border = androidx.compose.foundation.BorderStroke(
                                    if (isActive) 1.5.dp else 0.5.dp,
                                    if (isActive) CrimsonNeon else DarkBorder
                                )
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Text(
                                                tpl.name,
                                                color = TextPrimary,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 12.sp
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Box(
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(4.dp))
                                                    .background(DarkSurface1)
                                                    .border(0.5.dp, PurpleNeon.copy(alpha = 0.5f), RoundedCornerShape(4.dp))
                                                    .padding(horizontal = 5.dp, vertical = 1.dp)
                                            ) {
                                                Text(
                                                    tpl.category,
                                                    color = PurpleNeon,
                                                    fontSize = 8.sp,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            }
                                        }

                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            if (isActive) {
                                                Icon(
                                                    Icons.Default.CheckCircle,
                                                    contentDescription = "Активен",
                                                    tint = CrimsonNeon,
                                                    modifier = Modifier.size(16.dp)
                                                )
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text(
                                                    "Активен",
                                                    color = CrimsonNeon,
                                                    fontSize = 10.sp,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            } else {
                                                TextButton(
                                                    onClick = {
                                                        onSelectTemplate(tpl)
                                                        Toast.makeText(context, "Шаблон '${tpl.name}' активирован!", Toast.LENGTH_SHORT).show()
                                                    },
                                                    contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp)
                                                ) {
                                                    Text("Выбрать", color = CyanNeon, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                                }
                                            }

                                            if (tpl.isCustom) {
                                                IconButton(
                                                    onClick = {
                                                        onDeleteTemplate(tpl.id)
                                                        Toast.makeText(context, "Шаблон удален", Toast.LENGTH_SHORT).show()
                                                    },
                                                    modifier = Modifier.size(24.dp)
                                                ) {
                                                    Icon(
                                                        Icons.Default.DeleteOutline,
                                                        contentDescription = "Удалить шаблон",
                                                        tint = TextMuted,
                                                        modifier = Modifier.size(16.dp)
                                                    )
                                                }
                                            }
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        tpl.description,
                                        color = TextSecondary,
                                        fontSize = 10.sp,
                                        maxLines = 2
                                    )

                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = tpl.systemPrompt,
                                        color = TextMuted,
                                        fontSize = 9.sp,
                                        fontFamily = FontFamily.Monospace,
                                        maxLines = 2,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .background(DarkSurface1)
                                            .padding(6.dp)
                                    )

                                    Spacer(modifier = Modifier.height(4.dp))
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text("Temp: ${tpl.temperature} • TopP: ${tpl.topP}", color = TextMuted, fontSize = 9.sp)
                                        Text("Ctx: ${tpl.contextWindow} tok", color = TextMuted, fontSize = 9.sp)
                                    }
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { showTemplatesDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = CrimsonNeon)
                ) {
                    Text("Готово", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        )
    }

    // ==========================================
    // DIALOG 2: Create New Template / Pal
    // ==========================================
    if (showCreateTemplateDialog) {
        AlertDialog(
            onDismissRequest = { showCreateTemplateDialog = false },
            containerColor = DarkSurface1,
            title = {
                Text("Новый шаблон / Pal", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = newTplName,
                        onValueChange = { newTplName = it },
                        label = { Text("Имя персонажа / шаблона", fontSize = 11.sp) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = CrimsonNeon,
                            unfocusedBorderColor = DarkBorder,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        )
                    )

                    OutlinedTextField(
                        value = newTplCategory,
                        onValueChange = { newTplCategory = it },
                        label = { Text("Категория (напр. Код, Ролевая, Логика)", fontSize = 11.sp) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = CrimsonNeon,
                            unfocusedBorderColor = DarkBorder,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        )
                    )

                    OutlinedTextField(
                        value = newTplDescription,
                        onValueChange = { newTplDescription = it },
                        label = { Text("Краткое описание", fontSize = 11.sp) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = CrimsonNeon,
                            unfocusedBorderColor = DarkBorder,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        )
                    )

                    OutlinedTextField(
                        value = newTplSystemPrompt,
                        onValueChange = { newTplSystemPrompt = it },
                        label = { Text("Системный промпт (Инструкция)", fontSize = 11.sp) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = CrimsonNeon,
                            unfocusedBorderColor = DarkBorder,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        )
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Temperature: ${String.format("%.2f", newTplTemperature)}", color = TextSecondary, fontSize = 10.sp)
                    }
                    Slider(
                        value = newTplTemperature,
                        onValueChange = { newTplTemperature = it },
                        valueRange = 0.0f..1.5f,
                        colors = SliderDefaults.colors(
                            thumbColor = CrimsonNeon,
                            activeTrackColor = CrimsonNeon,
                            inactiveTrackColor = DarkSurface3
                        )
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newTplName.isNotBlank() && newTplSystemPrompt.isNotBlank()) {
                            onCreateTemplate(
                                newTplName.trim(),
                                newTplDescription.trim().ifBlank { "Пользовательский шаблон" },
                                newTplSystemPrompt.trim(),
                                newTplTemperature,
                                newTplTopP,
                                newTplContextSize,
                                newTplCategory.trim().ifBlank { "Пользовательские" }
                            )
                            Toast.makeText(context, "Шаблон '$newTplName' создан!", Toast.LENGTH_SHORT).show()
                            showCreateTemplateDialog = false
                        } else {
                            Toast.makeText(context, "Укажите имя и системный промпт", Toast.LENGTH_SHORT).show()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CrimsonNeon)
                ) {
                    Text("Сохранить", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showCreateTemplateDialog = false }) {
                    Text("Отмена", color = TextSecondary)
                }
            }
        )
    }

    // ==========================================
    // DIALOG 3: Export Templates JSON
    // ==========================================
    if (showExportTemplateDialog) {
        AlertDialog(
            onDismissRequest = { showExportTemplateDialog = false },
            containerColor = DarkSurface1,
            title = {
                Text("Экспорт шаблонов (JSON)", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        "Скопируйте JSON конфигурацию шаблонов для использования на других устройствах:",
                        color = TextSecondary,
                        fontSize = 11.sp
                    )

                    OutlinedTextField(
                        value = exportTemplateJsonString,
                        onValueChange = {},
                        readOnly = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PurpleNeon,
                            unfocusedBorderColor = DarkBorder,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        ),
                        textStyle = androidx.compose.ui.text.TextStyle(
                            fontFamily = FontFamily.Monospace,
                            fontSize = 9.sp
                        )
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        clipboardManager.setText(AnnotatedString(exportTemplateJsonString))
                        Toast.makeText(context, "JSON скопирован в буфер обмена!", Toast.LENGTH_SHORT).show()
                        showExportTemplateDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PurpleNeon)
                ) {
                    Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color.White)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Скопировать JSON", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showExportTemplateDialog = false }) {
                    Text("Закрыть", color = TextSecondary)
                }
            }
        )
    }

    // ==========================================
    // DIALOG 4: Import Templates JSON
    // ==========================================
    if (showImportTemplateDialog) {
        AlertDialog(
            onDismissRequest = { showImportTemplateDialog = false },
            containerColor = DarkSurface1,
            title = {
                Text("Импорт шаблонов (JSON)", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        "Вставьте JSON-структуру шаблонов PocketPal / Aether:",
                        color = TextSecondary,
                        fontSize = 11.sp
                    )

                    OutlinedTextField(
                        value = importTemplateJsonInput,
                        onValueChange = { importTemplateJsonInput = it },
                        placeholder = { Text("Вставьте JSON здесь...", fontSize = 10.sp, color = TextSecondary) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(160.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = EmeraldAi,
                            unfocusedBorderColor = DarkBorder,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        ),
                        textStyle = androidx.compose.ui.text.TextStyle(
                            fontFamily = FontFamily.Monospace,
                            fontSize = 9.sp
                        )
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (importTemplateJsonInput.isNotBlank()) {
                            val result = onImportTemplates(importTemplateJsonInput.trim())
                            Toast.makeText(context, result.second, Toast.LENGTH_LONG).show()
                            if (result.first) {
                                showImportTemplateDialog = false
                            }
                        } else {
                            Toast.makeText(context, "Вставьте JSON для импорта", Toast.LENGTH_SHORT).show()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldAi)
                ) {
                    Text("Импортировать", color = DarkVoid, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showImportTemplateDialog = false }) {
                    Text("Отмена", color = TextSecondary)
                }
            }
        )
    }
}

@Composable
fun MessageBubble(
    message: ChatMessageEntity,
    onCopy: (String) -> Unit
) {
    val isUser = message.role.equals("user", ignoreCase = true)

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = if (isUser) Alignment.End else Alignment.Start
    ) {
        if (!isUser) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 4.dp, start = 4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(20.dp)
                        .clip(CircleShape)
                        .background(CrimsonNeon.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("🦊", fontSize = 11.sp)
                }
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "PocketPal" + if (!message.language.isNullOrBlank()) " • ${message.language}" else "",
                    color = CrimsonNeon,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Surface(
            shape = RoundedCornerShape(
                topStart = 16.dp,
                topEnd = 16.dp,
                bottomStart = if (isUser) 16.dp else 4.dp,
                bottomEnd = if (isUser) 4.dp else 16.dp
            ),
            color = if (isUser) DarkSurface3 else DarkSurface1,
            border = if (!isUser) androidx.compose.foundation.BorderStroke(1.dp, DarkBorder) else null,
            modifier = Modifier.widthIn(max = 340.dp)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                // Show thinking trace if available
                if (!message.thoughtTrace.isNullOrBlank()) {
                    var showTrace by remember { mutableStateOf(false) }
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(DarkSurface2)
                            .border(1.dp, PurpleNeon.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                            .clickable { showTrace = !showTrace }
                            .padding(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "🧠 Логика рассуждений (Deep Reasoning)",
                                color = PurpleNeon,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Icon(
                                imageVector = if (showTrace) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                contentDescription = null,
                                tint = PurpleNeon,
                                modifier = Modifier.size(14.dp)
                            )
                        }

                        if (showTrace) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = message.thoughtTrace,
                                color = TextSecondary,
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                }

                // Main Message Content
                RenderFormattedMessageText(text = message.content)

                // Generation Telemetry / TPS Footer
                if (!isUser && message.tokPerSec > 0f) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(6.dp))
                            .background(DarkSurface2)
                            .padding(horizontal = 6.dp, vertical = 3.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${String.format("%.1f", message.tokPerSec)} т/сек • ${message.tokensGenerated} ток • ARM NEON/GPU",
                            color = CrimsonNeon,
                            fontSize = 9.sp,
                            fontFamily = FontFamily.Monospace
                        )

                        IconButton(
                            onClick = { onCopy(message.content) },
                            modifier = Modifier.size(18.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ContentCopy,
                                contentDescription = "Копировать",
                                tint = TextMuted,
                                modifier = Modifier.size(12.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun GeneratingAssistantBubble(
    thoughtTrace: String,
    streamingText: String,
    isThoughtExpanded: Boolean,
    onToggleThought: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.Start
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 4.dp, start = 4.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(20.dp)
                    .clip(CircleShape)
                    .background(CrimsonNeon.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Text("🦊", fontSize = 11.sp)
            }
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "PocketPal думает...",
                color = CrimsonNeon,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Surface(
            shape = RoundedCornerShape(
                topStart = 16.dp,
                topEnd = 16.dp,
                bottomStart = 4.dp,
                bottomEnd = 16.dp
            ),
            color = DarkSurface1,
            border = androidx.compose.foundation.BorderStroke(1.dp, CrimsonNeon.copy(alpha = 0.5f)),
            modifier = Modifier.widthIn(max = 340.dp)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                // Streaming Thought Trace
                if (thoughtTrace.isNotBlank()) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(DarkSurface2)
                            .border(1.dp, PurpleNeon.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                            .clickable(onClick = onToggleThought)
                            .padding(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "🧠 Мыслительный процесс DeepSeek...",
                                color = PurpleNeon,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Icon(
                                imageVector = if (isThoughtExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                contentDescription = null,
                                tint = PurpleNeon,
                                modifier = Modifier.size(14.dp)
                            )
                        }

                        if (isThoughtExpanded) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = thoughtTrace,
                                color = TextSecondary,
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                }

                // Streaming Text
                if (streamingText.isNotBlank()) {
                    RenderFormattedMessageText(text = streamingText)
                } else if (thoughtTrace.isBlank()) {
                    Text(
                        text = "Вычисление тензоров в памяти...",
                        color = TextMuted,
                        fontSize = 11.sp,
                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                    )
                }
            }
        }
    }
}

@Composable
fun RenderFormattedMessageText(text: String) {
    val clipboardManager = LocalClipboardManager.current
    val parts = text.split("```")

    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        parts.forEachIndexed { index, part ->
            if (index % 2 == 1) {
                // Code block
                val lines = part.trim().lines()
                val lang = if (lines.isNotEmpty() && !lines.first().contains(" ")) lines.first() else "code"
                val codeContent = if (lines.size > 1) lines.drop(1).joinToString("\n") else part

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = DarkSurface3),
                    shape = RoundedCornerShape(8.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder)
                ) {
                    Column(modifier = Modifier.padding(8.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Code, contentDescription = null, tint = CrimsonNeon, modifier = Modifier.size(12.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = lang.uppercase(),
                                    color = CrimsonNeon,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace
                                )
                            }

                            IconButton(
                                onClick = { clipboardManager.setText(AnnotatedString(codeContent)) },
                                modifier = Modifier.size(20.dp)
                            ) {
                                Icon(Icons.Default.ContentCopy, contentDescription = "Копировать код", tint = TextMuted, modifier = Modifier.size(12.dp))
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = codeContent,
                            color = TextPrimary,
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace,
                            lineHeight = 15.sp
                        )
                    }
                }
            } else {
                if (part.isNotBlank()) {
                    Text(
                        text = part.trim(),
                        color = TextPrimary,
                        fontSize = 13.sp,
                        lineHeight = 18.sp
                    )
                }
            }
        }
    }
}

@Composable
fun PocketPalWelcomeView(
    activeModelName: String,
    activePalName: String,
    onSelectSuggestion: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Spacer(modifier = Modifier.height(10.dp))

        // Hero glowing Fox Badge
        Box(
            modifier = Modifier
                .size(70.dp)
                .clip(RoundedCornerShape(22.dp))
                .background(
                    Brush.linearGradient(
                        listOf(CrimsonNeon, AmberWarning)
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Text("🦊", fontSize = 38.sp)
        }

        Spacer(modifier = Modifier.height(14.dp))

        Text(
            text = "PocketPal AI",
            color = TextPrimary,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "Локальный автономный ИИ. Работает прямо на процессоре устройства без интернета и серверов.",
            color = TextSecondary,
            fontSize = 12.sp,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Status chips: Active Pal & Active Model
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(PurpleNeon.copy(alpha = 0.15f))
                    .border(0.5.dp, PurpleNeon.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text("🦊 $activePalName", color = PurpleNeon, fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(CrimsonNeon.copy(alpha = 0.15f))
                    .border(0.5.dp, CrimsonNeon.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text("📦 $activeModelName", color = CrimsonNeon, fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "Быстрый старт",
            color = TextMuted,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        val suggestions = listOf(
            Triple("🧠 Глубокие рассуждения", "Объясни парадокс кота Шрёдингера с точки зрения квантовой логики.", PurpleNeon),
            Triple("💻 Разработка кода", "Напиши на Python быструю реализацию LRU Cache с объяснением алгоритма.", CyanNeon),
            Triple("⚡ Бенчмарк чипа", "Замерь скорость вывода токенов в секунду для моего процессора.", EmeraldAi),
            Triple("📝 Редактура текста", "Сделай следующий текст более убедительным и грамотным: ", AmberWarning)
        )

        suggestions.forEach { (title, prompt, color) ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .clickable { onSelectSuggestion(prompt) },
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurface1),
                border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(title, color = color, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        Text(prompt.take(65) + "...", color = TextSecondary, fontSize = 11.sp, maxLines = 1)
                    }
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Выбрать",
                        tint = color,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}
