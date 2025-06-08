package com.example.mvc.screens.designer

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.mvc.DatabaseHelper
import com.example.mvc.Designer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DesignerProfile(
    databaseHelper: DatabaseHelper,
    navController: NavController,
    currentDesigner: Designer
) {
    var isEditMode by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }

    // Form fields
    var name by remember { mutableStateOf(currentDesigner.name) }
    var email by remember { mutableStateOf(currentDesigner.email) }
    var uniqueId by remember { mutableStateOf(currentDesigner.uniqueId) }
    var currentPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var showPasswords by remember { mutableStateOf(false) }
    var isChangingPassword by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // Get designer's designs count
    var designsCount by remember { mutableStateOf(0) }

    LaunchedEffect(currentDesigner.id) {
        withContext(Dispatchers.IO) {
            val designs = databaseHelper.getDesignsByDesigner(currentDesigner.id)
            withContext(Dispatchers.Main) {
                designsCount = designs.size
            }
        }
    }

    fun saveProfile() {
        if (name.isBlank() || email.isBlank() || uniqueId.isBlank()) {
            Toast.makeText(context, "Please fill in all required fields", Toast.LENGTH_SHORT).show()
            return
        }

        if (isChangingPassword) {
            if (currentPassword.isBlank() || newPassword.isBlank() || confirmPassword.isBlank()) {
                Toast.makeText(context, "Please fill in all password fields", Toast.LENGTH_SHORT).show()
                return
            }

            if (currentPassword != currentDesigner.password) {
                Toast.makeText(context, "Current password is incorrect", Toast.LENGTH_SHORT).show()
                return
            }

            if (newPassword != confirmPassword) {
                Toast.makeText(context, "New passwords don't match", Toast.LENGTH_SHORT).show()
                return
            }

            if (newPassword.length < 6) {
                Toast.makeText(context, "New password must be at least 6 characters", Toast.LENGTH_SHORT).show()
                return
            }
        }

        isLoading = true
        scope.launch(Dispatchers.IO) {
            try {
                val result = if (isChangingPassword) {
                    // Update with new password (you'll need to add this method to DatabaseHelper)
                    databaseHelper.updateDesignerWithPassword(
                        currentDesigner.id,
                        name,
                        email,
                        uniqueId,
                        newPassword
                    )
                } else {
                    databaseHelper.updateDesigner(currentDesigner.id, name, email, uniqueId)
                }

                withContext(Dispatchers.Main) {
                    isLoading = false
                    if (result > 0) {
                        Toast.makeText(context, "Profile updated successfully!", Toast.LENGTH_SHORT).show()
                        isEditMode = false
                        isChangingPassword = false
                        currentPassword = ""
                        newPassword = ""
                        confirmPassword = ""

                        // Navigate back to dashboard with updated info
                        navController.navigate("designer-dashboard/${currentDesigner.id}") {
                            popUpTo("designer-profile/${currentDesigner.id}") {
                                inclusive = true
                            }
                        }
                    } else {
                        Toast.makeText(context, "Failed to update profile", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    isLoading = false
                    Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Designer Profile") },
                navigationIcon = {
                    IconButton(
                        onClick = { navController.navigateUp() }
                    ) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (!isEditMode) {
                        IconButton(
                            onClick = { isEditMode = true }
                        ) {
                            Icon(Icons.Default.Edit, contentDescription = "Edit Profile")
                        }
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // Profile Header Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Profile Avatar
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.onPrimaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = currentDesigner.name.first().toString().uppercase(),
                            style = MaterialTheme.typography.headlineLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primaryContainer
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    if (!isEditMode) {
                        Text(
                            text = currentDesigner.name,
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )

                        Text(
                            text = currentDesigner.email,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Designer ID: ${currentDesigner.uniqueId}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.6f)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Stats Card
            if (!isEditMode) {
                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp)
                    ) {
                        Text(
                            text = "Statistics",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = designsCount.toString(),
                                    style = MaterialTheme.typography.headlineMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    text = "Designs",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "0", // You can add more stats like views, likes, etc.
                                    style = MaterialTheme.typography.headlineMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    text = "Sales",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "0", // You can add followers count
                                    style = MaterialTheme.typography.headlineMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    text = "Followers",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
            }

            // Edit Profile Form
            if (isEditMode) {
                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp)
                    ) {
                        Text(
                            text = "Edit Profile",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )

                        // Name Field
                        OutlinedTextField(
                            value = name,
                            onValueChange = { name = it },
                            label = { Text("Full Name") },
                            leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Email Field
                        OutlinedTextField(
                            value = email,
                            onValueChange = { email = it },
                            label = { Text("Email") },
                            leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Unique ID Field
                        OutlinedTextField(
                            value = uniqueId,
                            onValueChange = { uniqueId = it },
                            label = { Text("Designer ID") },
                            leadingIcon = { Icon(Icons.Default.Badge, contentDescription = null) },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        // Change Password Section
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Change Password",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Medium
                            )

                            Switch(
                                checked = isChangingPassword,
                                onCheckedChange = {
                                    isChangingPassword = it
                                    if (!it) {
                                        currentPassword = ""
                                        newPassword = ""
                                        confirmPassword = ""
                                    }
                                }
                            )
                        }

                        if (isChangingPassword) {
                            Spacer(modifier = Modifier.height(16.dp))

                            // Current Password
                            OutlinedTextField(
                                value = currentPassword,
                                onValueChange = { currentPassword = it },
                                label = { Text("Current Password") },
                                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                                trailingIcon = {
                                    IconButton(onClick = { showPasswords = !showPasswords }) {
                                        Icon(
                                            if (showPasswords) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                            contentDescription = if (showPasswords) "Hide password" else "Show password"
                                        )
                                    }
                                },
                                visualTransformation = if (showPasswords) VisualTransformation.None else PasswordVisualTransformation(),
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            // New Password
                            OutlinedTextField(
                                value = newPassword,
                                onValueChange = { newPassword = it },
                                label = { Text("New Password") },
                                leadingIcon = { Icon(Icons.Default.LockOpen, contentDescription = null) },
                                visualTransformation = if (showPasswords) VisualTransformation.None else PasswordVisualTransformation(),
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            // Confirm Password
                            OutlinedTextField(
                                value = confirmPassword,
                                onValueChange = { confirmPassword = it },
                                label = { Text("Confirm New Password") },
                                leadingIcon = { Icon(Icons.Default.LockOpen, contentDescription = null) },
                                visualTransformation = if (showPasswords) VisualTransformation.None else PasswordVisualTransformation(),
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true,
                                isError = confirmPassword.isNotEmpty() && newPassword != confirmPassword
                            )

                            if (confirmPassword.isNotEmpty() && newPassword != confirmPassword) {
                                Text(
                                    text = "Passwords don't match",
                                    color = MaterialTheme.colorScheme.error,
                                    style = MaterialTheme.typography.bodySmall,
                                    modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(32.dp))

                        // Action Buttons
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            // Cancel Button
                            OutlinedButton(
                                onClick = {
                                    isEditMode = false
                                    isChangingPassword = false
                                    name = currentDesigner.name
                                    email = currentDesigner.email
                                    uniqueId = currentDesigner.uniqueId
                                    currentPassword = ""
                                    newPassword = ""
                                    confirmPassword = ""
                                },
                                modifier = Modifier.weight(1f),
                                enabled = !isLoading
                            ) {
                                Text("Cancel")
                            }

                            // Save Button
                            Button(
                                onClick = { saveProfile() },
                                modifier = Modifier.weight(1f),
                                enabled = !isLoading
                            ) {
                                if (isLoading) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(20.dp),
                                        strokeWidth = 2.dp,
                                        color = MaterialTheme.colorScheme.onPrimary
                                    )
                                } else {
                                    Text("Save Changes")
                                }
                            }
                        }
                    }
                }
            }

            // Quick Actions (only in view mode)
            if (!isEditMode) {
                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp)
                    ) {
                        Text(
                            text = "Quick Actions",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )

                        // View My Designs
                        OutlinedButton(
                            onClick = {
                                navController.navigate("designer-dashboard/${currentDesigner.id}")
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.Palette, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Upload new Designs")
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // View Transactions
                        OutlinedButton(
                            onClick = {
                                navController.navigate("designer-transactions/${currentDesigner.id}")
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {

                            Spacer(modifier = Modifier.width(8.dp))
                            Text("View Transactions")
                        }
                    }
                }
            }
        }
    }
}

