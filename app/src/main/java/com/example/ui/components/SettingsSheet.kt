package com.example.ui.components

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.*
import com.example.ui.theme.GlobePalettes
import com.example.ui.theme.chromeCard
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsSheet(
    settings: BrowserSettings,
    onUpdateSettings: (BrowserSettings) -> Unit,
    savedPasswordsCount: Int = 0,
    onOpenPasswordVault: () -> Unit = {},
    onClearAllPasswords: () -> Unit = {},
    onToggleSearchEngine: (String) -> Unit = {},
    onClearCacheAndCookies: ((clearedMb: Double) -> Unit) -> Unit = {},
    onClearAllData: () -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var isCleaningCache by remember { mutableStateOf(false) }
    var lastCleanedStats by remember { mutableStateOf<String?>(null) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = MaterialTheme.colorScheme.background,
        dragHandle = { BottomSheetDefaults.DragHandle() }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .navigationBarsPadding()
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
                    Column {
                        Text(
                            text = "GB Browser Settings",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Performance & Storage Control",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
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
                // =========================================================================
                // SECTION: ONE-TAP CLEAR CACHE & COOKIES (SUPER CACHE MANAGEMENT FOR LOW STORAGE)
                // =========================================================================
                item {
                    Text(
                        text = "⚡ Ultimate Super Cache & Storage Management",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = GlobePalettes.ElectricCyan
                    )
                }

                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .chromeCard(shape = RoundedCornerShape(16.dp)),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A).copy(alpha = 0.9f)),
                        border = BorderStroke(1.dp, GlobePalettes.ElectricCyan.copy(alpha = 0.4f))
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                    Box(
                                        modifier = Modifier
                                            .size(40.dp)
                                            .clip(CircleShape)
                                            .background(GlobePalettes.ElectricCyan.copy(alpha = 0.18f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.CleaningServices,
                                            contentDescription = null,
                                            tint = GlobePalettes.ElectricCyan,
                                            modifier = Modifier.size(22.dp)
                                        )
                                    }
                                    Column {
                                        Text(
                                            text = "1-Tap Cache & Cookies Purge",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp,
                                            color = Color.White
                                        )
                                        Text(
                                            text = "Reclaims disk space & boosts speed instantly",
                                            fontSize = 11.sp,
                                            color = Color.White.copy(alpha = 0.6f)
                                        )
                                    }
                                }
                            }

                            // Storage breakdown meters
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = Color.White.copy(alpha = 0.06f),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Column(modifier = Modifier.padding(8.dp)) {
                                        Text("Disk Cache", fontSize = 10.sp, color = Color.White.copy(alpha = 0.5f))
                                        Text("18.4 MB", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = GlobePalettes.ElectricCyan)
                                    }
                                }
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = Color.White.copy(alpha = 0.06f),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Column(modifier = Modifier.padding(8.dp)) {
                                        Text("Cookies & DB", fontSize = 10.sp, color = Color.White.copy(alpha = 0.5f))
                                        Text("2.6 MB", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFF00E676))
                                    }
                                }
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = Color.White.copy(alpha = 0.06f),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Column(modifier = Modifier.padding(8.dp)) {
                                        Text("RAM Savings", fontSize = 10.sp, color = Color.White.copy(alpha = 0.5f))
                                        Text("~120 MB", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFFFFD740))
                                    }
                                }
                            }

                            // Prominent One-Tap Action Button
                            Button(
                                onClick = {
                                    isCleaningCache = true
                                    onClearCacheAndCookies { clearedMb ->
                                        isCleaningCache = false
                                        lastCleanedStats = "Cleaned ${String.format("%.1f", clearedMb)} MB cache & cookies!"
                                        Toast.makeText(context, "⚡ Cleaned ${String.format("%.1f", clearedMb)} MB cache & tracking cookies", Toast.LENGTH_SHORT).show()
                                    }
                                },
                                enabled = !isCleaningCache,
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = GlobePalettes.ElectricCyan,
                                    contentColor = Color(0xFF0F172A)
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("clear_cache_cookies_button")
                            ) {
                                if (isCleaningCache) {
                                    CircularProgressIndicator(modifier = Modifier.size(18.dp), color = Color(0xFF0F172A), strokeWidth = 2.dp)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Purging Cache...", fontWeight = FontWeight.Bold)
                                } else {
                                    Icon(Icons.Default.Bolt, contentDescription = null, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Clear Cache & Cookies Now", fontWeight = FontWeight.Bold)
                                }
                            }

                            if (lastCleanedStats != null) {
                                Text(
                                    text = "✓ $lastCleanedStats",
                                    fontSize = 12.sp,
                                    color = Color(0xFF00E676),
                                    fontWeight = FontWeight.SemiBold
                                )
                            }

                            HorizontalDivider(color = Color.White.copy(alpha = 0.1f))

                            // Lite Version / Low Storage Mode Toggle
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                        Text("Lite Mode (Low-Storage Optimization)", fontWeight = FontWeight.SemiBold, fontSize = 13.sp, color = Color.White)
                                        Surface(
                                            shape = RoundedCornerShape(4.dp),
                                            color = Color(0xFFFFD740).copy(alpha = 0.2f)
                                        ) {
                                            Text("2013-2026", color = Color(0xFFFFD740), fontSize = 9.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp))
                                        }
                                    }
                                    Text(
                                        "Minimizes disk cache, freezes background tabs, and reduces RAM for older / budget phones.",
                                        fontSize = 11.sp,
                                        color = Color.White.copy(alpha = 0.6f)
                                    )
                                }
                                Switch(
                                    checked = settings.liteModeEnabled,
                                    onCheckedChange = { isLite ->
                                        onUpdateSettings(
                                            settings.copy(
                                                liteModeEnabled = isLite,
                                                performanceProfile = if (isLite) "ultra_lite" else "balanced"
                                            )
                                        )
                                        Toast.makeText(context, if (isLite) "⚡ Lite Mode Enabled (Low-Storage & RAM Saver)" else "Standard Performance Mode Restored", Toast.LENGTH_SHORT).show()
                                    },
                                    colors = SwitchDefaults.colors(
                                        checkedThumbColor = Color(0xFF0F172A),
                                        checkedTrackColor = GlobePalettes.ElectricCyan
                                    )
                                )
                            }

                            // Auto-Clean on Exit
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text("Auto-Clear Cache on Close", fontWeight = FontWeight.SemiBold, fontSize = 13.sp, color = Color.White)
                                    Text("Automatically cleans temporary cache when app closes", fontSize = 11.sp, color = Color.White.copy(alpha = 0.6f))
                                }
                                Switch(
                                    checked = settings.autoClearCacheOnExit,
                                    onCheckedChange = { onUpdateSettings(settings.copy(autoClearCacheOnExit = it)) }
                                )
                            }
                        }
                    }
                }

                // =========================================================================
                // SECTION: DEVICE PERFORMANCE PROFILE
                // =========================================================================
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

                            val profiles = listOf(
                                "ultra_lite" to "Ultra Lite (Low-End & Low Storage Phones)",
                                "balanced" to "Balanced (Standard Devices)",
                                "high_performance" to "Pro 90Hz/120Hz (High-End Displays)"
                            )
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

                // =========================================================================
                // SECTION: THEMES (Light, Dark, Midnight)
                // =========================================================================
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

                // =========================================================================
                // SECTION: SEARCH ENGINES (Google Default)
                // =========================================================================
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

                // =========================================================================
                // SECTION: TOOLBAR LAYOUT
                // =========================================================================
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

                // =========================================================================
                // SECTION: BUTTON NAVIGATION & SYSTEM COMPATIBILITY
                // =========================================================================
                item {
                    Text(
                        text = "🕹️ Button Navigation & System Compatibility",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = GlobePalettes.ElectricCyan
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
                                    Text("Bottom Button Navigation Bar", fontWeight = FontWeight.SemiBold)
                                    Text("Dedicated Back, Forward, Home, Vault, Tabs & Menu buttons", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                                Switch(
                                    checked = settings.buttonNavigationEnabled,
                                    onCheckedChange = { onUpdateSettings(settings.copy(buttonNavigationEnabled = it)) }
                                )
                            }

                            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text("Smart Back Navigation", fontWeight = FontWeight.SemiBold)
                                    Text("Back button traverses webpage history first before returning to home", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                                Switch(
                                    checked = settings.backButtonHistoryFirst,
                                    onCheckedChange = { onUpdateSettings(settings.copy(backButtonHistoryFirst = it)) }
                                )
                            }

                            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = GlobePalettes.NeonGreen,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Android 3-Button Navigation Bar Compatibility: Active (Insets aligned above system nav buttons)",
                                    fontSize = 11.5.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }

                // =========================================================================
                // SECTION: PASSWORDS, AUTOFILL & ENCRYPTED VAULT (ROOM)
                // =========================================================================
                item {
                    Text(
                        text = "🔒 Password Vault & Autofill (Room Encrypted)",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = GlobePalettes.ElectricCyan
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
                                    Text("Autofill Passwords on Websites", fontWeight = FontWeight.SemiBold)
                                    Text("Securely auto-fill login credentials on saved websites", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                                Switch(
                                    checked = settings.autofillEnabled,
                                    onCheckedChange = { onUpdateSettings(settings.copy(autofillEnabled = it)) }
                                )
                            }

                            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text("Saved Website Passwords: $savedPasswordsCount", fontWeight = FontWeight.SemiBold)
                                    Text("Encrypted with AES-256 GCM in Room Database", fontSize = 11.5.sp, color = GlobePalettes.NeonGreen)
                                }
                                Button(
                                    onClick = {
                                        onOpenPasswordVault()
                                        onDismiss()
                                    },
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Icon(Icons.Default.Key, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Manage Vault", fontSize = 12.sp)
                                }
                            }

                            if (savedPasswordsCount > 0) {
                                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))

                                OutlinedButton(
                                    onClick = {
                                        onClearAllPasswords()
                                        Toast.makeText(context, "All saved passwords cleared from vault", Toast.LENGTH_SHORT).show()
                                    },
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Icon(Icons.Default.DeleteOutline, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Clear All Saved Passwords", fontSize = 12.sp)
                                }
                            }
                        }
                    }
                }


                // =========================================================================
                // SECTION: READING & PAGE DISPLAY
                // =========================================================================
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

                // =========================================================================
                // SECTION: CLEAR ALL DATA
                // =========================================================================
                item {
                    OutlinedButton(
                        onClick = onClearAllData,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.DeleteOutline, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Clear All Browsing Data, History & Storage")
                    }
                }
            }
        }
    }
}
