package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.BrowserSettings
import com.example.model.UserAccountItem
import com.example.ui.theme.GlobePalettes
import com.example.ui.theme.chromeCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountsSheet(
    settings: BrowserSettings,
    accounts: List<UserAccountItem>,
    onSwitchAccount: (Long) -> Unit,
    onAddAccount: (UserAccountItem) -> Unit = {},
    onDeleteAccount: (Long) -> Unit = {},
    onDismiss: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var showAddDialog by remember { mutableStateOf(false) }
    var newFullName by remember { mutableStateOf("") }
    var newEmail by remember { mutableStateOf("") }
    var newUsername by remember { mutableStateOf("") }
    var newRole by remember { mutableStateOf("Personal") }
    var selectedColorHex by remember { mutableStateOf("#1A73E8") }

    val filteredAccounts = remember(searchQuery, accounts) {
        if (searchQuery.isBlank()) {
            accounts
        } else {
            accounts.filter {
                it.fullName.contains(searchQuery, ignoreCase = true) ||
                it.email.contains(searchQuery, ignoreCase = true) ||
                it.username.contains(searchQuery, ignoreCase = true) ||
                it.role.contains(searchQuery, ignoreCase = true)
            }
        }
    }

    val currentAccount = accounts.find { it.isCurrent } ?: accounts.firstOrNull()

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
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(
                        imageVector = Icons.Default.AccountCircle,
                        contentDescription = "Accounts",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(26.dp)
                    )
                    Column {
                        Text(
                            text = "Private Account Vault",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Storage: ${accounts.size} / 700 Accounts Capacity",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    FilledTonalButton(
                        onClick = { showAddDialog = true },
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                        modifier = Modifier.testTag("add_account_btn")
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Add", fontSize = 12.sp)
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close Accounts")
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Current Active Profile Card
            if (currentAccount != null) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .chromeCard(shape = RoundedCornerShape(16.dp)),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        val avatarBg = try { Color(android.graphics.Color.parseColor(currentAccount.avatarColorHex)) } catch (_: Exception) { Color(0xFF1A73E8) }
                        Box(
                            modifier = Modifier
                                .size(46.dp)
                                .clip(CircleShape)
                                .background(avatarBg),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = currentAccount.fullName.take(1).uppercase(),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Text(
                                    text = currentAccount.fullName,
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold
                                )
                                SuggestionChip(
                                    onClick = {},
                                    label = { Text("ACTIVE", fontSize = 9.sp, fontWeight = FontWeight.Bold) },
                                    modifier = Modifier.height(22.dp)
                                )
                            }
                            Text(
                                text = currentAccount.email,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "Profile: ${currentAccount.role} • @${currentAccount.username}",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Search Bar across private accounts
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Search your accounts...", fontSize = 13.sp) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, modifier = Modifier.size(18.dp)) },
                trailingIcon = if (searchQuery.isNotEmpty()) {
                    {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(Icons.Default.Close, contentDescription = "Clear", modifier = Modifier.size(16.dp))
                        }
                    }
                } else null,
                shape = RoundedCornerShape(14.dp),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Accounts List
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                items(filteredAccounts, key = { it.id }) { item ->
                    val isCurrent = item.isCurrent
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSwitchAccount(item.id) },
                        colors = CardDefaults.cardColors(
                            containerColor = if (isCurrent) MaterialTheme.colorScheme.primary.copy(alpha = 0.12f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            val color = try { Color(android.graphics.Color.parseColor(item.avatarColorHex)) } catch (_: Exception) { Color(0xFF1A73E8) }
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(color),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = item.fullName.take(1).uppercase(),
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = item.fullName,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.SemiBold,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = item.email,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = "${item.role} • @${item.username}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }

                            if (isCurrent) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = "Active",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(20.dp)
                                )
                            } else {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    TextButton(onClick = { onSwitchAccount(item.id) }) {
                                        Text("Switch", fontSize = 12.sp)
                                    }
                                    if (accounts.size > 1) {
                                        IconButton(onClick = { onDeleteAccount(item.id) }) {
                                            Icon(Icons.Default.DeleteOutline, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(18.dp))
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Add Account Dialog
    if (showAddDialog) {
        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(Icons.Default.PersonAdd, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Text("Add Account to Vault", fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = newFullName,
                        onValueChange = { newFullName = it },
                        label = { Text("Account / Full Name") },
                        placeholder = { Text("e.g. Work Profile, Google Alt") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = newEmail,
                        onValueChange = { newEmail = it },
                        label = { Text("Email Address") },
                        placeholder = { Text("e.g. me@example.com") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = newUsername,
                        onValueChange = { newUsername = it },
                        label = { Text("Username") },
                        placeholder = { Text("e.g. user_work") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = newRole,
                        onValueChange = { newRole = it },
                        label = { Text("Profile Type / Role") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newFullName.isNotBlank() && newEmail.isNotBlank()) {
                            onAddAccount(
                                UserAccountItem(
                                    id = 0,
                                    username = newUsername.ifBlank { newFullName.lowercase().replace(" ", "_") },
                                    email = newEmail.trim(),
                                    fullName = newFullName.trim(),
                                    avatarColorHex = selectedColorHex,
                                    role = newRole.trim(),
                                    isCurrent = false
                                )
                            )
                            newFullName = ""
                            newEmail = ""
                            newUsername = ""
                            showAddDialog = false
                        }
                    }
                ) {
                    Text("Save to Vault")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
