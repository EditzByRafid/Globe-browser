package com.example.ui.components

import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ai.ChatMessage
import com.example.ai.GeminiModel
import com.example.model.BrowserSettings
import com.example.model.TabItem
import com.example.ui.theme.GlobePalettes
import com.example.ui.theme.liquidGlass

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GeminiAiSheet(
    currentTab: TabItem,
    messages: List<ChatMessage>,
    isLoading: Boolean,
    settings: BrowserSettings,
    onSendMessage: (prompt: String, model: GeminiModel, useHighThinking: Boolean, useGoogleSearch: Boolean, useGoogleMaps: Boolean, imageBitmap: Bitmap?) -> Unit,
    onSummarizePage: (url: String, title: String) -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    var inputPrompt by remember { mutableStateOf("") }
    var selectedModel by remember { mutableStateOf(GeminiModel.FLASH) }
    var highThinkingEnabled by remember { mutableStateOf(false) }
    var googleSearchGrounding by remember { mutableStateOf(true) }
    var googleMapsGrounding by remember { mutableStateOf(false) }
    var pickedImageBitmap by remember { mutableStateOf<Bitmap?>(null) }

    // Google Play Policy compliant zero-permission Photo Picker
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            try {
                val bitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    ImageDecoder.decodeBitmap(ImageDecoder.createSource(context.contentResolver, uri))
                } else {
                    @Suppress("DEPRECATION")
                    MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
                }
                pickedImageBitmap = bitmap
                selectedModel = GeminiModel.PRO // Pro model for vision/image understanding
            } catch (_: Exception) {}
        }
    }

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
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = "Gemini AI",
                        tint = GlobePalettes.ElectricCyan,
                        modifier = Modifier.size(24.dp)
                    )
                    Column {
                        Text(
                            text = "Gemini AI & Google Lens",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            text = if (pickedImageBitmap != null) "Google Lens Vision Active" else selectedModel.displayName,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Close AI")
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // AI Model & Grounding Controls
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(GeminiModel.entries) { model ->
                    FilterChip(
                        selected = (selectedModel == model),
                        onClick = {
                            selectedModel = model
                            highThinkingEnabled = (model == GeminiModel.PRO || model == GeminiModel.PRO_3_5)
                        },
                        label = {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text(model.displayName, fontSize = 11.sp)
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = if (selectedModel == model) MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.2f) else MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                                ) {
                                    Text(
                                        text = model.badge,
                                        fontSize = 8.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 3.dp, vertical = 1.dp)
                                    )
                                }
                            }
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = when (model) {
                                    GeminiModel.LITE -> Icons.Default.Speed
                                    GeminiModel.FLASH -> Icons.Default.Bolt
                                    GeminiModel.PRO, GeminiModel.PRO_3_5 -> Icons.Default.Psychology
                                    GeminiModel.FLASH_8, GeminiModel.FLASH_7 -> Icons.Default.AutoAwesome
                                    else -> Icons.Default.Science
                                },
                                contentDescription = null,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    )
                }

                item {
                    FilterChip(
                        selected = googleSearchGrounding,
                        onClick = { googleSearchGrounding = !googleSearchGrounding },
                        label = { Text("Google Search", fontSize = 11.sp) },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, modifier = Modifier.size(14.dp)) }
                    )
                }
                item {
                    FilterChip(
                        selected = googleMapsGrounding,
                        onClick = { googleMapsGrounding = !googleMapsGrounding },
                        label = { Text("Google Maps", fontSize = 11.sp) },
                        leadingIcon = { Icon(Icons.Default.Place, contentDescription = null, modifier = Modifier.size(14.dp)) }
                    )
                }
            }

            // Quick Page Action: "Summarize this page" button if visiting a URL
            if (!currentTab.url.startsWith("globe://") && currentTab.url.isNotBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedButton(
                    onClick = { onSummarizePage(currentTab.url, currentTab.title) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Icon(Icons.Default.Summarize, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Summarize current webpage (${currentTab.title.take(28)}...)", fontSize = 12.sp)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Conversation Messages Thread
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(vertical = 8.dp)
            ) {
                if (messages.isEmpty()) {
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.SmartToy,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f),
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "Ask Globe AI Anything",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Powered by Gemini 3.5 Flash & 3.1 Pro with Search and Maps grounding",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                items(messages, key = { it.id }) { msg ->
                    val isUser = (msg.sender == "user")
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
                    ) {
                        Card(
                            modifier = Modifier
                                .widthIn(max = 310.dp)
                                .liquidGlass(
                                    enabled = settings.liquidGlassEnabled,
                                    lowEndMode = settings.lowEndModeEnabled,
                                    shape = RoundedCornerShape(
                                        topStart = 16.dp,
                                        topEnd = 16.dp,
                                        bottomStart = if (isUser) 16.dp else 4.dp,
                                        bottomEnd = if (isUser) 4.dp else 16.dp
                                    ),
                                    tintColor = if (isUser) MaterialTheme.colorScheme.primary.copy(alpha = 0.25f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                                ),
                            colors = CardDefaults.cardColors(containerColor = Color.Transparent)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                if (msg.imageBitmap != null) {
                                    Image(
                                        bitmap = msg.imageBitmap.asImageBitmap(),
                                        contentDescription = "Analyzed Lens Image",
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(160.dp)
                                            .clip(RoundedCornerShape(8.dp))
                                            .padding(bottom = 8.dp)
                                    )
                                }

                                Text(
                                    text = msg.text,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )

                                if (msg.isGroundingSearchUsed && msg.groundingUrls.isNotEmpty()) {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "Sources:",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    for (url in msg.groundingUrls.take(3)) {
                                        Text(
                                            text = "• $url",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            maxLines = 1
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                if (isLoading) {
                    item {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.padding(8.dp)
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(18.dp),
                                strokeWidth = 2.dp,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = if (highThinkingEnabled) "Gemini Thinking deeply..." else "Globe AI is generating response...",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            // Image Preview Bar if Google Lens photo picked
            if (pickedImageBitmap != null) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Image(
                            bitmap = pickedImageBitmap!!.asImageBitmap(),
                            contentDescription = "Selected Photo",
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(6.dp))
                        )
                        Text("Google Lens Image attached", style = MaterialTheme.typography.labelSmall)
                    }
                    IconButton(onClick = { pickedImageBitmap = null }, modifier = Modifier.size(24.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Remove photo", modifier = Modifier.size(16.dp))
                    }
                }
            }

            // Input Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Photo Picker button (Google Lens)
                IconButton(
                    onClick = {
                        photoPickerLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    },
                    modifier = Modifier
                        .size(44.dp)
                        .testTag("lens_pick_photo_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.CameraAlt,
                        contentDescription = "Google Lens Photo Analysis",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }

                TextField(
                    value = inputPrompt,
                    onValueChange = { inputPrompt = it },
                    placeholder = { Text("Ask Gemini or describe photo...", fontSize = 14.sp) },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("gemini_prompt_input"),
                    shape = RoundedCornerShape(20.dp),
                    colors = TextFieldDefaults.colors(
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                    keyboardActions = KeyboardActions(
                        onSend = {
                            if (inputPrompt.isNotBlank() || pickedImageBitmap != null) {
                                val text = inputPrompt
                                val img = pickedImageBitmap
                                inputPrompt = ""
                                pickedImageBitmap = null
                                onSendMessage(text, selectedModel, highThinkingEnabled, googleSearchGrounding, googleMapsGrounding, img)
                            }
                        }
                    )
                )

                IconButton(
                    onClick = {
                        if (inputPrompt.isNotBlank() || pickedImageBitmap != null) {
                            val text = inputPrompt
                            val img = pickedImageBitmap
                            inputPrompt = ""
                            pickedImageBitmap = null
                            onSendMessage(text, selectedModel, highThinkingEnabled, googleSearchGrounding, googleMapsGrounding, img)
                        }
                    },
                    enabled = (inputPrompt.isNotBlank() || pickedImageBitmap != null) && !isLoading,
                    modifier = Modifier
                        .size(44.dp)
                        .testTag("gemini_send_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Send,
                        contentDescription = "Send",
                        tint = if (inputPrompt.isNotBlank() || pickedImageBitmap != null) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
                    )
                }
            }
        }
    }
}
