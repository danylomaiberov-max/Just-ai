package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.DataObject
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.FileUpload
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ai.engine.AiRuntimeEngineMode
import com.example.ai.engine.InferenceConfig
import com.example.data.templates.PromptTemplate
import com.example.privacy.PrivacyTelemetry
import com.example.ui.theme.AmberWarning
import com.example.ui.theme.AppThemeMode
import com.example.ui.theme.CrimsonNeon
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkSurface1
import com.example.ui.theme.DarkSurface2
import com.example.ui.theme.DarkSurface3
import com.example.ui.theme.DarkVoid
import com.example.ui.theme.EmeraldAi
import com.example.ui.theme.PurpleNeon
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun SettingsPrivacyScreen(
    telemetry: PrivacyTelemetry,
    inferenceConfig: InferenceConfig,
    engineMode: AiRuntimeEngineMode = AiRuntimeEngineMode.POCKET_PAL_STANDALONE,
    ollamaHost: String = "http://127.0.0.1:11434",
    themeMode: AppThemeMode = AppThemeMode.CRIMSON_NEON,
    onSelectThemeMode: (AppThemeMode) -> Unit = {},
    promptTemplates: List<PromptTemplate> = emptyList(),
    activeTemplate: PromptTemplate? = null,
    onApplyTemplate: (PromptTemplate) -> Unit = {},
    onCreateTemplate: (String, String, String, Float, Float, Int, String) -> Unit = { _, _, _, _, _, _, _ -> },
    onDeleteTemplate: (String) -> Unit = {},
    onExportTemplates: () -> String = { "" },
    onImportTemplates: (String) -> Pair<Boolean, String> = { Pair(false, "") },
    onSelectEngineMode: (AiRuntimeEngineMode) -> Unit = {},
    onUpdateOllamaHost: (String) -> Unit = {},
    onToggleOfflineMode: (Boolean) -> Unit,
    onToggleAutoCompile: (Boolean) -> Unit,
    onToggleGpuVulkan: (Boolean) -> Unit,
    onSetCpuThreads: (Int) -> Unit,
    onUpdateInferenceConfig: (InferenceConfig) -> Unit,
    onEmergencyWipe: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current

    var temp by remember { mutableFloatStateOf(inferenceConfig.temperature) }
    var topP by remember { mutableFloatStateOf(inferenceConfig.topP) }
    var contextSize by remember { mutableFloatStateOf(inferenceConfig.contextWindow.toFloat()) }
    var customHost by remember { mutableStateOf(ollamaHost) }

    // Dialog States
    var showCreateDialog by remember { mutableStateOf(false) }
    var showExportDialog by remember { mutableStateOf(false) }
    var showImportDialog by remember { mutableStateOf(false) }
    var exportJsonString by remember { mutableStateOf("") }
    var importJsonInput by remember { mutableStateOf("") }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(DarkVoid)
            .padding(14.dp)
            .testTag("settings_privacy_screen"),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {

        // 1. CUSTOMIZATION & THEMES (Red Theme & Aesthetics)
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = DarkSurface1),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, CrimsonNeon.copy(alpha = 0.8f))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Palette,
                            contentDescription = null,
                            tint = CrimsonNeon,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                "КАСТОМИЗАЦИЯ И ТЕМЫ ОФОРМЛЕНИЯ",
                                color = TextPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                            Text(
                                "Переключение цветовой схемы приложения и неоновых акцентов",
                                color = TextSecondary,
                                fontSize = 10.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    AppThemeMode.entries.forEach { mode ->
                        val isSelected = themeMode == mode
                        val isRedFlagship = mode == AppThemeMode.CRIMSON_NEON || mode == AppThemeMode.CYBERPUNK_RUBY || mode == AppThemeMode.OLED_RED

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clickable { onSelectThemeMode(mode) },
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSelected) DarkSurface2 else DarkSurface3
                            ),
                            shape = RoundedCornerShape(10.dp),
                            border = BorderStroke(
                                if (isSelected) 1.5.dp else 1.dp,
                                if (isSelected) mode.accentColor else DarkBorder
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    // Accent color preview circle
                                    Box(
                                        modifier = Modifier
                                            .size(22.dp)
                                            .clip(CircleShape)
                                            .background(mode.accentColor)
                                            .border(
                                                1.dp,
                                                if (isSelected) Color.White else Color.Transparent,
                                                CircleShape
                                            )
                                    )

                                    Spacer(modifier = Modifier.width(10.dp))

                                    Column {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(
                                                mode.title,
                                                color = TextPrimary,
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                            if (isRedFlagship && mode == AppThemeMode.CRIMSON_NEON) {
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Box(
                                                    modifier = Modifier
                                                        .clip(RoundedCornerShape(4.dp))
                                                        .background(CrimsonNeon.copy(alpha = 0.25f))
                                                        .padding(horizontal = 5.dp, vertical = 2.dp)
                                                ) {
                                                    Text(
                                                        "ТУ САМАЯ КРАСНАЯ",
                                                        color = CrimsonNeon,
                                                        fontSize = 8.sp,
                                                        fontWeight = FontWeight.Bold
                                                    )
                                                }
                                            }
                                        }
                                        Text(
                                            mode.description,
                                            color = TextSecondary,
                                            fontSize = 10.sp
                                        )
                                    }
                                }

                                if (isSelected) {
                                    Icon(
                                        Icons.Default.CheckCircle,
                                        contentDescription = "Active",
                                        tint = mode.accentColor,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // 2. PROMPT & CHARACTER TEMPLATES (POCKET PAL COMPATIBLE)
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = DarkSurface1),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, PurpleNeon.copy(alpha = 0.7f))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.DataObject,
                                contentDescription = null,
                                tint = PurpleNeon,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    "ШАБЛОНЫ И ПРЕДУСТАНОВКИ (POCKET PAL)",
                                    color = TextPrimary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                                Text(
                                    "Системные инструкции, персонажи и форматы запросов",
                                    color = TextSecondary,
                                    fontSize = 10.sp
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Action buttons: Create, Export, Import
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { showCreateDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = CrimsonNeon),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp), tint = DarkVoid)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Создать", color = DarkVoid, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }

                        OutlinedButton(
                            onClick = {
                                exportJsonString = onExportTemplates()
                                showExportDialog = true
                            },
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = PurpleNeon),
                            border = BorderStroke(1.dp, PurpleNeon),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.FileUpload, contentDescription = null, modifier = Modifier.size(16.dp), tint = PurpleNeon)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Экспорт", color = PurpleNeon, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }

                        OutlinedButton(
                            onClick = {
                                importJsonInput = ""
                                showImportDialog = true
                            },
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = EmeraldAi),
                            border = BorderStroke(1.dp, EmeraldAi),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.FileDownload, contentDescription = null, modifier = Modifier.size(16.dp), tint = EmeraldAi)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Импорт", color = EmeraldAi, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Templates List
                    promptTemplates.forEach { tpl ->
                        val isActive = activeTemplate?.id == tpl.id
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isActive) DarkSurface2 else DarkSurface3
                            ),
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(
                                if (isActive) 1.dp else 0.5.dp,
                                if (isActive) CrimsonNeon else DarkBorder
                            )
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
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
                                                .background(PurpleNeon.copy(alpha = 0.2f))
                                                .padding(horizontal = 5.dp, vertical = 2.dp)
                                        ) {
                                            Text(
                                                tpl.category,
                                                color = PurpleNeon,
                                                fontSize = 8.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }

                                    if (tpl.isCustom) {
                                        IconButton(
                                            onClick = { onDeleteTemplate(tpl.id) },
                                            modifier = Modifier.size(24.dp)
                                        ) {
                                            Icon(
                                                Icons.Default.Delete,
                                                contentDescription = "Удалить",
                                                tint = AmberWarning,
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    tpl.description,
                                    color = TextSecondary,
                                    fontSize = 10.sp
                                )

                                Spacer(modifier = Modifier.height(6.dp))
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(DarkVoid)
                                        .padding(6.dp)
                                ) {
                                    Text(
                                        tpl.systemPrompt.take(120) + if (tpl.systemPrompt.length > 120) "..." else "",
                                        color = TextSecondary,
                                        fontSize = 9.sp,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }

                                Spacer(modifier = Modifier.height(6.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        "T: ${tpl.temperature} • TopP: ${tpl.topP} • Ctx: ${tpl.contextWindow}",
                                        color = CrimsonNeon,
                                        fontSize = 9.sp,
                                        fontFamily = FontFamily.Monospace
                                    )

                                    if (isActive) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(
                                                Icons.Default.Check,
                                                contentDescription = null,
                                                tint = EmeraldAi,
                                                modifier = Modifier.size(14.dp)
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("Активен в чате", color = EmeraldAi, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                        }
                                    } else {
                                        Button(
                                            onClick = { onApplyTemplate(tpl) },
                                            colors = ButtonDefaults.buttonColors(containerColor = DarkSurface1),
                                            shape = RoundedCornerShape(6.dp),
                                            modifier = Modifier.height(28.dp)
                                        ) {
                                            Text("Применить", color = CrimsonNeon, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // 3. POCKETPAL ENGINE ARCHITECTURE & OLLAMA
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = DarkSurface1),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, CrimsonNeon.copy(alpha = 0.6f))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Memory, contentDescription = null, tint = CrimsonNeon, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("ДВИЖОК ЛОКАЛЬНОГО ИНФЕРЕНСА (AI RUNTIME)", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Option 1: PocketPal Core
                    val isPocketPal = engineMode == AiRuntimeEngineMode.POCKET_PAL_STANDALONE
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSelectEngineMode(AiRuntimeEngineMode.POCKET_PAL_STANDALONE) },
                        colors = CardDefaults.cardColors(containerColor = if (isPocketPal) DarkSurface2 else DarkSurface3),
                        shape = RoundedCornerShape(10.dp),
                        border = BorderStroke(
                            1.dp,
                            if (isPocketPal) CrimsonNeon else DarkBorder
                        )
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("PocketPal Core (llama.cpp Standalone)", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(CrimsonNeon.copy(alpha = 0.2f))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text("100% НА УСТРОЙСТВЕ", color = CrimsonNeon, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                "Прямое исполнение бинарных весов .GGUF в оперативной памяти смартфона с аппаратным ускорением GPU Vulkan и ARM NEON без сети.",
                                color = TextSecondary,
                                fontSize = 10.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Option 2: Ollama Local Daemon
                    val isOllama = engineMode == AiRuntimeEngineMode.OLLAMA_DAEMON
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSelectEngineMode(AiRuntimeEngineMode.OLLAMA_DAEMON) },
                        colors = CardDefaults.cardColors(containerColor = if (isOllama) DarkSurface2 else DarkSurface3),
                        shape = RoundedCornerShape(10.dp),
                        border = BorderStroke(
                            1.dp,
                            if (isOllama) CrimsonNeon else DarkBorder
                        )
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Ollama Local Daemon / API Protocol", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(PurpleNeon.copy(alpha = 0.2f))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text("OLLAMA API", color = PurpleNeon, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                "Взаимодействие с локальным сервисом Ollama на 127.0.0.1:11434, поддержка команд ollama pull и синхронизация моделей.",
                                color = TextSecondary,
                                fontSize = 10.sp
                            )

                            if (isOllama) {
                                Spacer(modifier = Modifier.height(8.dp))
                                OutlinedTextField(
                                    value = customHost,
                                    onValueChange = {
                                        customHost = it
                                        onUpdateOllamaHost(it)
                                    },
                                    label = { Text("Хост Ollama Daemon", fontSize = 10.sp) },
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = CrimsonNeon,
                                        unfocusedBorderColor = DarkBorder,
                                        focusedTextColor = TextPrimary,
                                        unfocusedTextColor = TextPrimary
                                    ),
                                    shape = RoundedCornerShape(8.dp),
                                    singleLine = true
                                )
                            }
                        }
                    }
                }
            }
        }

        // 4. Privacy & Zero Data Leakage Card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = DarkSurface2),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, EmeraldAi.copy(alpha = 0.5f))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Security, contentDescription = null, tint = EmeraldAi, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text("Изолированный оффлайн-режим", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Text("Полная блокировка исходящего трафика и телеметрии", color = TextSecondary, fontSize = 11.sp)
                            }
                        }

                        Switch(
                            checked = telemetry.isOfflineModeEnforced,
                            onCheckedChange = onToggleOfflineMode,
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = DarkVoid,
                                checkedTrackColor = EmeraldAi
                            ),
                            modifier = Modifier.testTag("offline_shield_switch")
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(DarkSurface3)
                            .padding(10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Передано байт в сеть:", color = TextSecondary, fontSize = 10.sp)
                            Text("0.00 КБ (Нулевая утечка)", color = EmeraldAi, fontWeight = FontWeight.Bold, fontSize = 12.sp, fontFamily = FontFamily.Monospace)
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(EmeraldAi.copy(alpha = 0.2f))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text("100% БЕЗОПАСНО", color = EmeraldAi, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // 5. Inference Engine Parameters
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = DarkSurface1),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, DarkBorder)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Tune, contentDescription = null, tint = CrimsonNeon, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("ПАРАМЕТРЫ ИНФЕРЕНСА LLM", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Temperature Slider
                    Column {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Температура (Креативность):", color = TextSecondary, fontSize = 11.sp)
                            Text(String.format("%.2f", temp), color = CrimsonNeon, fontWeight = FontWeight.Bold, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                        }
                        Slider(
                            value = temp,
                            onValueChange = {
                                temp = it
                                onUpdateInferenceConfig(inferenceConfig.copy(temperature = it))
                            },
                            valueRange = 0.0f..1.5f,
                            colors = SliderDefaults.colors(thumbColor = CrimsonNeon, activeTrackColor = CrimsonNeon)
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    // Top-P Slider
                    Column {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Top-P выборка:", color = TextSecondary, fontSize = 11.sp)
                            Text(String.format("%.2f", topP), color = PurpleNeon, fontWeight = FontWeight.Bold, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                        }
                        Slider(
                            value = topP,
                            onValueChange = {
                                topP = it
                                onUpdateInferenceConfig(inferenceConfig.copy(topP = it))
                            },
                            valueRange = 0.1f..1.0f,
                            colors = SliderDefaults.colors(thumbColor = PurpleNeon, activeTrackColor = PurpleNeon)
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    // Context Size Slider
                    Column {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Размер контекстного окна:", color = TextSecondary, fontSize = 11.sp)
                            Text("${contextSize.toInt()} токенов", color = EmeraldAi, fontWeight = FontWeight.Bold, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                        }
                        Slider(
                            value = contextSize,
                            onValueChange = {
                                contextSize = it
                                onUpdateInferenceConfig(inferenceConfig.copy(contextWindow = it.toInt()))
                            },
                            valueRange = 1024f..32768f,
                            steps = 30,
                            colors = SliderDefaults.colors(thumbColor = EmeraldAi, activeTrackColor = EmeraldAi)
                        )
                    }
                }
            }
        }

        // 6. Hardware Acceleration Switches
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = DarkSurface1),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, DarkBorder)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("АППАРАТНОЕ УСКОРЕНИЕ", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(10.dp))

                    // Vulkan Switch
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Vulkan GPU ускорение", color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                            Text("Выгрузка слоев трансформера в видеопамять Adreno / Mali", color = TextSecondary, fontSize = 10.sp)
                        }
                        Switch(
                            checked = inferenceConfig.isGpuAccelerated,
                            onCheckedChange = onToggleGpuVulkan,
                            colors = SwitchDefaults.colors(checkedThumbColor = DarkVoid, checkedTrackColor = CrimsonNeon)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Auto Compile Code
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Автокомпиляция кода из чата", color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                            Text("Автоматический запуск сниппетов C++, Rust, Python в локальной IDE", color = TextSecondary, fontSize = 10.sp)
                        }
                        Switch(
                            checked = telemetry.autoCompileCode,
                            onCheckedChange = onToggleAutoCompile,
                            colors = SwitchDefaults.colors(checkedThumbColor = DarkVoid, checkedTrackColor = PurpleNeon)
                        )
                    }
                }
            }
        }

        // 7. Emergency Data Wipe
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = DarkSurface1),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, AmberWarning.copy(alpha = 0.5f))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("УПРАВЛЕНИЕ ХРАНИЛИЩЕМ", color = AmberWarning, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Очистка всех локальных сессий, векторных индексов и временных кэшей.", color = TextSecondary, fontSize = 11.sp)

                    Spacer(modifier = Modifier.height(10.dp))

                    Button(
                        onClick = onEmergencyWipe,
                        colors = ButtonDefaults.buttonColors(containerColor = AmberWarning),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.DeleteForever, contentDescription = null, tint = DarkVoid, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Очистить все локальные данные", color = DarkVoid, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }

    // DIALOG: Create Custom Template
    if (showCreateDialog) {
        var tplName by remember { mutableStateOf("") }
        var tplDesc by remember { mutableStateOf("") }
        var tplSysPrompt by remember { mutableStateOf("") }
        var tplTemp by remember { mutableFloatStateOf(0.7f) }
        var tplTopP by remember { mutableFloatStateOf(0.9f) }

        AlertDialog(
            onDismissRequest = { showCreateDialog = false },
            containerColor = DarkSurface1,
            title = {
                Text("Создать свой шаблон (PocketPal)", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = tplName,
                        onValueChange = { tplName = it },
                        label = { Text("Название шаблона", fontSize = 10.sp) },
                        placeholder = { Text("например: Python Senior Эксперт", fontSize = 10.sp) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = CrimsonNeon,
                            unfocusedBorderColor = DarkBorder,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        ),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = tplDesc,
                        onValueChange = { tplDesc = it },
                        label = { Text("Краткое описание", fontSize = 10.sp) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = CrimsonNeon,
                            unfocusedBorderColor = DarkBorder,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        ),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = tplSysPrompt,
                        onValueChange = { tplSysPrompt = it },
                        label = { Text("Системный промпт (Инструкция)", fontSize = 10.sp) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(110.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = CrimsonNeon,
                            unfocusedBorderColor = DarkBorder,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        )
                    )

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Температура: ${String.format("%.2f", tplTemp)}", color = TextSecondary, fontSize = 10.sp)
                    }
                    Slider(
                        value = tplTemp,
                        onValueChange = { tplTemp = it },
                        valueRange = 0.0f..1.5f,
                        colors = SliderDefaults.colors(thumbColor = CrimsonNeon, activeTrackColor = CrimsonNeon)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (tplName.isNotBlank() && tplSysPrompt.isNotBlank()) {
                            onCreateTemplate(
                                tplName.trim(),
                                tplDesc.ifBlank { "Пользовательский шаблон" },
                                tplSysPrompt.trim(),
                                tplTemp,
                                tplTopP,
                                4096,
                                "Кастомные"
                            )
                            Toast.makeText(context, "Шаблон «$tplName» создан и активирован!", Toast.LENGTH_SHORT).show()
                            showCreateDialog = false
                        } else {
                            Toast.makeText(context, "Заполните название и системный промпт", Toast.LENGTH_SHORT).show()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CrimsonNeon)
                ) {
                    Text("Сохранить", color = DarkVoid, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showCreateDialog = false }) {
                    Text("Отмена", color = TextSecondary)
                }
            }
        )
    }

    // DIALOG: Export Templates JSON
    if (showExportDialog) {
        AlertDialog(
            onDismissRequest = { showExportDialog = false },
            containerColor = DarkSurface1,
            title = {
                Text("Экспорт шаблонов (JSON)", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        "Скопируйте JSON конфигурацию или поделитесь шаблонами с другими устройствами:",
                        color = TextSecondary,
                        fontSize = 11.sp
                    )

                    OutlinedTextField(
                        value = exportJsonString,
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
                        clipboardManager.setText(AnnotatedString(exportJsonString))
                        Toast.makeText(context, "JSON скопирован в буфер обмена!", Toast.LENGTH_SHORT).show()
                        showExportDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PurpleNeon)
                ) {
                    Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color.White)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Скопировать JSON", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showExportDialog = false }) {
                    Text("Закрыть", color = TextSecondary)
                }
            }
        )
    }

    // DIALOG: Import Templates JSON
    if (showImportDialog) {
        AlertDialog(
            onDismissRequest = { showImportDialog = false },
            containerColor = DarkSurface1,
            title = {
                Text("Импорт шаблонов (JSON)", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        "Вставьте JSON-структуру шаблонов (совместимо с PocketPal AI & Aether):",
                        color = TextSecondary,
                        fontSize = 11.sp
                    )

                    OutlinedTextField(
                        value = importJsonInput,
                        onValueChange = { importJsonInput = it },
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
                        if (importJsonInput.isNotBlank()) {
                            val result = onImportTemplates(importJsonInput.trim())
                            Toast.makeText(context, result.second, Toast.LENGTH_LONG).show()
                            if (result.first) {
                                showImportDialog = false
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
                TextButton(onClick = { showImportDialog = false }) {
                    Text("Отмена", color = TextSecondary)
                }
            }
        )
    }
}
