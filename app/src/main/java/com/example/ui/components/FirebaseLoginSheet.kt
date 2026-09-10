package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.auth.FirebaseUserState
import com.example.model.BrowserSettings
import com.example.ui.theme.GlobePalettes
import com.example.ui.theme.liquidGlass

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FirebaseLoginSheet(
    settings: BrowserSettings,
    userState: FirebaseUserState,
    onSignIn: (email: String, pass: String, (Boolean, String?) -> Unit) -> Unit,
    onSignUp: (email: String, pass: String, displayName: String, (Boolean, String?) -> Unit) -> Unit,
    onSignInAnonymously: ((Boolean, String?) -> Unit) -> Unit,
    onSignInWithGoogle: ((Boolean, String?) -> Unit) -> Unit = { _ -> },
    onSendPasswordReset: (email: String, (Boolean, String?) -> Unit) -> Unit,
    onSignOut: () -> Unit,
    onSyncData: ((Int) -> Unit) -> Unit,
    onDismiss: () -> Unit
) {
    val clipboardManager = LocalClipboardManager.current
    var activeTab by remember { mutableIntStateOf(0) } // 0: Sign In, 1: Sign Up, 2: Guest

    // Form inputs
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var displayName by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var autoGoogleLoginEnabled by remember { mutableStateOf(true) }

    // Loading & feedback states
    var isLoading by remember { mutableStateOf(false) }
    var statusMessage by remember { mutableStateOf<String?>(null) }
    var isErrorMessage by remember { mutableStateOf(false) }
    var isSyncing by remember { mutableStateOf(false) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = MaterialTheme.colorScheme.background,
        dragHandle = { BottomSheetDefaults.DragHandle() }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp, vertical = 4.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header
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
                                    listOf(Color(0xFFFFCA28), Color(0xFFF57C00), Color(0xFFD32F2F))
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.CloudSync,
                            contentDescription = "Firebase Cloud Auth",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(
                                text = "Firebase Cloud Account",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = Color(0xFFFFA000).copy(alpha = 0.2f)
                            ) {
                                Text(
                                    text = "Firebase Auth",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFFF8F00),
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                )
                            }
                        }
                        Text(
                            text = "Sync tabs, bookmarks & history securely across devices",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                IconButton(onClick = onDismiss, modifier = Modifier.testTag("auth_close_button")) {
                    Icon(Icons.Default.Close, contentDescription = "Close Auth")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Feedback alert if present
            AnimatedVisibility(visible = statusMessage != null) {
                statusMessage?.let { msg ->
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = if (isErrorMessage) MaterialTheme.colorScheme.errorContainer else Color(0xFFE6F4EA),
                        modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = if (isErrorMessage) Icons.Default.ErrorOutline else Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = if (isErrorMessage) MaterialTheme.colorScheme.error else Color(0xFF137333),
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = msg,
                                fontSize = 12.sp,
                                color = if (isErrorMessage) MaterialTheme.colorScheme.onErrorContainer else Color(0xFF137333),
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }

            if (userState.isLoggedIn) {
                // User is signed in with Firebase: Display Profile & Sync Center
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .liquidGlass(
                            enabled = settings.liquidGlassEnabled,
                            lowEndMode = settings.lowEndModeEnabled,
                            shape = RoundedCornerShape(18.dp)
                        ),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        // User Avatar
                        Box(
                            modifier = Modifier
                                .size(72.dp)
                                .clip(CircleShape)
                                .background(
                                    Brush.linearGradient(
                                        listOf(GlobePalettes.ElectricCyan, MaterialTheme.colorScheme.primary)
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = (userState.displayName?.take(1) ?: userState.email?.take(1) ?: "U").uppercase(),
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black
                            )
                        }

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = userState.displayName ?: "Firebase User",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = userState.email ?: "Anonymous Session",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                modifier = Modifier.padding(top = 4.dp)
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = if (userState.isAnonymous) Color(0xFFFEF3C7) else Color(0xFFD1FAE5)
                                ) {
                                    Text(
                                        text = if (userState.isAnonymous) "Guest Account" else "Verified Cloud User",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = if (userState.isAnonymous) Color(0xFFB45309) else Color(0xFF047857),
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }

                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))

                        // Account Metadata
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = MaterialTheme.colorScheme.surface,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("Firebase UID:", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = userState.uid.take(12) + "...",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Medium
                                        )
                                        IconButton(
                                            onClick = {
                                                clipboardManager.setText(AnnotatedString(userState.uid))
                                                statusMessage = "UID copied to clipboard"
                                                isErrorMessage = false
                                            },
                                            modifier = Modifier.size(24.dp)
                                        ) {
                                            Icon(Icons.Default.ContentCopy, contentDescription = "Copy UID", modifier = Modifier.size(14.dp))
                                        }
                                    }
                                }

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Cloud Sync Status:", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Text(
                                        text = if (userState.syncedItemsCount > 0) "${userState.syncedItemsCount} items synced" else "Up to date",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = Color(0xFF10B981)
                                    )
                                }
                            }
                        }

                        // Cloud Sync Button
                        Button(
                            onClick = {
                                isSyncing = true
                                onSyncData { count ->
                                    isSyncing = false
                                    statusMessage = "Successfully synchronized $count bookmarks and history records to Firebase!"
                                    isErrorMessage = false
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("firebase_sync_button"),
                            shape = RoundedCornerShape(12.dp),
                            enabled = !isSyncing
                        ) {
                            if (isSyncing) {
                                CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp, color = Color.White)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Syncing to Firebase...")
                            } else {
                                Icon(Icons.Default.Sync, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Sync Bookmarks & History Now")
                            }
                        }

                        // Sign Out Button
                        OutlinedButton(
                            onClick = {
                                onSignOut()
                                statusMessage = "Signed out of Firebase account."
                                isErrorMessage = false
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("firebase_signout_button"),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)
                        ) {
                            Icon(Icons.Default.Logout, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Sign Out of Firebase")
                        }
                    }
                }
            } else {
                // Not logged in: Tabbed Login / Sign Up Form
                TabRow(
                    selectedTabIndex = activeTab,
                    containerColor = Color.Transparent,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Tab(
                        selected = (activeTab == 0),
                        onClick = { activeTab = 0; statusMessage = null },
                        text = { Text("Sign In", fontWeight = FontWeight.SemiBold) }
                    )
                    Tab(
                        selected = (activeTab == 1),
                        onClick = { activeTab = 1; statusMessage = null },
                        text = { Text("Create Account", fontWeight = FontWeight.SemiBold) }
                    )
                    Tab(
                        selected = (activeTab == 2),
                        onClick = { activeTab = 2; statusMessage = null },
                        text = { Text("Guest", fontWeight = FontWeight.SemiBold) }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .liquidGlass(
                            enabled = settings.liquidGlassEnabled,
                            lowEndMode = settings.lowEndModeEnabled,
                            shape = RoundedCornerShape(18.dp)
                        ),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        if (activeTab == 0 || activeTab == 1) {
                            // Primary Google Sign In Button
                            Button(
                                onClick = {
                                    isLoading = true
                                    statusMessage = null
                                    onSignInWithGoogle { success, errorMsg ->
                                        isLoading = false
                                        if (success) {
                                            statusMessage = "Signed in with Google Account successfully!"
                                            isErrorMessage = false
                                        } else {
                                            statusMessage = errorMsg ?: "Google Sign In encountered an issue"
                                            isErrorMessage = true
                                        }
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                                    .testTag("google_signin_button"),
                                shape = RoundedCornerShape(14.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color.White,
                                    contentColor = Color(0xFF1F1F1F)
                                ),
                                elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    // Google Multi-Color 'G' Dot
                                    Box(
                                        modifier = Modifier
                                            .size(24.dp)
                                            .clip(CircleShape)
                                            .background(Color(0xFF4285F4)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text("G", color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 14.sp)
                                    }
                                    Text(
                                        text = "Continue with Google",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        color = Color(0xFF1F1F1F)
                                    )
                                }
                            }

                            // Auto-Login Google toggle
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "Auto-login with Google",
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Text(
                                        text = "Automatically connect account upon app launch",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                Switch(
                                    checked = autoGoogleLoginEnabled,
                                    onCheckedChange = { autoGoogleLoginEnabled = it }
                                )
                            }

                            // OR Divider
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                HorizontalDivider(modifier = Modifier.weight(1f), color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
                                Text("OR EMAIL", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                HorizontalDivider(modifier = Modifier.weight(1f), color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
                            }
                        }

                        if (activeTab == 1) {
                            // Display name for sign up
                            OutlinedTextField(
                                value = displayName,
                                onValueChange = { displayName = it },
                                label = { Text("Full Name / Nickname") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth().testTag("auth_name_input"),
                                shape = RoundedCornerShape(12.dp),
                                leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) }
                            )
                        }

                        if (activeTab == 0 || activeTab == 1) {
                            // Email input
                            OutlinedTextField(
                                value = email,
                                onValueChange = { email = it },
                                label = { Text("Email Address") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth().testTag("auth_email_input"),
                                shape = RoundedCornerShape(12.dp),
                                leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                            )

                            // Password input
                            OutlinedTextField(
                                value = password,
                                onValueChange = { password = it },
                                label = { Text("Password") },
                                singleLine = true,
                                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                                modifier = Modifier.fillMaxWidth().testTag("auth_password_input"),
                                shape = RoundedCornerShape(12.dp),
                                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                                trailingIcon = {
                                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                        Icon(
                                            imageVector = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                            contentDescription = "Toggle password"
                                        )
                                    }
                                },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
                            )

                            if (activeTab == 0) {
                                // Forgot password link
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.End
                                ) {
                                    TextButton(
                                        onClick = {
                                            if (email.isBlank()) {
                                                statusMessage = "Please enter your email address above first"
                                                isErrorMessage = true
                                            } else {
                                                isLoading = true
                                                onSendPasswordReset(email) { success, err ->
                                                    isLoading = false
                                                    if (success) {
                                                        statusMessage = "Password reset email sent to $email"
                                                        isErrorMessage = false
                                                    } else {
                                                        statusMessage = err ?: "Could not send reset email"
                                                        isErrorMessage = true
                                                    }
                                                }
                                            }
                                        }
                                    ) {
                                        Text("Forgot Password?", fontSize = 12.sp)
                                    }
                                }

                                // Sign In Button
                                Button(
                                    onClick = {
                                        if (email.isBlank() || password.isBlank()) {
                                            statusMessage = "Please enter email and password"
                                            isErrorMessage = true
                                        } else {
                                            isLoading = true
                                            onSignIn(email, password) { success, err ->
                                                isLoading = false
                                                if (success) {
                                                    statusMessage = "Signed in successfully!"
                                                    isErrorMessage = false
                                                } else {
                                                    statusMessage = err ?: "Sign in failed"
                                                    isErrorMessage = true
                                                }
                                            }
                                        }
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("auth_submit_signin_button"),
                                    shape = RoundedCornerShape(12.dp),
                                    enabled = !isLoading
                                ) {
                                    if (isLoading) {
                                        CircularProgressIndicator(modifier = Modifier.size(16.dp), color = Color.White, strokeWidth = 2.dp)
                                    } else {
                                        Icon(Icons.Default.Login, contentDescription = null, modifier = Modifier.size(18.dp))
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("Sign In with Firebase")
                                    }
                                }
                            } else {
                                // Sign Up Button
                                Button(
                                    onClick = {
                                        if (email.isBlank() || password.isBlank()) {
                                            statusMessage = "Please enter email and password"
                                            isErrorMessage = true
                                        } else if (password.length < 6) {
                                            statusMessage = "Password must be at least 6 characters"
                                            isErrorMessage = true
                                        } else {
                                            isLoading = true
                                            onSignUp(email, password, displayName) { success, err ->
                                                isLoading = false
                                                if (success) {
                                                    statusMessage = "Account created successfully!"
                                                    isErrorMessage = false
                                                } else {
                                                    statusMessage = err ?: "Sign up failed"
                                                    isErrorMessage = true
                                                }
                                            }
                                        }
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("auth_submit_signup_button"),
                                    shape = RoundedCornerShape(12.dp),
                                    enabled = !isLoading
                                ) {
                                    if (isLoading) {
                                        CircularProgressIndicator(modifier = Modifier.size(16.dp), color = Color.White, strokeWidth = 2.dp)
                                    } else {
                                        Icon(Icons.Default.PersonAdd, contentDescription = null, modifier = Modifier.size(18.dp))
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("Create Firebase Account")
                                    }
                                }
                            }
                        } else {
                            // Guest / Anonymous Sign In Tab
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Shield,
                                    contentDescription = null,
                                    tint = GlobePalettes.ElectricCyan,
                                    modifier = Modifier.size(42.dp)
                                )
                                Text(
                                    text = "Anonymous Guest Session",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Start browsing immediately with cloud bookmark synchronization. No personal data or email required.",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )

                                Button(
                                    onClick = {
                                        isLoading = true
                                        onSignInAnonymously { success, err ->
                                            isLoading = false
                                            if (success) {
                                                statusMessage = "Signed in as Guest!"
                                                isErrorMessage = false
                                            } else {
                                                statusMessage = err ?: "Guest session failed"
                                                isErrorMessage = true
                                            }
                                        }
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("auth_guest_signin_button"),
                                    shape = RoundedCornerShape(12.dp),
                                    enabled = !isLoading
                                ) {
                                    if (isLoading) {
                                        CircularProgressIndicator(modifier = Modifier.size(16.dp), color = Color.White, strokeWidth = 2.dp)
                                    } else {
                                        Icon(Icons.Default.Bolt, contentDescription = null, modifier = Modifier.size(18.dp))
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("Continue as Guest")
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
