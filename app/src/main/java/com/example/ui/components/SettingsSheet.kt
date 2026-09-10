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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.*
import com.example.ui.theme.GlobePalettes
import com.example.ui.theme.liquidGlass

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsSheet(
    settings: BrowserSettings,
    onUpdateSettings: (BrowserSettings) -> Unit,
    onClearAllData: () -> Unit,
    onOpenFirebaseAccount: () -> Unit = {},
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
                        tint = GlobePalettes.ElectricCyan,
                        modifier = Modifier.size(24.dp)
                    )
                    Text(
                        text = "Browser Settings",
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
                // Section: Appearance & Performance
                item {
                    Text(
                        text = "Appearance & Performance",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .liquidGlass(
                                enabled = settings.liquidGlassEnabled,
                                lowEndMode = settings.lowEndModeEnabled,
                                shape = RoundedCornerShape(14.dp)
                            ),
                        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
                    ) {
                        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            // Liquid Glass toggle
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text("Liquid Glass Effect", fontWeight = FontWeight.SemiBold)
                                    Text("Translucent glassmorphism with specular reflections", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                                Switch(
                                    checked = settings.liquidGlassEnabled,
                                    onCheckedChange = { onUpdateSettings(settings.copy(liquidGlassEnabled = it)) }
                                )
                            }

                            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))

                            // Low End Device Mode (Realme Note 60 Optimization)
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text("Low-End Performance Mode", fontWeight = FontWeight.SemiBold, color = if (settings.lowEndModeEnabled) Color(0xFF10B981) else Color.Unspecified)
                                    Text("Realme Note 60 zero-lag mode. Disables blur, shadows, and alpha compositing.", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                                Switch(
                                    checked = settings.lowEndModeEnabled,
                                    onCheckedChange = { onUpdateSettings(settings.copy(lowEndModeEnabled = it)) }
                                )
                            }

                            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))

                            // Reduce Motion
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text("Reduce Motion", fontWeight = FontWeight.SemiBold)
                                    Text("Minimizes transitions for max responsiveness", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                                Switch(
                                    checked = settings.reduceMotion,
                                    onCheckedChange = { onUpdateSettings(settings.copy(reduceMotion = it)) }
                                )
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
                            .liquidGlass(
                                enabled = settings.liquidGlassEnabled,
                                lowEndMode = settings.lowEndModeEnabled,
                                shape = RoundedCornerShape(14.dp)
                            ),
                        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
                    ) {
                        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = if (settings.toolbarPosition == ToolbarPosition.BOTTOM) "Bottom Toolbar (Safari Style)" else "Top Toolbar (Chrome Style)",
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Text("Customize address & search bar placement for one-handed reach", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }

                            Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                                FilterChip(
                                    selected = settings.toolbarPosition == ToolbarPosition.BOTTOM,
                                    onClick = { onUpdateSettings(settings.copy(toolbarPosition = ToolbarPosition.BOTTOM)) },
                                    label = { Text("Bottom Bar (Safari)") },
                                    leadingIcon = { Icon(Icons.Default.VerticalAlignBottom, contentDescription = null, modifier = Modifier.size(16.dp)) }
                                )
                                FilterChip(
                                    selected = settings.toolbarPosition == ToolbarPosition.TOP,
                                    onClick = { onUpdateSettings(settings.copy(toolbarPosition = ToolbarPosition.TOP)) },
                                    label = { Text("Top Bar (Chrome)") },
                                    leadingIcon = { Icon(Icons.Default.VerticalAlignTop, contentDescription = null, modifier = Modifier.size(16.dp)) }
                                )
                            }
                        }
                    }
                }

                // Section: Theme Presets (Opera GX, Chrome Light/Dark, Electric Blue)
                item {
                    Text(
                        text = "Browser Theme & Engine",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .liquidGlass(
                                enabled = settings.liquidGlassEnabled,
                                lowEndMode = settings.lowEndModeEnabled,
                                shape = RoundedCornerShape(14.dp)
                            ),
                        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
                    ) {
                        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Text("Theme Preset", fontWeight = FontWeight.SemiBold)
                            for (t in BrowserTheme.values()) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(t.label, fontSize = 13.sp)
                                    RadioButton(
                                        selected = (settings.theme == t),
                                        onClick = { onUpdateSettings(settings.copy(theme = t)) }
                                    )
                                }
                            }

                            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))

                            Text("Default Search Engine", fontWeight = FontWeight.SemiBold)
                            for (engine in SearchEngine.values()) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(engine.displayName, fontSize = 13.sp)
                                    RadioButton(
                                        selected = (settings.searchEngine == engine),
                                        onClick = { onUpdateSettings(settings.copy(searchEngine = engine)) }
                                    )
                                }
                            }
                        }
                    }
                }

                // Section: Desktop Mode & Accessibility (Reader Mode & Zoom)
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
                            .liquidGlass(
                                enabled = settings.liquidGlassEnabled,
                                lowEndMode = settings.lowEndModeEnabled,
                                shape = RoundedCornerShape(14.dp)
                            ),
                        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
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

                // Section: Firebase Cloud Sync
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onOpenFirebaseAccount() }
                            .liquidGlass(
                                enabled = settings.liquidGlassEnabled,
                                lowEndMode = settings.lowEndModeEnabled,
                                shape = RoundedCornerShape(14.dp)
                            ),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFA000).copy(alpha = 0.1f))
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Icon(Icons.Default.CloudSync, contentDescription = null, tint = Color(0xFFF57C00), modifier = Modifier.size(24.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Firebase Cloud Account & Sync", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Text("Manage cloud session and sync data", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
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
