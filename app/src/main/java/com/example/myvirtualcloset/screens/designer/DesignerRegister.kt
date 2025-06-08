package com.example.mvc.screens.designer

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.VpnKey
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.mvc.DatabaseHelper
import com.example.mvc.InputField
import kotlinx.coroutines.launch
import java.util.regex.Pattern

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DesignerRegister(
    databaseHelper: DatabaseHelper,
    navController: NavController
) {
    // State variables for input fields
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var uniqueId by remember { mutableStateOf("") }

    // State variables for input errors
    var nameError by remember { mutableStateOf<String?>(null) }
    var emailError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }
    var uniqueIdError by remember { mutableStateOf<String?>(null) }

    // State variable for password visibility
    var passwordVisible by remember { mutableStateOf(false) }

    // Loading state
    var isLoading by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val scrollState = rememberScrollState()

    // Email pattern for validation
    val emailPattern = Pattern.compile(
        "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"
    )

    // Scaffold for better UI structure
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Designer Registration") },

                )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // App Logo or Icon could go here
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Create Your Designer Account",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Name Input
            InputField(
                value = name,
                onValueChange = {
                    name = it
                    nameError = null
                },
                label = "Full Name",
                errorMessage = nameError,
                icon = Icons.Filled.Person,
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Next
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Email Input
            InputField(
                value = email,
                onValueChange = {
                    email = it
                    emailError = null
                },
                label = "Email Address",
                errorMessage = emailError,
                icon = Icons.Filled.Email,
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Password Input with visibility toggle
            OutlinedTextField(
                value = password,
                onValueChange = {
                    password = it
                    passwordError = null
                },
                label = { Text("Password") },
                isError = passwordError != null,
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = { Icon(Icons.Filled.Lock, contentDescription = "Password") },
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            imageVector = if (passwordVisible) Icons.Outlined.VisibilityOff else Icons.Outlined.Visibility,
                            contentDescription = if (passwordVisible) "Hide password" else "Show password"
                        )
                    }
                },
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Next
                ),
                singleLine = true
            )
            if (passwordError != null) {
                Text(
                    text = passwordError!!,

                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, top = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Unique ID Input
            InputField(
                value = uniqueId,
                onValueChange = {
                    uniqueId = it
                    uniqueIdError = null
                },
                label = "Unique Designer ID",
                errorMessage = uniqueIdError,
                icon = Icons.Filled.VpnKey,
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Done
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Register Button
            Button(
                onClick = {
                    // Validate inputs
                    var isValid = true

                    if (name.trim().isEmpty()) {
                        nameError = "Please enter your name"
                        isValid = false
                    }

                    if (email.trim().isEmpty()) {
                        emailError = "Email cannot be empty"
                        isValid = false
                    } else if (!emailPattern.matcher(email).matches()) {
                        emailError = "Please enter a valid email address"
                        isValid = false
                    }

                    if (password.trim().isEmpty()) {
                        passwordError = "Password cannot be empty"
                        isValid = false
                    } else if (password.length < 6) {
                        passwordError = "Password must be at least 6 characters"
                        isValid = false
                    }

                    if (uniqueId.trim().isEmpty()) {
                        uniqueIdError = "Please enter your unique designer ID"
                        isValid = false
                    }

                    if (isValid) {
                        isLoading = true
                        scope.launch {
                            try {
                                val result = databaseHelper.insertDesigner(name.trim(), email.trim(), password, uniqueId.trim())
                                isLoading = false

                                if (result != -1L) {
                                    // Success
                                    navController.navigate("designer-login") {
                                        // Pop up to login so the user can't go back to registration
                                        popUpTo("Successfully logged-in") { inclusive = true }
                                    }
                                } else {
                                    // Show appropriate error message
                                    emailError = "Email or Designer ID may already be registered"
                                }
                            } catch (e: Exception) {
                                isLoading = false
                                // Handle specific exceptions better here
                                emailError = "Registration failed: ${e.message}"
                            }
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(

                        modifier = Modifier.size(24.dp)
                    )
                } else {
                    Text("REGISTER", fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Navigate to login
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Already have an account? ",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                TextButton(
                    onClick = { navController.navigate("designer-login") }
                ) {
                    Text(
                        text = "Sign-in",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
