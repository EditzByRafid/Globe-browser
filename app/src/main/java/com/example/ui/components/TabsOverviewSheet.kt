package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import com.example.model.TabItem
import com.example.ui.theme.GlobePalettes
import com.example.ui.theme.chromeCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TabsOverviewSheet(
    tabs: List<TabItem>,
    activeTabId: String,
    settings: BrowserSettings,
    onSelectTab: (String) -> Unit,
    onCloseTab: (String) -> Unit,
    onDuplicateTab: (String) -> Unit = {},
    onCloseOtherTabs: (String) -> Unit = {},
    onNewTab: (Boolean) -> Unit,
    onCloseAllTabs: () -> Unit,
    onDismiss: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    val filteredTabs = tabs.filter {
        searchQuery.isBlank() || it.title.contains(searchQuery, ignoreCase = true) || it.url.contains(searchQuery, ignoreCase = true)
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
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Open Tabs (${tabs.size})",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    if (tabs.size >= settings.tabLimitWarning) {
                        Text(
                            text = "⚠️ High tab count (${tabs.size}) may affect budget devices",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    TextButton(onClick = onCloseAllTabs) {
                        Text("Close All", color = MaterialTheme.colorScheme.error)
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close Tab Switcher")
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Tab search bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search open tabs...", fontSize = 13.sp) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, modifier = Modifier.size(18.dp)) },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(Icons.Default.Close, contentDescription = "Clear search", modifier = Modifier.size(16.dp))
                        }
                    }
                }
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Tab Action Bar (Standard New Tab vs Incognito New Tab)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = { onNewTab(false) },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("new_regular_tab_button"),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("New Tab")
                }

                OutlinedButton(
                    onClick = { onNewTab(true) },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("new_incognito_tab_button"),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color(0xFFA855F7)
                    )
                ) {
                    Icon(Icons.Default.Security, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Incognito")
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Grid of Tabs
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                items(filteredTabs, key = { it.id }) { tab ->
                    val isActive = (tab.id == activeTabId)

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(140.dp)
                            .chromeCard(
                                shape = RoundedCornerShape(14.dp)
                            )
                            .clickable { onSelectTab(tab.id) },
                        colors = CardDefaults.cardColors(
                            containerColor = if (isActive) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                            else if (tab.isIncognito) Color(0xFF1E102E)
                            else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(10.dp),
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            // Top Row: Favicon / Mode + Action buttons
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    if (tab.isIncognito) {
                                        Icon(
                                            imageVector = Icons.Default.Security,
                                            contentDescription = "Private Tab",
                                            tint = Color(0xFFA855F7),
                                            modifier = Modifier.size(16.dp)
                                        )
                                    } else {
                                        Icon(
                                            imageVector = Icons.Default.Language,
                                            contentDescription = "Web Tab",
                                            tint = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                    Text(
                                        text = tab.title.ifBlank { "New Tab" },
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = if (isActive) FontWeight.Bold else FontWeight.Medium,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }

                                Row {
                                    IconButton(
                                        onClick = { onDuplicateTab(tab.id) },
                                        modifier = Modifier.size(24.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.ContentCopy,
                                            contentDescription = "Duplicate Tab",
                                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                            modifier = Modifier.size(14.dp)
                                        )
                                    }

                                    IconButton(
                                        onClick = { onCloseTab(tab.id) },
                                        modifier = Modifier.size(24.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Close,
                                            contentDescription = "Close Tab",
                                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                            modifier = Modifier.size(14.dp)
                                        )
                                    }
                                }
                            }

                            // Preview Placeholder Card Body
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f)
                                    .padding(vertical = 4.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = if (tab.url.startsWith("globe://")) "Start Page" else tab.url.removePrefix("https://").removePrefix("http://"),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier.padding(4.dp)
                                )
                            }

                            // Bottom active indicator & options
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                if (isActive) {
                                    Box(
                                        modifier = Modifier
                                            .width(48.dp)
                                            .height(3.dp)
                                            .clip(CircleShape)
                                            .background(MaterialTheme.colorScheme.primary)
                                    )
                                } else {
                                    Spacer(modifier = Modifier.width(4.dp))
                                }

                                Text(
                                    text = "Only this",
                                    fontSize = 10.sp,
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.clickable { onCloseOtherTabs(tab.id) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
