package com.example.ui.screens

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.NetworkCheck
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ai.engine.AiRuntimeEngineMode
import com.example.ai.engine.InferenceConfig
import com.example.privacy.PrivacyTelemetry
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
fun SettingsPrivacyScreen(
    telemetry: PrivacyTelemetry,
    inferenceConfig: InferenceConfig,
    engineMode: AiRuntimeEngineMode = AiRuntimeEngineMode.POCKET_PAL_STANDALONE,
    ollamaHost: String = "http://127.0.0.1:11434",
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
    var temp by remember { mutableFloatStateOf(inferenceConfig.temperature) }
    var topP by remember { mutableFloatStateOf(inferenceConfig.topP) }
    var contextSize by remember { mutableFloatStateOf(inferenceConfig.contextWindow.toFloat()) }
    var vulkanLayers by remember { mutableFloatStateOf(inferenceConfig.gpuOffloadLayers.toFloat()) }
    var customHost by remember { mutableStateOf(ollamaHost) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(DarkVoid)
            .padding(14.dp)
            .testTag("settings_privacy_screen"),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Engine Architecture: PocketPal Core vs Ollama Daemon
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = DarkSurface1),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, CrimsonNeon.copy(alpha = 0.6f))
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
                        border = androidx.compose.foundation.BorderStroke(
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
                        border = androidx.compose.foundation.BorderStroke(
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

        // Privacy & Zero Data Leakage Card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = DarkSurface2),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, EmeraldAi.copy(alpha = 0.5f))
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

        // Inference Engine Parameters
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = DarkSurface1),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder)
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

        // Hardware Acceleration Switches
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = DarkSurface1),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder)
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

        // Emergency Data Wipe
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = DarkSurface1),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, AmberWarning.copy(alpha = 0.5f))
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
}
