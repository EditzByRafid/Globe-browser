package com.example.ui.components

import android.webkit.WebView
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.model.BrowserSettings
import com.example.model.FileLabItem
import com.example.ui.theme.GlobePalettes
import com.example.ui.theme.chromeCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FileLabSheet(
    browserSettings: BrowserSettings,
    onDismiss: () -> Unit
) {
    val sampleFiles = remember {
        mutableStateListOf(
            FileLabItem(
                id = "1",
                filename = "index.html",
                extension = "html",
                content = """<!DOCTYPE html>
<html>
<head>
  <style>
    body { font-family: sans-serif; background: #0b1329; color: #00E5FF; text-align: center; padding: 40px; }
    h1 { font-size: 28px; text-shadow: 0 0 12px rgba(0,229,255,0.6); }
    .btn { background: #00E5FF; color: black; border: none; padding: 10px 20px; border-radius: 8px; font-weight: bold; cursor: pointer; }
  </style>
</head>
<body>
  <h1>Globe Browser File Lab</h1>
  <p>Live sandboxed HTML/CSS/JS runtime running on your Android device!</p>
  <button class="btn" onclick="alert('Hello from Globe Sandbox!')">Click Me</button>
</body>
</html>"""
            ),
            FileLabItem(
                id = "2",
                filename = "Solution.kt",
                extension = "kt",
                content = """package com.example.algorithm

// Globe Browser High-Performance Thread Safe LRU Cache
class LruCache<K, V>(private val capacity: Int) {
    private val map = LinkedHashMap<K, V>(capacity, 0.75f, true)

    @Synchronized
    fun get(key: K): V? = map[key]

    @Synchronized
    fun put(key: K, value: V) {
        if (map.size >= capacity && !map.containsKey(key)) {
            val eldest = map.entries.iterator().next().key
            map.remove(eldest)
        }
        map[key] = value
    }
}"""
            ),
            FileLabItem(
                id = "3",
                filename = "notes.md",
                extension = "md",
                content = """# Globe Browser Architecture

## Key Design Principles
* **Liquid Glass UI**: Glassmorphism with fallback for Realme Note 60.
* **Ad & Tracker Blocker**: Local interception before network fetch.
* **File Lab**: Multi-format source viewer and sandbox runner.
* **Large Database**: 700 accounts and 500 person profiles in Room.
"""
            ),
            FileLabItem(
                id = "4",
                filename = "script.py",
                extension = "py",
                content = """import math

def calculate_spherical_distance(lat1, lon1, lat2, lon2):
    # Haversine formula for Globe navigation
    R = 6371.0 # Earth radius in km
    dlat = math.radians(lat2 - lat1)
    dlon = math.radians(lon2 - lon1)
    a = math.sin(dlat / 2)**2 + math.cos(math.radians(lat1)) * math.cos(math.radians(lat2)) * math.sin(dlon / 2)**2
    c = 2 * math.atan2(math.sqrt(a), math.sqrt(1 - a))
    return R * c

print("Distance:", calculate_spherical_distance(37.7749, -122.4194, 34.0522, -118.2437))
"""
            )
        )
    }

    var activeFileId by remember { mutableStateOf(sampleFiles.first().id) }
    var showLivePreview by remember { mutableStateOf(false) }
    val activeFile = sampleFiles.find { it.id == activeFileId } ?: sampleFiles.first()

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = MaterialTheme.colorScheme.background,
        dragHandle = { BottomSheetDefaults.DragHandle() }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 4.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(
                        imageVector = Icons.Default.Code,
                        contentDescription = "File Lab",
                        tint = GlobePalettes.ElectricCyan,
                        modifier = Modifier.size(24.dp)
                    )
                    Column {
                        Text(
                            text = "File Lab Code Studio",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${activeFile.filename} (${activeFile.content.length} chars)",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    // New file button
                    IconButton(
                        onClick = {
                            val newFile = FileLabItem(
                                id = System.currentTimeMillis().toString(),
                                filename = "untitled.js",
                                extension = "js",
                                content = "// New Javascript file\nconsole.log('Hello World');"
                            )
                            sampleFiles.add(newFile)
                            activeFileId = newFile.id
                        }
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "New File")
                    }

                    // Run sandbox preview button for HTML/JS/Markdown
                    if (activeFile.extension in listOf("html", "htm", "svg", "md")) {
                        FilledTonalButton(
                            onClick = { showLivePreview = !showLivePreview },
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(
                                imageVector = if (showLivePreview) Icons.Default.Code else Icons.Default.PlayArrow,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(if (showLivePreview) "Code" else "Run Sandbox", fontSize = 12.sp)
                        }
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close File Lab")
                    }
                }
            }

            // File Tabs Bar
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                items(sampleFiles, key = { it.id }) { file ->
                    val isSelected = (file.id == activeFileId)
                    InputChip(
                        selected = isSelected,
                        onClick = {
                            activeFileId = file.id
                            showLivePreview = false
                        },
                        label = { Text(file.filename, fontSize = 12.sp) },
                        trailingIcon = if (sampleFiles.size > 1) {
                            {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Close",
                                    modifier = Modifier
                                        .size(14.dp)
                                        .clickable {
                                            sampleFiles.remove(file)
                                            if (activeFileId == file.id) {
                                                activeFileId = sampleFiles.first().id
                                            }
                                        }
                                )
                            }
                        } else null
                    )
                }
            }

            // Body: Code Editor or Live Sandbox Preview
            if (showLivePreview && activeFile.extension in listOf("html", "htm", "svg")) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.White)
                ) {
                    AndroidView(
                        factory = { ctx ->
                            WebView(ctx).apply {
                                settings.javaScriptEnabled = true
                                loadDataWithBaseURL(null, activeFile.content, "text/html", "UTF-8", null)
                            }
                        },
                        update = { wv ->
                            wv.loadDataWithBaseURL(null, activeFile.content, "text/html", "UTF-8", null)
                        },
                        modifier = Modifier.fillMaxSize()
                    )
                }
            } else if (showLivePreview && activeFile.extension == "md") {
                // Rendered Markdown preview
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                        .padding(12.dp)
                ) {
                    for (line in activeFile.content.lines()) {
                        when {
                            line.startsWith("# ") -> Text(line.removePrefix("# "), style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                            line.startsWith("## ") -> Text(line.removePrefix("## "), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                            line.startsWith("* ") || line.startsWith("- ") -> Row(modifier = Modifier.padding(vertical = 2.dp)) {
                                Text("•  ", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                                Text(line.substring(2), style = MaterialTheme.typography.bodyMedium)
                            }
                            else -> Text(line, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(vertical = 2.dp))
                        }
                    }
                }
            } else {
                // Code Editor with Syntax Token Highlighting & Line Numbers
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF0F172A))
                        .padding(8.dp)
                ) {
                    val lines = activeFile.content.lines()
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .horizontalScroll(rememberScrollState())
                            .verticalScroll(rememberScrollState())
                    ) {
                        // Line numbers column
                        Column(modifier = Modifier.padding(end = 12.dp)) {
                            for (i in 1..lines.size) {
                                Text(
                                    text = "$i",
                                    style = TextStyle(
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 12.sp,
                                        color = Color(0xFF64748B)
                                    )
                                )
                            }
                        }

                        // Code Editor BasicTextField
                        BasicTextField(
                            value = activeFile.content,
                            onValueChange = { newText ->
                                val index = sampleFiles.indexOfFirst { it.id == activeFile.id }
                                if (index != -1) {
                                    sampleFiles[index] = activeFile.copy(content = newText, isModified = true)
                                }
                            },
                            textStyle = TextStyle(
                                fontFamily = FontFamily.Monospace,
                                fontSize = 12.sp,
                                color = Color(0xFFE2E8F0)
                            ),
                            cursorBrush = SolidColor(GlobePalettes.ElectricCyan),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("file_lab_code_editor")
                        )
                    }
                }
            }

            // Footer info explaining execution rules
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp, bottom = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = if (activeFile.extension in listOf("html", "js", "css", "md"))
                        "✓ Safe client sandbox execution enabled"
                    else
                        "ℹ️ Native language viewing & editing mode (Requires WASM/Server for execution)",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
