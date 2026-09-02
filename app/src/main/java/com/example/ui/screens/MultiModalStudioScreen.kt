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
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import com.example.ai.multimodal.AudioSynthesisResult
import com.example.ai.multimodal.ImageGenerationResult
import com.example.ai.multimodal.MultiModalStudioEngine
import com.example.ai.multimodal.TranslationResult
import com.example.ai.multimodal.VideoGenerationResult
import com.example.ui.StudioSubTab
import com.example.ui.theme.AccentBlue
import com.example.ui.theme.AccentLime
import com.example.ui.theme.AccentPink
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
fun MultiModalStudioScreen(
    currentSubTab: StudioSubTab,
    onSubTabSelected: (StudioSubTab) -> Unit,
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
    onTranslate: (String, String, String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkVoid)
            .testTag("multimodal_studio_screen")
    ) {
        // Sub-tabs Selector
        TabRow(
            selectedTabIndex = currentSubTab.ordinal,
            containerColor = DarkSurface1,
            contentColor = CyanNeon,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    Modifier.tabIndicatorOffset(tabPositions[currentSubTab.ordinal]),
                    color = CyanNeon
                )
            }
        ) {
            Tab(
                selected = currentSubTab == StudioSubTab.PHOTO,
                onClick = { onSubTabSelected(StudioSubTab.PHOTO) },
                text = { Text("Photo AI", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                icon = { Icon(Icons.Default.Image, contentDescription = null, modifier = Modifier.size(16.dp)) },
                modifier = Modifier.testTag("subtab_photo")
            )
            Tab(
                selected = currentSubTab == StudioSubTab.VIDEO,
                onClick = { onSubTabSelected(StudioSubTab.VIDEO) },
                text = { Text("Video AI", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                icon = { Icon(Icons.Default.Movie, contentDescription = null, modifier = Modifier.size(16.dp)) },
                modifier = Modifier.testTag("subtab_video")
            )
            Tab(
                selected = currentSubTab == StudioSubTab.VOICE_TTS,
                onClick = { onSubTabSelected(StudioSubTab.VOICE_TTS) },
                text = { Text("Voice TTS", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                icon = { Icon(Icons.Default.RecordVoiceOver, contentDescription = null, modifier = Modifier.size(16.dp)) },
                modifier = Modifier.testTag("subtab_voice")
            )
            Tab(
                selected = currentSubTab == StudioSubTab.TRANSLATE,
                onClick = { onSubTabSelected(StudioSubTab.TRANSLATE) },
                text = { Text("Translate", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                icon = { Icon(Icons.Default.Translate, contentDescription = null, modifier = Modifier.size(16.dp)) },
                modifier = Modifier.testTag("subtab_translate")
            )
        }

        when (currentSubTab) {
            StudioSubTab.PHOTO -> PhotoStudioView(imageResult, isImageGenerating, onGenerateImage)
            StudioSubTab.VIDEO -> VideoStudioView(videoResult, isVideoGenerating, onGenerateVideo)
            StudioSubTab.VOICE_TTS -> VoiceTtsView(audioResult, isAudioGenerating, onSynthesizeAudio)
            StudioSubTab.TRANSLATE -> TranslateView(translateResult, isTranslating, onTranslate)
        }
    }
}

@Composable
fun PhotoStudioView(
    result: ImageGenerationResult?,
    isGenerating: Boolean,
    onGenerate: (String, String, String) -> Unit
) {
    var prompt by remember { mutableStateOf("Cybernetic neural samurai in glowing neon rain, ultra detailed 8k") }
    var selectedStyle by remember { mutableStateOf(MultiModalStudioEngine.imageStyles.first()) }
    var selectedRatio by remember { mutableStateOf("1:1") }

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
                    Text(
                        text = "🎨 Stable Diffusion Mobile (Text-to-Image)",
                        color = CyanNeon,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = prompt,
                        onValueChange = { prompt = it },
                        label = { Text("Image Description Prompt") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("photo_prompt_input"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = CyanNeon,
                            unfocusedBorderColor = DarkBorder,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        ),
                        maxLines = 3
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text("Aesthetic Style Filter:", color = TextSecondary, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                    Spacer(modifier = Modifier.height(6.dp))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(MultiModalStudioEngine.imageStyles) { style ->
                            val isSelected = style == selectedStyle
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSelected) PurpleNeon.copy(alpha = 0.3f) else DarkSurface3)
                                    .border(1.dp, if (isSelected) PurpleNeon else DarkBorder, RoundedCornerShape(8.dp))
                                    .clickable { selectedStyle = style }
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = style,
                                    color = if (isSelected) PurpleNeon else TextSecondary,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            listOf("1:1", "16:9", "9:16").forEach { ratio ->
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(if (selectedRatio == ratio) CyanNeon.copy(alpha = 0.2f) else DarkSurface3)
                                        .border(1.dp, if (selectedRatio == ratio) CyanNeon else DarkBorder, RoundedCornerShape(6.dp))
                                        .clickable { selectedRatio = ratio }
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = ratio,
                                        color = if (selectedRatio == ratio) CyanNeon else TextMuted,
                                        fontSize = 10.sp
                                    )
                                }
                            }
                        }

                        Button(
                            onClick = { onGenerate(prompt, selectedStyle, selectedRatio) },
                            enabled = !isGenerating,
                            colors = ButtonDefaults.buttonColors(containerColor = CyanNeon, contentColor = DarkVoid),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.testTag("generate_photo_button")
                        ) {
                            if (isGenerating) {
                                CircularProgressIndicator(modifier = Modifier.size(16.dp), color = DarkVoid, strokeWidth = 2.dp)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Synthesizing...")
                            } else {
                                Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Generate On-Device", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        // Render Canvas Output
        item {
            if (result != null) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = DarkSurface2),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, CyanNeon.copy(alpha = 0.5f))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text("✨ Rendered AI Asset (Seed: ${result.seed})", color = CyanNeon, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Spacer(modifier = Modifier.height(8.dp))

                        // Visual Artwork Canvas with Gradient Atmosphere
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(220.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(
                                    Brush.linearGradient(
                                        result.colorPalette.map { Color(it) }
                                    )
                                )
                                .border(1.dp, DarkBorder, RoundedCornerShape(10.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    imageVector = Icons.Default.CameraAlt,
                                    contentDescription = null,
                                    tint = Color.White.copy(alpha = 0.8f),
                                    modifier = Modifier.size(48.dp)
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = result.style,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                )
                                Text(
                                    text = "Generated locally in ${result.inferenceTimeMs}ms",
                                    color = Color.White.copy(alpha = 0.7f),
                                    fontSize = 11.sp,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        Text(text = result.description, color = TextPrimary, fontSize = 12.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun VideoStudioView(
    result: VideoGenerationResult?,
    isGenerating: Boolean,
    onGenerate: (String, Float) -> Unit
) {
    var script by remember { mutableStateOf("Drone flight through a futuristic cyberpunk megacity at dusk with neon skyscrapers") }
    var duration by remember { mutableFloatStateOf(10f) }

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
                    Text("🎬 AI Video Storyboard & Keyframe Animator", color = PurpleNeon, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = script,
                        onValueChange = { script = it },
                        label = { Text("Video Script / Scene Concept") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PurpleNeon,
                            unfocusedBorderColor = DarkBorder,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        ),
                        maxLines = 3
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text("Duration: ${duration.toInt()} seconds", color = TextSecondary, fontSize = 12.sp)
                    Slider(
                        value = duration,
                        onValueChange = { duration = it },
                        valueRange = 5f..30f,
                        steps = 4,
                        colors = SliderDefaults.colors(thumbColor = PurpleNeon, activeTrackColor = PurpleNeon)
                    )

                    Button(
                        onClick = { onGenerate(script, duration) },
                        enabled = !isGenerating,
                        colors = ButtonDefaults.buttonColors(containerColor = PurpleNeon),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        if (isGenerating) {
                            CircularProgressIndicator(modifier = Modifier.size(16.dp), color = Color.White, strokeWidth = 2.dp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Rendering Scenes...")
                        } else {
                            Icon(Icons.Default.Videocam, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Animate & Generate Storyboard", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        if (result != null) {
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = DarkSurface2),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, PurpleNeon.copy(alpha = 0.5f))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(result.title, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text("${result.fps} FPS • ${result.resolution}", color = PurpleNeon, fontSize = 11.sp)
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        result.scenes.forEach { scene ->
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(DarkSurface3)
                                    .border(1.dp, DarkBorder, RoundedCornerShape(8.dp))
                                    .padding(10.dp)
                            ) {
                                Column {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text("Scene #${scene.sceneNumber}: ${scene.title}", color = CyanNeon, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                        Text("${scene.durationSec.toInt()}s • ${scene.cameraMovement}", color = TextSecondary, fontSize = 10.sp)
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(scene.promptDescription, color = TextPrimary, fontSize = 11.sp)
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
fun VoiceTtsView(
    result: AudioSynthesisResult?,
    isGenerating: Boolean,
    onSynthesize: (String, String, Float, Float) -> Unit
) {
    var text by remember { mutableStateOf("Welcome to Aether AI. Local neural voice synthesis running with ultra low latency.") }
    var selectedVoiceId by remember { mutableStateOf(MultiModalStudioEngine.availableVoices.first().id) }
    var speed by remember { mutableFloatStateOf(1.0f) }
    var pitch by remember { mutableFloatStateOf(1.0f) }
    var isPlaying by remember { mutableStateOf(false) }

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
                    Text("🎙️ Neural Audio & Voice Studio (TTS & Whisper)", color = EmeraldAi, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = text,
                        onValueChange = { text = it },
                        label = { Text("Text to Speak") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = EmeraldAi,
                            unfocusedBorderColor = DarkBorder,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        ),
                        maxLines = 4
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text("Select Neural Voice Profile:", color = TextSecondary, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                    Spacer(modifier = Modifier.height(6.dp))

                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(MultiModalStudioEngine.availableVoices) { voice ->
                            val isSelected = voice.id == selectedVoiceId
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSelected) EmeraldAi.copy(alpha = 0.25f) else DarkSurface3)
                                    .border(1.dp, if (isSelected) EmeraldAi else DarkBorder, RoundedCornerShape(8.dp))
                                    .clickable { selectedVoiceId = voice.id }
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Column {
                                    Text(
                                        text = "${voice.name} (${voice.gender})",
                                        color = if (isSelected) EmeraldAi else TextPrimary,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = voice.tone,
                                        color = TextSecondary,
                                        fontSize = 9.sp
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Speed: ${String.format("%.1f", speed)}x", color = TextSecondary, fontSize = 11.sp)
                        Text("Pitch: ${String.format("%.1f", pitch)}x", color = TextSecondary, fontSize = 11.sp)
                    }
                    Slider(
                        value = speed,
                        onValueChange = { speed = it },
                        valueRange = 0.5f..2.0f,
                        colors = SliderDefaults.colors(thumbColor = EmeraldAi, activeTrackColor = EmeraldAi)
                    )

                    Button(
                        onClick = { onSynthesize(text, selectedVoiceId, speed, pitch) },
                        enabled = !isGenerating,
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldAi, contentColor = DarkVoid),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        if (isGenerating) {
                            CircularProgressIndicator(modifier = Modifier.size(16.dp), color = DarkVoid, strokeWidth = 2.dp)
                        } else {
                            Icon(Icons.Default.VolumeUp, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Synthesize Neural Voice", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        if (result != null) {
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
                            Column {
                                Text("Voice: ${result.voice.name}", color = EmeraldAi, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                Text("Duration: ${String.format("%.1f", result.durationSeconds)}s", color = TextSecondary, fontSize = 11.sp)
                            }
                            IconButton(
                                onClick = { isPlaying = !isPlaying },
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(EmeraldAi)
                            ) {
                                Icon(
                                    imageVector = if (isPlaying) Icons.Default.GraphicEq else Icons.Default.PlayArrow,
                                    contentDescription = "Play",
                                    tint = DarkVoid
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Waveform Amplitudes Bar
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(40.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(DarkSurface3)
                                .padding(horizontal = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            result.waveformSamples.take(32).forEach { sample ->
                                Box(
                                    modifier = Modifier
                                        .width(3.dp)
                                        .height((32 * sample).dp)
                                        .clip(RoundedCornerShape(2.dp))
                                        .background(if (isPlaying) EmeraldAi else CyanNeon)
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
fun TranslateView(
    result: TranslationResult?,
    isTranslating: Boolean,
    onTranslate: (String, String, String) -> Unit
) {
    var text by remember { mutableStateOf("Local on-device AI runs with absolute data security, zero telemetry, and maximum performance.") }
    var targetLanguage by remember { mutableStateOf("Russian (Русский)") }
    var tone by remember { mutableStateOf("Technical") }

    val languages = listOf("Russian (Русский)", "English (US)", "German (Deutsch)", "Japanese (日本語)", "Chinese (中文)", "French (Français)", "Spanish (Español)")

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
                    Text("🌐 Neural Multilingual Translator (50+ Languages)", color = AccentBlue, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = text,
                        onValueChange = { text = it },
                        label = { Text("Source Text") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = AccentBlue,
                            unfocusedBorderColor = DarkBorder,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        ),
                        maxLines = 4
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text("Target Language:", color = TextSecondary, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                    Spacer(modifier = Modifier.height(6.dp))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(languages) { lang ->
                            val isSelected = lang == targetLanguage
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSelected) AccentBlue.copy(alpha = 0.25f) else DarkSurface3)
                                    .border(1.dp, if (isSelected) AccentBlue else DarkBorder, RoundedCornerShape(8.dp))
                                    .clickable { targetLanguage = lang }
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = lang,
                                    color = if (isSelected) AccentBlue else TextSecondary,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = { onTranslate(text, targetLanguage, tone) },
                        enabled = !isTranslating,
                        colors = ButtonDefaults.buttonColors(containerColor = AccentBlue, contentColor = DarkVoid),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        if (isTranslating) {
                            CircularProgressIndicator(modifier = Modifier.size(16.dp), color = DarkVoid, strokeWidth = 2.dp)
                        } else {
                            Icon(Icons.Default.Translate, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Translate On-Device", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        if (result != null) {
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = DarkSurface2),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, AccentBlue.copy(alpha = 0.5f))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text("🎯 Translation to ${result.targetLanguage}", color = AccentBlue, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(result.translatedText, color = TextPrimary, fontSize = 13.sp, lineHeight = 19.sp)

                        Spacer(modifier = Modifier.height(10.dp))
                        Text("Grammar & Context Notes:", color = TextSecondary, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        result.grammarNotes.forEach { note ->
                            Text("• $note", color = TextMuted, fontSize = 10.sp)
                        }
                    }
                }
            }
        }
    }
}
