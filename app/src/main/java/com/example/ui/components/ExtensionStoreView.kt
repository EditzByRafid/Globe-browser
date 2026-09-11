package com.example.ui.components

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.model.ExtensionItem
import com.example.model.StoreExtensionItem
import com.example.model.StoreReview
import com.example.ui.theme.GlobePalettes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExtensionStoreView(
    storeExtensions: List<StoreExtensionItem>,
    installedExtensions: List<ExtensionItem>,
    onInstallExtension: (StoreExtensionItem) -> Unit,
    onUninstallExtension: (String) -> Unit,
    onCreateCustomExtension: (name: String, desc: String, category: String, script: String) -> Unit,
    onAddReview: (extensionId: String, author: String, rating: Int, comment: String) -> Unit,
    onCloseTab: () -> Unit
) {
    val context = LocalContext.current
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("All") }
    var selectedPreviewItem by remember { mutableStateOf<StoreExtensionItem?>(null) }
    var showCreateDialog by remember { mutableStateOf(false) }

    val categories = listOf("All", "Ad Blockers", "Privacy & Security", "Productivity", "Themes & Style", "Developer Tools")

    val filteredExtensions = remember(storeExtensions, searchQuery, selectedCategory) {
        storeExtensions.filter { item ->
            val matchesCategory = selectedCategory == "All" || item.category.equals(selectedCategory, ignoreCase = true)
            val matchesSearch = searchQuery.isBlank() ||
                    item.name.contains(searchQuery, ignoreCase = true) ||
                    item.shortDescription.contains(searchQuery, ignoreCase = true) ||
                    item.developer.contains(searchQuery, ignoreCase = true)
            matchesCategory && matchesSearch
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Chrome Web Store Header
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 2.dp,
            shadowElevation = 2.dp
        ) {
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Chrome Web Store Logo & Branding
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(GlobePalettes.GoogleBlue),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Extension,
                                contentDescription = "Store",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Chrome",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 17.sp,
                                    color = GlobePalettes.GoogleBlue
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Web Store",
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 17.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                            Text(
                                text = "Extensions & Tools for GB Browser",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    // "+ Create Extension" action
                    Button(
                        onClick = { showCreateDialog = true },
                        shape = RoundedCornerShape(20.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = GlobePalettes.GoogleGreen)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Create", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Search Bar
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Search extensions, themes, and tools...", fontSize = 13.sp) },
                    leadingIcon = {
                        Icon(Icons.Default.Search, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(Icons.Default.Close, contentDescription = "Clear", modifier = Modifier.size(18.dp))
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(24.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Category Tabs
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items(categories) { cat ->
                        val isSelected = selectedCategory == cat
                        FilterChip(
                            selected = isSelected,
                            onClick = { selectedCategory = cat },
                            label = { Text(cat, fontSize = 12.sp) },
                            shape = RoundedCornerShape(16.dp)
                        )
                    }
                }
            }
        }

        // Store Content List
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Featured Hero Showcase (shown when search is empty)
            if (searchQuery.isBlank() && selectedCategory == "All") {
                item {
                    FeaturedHeroBanner(
                        onExploreFeatured = {
                            selectedPreviewItem = storeExtensions.firstOrNull()
                        }
                    )
                }
            }

            // Section Header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = if (selectedCategory == "All") "Popular & Recommended" else selectedCategory,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "${filteredExtensions.size} extensions",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Extension Cards
            items(filteredExtensions, key = { it.id }) { item ->
                val isInstalled = installedExtensions.any { it.id == item.id }
                StoreExtensionCard(
                    item = item,
                    isInstalled = isInstalled,
                    onOpenPreview = { selectedPreviewItem = item },
                    onInstallToggle = {
                        if (isInstalled) {
                            onUninstallExtension(item.id)
                            Toast.makeText(context, "Removed ${item.name}", Toast.LENGTH_SHORT).show()
                        } else {
                            onInstallExtension(item)
                            Toast.makeText(context, "Added ${item.name} to GB Browser", Toast.LENGTH_SHORT).show()
                        }
                    }
                )
            }
        }
    }

    // Extension Preview & Review Dialog
    selectedPreviewItem?.let { item ->
        val isInstalled = installedExtensions.any { it.id == item.id }
        ExtensionDetailDialog(
            item = item,
            isInstalled = isInstalled,
            onInstall = {
                onInstallExtension(item)
                Toast.makeText(context, "Installed ${item.name}", Toast.LENGTH_SHORT).show()
            },
            onUninstall = {
                onUninstallExtension(item.id)
                Toast.makeText(context, "Uninstalled ${item.name}", Toast.LENGTH_SHORT).show()
            },
            onAddReview = { author, rating, comment ->
                onAddReview(item.id, author, rating, comment)
                Toast.makeText(context, "Review submitted!", Toast.LENGTH_SHORT).show()
            },
            onDismiss = { selectedPreviewItem = null }
        )
    }

    // Create Custom Extension Dialog
    if (showCreateDialog) {
        CreateExtensionDialog(
            onCreate = { name, desc, cat, script ->
                onCreateCustomExtension(name, desc, cat, script)
                showCreateDialog = false
                Toast.makeText(context, "Extension '$name' created & activated!", Toast.LENGTH_SHORT).show()
            },
            onDismiss = { showCreateDialog = false }
        )
    }
}

@Composable
fun FeaturedHeroBanner(onExploreFeatured: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onExploreFeatured() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = GlobePalettes.GoogleBlue.copy(alpha = 0.12f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = GlobePalettes.GoogleBlue,
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = "FEATURED SELECTION",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Editor's Choice 2026",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "uBlock Origin Lite — Fast, Zero-Lag Ad Blocker",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Engineered specifically for low-overhead performance. Blocks ads, miners, and trackers automatically.",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Tap to view screenshots, reviews & preview →",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = GlobePalettes.GoogleBlue
                )
            }
        }
    }
}

@Composable
fun StoreExtensionCard(
    item: StoreExtensionItem,
    isInstalled: Boolean,
    onOpenPreview: () -> Unit,
    onInstallToggle: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onOpenPreview() },
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {
                // Extension Icon
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(
                            try {
                                Color(android.graphics.Color.parseColor(item.iconColorHex))
                            } catch (e: Exception) {
                                GlobePalettes.GoogleBlue
                            }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = when (item.category) {
                            "Ad Blockers" -> Icons.Default.Shield
                            "Privacy & Security" -> Icons.Default.Security
                            "Themes & Style" -> Icons.Default.Palette
                            "Developer Tools" -> Icons.Default.Code
                            else -> Icons.Default.Extension
                        },
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = item.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = item.developer,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "★ ${item.rating}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = GlobePalettes.GoogleYellow
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "(${item.ratingCount})",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "• ${item.userCount}",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Add to GB / Installed Button
                if (isInstalled) {
                    OutlinedButton(
                        onClick = onInstallToggle,
                        shape = RoundedCornerShape(18.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = GlobePalettes.GoogleGreen)
                    ) {
                        Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Added", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                } else {
                    Button(
                        onClick = onInstallToggle,
                        shape = RoundedCornerShape(18.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = GlobePalettes.GoogleBlue)
                    ) {
                        Text("Add to GB", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = item.shortDescription,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Badge & Category chip
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = item.previewBadge,
                        fontSize = 10.sp,
                        color = GlobePalettes.GoogleBlue,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }

                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = item.category,
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun ExtensionDetailDialog(
    item: StoreExtensionItem,
    isInstalled: Boolean,
    onInstall: () -> Unit,
    onUninstall: () -> Unit,
    onAddReview: (author: String, rating: Int, comment: String) -> Unit,
    onDismiss: () -> Unit
) {
    var selectedTab by remember { mutableStateOf(0) } // 0: Overview, 1: Live Preview, 2: Reviews, 3: Settings
    var newReviewComment by remember { mutableStateOf("") }
    var newReviewAuthor by remember { mutableStateOf("") }
    var newReviewRating by remember { mutableStateOf(5) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.85f),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Header with Extension Info & Close
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(46.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(
                                    try {
                                        Color(android.graphics.Color.parseColor(item.iconColorHex))
                                    } catch (e: Exception) {
                                        GlobePalettes.GoogleBlue
                                    }
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Extension, contentDescription = null, tint = Color.White)
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = item.name,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "v${item.version} • ${item.developer}",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                // Install Button bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "★ ${item.rating}",
                            fontWeight = FontWeight.Bold,
                            color = GlobePalettes.GoogleYellow
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "(${item.ratingCount} reviews) • ${item.userCount}",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    if (isInstalled) {
                        Button(
                            onClick = onUninstall,
                            colors = ButtonDefaults.buttonColors(containerColor = GlobePalettes.GoogleRed),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Text("Remove", fontSize = 12.sp)
                        }
                    } else {
                        Button(
                            onClick = onInstall,
                            colors = ButtonDefaults.buttonColors(containerColor = GlobePalettes.GoogleBlue),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Text("Add to GB", fontSize = 12.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Detail Tabs: Overview, Preview Options, Reviews, Settings
                TabRow(
                    selectedTabIndex = selectedTab,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        text = { Text("Overview", fontSize = 12.sp) }
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = { Text("Preview", fontSize = 12.sp) }
                    )
                    Tab(
                        selected = selectedTab == 2,
                        onClick = { selectedTab = 2 },
                        text = { Text("Reviews (${item.reviews.size})", fontSize = 12.sp) }
                    )
                    Tab(
                        selected = selectedTab == 3,
                        onClick = { selectedTab = 3 },
                        text = { Text("Settings", fontSize = 12.sp) }
                    )
                }

                // Tab Contents
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(16.dp)
                ) {
                    when (selectedTab) {
                        0 -> {
                            // Overview Tab
                            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                item {
                                    Text(
                                        text = "Description",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = item.fullDescription,
                                        fontSize = 13.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                item {
                                    Text(
                                        text = "Key Features",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    item.previewFeatureHighlights.forEach { feat ->
                                        Row(
                                            modifier = Modifier.padding(vertical = 2.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(
                                                Icons.Default.CheckCircle,
                                                contentDescription = null,
                                                tint = GlobePalettes.GoogleGreen,
                                                modifier = Modifier.size(16.dp)
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(feat, fontSize = 12.sp)
                                        }
                                    }
                                }
                                item {
                                    Text(
                                        text = "Permissions Required",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    item.permissions.forEach { perm ->
                                        Text(
                                            text = "• $perm",
                                            fontSize = 12.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }
                        }
                        1 -> {
                            // Live Preview Options Tab
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "Interactive In-Browser Preview",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                                Spacer(modifier = Modifier.height(8.dp))

                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(180.dp),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                                ) {
                                    Box(
                                        modifier = Modifier.fillMaxSize().padding(12.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            Icon(
                                                Icons.Default.Visibility,
                                                contentDescription = null,
                                                tint = GlobePalettes.GoogleBlue,
                                                modifier = Modifier.size(40.dp)
                                            )
                                            Spacer(modifier = Modifier.height(8.dp))
                                            Text(
                                                text = "${item.name} Mockup Active",
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 13.sp
                                            )
                                            Text(
                                                text = "Simulates real DOM injection & stylesheet overrides in active browser tabs.",
                                                fontSize = 11.sp,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = "Preview options: Full DOM Filter, Real-time Script, Network Blocker.",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                        2 -> {
                            // Reviews Tab
                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                // Add Review Form
                                item {
                                    Card(
                                        shape = RoundedCornerShape(10.dp),
                                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                                    ) {
                                        Column(modifier = Modifier.padding(10.dp)) {
                                            Text("Write a Review", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                            Spacer(modifier = Modifier.height(6.dp))
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Text("Rating: ", fontSize = 12.sp)
                                                (1..5).forEach { star ->
                                                    IconButton(
                                                        onClick = { newReviewRating = star },
                                                        modifier = Modifier.size(26.dp)
                                                    ) {
                                                        Icon(
                                                            Icons.Default.Star,
                                                            contentDescription = null,
                                                            tint = if (star <= newReviewRating) GlobePalettes.GoogleYellow else Color.Gray,
                                                            modifier = Modifier.size(20.dp)
                                                        )
                                                    }
                                                }
                                            }
                                            OutlinedTextField(
                                                value = newReviewComment,
                                                onValueChange = { newReviewComment = it },
                                                placeholder = { Text("Describe your experience...", fontSize = 12.sp) },
                                                modifier = Modifier.fillMaxWidth(),
                                                maxLines = 2
                                            )
                                            Spacer(modifier = Modifier.height(6.dp))
                                            Button(
                                                onClick = {
                                                    if (newReviewComment.isNotBlank()) {
                                                        onAddReview("You", newReviewRating, newReviewComment)
                                                        newReviewComment = ""
                                                    }
                                                },
                                                modifier = Modifier.align(Alignment.End),
                                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                                            ) {
                                                Text("Submit Review", fontSize = 11.sp)
                                            }
                                        }
                                    }
                                }

                                items(item.reviews) { rev ->
                                    Card(
                                        shape = RoundedCornerShape(8.dp),
                                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                                    ) {
                                        Column(modifier = Modifier.padding(10.dp)) {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween
                                            ) {
                                                Text(rev.author, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                                Text(rev.date, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                            }
                                            Row {
                                                repeat(rev.rating) {
                                                    Icon(Icons.Default.Star, contentDescription = null, tint = GlobePalettes.GoogleYellow, modifier = Modifier.size(14.dp))
                                                }
                                            }
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text(rev.comment, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface)
                                        }
                                    }
                                }
                            }
                        }
                        3 -> {
                            // Settings Tab
                            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                Text("Extension Settings", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column {
                                        Text("Allow in Incognito", fontSize = 13.sp)
                                        Text("Run while in private browsing mode", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                    Switch(checked = false, onCheckedChange = {})
                                }
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column {
                                        Text("Automatic Updates", fontSize = 13.sp)
                                        Text("Keep extension updated with latest rules", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                    Switch(checked = true, onCheckedChange = {})
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CreateExtensionDialog(
    onCreate: (name: String, desc: String, category: String, script: String) -> Unit,
    onDismiss: () -> Unit
) {
    var extName by remember { mutableStateOf("") }
    var extDesc by remember { mutableStateOf("") }
    var extCategory by remember { mutableStateOf("Productivity") }
    var extScript by remember { mutableStateOf("// ==UserScript==\n// @name Custom Extension\n// @match *://*/*\n// ==/UserScript==\n\nconsole.log('Running custom extension');") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.85f),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Create Custom Extension", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = extName,
                    onValueChange = { extName = it },
                    label = { Text("Extension Name") },
                    placeholder = { Text("e.g. YouTube Ad Skipper") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = extDesc,
                    onValueChange = { extDesc = it },
                    label = { Text("Short Description") },
                    placeholder = { Text("e.g. Automatically clicks skip on video ads") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 2
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text("Script Code (JavaScript / CSS UserScript):", style = MaterialTheme.typography.labelMedium)
                Spacer(modifier = Modifier.height(4.dp))

                OutlinedTextField(
                    value = extScript,
                    onValueChange = { extScript = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    textStyle = MaterialTheme.typography.bodySmall.copy(fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace)
                )

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = {
                        if (extName.isNotBlank()) {
                            onCreate(extName, extDesc.ifBlank { "Custom extension created by user." }, extCategory, extScript)
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = GlobePalettes.GoogleGreen)
                ) {
                    Icon(Icons.Default.Check, contentDescription = null)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Compile & Install into GB Browser")
                }
            }
        }
    }
}
