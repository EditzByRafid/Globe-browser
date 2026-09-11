package com.example.ui.components

import android.graphics.Bitmap
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.BrowserSettings
import com.example.model.LensAnalysisResult
import com.example.model.LensMode
import com.example.ui.theme.GlobePalettes
import com.example.ui.theme.chromeCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GoogleLensSheet(
    settings: BrowserSettings,
    lensImage: Bitmap?,
    isAnalyzing: Boolean,
    analysisResult: LensAnalysisResult?,
    activeMode: LensMode,
    onChangeMode: (LensMode) -> Unit,
    onTakePhoto: () -> Unit,
    onPickImage: () -> Unit,
    onCaptureWebPage: () -> Unit,
    onSelectSampleImage: (String) -> Unit,
    onPerformGoogleSearch: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val clipboardManager = LocalClipboardManager.current
    var copiedNotice by remember { mutableStateOf(false) }

    // Animated laser scanner beam
    val infiniteTransition = rememberInfiniteTransition(label = "laser")
    val laserOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "laser_pos"
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
                .padding(horizontal = 16.dp, vertical = 4.dp)
        ) {
            // Top Bar with Google Lens Branding
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(
                                Brush.linearGradient(
                                    listOf(
                                        Color(0xFF4285F4), // Blue
                                        Color(0xFFEA4335), // Red
                                        Color(0xFFFBBC05), // Yellow
                                        Color(0xFF34A853)  // Green
                                    )
                                )
                            )
                            .padding(2.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(RoundedCornerShape(8.dp))
                                .background(MaterialTheme.colorScheme.surface),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.CameraAlt,
                                contentDescription = "Google Lens",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(
                                text = "Google Lens",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = MaterialTheme.colorScheme.primaryContainer
                            ) {
                                Text(
                                    text = "Visual Search",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                )
                            }
                        }
                        Text(
                            text = "Search what you see with AI vision",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                IconButton(onClick = onDismiss, modifier = Modifier.testTag("lens_close_button")) {
                    Icon(Icons.Default.Close, contentDescription = "Close Lens")
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Lens Mode Selector Chips (Search, Text OCR, Shopping, Places)
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(LensMode.values()) { mode ->
                    FilterChip(
                        selected = (activeMode == mode),
                        onClick = { onChangeMode(mode) },
                        label = { Text("${mode.iconEmoji} ${mode.label}", fontSize = 12.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(14.dp),
                contentPadding = PaddingValues(bottom = 32.dp)
            ) {
                // Viewfinder / Image Preview Section
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(240.dp)
                            .chromeCard(
                                shape = RoundedCornerShape(18.dp)
                            ),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(RoundedCornerShape(18.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            if (lensImage != null) {
                                Image(
                                    bitmap = lensImage.asImageBitmap(),
                                    contentDescription = "Visual Search Target",
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )

                                // Scanning laser line animation
                                if (isAnalyzing) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(3.dp)
                                            .align(Alignment.TopCenter)
                                            .offset(y = (240 * laserOffset).dp)
                                            .background(
                                                Brush.horizontalGradient(
                                                    listOf(
                                                        Color.Transparent,
                                                        GlobePalettes.ElectricCyan,
                                                        Color.White,
                                                        GlobePalettes.ElectricCyan,
                                                        Color.Transparent
                                                    )
                                                )
                                            )
                                    )
                                }

                                // Viewfinder corner brackets
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(16.dp)
                                        .border(
                                            width = 1.5.dp,
                                            brush = Brush.linearGradient(
                                                listOf(Color(0xFF4285F4), Color(0xFF34A853))
                                            ),
                                            shape = RoundedCornerShape(12.dp)
                                        )
                                )
                            } else {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(10.dp),
                                    modifier = Modifier.padding(16.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.PhotoCamera,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(48.dp)
                                    )
                                    Text(
                                        text = "Select an image or take a photo to search with Google Lens",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }

                            // Analyzing overlay badge
                            if (isAnalyzing) {
                                Surface(
                                    shape = RoundedCornerShape(20.dp),
                                    color = Color.Black.copy(alpha = 0.75f),
                                    modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 14.dp)
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                                    ) {
                                        CircularProgressIndicator(
                                            modifier = Modifier.size(14.dp),
                                            strokeWidth = 2.dp,
                                            color = GlobePalettes.ElectricCyan
                                        )
                                        Text(
                                            text = "Google Lens is analyzing image...",
                                            fontSize = 12.sp,
                                            color = Color.White,
                                            fontWeight = FontWeight.Medium
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Input Source Action Buttons (Capture, Upload, Capture Tab)
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = onTakePhoto,
                            modifier = Modifier
                                .weight(1f)
                                .testTag("lens_capture_camera_button"),
                            shape = RoundedCornerShape(12.dp),
                            contentPadding = PaddingValues(vertical = 10.dp)
                        ) {
                            Icon(Icons.Default.PhotoCamera, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Take Photo", fontSize = 12.sp)
                        }

                        OutlinedButton(
                            onClick = onPickImage,
                            modifier = Modifier
                                .weight(1f)
                                .testTag("lens_upload_image_button"),
                            shape = RoundedCornerShape(12.dp),
                            contentPadding = PaddingValues(vertical = 10.dp)
                        ) {
                            Icon(Icons.Default.UploadFile, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Upload File", fontSize = 12.sp)
                        }

                        OutlinedButton(
                            onClick = onCaptureWebPage,
                            modifier = Modifier
                                .weight(1f)
                                .testTag("lens_capture_webpage_button"),
                            shape = RoundedCornerShape(12.dp),
                            contentPadding = PaddingValues(vertical = 10.dp)
                        ) {
                            Icon(Icons.Default.Screenshot, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Scan Page", fontSize = 12.sp)
                        }
                    }
                }

                // Quick Preset Samples for instant testing
                item {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "Or try a sample search:",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(bottom = 6.dp)
                        )
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            val samples = listOf(
                                Triple("Eiffel Tower", "🏛️ Landmark", "sample_landmark"),
                                Triple("Pixel 9 Pro", "📱 Product", "sample_device"),
                                Triple("Monstera Plant", "🌿 Nature", "sample_nature"),
                                Triple("Receipt OCR", "🧾 Text", "sample_text")
                            )
                            for (s in samples) {
                                SuggestionChip(
                                    onClick = { onSelectSampleImage(s.third) },
                                    label = { Text(s.second, fontSize = 11.sp) },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }
                }

                // Results Section
                if (analysisResult != null) {
                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .chromeCard(
                                    shape = RoundedCornerShape(16.dp)
                                ),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f))
                        ) {
                            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = analysisResult.mainTitle,
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Text(
                                            text = "Category: ${analysisResult.detectedCategory}",
                                            fontSize = 12.sp,
                                            color = MaterialTheme.colorScheme.primary,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }

                                    Button(
                                        onClick = { onPerformGoogleSearch(analysisResult.searchQuery) },
                                        shape = RoundedCornerShape(20.dp),
                                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                                        modifier = Modifier.testTag("lens_search_google_button")
                                    ) {
                                        Icon(Icons.Default.Search, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Search Google", fontSize = 12.sp)
                                    }
                                }

                                Text(
                                    text = analysisResult.description,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )

                                // Extracted Text (OCR) Card if available
                                if (!analysisResult.extractedText.isNullOrBlank()) {
                                    Surface(
                                        shape = RoundedCornerShape(10.dp),
                                        color = MaterialTheme.colorScheme.surface,
                                        modifier = Modifier.fillMaxWidth().padding(top = 4.dp)
                                    ) {
                                        Column(modifier = Modifier.padding(12.dp)) {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.SpaceBetween
                                            ) {
                                                Text(
                                                    text = "📝 Extracted Text (OCR)",
                                                    fontSize = 12.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = MaterialTheme.colorScheme.primary
                                                )
                                                Row {
                                                    IconButton(
                                                        onClick = {
                                                            clipboardManager.setText(AnnotatedString(analysisResult.extractedText))
                                                            copiedNotice = true
                                                        },
                                                        modifier = Modifier.size(28.dp)
                                                    ) {
                                                        Icon(Icons.Default.ContentCopy, contentDescription = "Copy Text", modifier = Modifier.size(16.dp))
                                                    }
                                                    IconButton(
                                                        onClick = { onPerformGoogleSearch(analysisResult.extractedText) },
                                                        modifier = Modifier.size(28.dp)
                                                    ) {
                                                        Icon(Icons.Default.Search, contentDescription = "Search text", modifier = Modifier.size(16.dp))
                                                    }
                                                }
                                            }
                                            Text(
                                                text = analysisResult.extractedText,
                                                fontSize = 13.sp,
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                            if (copiedNotice) {
                                                Text("✓ Copied to clipboard", fontSize = 11.sp, color = Color(0xFF10B981))
                                            }
                                        }
                                    }
                                }

                                // Visual Matches
                                if (analysisResult.visualMatches.isNotEmpty()) {
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "Visual & Web Matches",
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold
                                    )
                                    for (match in analysisResult.visualMatches) {
                                        Surface(
                                            shape = RoundedCornerShape(10.dp),
                                            color = MaterialTheme.colorScheme.surface,
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clickable { onPerformGoogleSearch(match.query) }
                                        ) {
                                            Row(
                                                modifier = Modifier.padding(12.dp),
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                                            ) {
                                                Text(text = match.iconEmoji, fontSize = 20.sp)
                                                Column(modifier = Modifier.weight(1f)) {
                                                    Text(
                                                        text = match.title,
                                                        fontWeight = FontWeight.SemiBold,
                                                        fontSize = 13.sp,
                                                        maxLines = 1,
                                                        overflow = TextOverflow.Ellipsis
                                                    )
                                                    Text(
                                                        text = match.subtitle,
                                                        fontSize = 11.sp,
                                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                                    )
                                                }
                                                Icon(
                                                    imageVector = Icons.Default.OpenInNew,
                                                    contentDescription = "Open Search",
                                                    modifier = Modifier.size(16.dp),
                                                    tint = MaterialTheme.colorScheme.primary
                                                )
                                            }
                                        }
                                    }
                                }

                                // Shopping Matches if in shopping mode
                                if (analysisResult.shoppingMatches.isNotEmpty()) {
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "Shopping & Product Listings",
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFFEA580C)
                                    )
                                    for (shop in analysisResult.shoppingMatches) {
                                        Surface(
                                            shape = RoundedCornerShape(10.dp),
                                            color = MaterialTheme.colorScheme.surface,
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clickable { onPerformGoogleSearch("${shop.query} buy online") }
                                        ) {
                                            Row(
                                                modifier = Modifier.padding(12.dp),
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                                            ) {
                                                Text(text = "🛍️", fontSize = 20.sp)
                                                Column(modifier = Modifier.weight(1f)) {
                                                    Text(
                                                        text = shop.title,
                                                        fontWeight = FontWeight.SemiBold,
                                                        fontSize = 13.sp
                                                    )
                                                    Text(
                                                        text = shop.subtitle,
                                                        fontSize = 11.sp,
                                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                                    )
                                                }
                                                Text(
                                                    text = "Find Best Price",
                                                    fontSize = 11.sp,
                                                    color = MaterialTheme.colorScheme.primary,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            }
                                        }
                                    }
                                }

                                // Direct Google Lens web portal link
                                OutlinedButton(
                                    onClick = { onPerformGoogleSearch("https://lens.google.com/") },
                                    modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Icon(Icons.Default.Public, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Open Google Lens Web (lens.google.com)")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
