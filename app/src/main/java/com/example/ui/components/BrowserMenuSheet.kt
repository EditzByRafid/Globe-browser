package com.example.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
    onDuplicateTab: () -> Unit = {},
    onOpenLens: () -> Unit = {},
    onOpenFirebase: () -> Unit = {},
    onToggleReaderMode: () -> Unit = {},
    onFindInPage: () -> Unit = {},
    onAddBookmark: () -> Unit,
    onOpenBookmarks: () -> Unit,
    onOpenHistory: () -> Unit,
    onOpenFileLab: () -> Unit,
    onOpenExtensions: () -> Unit,
    onOpenAccounts: () -> Unit,
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
        containerColor = MaterialTheme.colorScheme.background,
        dragHandle = { BottomSheetDefaults.DragHandle() }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = "Globe Browser Menu",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            // Primary Google Lens Visual Search Trigger
            MenuRowItem(icon = Icons.Default.CameraAlt, title = "Google Lens Visual Search", subtitle = "Analyze photos, text OCR, shopping") {
                onOpenLens()
                onDismiss()
            }

            // Firebase Cloud Account & Sync Trigger
            MenuRowItem(icon = Icons.Default.CloudSync, title = "Firebase Cloud Sync & Account", subtitle = "Sync bookmarks & history to Firebase") {
                onOpenFirebase()
                onDismiss()
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp), color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))

            // Tab actions
            MenuRowItem(icon = Icons.Default.Add, title = "New Tab") {
                onNewTab(false)
                onDismiss()
            }
            MenuRowItem(icon = Icons.Default.Security, title = "New Incognito Tab") {
                onNewTab(true)
                onDismiss()
            }
            MenuRowItem(icon = Icons.Default.ContentCopy, title = "Duplicate Current Tab") {
                onDuplicateTab()
                onDismiss()
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp), color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))

            // Page tools
            MenuRowItem(icon = Icons.Default.FindInPage, title = "Find in Page") {
                onFindInPage()
                onDismiss()
            }
            MenuRowItem(
                icon = Icons.Default.Article,
                title = if (settings.readerModeEnabled) "Exit Reader Mode" else "Reader Mode (Clutter-Free)"
            ) {
                onToggleReaderMode()
                onDismiss()
            }
            MenuRowItem(icon = Icons.Default.BookmarkBorder, title = "Bookmark this page") {
                onAddBookmark()
                onDismiss()
            }
            MenuRowItem(icon = Icons.Default.Bookmarks, title = "Bookmarks") {
                onOpenBookmarks()
                onDismiss()
            }
            MenuRowItem(icon = Icons.Default.History, title = "History") {
                onOpenHistory()
                onDismiss()
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp), color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))

            MenuRowItem(icon = Icons.Default.Place, title = "Google Maps Grounding & Navigation") {
                onOpenMaps()
                onDismiss()
            }
            MenuRowItem(icon = Icons.Default.Code, title = "File Lab (Code Viewer & Sandbox)") {
                onOpenFileLab()
                onDismiss()
            }
            MenuRowItem(icon = Icons.Default.Extension, title = "Extensions Manager") {
                onOpenExtensions()
                onDismiss()
            }
            MenuRowItem(icon = Icons.Default.AccountCircle, title = "Profiles & Accounts DB") {
                onOpenAccounts()
                onDismiss()
            }
            MenuRowItem(icon = Icons.Default.Shield, title = "Privacy Shield Dashboard") {
                onOpenPrivacy()
                onDismiss()
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp), color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))

            MenuRowItem(
                icon = if (settings.desktopMode) Icons.Default.PhoneAndroid else Icons.Default.DesktopWindows,
                title = if (settings.desktopMode) "Switch to Mobile View" else "Desktop Site Request"
            ) {
                onToggleDesktopMode()
                onDismiss()
            }
            MenuRowItem(icon = Icons.Default.Share, title = "Share Page") {
                onShare()
                onDismiss()
            }
            MenuRowItem(icon = Icons.Default.Settings, title = "Settings") {
                onOpenSettings()
                onDismiss()
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun MenuRowItem(
    icon: ImageVector,
    title: String,
    subtitle: String? = null,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(22.dp)
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

