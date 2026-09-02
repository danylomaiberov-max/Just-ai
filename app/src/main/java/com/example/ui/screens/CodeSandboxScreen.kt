package com.example.ui.screens

import android.webkit.WebView
import android.webkit.WebViewClient
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Web
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.compiler.ExecutionResult
import com.example.compiler.MultiLangCompilerEngine
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
fun CodeSandboxScreen(
    selectedLanguage: String,
    codeBuffer: String,
    executionResult: ExecutionResult?,
    isCompiling: Boolean,
    onLanguageSelected: (String) -> Unit,
    onCodeChanged: (String) -> Unit,
    onRunCode: () -> Unit,
    modifier: Modifier = Modifier
) {
    val clipboardManager = LocalClipboardManager.current
    var showWebPreview by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(DarkVoid)
            .padding(14.dp)
            .testTag("code_sandbox_screen"),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Language Selector Bar
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = DarkSurface2),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder)
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Text("ЯЗЫК ПРОГРАММИРОВАНИЯ", color = TextSecondary, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(6.dp))

                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(MultiLangCompilerEngine.supportedLanguages) { langPair ->
                            val isSelected = selectedLanguage.equals(langPair.first, ignoreCase = true)
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSelected) CyanNeon else DarkSurface3)
                                    .clickable { onLanguageSelected(langPair.first) }
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = langPair.first.uppercase(),
                                    color = if (isSelected) DarkVoid else TextPrimary,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        }
                    }
                }
            }
        }

        // Code Editor Box
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = DarkSurface1),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, CyanNeon.copy(alpha = 0.4f))
            ) {
                Column {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(DarkSurface2)
                            .padding(horizontal = 12.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Code, contentDescription = null, tint = CyanNeon, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("main.${selectedLanguage}", color = TextPrimary, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                        }

                        IconButton(
                            onClick = { clipboardManager.setText(AnnotatedString(codeBuffer)) },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(Icons.Default.ContentCopy, contentDescription = "Копировать", tint = TextMuted, modifier = Modifier.size(14.dp))
                        }
                    }

                    OutlinedTextField(
                        value = codeBuffer,
                        onValueChange = onCodeChanged,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("code_editor_input"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = EmeraldAi,
                            unfocusedTextColor = EmeraldAi,
                            focusedContainerColor = DarkVoid,
                            unfocusedContainerColor = DarkVoid,
                            focusedBorderColor = Color.Transparent,
                            unfocusedBorderColor = Color.Transparent
                        ),
                        textStyle = androidx.compose.ui.text.TextStyle(
                            fontFamily = FontFamily.Monospace,
                            fontSize = 12.sp,
                            lineHeight = 18.sp
                        ),
                        minLines = 8,
                        maxLines = 16
                    )
                }
            }
        }

        // Action Run Button
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onRunCode,
                    enabled = !isCompiling && codeBuffer.isNotBlank(),
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp)
                        .testTag("run_code_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldAi),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    if (isCompiling) {
                        CircularProgressIndicator(modifier = Modifier.size(18.dp), color = DarkVoid, strokeWidth = 2.dp)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Компиляция...", color = DarkVoid, fontWeight = FontWeight.Bold)
                    } else {
                        Icon(Icons.Default.PlayArrow, contentDescription = null, tint = DarkVoid, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Запустить код", color = DarkVoid, fontWeight = FontWeight.Bold)
                    }
                }

                if (selectedLanguage == "html") {
                    Button(
                        onClick = { showWebPreview = !showWebPreview },
                        colors = ButtonDefaults.buttonColors(containerColor = DarkSurface2),
                        shape = RoundedCornerShape(10.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, CyanNeon.copy(alpha = 0.5f))
                    ) {
                        Icon(Icons.Default.Web, contentDescription = null, tint = CyanNeon, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(if (showWebPreview) "Скрыть" else "Превью", color = CyanNeon, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // HTML Web Preview
        if (selectedLanguage == "html" && showWebPreview) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    AndroidView(
                        factory = { ctx ->
                            WebView(ctx).apply {
                                webViewClient = WebViewClient()
                                settings.javaScriptEnabled = true
                                loadDataWithBaseURL(null, codeBuffer, "text/html", "UTF-8", null)
                            }
                        },
                        update = { view ->
                            view.loadDataWithBaseURL(null, codeBuffer, "text/html", "UTF-8", null)
                        },
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }

        // Terminal Output Result
        if (executionResult != null) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = DarkSurface1),
                    shape = RoundedCornerShape(10.dp),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (executionResult.isSuccess) EmeraldAi.copy(alpha = 0.6f) else AmberWarning
                    )
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                if (executionResult.isSuccess) "ВЫВОД ТЕРМИНАЛА (УСПЕШНО)" else "ОШИБКА КОМПИЛЯЦИИ",
                                color = if (executionResult.isSuccess) EmeraldAi else AmberWarning,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                            Text("${executionResult.executionTimeMs} мс", color = TextMuted, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(6.dp))
                                .background(DarkVoid)
                                .padding(10.dp)
                        ) {
                            Text(
                                text = if (executionResult.isSuccess) executionResult.stdout else executionResult.stderr.ifBlank { executionResult.stdout },
                                color = if (executionResult.isSuccess) TextPrimary else AmberWarning,
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                }
            }
        }
    }
}
