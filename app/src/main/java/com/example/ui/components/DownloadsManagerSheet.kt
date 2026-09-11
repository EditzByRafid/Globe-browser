package com.example.ui.components

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.DownloadCategory
import com.example.model.DownloadItem
import com.example.model.DownloadStatus
import com.example.ui.theme.GlobePalettes
import com.example.ui.theme.chromeCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DownloadsManagerSheet(
    downloads: List<DownloadItem>,
    onPauseDownload: (String) -> Unit,
    onResumeDownload: (String) -> Unit,
    onDeleteDownload: (String) -> Unit,
    onClearFinishedDownloads: () -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf(DownloadCategory.ALL) }

    val filteredDownloads = remember(downloads, searchQuery, selectedCategory) {
        downloads.filter { item ->
            val matchesCat = selectedCategory == DownloadCategory.ALL || item.category == selectedCategory
            val matchesQuery = searchQuery.isBlank() || item.fileName.contains(searchQuery, ignoreCase = true)
            matchesCat && matchesQuery
        }
    }

    val totalBytesUsed = remember(downloads) {
        downloads.sumOf { it.downloadedBytes }
    }
    val formattedTotalMb = remember(totalBytesUsed) {
        String.format("%.1f MB", totalBytesUsed / (1024.0 * 1024.0))
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = MaterialTheme.colorScheme.surface,
        dragHandle = {
            Column(
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .width(36.dp)
                        .height(4.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f))
                )
            }
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.85f)
                .padding(horizontal = 16.dp)
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(GlobePalettes.GoogleBlue.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Download,
                            contentDescription = "Downloads",
                            tint = GlobePalettes.GoogleBlue
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Downloads Manager",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Storage used: $formattedTotalMb • ${downloads.size} files",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                if (downloads.any { it.status == DownloadStatus.COMPLETED }) {
                    TextButton(onClick = onClearFinishedDownloads) {
                        Text("Clear All", color = GlobePalettes.GoogleRed, fontSize = 13.sp)
                    }
                }
            }

            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                placeholder = { Text("Search downloaded files...", fontSize = 14.sp) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(Icons.Default.Close, contentDescription = "Clear search", modifier = Modifier.size(18.dp))
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )

            // Category Filter Pills
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                listOf(
                    DownloadCategory.ALL,
                    DownloadCategory.DOCUMENTS,
                    DownloadCategory.IMAGES,
                    DownloadCategory.VIDEOS,
                    DownloadCategory.OTHERS
                ).forEach { cat ->
                    val isSelected = selectedCategory == cat
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedCategory = cat },
                        label = { Text("${cat.icon} ${cat.label}", fontSize = 12.sp) },
                        shape = RoundedCornerShape(10.dp)
                    )
                }
            }

            // List of Downloads
            if (filteredDownloads.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.FolderOpen,
                            contentDescription = null,
                            modifier = Modifier.size(56.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = if (searchQuery.isNotEmpty()) "No files match '$searchQuery'" else "No downloaded files yet",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(bottom = 24.dp)
                ) {
                    items(filteredDownloads, key = { it.id }) { item ->
                        DownloadItemRow(
                            item = item,
                            onPause = { onPauseDownload(item.id) },
                            onResume = { onResumeDownload(item.id) },
                            onDelete = { onDeleteDownload(item.id) },
                            onOpen = {
                                Toast.makeText(context, "Opening ${item.fileName}...", Toast.LENGTH_SHORT).show()
                            },
                            onShare = {
                                val shareIntent = Intent().apply {
                                    action = Intent.ACTION_SEND
                                    putExtra(Intent.EXTRA_TEXT, "Downloaded file: ${item.fileName}\nSource: ${item.fileUrl}")
                                    type = "text/plain"
                                }
                                context.startActivity(Intent.createChooser(shareIntent, "Share Downloaded File"))
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun DownloadItemRow(
    item: DownloadItem,
    onPause: () -> Unit,
    onResume: () -> Unit,
    onDelete: () -> Unit,
    onOpen: () -> Unit,
    onShare: () -> Unit
) {
    val formattedSize = remember(item.totalSizeBytes) {
        String.format("%.1f MB", item.totalSizeBytes / (1024.0 * 1024.0))
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { if (item.status == DownloadStatus.COMPLETED) onOpen() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // File Type Icon
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(
                            when (item.category) {
                                DownloadCategory.DOCUMENTS -> GlobePalettes.GoogleBlue.copy(alpha = 0.15f)
                                DownloadCategory.IMAGES -> GlobePalettes.GoogleGreen.copy(alpha = 0.15f)
                                DownloadCategory.VIDEOS -> GlobePalettes.GoogleRed.copy(alpha = 0.15f)
                                DownloadCategory.AUDIO -> GlobePalettes.GoogleYellow.copy(alpha = 0.15f)
                                else -> MaterialTheme.colorScheme.surfaceVariant
                            }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = item.category.icon, fontSize = 20.sp)
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = item.fileName,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "$formattedSize • ${item.status.name.lowercase().replaceFirstChar { it.uppercase() }}",
                        style = MaterialTheme.typography.bodySmall,
                        color = when (item.status) {
                            DownloadStatus.COMPLETED -> GlobePalettes.GoogleGreen
                            DownloadStatus.DOWNLOADING -> GlobePalettes.GoogleBlue
                            DownloadStatus.PAUSED -> GlobePalettes.GoogleYellow
                            DownloadStatus.FAILED -> GlobePalettes.GoogleRed
                        }
                    )
                }

                // Action buttons
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (item.status == DownloadStatus.DOWNLOADING) {
                        IconButton(onClick = onPause) {
                            Icon(Icons.Default.Pause, contentDescription = "Pause", tint = GlobePalettes.GoogleYellow)
                        }
                    } else if (item.status == DownloadStatus.PAUSED) {
                        IconButton(onClick = onResume) {
                            Icon(Icons.Default.PlayArrow, contentDescription = "Resume", tint = GlobePalettes.GoogleGreen)
                        }
                    } else if (item.status == DownloadStatus.COMPLETED) {
                        IconButton(onClick = onShare) {
                            Icon(Icons.Default.Share, contentDescription = "Share", modifier = Modifier.size(18.dp))
                        }
                    }

                    IconButton(onClick = onDelete) {
                        Icon(Icons.Default.DeleteOutline, contentDescription = "Delete", tint = GlobePalettes.GoogleRed, modifier = Modifier.size(18.dp))
                    }
                }
            }

            // Progress bar if in progress
            if (item.status == DownloadStatus.DOWNLOADING || item.status == DownloadStatus.PAUSED) {
                Spacer(modifier = Modifier.height(8.dp))
                LinearProgressIndicator(
                    progress = { item.progressFraction },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp)),
                    color = GlobePalettes.GoogleBlue,
                    trackColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
                )
            }
        }
    }
}
