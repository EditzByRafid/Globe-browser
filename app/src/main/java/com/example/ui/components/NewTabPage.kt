package com.example.ui.components

import android.widget.Toast
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.*
import com.example.ui.theme.GlobePalettes
import com.example.ui.theme.chromeCard

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun NewTabPage(
    settings: BrowserSettings,
    bookmarks: List<BookmarkItem>,
    history: List<HistoryItem>,
    totalBlocked: Int,
    onNavigate: (String) -> Unit,
    onSetTheme: (BrowserTheme) -> Unit = {},
    onSelectSearchEngine: (SearchEngine) -> Unit = {},
    onOpenSearchOverlay: () -> Unit,
    onOpenMaps: () -> Unit,
    onOpenLens: () -> Unit = {},
    onPanicWipe: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showAddShortcutDialog by remember { mutableStateOf(false) }
    var shortcutToManage by remember { mutableStateOf<Pair<String, String>?>(null) }
    var newShortcutName by remember { mutableStateOf("") }
    var newShortcutUrl by remember { mutableStateOf("") }
    val context = LocalContext.current

    var customShortcuts by remember {
        mutableStateOf(
            listOf(
                "Google Drive" to "https://drive.google.com",
                "ChatGPT" to "https://chat.openai.com",
                "Spotify" to "https://open.spotify.com",
                "Discord" to "https://discord.com"
            )
        )
    }
    var selectedNewsCategory by remember { mutableStateOf("For You") }

    val defaultShortcuts = remember {
        listOf(
            "Google" to "https://www.google.com",
            "YouTube" to "https://www.youtube.com",
            "GitHub" to "https://github.com",
            "Wikipedia" to "https://www.wikipedia.org",
            "Reddit" to "https://www.reddit.com",
            "X" to "https://x.com",
            "Maps" to "https://maps.google.com",
            "Amazon" to "https://www.amazon.com",
            "TechCrunch" to "https://techcrunch.com"
        )
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 96.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Hero Branding Header with GB Globe Logo Badge & Mask
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                GlobeLogoBadge(
                    size = 88.dp,
                    animated = !settings.reduceMotion
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "GB",
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.5.sp,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                    ) {
                        Text(
                            text = "BROWSER",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }

                Text(
                    text = "Ultra-Fast • Private • Native Performance",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                    modifier = Modifier.padding(top = 2.dp)
                )

                if (settings.liteModeEnabled) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = GlobePalettes.ElectricCyan.copy(alpha = 0.15f),
                        border = BorderStroke(1.dp, GlobePalettes.ElectricCyan.copy(alpha = 0.4f))
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text("⚡", fontSize = 12.sp)
                            Text(
                                text = "Lite Version • Super Cache Optimizer Active",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = GlobePalettes.ElectricCyan
                            )
                        }
                    }
                }
            }
        }

        // Spacious Search Bar Card (Opens Fullscreen Search Overlay)
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .chromeCard(shape = RoundedCornerShape(26.dp))
                    .clickable { onOpenSearchOverlay() }
                    .testTag("ntp_search_card_touch"),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Search Engine Vector Logo
                    SearchEngineVectorLogo(
                        engine = settings.searchEngine,
                        size = 24.dp
                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    Text(
                        text = "Search ${settings.searchEngine.displayName} or enter URL...",
                        fontSize = 15.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )

                    // Google Lens Visual Search Icon
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.9f))
                            .clickable { onOpenLens() }
                            .testTag("ntp_lens_button"),
                        contentAlignment = Alignment.Center
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
        }

        // Quick Search Engine Selector Row with Real Vector Logos
        item {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Search Engine (Default: Google)",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val availableEngines = SearchEngine.values().filter {
                        it == SearchEngine.GOOGLE || settings.enabledSearchEngines.contains(it.name)
                    }

                    items(availableEngines) { engine ->
                        val isSelected = (settings.searchEngine == engine)
                        FilterChip(
                            selected = isSelected,
                            onClick = { onSelectSearchEngine(engine) },
                            leadingIcon = {
                                SearchEngineVectorLogo(engine = engine, size = 18.dp)
                            },
                            label = { Text(engine.displayName, fontSize = 12.sp) },
                            shape = RoundedCornerShape(12.dp)
                        )
                    }
                }
            }
        }

        // Quick Theme Selector (Light, Dark, Midnight OLED)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Display Theme:",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    for (themeOption in BrowserTheme.values()) {
                        val selected = (settings.theme == themeOption)
                        FilterChip(
                            selected = selected,
                            onClick = { onSetTheme(themeOption) },
                            label = { Text(themeOption.label, fontSize = 11.sp, fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal) },
                            shape = RoundedCornerShape(10.dp)
                        )
                    }
                }
            }
        }

        // Speed Dial / Quick Links Grid (Real Vector Logos)
        item {
            val combinedShortcuts = defaultShortcuts + customShortcuts

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .chromeCard(shape = RoundedCornerShape(18.dp)),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f))
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

                    Spacer(modifier = Modifier.height(14.dp))

                    val totalSlots = combinedShortcuts.size + 1
                    val rowCount = (totalSlots + 3) / 4

                    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
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
                                                .combinedClickable(
                                                    onClick = {
                                                        if (link.first == "Maps") onOpenMaps() else onNavigate(link.second)
                                                    },
                                                    onLongClick = {
                                                        shortcutToManage = link
                                                    }
                                                )
                                                .padding(6.dp)
                                        ) {
                                            BrandVectorLogo(
                                                name = link.first,
                                                size = 46.dp
                                            )
                                            Spacer(modifier = Modifier.height(6.dp))
                                            Text(
                                                text = link.first,
                                                style = MaterialTheme.typography.labelSmall,
                                                fontWeight = FontWeight.Medium,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                        }
                                    } else if (index == combinedShortcuts.size) {
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

        // Privacy Shield Summary Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .chromeCard(shape = RoundedCornerShape(16.dp)),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF34A853).copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Shield,
                            contentDescription = null,
                            tint = Color(0xFF34A853),
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(
                                text = "GB Privacy Shield",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                            SuggestionChip(
                                onClick = {},
                                label = { Text("ACTIVE", fontSize = 9.sp, fontWeight = FontWeight.Bold) },
                                modifier = Modifier.height(20.dp)
                            )
                        }
                        Text(
                            text = "$totalBlocked ads & tracking telemetry blocked on-device",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        // Discover Feed Section
        item {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Discover Feed",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "Top Stories",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    val categories = listOf("For You", "Tech", "Gaming", "World", "Science")
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
            NewsItem("1", "Next-gen Mobile Web Architectures: High Performance on Android", "TechRadar", "2h ago", "Tech", "https://techradar.com", "TechCrunch"),
            NewsItem("2", "Privacy by Default: How Ad Trackers are Blocked Completely On-Device", "CyberSec Weekly", "4h ago", "For You", "https://eff.org", "Wikipedia"),
            NewsItem("3", "Hardware Accelerated 90Hz Mobile Browsing Breakthroughs", "Android Central", "1h ago", "Gaming", "https://androidcentral.com", "GitHub"),
            NewsItem("4", "James Webb Space Telescope Captures Distant Star Cluster", "NASA Science", "3h ago", "Science", "https://nasa.gov", "Reddit"),
            NewsItem("5", "Global Web Standards 2026: Fast HTML5 Canvas & WebGL Execution", "W3C Tech", "5h ago", "World", "https://w3.org", "Google")
        )

        val filteredNews = if (selectedNewsCategory == "For You") allNewsList else allNewsList.filter { it.category == selectedNewsCategory }

        items(filteredNews) { news ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .chromeCard(shape = RoundedCornerShape(14.dp))
                    .clickable { onNavigate(news.url) },
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    BrandVectorLogo(name = news.icon, size = 36.dp)

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
                        modifier = Modifier.size(18.dp)
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
                        placeholder = { Text("e.g. Reddit, Twitch, Discord") },
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
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newShortcutName.isNotBlank() && newShortcutUrl.isNotBlank()) {
                            val formattedUrl = if (!newShortcutUrl.startsWith("http://") && !newShortcutUrl.startsWith("https://")) {
                                "https://$newShortcutUrl"
                            } else newShortcutUrl

                            customShortcuts = customShortcuts + (newShortcutName.trim() to formattedUrl.trim())
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

    // Shortcut Manage & Delete Dialog
    shortcutToManage?.let { shortcut ->
        val isDefault = defaultShortcuts.any { it.first == shortcut.first }
        AlertDialog(
            onDismissRequest = { shortcutToManage = null },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    BrandVectorLogo(name = shortcut.first, size = 28.dp)
                    Text(shortcut.first, fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("URL: ${shortcut.second}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    if (isDefault) {
                        Text("This is a built-in speed dial tile.", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f))
                    } else {
                        Text("Manage or remove this shortcut from your home screen.", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f))
                    }
                }
            },
            confirmButton = {
                if (!isDefault) {
                    Button(
                        onClick = {
                            customShortcuts = customShortcuts.filter { it.first != shortcut.first }
                            shortcutToManage = null
                            Toast.makeText(context, "Removed ${shortcut.first}", Toast.LENGTH_SHORT).show()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Remove Shortcut")
                    }
                } else {
                    Button(
                        onClick = {
                            onNavigate(shortcut.second)
                            shortcutToManage = null
                        },
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Open")
                    }
                }
            },
            dismissButton = {
                TextButton(onClick = { shortcutToManage = null }) {
                    Text("Close")
                }
            }
        )
    }
}

