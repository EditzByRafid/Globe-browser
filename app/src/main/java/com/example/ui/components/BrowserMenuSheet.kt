package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.BrowserSettings
import com.example.model.TabItem
import com.example.ui.theme.GlobePalettes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BrowserMenuSheet(
    tab: TabItem,
    settings: BrowserSettings,
    onNewTab: (Boolean) -> Unit,
    onForward: () -> Unit = {},
    onReload: () -> Unit = {},
    onDuplicateTab: () -> Unit = {},
    onOpenLens: () -> Unit = {},
    onToggleReaderMode: () -> Unit = {},
    onFindInPage: () -> Unit = {},
    onAddBookmark: () -> Unit,
    onAddToReadingList: () -> Unit = {},
    onOpenBookmarks: () -> Unit,
    onOpenReadingList: () -> Unit = {},
    onOpenHistory: () -> Unit,
    onOpenFileLab: () -> Unit,
    onOpenExtensions: () -> Unit,
    onOpenAccounts: () -> Unit = {},
    onOpenPasswordVault: () -> Unit = {},
    onOpenPrivacy: () -> Unit,
    onOpenSettings: () -> Unit,
    onOpenMaps: () -> Unit,
    onToggleDesktopMode: () -> Unit,
    onShare: () -> Unit,
    onDismiss: () -> Unit
) {

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = MaterialTheme.colorScheme.surface,
        dragHandle = { BottomSheetDefaults.DragHandle() },
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 4.dp)
                .navigationBarsPadding(),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            // Chrome-style Top Row of Quick Action Buttons
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Forward Button
                IconButton(
                    onClick = {
                        onForward()
                        onDismiss()
                    },
                    modifier = Modifier.size(42.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = "Forward",
                        tint = if (tab.canGoForward) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f),
                        modifier = Modifier.size(22.dp)
                    )
                }

                // Bookmark Button
                IconButton(
                    onClick = {
                        onAddBookmark()
                        onDismiss()
                    },
                    modifier = Modifier.size(42.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.StarOutline,
                        contentDescription = "Bookmark",
                        tint = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.size(22.dp)
                    )
                }

                // Download / Saved Files Button
                IconButton(
                    onClick = {
                        onOpenFileLab()
                        onDismiss()
                    },
                    modifier = Modifier.size(42.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Download,
                        contentDescription = "Downloads",
                        tint = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.size(22.dp)
                    )
                }

                // Privacy Shield / Page Info Button
                IconButton(
                    onClick = {
                        onOpenPrivacy()
                        onDismiss()
                    },
                    modifier = Modifier.size(42.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Shield,
                        contentDescription = "Privacy Shield",
                        tint = Color(0xFF34A853),
                        modifier = Modifier.size(22.dp)
                    )
                }

                // Refresh / Reload Page Button
                IconButton(
                    onClick = {
                        onReload()
                        onDismiss()
                    },
                    modifier = Modifier.size(42.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Refresh",
                        tint = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 4.dp),
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
            )

            // Standard Chrome Menu Rows
            ChromeMenuItem(icon = Icons.Default.Add, title = "New tab") {
                onNewTab(false)
                onDismiss()
            }

            ChromeMenuItem(icon = Icons.Default.Security, title = "New Incognito tab") {
                onNewTab(true)
                onDismiss()
            }

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 4.dp),
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
            )

            // Visual Search (Camera Logo)
            ChromeMenuItem(
                icon = Icons.Default.CameraAlt,
                title = "Search with Google Lens",
                iconTint = Color(0xFF4285F4)
            ) {
                onOpenLens()
                onDismiss()
            }

            ChromeMenuItem(icon = Icons.Default.History, title = "History") {
                onOpenHistory()
                onDismiss()
            }

            ChromeMenuItem(icon = Icons.Default.Bookmarks, title = "Bookmarks") {
                onOpenBookmarks()
                onDismiss()
            }

            ChromeMenuItem(icon = Icons.Default.MenuBook, title = "Reading List") {
                onOpenReadingList()
                onDismiss()
            }

            ChromeMenuItem(icon = Icons.Default.PlaylistAdd, title = "Add to Reading List") {
                onAddToReadingList()
                onDismiss()
            }


            ChromeMenuItem(icon = Icons.Default.FileDownload, title = "Downloads & Offline Files") {
                onOpenFileLab()
                onDismiss()
            }

            ChromeMenuItem(icon = Icons.Default.Extension, title = "Extensions") {
                onOpenExtensions()
                onDismiss()
            }

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 4.dp),
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
            )

            ChromeMenuItem(icon = Icons.Default.Share, title = "Share...") {
                onShare()
                onDismiss()
            }

            ChromeMenuItem(icon = Icons.Default.FindInPage, title = "Find in page") {
                onFindInPage()
                onDismiss()
            }

            ChromeMenuItem(
                icon = Icons.Default.Article,
                title = if (settings.readerModeEnabled) "Turn off Reader Mode" else "Reader Mode"
            ) {
                onToggleReaderMode()
                onDismiss()
            }

            // Desktop Site Toggle (Chrome style with checkbox)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .clickable {
                        onToggleDesktopMode()
                        onDismiss()
                    }
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.DesktopWindows,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(22.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = "Desktop site",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Normal,
                    modifier = Modifier.weight(1f)
                )
                Checkbox(
                    checked = settings.desktopMode,
                    onCheckedChange = {
                        onToggleDesktopMode()
                        onDismiss()
                    }
                )
            }

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 4.dp),
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
            )

            ChromeMenuItem(
                icon = Icons.Default.Key,
                title = "Password Manager & Vault",
                iconTint = GlobePalettes.ElectricCyan
            ) {
                onOpenPasswordVault()
                onDismiss()
            }

            ChromeMenuItem(icon = Icons.Default.Settings, title = "Settings") {
                onOpenSettings()
                onDismiss()
            }


            ChromeMenuItem(icon = Icons.Default.Place, title = "Google Maps Navigation") {
                onOpenMaps()
                onDismiss()
            }

            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
private fun ChromeMenuItem(
    icon: ImageVector,
    title: String,
    iconTint: Color? = null,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = iconTint ?: MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(22.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Normal,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

