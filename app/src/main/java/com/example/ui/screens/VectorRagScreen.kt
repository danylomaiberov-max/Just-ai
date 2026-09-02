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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FindInPage
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
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
import com.example.data.database.VectorCollectionEntity
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
import com.example.vectordb.SearchResult

@Composable
fun VectorRagScreen(
    collections: List<VectorCollectionEntity>,
    searchResults: List<SearchResult>,
    onCreateCollection: (String, String) -> Unit,
    onDeleteCollection: (Long) -> Unit,
    onAddDocument: (Long, String, String) -> Unit,
    onTestSearch: (String, Long?) -> Unit,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("Квантование GGUF и локальная архитектура") }
    var newDocTitle by remember { mutableStateOf("") }
    var newDocContent by remember { mutableStateOf("") }
    var selectedCollectionId by remember { mutableStateOf<Long?>(collections.firstOrNull()?.id) }
    var isAddingDoc by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(DarkVoid)
            .padding(14.dp)
            .testTag("vector_rag_screen"),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Top Header Card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = DarkSurface2),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.FindInPage, contentDescription = null, tint = CyanNeon, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Локальная векторная база (RAG)", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        "Семантический поиск и долгосрочная память LLM. Документы разбиваются на чанки и векторизуются прямо на устройстве.",
                        color = TextSecondary,
                        fontSize = 11.sp
                    )
                }
            }
        }

        // Semantic Search Query Bar
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = DarkSurface1),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, CyanNeon.copy(alpha = 0.4f))
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text("СЕМАНТИЧЕСКИЙ ПОИСК В ПАМЯТИ", color = CyanNeon, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(6.dp))

                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = { Text("Введите поисковый запрос...", color = TextMuted, fontSize = 12.sp) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = CyanNeon,
                            unfocusedBorderColor = DarkBorder,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            focusedContainerColor = DarkSurface2,
                            unfocusedContainerColor = DarkSurface2
                        ),
                        shape = RoundedCornerShape(8.dp),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Button(
                        onClick = { onTestSearch(searchQuery, selectedCollectionId) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = CyanNeon),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(Icons.Default.Search, contentDescription = null, tint = DarkVoid, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Найти похожие чанки", color = DarkVoid, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            }
        }

        // Search Results List
        if (searchResults.isNotEmpty()) {
            item {
                Text("РЕЗУЛЬТАТЫ ПОИСКА (КОСИНУСНОЕ СХОДСТВО)", color = EmeraldAi, fontSize = 10.sp, fontWeight = FontWeight.Bold)
            }

            items(searchResults) { res ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = DarkSurface2),
                    shape = RoundedCornerShape(8.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, EmeraldAi.copy(alpha = 0.4f))
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Сходство: ${String.format("%.1f%%", res.similarityScore * 100)}", color = EmeraldAi, fontWeight = FontWeight.Bold, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                            Text(res.documentTitle, color = TextMuted, fontSize = 9.sp, fontFamily = FontFamily.Monospace)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(res.chunkText, color = TextPrimary, fontSize = 11.sp)
                    }
                }
            }
        }

        // Collections Section
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("КОЛЛЕКЦИИ ЗНАНИЙ (${collections.size})", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 12.sp)

                Button(
                    onClick = { isAddingDoc = !isAddingDoc },
                    colors = ButtonDefaults.buttonColors(containerColor = DarkSurface2),
                    shape = RoundedCornerShape(8.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, tint = CyanNeon, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(if (isAddingDoc) "Закрыть" else "Добавить документ", color = CyanNeon, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Add Document Form
        if (isAddingDoc) {
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = DarkSurface1),
                    shape = RoundedCornerShape(10.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, PurpleNeon.copy(alpha = 0.5f))
                ) {
                    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("НОВЫЙ ДОКУМЕНТ В БАЗУ ЗНАНИЙ", color = PurpleNeon, fontSize = 11.sp, fontWeight = FontWeight.Bold)

                        OutlinedTextField(
                            value = newDocTitle,
                            onValueChange = { newDocTitle = it },
                            placeholder = { Text("Заголовок документа", color = TextMuted, fontSize = 12.sp) },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = PurpleNeon,
                                unfocusedBorderColor = DarkBorder,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary,
                                focusedContainerColor = DarkSurface2,
                                unfocusedContainerColor = DarkSurface2
                            ),
                            shape = RoundedCornerShape(6.dp),
                            singleLine = true
                        )

                        OutlinedTextField(
                            value = newDocContent,
                            onValueChange = { newDocContent = it },
                            placeholder = { Text("Текст документа для индексации и генерации эмбеддингов...", color = TextMuted, fontSize = 12.sp) },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = PurpleNeon,
                                unfocusedBorderColor = DarkBorder,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary,
                                focusedContainerColor = DarkSurface2,
                                unfocusedContainerColor = DarkSurface2
                            ),
                            shape = RoundedCornerShape(6.dp),
                            minLines = 3
                        )

                        Button(
                            onClick = {
                                val colId = selectedCollectionId ?: collections.firstOrNull()?.id ?: 1L
                                if (newDocTitle.isNotBlank() && newDocContent.isNotBlank()) {
                                    onAddDocument(colId, newDocTitle, newDocContent)
                                    newDocTitle = ""
                                    newDocContent = ""
                                    isAddingDoc = false
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = PurpleNeon),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Индексировать и сохранить", color = DarkVoid, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                }
            }
        }

        // Collections List
        items(collections) { col ->
            val isSelected = selectedCollectionId == col.id
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { selectedCollectionId = col.id },
                colors = CardDefaults.cardColors(containerColor = DarkSurface1),
                shape = RoundedCornerShape(10.dp),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (isSelected) CyanNeon else DarkBorder
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Book, contentDescription = null, tint = if (isSelected) CyanNeon else TextSecondary, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(col.name, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                        Text(col.description, color = TextSecondary, fontSize = 10.sp)
                    }

                    IconButton(
                        onClick = { onDeleteCollection(col.id) },
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = "Удалить", tint = TextMuted, modifier = Modifier.size(14.dp))
                    }
                }
            }
        }
    }
}
