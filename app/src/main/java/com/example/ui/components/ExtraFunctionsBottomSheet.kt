package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Dns
import androidx.compose.material.icons.filled.FindInPage
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.AppTab
import com.example.ui.theme.CrimsonNeon
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExtraFunctionsBottomSheet(
    onDismissRequest: () -> Unit,
    onNavigateToTab: (AppTab) -> Unit,
    modifier: Modifier = Modifier,
    sheetState: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
) {
    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
        containerColor = DarkSurface1,
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(top = 10.dp, bottom = 4.dp)
                    .size(width = 42.dp, height = 4.dp)
                    .clip(CircleShape)
                    .background(CrimsonNeon.copy(alpha = 0.5f))
            )
        },
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        modifier = modifier.testTag("extra_functions_bottom_sheet")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(
                                Brush.linearGradient(listOf(CrimsonNeon, PurpleNeon))
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Text(
                            text = "Дополнительные функции",
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp
                        )
                        Text(
                            text = "Автономные встроенные инструменты PocketPal AI",
                            color = TextSecondary,
                            fontSize = 11.sp
                        )
                    }
                }

                IconButton(
                    onClick = onDismissRequest,
                    modifier = Modifier.testTag("close_tools_sheet_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Закрыть",
                        tint = TextSecondary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 1. MultiModal Studio
            FunctionSheetCard(
                title = "Студия Мультимедиа",
                badge = "VISION • TTS • TRANSLATE",
                badgeColor = CrimsonNeon,
                description = "Локальный OCR-анализ изображений, синтез речи нейро-голосом (TTS), офлайн-переводчик и концепт-визуализации.",
                icon = Icons.Default.AutoAwesome,
                testTag = "sheet_btn_studio",
                onClick = {
                    onDismissRequest()
                    onNavigateToTab(AppTab.STUDIO_MULTIMODAL)
                }
            )

            Spacer(modifier = Modifier.height(10.dp))

            // 2. Code Sandbox IDE
            FunctionSheetCard(
                title = "Код Sandbox IDE",
                badge = "C++ • RUST • PYTHON • JS",
                badgeColor = PurpleNeon,
                description = "Песочница и локальный компилятор кода без обращения к серверу. Подсветка синтаксиса, запуск алгоритмов и терминал вывода.",
                icon = Icons.Default.Code,
                testTag = "sheet_btn_code",
                onClick = {
                    onDismissRequest()
                    onNavigateToTab(AppTab.CODE_IDE)
                }
            )

            Spacer(modifier = Modifier.height(10.dp))

            // 3. Vector RAG Knowledge Base
            FunctionSheetCard(
                title = "Векторная RAG Память",
                badge = "LOCAL EMBEDDINGS",
                badgeColor = EmeraldAi,
                description = "Семантический поиск по персональным документам, PDF и заметкам. Автономная индексация и контекстное обогащение ответов модели.",
                icon = Icons.Default.FindInPage,
                testTag = "sheet_btn_rag",
                onClick = {
                    onDismissRequest()
                    onNavigateToTab(AppTab.VECTOR_RAG)
                }
            )

            Spacer(modifier = Modifier.height(10.dp))

            // 4. Local API Server
            FunctionSheetCard(
                title = "Локальный API Сервер",
                badge = "OLLAMA & OPENAI REST :11434",
                badgeColor = CrimsonNeon,
                description = "Встроенный HTTP-сервер на устройстве. Позволяет подключать ПК, веб-браузер или сторонние клиенты к локальной модели через Wi-Fi.",
                icon = Icons.Default.Dns,
                testTag = "sheet_btn_server",
                onClick = {
                    onDismissRequest()
                    onNavigateToTab(AppTab.AAS_SERVER)
                }
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Quick Shortcut Row to Benchmark & Settings
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                QuickNavTile(
                    title = "Бенчмарк",
                    subtitle = "Тест скорости железа",
                    icon = Icons.Default.Speed,
                    modifier = Modifier.weight(1f),
                    onClick = {
                        onDismissRequest()
                        onNavigateToTab(AppTab.HARDWARE_RUNNER)
                    }
                )

                QuickNavTile(
                    title = "Pals Хаб",
                    subtitle = "Персонажи и роли",
                    icon = Icons.Default.Psychology,
                    modifier = Modifier.weight(1f),
                    onClick = {
                        onDismissRequest()
                        onNavigateToTab(AppTab.PALS)
                    }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun FunctionSheetCard(
    title: String,
    badge: String,
    badgeColor: Color,
    description: String,
    icon: ImageVector,
    testTag: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag(testTag),
        colors = CardDefaults.cardColors(containerColor = DarkSurface2),
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(1.dp, CrimsonNeon.copy(alpha = 0.35f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(DarkSurface3)
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = badgeColor,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = title,
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(badgeColor.copy(alpha = 0.15f))
                            .padding(horizontal = 5.dp, vertical = 1.dp)
                    ) {
                        Text(
                            text = badge,
                            color = badgeColor,
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(3.dp))

                Text(
                    text = description,
                    color = TextSecondary,
                    fontSize = 11.sp,
                    lineHeight = 15.sp
                )
            }

            Spacer(modifier = Modifier.width(6.dp))

            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = TextMuted,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
private fun QuickNavTile(
    title: String,
    subtitle: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = DarkSurface3),
        shape = RoundedCornerShape(10.dp),
        border = BorderStroke(0.5.dp, DarkBorder)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = CrimsonNeon,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(
                    text = title,
                    color = TextPrimary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = subtitle,
                    color = TextMuted,
                    fontSize = 9.sp
                )
            }
        }
    }
}
