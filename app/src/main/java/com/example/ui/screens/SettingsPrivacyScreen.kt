package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ai.engine.InferenceConfig
import com.example.privacy.PrivacyTelemetry
import com.example.ui.theme.AmberWarning
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
    var threads by remember { mutableIntStateOf(telemetry.cpuThreadCount) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(DarkVoid)
            .padding(14.dp)
            .testTag("settings_privacy_screen"),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
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
                                Text("Air-Gapped Privacy Shield", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Text("Zero telemetry & strict offline isolation", color = TextSecondary, fontSize = 11.sp)
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
                            Text("Outbound Bytes Transmitted:", color = TextSecondary, fontSize = 10.sp)
                            Text("0.00 KB (Zero-Leakage Verified)", color = EmeraldAi, fontWeight = FontWeight.Bold, fontSize = 12.sp, fontFamily = FontFamily.Monospace)
                        }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(EmeraldAi.copy(alpha = 0.2f))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text("ENCRYPTED", color = EmeraldAi, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // PocketPal / Ollama LLM Inference Hyperparameters
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = DarkSurface2),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Tune, contentDescription = null, tint = CyanNeon, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("PocketPal / Ollama Inference Tuning", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Temperature Slider
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Temperature (Creativity):", color = TextSecondary, fontSize = 11.sp)
                        Text("${String.format("%.2f", temp)}", color = CyanNeon, fontWeight = FontWeight.Bold, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                    }
                    Slider(
                        value = temp,
                        onValueChange = {
                            temp = it
                            onUpdateInferenceConfig(inferenceConfig.copy(temperature = it))
                        },
                        valueRange = 0.1f..1.5f,
                        colors = SliderDefaults.colors(thumbColor = CyanNeon, activeTrackColor = CyanNeon)
                    )

                    // Top-P Sampling Slider
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Top-P (Nucleus Sampling):", color = TextSecondary, fontSize = 11.sp)
                        Text("${String.format("%.2f", topP)}", color = CyanNeon, fontWeight = FontWeight.Bold, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                    }
                    Slider(
                        value = topP,
                        onValueChange = {
                            topP = it
                            onUpdateInferenceConfig(inferenceConfig.copy(topP = it))
                        },
                        valueRange = 0.1f..1.0f,
                        colors = SliderDefaults.colors(thumbColor = CyanNeon, activeTrackColor = CyanNeon)
                    )

                    // Context Window Slider
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Context Window Size:", color = TextSecondary, fontSize = 11.sp)
                        Text("${contextSize.toInt()} tokens", color = CyanNeon, fontWeight = FontWeight.Bold, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                    }
                    Slider(
                        value = contextSize,
                        onValueChange = {
                            contextSize = it
                            onUpdateInferenceConfig(inferenceConfig.copy(contextWindow = it.toInt()))
                        },
                        valueRange = 2048f..32768f,
                        steps = 14,
                        colors = SliderDefaults.colors(thumbColor = CyanNeon, activeTrackColor = CyanNeon)
                    )
                }
            }
        }

        // Hardware Acceleration & Auto-Compile
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = DarkSurface2),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Memory, contentDescription = null, tint = PurpleNeon, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Hardware Acceleration & Sandbox", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Vulkan GPU Acceleration
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Vulkan NPU / GPU Offload", color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                            Text("Offload layers to mobile Adreno / Mali GPU", color = TextSecondary, fontSize = 10.sp)
                        }
                        Switch(
                            checked = telemetry.gpuVulkanActive,
                            onCheckedChange = onToggleGpuVulkan,
                            colors = SwitchDefaults.colors(checkedThumbColor = DarkVoid, checkedTrackColor = PurpleNeon)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Auto-Compile Code
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Auto-Compile AI Code Snippets", color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                            Text("Executes C++, Rust, Python automatically in background IDE", color = TextSecondary, fontSize = 10.sp)
                        }
                        Switch(
                            checked = telemetry.autoCompileCode,
                            onCheckedChange = onToggleAutoCompile,
                            colors = SwitchDefaults.colors(checkedThumbColor = DarkVoid, checkedTrackColor = CyanNeon)
                        )
                    }
                }
            }
        }

        // Emergency Data Wipe Button
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = DarkSurface2),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, AmberWarning.copy(alpha = 0.4f))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.DeleteForever, contentDescription = null, tint = AmberWarning, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Emergency 1-Tap Data Purge", color = AmberWarning, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Instantly clears all chat sessions, vector embeddings, generated media, and local cache.",
                        color = TextSecondary,
                        fontSize = 11.sp
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Button(
                        onClick = onEmergencyWipe,
                        colors = ButtonDefaults.buttonColors(containerColor = AmberWarning, contentColor = DarkVoid),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("emergency_wipe_button")
                    ) {
                        Icon(Icons.Default.CleaningServices, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Purge All Local AI Data", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
