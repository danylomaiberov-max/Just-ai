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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Dns
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.FindInPage
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.templates.PromptTemplate
import com.example.ui.AppTab
import com.example.ui.theme.AmberWarning
import com.example.ui.theme.CyanNeon
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkSurface1
import com.example.ui.theme.DarkSurface2
import com.example.ui.theme.DarkSurface3
import com.example.ui.theme.DarkVoid
import com.example.ui.theme.EmeraldAi
import com.example.ui.theme.PocketPalCoral
import com.example.ui.theme.PocketPalOrange
import com.example.ui.theme.PurpleNeon
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

data class CommunityPal(
    val id: String,
    val emoji: String,
    val name: String,
    val author: String,
    val description: String,
    val systemPrompt: String,
    val temperature: Float,
    val category: String,
    val downloads: String
)

@Composable
fun PalsScreen(
    templates: List<PromptTemplate>,
    activeTemplate: PromptTemplate?,
    onSelectPal: (PromptTemplate) -> Unit,
    onCreatePal: (name: String, desc: String, prompt: String, temp: Float, topP: Float, ctx: Int, category: String) -> Unit,
    onDeletePal: (String) -> Unit,
    onOpenChatWithPal: (PromptTemplate) -> Unit,
    onNavigateToTab: (AppTab) -> Unit,
    onOpenToolsSheet: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableIntStateOf(0) } // 0: My Pals, 1: PalsHub
    var searchQuery by remember { mutableStateOf("") }
    var showCreateDialog by remember { mutableStateOf(false) }

    val communityPals = remember {
        listOf(
            CommunityPal(
                id = "comm_cyberpunk_rpg",
                emoji = "🦾",
                name = "Cyberpunk RPG Netrunner",
                author = "PocketPal Team",
                description = "Иммерсивный ролевой персонаж в неоновом мире Найт-Сити с диалогами и выборами действий.",
                systemPrompt = "You are V, a veteran netrunner in Night City. Speak in authentic slang, describe choices dramatically, and react in real-time.",
                temperature = 0.95f,
                category = "Roleplay",
                downloads = "14.2K"
            ),
            CommunityPal(
                id = "comm_english_mentor",
                emoji = "🇬🇧",
                name = "English Fluency Coach",
                author = "Community",
                description = "Терпеливый репетитор разговорного английского: находит и мягко исправляет грамматику и идиомы.",
                systemPrompt = "You are an empathetic English tutor. Converse naturally, point out subtle grammatical mistakes, and explain natural phrasal verbs.",
                temperature = 0.5f,
                category = "Education",
                downloads = "28.5K"
            ),
            CommunityPal(
                id = "comm_bash_god",
                emoji = "🐧",
                name = "Linux & DevOps Terminal",
                author = "KernelTeam",
                description = "Справочник и генератор shell-скриптов, one-liner команд, Dockerfile и CI/CD пайплайнов.",
                systemPrompt = "You are a senior Linux SysAdmin and DevOps engineer. Output safe, clean, copyable commands with flags explained.",
                temperature = 0.2f,
                category = "Coding",
                downloads = "19.8K"
            ),
            CommunityPal(
                id = "comm_zen_coach",
                emoji = "🧘",
                name = "Mindfulness & Zen Guide",
                author = "PocketPal Wellness",
                description = "Практики осознанности, техники глубокого дыхания и преодоление тревожности.",
                systemPrompt = "You are a calming mindfulness and meditation guide. Speak in soothing, grounding sentences to relieve stress.",
                temperature = 0.7f,
                category = "Lifestyle",
                downloads = "9.4K"
            )
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkVoid)
            .padding(horizontal = 14.dp, vertical = 8.dp)
    ) {
        // Header Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Pals",
                        color = TextPrimary,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(PocketPalOrange.copy(alpha = 0.2f))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text("🦊 Персоны", color = PocketPalOrange, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }
                Text(
                    text = "ИИ-ассистенты и характер диалога PocketPal",
                    color = TextSecondary,
                    fontSize = 11.sp
                )
            }

            Button(
                onClick = { showCreateDialog = true },
                colors = ButtonDefaults.buttonColors(containerColor = PocketPalOrange),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.testTag("create_pal_button")
            ) {
                Icon(Icons.Filled.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Создать", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Поиск Pals...", color = TextMuted, fontSize = 12.sp) },
            leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null, tint = TextMuted, modifier = Modifier.size(18.dp)) },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("search_pals_input"),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = DarkSurface1,
                unfocusedContainerColor = DarkSurface1,
                focusedBorderColor = PocketPalOrange,
                unfocusedBorderColor = DarkBorder
            ),
            shape = RoundedCornerShape(14.dp),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Tabs: My Pals vs PalsHub
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = DarkSurface1,
            contentColor = PocketPalOrange,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                    color = PocketPalOrange
                )
            },
            divider = { HorizontalDivider(color = DarkBorder, thickness = 0.5.dp) },
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
        ) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("🦊 Мои Pals (${templates.size})", fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal, fontSize = 12.sp)
                    }
                }
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.Explore, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("PalsHub Каталог", fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal, fontSize = 12.sp)
                    }
                }
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (selectedTab == 0) {
            // MY PALS LIST
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Specialized Native Tools Cards section
                item {
                    Text(
                        text = "Встроенные расширения и функции",
                        color = TextMuted,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                }

                item {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        item {
                            SpecialFeatureChip(
                                icon = Icons.Filled.Tune,
                                title = "Шторка функций",
                                subtitle = "Быстрое меню",
                                color = PocketPalCoral,
                                onClick = onOpenToolsSheet
                            )
                        }
                        item {
                            SpecialFeatureChip(
                                icon = Icons.Filled.AutoAwesome,
                                title = "Студия Мультимедиа",
                                subtitle = "OCR, Фото, Голос TTS",
                                color = PocketPalOrange,
                                onClick = { onNavigateToTab(AppTab.STUDIO_MULTIMODAL) }
                            )
                        }
                        item {
                            SpecialFeatureChip(
                                icon = Icons.Filled.Code,
                                title = "Код Sandbox IDE",
                                subtitle = "Компилятор & Терминал",
                                color = CyanNeon,
                                onClick = { onNavigateToTab(AppTab.CODE_IDE) }
                            )
                        }
                        item {
                            SpecialFeatureChip(
                                icon = Icons.Filled.FindInPage,
                                title = "RAG База Знаний",
                                subtitle = "Векторный поиск PDF",
                                color = PurpleNeon,
                                onClick = { onNavigateToTab(AppTab.VECTOR_RAG) }
                            )
                        }
                        item {
                            SpecialFeatureChip(
                                icon = Icons.Filled.Dns,
                                title = "Локальный API Сервер",
                                subtitle = "Ollama / OpenAI :11434",
                                color = EmeraldAi,
                                onClick = { onNavigateToTab(AppTab.AAS_SERVER) }
                            )
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Персоны для диалога",
                        color = TextMuted,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                }

                val filtered = templates.filter {
                    searchQuery.isBlank() || it.name.contains(searchQuery, ignoreCase = true) || it.description.contains(searchQuery, ignoreCase = true)
                }

                items(filtered) { template ->
                    val isActive = template.id == activeTemplate?.id
                    PalCard(
                        template = template,
                        isActive = isActive,
                        onSelect = { onSelectPal(template) },
                        onStartChat = { onOpenChatWithPal(template) },
                        onDelete = if (template.isCustom) { { onDeletePal(template.id) } } else null
                    )
                }
            }
        } else {
            // PALSHUB COMMUNITY
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = DarkSurface1),
                        shape = RoundedCornerShape(14.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, PocketPalOrange.copy(alpha = 0.3f))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("🌐", fontSize = 26.sp)
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text("PalsHub Community", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                Text("Каталог открытых персон PocketPal AI для мгновенного добавления на устройство.", color = TextSecondary, fontSize = 11.sp)
                            }
                        }
                    }
                }

                val filteredCommunity = communityPals.filter {
                    searchQuery.isBlank() || it.name.contains(searchQuery, ignoreCase = true) || it.description.contains(searchQuery, ignoreCase = true)
                }

                items(filteredCommunity) { communityPal ->
                    CommunityPalCard(
                        pal = communityPal,
                        onInstall = {
                            onCreatePal(
                                communityPal.name,
                                communityPal.description,
                                communityPal.systemPrompt,
                                communityPal.temperature,
                                0.9f,
                                4096,
                                communityPal.category
                            )
                            selectedTab = 0
                        }
                    )
                }
            }
        }
    }

    if (showCreateDialog) {
        CreatePalDialog(
            onDismiss = { showCreateDialog = false },
            onSave = { name, desc, prompt, temp, topP, ctx, cat ->
                onCreatePal(name, desc, prompt, temp, topP, ctx, cat)
                showCreateDialog = false
            }
        )
    }
}

@Composable
fun SpecialFeatureChip(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    color: Color,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(160.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurface1),
        border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.4f))
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(color.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(14.dp))
                }
                Spacer(modifier = Modifier.width(6.dp))
                Text(title, color = TextPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold, maxLines = 1)
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(subtitle, color = TextSecondary, fontSize = 9.sp, maxLines = 1)
        }
    }
}

@Composable
fun PalCard(
    template: PromptTemplate,
    isActive: Boolean,
    onSelect: () -> Unit,
    onStartChat: () -> Unit,
    onDelete: (() -> Unit)? = null
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSelect() },
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isActive) DarkSurface2 else DarkSurface1
        ),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isActive) PocketPalOrange else DarkBorder
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(
                                if (isActive) PocketPalOrange.copy(alpha = 0.25f) else DarkSurface3
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = when {
                                template.name.contains("DeepSeek", ignoreCase = true) -> "🧠"
                                template.name.contains("C++", ignoreCase = true) || template.name.contains("Code", ignoreCase = true) -> "💻"
                                template.name.contains("Math", ignoreCase = true) -> "📐"
                                template.name.contains("Writer", ignoreCase = true) || template.name.contains("Сценарист", ignoreCase = true) -> "📝"
                                else -> "🦊"
                            },
                            fontSize = 20.sp
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = template.name,
                                color = TextPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                            if (isActive) {
                                Spacer(modifier = Modifier.width(6.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(PocketPalOrange)
                                        .padding(horizontal = 5.dp, vertical = 1.dp)
                                ) {
                                    Text("АКТИВЕН", color = Color.White, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        Text(
                            text = template.category,
                            color = TextMuted,
                            fontSize = 11.sp
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (onDelete != null) {
                        IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
                            Icon(Icons.Filled.Delete, contentDescription = "Удалить", tint = TextMuted, modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = template.description,
                color = TextSecondary,
                fontSize = 11.sp,
                maxLines = 2
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Badges
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(DarkSurface3)
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text("T=${template.temperature}", color = TextSecondary, fontSize = 10.sp)
                    }
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(DarkSurface3)
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text("Ctx=${template.contextWindow}", color = TextSecondary, fontSize = 10.sp)
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    OutlinedButton(
                        onClick = onSelect,
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = if (isActive) PocketPalOrange else TextSecondary
                        ),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.height(30.dp)
                    ) {
                        Text(if (isActive) "Выбран" else "Выбрать", fontSize = 11.sp)
                    }

                    Button(
                        onClick = onStartChat,
                        colors = ButtonDefaults.buttonColors(containerColor = PocketPalOrange),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.height(30.dp)
                    ) {
                        Icon(Icons.Filled.Chat, contentDescription = null, modifier = Modifier.size(12.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Чат", fontSize = 11.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun CommunityPalCard(
    pal: CommunityPal,
    onInstall: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurface1),
        border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(DarkSurface2),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(pal.emoji, fontSize = 20.sp)
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(pal.name, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Text("от ${pal.author} • ${pal.category}", color = TextMuted, fontSize = 10.sp)
                    }
                }

                Button(
                    onClick = onInstall,
                    colors = ButtonDefaults.buttonColors(containerColor = DarkSurface3),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.height(32.dp)
                ) {
                    Icon(Icons.Filled.Download, contentDescription = null, tint = PocketPalOrange, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Установить", color = TextPrimary, fontSize = 11.sp)
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(pal.description, color = TextSecondary, fontSize = 11.sp)

            Spacer(modifier = Modifier.height(8.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("🔥 ${pal.downloads} загрузок", color = TextMuted, fontSize = 10.sp)
                Spacer(modifier = Modifier.width(12.dp))
                Text("Температура: ${pal.temperature}", color = TextMuted, fontSize = 10.sp)
            }
        }
    }
}

@Composable
fun CreatePalDialog(
    onDismiss: () -> Unit,
    onSave: (name: String, desc: String, prompt: String, temp: Float, topP: Float, ctx: Int, category: String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var systemPrompt by remember { mutableStateOf("") }
    var temperature by remember { mutableFloatStateOf(0.7f) }
    var topP by remember { mutableFloatStateOf(0.9f) }
    var contextWindow by remember { mutableIntStateOf(4096) }
    var category by remember { mutableStateOf("Пользовательские") }

    val emojis = listOf("🦊", "🧠", "💻", "🤖", "🎨", "🧙", "🐧", "📝", "⚡", "🧘")
    var selectedEmoji by remember { mutableStateOf("🦊") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Новый Pal персонаж", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 16.sp)
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text("Выберите аватар:", color = TextSecondary, fontSize = 11.sp)
                Spacer(modifier = Modifier.height(4.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(emojis) { em ->
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(if (selectedEmoji == em) PocketPalOrange.copy(alpha = 0.3f) else DarkSurface2)
                                .border(if (selectedEmoji == em) 1.5.dp else 0.5.dp, if (selectedEmoji == em) PocketPalOrange else DarkBorder, CircleShape)
                                .clickable { selectedEmoji = em },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(em, fontSize = 18.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Имя Pal", fontSize = 11.sp) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Краткое описание", fontSize = 11.sp) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = systemPrompt,
                    onValueChange = { systemPrompt = it },
                    label = { Text("Системный промпт / Характер", fontSize = 11.sp) },
                    maxLines = 4,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text("Креативность (Температура: ${String.format("%.2f", temperature)})", color = TextSecondary, fontSize = 11.sp)
                Slider(
                    value = temperature,
                    onValueChange = { temperature = it },
                    valueRange = 0.1f..1.5f,
                    colors = SliderDefaults.colors(thumbColor = PocketPalOrange, activeTrackColor = PocketPalOrange)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank()) {
                        val fullName = "$selectedEmoji $name"
                        onSave(fullName, description, systemPrompt, temperature, topP, contextWindow, category)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = PocketPalOrange),
                enabled = name.isNotBlank()
            ) {
                Text("Создать Pal")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("Отмена", color = TextSecondary)
            }
        },
        containerColor = DarkSurface1
    )
}
