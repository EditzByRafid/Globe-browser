package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.BrowserSettings
import com.example.model.SearchEngine
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
    onOpenAi: () -> Unit,
    onOpenLens: () -> Unit,
    onOpenMenu: () -> Unit,
    modifier: Modifier = Modifier
) {
    val focusManager = LocalFocusManager.current
    var isEditing by remember { mutableStateOf(false) }
    var inputText by remember(tab.url) {
        val display = if (tab.url.startsWith("globe://")) "" else tab.url
        mutableStateOf(display)
    }

    val isHttps = tab.url.startsWith("https://")
    val isGlobe = tab.url.startsWith("globe://") || tab.url.isBlank()

    Column(
        modifier = modifier
            .fillMaxWidth()
            .chromeCard(
                shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp, bottomStart = 20.dp, bottomEnd = 20.dp)
            )
            .background(MaterialTheme.colorScheme.surface)
            .padding(horizontal = 8.dp, vertical = 6.dp)
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
            Spacer(modifier = Modifier.height(4.dp))
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            // Back Button
            IconButton(
                onClick = onBack,
                enabled = tab.canGoBack || !isGlobe,
                modifier = Modifier
                    .size(40.dp)
                    .testTag("nav_back_button")
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = if (tab.canGoBack || !isGlobe) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
                    modifier = Modifier.size(20.dp)
                )
            }

            // Forward Button
            IconButton(
                onClick = onForward,
                enabled = tab.canGoForward,
                modifier = Modifier
                    .size(40.dp)
                    .testTag("nav_forward_button")
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowForward,
                    contentDescription = "Forward",
                    tint = if (tab.canGoForward) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
                    modifier = Modifier.size(20.dp)
                )
            }

            // Omnibox URL / Search input box
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(42.dp)
                    .clip(RoundedCornerShape(22.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(horizontal = 10.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Security / Mode Indicator Icon
                    if (tab.isIncognito) {
                        Icon(
                            imageVector = Icons.Default.Security,
                            contentDescription = "Incognito Mode",
                            tint = Color(0xFFA855F7),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                    } else if (isHttps) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "Secure Connection",
                            tint = if (settings.theme.name.contains("GX")) GlobePalettes.GxCrimson else GlobePalettes.ElectricCyan,
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                    } else if (!isGlobe) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = "Not Secure",
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                    }

                    // Text Field
                    Box(modifier = Modifier.weight(1f)) {
                        if (inputText.isEmpty() && !isEditing) {
                            Text(
                                text = "Search with ${settings.searchEngine.displayName} or type URL",
                                style = TextStyle(fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.75f)),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        BasicTextField(
                            value = inputText,
                            onValueChange = {
                                inputText = it
                                isEditing = true
                            },
                            singleLine = true,
                            textStyle = TextStyle(
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onSurface,
                                fontWeight = FontWeight.Normal
                            ),
                            cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Go),
                            keyboardActions = KeyboardActions(
                                onGo = {
                                    focusManager.clearFocus()
                                    isEditing = false
                                    onNavigate(inputText)
                                }
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("omnibox_input")
                        )
                    }

                    // Quick Clear Button if editing
                    if (inputText.isNotEmpty() && isEditing) {
                        IconButton(
                            onClick = { inputText = "" },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Clear",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    // Google Lens Distinctive UI Button
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.8f))
                            .clickable { onOpenLens() }
                            .testTag("omnibox_lens_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.CameraAlt,
                            contentDescription = "Google Lens Visual Search",
                            tint = Color(0xFF4285F4), // Google Blue
                            modifier = Modifier.size(18.dp)
                        )
                        // Tiny Google Accent Dot
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .align(Alignment.TopEnd)
                                .offset(x = (-2).dp, y = 2.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFEA4335)) // Google Red
                        )
                    }

                    Spacer(modifier = Modifier.width(2.dp))

                    // Gemini Spark Assistant Icon button
                    IconButton(
                        onClick = onOpenAi,
                        modifier = Modifier
                            .size(28.dp)
                            .testTag("omnibox_gemini_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = "Gemini AI",
                            tint = if (settings.theme.name.contains("GX")) GlobePalettes.GxCrimson else GlobePalettes.ElectricCyan,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            // Reload or Stop Button
            IconButton(
                onClick = onReload,
                modifier = Modifier
                    .size(40.dp)
                    .testTag("omnibox_reload_button")
            ) {
                Icon(
                    imageVector = if (tab.isLoading) Icons.Default.Close else Icons.Default.Refresh,
                    contentDescription = if (tab.isLoading) "Stop" else "Reload",
                    tint = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.size(20.dp)
                )
            }

            // Tabs Switcher Counter Button
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.18f))
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

            // More Menu Button
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
