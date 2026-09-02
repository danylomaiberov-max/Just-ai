package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.hardware.HardwareBackend
import com.example.hardware.HardwareBenchmarkResult
import com.example.hardware.HardwareRealtimeStats
import com.example.hardware.HardwareSpecs
import com.example.hardware.HardwareStressTestProgress
import com.example.ui.theme.AmberWarning
import com.example.ui.theme.CoralError
import com.example.ui.theme.CyanNeon
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkSurface1
import com.example.ui.theme.DarkSurface2
import com.example.ui.theme.DarkVoid
import com.example.ui.theme.EmeraldAi
import com.example.ui.theme.PurpleNeon
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@OptIn(ExperimentalLayoutApi::class)
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
    var selectedMatrixDim by remember { mutableIntStateOf(256) }
    var selectedQuantization by remember { mutableStateOf("INT8") }

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
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(Brush.linearGradient(listOf(CyanNeon, PurpleNeon))),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.DeveloperBoard,
                                    contentDescription = "Hardware",
                                    tint = DarkVoid,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "Запуск на железе смартфона",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    color = TextPrimary
                                )
                                Text(
                                    text = specs.deviceModel,
                                    fontSize = 12.sp,
                                    color = CyanNeon,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = EmeraldAi.copy(alpha = 0.15f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, EmeraldAi.copy(alpha = 0.4f))
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .clip(CircleShape)
                                        .background(EmeraldAi)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "NATIVE ON-DEVICE",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = EmeraldAi
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // SoC and Chip Details Grid
                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        SpecBadge("Чипсет", specs.socManufacturer)
                        SpecBadge("Архитектура", specs.cpuArch)
                        SpecBadge("Ядра CPU", "${specs.totalCores}x Cores")
                        SpecBadge("GPU / Графика", specs.gpuRenderer)
                        SpecBadge("Физическая RAM", "${specs.totalRamMb} MB LPDDR5")
                    }
                }
            }
        }

        // Realtime Hardware Monitor HUD
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, DarkBorder, RoundedCornerShape(14.dp)),
                colors = CardDefaults.cardColors(containerColor = DarkSurface1),
                shape = RoundedCornerShape(14.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "АППАРАТНАЯ ТЕЛЕМЕТРИЯ В РЕАЛЬНОМ ВРЕМЕНИ",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextMuted,
                        letterSpacing = 1.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        MetricBlock(
                            label = "ТЕМПЕРАТУРА",
                            value = "${String.format("%.1f", realtimeStats.batteryTempCelsius)}°C",
                            subValue = realtimeStats.thermalStatus,
                            color = if (realtimeStats.batteryTempCelsius > 40f) CoralError else EmeraldAi,
                            icon = Icons.Default.Thermostat
                        )

                        MetricBlock(
                            label = "ВЫЧИСЛЕНИЯ",
                            value = "${String.format("%.0f", realtimeStats.currentGflops)} GFLOPS",
                            subValue = realtimeStats.activeBackend.tag,
                            color = CyanNeon,
                            icon = Icons.Default.Bolt
                        )

                        MetricBlock(
                            label = "ПАМЯТЬ RAM",
                            value = "${realtimeStats.ramUsedMb} MB",
                            subValue = "${realtimeStats.ramFreeMb} MB свободно",
                            color = PurpleNeon,
                            icon = Icons.Default.Memory
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // CPU Cores Visualizer
                    Text(
                        text = "Нагрузка по ядрам процессора (${specs.totalCores} ядер):",
                        fontSize = 11.sp,
                        color = TextSecondary
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        realtimeStats.coreStats.forEach { core ->
                            val animatedLoad by animateFloatAsState(
                                targetValue = core.loadPercentage / 100f,
                                animationSpec = tween(300, easing = FastOutSlowInEasing),
                                label = "core_load"
                            )
                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(DarkSurface2)
                                    .padding(4.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(28.dp)
                                        .clip(RoundedCornerShape(2.dp))
                                        .background(DarkVoid),
                                    contentAlignment = Alignment.BottomCenter
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(28.dp * animatedLoad.coerceIn(0.05f, 1f))
                                            .background(
                                                if (core.isPerformanceCore) CyanNeon else EmeraldAi
                                            )
                                    )
                                }
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "C${core.coreIndex}",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (core.isPerformanceCore) CyanNeon else TextMuted
                                )
                            }
                        }
                    }
                }
            }
        }

        // Hardware Backend Selector
        item {
            Text(
                text = "ВЫБОР ВЫЧИСЛИТЕЛЬНОГО БЭКЕНДА ДЛЯ ИНФЕРЕНСА",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = TextMuted,
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                HardwareBackend.values().forEach { backend ->
                    val isSelected = realtimeStats.activeBackend == backend
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSelectBackend(backend) }
                            .border(
                                width = if (isSelected) 1.5.dp else 1.dp,
                                color = if (isSelected) CyanNeon else DarkBorder,
                                shape = RoundedCornerShape(12.dp)
                            )
                            .testTag("backend_card_${backend.tag.lowercase()}"),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSelected) DarkSurface2 else DarkSurface1
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                modifier = Modifier.weight(1f),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(34.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(
                                            if (isSelected) CyanNeon.copy(alpha = 0.2f) else DarkSurface2
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = when (backend) {
                                            HardwareBackend.CPU_NEON -> Icons.Default.DeveloperBoard
                                            HardwareBackend.GPU_VULKAN -> Icons.Default.Speed
                                            HardwareBackend.NPU_NNAPI -> Icons.Default.Bolt
                                            HardwareBackend.HYBRID_CORE -> Icons.Default.Memory
                                        },
                                        contentDescription = backend.title,
                                        tint = if (isSelected) CyanNeon else TextSecondary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.width(12.dp))

                                Column {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = backend.title,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp,
                                            color = if (isSelected) CyanNeon else TextPrimary
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Surface(
                                            shape = RoundedCornerShape(4.dp),
                                            color = if (isSelected) CyanNeon.copy(alpha = 0.15f) else DarkSurface2
                                        ) {
                                            Text(
                                                text = backend.tag,
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = if (isSelected) CyanNeon else TextMuted,
                                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                            )
                                        }
                                    }
                                    Text(
                                        text = backend.description,
                                        fontSize = 11.sp,
                                        color = TextSecondary,
                                        lineHeight = 14.sp
                                    )
                                }
                            }

                            if (isSelected) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = "Active",
                                    tint = CyanNeon,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Stress Test & Benchmark Section
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, DarkBorder, RoundedCornerShape(14.dp)),
                colors = CardDefaults.cardColors(containerColor = DarkSurface1),
                shape = RoundedCornerShape(14.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "АППАРАТНЫЙ БЕНЧМАРК ЖЕЛЕЗА",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = TextPrimary
                            )
                            Text(
                                text = "Замер реальных GFLOPS, шины LPDDR5 и тензорных матриц",
                                fontSize = 11.sp,
                                color = TextSecondary
                            )
                        }

                        Button(
                            onClick = onRunBenchmark,
                            enabled = !benchmarkProgress.isRunning,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = CyanNeon,
                                contentColor = DarkVoid
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.testTag("btn_run_hardware_benchmark")
                        ) {
                            if (benchmarkProgress.isRunning) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(16.dp),
                                    color = DarkVoid,
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Default.RocketLaunch,
                                    contentDescription = "Run",
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Запустить",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }

                    if (benchmarkProgress.isRunning) {
                        Spacer(modifier = Modifier.height(14.dp))
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = benchmarkProgress.currentPhase,
                                    fontSize = 11.sp,
                                    color = CyanNeon,
                                    fontWeight = FontWeight.Medium
                                )
                                Text(
                                    text = "${(benchmarkProgress.progress * 100).toInt()}%",
                                    fontSize = 11.sp,
                                    color = CyanNeon,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            LinearProgressIndicator(
                                progress = { benchmarkProgress.progress },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(6.dp)
                                    .clip(RoundedCornerShape(3.dp)),
                                color = CyanNeon,
                                trackColor = DarkSurface2
                            )
                        }
                    }

                    benchmarkProgress.latestResult?.let { res ->
                        Spacer(modifier = Modifier.height(14.dp))
                        BenchmarkResultsCard(result = res)
                    }
                }
            }
        }

        // Live On-Device Tensor Multiplier Sandbox
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, DarkBorder, RoundedCornerShape(14.dp)),
                colors = CardDefaults.cardColors(containerColor = DarkSurface1),
                shape = RoundedCornerShape(14.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "ИНТЕРАКТИВНЫЙ ТЕНЗОРНЫЙ ПАСС НА КРЕМНИИ",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextMuted,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Прямой запуск перемножения квантованных весовых матриц в памяти телефона.",
                        fontSize = 11.sp,
                        color = TextSecondary
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = "Размер матрицы:", fontSize = 11.sp, color = TextMuted)
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                listOf(128, 256, 384, 512).forEach { dim ->
                                    FilterChip(
                                        selected = selectedMatrixDim == dim,
                                        onClick = { selectedMatrixDim = dim },
                                        label = { Text("${dim}x${dim}", fontSize = 10.sp) },
                                        colors = FilterChipDefaults.filterChipColors(
                                            selectedContainerColor = PurpleNeon,
                                            selectedLabelColor = DarkVoid,
                                            containerColor = DarkSurface2,
                                            labelColor = TextSecondary
                                        )
                                    )
                                }
                            }
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = "Квантование:", fontSize = 11.sp, color = TextMuted)
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                listOf("INT4", "INT8", "FP16", "FP32").forEach { q ->
                                    FilterChip(
                                        selected = selectedQuantization == q,
                                        onClick = { selectedQuantization = q },
                                        label = { Text(q, fontSize = 10.sp) },
                                        colors = FilterChipDefaults.filterChipColors(
                                            selectedContainerColor = CyanNeon,
                                            selectedLabelColor = DarkVoid,
                                            containerColor = DarkSurface2,
                                            labelColor = TextSecondary
                                        )
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = { onRunTensorTest(selectedMatrixDim, selectedQuantization) },
                        enabled = !isTensorTesting,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = PurpleNeon,
                            contentColor = DarkVoid
                        ),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("btn_execute_silicon_pass")
                    ) {
                        if (isTensorTesting) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                color = DarkVoid,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = "Compute",
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Вычислить на кремнии чипсета (${selectedMatrixDim}x${selectedMatrixDim} $selectedQuantization)",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }
                    }

                    tensorTestResult?.let { (timeMs, gflops) ->
                        Spacer(modifier = Modifier.height(10.dp))
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = DarkSurface2,
                            border = androidx.compose.foundation.BorderStroke(1.dp, PurpleNeon.copy(alpha = 0.5f))
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = "ТЕНЗОРНЫЙ РЕЗУЛЬТАТ В ПАМЯТИ",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = PurpleNeon
                                    )
                                    Text(
                                        text = "Время вычисления: ${timeMs} ms",
                                        fontSize = 12.sp,
                                        color = TextPrimary
                                    )
                                }
                                Text(
                                    text = "${String.format("%.1f", gflops)} GFLOPS",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = EmeraldAi,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        }
                    }
                }
            }
        }

        // Hardware Controls (CPU Threads & Vulkan Offload)
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, DarkBorder, RoundedCornerShape(14.dp)),
                colors = CardDefaults.cardColors(containerColor = DarkSurface1),
                shape = RoundedCornerShape(14.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "ТОНКАЯ НАСТРОЙКА АППАРАТНЫХ ПОТОКОВ",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextMuted,
                        letterSpacing = 1.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // CPU Threads Slider
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Выделенные потоки CPU:",
                            fontSize = 12.sp,
                            color = TextPrimary
                        )
                        Text(
                            text = "$cpuThreads из ${specs.totalCores} ядер",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = CyanNeon
                        )
                    }
                    Slider(
                        value = cpuThreads.toFloat(),
                        onValueChange = { onUpdateCpuThreads(it.toInt()) },
                        valueRange = 1f..specs.totalCores.toFloat(),
                        steps = specs.totalCores - 2,
                        colors = SliderDefaults.colors(
                            thumbColor = CyanNeon,
                            activeTrackColor = CyanNeon,
                            inactiveTrackColor = DarkSurface2
                        ),
                        modifier = Modifier.testTag("slider_cpu_threads")
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // GPU Offload Layers
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Выгрузка слоёв в Vulkan GPU:",
                            fontSize = 12.sp,
                            color = TextPrimary
                        )
                        Text(
                            text = "$gpuLayers слоёв",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = PurpleNeon
                        )
                    }
                    Slider(
                        value = gpuLayers.toFloat(),
                        onValueChange = { onUpdateGpuLayers(it.toInt()) },
                        valueRange = 0f..33f,
                        steps = 32,
                        colors = SliderDefaults.colors(
                            thumbColor = PurpleNeon,
                            activeTrackColor = PurpleNeon,
                            inactiveTrackColor = DarkSurface2
                        ),
                        modifier = Modifier.testTag("slider_gpu_layers")
                    )
                }
            }
        }
    }
}

@Composable
fun BenchmarkResultsCard(result: HardwareBenchmarkResult) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = DarkSurface2,
        border = androidx.compose.foundation.BorderStroke(1.dp, CyanNeon.copy(alpha = 0.5f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "ОЦЕНКА ПРОИЗВОДИТЕЛЬНОСТИ ЖЕЛЕЗА",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = CyanNeon
                    )
                    Text(
                        text = "Общий индекс: ${result.score} pts",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = CyanNeon.copy(alpha = 0.2f)
                ) {
                    Text(
                        text = result.backend.tag,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = CyanNeon,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                ResItem("FP32 GFLOPS", "${result.gflops}", EmeraldAi)
                ResItem("INT8 TOPS", "${result.int8Tops}", CyanNeon)
                ResItem("LPDDR5 Шина", "${result.memoryBandwidthGbps} GB/s", PurpleNeon)
                ResItem("Задержка Кэша", "${result.cacheLatencyNs} ns", AmberWarning)
            }
        }
    }
}

@Composable
fun ResItem(label: String, value: String, color: Color) {
    Column {
        Text(text = label, fontSize = 9.sp, color = TextMuted)
        Text(
            text = value,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = color,
            fontFamily = FontFamily.Monospace
        )
    }
}

@Composable
fun SpecBadge(title: String, value: String) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = DarkSurface2,
        border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder)
    ) {
        Column(modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)) {
            Text(text = title, fontSize = 9.sp, color = TextMuted)
            Text(text = value, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
        }
    }
}

@Composable
fun MetricBlock(
    label: String,
    value: String,
    subValue: String,
    color: Color,
    icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(imageVector = icon, contentDescription = null, tint = color, modifier = Modifier.size(12.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text(text = label, fontSize = 9.sp, color = TextMuted, fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = value,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = color,
            fontFamily = FontFamily.Monospace
        )
        Text(text = subValue, fontSize = 9.sp, color = TextMuted)
    }
}
