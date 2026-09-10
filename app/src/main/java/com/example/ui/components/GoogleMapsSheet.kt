package com.example.ui.components

import android.webkit.WebView
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.ui.theme.GlobePalettes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GoogleMapsSheet(
    onNavigateWeb: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var currentMapUrl by remember { mutableStateOf("https://maps.google.com/?force=pwa") }
    var mapWebView by remember { mutableStateOf<WebView?>(null) }

    val quickPlaceCategories = listOf(
        Pair("Restaurants", "restaurants"),
        Pair("Cafes", "coffee"),
        Pair("Hotels", "hotels"),
        Pair("Attractions", "attractions"),
        Pair("Gas & EV", "gas stations"),
        Pair("Pharmacies", "pharmacies")
    )

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = MaterialTheme.colorScheme.background,
        dragHandle = { BottomSheetDefaults.DragHandle() }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 14.dp, vertical = 4.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(
                        imageVector = Icons.Default.Place,
                        contentDescription = "Google Maps",
                        tint = GlobePalettes.ElectricCyan,
                        modifier = Modifier.size(24.dp)
                    )
                    Text(
                        text = "Google Maps Grounding",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    FilledTonalButton(
                        onClick = {
                            onNavigateWeb(currentMapUrl)
                            onDismiss()
                        },
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp)
                    ) {
                        Icon(Icons.Default.OpenInBrowser, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Full Tab", fontSize = 12.sp)
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Search input
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search location or place in Maps...", fontSize = 13.sp) },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("maps_search_input"),
                shape = RoundedCornerShape(14.dp),
                singleLine = true,
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, modifier = Modifier.size(18.dp)) },
                trailingIcon = {
                    if (searchQuery.isNotBlank()) {
                        IconButton(
                            onClick = {
                                val encoded = java.net.URLEncoder.encode(searchQuery, "UTF-8")
                                val target = "https://www.google.com/maps/search/$encoded"
                                currentMapUrl = target
                                mapWebView?.loadUrl(target)
                            }
                        ) {
                            Icon(Icons.Default.ArrowForward, contentDescription = "Search", tint = MaterialTheme.colorScheme.primary)
                        }
                    }
                }
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Quick discovery categories chips
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                items(quickPlaceCategories) { (label, query) ->
                    AssistChip(
                        onClick = {
                            searchQuery = label
                            val target = "https://www.google.com/maps/search/$query"
                            currentMapUrl = target
                            mapWebView?.loadUrl(target)
                        },
                        label = { Text(label, fontSize = 11.sp) },
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Embedded Interactive Map Container
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
            ) {
                AndroidView(
                    factory = { ctx ->
                        WebView(ctx).apply {
                            settings.javaScriptEnabled = true
                            settings.domStorageEnabled = true
                            settings.setGeolocationEnabled(true)
                            mapWebView = this
                            loadUrl(currentMapUrl)
                        }
                    },
                    update = { wv ->
                        mapWebView = wv
                    },
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}
