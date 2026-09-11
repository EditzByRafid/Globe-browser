package com.example.ui.components

import android.speech.tts.TextToSpeech
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.BrowserSettings
import com.example.model.ReaderArticle
import com.example.ui.theme.GlobePalettes
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReaderModeView(
    article: ReaderArticle,
    settings: BrowserSettings,
    onUpdateSettings: (BrowserSettings) -> Unit,
    onClose: () -> Unit
) {
    val context = LocalContext.current
    var isSpeaking by remember { mutableStateOf(false) }
    var ttsInstance by remember { mutableStateOf<TextToSpeech?>(null) }
    var showFormatMenu by remember { mutableStateOf(false) }

    // Initialize TTS
    DisposableEffect(Unit) {
        var tts: TextToSpeech? = null
        tts = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                tts?.language = Locale.US
            }
        }
        ttsInstance = tts
        onDispose {
            tts?.stop()
            tts?.shutdown()
        }
    }

    // Colors according to reader theme
    val (readerBg, readerTextColor) = when (settings.readerTheme) {
        "LIGHT" -> Color(0xFFFAFAFA) to Color(0xFF202124)
        "SEPIA" -> Color(0xFFFBF0D9) to Color(0xFF43301B)
        "OLED" -> Color(0xFF000000) to Color(0xFFECEFF1)
        else -> Color(0xFF202124) to Color(0xFFE8EAED) // DARK
    }

    val chosenFontFamily = when (settings.readerFontFamily) {
        "SERIF" -> FontFamily.Serif
        "MONO" -> FontFamily.Monospace
        else -> FontFamily.SansSerif
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(readerBg)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Reader Top Control Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(onClick = onClose) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Exit Reader Mode",
                        tint = readerTextColor
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Read Aloud / TTS
                    IconButton(
                        onClick = {
                            val tts = ttsInstance
                            if (tts != null) {
                                if (isSpeaking) {
                                    tts.stop()
                                    isSpeaking = false
                                } else {
                                    val fullText = article.title + ". " + article.paragraphs.joinToString(". ")
                                    tts.speak(fullText, TextToSpeech.QUEUE_FLUSH, null, "reader_tts")
                                    isSpeaking = true
                                    Toast.makeText(context, "Reading aloud...", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    ) {
                        Icon(
                            imageVector = if (isSpeaking) Icons.Default.VolumeUp else Icons.Default.VolumeMute,
                            contentDescription = "Read Aloud",
                            tint = if (isSpeaking) GlobePalettes.GoogleBlue else readerTextColor
                        )
                    }

                    // Format Settings (Font, Size, Theme)
                    Box {
                        IconButton(onClick = { showFormatMenu = !showFormatMenu }) {
                            Icon(
                                imageVector = Icons.Default.FormatSize,
                                contentDescription = "Typography Settings",
                                tint = readerTextColor
                            )
                        }

                        DropdownMenu(
                            expanded = showFormatMenu,
                            onDismissRequest = { showFormatMenu = false },
                            modifier = Modifier.width(260.dp)
                        ) {
                            // Themes Picker
                            Text(
                                text = "Reader Theme",
                                style = MaterialTheme.typography.labelMedium,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                            )
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp, vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                listOf(
                                    "LIGHT" to Color(0xFFFFFFFF),
                                    "SEPIA" to Color(0xFFFBF0D9),
                                    "DARK" to Color(0xFF2D2E30),
                                    "OLED" to Color(0xFF000000)
                                ).forEach { (thKey, thColor) ->
                                    val isSelected = settings.readerTheme == thKey
                                    Box(
                                        modifier = Modifier
                                            .size(36.dp)
                                            .clip(CircleShape)
                                            .background(thColor)
                                            .padding(if (isSelected) 3.dp else 0.dp)
                                    ) {
                                        IconButton(
                                            onClick = {
                                                onUpdateSettings(settings.copy(readerTheme = thKey))
                                            },
                                            modifier = Modifier.fillMaxSize()
                                        ) {
                                            if (isSelected) {
                                                Icon(
                                                    Icons.Default.Check,
                                                    contentDescription = null,
                                                    tint = if (thKey == "LIGHT" || thKey == "SEPIA") Color.Black else Color.White,
                                                    modifier = Modifier.size(16.dp)
                                                )
                                            }
                                        }
                                    }
                                }
                            }

                            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                            // Font Size Adjust
                            Text(
                                text = "Font Size: ${settings.readerFontSize} sp",
                                style = MaterialTheme.typography.labelMedium,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                            )
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                FilledTonalButton(
                                    onClick = {
                                        val newSize = (settings.readerFontSize - 2).coerceAtLeast(14)
                                        onUpdateSettings(settings.copy(readerFontSize = newSize))
                                    },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text("A-", fontWeight = FontWeight.Bold)
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                FilledTonalButton(
                                    onClick = {
                                        val newSize = (settings.readerFontSize + 2).coerceAtMost(28)
                                        onUpdateSettings(settings.copy(readerFontSize = newSize))
                                    },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text("A+", fontWeight = FontWeight.Bold)
                                }
                            }

                            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                            // Font Family
                            Text(
                                text = "Font Style",
                                style = MaterialTheme.typography.labelMedium,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                            )
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 4.dp),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                listOf("SERIF" to "Serif", "SANS" to "Sans", "MONO" to "Mono").forEach { (fKey, fLabel) ->
                                    val isSelected = settings.readerFontFamily == fKey
                                    FilterChip(
                                        selected = isSelected,
                                        onClick = { onUpdateSettings(settings.copy(readerFontFamily = fKey)) },
                                        label = { Text(fLabel, fontSize = 11.sp) },
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            HorizontalDivider(color = readerTextColor.copy(alpha = 0.15f))

            // Article Content Area (Scrollable)
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp, vertical = 20.dp)
            ) {
                // Domain & Reading Time Badge
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = article.domain.uppercase(),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = GlobePalettes.GoogleBlue
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "• ${article.readingTimeMinutes} min read (${article.wordCount} words)",
                        style = MaterialTheme.typography.labelSmall,
                        color = readerTextColor.copy(alpha = 0.6f)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Article Title
                Text(
                    text = article.title,
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontFamily = chosenFontFamily,
                        fontSize = (settings.readerFontSize + 8).sp,
                        lineHeight = (settings.readerFontSize + 14).sp,
                        fontWeight = FontWeight.Bold
                    ),
                    color = readerTextColor
                )

                if (!article.author.isNullOrBlank()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "By ${article.author}",
                        style = MaterialTheme.typography.bodySmall,
                        color = readerTextColor.copy(alpha = 0.7f)
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))
                HorizontalDivider(color = readerTextColor.copy(alpha = 0.12f))
                Spacer(modifier = Modifier.height(20.dp))

                // Body Paragraphs
                article.paragraphs.forEach { paragraph ->
                    Text(
                        text = paragraph,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontFamily = chosenFontFamily,
                            fontSize = settings.readerFontSize.sp,
                            lineHeight = (settings.readerFontSize * 1.6f).sp
                        ),
                        color = readerTextColor,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                }

                Spacer(modifier = Modifier.height(48.dp))

                // End of Reader card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = readerTextColor.copy(alpha = 0.05f))
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "You reached the end of the distraction-free article.",
                            style = MaterialTheme.typography.bodySmall,
                            color = readerTextColor.copy(alpha = 0.7f)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = onClose,
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = GlobePalettes.GoogleBlue)
                        ) {
                            Text("Back to Web View", color = Color.White)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(36.dp))
            }
        }
    }
}
