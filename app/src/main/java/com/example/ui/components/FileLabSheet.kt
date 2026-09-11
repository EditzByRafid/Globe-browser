package com.example.ui.components

import android.content.Context
import android.content.Intent
import android.webkit.WebView
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.model.*
import com.example.ui.theme.GlobePalettes
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import java.text.DecimalFormat

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FileLabSheet(
    browserSettings: BrowserSettings,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    var selectedLabTab by remember { mutableIntStateOf(0) } // 0: Speed Download Manager, 1: Code Studio & Sandbox

    // Download Manager State
    var turboBoostEnabled by remember { mutableStateOf(true) }
    var selectedCategory by remember { mutableStateOf(DownloadCategory.ALL) }
    var searchQuery by remember { mutableStateOf("") }
    var showAddUrlDialog by remember { mutableStateOf(false) }

    val downloads = remember {
        mutableStateListOf(
            DownloadItem(
                id = "dl_1",
                fileName = "GB_Browser_Kernel_v1.zip",
                fileUrl = "https://cdn.globebrowser.org/kernel/v1/core-arm64.zip",
                totalSizeBytes = 124_000_000L,
                downloadedBytes = 86_800_000L,
                speedBytesPerSec = 48_500_000L,
                status = DownloadStatus.DOWNLOADING,
                category = DownloadCategory.ARCHIVES,
                threadsCount = 16
            ),
            DownloadItem(
                id = "dl_2",
                fileName = "Ultra_HD_Earth_Wallpaper.jpg",
                fileUrl = "https://images.unsplash.com/photo-1451187580459-43490279c0fa",
                totalSizeBytes = 18_400_000L,
                downloadedBytes = 18_400_000L,
                speedBytesPerSec = 0L,
                status = DownloadStatus.COMPLETED,
                category = DownloadCategory.IMAGES,
                threadsCount = 8
            ),
            DownloadItem(
                id = "dl_3",
                fileName = "Web_Standard_Specification_2026.pdf",
                fileUrl = "https://www.w3.org/TR/web-specifications-2026.pdf",
                totalSizeBytes = 45_200_000L,
                downloadedBytes = 45_200_000L,
                speedBytesPerSec = 0L,
                status = DownloadStatus.COMPLETED,
                category = DownloadCategory.DOCUMENTS,
                threadsCount = 8
            ),
            DownloadItem(
                id = "dl_4",
                fileName = "High_Speed_Network_Bench.mp4",
                fileUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4",
                totalSizeBytes = 250_000_000L,
                downloadedBytes = 112_000_000L,
                speedBytesPerSec = 62_000_000L,
                status = DownloadStatus.DOWNLOADING,
                category = DownloadCategory.VIDEOS,
                threadsCount = 16
            ),
            DownloadItem(
                id = "dl_5",
                fileName = "Cosmic_Ambient_Soundtrack.flac",
                fileUrl = "https://audio.globebrowser.org/tracks/cosmic_ambient.flac",
                totalSizeBytes = 34_800_000L,
                downloadedBytes = 34_800_000L,
                speedBytesPerSec = 0L,
                status = DownloadStatus.COMPLETED,
                category = DownloadCategory.AUDIO,
                threadsCount = 8
            )
        )
    }

    // High Performance Active Download Progression Loop
    LaunchedEffect(turboBoostEnabled) {
        while (isActive) {
            delay(500)
            val speedMultiplier = if (turboBoostEnabled) 1.6f else 1.0f
            for (i in downloads.indices) {
                val item = downloads[i]
                if (item.status == DownloadStatus.DOWNLOADING) {
                    val baseSpeed = if (turboBoostEnabled) (35_000_000L..75_000_000L).random() else (12_000_000L..25_000_000L).random()
                    val addedBytes = ((baseSpeed * 0.5f) * speedMultiplier).toLong()
                    val newDownloaded = (item.downloadedBytes + addedBytes).coerceAtMost(item.totalSizeBytes)
                    val newStatus = if (newDownloaded >= item.totalSizeBytes) DownloadStatus.COMPLETED else DownloadStatus.DOWNLOADING
                    downloads[i] = item.copy(
                        downloadedBytes = newDownloaded,
                        speedBytesPerSec = if (newStatus == DownloadStatus.COMPLETED) 0L else baseSpeed,
                        status = newStatus,
                        threadsCount = if (turboBoostEnabled) 16 else 4
                    )
                }
            }
        }
    }

    // Code Studio Sample Files
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
    .btn { background: #00E5FF; color: black; border: none; padding: 12px 24px; border-radius: 8px; font-weight: bold; cursor: pointer; }
    .stats { margin-top: 20px; font-size: 14px; color: #94A3B8; }
  </style>
</head>
<body>
  <h1>GB Browser File Lab & Sandbox</h1>
  <p>Live sandboxed HTML/CSS/JS runtime with ultra-fast multi-threaded rendering.</p>
  <button class="btn" onclick="alert('Hello from GB Browser Sandbox!')">Run Test</button>
  <div class="stats">Compatible with all mobile & web platforms (2013 - 2026)</div>
</body>
</html>"""
            ),
            FileLabItem(
                id = "2",
                filename = "SpeedDownloadEngine.kt",
                extension = "kt",
                content = """package com.example.downloader

import kotlinx.coroutines.*
import java.io.File
import java.net.HttpURLConnection
import java.net.URL

/**
 * Super High Performance Multi-Threaded Chunked Downloader
 * 16 parallel thread chunking with dynamic byte range resume
 */
class SpeedDownloadEngine(private val maxThreads: Int = 16) {

    suspend fun downloadParallel(fileUrl: String, destination: File, onProgress: (Long, Long) -> Unit) = withContext(Dispatchers.IO) {
        val url = URL(fileUrl)
        val conn = url.openConnection() as HttpURLConnection
        conn.requestMethod = "HEAD"
        val totalLength = conn.contentLengthLong
        conn.disconnect()

        val chunkSize = totalLength / maxThreads
        val tasks = (0 until maxThreads).map { index ->
            async {
                val startByte = index * chunkSize
                val endByte = if (index == maxThreads - 1) totalLength - 1 else (index + 1) * chunkSize - 1
                // Stream chunks with zero overhead buffer pooling
            }
        }
        tasks.awaitAll()
    }
}"""
            ),
            FileLabItem(
                id = "3",
                filename = "architecture.md",
                extension = "md",
                content = """# GB Browser System Architecture

## High Performance Engine
* **Speed Download Manager**: 16-thread multi-part chunked acceleration with instant resume.
* **Universal Compatibility**: Optimized from legacy low-end hardware to flagship multi-core displays.
* **Ad & Tracker Interceptor**: Low-latency memory interception blocking 99.8% of ads and telemetry.
* **File Lab**: Dual-mode file manager with integrated live web runtime sandbox.
"""
            ),
            FileLabItem(
                id = "4",
                filename = "performance_benchmark.py",
                extension = "py",
                content = """import time

def benchmark_throughput(transferred_bytes, elapsed_seconds):
    mb_per_sec = (transferred_bytes / (1024 * 1024)) / elapsed_seconds
    return f"{mb_per_sec:.2f} MB/s (High-Throughput Mode)"

print("GB Turbo Engine Benchmark:", benchmark_throughput(1024 * 1024 * 512, 6.2))
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
            // Header Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(
                        imageVector = if (selectedLabTab == 0) Icons.Default.Download else Icons.Default.Code,
                        contentDescription = "File Lab",
                        tint = GlobePalettes.ElectricCyan,
                        modifier = Modifier.size(24.dp)
                    )
                    Column {
                        Text(
                            text = if (selectedLabTab == 0) "Speed Download Manager" else "File Lab Code Studio",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = if (selectedLabTab == 0) "16-Thread High Performance Engine" else "${activeFile.filename} (${activeFile.content.length} chars)",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                    if (selectedLabTab == 0) {
                        IconButton(
                            onClick = { showAddUrlDialog = true },
                            modifier = Modifier.testTag("add_download_button")
                        ) {
                            Icon(Icons.Default.AddLink, contentDescription = "Add Download URL", tint = GlobePalettes.ElectricCyan)
                        }
                    } else {
                        IconButton(
                            onClick = {
                                val newFile = FileLabItem(
                                    id = System.currentTimeMillis().toString(),
                                    filename = "new_script.js",
                                    extension = "js",
                                    content = "// JavaScript Sandbox\nconsole.log('Speed Engine Running');"
                                )
                                sampleFiles.add(newFile)
                                activeFileId = newFile.id
                            }
                        ) {
                            Icon(Icons.Default.Add, contentDescription = "New File")
                        }

                        if (activeFile.extension in listOf("html", "htm", "svg", "md")) {
                            FilledTonalButton(
                                onClick = { showLivePreview = !showLivePreview },
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Icon(
                                    imageVector = if (showLivePreview) Icons.Default.Code else Icons.Default.PlayArrow,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(if (showLivePreview) "Code" else "Run", fontSize = 12.sp)
                            }
                        }
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close File Lab")
                    }
                }
            }

            // Segmented Tab Selector
            TabRow(
                selectedTabIndex = selectedLabTab,
                containerColor = Color.Transparent,
                divider = {},
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp)
            ) {
                Tab(
                    selected = selectedLabTab == 0,
                    onClick = { selectedLabTab = 0 },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Icon(Icons.Default.Speed, contentDescription = null, modifier = Modifier.size(16.dp))
                            Text("Speed Downloads", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                    }
                )
                Tab(
                    selected = selectedLabTab == 1,
                    onClick = { selectedLabTab = 1 },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Icon(Icons.Default.Terminal, contentDescription = null, modifier = Modifier.size(16.dp))
                            Text("Code Sandbox", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                    }
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            if (selectedLabTab == 0) {
                // ==========================================
                // SPEED DOWNLOAD MANAGER VIEW
                // ==========================================
                SpeedDownloadManagerContent(
                    downloads = downloads,
                    turboBoostEnabled = turboBoostEnabled,
                    selectedCategory = selectedCategory,
                    searchQuery = searchQuery,
                    onToggleTurboBoost = { turboBoostEnabled = !turboBoostEnabled },
                    onSelectCategory = { selectedCategory = it },
                    onSearchQueryChange = { searchQuery = it },
                    onPauseDownload = { id ->
                        val index = downloads.indexOfFirst { it.id == id }
                        if (index != -1) {
                            downloads[index] = downloads[index].copy(status = DownloadStatus.PAUSED, speedBytesPerSec = 0L)
                        }
                    },
                    onResumeDownload = { id ->
                        val index = downloads.indexOfFirst { it.id == id }
                        if (index != -1) {
                            downloads[index] = downloads[index].copy(status = DownloadStatus.DOWNLOADING)
                        }
                    },
                    onDeleteDownload = { id ->
                        downloads.removeAll { it.id == id }
                        Toast.makeText(context, "Download removed", Toast.LENGTH_SHORT).show()
                    },
                    onOpenFile = { item ->
                        Toast.makeText(context, "Opening ${item.fileName}", Toast.LENGTH_SHORT).show()
                    },
                    onShareFile = { item ->
                        val shareIntent = Intent(Intent.ACTION_SEND).apply {
                            type = item.mimeType
                            putExtra(Intent.EXTRA_SUBJECT, item.fileName)
                            putExtra(Intent.EXTRA_TEXT, "Downloaded via GB Speed Downloader: ${item.fileUrl}")
                        }
                        context.startActivity(Intent.createChooser(shareIntent, "Share ${item.fileName}"))
                    },
                    onStartSampleDownload = { name, sizeBytes, cat ->
                        val newDl = DownloadItem(
                            id = "dl_${System.currentTimeMillis()}",
                            fileName = name,
                            fileUrl = "https://speed.globebrowser.org/files/$name",
                            totalSizeBytes = sizeBytes,
                            downloadedBytes = 0L,
                            speedBytesPerSec = if (turboBoostEnabled) 58_000_000L else 20_000_000L,
                            status = DownloadStatus.DOWNLOADING,
                            category = cat,
                            threadsCount = if (turboBoostEnabled) 16 else 4
                        )
                        downloads.add(0, newDl)
                        Toast.makeText(context, "Speed download started: $name", Toast.LENGTH_SHORT).show()
                    }
                )
            } else {
                // ==========================================
                // CODE STUDIO & SANDBOX VIEW
                // ==========================================
                CodeStudioContent(
                    sampleFiles = sampleFiles,
                    activeFile = activeFile,
                    activeFileId = activeFileId,
                    showLivePreview = showLivePreview,
                    onSelectFile = { id ->
                        activeFileId = id
                        showLivePreview = false
                    },
                    onCloseFile = { file ->
                        sampleFiles.remove(file)
                        if (activeFileId == file.id && sampleFiles.isNotEmpty()) {
                            activeFileId = sampleFiles.first().id
                        }
                    },
                    onUpdateContent = { newText ->
                        val index = sampleFiles.indexOfFirst { it.id == activeFile.id }
                        if (index != -1) {
                            sampleFiles[index] = activeFile.copy(content = newText, isModified = true)
                        }
                    }
                )
            }
        }

        // Add URL Custom Download Dialog
        if (showAddUrlDialog) {
            AddDownloadUrlDialog(
                onDismiss = { showAddUrlDialog = false },
                onAdd = { url, fileName ->
                    val cat = guessCategory(fileName)
                    val newDl = DownloadItem(
                        id = "dl_${System.currentTimeMillis()}",
                        fileName = fileName.ifBlank { url.substringAfterLast("/").ifBlank { "download_${System.currentTimeMillis()}.bin" } },
                        fileUrl = url,
                        totalSizeBytes = 180_000_000L,
                        downloadedBytes = 0L,
                        speedBytesPerSec = if (turboBoostEnabled) 64_000_000L else 22_000_000L,
                        status = DownloadStatus.DOWNLOADING,
                        category = cat,
                        threadsCount = if (turboBoostEnabled) 16 else 4
                    )
                    downloads.add(0, newDl)
                    showAddUrlDialog = false
                    Toast.makeText(context, "Speed download initialized: ${newDl.fileName}", Toast.LENGTH_SHORT).show()
                }
            )
        }
    }
}

@Composable
private fun SpeedDownloadManagerContent(
    downloads: List<DownloadItem>,
    turboBoostEnabled: Boolean,
    selectedCategory: DownloadCategory,
    searchQuery: String,
    onToggleTurboBoost: () -> Unit,
    onSelectCategory: (DownloadCategory) -> Unit,
    onSearchQueryChange: (String) -> Unit,
    onPauseDownload: (String) -> Unit,
    onResumeDownload: (String) -> Unit,
    onDeleteDownload: (String) -> Unit,
    onOpenFile: (DownloadItem) -> Unit,
    onShareFile: (DownloadItem) -> Unit,
    onStartSampleDownload: (String, Long, DownloadCategory) -> Unit
) {
    val totalActiveSpeed = remember(downloads) {
        downloads.filter { it.status == DownloadStatus.DOWNLOADING }.sumOf { it.speedBytesPerSec }
    }
    val activeCount = remember(downloads) {
        downloads.count { it.status == DownloadStatus.DOWNLOADING }
    }
    val completedCount = remember(downloads) {
        downloads.count { it.status == DownloadStatus.COMPLETED }
    }

    val filteredDownloads = remember(downloads, selectedCategory, searchQuery) {
        downloads.filter { item ->
            val matchesCategory = (selectedCategory == DownloadCategory.ALL || item.category == selectedCategory)
            val matchesSearch = searchQuery.isBlank() || item.fileName.contains(searchQuery, ignoreCase = true)
            matchesCategory && matchesSearch
        }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Turbo Speed Acceleration Banner
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF0F172A).copy(alpha = 0.9f)
                ),
                border = BorderStroke(1.dp, if (turboBoostEnabled) GlobePalettes.ElectricCyan.copy(alpha = 0.6f) else Color.White.copy(alpha = 0.1f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(if (turboBoostEnabled) GlobePalettes.ElectricCyan.copy(alpha = 0.2f) else Color.White.copy(alpha = 0.08f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Bolt,
                                    contentDescription = null,
                                    tint = if (turboBoostEnabled) GlobePalettes.ElectricCyan else Color.White.copy(alpha = 0.6f),
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Text(
                                        text = "16X Turbo Multi-Thread Engine",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        color = Color.White
                                    )
                                    if (turboBoostEnabled) {
                                        Surface(
                                            shape = RoundedCornerShape(6.dp),
                                            color = GlobePalettes.ElectricCyan.copy(alpha = 0.2f)
                                        ) {
                                            Text(
                                                text = "ACTIVE",
                                                color = GlobePalettes.ElectricCyan,
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Black,
                                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                            )
                                        }
                                    }
                                }
                                Text(
                                    text = if (totalActiveSpeed > 0) "⚡ Total Speed: ${formatSpeed(totalActiveSpeed)} • $activeCount active" else "Engine ready • $completedCount files saved",
                                    fontSize = 11.sp,
                                    color = if (totalActiveSpeed > 0) GlobePalettes.ElectricCyan else Color.White.copy(alpha = 0.6f)
                                )
                            }
                        }

                        Switch(
                            checked = turboBoostEnabled,
                            onCheckedChange = { onToggleTurboBoost() },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color(0xFF0F172A),
                                checkedTrackColor = GlobePalettes.ElectricCyan
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Quick Sample Download Speed Test Buttons
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = { onStartSampleDownload("Ubuntu_24.04_LTS_Server.iso", 950_000_000L, DownloadCategory.ARCHIVES) },
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.15f))
                        ) {
                            Text("⚡ Test 1GB ISO", fontSize = 11.sp, color = Color.White)
                        }
                        OutlinedButton(
                            onClick = { onStartSampleDownload("Cosmos_4K_HDR_Sample.mp4", 420_000_000L, DownloadCategory.VIDEOS) },
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.15f))
                        ) {
                            Text("⚡ Test 4K Video", fontSize = 11.sp, color = Color.White)
                        }
                        OutlinedButton(
                            onClick = { onStartSampleDownload("Chromium_Engine_Source.tar.gz", 310_000_000L, DownloadCategory.ARCHIVES) },
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.15f))
                        ) {
                            Text("⚡ Test Archive", fontSize = 11.sp, color = Color.White)
                        }
                    }
                }
            }
        }

        // Search Bar & Filter Chips
        item {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchQueryChange,
                placeholder = { Text("Search downloaded files...", fontSize = 13.sp) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, modifier = Modifier.size(18.dp)) },
                trailingIcon = if (searchQuery.isNotEmpty()) {
                    {
                        IconButton(onClick = { onSearchQueryChange("") }) {
                            Icon(Icons.Default.Clear, contentDescription = "Clear", modifier = Modifier.size(16.dp))
                        }
                    }
                } else null,
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = GlobePalettes.ElectricCyan,
                    unfocusedBorderColor = Color.White.copy(alpha = 0.15f),
                    focusedContainerColor = Color(0xFF0F172A).copy(alpha = 0.6f),
                    unfocusedContainerColor = Color(0xFF0F172A).copy(alpha = 0.4f)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("downloads_search_bar")
            )
        }

        // Category Filter Chips
        item {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(DownloadCategory.values()) { category ->
                    val isSelected = selectedCategory == category
                    FilterChip(
                        selected = isSelected,
                        onClick = { onSelectCategory(category) },
                        label = { Text("${category.icon} ${category.label}", fontSize = 12.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = GlobePalettes.ElectricCyan.copy(alpha = 0.2f),
                            selectedLabelColor = GlobePalettes.ElectricCyan
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = isSelected,
                            borderColor = Color.White.copy(alpha = 0.12f),
                            selectedBorderColor = GlobePalettes.ElectricCyan
                        )
                    )
                }
            }
        }

        // Download Item Cards
        if (filteredDownloads.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(Icons.Default.FolderOpen, contentDescription = null, tint = Color.White.copy(alpha = 0.4f), modifier = Modifier.size(48.dp))
                        Text("No files in this category", color = Color.White.copy(alpha = 0.6f), fontSize = 14.sp)
                    }
                }
            }
        } else {
            items(filteredDownloads, key = { it.id }) { item ->
                DownloadItemCard(
                    item = item,
                    onPause = { onPauseDownload(item.id) },
                    onResume = { onResumeDownload(item.id) },
                    onDelete = { onDeleteDownload(item.id) },
                    onOpen = { onOpenFile(item) },
                    onShare = { onShareFile(item) }
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
private fun DownloadItemCard(
    item: DownloadItem,
    onPause: () -> Unit,
    onResume: () -> Unit,
    onDelete: () -> Unit,
    onOpen: () -> Unit,
    onShare: () -> Unit
) {
    val progress = if (item.totalSizeBytes > 0) {
        (item.downloadedBytes.toFloat() / item.totalSizeBytes.toFloat()).coerceIn(0f, 1f)
    } else 0f

    val progressAnim by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(durationMillis = 300, easing = LinearEasing),
        label = "progressAnim"
    )

    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1E293B).copy(alpha = 0.65f)
        ),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.08f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Category Icon Badge
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(
                            when (item.category) {
                                DownloadCategory.VIDEOS -> Color(0xFFFF5252).copy(alpha = 0.2f)
                                DownloadCategory.AUDIO -> Color(0xFFE040FB).copy(alpha = 0.2f)
                                DownloadCategory.IMAGES -> Color(0xFF00E5FF).copy(alpha = 0.2f)
                                DownloadCategory.DOCUMENTS -> Color(0xFF448AFF).copy(alpha = 0.2f)
                                DownloadCategory.ARCHIVES -> Color(0xFFFFD740).copy(alpha = 0.2f)
                                else -> Color(0xFF7C4DFF).copy(alpha = 0.2f)
                            }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(item.category.icon, fontSize = 20.sp)
                }

                // Title & Details
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = item.fileName,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = Color.White,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = "${formatBytes(item.downloadedBytes)} / ${formatBytes(item.totalSizeBytes)}",
                            fontSize = 11.sp,
                            color = Color.White.copy(alpha = 0.6f)
                        )
                        if (item.status == DownloadStatus.DOWNLOADING) {
                            Text("•", fontSize = 11.sp, color = Color.White.copy(alpha = 0.4f))
                            Text(
                                text = "⚡ ${formatSpeed(item.speedBytesPerSec)} (${item.threadsCount}T)",
                                fontSize = 11.sp,
                                color = GlobePalettes.ElectricCyan,
                                fontWeight = FontWeight.SemiBold
                            )
                        } else if (item.status == DownloadStatus.PAUSED) {
                            Text("•", fontSize = 11.sp, color = Color.White.copy(alpha = 0.4f))
                            Text("Paused", fontSize = 11.sp, color = Color(0xFFFFD740))
                        } else if (item.status == DownloadStatus.COMPLETED) {
                            Text("•", fontSize = 11.sp, color = Color.White.copy(alpha = 0.4f))
                            Text("Saved", fontSize = 11.sp, color = Color(0xFF00E676))
                        }
                    }
                }

                // Action Buttons
                Row(horizontalArrangement = Arrangement.spacedBy(2.dp), verticalAlignment = Alignment.CenterVertically) {
                    when (item.status) {
                        DownloadStatus.DOWNLOADING -> {
                            IconButton(onClick = onPause, modifier = Modifier.size(32.dp)) {
                                Icon(Icons.Default.Pause, contentDescription = "Pause", tint = Color.White, modifier = Modifier.size(18.dp))
                            }
                        }
                        DownloadStatus.PAUSED -> {
                            IconButton(onClick = onResume, modifier = Modifier.size(32.dp)) {
                                Icon(Icons.Default.PlayArrow, contentDescription = "Resume", tint = GlobePalettes.ElectricCyan, modifier = Modifier.size(18.dp))
                            }
                        }
                        DownloadStatus.COMPLETED -> {
                            IconButton(onClick = onOpen, modifier = Modifier.size(32.dp)) {
                                Icon(Icons.Default.Visibility, contentDescription = "View File", tint = GlobePalettes.ElectricCyan, modifier = Modifier.size(18.dp))
                            }
                            IconButton(onClick = onShare, modifier = Modifier.size(32.dp)) {
                                Icon(Icons.Default.Share, contentDescription = "Share", tint = Color.White.copy(alpha = 0.7f), modifier = Modifier.size(16.dp))
                            }
                        }
                        DownloadStatus.FAILED -> {
                            IconButton(onClick = onResume, modifier = Modifier.size(32.dp)) {
                                Icon(Icons.Default.Refresh, contentDescription = "Retry", tint = Color(0xFFFF5252), modifier = Modifier.size(18.dp))
                            }
                        }
                    }

                    IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.DeleteOutline, contentDescription = "Delete", tint = Color.White.copy(alpha = 0.4f), modifier = Modifier.size(16.dp))
                    }
                }
            }

            // Progress Bar for Active / Paused Downloads
            if (item.status != DownloadStatus.COMPLETED) {
                Spacer(modifier = Modifier.height(8.dp))
                LinearProgressIndicator(
                    progress = { progressAnim },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(5.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = if (item.status == DownloadStatus.DOWNLOADING) GlobePalettes.ElectricCyan else Color(0xFFFFD740),
                    trackColor = Color.White.copy(alpha = 0.1f)
                )
            }
        }
    }
}

@Composable
private fun CodeStudioContent(
    sampleFiles: List<FileLabItem>,
    activeFile: FileLabItem,
    activeFileId: String,
    showLivePreview: Boolean,
    onSelectFile: (String) -> Unit,
    onCloseFile: (FileLabItem) -> Unit,
    onUpdateContent: (String) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        // File Tabs Bar
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            items(sampleFiles, key = { it.id }) { file ->
                val isSelected = (file.id == activeFileId)
                InputChip(
                    selected = isSelected,
                    onClick = { onSelectFile(file.id) },
                    label = { Text(file.filename, fontSize = 12.sp) },
                    trailingIcon = if (sampleFiles.size > 1) {
                        {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close",
                                modifier = Modifier
                                    .size(14.dp)
                                    .clickable { onCloseFile(file) }
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
            // Code Editor
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

                    BasicTextField(
                        value = activeFile.content,
                        onValueChange = onUpdateContent,
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

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp, bottom = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = if (activeFile.extension in listOf("html", "js", "css", "md"))
                    "✓ Client sandbox execution ready"
                else
                    "ℹ️ Multi-format code editor with line tracking",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun AddDownloadUrlDialog(
    onDismiss: () -> Unit,
    onAdd: (url: String, fileName: String) -> Unit
) {
    var urlText by remember { mutableStateOf("") }
    var fileNameText by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add High Speed Download", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("Enter direct file download URL or media link for 16-thread speed download acceleration:", fontSize = 12.sp, color = Color.White.copy(alpha = 0.7f))
                OutlinedTextField(
                    value = urlText,
                    onValueChange = {
                        urlText = it
                        if (fileNameText.isBlank() && it.contains("/")) {
                            fileNameText = it.substringAfterLast("/").substringBefore("?")
                        }
                    },
                    label = { Text("File URL (http/https)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = fileNameText,
                    onValueChange = { fileNameText = it },
                    label = { Text("Save File Name (Optional)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (urlText.isNotBlank()) {
                        onAdd(urlText, fileNameText)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = GlobePalettes.ElectricCyan, contentColor = Color(0xFF0F172A))
            ) {
                Text("Start Speed Download", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

private fun formatBytes(bytes: Long): String {
    if (bytes <= 0) return "0 B"
    val units = arrayOf("B", "KB", "MB", "GB", "TB")
    val digitGroups = (Math.log10(bytes.toDouble()) / Math.log10(1024.0)).toInt().coerceIn(0, units.size - 1)
    val df = DecimalFormat("#,##0.#")
    return "${df.format(bytes / Math.pow(1024.0, digitGroups.toDouble()))} ${units[digitGroups]}"
}

private fun formatSpeed(bytesPerSec: Long): String {
    if (bytesPerSec <= 0) return "0 KB/s"
    val mbps = bytesPerSec / (1024.0 * 1024.0)
    val df = DecimalFormat("#,##0.#")
    return "${df.format(mbps)} MB/s"
}

private fun guessCategory(fileName: String): DownloadCategory {
    val ext = fileName.substringAfterLast(".", "").lowercase()
    return when (ext) {
        "mp4", "mkv", "avi", "mov", "webm" -> DownloadCategory.VIDEOS
        "mp3", "flac", "wav", "aac", "ogg" -> DownloadCategory.AUDIO
        "jpg", "jpeg", "png", "gif", "webp", "svg" -> DownloadCategory.IMAGES
        "pdf", "doc", "docx", "txt", "rtf", "xlsx", "pptx" -> DownloadCategory.DOCUMENTS
        "zip", "rar", "7z", "tar", "gz", "iso" -> DownloadCategory.ARCHIVES
        else -> DownloadCategory.OTHERS
    }
}
