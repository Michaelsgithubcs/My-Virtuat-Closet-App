package com.example.mvc.screens

import android.content.Context
import android.util.Log
import android.util.Patterns
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.*
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.mvc.DatabaseHelper
import com.example.mvc.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun LoginScreen(databaseHelper: DatabaseHelper, navController: NavController) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var rememberMe by remember { mutableStateOf(false) }
    var isPasswordVisible by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    // Load saved credentials if "Remember me" was checked previously
    LaunchedEffect(Unit) {
        val sharedPref = context.getSharedPreferences("login_prefs", Context.MODE_PRIVATE)
        if (sharedPref.getBoolean("remember_me", false)) {
            email = sharedPref.getString("saved_email", "") ?: ""
            password = sharedPref.getString("saved_password", "") ?: ""
            rememberMe = true
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState) // Make the screen scrollable
            .padding(horizontal = 24.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Spacer(modifier = Modifier.height(32.dp))

        // App Logo/Icon
        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = "App Logo",
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 250.dp)
                .padding(bottom = 16.dp)
        )

        // App Title
        Text(
            text = "My Virtual Closet",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Error message if any
        errorMessage?.let {
            Text(
                text = it,
                color = Color.Red,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }

        // Email Field
        OutlinedTextField(
            value = email,
            onValueChange = {
                email = it
                errorMessage = null
            },
            label = { Text("Email") },
            placeholder = { Text("Enter your email") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next
            ),
            leadingIcon = {
                Icon(imageVector = Icons.Default.Email, contentDescription = "Email Icon")
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Password Field
        OutlinedTextField(
            value = password,
            onValueChange = {
                password = it
                errorMessage = null
            },
            label = { Text("Password") },
            placeholder = { Text("Enter your password") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done
            ),
            leadingIcon = {
                Icon(imageVector = Icons.Default.Lock, contentDescription = "Password Icon")
            },
            trailingIcon = {
                IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                    Icon(
                        imageVector = if (isPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                        contentDescription = if (isPasswordVisible) "Hide Password" else "Show Password"
                    )
                }
            },
            visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation()
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Remember Me Checkbox
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = rememberMe,
                onCheckedChange = { rememberMe = it }
            )
            Text(
                text = "Remember me",
                modifier = Modifier.clickable { rememberMe = !rememberMe }
            )

            Spacer(modifier = Modifier.weight(1f))

            // Forgot Password Link
            TextButton(onClick = {


            }) {
                Text("Forgot Password?")
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Login Button
        Button(
            onClick = {
                when {
                    email.isBlank() -> errorMessage = "Email cannot be empty"
                    !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> errorMessage = "Please enter a valid email"
                    password.isBlank() -> errorMessage = "Password cannot be empty"
                    password.length < 6 -> errorMessage = "Password must be at least 6 characters"
                    else -> {
                        isLoading = true
                        errorMessage = null

                        val sharedPref = context.getSharedPreferences("login_prefs", Context.MODE_PRIVATE)
                        if (rememberMe) {
                            with(sharedPref.edit()) {
                                putString("saved_email", email)
                                putString("saved_password", password)
                                putBoolean("remember_me", true)
                                apply()
                            }
                        } else {
                            with(sharedPref.edit()) {
                                clear()
                                apply()
                            }
                        }

                        CoroutineScope(Dispatchers.IO).launch {
                            try {
                                val isValid = databaseHelper.validateUser(email, password)
                                withContext(Dispatchers.Main) {
                                    isLoading = false
                                    if (isValid) {
                                        val userInfo = databaseHelper.getCurrentUser(email)
                                        Toast.makeText(context, "Login successful!", Toast.LENGTH_SHORT).show()
                                        navController.navigate("success/${email}")
                                    } else {
                                        errorMessage = "Invalid email or password"
                                    }
                                }
                            } catch (e: Exception) {
                                withContext(Dispatchers.Main) {
                                    isLoading = false
                                    errorMessage = "Login error: ${e.message}"
                                    Log.e("LoginScreen", "Login error", e)
                                }
                            }
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
                    modifier = Modifier.size(24.dp),
                    color = Color.White
                )
            } else {
                Text("Login")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Sign Up prompt
        Text(
            text = "Don't have an account?",
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(
                onClick = { navController.navigate("signup") },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondary
                )
            ) {
                Text("Sign Up as User")
            }

            Spacer(modifier = Modifier.width(16.dp))

            Button(
                onClick = { navController.navigate("designer-signup") },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.tertiary
                )
            ) {
                Text("Sign Up as Designer")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Continue as Guest
        TextButton(
            onClick = { navController.navigate("main") }
        ) {
            Text("Continue as Guest")
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}
