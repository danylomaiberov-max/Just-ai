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
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var inputText by remember { mutableStateOf("") }
    var selectedModelId by remember(models) {
        mutableStateOf(models.firstOrNull { it.isLoadedInRam }?.id ?: models.firstOrNull()?.id ?: "deepseek-r1-1.5b-q4")
    }
    var selectedPluginId by remember { mutableStateOf<String?>(null) }
    var modelDropdownExpanded by remember { mutableStateOf(false) }
    var isThoughtExpanded by remember { mutableStateOf(true) }

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
            // Left: Model Selector Pill
            Box {
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(DarkSurface2)
                        .border(1.dp, CrimsonNeon.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
                        .clickable { modelDropdownExpanded = true }
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                        .testTag("chat_model_selector"),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(7.dp)
                            .clip(CircleShape)
                            .background(CrimsonNeon)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = models.find { it.id == selectedModelId }?.name ?: "Локальная модель",
                        color = TextPrimary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1
                    )
                    Icon(
                        imageVector = Icons.Default.ExpandMore,
                        contentDescription = "Выбрать модель",
                        tint = TextSecondary,
                        modifier = Modifier.size(16.dp)
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

        // Clean Chat Message List (without the large icon and prompt template cards)
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

        // Bottom Input Toolbar
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = DarkSurface1,
            tonalElevation = 6.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = inputText,
                    onValueChange = { inputText = it },
                    placeholder = {
                        Text(
                            text = "Введите запрос, задачу или код...",
                            color = TextMuted,
                            fontSize = 12.sp
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
                    shape = RoundedCornerShape(20.dp),
                    maxLines = 4
                )

                Spacer(modifier = Modifier.width(8.dp))

                if (isGenerating) {
                    IconButton(
                        onClick = onStopGeneration,
                        modifier = Modifier
                            .size(44.dp)
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
                            .size(44.dp)
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
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = null,
                    tint = CrimsonNeon,
                    modifier = Modifier.size(12.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Локальный ИИ" + if (!message.language.isNullOrBlank()) " (${message.language})" else "",
                    color = CrimsonNeon,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
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
            Icon(
                imageVector = Icons.Default.AutoAwesome,
                contentDescription = null,
                tint = CrimsonNeon,
                modifier = Modifier.size(12.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "Инференс на устройстве...",
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
