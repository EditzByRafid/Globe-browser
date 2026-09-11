package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.TabItem
import com.example.ui.theme.GlobePalettes

/**
 * Bottom Button Navigation Bar providing ergonomic one-handed browser control
 * with seamless compatibility for Android 3-Button system navigation (Back/Home/Recents).
 */
@Composable
fun BottomNavBar(
    tab: TabItem,
    tabsCount: Int,
    matchingCredentialsCount: Int = 0,
    onBack: () -> Unit,
    onForward: () -> Unit,
    onHome: () -> Unit,
    onOpenVault: () -> Unit,
    onOpenTabs: () -> Unit,
    onOpenMenu: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding(),
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f),
        tonalElevation = 6.dp,
        shadowElevation = 8.dp
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            HorizontalDivider(
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.25f),
                thickness = 0.5.dp
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .padding(horizontal = 4.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 1. Back Navigation Button
                val canBack = tab.canGoBack || !tab.url.startsWith("globe://newtab")
                IconButton(
                    onClick = onBack,
                    enabled = canBack,
                    modifier = Modifier
                        .size(46.dp)
                        .testTag("nav_btn_back")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = if (canBack) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.35f),
                        modifier = Modifier.size(22.dp)
                    )
                }

                // 2. Forward Navigation Button
                IconButton(
                    onClick = onForward,
                    enabled = tab.canGoForward,
                    modifier = Modifier
                        .size(46.dp)
                        .testTag("nav_btn_forward")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = "Forward",
                        tint = if (tab.canGoForward) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.35f),
                        modifier = Modifier.size(22.dp)
                    )
                }

                // 3. Home Button
                IconButton(
                    onClick = onHome,
                    modifier = Modifier
                        .size(46.dp)
                        .testTag("nav_btn_home")
                ) {
                    Icon(
                        imageVector = Icons.Default.Home,
                        contentDescription = "Home",
                        tint = if (tab.url.startsWith("globe://newtab")) GlobePalettes.ElectricCyan else MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.size(22.dp)
                    )
                }

                // 4. Password Vault & Autofill Quick Key
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.size(46.dp)
                ) {
                    IconButton(
                        onClick = onOpenVault,
                        modifier = Modifier
                            .fillMaxSize()
                            .testTag("nav_btn_vault")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Key,
                            contentDescription = "Password Vault",
                            tint = if (matchingCredentialsCount > 0) GlobePalettes.NeonGreen else MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.size(21.dp)
                        )
                    }

                    // Badge if current domain has saved credentials ready for autofill
                    if (matchingCredentialsCount > 0) {
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .offset(x = (-4).dp, y = 6.dp)
                                .size(15.dp)
                                .clip(CircleShape)
                                .background(GlobePalettes.NeonGreen),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "$matchingCredentialsCount",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black
                            )
                        }
                    }
                }

                // 5. Tabs Switcher Button
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .border(
                            width = 1.5.dp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.85f),
                            shape = RoundedCornerShape(8.dp)
                        )
                        .clickable(onClick = onOpenTabs)
                        .testTag("nav_btn_tabs"),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (tabsCount > 99) ":D" else "$tabsCount",
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                // 6. 3-Dots More Menu Button
                IconButton(
                    onClick = onOpenMenu,
                    modifier = Modifier
                        .size(46.dp)
                        .testTag("nav_btn_menu")
                ) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "Menu",
                        tint = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }
        }
    }
}
