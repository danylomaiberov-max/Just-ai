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
    var searchQuery by remember { mutableStateOf("Local LLM architecture and quantization") }
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
                            Text("Local Vector Database & RAG", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }

                        Button(
                            onClick = {
                                onCreateCollection(
                                    "Project Docs #${collections.size + 1}",
                                    "Local knowledge base with dense semantic vectors"
                                )
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = CyanNeon, contentColor = DarkVoid),
                            shape = RoundedCornerShape(6.dp),
                            modifier = Modifier.height(30.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("New Base", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Indexes documents on-device with 64-dim dense embeddings for context augmentation.",
                        color = TextSecondary,
                        fontSize = 11.sp
                    )
                }
            }
        }

        // Knowledge Base Collections
        item {
            Text("Knowledge Base Collections (${collections.size}):", color = TextSecondary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(6.dp))

            if (collections.isEmpty()) {
                Text("No collections created yet. Tap '+ New Base' to begin indexing.", color = TextMuted, fontSize = 11.sp)
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    collections.forEach { col ->
                        val isSelected = col.id == selectedCollectionId
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) PurpleNeon.copy(alpha = 0.2f) else DarkSurface2)
                                .border(1.dp, if (isSelected) PurpleNeon else DarkBorder, RoundedCornerShape(8.dp))
                                .clickable { selectedCollectionId = col.id }
                                .padding(10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Book, contentDescription = null, tint = if (isSelected) PurpleNeon else TextSecondary, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(col.name, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                    Text(col.description, color = TextSecondary, fontSize = 10.sp)
                                }
                            }

                            Row {
                                IconButton(onClick = { onDeleteCollection(col.id) }, modifier = Modifier.size(28.dp)) {
                                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = TextMuted, modifier = Modifier.size(16.dp))
                                }
                            }
                        }
                    }
                }
            }
        }

        // Ingest New Document Form
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = DarkSurface2),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("📥 Ingest Document into Vector Store", color = PurpleNeon, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = newDocTitle,
                        onValueChange = { newDocTitle = it },
                        placeholder = { Text("Document Title (e.g. Model Guide, API Spec)") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PurpleNeon,
                            unfocusedBorderColor = DarkBorder,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        ),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = newDocContent,
                        onValueChange = { newDocContent = it },
                        placeholder = { Text("Paste text or notes to auto-chunk and embed...") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PurpleNeon,
                            unfocusedBorderColor = DarkBorder,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        ),
                        maxLines = 4
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Button(
                        onClick = {
                            if (newDocTitle.isNotBlank() && newDocContent.isNotBlank() && selectedCollectionId != null) {
                                onAddDocument(selectedCollectionId!!, newDocTitle, newDocContent)
                                newDocTitle = ""
                                newDocContent = ""
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = PurpleNeon),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Chunk & Generate Vector Embeddings", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            }
        }

        // Semantic Vector Search Tester
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = DarkSurface2),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, CyanNeon.copy(alpha = 0.4f))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("🔎 Semantic Vector Search (Cosine Similarity)", color = CyanNeon, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(modifier = Modifier.fillMaxWidth()) {
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            placeholder = { Text("Enter semantic query...") },
                            modifier = Modifier.weight(1f),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = CyanNeon,
                                unfocusedBorderColor = DarkBorder,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary
                            ),
                            singleLine = true
                        )

                        Spacer(modifier = Modifier.width(6.dp))

                        Button(
                            onClick = { onTestSearch(searchQuery, selectedCollectionId) },
                            colors = ButtonDefaults.buttonColors(containerColor = CyanNeon, contentColor = DarkVoid),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(Icons.Default.Search, contentDescription = null, modifier = Modifier.size(16.dp))
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    if (searchResults.isNotEmpty()) {
                        Text("Top Retrieved Chunks (${searchResults.size}):", color = TextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(6.dp))

                        searchResults.forEach { result ->
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
                                        Text(result.documentTitle, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                        Text(
                                            "Match: ${(result.similarityScore * 100).toInt()}%",
                                            color = if (result.similarityScore > 0.7f) EmeraldAi else CyanNeon,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 11.sp
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    LinearProgressIndicator(
                                        progress = { result.similarityScore },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(3.dp)
                                            .clip(RoundedCornerShape(2.dp)),
                                        color = if (result.similarityScore > 0.7f) EmeraldAi else CyanNeon,
                                        trackColor = DarkVoid
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(result.chunkText, color = TextSecondary, fontSize = 11.sp, lineHeight = 16.sp)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
