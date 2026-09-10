package com.example.ui.components

import androidx.compose.animation.core.*
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.*
import com.example.ui.theme.GlobePalettes
import com.example.ui.theme.liquidGlass

@Composable
fun NewTabPage(
    settings: BrowserSettings,
    bookmarks: List<BookmarkItem>,
    history: List<HistoryItem>,
    totalBlocked: Int,
    onNavigate: (String) -> Unit,
    onToggleLiquidGlass: () -> Unit,
    onToggleLowEndMode: () -> Unit,
    onOpenAiWithPrompt: (String) -> Unit,
    onOpenMaps: () -> Unit,
    onOpenLens: () -> Unit = {},
    onOpenAuth: () -> Unit = {},
    onPanicWipe: () -> Unit,
    modifier: Modifier = Modifier
) {
    var ntpSearchText by remember { mutableStateOf("") }
    var showAddShortcutDialog by remember { mutableStateOf(false) }
    var newShortcutName by remember { mutableStateOf("") }
    var newShortcutUrl by remember { mutableStateOf("") }
    var newShortcutEmoji by remember { mutableStateOf("🌐") }
    var customShortcuts by remember {
        mutableStateOf(
            listOf(
                Triple("Gemini Web", "https://gemini.google.com", "✨"),
                Triple("Google Drive", "https://drive.google.com", "📁")
            )
        )
    }
    var selectedNewsCategory by remember { mutableStateOf("For You") }

    // Opening animation for Google/Globe brand dots
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.96f,
        targetValue = 1.04f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    val quickLinks = remember {
        listOf(
            Triple("Google", "https://www.google.com", "🔍"),
            Triple("Google Maps", "https://maps.google.com", "🗺️"),
            Triple("YouTube", "https://www.youtube.com", "▶️"),
            Triple("Wikipedia", "https://www.wikipedia.org", "📚"),
            Triple("GitHub", "https://github.com", "💻"),
            Triple("Opera GX", "https://www.opera.com/gx", "🎮"),
            Triple("Reddit", "https://www.reddit.com", "💬"),
            Triple("TechCrunch", "https://techcrunch.com", "⚡")
        )
    }

    val curatedNews = remember {
        listOf(
            NewsItem("1", "Next-gen Web Architectures: High Performance on Budget Phones", "TechRadar", "2h ago", "Technology", "https://techradar.com", "📱"),
            NewsItem("2", "Gemini 3.5 & Pro Redefine Mobile Browser Intelligence", "AI Insights", "4h ago", "Artificial Intelligence", "https://ai.google", "✨"),
            NewsItem("3", "Privacy by Default: How Ad Trackers are Intercepted Locally", "CyberSec Weekly", "6h ago", "Privacy", "https://eff.org", "🛡️"),
            NewsItem("4", "Google Maps Grounding Bridges Local Discovery and Web Search", "Maps Daily", "8h ago", "Navigation", "https://google.com/maps", "📍"),
            NewsItem("5", "CSS Shaders and Glassmorphism Rendering on Mobile Chips", "DevBytes", "12h ago", "Web Design", "https://web.dev", "🎨")
        )
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 24.dp, bottom = 96.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        // Hero Branding Header with Google Chrome 'G' Ring Logo
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Google Chrome / Globe 'G' Ring Logo
                Box(
                    modifier = Modifier
                        .size(76.dp)
                        .scale(if (settings.reduceMotion || settings.lowEndModeEnabled) 1f else pulseScale)
                        .clip(CircleShape)
                        .background(
                            Brush.sweepGradient(
                                listOf(
                                    Color(0xFF4285F4),
                                    Color(0xFFEA4335),
                                    Color(0xFFFBBC05),
                                    Color(0xFF34A853),
                                    Color(0xFF4285F4)
                                )
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.background),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "G",
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Black,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "Globe Browser",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )

                // Google Opening 4-color dots
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.padding(top = 4.dp)
                ) {
                    Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(Color(0xFF4285F4))) // Blue
                    Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(Color(0xFFEA4335))) // Red
                    Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(Color(0xFFFBBC05))) // Yellow
                    Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(Color(0xFF34A853))) // Green
                }

                Text(
                    text = "Liquid Glass • Gemini AI • Chrome & Safari Power Engine",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 6.dp)
                )
            }
        }

        // Dedicated Google Search Box with Google Lens Button
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .liquidGlass(
                        enabled = settings.liquidGlassEnabled,
                        lowEndMode = settings.lowEndModeEnabled,
                        shape = RoundedCornerShape(26.dp),
                        elevation = 4.dp
                    ),
                colors = CardDefaults.cardColors(
                    containerColor = if (settings.liquidGlassEnabled && !settings.lowEndModeEnabled)
                        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)
                    else
                        MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Google 4-color 'G' brand mark
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.sweepGradient(
                                    listOf(
                                        Color(0xFF4285F4),
                                        Color(0xFFEA4335),
                                        Color(0xFFFBBC05),
                                        Color(0xFF34A853),
                                        Color(0xFF4285F4)
                                    )
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "G",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    OutlinedTextField(
                        value = ntpSearchText,
                        onValueChange = { ntpSearchText = it },
                        placeholder = {
                            Text(
                                "Search ${settings.searchEngine.displayName} or type URL",
                                fontSize = 13.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color.Transparent,
                            unfocusedBorderColor = Color.Transparent
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("ntp_google_search_input"),
                        trailingIcon = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                if (ntpSearchText.isNotBlank()) {
                                    IconButton(onClick = { onNavigate(ntpSearchText) }) {
                                        Icon(Icons.Default.ArrowForward, contentDescription = "Search", tint = MaterialTheme.colorScheme.primary)
                                    }
                                }

                                // Clear, prominent Google Lens Button
                                IconButton(
                                    onClick = onOpenLens,
                                    modifier = Modifier
                                        .size(36.dp)
                                        .testTag("ntp_lens_button")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.CameraAlt,
                                        contentDescription = "Search with Google Lens",
                                        tint = Color(0xFF4285F4),
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }
                    )
                }
            }
        }

        // Search Mode Selector Bar: AI Mode, Images, Videos, News, Forums, Search Tools
        item {
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    AssistChip(
                        onClick = {
                            val q = ntpSearchText.ifBlank { "google trending" }
                            onNavigate("https://www.google.com/search?q=${java.net.URLEncoder.encode(q, "UTF-8")}")
                        },
                        label = { Text("🌐 All", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
                        shape = RoundedCornerShape(12.dp)
                    )
                }
                item {
                    AssistChip(
                        onClick = {
                            val q = ntpSearchText.ifBlank { "Explain the latest web technologies" }
                            onOpenAiWithPrompt(q)
                        },
                        label = { Text("✨ AI Mode", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = GlobePalettes.ElectricCyan) },
                        shape = RoundedCornerShape(12.dp),
                        colors = AssistChipDefaults.assistChipColors(containerColor = GlobePalettes.ElectricCyan.copy(alpha = 0.15f))
                    )
                }
                item {
                    AssistChip(
                        onClick = {
                            val q = ntpSearchText.ifBlank { "high resolution wallpapers" }
                            onNavigate("https://www.google.com/search?tbm=isch&q=${java.net.URLEncoder.encode(q, "UTF-8")}")
                        },
                        label = { Text("🖼️ Images", fontSize = 11.sp) },
                        shape = RoundedCornerShape(12.dp)
                    )
                }
                item {
                    AssistChip(
                        onClick = {
                            val q = ntpSearchText.ifBlank { "popular videos" }
                            onNavigate("https://www.google.com/search?tbm=vid&q=${java.net.URLEncoder.encode(q, "UTF-8")}")
                        },
                        label = { Text("🎥 Videos", fontSize = 11.sp) },
                        shape = RoundedCornerShape(12.dp)
                    )
                }
                item {
                    AssistChip(
                        onClick = {
                            val q = ntpSearchText.ifBlank { "breaking news" }
                            onNavigate("https://news.google.com/search?q=${java.net.URLEncoder.encode(q, "UTF-8")}")
                        },
                        label = { Text("📰 News", fontSize = 11.sp) },
                        shape = RoundedCornerShape(12.dp)
                    )
                }
                item {
                    AssistChip(
                        onClick = {
                            val q = ntpSearchText.ifBlank { "best android browsers" }
                            onNavigate("https://www.google.com/search?q=${java.net.URLEncoder.encode(q, "UTF-8")}+site:reddit.com+OR+site:quora.com")
                        },
                        label = { Text("💬 Forums", fontSize = 11.sp) },
                        shape = RoundedCornerShape(12.dp)
                    )
                }
                item {
                    AssistChip(
                        onClick = {
                            val q = ntpSearchText.ifBlank { "technology" }
                            onNavigate("https://www.google.com/search?q=${java.net.URLEncoder.encode(q, "UTF-8")}&tbs=qdr:d")
                        },
                        label = { Text("⚡ Past 24h", fontSize = 11.sp) },
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            }
        }

        // Google Lens Visual Discovery & Firebase Account Quick Access Row
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Google Lens Visual Search Card
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onOpenLens() }
                        .liquidGlass(
                            enabled = settings.liquidGlassEnabled,
                            lowEndMode = settings.lowEndModeEnabled,
                            shape = RoundedCornerShape(16.dp)
                        ),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFF4285F4).copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.DocumentScanner, contentDescription = null, tint = Color(0xFF4285F4), modifier = Modifier.size(18.dp))
                        }
                        Column {
                            Text("Google Lens", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            Text("Visual Search & OCR", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }

                // Firebase Cloud Account Card
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onOpenAuth() }
                        .liquidGlass(
                            enabled = settings.liquidGlassEnabled,
                            lowEndMode = settings.lowEndModeEnabled,
                            shape = RoundedCornerShape(16.dp)
                        ),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFFFFA000).copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.CloudSync, contentDescription = null, tint = Color(0xFFF57C00), modifier = Modifier.size(18.dp))
                        }
                        Column {
                            Text("Firebase Sync", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            Text("Login & Cloud Sync", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }
        }

        // Gemini AI Quick Actions Chips
        item {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.padding(bottom = 8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "Gemini Quick Sparks",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    val aiPrompts = listOf(
                        "✨ Summarize today's top news",
                        "🗺️ Find best coffee spots nearby",
                        "💡 Explain quantum computing simply",
                        "💻 Write a Kotlin coroutine example",
                        "🛡️ How does browser fingerprinting work?"
                    )
                    items(aiPrompts) { prompt ->
                        AssistChip(
                            onClick = { onOpenAiWithPrompt(prompt) },
                            label = { Text(prompt, fontSize = 12.sp) },
                            shape = RoundedCornerShape(16.dp),
                            colors = AssistChipDefaults.assistChipColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                            ),
                            border = AssistChipDefaults.assistChipBorder(enabled = true, borderColor = MaterialTheme.colorScheme.outlineVariant)
                        )
                    }
                }
            }
        }

        // Speed Dial / Quick Links Grid with Shortcut Creator
        item {
            val combinedShortcuts = quickLinks + customShortcuts

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .liquidGlass(
                        enabled = settings.liquidGlassEnabled,
                        lowEndMode = settings.lowEndModeEnabled,
                        shape = RoundedCornerShape(18.dp)
                    ),
                colors = CardDefaults.cardColors(containerColor = Color.Transparent)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Speed Dial",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        AssistChip(
                            onClick = { showAddShortcutDialog = true },
                            label = { Text("+ Add Shortcut", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
                            leadingIcon = { Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(14.dp)) },
                            shape = RoundedCornerShape(10.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Dynamic Grid of Shortcuts
                    val totalSlots = combinedShortcuts.size + 1 // +1 for the Add button tile
                    val rowCount = (totalSlots + 3) / 4

                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        for (r in 0 until rowCount) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceAround
                            ) {
                                for (c in 0..3) {
                                    val index = r * 4 + c
                                    if (index < combinedShortcuts.size) {
                                        val link = combinedShortcuts[index]
                                        Column(
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(12.dp))
                                                .clickable {
                                                    if (link.first == "Google Maps") onOpenMaps() else onNavigate(link.second)
                                                }
                                                .padding(6.dp)
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .size(46.dp)
                                                    .clip(CircleShape)
                                                    .background(MaterialTheme.colorScheme.surfaceVariant),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text(link.third, fontSize = 20.sp)
                                            }
                                            Spacer(modifier = Modifier.height(6.dp))
                                            Text(
                                                text = link.first,
                                                style = MaterialTheme.typography.labelSmall,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                        }
                                    } else if (index == combinedShortcuts.size) {
                                        // "+ Add" Tile
                                        Column(
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(12.dp))
                                                .clickable { showAddShortcutDialog = true }
                                                .padding(6.dp)
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .size(46.dp)
                                                    .clip(CircleShape)
                                                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Add,
                                                    contentDescription = "Add Shortcut",
                                                    tint = MaterialTheme.colorScheme.primary,
                                                    modifier = Modifier.size(24.dp)
                                                )
                                            }
                                            Spacer(modifier = Modifier.height(6.dp))
                                            Text(
                                                text = "Add",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = MaterialTheme.colorScheme.primary,
                                                fontWeight = FontWeight.Medium
                                            )
                                        }
                                    } else {
                                        Spacer(modifier = Modifier.size(46.dp))
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Real-Time Privacy & System Status Cards (Dual Row)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Privacy Shield Card
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .liquidGlass(
                            enabled = settings.liquidGlassEnabled,
                            lowEndMode = settings.lowEndModeEnabled,
                            shape = RoundedCornerShape(16.dp)
                        ),
                    colors = CardDefaults.cardColors(containerColor = Color.Transparent)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(
                                imageVector = Icons.Default.Shield,
                                contentDescription = null,
                                tint = GlobePalettes.ElectricCyan,
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                text = "ACTIVE",
                                style = MaterialTheme.typography.labelSmall,
                                color = GlobePalettes.ElectricCyan,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "$totalBlocked Blocked",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Trackers & Ads stopped",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Performance Mode Card (Realme Note 60 Optimization)
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .liquidGlass(
                            enabled = settings.liquidGlassEnabled,
                            lowEndMode = settings.lowEndModeEnabled,
                            shape = RoundedCornerShape(16.dp)
                        )
                        .clickable { onToggleLowEndMode() },
                    colors = CardDefaults.cardColors(containerColor = Color.Transparent)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(
                                imageVector = Icons.Default.Speed,
                                contentDescription = null,
                                tint = if (settings.lowEndModeEnabled) Color(0xFF10B981) else MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                            Switch(
                                checked = settings.lowEndModeEnabled,
                                onCheckedChange = { onToggleLowEndMode() },
                                modifier = Modifier.scale(0.7f)
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (settings.lowEndModeEnabled) "Low-End Mode ON" else "Standard Mode",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = if (settings.lowEndModeEnabled) "Optimized for budget chips" else "Tap for Realme Note 60",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        // Liquid Glass UI Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .liquidGlass(
                        enabled = settings.liquidGlassEnabled,
                        lowEndMode = settings.lowEndModeEnabled,
                        shape = RoundedCornerShape(16.dp)
                    )
                    .clickable { onToggleLiquidGlass() },
                colors = CardDefaults.cardColors(containerColor = Color.Transparent)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.BlurOn,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                        Column {
                            Text(
                                text = "Liquid Glass Effect",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = if (settings.liquidGlassEnabled) "Glossy translucent frosted glass" else "Solid high-contrast surface (Faster)",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Switch(
                        checked = settings.liquidGlassEnabled,
                        onCheckedChange = { onToggleLiquidGlass() }
                    )
                }
            }
        }

        // Discover & Chrome News Feed Section with Category Filter
        item {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Chrome Discover Feed",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "Personalized",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    val categories = listOf("For You", "Tech & AI", "Gaming GX", "World News", "Science", "Crypto")
                    items(categories) { cat ->
                        FilterChip(
                            selected = (selectedNewsCategory == cat),
                            onClick = { selectedNewsCategory = cat },
                            label = { Text(cat, fontSize = 11.sp) }
                        )
                    }
                }
            }
        }

        val allNewsList = listOf(
            NewsItem("1", "Next-gen Web Architectures: High Performance on Budget Phones", "TechRadar", "2h ago", "Tech & AI", "https://techradar.com", "📱"),
            NewsItem("2", "Gemini 3.5 & Pro Redefine Mobile Browser Intelligence", "AI Insights", "4h ago", "Tech & AI", "https://ai.google", "✨"),
            NewsItem("3", "Privacy by Default: How Ad Trackers are Intercepted Locally", "CyberSec Weekly", "6h ago", "For You", "https://eff.org", "🛡️"),
            NewsItem("4", "Opera GX and Chrome Introduce Dynamic Ram and CPU Limiters", "Gaming Central", "1h ago", "Gaming GX", "https://opera.com/gx", "🎮"),
            NewsItem("5", "Google Maps Grounding Bridges Local Discovery and Web Search", "Maps Daily", "8h ago", "For You", "https://google.com/maps", "📍"),
            NewsItem("6", "James Webb Space Telescope Captures Distant Galaxy Formation", "NASA Science", "3h ago", "Science", "https://nasa.gov", "🔭"),
            NewsItem("7", "Global Tech Markets Rally on AI Infrastructure Deployments", "Bloomberg Tech", "5h ago", "Crypto", "https://bloomberg.com", "📈"),
            NewsItem("8", "CSS Shaders and Glassmorphism Rendering on Mobile Chips", "DevBytes", "12h ago", "Tech & AI", "https://web.dev", "🎨"),
            NewsItem("9", "Next-Gen Unreal Engine 5.5 Features Mobile Nanite Geometry", "IGN Gaming", "7h ago", "Gaming GX", "https://ign.com", "🕹️"),
            NewsItem("10", "Quantum Computing Breakthrough in Fault-Tolerant Qubits", "Science Daily", "14h ago", "Science", "https://sciencedaily.com", "⚛️")
        )

        val filteredNews = if (selectedNewsCategory == "For You") allNewsList else allNewsList.filter { it.category == selectedNewsCategory }

        items(filteredNews) { news ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .liquidGlass(
                        enabled = settings.liquidGlassEnabled,
                        lowEndMode = settings.lowEndModeEnabled,
                        shape = RoundedCornerShape(14.dp)
                    )
                    .clickable { onNavigate(news.url) },
                colors = CardDefaults.cardColors(containerColor = Color.Transparent)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(news.icon, fontSize = 28.sp)
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = news.title,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(
                                text = news.source,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text("•", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(
                                text = news.timeAgo,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    Icon(
                        imageVector = Icons.Default.OpenInNew,
                        contentDescription = "Read",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }

    // Shortcut Creator Dialog
    if (showAddShortcutDialog) {
        AlertDialog(
            onDismissRequest = { showAddShortcutDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(Icons.Default.BookmarkAdd, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Text("Add Speed Dial Shortcut", fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = newShortcutName,
                        onValueChange = { newShortcutName = it },
                        label = { Text("Site Name") },
                        placeholder = { Text("e.g. Reddit, Discord") },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth().testTag("shortcut_name_input")
                    )

                    OutlinedTextField(
                        value = newShortcutUrl,
                        onValueChange = { newShortcutUrl = it },
                        label = { Text("URL") },
                        placeholder = { Text("https://example.com") },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth().testTag("shortcut_url_input")
                    )

                    Text("Pick an Icon / Emoji:", style = MaterialTheme.typography.labelMedium)
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        val emojis = listOf("🌐", "🔥", "⚡", "🎮", "💻", "🎵", "🛍️", "💬", "🚀", "📱", "📰", "✨")
                        items(emojis) { emoji ->
                            Surface(
                                shape = CircleShape,
                                color = if (newShortcutEmoji == emoji) MaterialTheme.colorScheme.primary.copy(alpha = 0.25f) else MaterialTheme.colorScheme.surfaceVariant,
                                modifier = Modifier
                                    .size(36.dp)
                                    .clickable { newShortcutEmoji = emoji }
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(emoji, fontSize = 16.sp)
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newShortcutName.isNotBlank() && newShortcutUrl.isNotBlank()) {
                            val formattedUrl = if (!newShortcutUrl.startsWith("http://") && !newShortcutUrl.startsWith("https://")) {
                                "https://$newShortcutUrl"
                            } else newShortcutUrl

                            customShortcuts = customShortcuts + Triple(newShortcutName.trim(), formattedUrl.trim(), newShortcutEmoji)
                            newShortcutName = ""
                            newShortcutUrl = ""
                            showAddShortcutDialog = false
                        }
                    },
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Add Shortcut")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddShortcutDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
