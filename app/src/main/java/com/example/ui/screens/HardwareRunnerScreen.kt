package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DeveloperBoard
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.hardware.HardwareBackend
import com.example.hardware.HardwareRealtimeStats
import com.example.hardware.HardwareSpecs
import com.example.hardware.HardwareStressTestProgress
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
fun HardwareRunnerScreen(
    specs: HardwareSpecs,
    realtimeStats: HardwareRealtimeStats,
    benchmarkProgress: HardwareStressTestProgress,
    onSelectBackend: (HardwareBackend) -> Unit,
    onRunBenchmark: () -> Unit,
    onRunTensorTest: (Int, String) -> Unit,
    tensorTestResult: Pair<Long, Float>?,
    isTensorTesting: Boolean,
    cpuThreads: Int,
    onUpdateCpuThreads: (Int) -> Unit,
    gpuLayers: Int,
    onUpdateGpuLayers: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(DarkVoid)
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .testTag("screen_hardware_runner"),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Hero Hardware Header
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Brush.horizontalGradient(listOf(CyanNeon, PurpleNeon)), RoundedCornerShape(16.dp)),
                colors = CardDefaults.cardColors(containerColor = DarkSurface1),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(CyanNeon.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.DeveloperBoard, contentDescription = null, tint = CyanNeon, modifier = Modifier.size(22.dp))
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text("Аппаратный ускоритель", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                Text("${specs.socManufacturer} ${specs.socModel}", color = TextSecondary, fontSize = 12.sp, fontFamily = FontFamily.Monospace)
                            }
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(DarkSurface2)
                                .border(1.dp, EmeraldAi.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(specs.cpuArch, color = EmeraldAi, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(DarkSurface3)
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("Ядра CPU", color = TextMuted, fontSize = 10.sp)
                            Text("${specs.totalCores} Cores", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                        Column {
                            Text("GPU & Версия", color = TextMuted, fontSize = 10.sp)
                            Text(specs.gpuRenderer.take(16), color = CyanNeon, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                        Column {
                            Text("Оперативная память", color = TextMuted, fontSize = 10.sp)
                            Text("${specs.totalRamMb / 1024} GB RAM", color = PurpleNeon, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                }
            }
        }

        // Live Telemetry Gauges
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = DarkSurface1),
                shape = RoundedCornerShape(14.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("МОНИТОРИНГ В РЕАЛЬНОМ ВРЕМЕНИ", color = CyanNeon, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        // CPU Usage Box
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(DarkSurface2)
                                .padding(10.dp)
                        ) {
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Speed, contentDescription = null, tint = CyanNeon, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Загрузка CPU", color = TextSecondary, fontSize = 10.sp)
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Text("${realtimeStats.cpuUsagePercent.toInt()}%", color = CyanNeon, fontWeight = FontWeight.Bold, fontSize = 16.sp, fontFamily = FontFamily.Monospace)
                                Spacer(modifier = Modifier.height(4.dp))
                                LinearProgressIndicator(
                                    progress = { realtimeStats.cpuUsagePercent / 100f },
                                    modifier = Modifier.fillMaxWidth().height(4.dp).clip(CircleShape),
                                    color = CyanNeon,
                                    trackColor = DarkSurface3
                                )
                            }
                        }

                        // RAM Usage Box
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(DarkSurface2)
                                .padding(10.dp)
                        ) {
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Memory, contentDescription = null, tint = PurpleNeon, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Память RAM", color = TextSecondary, fontSize = 10.sp)
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Text("${realtimeStats.ramUsedMb} МБ", color = PurpleNeon, fontWeight = FontWeight.Bold, fontSize = 16.sp, fontFamily = FontFamily.Monospace)
                                Spacer(modifier = Modifier.height(4.dp))
                                LinearProgressIndicator(
                                    progress = { (realtimeStats.ramUsedMb.toFloat() / specs.totalRamMb).coerceIn(0f, 1f) },
                                    modifier = Modifier.fillMaxWidth().height(4.dp).clip(CircleShape),
                                    color = PurpleNeon,
                                    trackColor = DarkSurface3
                                )
                            }
                        }

                        // Temperature Box
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(DarkSurface2)
                                .padding(10.dp)
                        ) {
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Thermostat, contentDescription = null, tint = EmeraldAi, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Температура", color = TextSecondary, fontSize = 10.sp)
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Text("${realtimeStats.batteryTempCelsius.toInt()}°C", color = EmeraldAi, fontWeight = FontWeight.Bold, fontSize = 16.sp, fontFamily = FontFamily.Monospace)
                                Spacer(modifier = Modifier.height(4.dp))
                                LinearProgressIndicator(
                                    progress = { (realtimeStats.batteryTempCelsius / 80f).coerceIn(0f, 1f) },
                                    modifier = Modifier.fillMaxWidth().height(4.dp).clip(CircleShape),
                                    color = EmeraldAi,
                                    trackColor = DarkSurface3
                                )
                            }
                        }
                    }
                }
            }
        }

        // Hardware Compute Backends Selection
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = DarkSurface1),
                shape = RoundedCornerShape(14.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("ВЫБОР ВЫЧИСЛИТЕЛЬНОГО БЭКЕНДА", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(8.dp))

                    HardwareBackend.entries.forEach { backend ->
                        val isSelected = realtimeStats.activeBackend == backend
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) CyanNeon.copy(alpha = 0.15f) else DarkSurface2)
                                .border(1.dp, if (isSelected) CyanNeon else DarkBorder, RoundedCornerShape(8.dp))
                                .clickable { onSelectBackend(backend) }
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(backend.title, color = if (isSelected) CyanNeon else TextPrimary, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(backend.tag, color = EmeraldAi, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                }
                                Text(backend.description, color = TextSecondary, fontSize = 10.sp)
                            }

                            if (isSelected) {
                                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = CyanNeon, modifier = Modifier.size(18.dp))
                            }
                        }
                    }
                }
            }
        }

        // Compute Tuning: CPU Threads & GPU Offload
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = DarkSurface1),
                shape = RoundedCornerShape(14.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("НАСТРОЙКА РАСПРЕДЕЛЕНИЯ НАГРУЗКИ", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(10.dp))

                    // CPU Threads Slider
                    Column {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Потоки CPU:", color = TextSecondary, fontSize = 11.sp)
                            Text("$cpuThreads потоков", color = CyanNeon, fontWeight = FontWeight.Bold, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                        }
                        Slider(
                            value = cpuThreads.toFloat(),
                            onValueChange = { onUpdateCpuThreads(it.toInt()) },
                            valueRange = 1f..specs.totalCores.toFloat(),
                            steps = (specs.totalCores - 2).coerceAtLeast(0),
                            colors = SliderDefaults.colors(thumbColor = CyanNeon, activeTrackColor = CyanNeon)
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    // GPU Offload Layers
                    Column {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Слои GPU (Vulkan Offload):", color = TextSecondary, fontSize = 11.sp)
                            Text("$gpuLayers слоев", color = PurpleNeon, fontWeight = FontWeight.Bold, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                        }
                        Slider(
                            value = gpuLayers.toFloat(),
                            onValueChange = { onUpdateGpuLayers(it.toInt()) },
                            valueRange = 0f..36f,
                            steps = 35,
                            colors = SliderDefaults.colors(thumbColor = PurpleNeon, activeTrackColor = PurpleNeon)
                        )
                    }
                }
            }
        }

        // Stress Benchmark Test
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = DarkSurface1),
                shape = RoundedCornerShape(14.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("БЕНЧМАРК И СТРЕСС-ТЕСТ КРЕМНИЯ", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text("Оценка производительности NPU/GPU в операциях матричного умножения FP16/INT8.", color = TextSecondary, fontSize = 11.sp)

                    Spacer(modifier = Modifier.height(10.dp))

                    if (benchmarkProgress.isRunning) {
                        Column {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text(benchmarkProgress.currentPhase, color = CyanNeon, fontSize = 11.sp)
                                Text("${(benchmarkProgress.progress * 100).toInt()}%", color = CyanNeon, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            LinearProgressIndicator(
                                progress = { benchmarkProgress.progress },
                                modifier = Modifier.fillMaxWidth().height(6.dp).clip(CircleShape),
                                color = CyanNeon,
                                trackColor = DarkSurface3
                            )
                        }
                    } else {
                        Button(
                            onClick = onRunBenchmark,
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = CyanNeon),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.Default.Bolt, contentDescription = null, tint = DarkVoid, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Запустить бенчмарк", color = DarkVoid, fontWeight = FontWeight.Bold)
                        }
                    }

                    if (benchmarkProgress.latestResult != null) {
                        Spacer(modifier = Modifier.height(10.dp))
                        val res = benchmarkProgress.latestResult!!
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(DarkSurface2)
                                .padding(10.dp)
                        ) {
                            Column {
                                Text("РЕЗУЛЬТАТ БЕНЧМАРКА: ${res.score} БАЛЛОВ", color = EmeraldAi, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                Text("• Производительность: ${res.gflops} GFLOPS (${res.int8Tops} INT8 TOPS)", color = TextSecondary, fontSize = 10.sp)
                                Text("• Пропускная способность памяти: ${res.memoryBandwidthGbps} GB/s", color = TextSecondary, fontSize = 10.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}
