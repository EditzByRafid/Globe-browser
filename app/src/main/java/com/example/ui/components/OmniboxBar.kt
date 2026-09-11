package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.BrowserSettings
import com.example.model.TabItem
import com.example.ui.theme.GlobePalettes
import com.example.ui.theme.chromeCard

@Composable
fun OmniboxBar(
    tab: TabItem,
    settings: BrowserSettings,
    tabCount: Int,
    onNavigate: (String) -> Unit,
    onBack: () -> Unit,
    onForward: () -> Unit,
    onReload: () -> Unit,
    onHome: () -> Unit,
    onOpenTabs: () -> Unit,
    onOpenSearchOverlay: () -> Unit,
    onOpenLens: () -> Unit,
    onOpenMenu: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isHttps = tab.url.startsWith("https://")
    val isGlobe = tab.url.startsWith("globe://") || tab.url.isBlank()

    Column(
        modifier = modifier
            .fillMaxWidth()
            .chromeCard(
                shape = RoundedCornerShape(22.dp)
            )
            .background(MaterialTheme.colorScheme.surface)
            .padding(horizontal = 4.dp, vertical = 4.dp)
    ) {
        // Linear loading progress bar
        if (tab.isLoading && tab.progress in 1..99) {
            LinearProgressIndicator(
                progress = { tab.progress / 100f },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(2.5.dp)
                    .clip(CircleShape),
                color = MaterialTheme.colorScheme.primary,
                trackColor = Color.Transparent
            )
            Spacer(modifier = Modifier.height(2.dp))
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            // Home Button
            IconButton(
                onClick = onHome,
                modifier = Modifier
                    .size(40.dp)
                    .testTag("nav_home_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Home,
                    contentDescription = "Home",
                    tint = if (isGlobe) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.size(22.dp)
                )
            }

            // Omnibox URL / Search input box (Pill)
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(42.dp)
                    .clip(RoundedCornerShape(21.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f))
                    .clickable { onOpenSearchOverlay() }
                    .padding(horizontal = 12.dp)
                    .testTag("omnibox_search_card"),
                contentAlignment = Alignment.CenterStart
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Search Engine Logo or Security Icon
                    if (tab.isIncognito) {
                        Icon(
                            imageVector = Icons.Default.Security,
                            contentDescription = "Incognito Mode",
                            tint = Color(0xFFA855F7),
                            modifier = Modifier.size(16.dp)
                        )
                    } else if (isGlobe) {
                        SearchEngineVectorLogo(
                            engine = settings.searchEngine,
                            size = 18.dp
                        )
                    } else if (isHttps) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "Secure Connection",
                            tint = GlobePalettes.ElectricCyan,
                            modifier = Modifier.size(15.dp)
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = "Not Secure",
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(15.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    // URL or Search Hint
                    Text(
                        text = if (isGlobe) "Search or type URL" else tab.url.removePrefix("https://").removePrefix("http://"),
                        style = TextStyle(
                            fontSize = 13.5.sp,
                            color = if (isGlobe) MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.75f) else MaterialTheme.colorScheme.onSurface,
                            fontWeight = if (isGlobe) FontWeight.Normal else FontWeight.Medium
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )

                    // Small search icon
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            // Tabs Switcher Counter Button
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.16f))
                    .clickable { onOpenTabs() }
                    .testTag("tab_switcher_button"),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (tabCount > 99) ":D" else tabCount.toString(),
                    style = TextStyle(
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                )
            }

            // 3-dots More Menu Button (Chrome style)
            IconButton(
                onClick = onOpenMenu,
                modifier = Modifier
                    .size(40.dp)
                    .testTag("browser_menu_button")
            ) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = "Browser Menu",
                    tint = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.size(22.dp)
                )
            }
        }
    }
}
