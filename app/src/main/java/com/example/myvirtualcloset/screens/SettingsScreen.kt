package com.example.mvc.screens

import android.content.Intent
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Help
import androidx.compose.material.icons.filled.AccessibilityNew
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.mvc.DatabaseHelper
import com.example.mvc.cards.AboutCard

import com.example.mvc.cards.AccountCard
import com.example.mvc.cards.HelpSupportCard
import com.example.mvc.cards.NotificationsCard
import com.example.mvc.cards.SecurityCard
import com.example.mvc.sections.SettingsSectionHeader
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigate: (String) -> Unit,
    databaseHelper: DatabaseHelper,
    userEmail: String,
    navController: NavController
     ) {
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var userId by remember { mutableStateOf<Long?>(null) }

    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf("") }
    val context = LocalContext.current
    var showLogoutDialog by remember { mutableStateOf(false) }
    var showDeleteAccountDialog by remember { mutableStateOf(false) }
    var showPasswordDialog by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    var autoUpdateEnabled by remember { mutableStateOf(false) }
    // Theme settings
    var darkModeEnabled by remember { mutableStateOf(false) }

    // Notification settings
    var notificationsEnabled by remember { mutableStateOf(true) }

    // Accessibility settings
    var textSize by remember { mutableFloatStateOf(1f) }
    var highContrastMode by remember { mutableStateOf(false) }
    var reduceAnimations by remember { mutableStateOf(false) }

    // Update settings
    var currentVersion by remember { mutableStateOf("1.0.0") }
    var isCheckingUpdate by remember { mutableStateOf(false) }

    // Fetch the current user information
    LaunchedEffect(Unit) {
        try {
            val userInfo = withContext(Dispatchers.IO) {
                databaseHelper.getCurrentUser(userEmail)
            }

            userInfo?.let { (name, userEmail) ->
                username = name
                email = userEmail
            } ?: run {
                errorMessage = "User not found"
            }
        } catch (e: Exception) {
            errorMessage = "Failed to load user profile: ${e.message}"
            Log.e("SettingsScreen", "Error loading user", e)
        } finally {
            isLoading = false
        }
    }
    LaunchedEffect(email) {
        if (email.isNotBlank()) {
            val id = withContext(Dispatchers.IO) {
                databaseHelper.getUserIdByEmail(email)
            }
            userId = id
            Log.d("SettingsScreen", "Fetched userId: $id")
            if (id == null) {
                Toast.makeText(context, "User ID could not be found", Toast.LENGTH_SHORT).show()
            }
        }
    }


    if (showLogoutDialog) {
        LogoutDialog(
            onConfirm = {
                showLogoutDialog = false
                onNavigate("main")
            },
            onDismiss = { showLogoutDialog = false }
        )
    }

    if (showDeleteAccountDialog) {
        DeleteAccountDialog(
            onConfirm = {
                showDeleteAccountDialog = false
                coroutineScope.launch {
                    userId?.let { id -> // `id` is a non-null Long
                        val deleted = try {
                            withContext(Dispatchers.IO) {
                                databaseHelper.deleteUserCompletely(id, email) // ✅ Use `id` here
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                            false
                        }

                        if (deleted) {
                            Toast.makeText(context, "Account deleted successfully", Toast.LENGTH_SHORT).show()
                            navController.navigate("main") {
                                popUpTo("settings") { inclusive = true }
                            }
                        } else {
                            Toast.makeText(context, "Failed to delete account", Toast.LENGTH_SHORT).show()
                        }
                    } ?: run {
                        Toast.makeText(context, "User ID not found. Cannot delete account.", Toast.LENGTH_SHORT).show()
                    }
                }
            },
            onDismiss = { showDeleteAccountDialog = false }
        )
    }

    if (showPasswordDialog) {
        ChangePasswordDialog(
            onConfirm = { currentPassword, newPassword ->
                showPasswordDialog = false
                coroutineScope.launch {
                    val success = withContext(Dispatchers.IO) {
                        databaseHelper.changeUserPassword(email, currentPassword, newPassword)
                    }

                    snackbarHostState.showSnackbar(
                        if (success) "Password changed successfully"
                        else "Incorrect current password"
                    )
                }
            },
            onDismiss = { showPasswordDialog = false }
        )
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Settings", maxLines = 1, overflow = TextOverflow.Ellipsis) },
                navigationIcon = {
                    IconButton(onClick = { onNavigate("home") }) {
                        Icon(
                            imageVector = Icons.Default.Home,
                            contentDescription = "Back to home"
                        )
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Loading state
            if (isLoading) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
            } else {
                // User Avatar & Quick Stats
                item {
                    UserProfileHeader(
                        username = username,
                        email = email,
                        onProfileClick = { /* Navigate to detailed profile page */ }
                    )
                }
                // Notification Settings
                item {
                    SettingsSectionHeader(title = "Notifications", icon = Icons.Default.Favorite)
                    NotificationsCard(
                        pushNotificationsEnabled = notificationsEnabled,
                        onPushNotificationsChange = {
                            notificationsEnabled = it
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar(
                                    "Push notifications ${if(it) "enabled" else "disabled"}"
                                )
                            }
                        }
                    )
                }

                // Security Settings
                item {
                    SettingsSectionHeader(title = "Security", icon = Icons.Default.Security)
                    SecurityCard(
                        onChangePassword = { showPasswordDialog = true }
                    )
                }

                // Account Options
                item {
                    SettingsSectionHeader(title = "Account", icon = Icons.Default.Person)
                    AccountCard(
                        onDeleteAccount = { showDeleteAccountDialog = true }
                    )
                }

                // Help & Support
                item {
                    SettingsSectionHeader(title = "Help & Support", icon = Icons.AutoMirrored.Filled.Help)
                    HelpSupportCard(
                        onFaqClick = {
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar("Navigating to FAQ")
                            }
                        },
                        onContactSupportClick = {
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar("Opening support form")
                            }
                        }
                    )
                }

                // About section with Updates
                item {
                    SettingsSectionHeader(title = "About", icon = Icons.Default.Info)
                    AboutCard(
                        currentVersion = currentVersion,
                        onPrivacyPolicyClick = {
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar("Opening privacy policy")
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/Draco-Mlaba/MVC/blob/0dc46afe4fcaa1ef2d77177eb1575452723bdace/privacy_policy.txt"))
                                context.startActivity(intent)
                            }
                        },
                        onTermsOfServiceClick = {
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar("Opening terms of service")
                            }
                        }
                    )
                }

                // Bottom space
                item {
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }
}

@Composable
public fun UserProfileHeader(
    username: String,
    email: String,
    onProfileClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onProfileClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Profile avatar
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.AccountCircle,
                    contentDescription = "Profile",
                    modifier = Modifier.size(48.dp),
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }

            // User info
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 16.dp)
            ) {
                Text(
                    text = username,
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Text(
                    text = email,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                )
            }


        }
    }
}

@Composable
public fun SettingsItem(
    title: String,
    icon: ImageVector,
    subtitle: String? = null,
    onClick: () -> Unit = {},
    endContent: @Composable (() -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.padding(end = 16.dp),
            tint = LocalContentColor.current.copy(alpha = 0.7f)
        )

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge
            )
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = LocalContentColor.current.copy(alpha = 0.7f)
                )
            }
        }

        if (endContent != null) {
            endContent()
        }
    }
}

@Composable
public fun LogoutDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Log Out") },
        text = { Text("Are you sure you want to log out?") },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text("Log Out")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
public fun DeleteAccountDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Delete Account") },
        text = {
            Text(
                "Are you sure you want to delete your account? This action cannot be undone and all your data will be permanently lost."
            )
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error
                )
            ) {
                Text("Delete Account")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
public fun ChangePasswordDialog(
    onConfirm: (String, String) -> Unit,
    onDismiss: () -> Unit
) {
    var currentPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Change Password") },
        text = {
            Column {
                if (errorMessage.isNotEmpty()) {
                    Text(
                        text = errorMessage,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }

                OutlinedTextField(
                    value = currentPassword,
                    onValueChange = { currentPassword = it },
                    label = { Text("Current Password") },
                    modifier = Modifier.fillMaxWidth(),
                    visualTransformation = PasswordVisualTransformation(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = newPassword,
                    onValueChange = { newPassword = it },
                    label = { Text("New Password") },
                    modifier = Modifier.fillMaxWidth(),
                    visualTransformation = PasswordVisualTransformation(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    label = { Text("Confirm New Password") },
                    modifier = Modifier.fillMaxWidth(),
                    visualTransformation = PasswordVisualTransformation(),
                    singleLine = true
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    when {
                        currentPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty() -> {
                            errorMessage = "Please fill in all fields"
                        }
                        newPassword != confirmPassword -> {
                            errorMessage = "New passwords don't match"
                        }
                        newPassword.length < 6 -> {
                            errorMessage = "Password must be at least 6 characters"
                        }
                        else -> {
                            onConfirm(currentPassword, newPassword)
                        }
                    }
                }
            ) {
                Text("Change Password")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

