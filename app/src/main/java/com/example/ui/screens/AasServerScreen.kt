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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ClearAll
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Dns
import androidx.compose.material.icons.filled.Http
import androidx.compose.material.icons.filled.Lan
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.database.ServerLogEntity
import com.example.server.AasServerManager
import com.example.server.ServerStatus
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
fun AasServerScreen(
    serverStatus: ServerStatus,
    recentLogs: List<ServerLogEntity>,
    onToggleServer: (Boolean) -> Unit,
    onClearLogs: () -> Unit,
    modifier: Modifier = Modifier
) {
    val clipboardManager = LocalClipboardManager.current

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(DarkVoid)
            .padding(14.dp)
            .testTag("aas_server_screen"),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Main Server Status Card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = DarkSurface2),
                shape = RoundedCornerShape(14.dp),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (serverStatus.isRunning) EmeraldAi else DarkBorder
                )
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(12.dp)
                                    .clip(CircleShape)
                                    .background(if (serverStatus.isRunning) EmeraldAi else TextMuted)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = if (serverStatus.isRunning) "AAS REST API: RUNNING" else "AAS REST API: STOPPED",
                                    color = if (serverStatus.isRunning) EmeraldAi else TextMuted,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                                Text(
                                    text = "Ollama & OpenAI Compatible Local Endpoints",
                                    color = TextSecondary,
                                    fontSize = 11.sp
                                )
                            }
                        }

                        Switch(
                            checked = serverStatus.isRunning,
                            onCheckedChange = onToggleServer,
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = DarkVoid,
                                checkedTrackColor = EmeraldAi,
                                uncheckedThumbColor = TextMuted,
                                uncheckedTrackColor = DarkSurface3
                            ),
                            modifier = Modifier.testTag("server_toggle_switch")
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Host & Port Display
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(DarkSurface3)
                            .border(1.dp, DarkBorder, RoundedCornerShape(8.dp))
                            .padding(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("Active Gateway URL:", color = TextSecondary, fontSize = 10.sp)
                                Text(
                                    text = "http://${serverStatus.host}:${serverStatus.port}",
                                    color = CyanNeon,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                            }
                            IconButton(onClick = {
                                clipboardManager.setText(AnnotatedString("http://${serverStatus.host}:${serverStatus.port}"))
                            }) {
                                Icon(Icons.Default.ContentCopy, contentDescription = "Copy", tint = CyanNeon, modifier = Modifier.size(16.dp))
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Metrics Grid (Requests, Tokens, Latency)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        MetricCard(
                            label = "Requests",
                            value = "${serverStatus.totalRequests}",
                            sublabel = "Total calls",
                            modifier = Modifier.weight(1f)
                        )
                        MetricCard(
                            label = "Tokens",
                            value = "${serverStatus.totalTokensServed}",
                            sublabel = "Served",
                            modifier = Modifier.weight(1f)
                        )
                        MetricCard(
                            label = "Latency",
                            value = "${serverStatus.averageLatencyMs}ms",
                            sublabel = "Avg response",
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        // Quick CURL Test Snippets
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = DarkSurface2),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Terminal, contentDescription = null, tint = PurpleNeon, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Terminal Integration (CURL Snippets)", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text("Ollama /api/chat Endpoint:", color = TextSecondary, fontSize = 11.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    CodeSnippetCard(code = AasServerManager.getSampleOllamaCurl(), onCopy = { clipboardManager.setText(AnnotatedString(it)) })

                    Spacer(modifier = Modifier.height(10.dp))

                    Text("OpenAI /v1/chat/completions Endpoint:", color = TextSecondary, fontSize = 11.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    CodeSnippetCard(code = AasServerManager.getSampleOpenAiCurl(), onCopy = { clipboardManager.setText(AnnotatedString(it)) })
                }
            }
        }

        // Live Server Request Logs
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
                            Icon(Icons.Default.Http, contentDescription = null, tint = CyanNeon, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Live Request Inspector (${recentLogs.size})", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }

                        IconButton(onClick = onClearLogs, modifier = Modifier.size(28.dp)) {
                            Icon(Icons.Default.ClearAll, contentDescription = "Clear", tint = TextMuted, modifier = Modifier.size(18.dp))
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    if (recentLogs.isEmpty()) {
                        Text("No external API requests recorded yet.", color = TextMuted, fontSize = 11.sp)
                    } else {
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            recentLogs.take(5).forEach { log ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(DarkSurface3)
                                        .padding(horizontal = 8.dp, vertical = 6.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(4.dp))
                                                .background(EmeraldAi.copy(alpha = 0.2f))
                                                .padding(horizontal = 4.dp, vertical = 1.dp)
                                        ) {
                                            Text(log.method, color = EmeraldAi, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                        }
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(log.endpoint, color = TextPrimary, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                                    }

                                    Text(
                                        "${log.latencyMs}ms • ${log.tokensProcessed} tok",
                                        color = TextSecondary,
                                        fontSize = 10.sp,
                                        fontFamily = FontFamily.Monospace
                                    )
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
fun MetricCard(
    label: String,
    value: String,
    sublabel: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(DarkSurface3)
            .padding(8.dp)
    ) {
        Column {
            Text(label, color = TextSecondary, fontSize = 10.sp)
            Spacer(modifier = Modifier.height(2.dp))
            Text(value, color = CyanNeon, fontWeight = FontWeight.Bold, fontSize = 14.sp, fontFamily = FontFamily.Monospace)
            Text(sublabel, color = TextMuted, fontSize = 9.sp)
        }
    }
}

@Composable
fun CodeSnippetCard(
    code: String,
    onCopy: (String) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(6.dp))
            .background(DarkVoid)
            .border(1.dp, DarkBorder, RoundedCornerShape(6.dp))
            .clickable { onCopy(code) }
            .padding(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = code,
                color = TextSecondary,
                fontSize = 10.sp,
                fontFamily = FontFamily.Monospace,
                lineHeight = 14.sp,
                modifier = Modifier.weight(1f)
            )
            Icon(
                imageVector = Icons.Default.ContentCopy,
                contentDescription = "Copy",
                tint = CyanNeon,
                modifier = Modifier
                    .size(16.dp)
                    .padding(start = 4.dp)
            )
        }
    }
}
