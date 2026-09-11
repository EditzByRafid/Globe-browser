package com.example.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.*
import com.example.ui.theme.chromeCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsSheet(
    settings: BrowserSettings,
    onUpdateSettings: (BrowserSettings) -> Unit,
    onToggleSearchEngine: (String) -> Unit = {},
    onClearAllData: () -> Unit,
    onDismiss: () -> Unit
) {
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
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Settings",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                    Text(
                        text = "GB Settings",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Close Settings")
                }
            }

            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(14.dp),
                contentPadding = PaddingValues(top = 10.dp, bottom = 24.dp)
            ) {
                // Section: Universal Hardware Performance Optimization
                item {
                    Text(
                        text = "Device Performance Profile (Android 4 - 16)",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .chromeCard(shape = RoundedCornerShape(14.dp)),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f))
                    ) {
                        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text("Engine Acceleration", fontWeight = FontWeight.SemiBold)
                            Text(
                                "Adapts graphics rendering, memory limits, and frame throttling to prevent stutters on low-end, mid-range, and flagship devices.",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            val profiles = listOf("ultra_lite" to "Ultra Lite (Low-End)", "balanced" to "Balanced (Mid-Range)", "high_performance" to "Pro 90Hz/120Hz (High-End)")
                            for (p in profiles) {
                                val isSelected = settings.performanceProfile == p.first
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(8.dp))
                                        .clickable { onUpdateSettings(settings.copy(performanceProfile = p.first)) }
                                        .padding(vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        p.second,
                                        fontSize = 13.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                    )
                                    RadioButton(
                                        selected = isSelected,
                                        onClick = { onUpdateSettings(settings.copy(performanceProfile = p.first)) }
                                    )
                                }
                            }
                        }
                    }
                }

                // Section: Themes (Light, Dark, Midnight)
                item {
                    Text(
                        text = "Theme & Colors",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .chromeCard(shape = RoundedCornerShape(14.dp)),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f))
                    ) {
                        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Text("Select Theme Mode", fontWeight = FontWeight.SemiBold)
                            for (t in BrowserTheme.values()) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(8.dp))
                                        .clickable { onUpdateSettings(settings.copy(theme = t)) }
                                        .padding(vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column {
                                        Text(t.label, fontSize = 13.sp, fontWeight = if (settings.theme == t) FontWeight.Bold else FontWeight.Normal)
                                        Text(
                                            when (t) {
                                                BrowserTheme.LIGHT -> "Clean bright surfaces & crisp contrast"
                                                BrowserTheme.DARK -> "Classic dark aesthetic"
                                                BrowserTheme.MIDNIGHT -> "Pure OLED black (0% battery drain)"
                                            },
                                            fontSize = 11.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                    RadioButton(
                                        selected = (settings.theme == t),
                                        onClick = { onUpdateSettings(settings.copy(theme = t)) }
                                    )
                                }
                            }
                        }
                    }
                }

                // Section: Search Engines (Toggles & Default)
                item {
                    Text(
                        text = "Search Engines (Google Default)",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .chromeCard(shape = RoundedCornerShape(14.dp)),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f))
                    ) {
                        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Text("Default Search Engine", fontWeight = FontWeight.SemiBold)

                            for (engine in SearchEngine.values()) {
                                val isEnabled = settings.enabledSearchEngines.contains(engine.name)
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 2.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                        SearchEngineVectorLogo(engine = engine, size = 20.dp)
                                        Column {
                                            Text(engine.displayName, fontSize = 13.sp, fontWeight = if (settings.searchEngine == engine) FontWeight.Bold else FontWeight.Normal)
                                            if (engine == SearchEngine.GOOGLE) {
                                                Text("Default search engine", fontSize = 10.sp, color = MaterialTheme.colorScheme.primary)
                                            }
                                        }
                                    }

                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        RadioButton(
                                            selected = (settings.searchEngine == engine),
                                            onClick = { onUpdateSettings(settings.copy(searchEngine = engine)) }
                                        )
                                        if (engine != SearchEngine.GOOGLE) {
                                            Switch(
                                                checked = isEnabled,
                                                onCheckedChange = { onToggleSearchEngine(engine.name) },
                                                modifier = Modifier.padding(start = 4.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // Section: Toolbar Layout (Top Chrome vs Bottom Safari)
                item {
                    Text(
                        text = "Toolbar Layout",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .chromeCard(shape = RoundedCornerShape(14.dp)),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f))
                    ) {
                        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(
                                text = if (settings.toolbarPosition == ToolbarPosition.BOTTOM) "Bottom Toolbar" else "Top Toolbar (Chrome Style)",
                                fontWeight = FontWeight.SemiBold
                            )
                            Text("Customize address & search bar placement", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)

                            Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth().padding(top = 6.dp)) {
                                FilterChip(
                                    selected = settings.toolbarPosition == ToolbarPosition.TOP,
                                    onClick = { onUpdateSettings(settings.copy(toolbarPosition = ToolbarPosition.TOP)) },
                                    label = { Text("Top Bar") },
                                    leadingIcon = { Icon(Icons.Default.VerticalAlignTop, contentDescription = null, modifier = Modifier.size(16.dp)) }
                                )
                                FilterChip(
                                    selected = settings.toolbarPosition == ToolbarPosition.BOTTOM,
                                    onClick = { onUpdateSettings(settings.copy(toolbarPosition = ToolbarPosition.BOTTOM)) },
                                    label = { Text("Bottom Bar") },
                                    leadingIcon = { Icon(Icons.Default.VerticalAlignBottom, contentDescription = null, modifier = Modifier.size(16.dp)) }
                                )
                            }
                        }
                    }
                }

                // Section: Reading & Page Display
                item {
                    Text(
                        text = "Reading & Page Display",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .chromeCard(shape = RoundedCornerShape(14.dp)),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f))
                    ) {
                        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text("Desktop Mode User-Agent", fontWeight = FontWeight.SemiBold)
                                    Text("Request full desktop web versions", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                                Switch(
                                    checked = settings.desktopMode,
                                    onCheckedChange = { onUpdateSettings(settings.copy(desktopMode = it)) }
                                )
                            }

                            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text("Reader Mode (Clutter-Free)", fontWeight = FontWeight.SemiBold)
                                    Text("High-contrast text view with ads and banners hidden", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                                Switch(
                                    checked = settings.readerModeEnabled,
                                    onCheckedChange = { onUpdateSettings(settings.copy(readerModeEnabled = it)) }
                                )
                            }

                            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))

                            Column {
                                Text("Page Text Zoom: ${settings.pageZoomPercent}%", fontWeight = FontWeight.SemiBold)
                                Slider(
                                    value = settings.pageZoomPercent.toFloat(),
                                    onValueChange = { onUpdateSettings(settings.copy(pageZoomPercent = it.toInt())) },
                                    valueRange = 75f..175f,
                                    steps = 3
                                )
                            }
                        }
                    }
                }

                // Section: Clear All Data
                item {
                    OutlinedButton(
                        onClick = onClearAllData,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.DeleteOutline, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Clear All Browsing Data & Cache")
                    }
                }
            }
        }
    }
}
