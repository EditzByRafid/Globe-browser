package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.BrowserSettings
import com.example.model.ExtensionItem
import com.example.ui.theme.GlobePalettes
import com.example.ui.theme.chromeCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExtensionsSheet(
    settings: BrowserSettings,
    extensions: List<ExtensionItem>,
    onToggleExtension: (String, Boolean) -> Unit,
    onInstallSample: (ExtensionItem) -> Unit,
    onDismiss: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("All") }
    var expandedExtensionId by remember { mutableStateOf<String?>(null) }

    val activeCount = remember(extensions) { extensions.count { it.isEnabled } }

    val categories = listOf(
        "All",
        "Active (${activeCount})",
        "Privacy",
        "AI",
        "Media",
        "Opera GX",
        "Safari & Chrome",
        "Developer",
        "Aesthetics"
    )

    val filteredExtensions = remember(extensions, searchQuery, selectedCategory) {
        extensions.filter { ext ->
            val matchesCategory = when (selectedCategory) {
                "All" -> true
                "Active (${activeCount})" -> ext.isEnabled
                else -> ext.description.contains("[$selectedCategory]", ignoreCase = true)
            }
            val matchesQuery = searchQuery.isBlank() ||
                ext.name.contains(searchQuery, ignoreCase = true) ||
                ext.description.contains(searchQuery, ignoreCase = true)

            matchesCategory && matchesQuery
        }
    }

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
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(GlobePalettes.ElectricCyan.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Extension,
                            contentDescription = "Extensions",
                            tint = GlobePalettes.ElectricCyan,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Column {
                        Text(
                            text = "Extensions Suite",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${extensions.size} Installed • $activeCount Always Active",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Close")
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search 200+ extensions...", fontSize = 13.sp) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, modifier = Modifier.size(18.dp)) },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(Icons.Default.Clear, contentDescription = "Clear", modifier = Modifier.size(16.dp))
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("extensions_search_input")
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Category Filter Chips
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(categories) { cat ->
                    FilterChip(
                        selected = (selectedCategory == cat),
                        onClick = { selectedCategory = cat },
                        label = { Text(cat, fontSize = 11.sp, fontWeight = if (selectedCategory == cat) FontWeight.Bold else FontWeight.Normal) },
                        shape = RoundedCornerShape(10.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Extensions List
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                items(filteredExtensions, key = { it.id }) { ext ->
                    val isExpanded = expandedExtensionId == ext.id

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .chromeCard(
                                shape = RoundedCornerShape(16.dp)
                            )
                            .clickable {
                                expandedExtensionId = if (isExpanded) null else ext.id
                            },
                        colors = CardDefaults.cardColors(
                            containerColor = if (ext.isEnabled) {
                                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.25f)
                            } else {
                                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
                            }
                        )
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    modifier = Modifier.weight(1f),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    // Extension category icon
                                    val iconChar = when {
                                        ext.description.contains("[Privacy]") -> "🛡️"
                                        ext.description.contains("[AI]") -> "✨"
                                        ext.description.contains("[Media]") -> "▶️"
                                        ext.description.contains("[Opera GX]") -> "🎮"
                                        ext.description.contains("[Safari") -> "🧭"
                                        ext.description.contains("[Developer]") -> "💻"
                                        else -> "🎨"
                                    }

                                    Box(
                                        modifier = Modifier
                                            .size(38.dp)
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(
                                                if (ext.isEnabled) GlobePalettes.ElectricCyan.copy(alpha = 0.2f)
                                                else MaterialTheme.colorScheme.surfaceVariant
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(iconChar, fontSize = 18.sp)
                                    }

                                    Column(modifier = Modifier.weight(1f)) {
                                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                            Text(
                                                text = ext.name,
                                                style = MaterialTheme.typography.titleSmall,
                                                fontWeight = FontWeight.Bold,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                            if (ext.isEnabled) {
                                                SuggestionChip(
                                                    onClick = {},
                                                    label = { Text("ACTIVE", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = GlobePalettes.ElectricCyan) },
                                                    modifier = Modifier.height(20.dp),
                                                    colors = SuggestionChipDefaults.suggestionChipColors(containerColor = GlobePalettes.ElectricCyan.copy(alpha = 0.12f))
                                                )
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(2.dp))

                                        Text(
                                            text = ext.description.replace(Regex("^\\[.*?\\]\\s*"), ""),
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            maxLines = if (isExpanded) 6 else 2,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                }

                                Switch(
                                    checked = ext.isEnabled,
                                    onCheckedChange = { onToggleExtension(ext.id, it) },
                                    modifier = Modifier.padding(start = 8.dp)
                                )
                            }

                            // Expanded details & live script simulation
                            AnimatedVisibility(visible = isExpanded) {
                                Column(modifier = Modifier.padding(top = 10.dp)) {
                                    Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                                    Spacer(modifier = Modifier.height(8.dp))

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text("Version: v${ext.version}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        Text("Sandbox: Isolated Web Worker", fontSize = 11.sp, color = GlobePalettes.ElectricCyan)
                                    }

                                    Spacer(modifier = Modifier.height(6.dp))

                                    Text("Permissions Granted:", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                                        modifier = Modifier.padding(top = 4.dp)
                                    ) {
                                        for (perm in ext.permissions) {
                                            SuggestionChip(
                                                onClick = {},
                                                label = { Text(perm, fontSize = 9.sp) },
                                                modifier = Modifier.height(20.dp)
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(6.dp))

                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text(
                                            text = "Status: ${if (ext.isEnabled) "Actively running in tab context" else "Disabled by user"}\nCode: ${ext.scriptCode}",
                                            fontSize = 10.sp,
                                            fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                                            modifier = Modifier.padding(8.dp),
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
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
}
