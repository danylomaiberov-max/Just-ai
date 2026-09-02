package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.layout.aspectRatio
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
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ai.multimodal.AudioSynthesisResult
import com.example.ai.multimodal.ImageGenerationResult
import com.example.ai.multimodal.MultiModalStudioEngine
import com.example.ai.multimodal.TranslationResult
import com.example.ai.multimodal.VideoGenerationResult
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
import kotlin.random.Random

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun MultiModalStudioScreen(
    imageResult: ImageGenerationResult?,
    isImageGenerating: Boolean,
    onGenerateImage: (String, String, String) -> Unit,
    videoResult: VideoGenerationResult?,
    isVideoGenerating: Boolean,
    onGenerateVideo: (String, Float) -> Unit,
    audioResult: AudioSynthesisResult?,
    isAudioGenerating: Boolean,
    onSynthesizeAudio: (String, String, Float, Float) -> Unit,
    translateResult: TranslationResult?,
    isTranslating: Boolean,
    onTranslateText: (String, String, String) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val clipboardManager = LocalClipboardManager.current

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkVoid)
            .testTag("multimodal_studio_screen")
    ) {
        // Tab Navigation Header
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = DarkSurface1,
            contentColor = PurpleNeon,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                    color = PurpleNeon
                )
            }
        ) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = { Text("Генерация фото", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                icon = { Icon(Icons.Default.Image, contentDescription = null, modifier = Modifier.size(16.dp)) },
                modifier = Modifier.testTag("tab_photo_gen")
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = { Text("AI Видео", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                icon = { Icon(Icons.Default.Movie, contentDescription = null, modifier = Modifier.size(16.dp)) },
                modifier = Modifier.testTag("tab_video_gen")
            )
            Tab(
                selected = selectedTab == 2,
                onClick = { selectedTab = 2 },
                text = { Text("Озвучка TTS", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                icon = { Icon(Icons.Default.MusicNote, contentDescription = null, modifier = Modifier.size(16.dp)) },
                modifier = Modifier.testTag("tab_audio_tts")
            )
            Tab(
                selected = selectedTab == 3,
                onClick = { selectedTab = 3 },
                text = { Text("Переводчик", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                icon = { Icon(Icons.Default.Translate, contentDescription = null, modifier = Modifier.size(16.dp)) },
                modifier = Modifier.testTag("tab_translator")
            )
        }

        // Sub-screen Content
        when (selectedTab) {
            0 -> PhotoDiffusionStudio(
                result = imageResult,
                isGenerating = isImageGenerating,
                onGenerate = onGenerateImage
            )
            1 -> VideoStoryboardStudio(
                result = videoResult,
                isGenerating = isVideoGenerating,
                onGenerate = onGenerateVideo
            )
            2 -> AudioTtsStudio(
                result = audioResult,
                isGenerating = isAudioGenerating,
                onSynthesize = onSynthesizeAudio
            )
            3 -> NeuralTranslatorStudio(
                result = translateResult,
                isTranslating = isTranslating,
                onTranslate = onTranslateText
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PhotoDiffusionStudio(
    result: ImageGenerationResult?,
    isGenerating: Boolean,
    onGenerate: (String, String, String) -> Unit
) {
    var prompt by remember { mutableStateOf("Неоновый киберпанк город с летающими автомобилями под дождем, 8K детализация") }
    var negativePrompt by remember { mutableStateOf("размытие, плохое качество, артефакты, искажения") }
    var selectedStyle by remember { mutableStateOf(MultiModalStudioEngine.imageStyles.first()) }
    var selectedModel by remember { mutableStateOf(MultiModalStudioEngine.diffusionModels.first()) }
    var selectedSampler by remember { mutableStateOf(MultiModalStudioEngine.samplers.first()) }
    var selectedAspect by remember { mutableStateOf("1:1") }
    var selectedResolution by remember { mutableStateOf("768x768") }
    var steps by remember { mutableFloatStateOf(20f) }
    var cfgScale by remember { mutableFloatStateOf(7.0f) }
    var seed by remember { mutableLongStateOf(749281L) }
    var showAdvancedSettings by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Hero Card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = DarkSurface2),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, PurpleNeon.copy(alpha = 0.5f))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = PurpleNeon, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Локальный генератор изображений", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(EmeraldAi.copy(alpha = 0.15f))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text("Vulkan NPU", color = EmeraldAi, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        "Создание фотореалистичных кадров, концепт-артов и иллюстраций прямо на устройстве без отправки на сервер.",
                        color = TextSecondary,
                        fontSize = 11.sp
                    )
                }
            }
        }

        // Model & Sampler Selection
        item {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text("МОДЕЛЬ ДИФФУЗИИ", color = CyanNeon, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(MultiModalStudioEngine.diffusionModels) { model ->
                        val isSelected = selectedModel == model
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) PurpleNeon.copy(alpha = 0.25f) else DarkSurface2)
                                .border(1.dp, if (isSelected) PurpleNeon else DarkBorder, RoundedCornerShape(8.dp))
                                .clickable { selectedModel = model }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = model,
                                color = if (isSelected) PurpleNeon else TextSecondary,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }
            }
        }

        // Text Prompt Input
        item {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text("ТЕКСТОВЫЙ ПРОМПТ", color = TextPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                OutlinedTextField(
                    value = prompt,
                    onValueChange = { prompt = it },
                    placeholder = { Text("Опишите детально то, что хотите сгенерировать...", color = TextMuted, fontSize = 12.sp) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("image_prompt_input"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PurpleNeon,
                        unfocusedBorderColor = DarkBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedContainerColor = DarkSurface2,
                        unfocusedContainerColor = DarkSurface2
                    ),
                    shape = RoundedCornerShape(10.dp),
                    minLines = 2,
                    maxLines = 4
                )
            }
        }

        // Style Selector Chips
        item {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text("ХУДОЖЕСТВЕННЫЙ СТИЛЬ", color = TextSecondary, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(MultiModalStudioEngine.imageStyles) { style ->
                        val isSelected = selectedStyle == style
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) CyanNeon.copy(alpha = 0.2f) else DarkSurface2)
                                .border(1.dp, if (isSelected) CyanNeon else DarkBorder, RoundedCornerShape(8.dp))
                                .clickable { selectedStyle = style }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = style,
                                color = if (isSelected) CyanNeon else TextSecondary,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }
            }
        }

        // Aspect Ratio Chips
        item {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text("СООТНОШЕНИЕ СТОРОН", color = TextSecondary, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                val aspects = listOf("1:1", "16:9", "9:16", "4:3")
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    aspects.forEach { aspect ->
                        val isSelected = selectedAspect == aspect
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) PurpleNeon else DarkSurface2)
                                .clickable { selectedAspect = aspect }
                                .padding(horizontal = 14.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = aspect,
                                color = if (isSelected) DarkVoid else TextPrimary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        // Toggle Advanced Diffusion Parameters
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(DarkSurface2)
                    .clickable { showAdvancedSettings = !showAdvancedSettings }
                    .padding(10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Tune, contentDescription = null, tint = CyanNeon, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Параметры сэмплера, шагов и CFG Scale", color = TextPrimary, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                }
                Text(if (showAdvancedSettings) "Скрыть" else "Настроить", color = CyanNeon, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
        }

        if (showAdvancedSettings) {
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = DarkSurface2),
                    shape = RoundedCornerShape(10.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder)
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Negative Prompt
                        Column {
                            Text("Отрицательный промпт (Negative Prompt):", color = TextSecondary, fontSize = 10.sp)
                            OutlinedTextField(
                                value = negativePrompt,
                                onValueChange = { negativePrompt = it },
                                modifier = Modifier.fillMaxWidth(),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = PurpleNeon,
                                    unfocusedBorderColor = DarkBorder,
                                    focusedTextColor = TextPrimary,
                                    unfocusedTextColor = TextPrimary,
                                    focusedContainerColor = DarkSurface3,
                                    unfocusedContainerColor = DarkSurface3
                                ),
                                shape = RoundedCornerShape(6.dp),
                                singleLine = true
                            )
                        }

                        // Steps Slider
                        Column {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Шаги диффузии (Sampling Steps):", color = TextSecondary, fontSize = 10.sp)
                                Text("${steps.toInt()}", color = CyanNeon, fontSize = 10.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                            }
                            Slider(
                                value = steps,
                                onValueChange = { steps = it },
                                valueRange = 1f..50f,
                                colors = SliderDefaults.colors(thumbColor = CyanNeon, activeTrackColor = CyanNeon)
                            )
                        }

                        // CFG Guidance
                        Column {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("CFG Guidance Scale (Следование промпту):", color = TextSecondary, fontSize = 10.sp)
                                Text(String.format("%.1f", cfgScale), color = PurpleNeon, fontSize = 10.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                            }
                            Slider(
                                value = cfgScale,
                                onValueChange = { cfgScale = it },
                                valueRange = 1.0f..15.0f,
                                colors = SliderDefaults.colors(thumbColor = PurpleNeon, activeTrackColor = PurpleNeon)
                            )
                        }

                        // Sampler
                        Column {
                            Text("Алгоритм сэмплера:", color = TextSecondary, fontSize = 10.sp)
                            LazyRow(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                items(MultiModalStudioEngine.samplers) { s ->
                                    val isSel = selectedSampler == s
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(if (isSel) CyanNeon else DarkSurface3)
                                            .clickable { selectedSampler = s }
                                            .padding(horizontal = 8.dp, vertical = 4.dp)
                                    ) {
                                        Text(s, color = if (isSel) DarkVoid else TextSecondary, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }

                        // Seed & Randomize
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Сид генерации (Seed): $seed", color = TextSecondary, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                            IconButton(
                                onClick = { seed = Random.nextLong(100000, 999999) },
                                modifier = Modifier.size(28.dp)
                            ) {
                                Icon(Icons.Default.Refresh, contentDescription = "Случайный сид", tint = CyanNeon, modifier = Modifier.size(16.dp))
                            }
                        }
                    }
                }
            }
        }

        // Generate Action Button
        item {
            Button(
                onClick = { onGenerate(prompt, selectedStyle, selectedAspect) },
                enabled = !isGenerating && prompt.isNotBlank(),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("generate_photo_button"),
                colors = ButtonDefaults.buttonColors(
                    containerColor = PurpleNeon,
                    disabledContainerColor = DarkSurface2
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                if (isGenerating) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp), color = DarkVoid, strokeWidth = 2.dp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Диффузия на NPU/GPU...", color = DarkVoid, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                } else {
                    Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = DarkVoid, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Сгенерировать фото", color = DarkVoid, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
            }
        }

        // Generated Image Canvas & Specs Card
        if (result != null) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = DarkSurface1),
                    shape = RoundedCornerShape(14.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, PurpleNeon.copy(alpha = 0.6f))
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        // Image Canvas Art Placeholder
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(
                                    when (result.aspectRatio) {
                                        "16:9" -> 16f / 9f
                                        "9:16" -> 9f / 16f
                                        "4:3" -> 4f / 3f
                                        else -> 1f
                                    }
                                )
                                .clip(RoundedCornerShape(10.dp))
                                .background(
                                    Brush.radialGradient(
                                        result.colorPalette.map { Color(it) }
                                    )
                                )
                                .border(1.dp, DarkBorder, RoundedCornerShape(10.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = Color.White, modifier = Modifier.size(36.dp))
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("Локальный рендер завершен", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                Text("${result.style} • ${result.aspectRatio}", color = Color.White.copy(alpha = 0.8f), fontSize = 10.sp)
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = result.description,
                            color = TextPrimary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        // Metadata Grid
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Время инференса: ${result.inferenceTimeMs} мс", color = CyanNeon, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                            Text("Сид: ${result.seed}", color = TextSecondary, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun VideoStoryboardStudio(
    result: VideoGenerationResult?,
    isGenerating: Boolean,
    onGenerate: (String, Float) -> Unit
) {
    var scriptPrompt by remember { mutableStateOf("Полет дрона сквозь футуристический мегаполис на закате, кинематографичные отражения") }
    var durationSec by remember { mutableFloatStateOf(10f) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = DarkSurface2),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("Генератор видео-раскадровок", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Создание кинематографичных сцен, траекторий движения камеры и планов действия.", color = TextSecondary, fontSize = 11.sp)
                }
            }
        }

        item {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text("СЦЕНАРИЙ ВИДЕО", color = TextPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                OutlinedTextField(
                    value = scriptPrompt,
                    onValueChange = { scriptPrompt = it },
                    placeholder = { Text("Опишите сюжет видеоролика...", color = TextMuted, fontSize = 12.sp) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = CyanNeon,
                        unfocusedBorderColor = DarkBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedContainerColor = DarkSurface2,
                        unfocusedContainerColor = DarkSurface2
                    ),
                    shape = RoundedCornerShape(10.dp),
                    minLines = 2
                )
            }
        }

        item {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Длительность ролика:", color = TextSecondary, fontSize = 11.sp)
                    Text("${durationSec.toInt()} сек", color = CyanNeon, fontSize = 11.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                }
                Slider(
                    value = durationSec,
                    onValueChange = { durationSec = it },
                    valueRange = 5f..30f,
                    colors = SliderDefaults.colors(thumbColor = CyanNeon, activeTrackColor = CyanNeon)
                )
            }
        }

        item {
            Button(
                onClick = { onGenerate(scriptPrompt, durationSec) },
                enabled = !isGenerating && scriptPrompt.isNotBlank(),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(containerColor = CyanNeon),
                shape = RoundedCornerShape(12.dp)
            ) {
                if (isGenerating) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp), color = DarkVoid, strokeWidth = 2.dp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Рендеринг раскадровки...", color = DarkVoid, fontWeight = FontWeight.Bold)
                } else {
                    Icon(Icons.Default.Movie, contentDescription = null, tint = DarkVoid, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Создать видео", color = DarkVoid, fontWeight = FontWeight.Bold)
                }
            }
        }

        if (result != null) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = DarkSurface1),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, CyanNeon.copy(alpha = 0.5f))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(result.title, color = CyanNeon, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Text("${result.resolution} • ${result.fps} FPS • ${result.totalDurationSec} сек", color = TextSecondary, fontSize = 10.sp, fontFamily = FontFamily.Monospace)

                        Spacer(modifier = Modifier.height(10.dp))

                        result.scenes.forEach { scene ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                colors = CardDefaults.cardColors(containerColor = DarkSurface2),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                        Text("Сцена ${scene.sceneNumber}: ${scene.title}", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                        Text("${scene.durationSec.toInt()}с", color = PurpleNeon, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                    }
                                    Text("Камера: ${scene.cameraMovement}", color = EmeraldAi, fontSize = 10.sp)
                                    Text(scene.promptDescription, color = TextSecondary, fontSize = 10.sp)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AudioTtsStudio(
    result: AudioSynthesisResult?,
    isGenerating: Boolean,
    onSynthesize: (String, String, Float, Float) -> Unit
) {
    var textInput by remember { mutableStateOf("Aether AI — передовая платформа для автономного запуска искусственного интеллекта на вашем устройстве.") }
    var selectedVoiceId by remember { mutableStateOf(MultiModalStudioEngine.availableVoices.first().id) }
    var speed by remember { mutableFloatStateOf(1.0f) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = DarkSurface2),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("Нейросетевая озвучка (TTS)", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Синтез естественной речи на русском и других языках без интернета.", color = TextSecondary, fontSize = 11.sp)
                }
            }
        }

        item {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text("ТЕКСТ ДЛЯ ОЗВУЧИВАНИЯ", color = TextPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                OutlinedTextField(
                    value = textInput,
                    onValueChange = { textInput = it },
                    placeholder = { Text("Введите текст для синтеза речи...", color = TextMuted, fontSize = 12.sp) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = EmeraldAi,
                        unfocusedBorderColor = DarkBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedContainerColor = DarkSurface2,
                        unfocusedContainerColor = DarkSurface2
                    ),
                    shape = RoundedCornerShape(10.dp),
                    minLines = 2
                )
            }
        }

        item {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text("ГОЛОСОВОЙ ПРОФИЛЬ", color = TextSecondary, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(MultiModalStudioEngine.availableVoices) { voice ->
                        val isSelected = selectedVoiceId == voice.id
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) EmeraldAi.copy(alpha = 0.2f) else DarkSurface2)
                                .border(1.dp, if (isSelected) EmeraldAi else DarkBorder, RoundedCornerShape(8.dp))
                                .clickable { selectedVoiceId = voice.id }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Column {
                                Text(voice.name, color = if (isSelected) EmeraldAi else TextPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                Text(voice.tone, color = TextSecondary, fontSize = 9.sp)
                            }
                        }
                    }
                }
            }
        }

        item {
            Button(
                onClick = { onSynthesize(textInput, selectedVoiceId, speed, 1.0f) },
                enabled = !isGenerating && textInput.isNotBlank(),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(containerColor = EmeraldAi),
                shape = RoundedCornerShape(12.dp)
            ) {
                if (isGenerating) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp), color = DarkVoid, strokeWidth = 2.dp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Синтез аудиопотока...", color = DarkVoid, fontWeight = FontWeight.Bold)
                } else {
                    Icon(Icons.Default.PlayArrow, contentDescription = null, tint = DarkVoid, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Озвучить текст", color = DarkVoid, fontWeight = FontWeight.Bold)
                }
            }
        }

        if (result != null) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = DarkSurface1),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, EmeraldAi.copy(alpha = 0.5f))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Голос: ${result.voice.name}", color = EmeraldAi, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            Text("${String.format("%.1f", result.durationSeconds)} сек", color = TextSecondary, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Waveform Visualizer
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(36.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(DarkSurface2)
                                .padding(horizontal = 6.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            result.waveformSamples.forEach { sample ->
                                val barHeight = (Math.abs(sample) * 28).coerceIn(4f, 30f).dp
                                Box(
                                    modifier = Modifier
                                        .width(3.dp)
                                        .height(barHeight)
                                        .clip(RoundedCornerShape(2.dp))
                                        .background(EmeraldAi)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun NeuralTranslatorStudio(
    result: TranslationResult?,
    isTranslating: Boolean,
    onTranslate: (String, String, String) -> Unit
) {
    var sourceText by remember { mutableStateOf("Локальный искусственный интеллект выполняет все вычисления прямо на процессоре устройства без передачи информации третьим лицам.") }
    var targetLang by remember { mutableStateOf("Английский") }
    var tone by remember { mutableStateOf("Профессиональный") }

    val languages = listOf("Английский", "Китайский", "Немецкий", "Испанский", "Французский", "Русский")

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = DarkSurface2),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("Локальный нейро-переводчик", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Перевод текстов и документации с сохранением контекста и грамматики.", color = TextSecondary, fontSize = 11.sp)
                }
            }
        }

        item {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text("ИСХОДНЫЙ ТЕКСТ", color = TextPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                OutlinedTextField(
                    value = sourceText,
                    onValueChange = { sourceText = it },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = CyanNeon,
                        unfocusedBorderColor = DarkBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedContainerColor = DarkSurface2,
                        unfocusedContainerColor = DarkSurface2
                    ),
                    shape = RoundedCornerShape(10.dp),
                    minLines = 3
                )
            }
        }

        item {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text("ЦЕЛЕВОЙ ЯЗЫК", color = TextSecondary, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(languages) { lang ->
                        val isSelected = targetLang == lang
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) CyanNeon else DarkSurface2)
                                .clickable { targetLang = lang }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(lang, color = if (isSelected) DarkVoid else TextPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        item {
            Button(
                onClick = { onTranslate(sourceText, targetLang, tone) },
                enabled = !isTranslating && sourceText.isNotBlank(),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(containerColor = CyanNeon),
                shape = RoundedCornerShape(12.dp)
            ) {
                if (isTranslating) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp), color = DarkVoid, strokeWidth = 2.dp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Перевод...", color = DarkVoid, fontWeight = FontWeight.Bold)
                } else {
                    Icon(Icons.Default.Translate, contentDescription = null, tint = DarkVoid, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Выполнить перевод", color = DarkVoid, fontWeight = FontWeight.Bold)
                }
            }
        }

        if (result != null) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = DarkSurface1),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, CyanNeon.copy(alpha = 0.5f))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Результат перевода (${result.targetLanguage})", color = CyanNeon, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            Text(result.detectedSourceLanguage, color = TextSecondary, fontSize = 10.sp)
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(result.translatedText, color = TextPrimary, fontSize = 13.sp, lineHeight = 19.sp)

                        Spacer(modifier = Modifier.height(10.dp))

                        result.grammarNotes.forEach { note ->
                            Text("• $note", color = TextSecondary, fontSize = 10.sp)
                        }
                    }
                }
            }
        }
    }
}
