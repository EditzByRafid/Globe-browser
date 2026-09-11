package com.example.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.model.BrowserSettings
import com.example.model.BrowserTheme
import com.example.model.SearchEngine
import com.example.model.ToolbarPosition
import com.example.ui.theme.GlobePalettes

/**
 * Out-Of-Box Setup Screen (like setting up a brand new or reset phone).
 * Allows user to pick:
 * 1. Destination / Region
 * 2. Language
 * 3. Default Search Engine (with real brand logos)
 * 4. Privacy & AdBlock Protection Level
 * 5. Theme & Layout Mode
 * 6. Performance & Speed Optimization (Low-end to High-end phones)
 */
@Composable
fun SetupWizardScreen(
    initialSettings: BrowserSettings,
    onComplete: (BrowserSettings) -> Unit,
    modifier: Modifier = Modifier
) {
    var step by remember { mutableIntStateOf(0) }
    val totalSteps = 6

    var selectedRegion by remember { mutableStateOf("Global") }
    var selectedLanguage by remember { mutableStateOf("English (US)") }
    var selectedEngine by remember { mutableStateOf(initialSettings.searchEngine) }
    var selectedTheme by remember { mutableStateOf(initialSettings.theme) }
    var selectedToolbar by remember { mutableStateOf(initialSettings.toolbarPosition) }
    var adBlockTier by remember { mutableStateOf("Ultra Shield") } // Ultra Shield, Balanced, Maximum Hardened
    var perfProfile by remember { mutableStateOf("ultra_smooth") }

    val languages = listOf(
        "English (US)", "English (UK)", "Español", "Français", 
        "Deutsch", "日本語 (Japanese)", "Português", "العربية (Arabic)", 
        "Bahasa Indonesia", "हिन्दी (Hindi)", "Русский", "Italiano"
    )

    val regions = listOf(
        "Global / International",
        "North America (USA & Canada)",
        "Europe (EU & UK)",
        "Asia Pacific & Japan",
        "Latin America",
        "Middle East & Africa"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF0F172A),
                        Color(0xFF020617),
                        Color(0xFF000000)
                    )
                )
            )
            .statusBarsPadding()
            .navigationBarsPadding()
            .testTag("oobe_setup_wizard")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Step Progress Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp, bottom = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                for (i in 0 until totalSteps) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(4.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(
                                if (i <= step) Color(0xFF00E5FF)
                                else Color.White.copy(alpha = 0.2f)
                            )
                    )
                }
            }

            // Step Content Animated Transition
            AnimatedContent(
                targetState = step,
                transitionSpec = {
                    if (targetState > initialState) {
                        slideInHorizontally { it } + fadeIn() togetherWith slideOutHorizontally { -it } + fadeOut()
                    } else {
                        slideInHorizontally { -it } + fadeIn() togetherWith slideOutHorizontally { it } + fadeOut()
                    }
                },
                modifier = Modifier.weight(1f),
                label = "setup_step_anim"
            ) { currentStep ->
                when (currentStep) {
                    0 -> WelcomeStep(onStart = { step++ })
                    1 -> RegionAndLanguageStep(
                        selectedRegion = selectedRegion,
                        selectedLanguage = selectedLanguage,
                        regions = regions,
                        languages = languages,
                        onSelectRegion = { selectedRegion = it },
                        onSelectLanguage = { selectedLanguage = it }
                    )
                    2 -> SearchEngineStep(
                        selectedEngine = selectedEngine,
                        onSelectEngine = { selectedEngine = it }
                    )
                    3 -> PrivacyAdBlockStep(
                        selectedTier = adBlockTier,
                        onSelectTier = { adBlockTier = it }
                    )
                    4 -> ThemeAndLayoutStep(
                        selectedTheme = selectedTheme,
                        selectedToolbar = selectedToolbar,
                        onSelectTheme = { selectedTheme = it },
                        onSelectToolbar = { selectedToolbar = it }
                    )
                    5 -> PerformanceAndFinishStep(
                        selectedProfile = perfProfile,
                        onSelectProfile = { perfProfile = it }
                    )
                }
            }

            // Bottom Navigation Controls
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (step > 0) {
                    OutlinedButton(
                        onClick = { step-- },
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.3f))
                    ) {
                        Icon(Icons.Default.ArrowBack, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Back", fontWeight = FontWeight.SemiBold)
                    }
                } else {
                    Spacer(modifier = Modifier.width(8.dp))
                }

                if (step < totalSteps - 1) {
                    Button(
                        onClick = { step++ },
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF00E5FF),
                            contentColor = Color(0xFF0F172A)
                        ),
                        modifier = Modifier.testTag("oobe_next_button")
                    ) {
                        Text("Next", fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(Icons.Default.ArrowForward, contentDescription = null, modifier = Modifier.size(18.dp))
                    }
                } else {
                    Button(
                        onClick = {
                            val updated = initialSettings.copy(
                                searchEngine = selectedEngine,
                                theme = selectedTheme,
                                toolbarPosition = selectedToolbar,
                                adBlockEnabled = true,
                                trackerBlockEnabled = true,
                                isFirstLaunchSetupDone = true,
                                destinationRegion = selectedRegion,
                                appLanguage = selectedLanguage,
                                performanceProfile = perfProfile
                            )
                            onComplete(updated)
                        },
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF00E5FF),
                            contentColor = Color(0xFF0F172A)
                        ),
                        modifier = Modifier.testTag("oobe_finish_button")
                    ) {
                        Text("Start Browsing", fontWeight = FontWeight.Black)
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(20.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun WelcomeStep(onStart: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        GlobeLogoBadge(size = 120.dp, animated = true)

        Spacer(modifier = Modifier.height(28.dp))

        Text(
            text = "Welcome to GB",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Black,
            color = Color.White,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = "Set up your lightning-fast, ad-free private browser.\nOptimized for every Android device from low-end to flagship.",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.White.copy(alpha = 0.75f),
            textAlign = TextAlign.Center,
            lineHeight = 22.sp
        )

        Spacer(modifier = Modifier.height(36.dp))

        Surface(
            shape = RoundedCornerShape(20.dp),
            color = Color(0xFF1E293B).copy(alpha = 0.8f),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF00E5FF).copy(alpha = 0.3f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                SetupFeaturePill(icon = Icons.Default.Shield, title = "Built-in 100% Ad & Tracker Blocker", desc = "Zero banner ads, video interruptions, or tracking")
                SetupFeaturePill(icon = Icons.Default.Speed, title = "Fluid 90Hz / 120Hz Hardware Acceleration", desc = "Instant startup, fast HTML5 rendering")
                SetupFeaturePill(icon = Icons.Default.Extension, title = "Extensions & Custom Speed Dial", desc = "Full support for web add-ons & bookmarks")
            }
        }
    }
}

@Composable
private fun SetupFeaturePill(icon: ImageVector, title: String, desc: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(Color(0xFF00E5FF).copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = Color(0xFF00E5FF), modifier = Modifier.size(20.dp))
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)
            Text(text = desc, fontSize = 11.sp, color = Color.White.copy(alpha = 0.6f))
        }
    }
}

@Composable
private fun RegionAndLanguageStep(
    selectedRegion: String,
    selectedLanguage: String,
    regions: List<String>,
    languages: List<String>,
    onSelectRegion: (String) -> Unit,
    onSelectLanguage: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "Language & Destination",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Text(
                text = "Choose your region and primary language for tailored news feeds and search results.",
                style = MaterialTheme.typography.bodySmall,
                color = Color.White.copy(alpha = 0.7f)
            )
        }

        item {
            Text("Select Destination / Region", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = Color(0xFF00E5FF))
            Spacer(modifier = Modifier.height(8.dp))
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                regions.forEach { reg ->
                    val isSelected = selectedRegion == reg
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSelectRegion(reg) },
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSelected) Color(0xFF00E5FF).copy(alpha = 0.2f) else Color(0xFF1E293B).copy(alpha = 0.6f)
                        ),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (isSelected) Color(0xFF00E5FF) else Color.Transparent
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = reg, fontSize = 14.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium, color = Color.White)
                            if (isSelected) {
                                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF00E5FF), modifier = Modifier.size(20.dp))
                            }
                        }
                    }
                }
            }
        }

        item {
            Text("Select App & Web Language", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = Color(0xFF00E5FF))
            Spacer(modifier = Modifier.height(8.dp))
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                languages.take(6).forEach { lang ->
                    val isSelected = selectedLanguage == lang
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSelectLanguage(lang) },
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSelected) Color(0xFF00E5FF).copy(alpha = 0.2f) else Color(0xFF1E293B).copy(alpha = 0.6f)
                        ),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (isSelected) Color(0xFF00E5FF) else Color.Transparent
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = lang, fontSize = 14.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium, color = Color.White)
                            if (isSelected) {
                                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF00E5FF), modifier = Modifier.size(20.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SearchEngineStep(
    selectedEngine: SearchEngine,
    onSelectEngine: (SearchEngine) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            text = "Default Search Engine",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
        Text(
            text = "Select the engine to use when typing directly in the address bar.",
            style = MaterialTheme.typography.bodySmall,
            color = Color.White.copy(alpha = 0.7f),
            modifier = Modifier.padding(bottom = 16.dp)
        )

        LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            items(SearchEngine.values()) { engine ->
                val isSelected = selectedEngine == engine
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onSelectEngine(engine) },
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSelected) Color(0xFF00E5FF).copy(alpha = 0.22f) else Color(0xFF1E293B).copy(alpha = 0.6f)
                    ),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (isSelected) Color(0xFF00E5FF) else Color.White.copy(alpha = 0.1f)
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        SearchEngineVectorLogo(engine = engine, size = 36.dp)
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Text(
                                    text = engine.displayName,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                if (engine == SearchEngine.GOOGLE) {
                                    SuggestionChip(
                                        onClick = {},
                                        label = { Text("Popular", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color(0xFF00E5FF)) },
                                        modifier = Modifier.height(20.dp),
                                        colors = SuggestionChipDefaults.suggestionChipColors(containerColor = Color(0xFF00E5FF).copy(alpha = 0.15f))
                                    )
                                }
                            }
                            Text(
                                text = when (engine) {
                                    SearchEngine.GOOGLE -> "Industry leader with comprehensive web results"
                                    SearchEngine.BING -> "Powered by Microsoft Copilot & Bing index"
                                    SearchEngine.DUCKDUCKGO -> "Private search with zero search history tracking"
                                    SearchEngine.BRAVE -> "Independent privacy-first web index"
                                    SearchEngine.ECOSIA -> "Trees planted with every search query"
                                    else -> "Fast alternative search engine"
                                },
                                fontSize = 11.sp,
                                color = Color.White.copy(alpha = 0.6f)
                            )
                        }
                        RadioButton(
                            selected = isSelected,
                            onClick = { onSelectEngine(engine) },
                            colors = RadioButtonDefaults.colors(
                                selectedColor = Color(0xFF00E5FF),
                                unselectedColor = Color.White.copy(alpha = 0.4f)
                            )
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PrivacyAdBlockStep(
    selectedTier: String,
    onSelectTier: (String) -> Unit
) {
    val tiers = listOf(
        Triple(
            "Ultra Shield",
            "Strongest ad & tracker block. Blocks popups, banners, video ads, tracking scripts, and cosmetic ad placeholders.",
            Icons.Default.Shield
        ),
        Triple(
            "Balanced Shield",
            "Blocks standard intrusive ads & high-risk telemetry while maintaining legacy compatibility.",
            Icons.Default.Security
        ),
        Triple(
            "Hardened Fortress",
            "Strict HTTPS upgrade, Canvas fingerprint noise injection, cookie isolation, and zero telemetry.",
            Icons.Default.Lock
        )
    )

    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            text = "AdBlock & Privacy Shield",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
        Text(
            text = "Pass strict adblocking tests (d3ward, adblock-tester) with on-device network interception.",
            style = MaterialTheme.typography.bodySmall,
            color = Color.White.copy(alpha = 0.7f),
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            tiers.forEach { (name, desc, icon) ->
                val isSelected = selectedTier == name
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onSelectTier(name) },
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSelected) Color(0xFF00E5FF).copy(alpha = 0.22f) else Color(0xFF1E293B).copy(alpha = 0.6f)
                    ),
                    border = androidx.compose.foundation.BorderStroke(
                        1.5.dp,
                        if (isSelected) Color(0xFF00E5FF) else Color.White.copy(alpha = 0.1f)
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(if (isSelected) Color(0xFF00E5FF).copy(alpha = 0.25f) else Color.White.copy(alpha = 0.1f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(imageVector = icon, contentDescription = null, tint = if (isSelected) Color(0xFF00E5FF) else Color.White, modifier = Modifier.size(24.dp))
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Text(text = name, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                if (name == "Ultra Shield") {
                                    SuggestionChip(
                                        onClick = {},
                                        label = { Text("RECOMMENDED", fontSize = 8.sp, fontWeight = FontWeight.Black, color = Color(0xFF00E5FF)) },
                                        modifier = Modifier.height(18.dp),
                                        colors = SuggestionChipDefaults.suggestionChipColors(containerColor = Color(0xFF00E5FF).copy(alpha = 0.2f))
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(text = desc, fontSize = 12.sp, color = Color.White.copy(alpha = 0.7f), lineHeight = 16.sp)
                        }

                        RadioButton(
                            selected = isSelected,
                            onClick = { onSelectTier(name) },
                            colors = RadioButtonDefaults.colors(selectedColor = Color(0xFF00E5FF), unselectedColor = Color.White.copy(alpha = 0.4f))
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ThemeAndLayoutStep(
    selectedTheme: BrowserTheme,
    selectedToolbar: ToolbarPosition,
    onSelectTheme: (BrowserTheme) -> Unit,
    onSelectToolbar: (ToolbarPosition) -> Unit
) {
    LazyColumn(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        item {
            Text(
                text = "Theme & Layout",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Text(
                text = "Pick your preferred visual aesthetic and navigation bar placement.",
                style = MaterialTheme.typography.bodySmall,
                color = Color.White.copy(alpha = 0.7f)
            )
        }

        item {
            Text("Display Theme", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = Color(0xFF00E5FF))
            Spacer(modifier = Modifier.height(8.dp))
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                BrowserTheme.values().forEach { theme ->
                    val isSelected = selectedTheme == theme
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSelectTheme(theme) },
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSelected) Color(0xFF00E5FF).copy(alpha = 0.22f) else Color(0xFF1E293B).copy(alpha = 0.6f)
                        ),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (isSelected) Color(0xFF00E5FF) else Color.White.copy(alpha = 0.1f)
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(theme.label, fontWeight = FontWeight.Bold, color = Color.White)
                                Text(theme.description, fontSize = 11.sp, color = Color.White.copy(alpha = 0.6f))
                            }
                            RadioButton(
                                selected = isSelected,
                                onClick = { onSelectTheme(theme) },
                                colors = RadioButtonDefaults.colors(selectedColor = Color(0xFF00E5FF))
                            )
                        }
                    }
                }
            }
        }

        item {
            Text("Address Bar Placement", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = Color(0xFF00E5FF))
            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                val isTop = selectedToolbar == ToolbarPosition.TOP
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onSelectToolbar(ToolbarPosition.TOP) },
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = if (isTop) Color(0xFF00E5FF).copy(alpha = 0.22f) else Color(0xFF1E293B).copy(alpha = 0.6f)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, if (isTop) Color(0xFF00E5FF) else Color.White.copy(alpha = 0.1f))
                ) {
                    Column(modifier = Modifier.padding(14.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.VerticalAlignTop, contentDescription = null, tint = if (isTop) Color(0xFF00E5FF) else Color.White)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text("Top Bar", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 13.sp)
                        Text("Google Chrome style", fontSize = 10.sp, color = Color.White.copy(alpha = 0.6f))
                    }
                }

                val isBottom = selectedToolbar == ToolbarPosition.BOTTOM
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onSelectToolbar(ToolbarPosition.BOTTOM) },
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = if (isBottom) Color(0xFF00E5FF).copy(alpha = 0.22f) else Color(0xFF1E293B).copy(alpha = 0.6f)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, if (isBottom) Color(0xFF00E5FF) else Color.White.copy(alpha = 0.1f))
                ) {
                    Column(modifier = Modifier.padding(14.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.VerticalAlignBottom, contentDescription = null, tint = if (isBottom) Color(0xFF00E5FF) else Color.White)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text("Bottom Bar", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 13.sp)
                        Text("One-handed thumb reach", fontSize = 10.sp, color = Color.White.copy(alpha = 0.6f))
                    }
                }
            }
        }
    }
}

@Composable
private fun PerformanceAndFinishStep(
    selectedProfile: String,
    onSelectProfile: (String) -> Unit
) {
    val profiles = listOf(
        Pair("Ultra Smooth (Auto 90Hz/120Hz)", "Maximizes frame rate for smooth scrolling, fluid tab transitions, and fast animations."),
        Pair("Balanced Performance", "Standard performance profile for everyday browsing and battery longevity."),
        Pair("Low RAM & Battery Saver", "Disables heavy background animations; ideal for low-end devices and weak networks.")
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF00E5FF), modifier = Modifier.size(54.dp))
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = "Setup Complete!",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Black,
            color = Color.White
        )
        Text(
            text = "Choose your device optimization profile to finalize.",
            style = MaterialTheme.typography.bodySmall,
            color = Color.White.copy(alpha = 0.7f),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(20.dp))

        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            profiles.forEach { (name, desc) ->
                val isSelected = selectedProfile == name
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onSelectProfile(name) },
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSelected) Color(0xFF00E5FF).copy(alpha = 0.22f) else Color(0xFF1E293B).copy(alpha = 0.6f)
                    ),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (isSelected) Color(0xFF00E5FF) else Color.White.copy(alpha = 0.1f)
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(
                            imageVector = if (name.contains("Smooth")) Icons.Default.Speed else Icons.Default.BatteryChargingFull,
                            contentDescription = null,
                            tint = if (isSelected) Color(0xFF00E5FF) else Color.White
                        )
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = name, fontSize = 13.5.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            Text(text = desc, fontSize = 11.sp, color = Color.White.copy(alpha = 0.6f))
                        }
                        RadioButton(
                            selected = isSelected,
                            onClick = { onSelectProfile(name) },
                            colors = RadioButtonDefaults.colors(selectedColor = Color(0xFF00E5FF))
                        )
                    }
                }
            }
        }
    }
}
