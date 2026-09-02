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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FileOpen
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.database.AiModelEntity
import com.example.models.HuggingFaceModelCard
import com.example.models.ModelManager
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
fun ModelsHubScreen(
    installedModels: List<AiModelEntity>,
    downloadingMap: Map<String, Int>,
    onDownloadModel: (HuggingFaceModelCard, String) -> Unit,
    onImportLocalModel: (String, Long, String) -> Unit,
    onLoadModel: (String) -> Unit,
    onDeleteModel: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableIntStateOf(0) } // 0: Installed, 1: HuggingFace Hub
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("All") }
    var showImportDialog by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkVoid)
            .testTag("models_hub_screen")
    ) {
        // Top Tab Switcher
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = DarkSurface1,
            contentColor = CyanNeon,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                    color = CyanNeon
                )
            }
        ) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = { Text("Local Models (${installedModels.size})", fontWeight = FontWeight.Bold, fontSize = 12.sp) },
                icon = { Icon(Icons.Default.Storage, contentDescription = null, modifier = Modifier.size(16.dp)) },
                modifier = Modifier.testTag("tab_local_models")
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = { Text("Hugging Face Hub", fontWeight = FontWeight.Bold, fontSize = 12.sp) },
                icon = { Icon(Icons.Default.CloudDownload, contentDescription = null, modifier = Modifier.size(16.dp)) },
                modifier = Modifier.testTag("tab_huggingface_hub")
            )
        }

        // Search & Category Filters
        Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search GGUF, SafeTensors, Llama, DeepSeek...", fontSize = 12.sp, color = TextMuted) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(18.dp)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("model_search_input"),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = CyanNeon,
                    unfocusedBorderColor = DarkBorder,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary,
                    focusedContainerColor = DarkSurface2,
                    unfocusedContainerColor = DarkSurface2
                ),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(8.dp))

            LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                items(listOf("All", "LLM", "Vision", "Audio", "Code")) { cat ->
                    val isSelected = cat == selectedCategory
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSelected) CyanNeon.copy(alpha = 0.2f) else DarkSurface2)
                            .border(1.dp, if (isSelected) CyanNeon else DarkBorder, RoundedCornerShape(8.dp))
                            .clickable { selectedCategory = cat }
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = cat,
                            color = if (isSelected) CyanNeon else TextSecondary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }

        if (selectedTab == 0) {
            // Local Installed Models List
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                item {
                    // Import From Device Storage Button
                    Button(
                        onClick = {
                            // Simulate Storage Access Framework import
                            onImportLocalModel(
                                "custom-mistral-nemo-12b.gguf",
                                4800,
                                "/storage/emulated/0/Download/custom-mistral-nemo-12b.gguf"
                            )
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = DarkSurface2),
                        border = androidx.compose.foundation.BorderStroke(1.dp, PurpleNeon),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("import_storage_button")
                    ) {
                        Icon(Icons.Default.FileOpen, contentDescription = null, tint = PurpleNeon, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Import GGUF / SafeTensors from Phone Storage", color = PurpleNeon, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }

                val filtered = installedModels.filter {
                    (selectedCategory == "All" || it.architecture.contains(selectedCategory, ignoreCase = true)) &&
                    (searchQuery.isBlank() || it.name.contains(searchQuery, ignoreCase = true) || it.id.contains(searchQuery, ignoreCase = true))
                }

                items(filtered) { model ->
                    InstalledModelCard(
                        model = model,
                        onLoad = { onLoadModel(model.id) },
                        onDelete = { onDeleteModel(model.id) }
                    )
                }
            }
        } else {
            // HuggingFace Hub Explorer
            val hfModels = ModelManager.popularHuggingFaceModels.filter {
                (selectedCategory == "All" || it.category.contains(selectedCategory, ignoreCase = true)) &&
                (searchQuery.isBlank() || it.title.contains(searchQuery, ignoreCase = true) || it.author.contains(searchQuery, ignoreCase = true))
            }

            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(hfModels) { card ->
                    val modelId = "${card.repoId.substringAfter("/")}-q4_k_m"
                    val progress = downloadingMap[modelId]
                    val isInstalled = installedModels.any { it.id.contains(card.repoId.substringAfter("/"), ignoreCase = true) && it.isDownloaded }

                    HuggingFaceCardView(
                        card = card,
                        isInstalled = isInstalled,
                        downloadProgress = progress,
                        onDownload = { quant -> onDownloadModel(card, quant) }
                    )
                }
            }
        }
    }
}

@Composable
fun InstalledModelCard(
    model: AiModelEntity,
    onLoad: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = DarkSurface2),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (model.isLoadedInRam) CyanNeon else DarkBorder
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(if (model.isLoadedInRam) EmeraldAi else TextMuted)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = model.name,
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }

                if (model.isLoadedInRam) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(CyanNeon.copy(alpha = 0.2f))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text("ACTIVE IN RAM", color = CyanNeon, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "${model.architecture} • Quant: ${model.quantization} • Size: ${model.fileSizeMb} MB • Context: ${model.contextWindow} tok",
                color = TextSecondary,
                fontSize = 11.sp,
                fontFamily = FontFamily.Monospace
            )

            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = model.description,
                color = TextMuted,
                fontSize = 11.sp,
                maxLines = 2
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Memory, contentDescription = null, tint = PurpleNeon, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Vulkan Layers: ${model.gpuOffloadLayers}", color = TextSecondary, fontSize = 10.sp)
                }

                Row {
                    IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = TextMuted, modifier = Modifier.size(16.dp))
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Button(
                        onClick = onLoad,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (model.isLoadedInRam) DarkSurface3 else CyanNeon,
                            contentColor = if (model.isLoadedInRam) TextSecondary else DarkVoid
                        ),
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier.height(32.dp)
                    ) {
                        Text(if (model.isLoadedInRam) "Loaded" else "Load to RAM", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun HuggingFaceCardView(
    card: HuggingFaceModelCard,
    isInstalled: Boolean,
    downloadProgress: Int?,
    onDownload: (String) -> Unit
) {
    var selectedQuant by remember { mutableStateOf(card.availableQuantizations.first()) }

    Card(
        colors = CardDefaults.cardColors(containerColor = DarkSurface2),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(card.title, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Text("HF: ${card.repoId}", color = CyanNeon, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(DarkSurface3)
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text("📥 ${card.downloads}", color = TextSecondary, fontSize = 10.sp)
                }
            }

            Spacer(modifier = Modifier.height(6.dp))
            Text(card.description, color = TextSecondary, fontSize = 11.sp, maxLines = 2)

            Spacer(modifier = Modifier.height(8.dp))

            // Quantization Chips
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    items(card.availableQuantizations) { quant ->
                        val isSelected = quant == selectedQuant
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(if (isSelected) PurpleNeon.copy(alpha = 0.3f) else DarkSurface3)
                                .border(1.dp, if (isSelected) PurpleNeon else DarkBorder, RoundedCornerShape(4.dp))
                                .clickable { selectedQuant = quant }
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(quant, color = if (isSelected) PurpleNeon else TextMuted, fontSize = 10.sp)
                        }
                    }
                }

                if (isInstalled) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = EmeraldAi, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Installed", color = EmeraldAi, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                } else if (downloadProgress != null) {
                    Column(horizontalAlignment = Alignment.End) {
                        Text("Downloading $downloadProgress%", color = CyanNeon, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        LinearProgressIndicator(
                            progress = { downloadProgress / 100f },
                            modifier = Modifier
                                .width(90.dp)
                                .height(4.dp)
                                .clip(RoundedCornerShape(2.dp)),
                            color = CyanNeon,
                            trackColor = DarkSurface3
                        )
                    }
                } else {
                    Button(
                        onClick = { onDownload(selectedQuant) },
                        colors = ButtonDefaults.buttonColors(containerColor = CyanNeon, contentColor = DarkVoid),
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier.height(30.dp)
                    ) {
                        Icon(Icons.Default.CloudDownload, contentDescription = null, modifier = Modifier.size(12.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Pull (${card.defaultSizeMb}MB)", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
