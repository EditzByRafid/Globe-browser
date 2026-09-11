package com.example.ui.components

import android.content.Intent
import android.net.Uri
import android.widget.Toast
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.*
import com.example.ui.theme.GlobePalettes
import com.example.ui.theme.chromeCard

@Composable
fun NewTabPage(
    settings: BrowserSettings,
    bookmarks: List<BookmarkItem>,
    history: List<HistoryItem>,
    totalBlocked: Int,
    onNavigate: (String) -> Unit,
    onSetTheme: (BrowserTheme) -> Unit = {},
    onSelectSearchEngine: (SearchEngine) -> Unit = {},
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
    var showApkDownloadDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current

    var customShortcuts by remember {
        mutableStateOf(
            listOf(
                Triple("Gemini AI", "https://gemini.google.com", "✨"),
                Triple("Google Drive", "https://drive.google.com", "📁")
            )
        )
    }
    var selectedNewsCategory by remember { mutableStateOf("For You") }

    // Opening animation for Google/Globe brand dots
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.97f,
        targetValue = 1.03f,
        animationSpec = infiniteRepeatable(
            animation = tween(1600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    val defaultShortcuts = remember {
        listOf(
            Triple("Google", "https://www.google.com", "🔍"),
            Triple("Maps", "https://maps.google.com", "🗺️"),
            Triple("YouTube", "https://www.youtube.com", "▶️"),
            Triple("Wikipedia", "https://www.wikipedia.org", "📚"),
            Triple("GitHub", "https://github.com", "💻"),
            Triple("Reddit", "https://www.reddit.com", "💬"),
            Triple("TechCrunch", "https://techcrunch.com", "⚡")
        )
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 18.dp, bottom = 96.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Physical Device / Download APK Banner
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .chromeCard(shape = RoundedCornerShape(16.dp))
                    .clickable { showApkDownloadDialog = true },
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.45f)
                )
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
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.DownloadForOffline,
                            contentDescription = "Download APK",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(
                                text = "Run on Your Real Phone (120Hz)",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                            SuggestionChip(
                                onClick = { showApkDownloadDialog = true },
                                label = { Text("FAST", fontSize = 9.sp, fontWeight = FontWeight.Bold) },
                                modifier = Modifier.height(20.dp)
                            )
                        }
                        Text(
                            text = "Download APK to bypass browser emulator lag & inverted colors",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    FilledTonalButton(
                        onClick = { showApkDownloadDialog = true },
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text("APK", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Hero Branding Header with Google Chrome 'G' Ring Logo
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Google Chrome 4-color Ring 'G'
                Box(
                    modifier = Modifier
                        .size(74.dp)
                        .scale(if (settings.reduceMotion) 1f else pulseScale)
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
                            .size(52.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.background),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "G",
                            fontSize = 30.sp,
                            fontWeight = FontWeight.Black,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Globe Chrome",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )

                // Google 4-color dots
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.padding(top = 4.dp)
                ) {
                    Box(modifier = Modifier.size(7.dp).clip(CircleShape).background(Color(0xFF4285F4)))
                    Box(modifier = Modifier.size(7.dp).clip(CircleShape).background(Color(0xFFEA4335)))
                    Box(modifier = Modifier.size(7.dp).clip(CircleShape).background(Color(0xFFFBBC05)))
                    Box(modifier = Modifier.size(7.dp).clip(CircleShape).background(Color(0xFF34A853)))
                }
            }
        }

        // Google / Omnibox Search Field with Lens button
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .chromeCard(shape = RoundedCornerShape(26.dp)),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Search Engine icon
                    Text(
                        text = settings.searchEngine.iconEmoji,
                        fontSize = 18.sp,
                        modifier = Modifier.padding(start = 2.dp)
                    )

                    Spacer(modifier = Modifier.width(10.dp))

                    OutlinedTextField(
                        value = ntpSearchText,
                        onValueChange = { ntpSearchText = it },
                        placeholder = {
                            Text(
                                "Search ${settings.searchEngine.displayName} or enter URL",
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

        // Quick Search Engine Selector Row (Google default + toggled engines)
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
                            label = { Text("${engine.iconEmoji} ${engine.displayName}", fontSize = 11.sp) },
                            shape = RoundedCornerShape(12.dp)
                        )
                    }
                }
            }
        }

        // Quick Theme Selector (Light, Dark, Midnight)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Theme Modes:",
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

        // Gemini AI Quick Sparks
        item {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.padding(bottom = 6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "Gemini AI Sparks",
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
                        "✨ Summarize top news",
                        "🗺️ Find best spots nearby",
                        "💡 Explain quantum physics",
                        "💻 Write a Kotlin example",
                        "🛡️ How do ad blockers work?"
                    )
                    items(aiPrompts) { prompt ->
                        AssistChip(
                            onClick = { onOpenAiWithPrompt(prompt) },
                            label = { Text(prompt, fontSize = 11.sp) },
                            shape = RoundedCornerShape(14.dp)
                        )
                    }
                }
            }
        }

        // Speed Dial / Quick Links Grid
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

                    Spacer(modifier = Modifier.height(12.dp))

                    val totalSlots = combinedShortcuts.size + 1
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
                                                    if (link.first == "Maps") onOpenMaps() else onNavigate(link.second)
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
                                text = "Local Privacy Shield",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                            SuggestionChip(
                                onClick = {},
                                label = { Text("ON", fontSize = 9.sp, fontWeight = FontWeight.Bold) },
                                modifier = Modifier.height(20.dp)
                            )
                        }
                        Text(
                            text = "$totalBlocked ads & tracking requests stopped on-device",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        // Chrome Discover Feed Section
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
                        text = "Stories for you",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    val categories = listOf("For You", "Tech & AI", "Gaming", "World News", "Science")
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
            NewsItem("1", "Next-gen Web Architectures: High Performance on Mobile Phones", "TechRadar", "2h ago", "Tech & AI", "https://techradar.com", "📱"),
            NewsItem("2", "Gemini 3.5 & Pro Redefine Mobile Browser Intelligence", "AI Insights", "4h ago", "Tech & AI", "https://ai.google", "✨"),
            NewsItem("3", "Privacy by Default: How Ad Trackers are Intercepted Locally", "CyberSec Weekly", "6h ago", "For You", "https://eff.org", "🛡️"),
            NewsItem("4", "Chrome & Web Engines Optimize RAM Limiters for Mobile", "Tech Daily", "1h ago", "Gaming", "https://google.com/chrome", "🎮"),
            NewsItem("5", "James Webb Space Telescope Captures Distant Galaxy Formation", "NASA Science", "3h ago", "Science", "https://nasa.gov", "🔭"),
            NewsItem("6", "Quantum Computing Breakthrough in Fault-Tolerant Qubits", "Science Daily", "14h ago", "Science", "https://sciencedaily.com", "⚛️")
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

    // APK Download Instructions Dialog
    if (showApkDownloadDialog) {
        AlertDialog(
            onDismissRequest = { showApkDownloadDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(Icons.Default.Android, contentDescription = null, tint = Color(0xFF34A853))
                    Text("Download APK for Real Phone", fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "To test Globe Browser on your real phone without any emulator lag or inverted colors:",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text("1. Click 'Export APK' or 'Download APK' in the AI Studio platform header/menu.", fontSize = 12.sp)
                            Text("2. Transfer or download the APK directly to your Android device.", fontSize = 12.sp)
                            Text("3. Tap the downloaded APK to install and enjoy 120Hz smooth browsing.", fontSize = 12.sp)
                        }
                    }
                    Text(
                        text = "Globe Browser is fully compiled with hardware acceleration and supports Android 9.0 to Android 15.",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        Toast.makeText(context, "Ready! Use the AI Studio top bar / menu to download APK", Toast.LENGTH_LONG).show()
                        showApkDownloadDialog = false
                    }
                ) {
                    Text("Got it")
                }
            },
            dismissButton = {
                TextButton(onClick = { showApkDownloadDialog = false }) {
                    Text("Close")
                }
            }
        )
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
