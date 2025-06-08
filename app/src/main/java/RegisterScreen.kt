import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.mvc.DatabaseHelper
import kotlinx.coroutines.delay

/**
 * A form to register a new user with input validation and improved UX
 *
 * @param databaseHelper Helper to interact with the database
 * @param navController Controller to navigate between screens
 */
@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun Register(databaseHelper: DatabaseHelper, navController: NavController) {
    // Form state
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    // Validation state
    var usernameError by remember { mutableStateOf<String?>(null) }
    var emailError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }
    var confirmPasswordError by remember { mutableStateOf<String?>(null) }

    // UI state
    var isPasswordVisible by remember { mutableStateOf(false) }
    var isConfirmPasswordVisible by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var showSuccessMessage by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val scrollState = rememberScrollState()

    // Success message effect
    if (showSuccessMessage) {
        LaunchedEffect(key1 = true) {
            delay(1500) // Show success message for 1.5 seconds
            navController.navigate("login")
        }
    }

    // Validation functions
    fun validateUsername(): Boolean {
        return if (username.isEmpty()) {
            usernameError = "Username cannot be empty"
            false
        } else if (username.length < 3) {
            usernameError = "Username must be at least 3 characters long"
            false
        } else {
            usernameError = null
            true
        }
    }

    fun validateEmail(): Boolean {
        val emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"
        return if (email.isEmpty()) {
            emailError = "Email cannot be empty"
            false
        } else if (!email.matches(emailPattern.toRegex())) {
            emailError = "Please enter a valid email address"
            false
        } else {
            emailError = null
            true
        }
    }

    fun validatePassword(): Boolean {
        return if (password.isEmpty()) {
            passwordError = "Password cannot be empty"
            false
        } else if (password.length < 6) {
            passwordError = "Password must be at least 6 characters long"
            false
        } else {
            passwordError = null
            true
        }
    }

    fun validateConfirmPassword(): Boolean {
        return if (confirmPassword.isEmpty()) {
            confirmPasswordError = "Please confirm your password"
            false
        } else if (confirmPassword != password) {
            confirmPasswordError = "Passwords don't match"
            false
        } else {
            confirmPasswordError = null
            true
        }
    }

    fun validateForm(): Boolean {
        val isUsernameValid = validateUsername()
        val isEmailValid = validateEmail()
        val isPasswordValid = validatePassword()
        val isConfirmPasswordValid = validateConfirmPassword()

        return isUsernameValid && isEmailValid && isPasswordValid && isConfirmPasswordValid
    }

    fun handleRegistration() {
        if (validateForm()) {
            isLoading = true
            // Simulate network delay
            Thread {
                val result = databaseHelper.insertUser(username, email, password)
                // Update UI on main thread
                (context as? android.app.Activity)?.runOnUiThread {
                    isLoading = false
                    if (result != -1L) {
                        showSuccessMessage = true
                        Toast.makeText(context, "Registration successful!", Toast.LENGTH_SHORT)
                            .show()
                    } else {
                        Toast.makeText(
                            context,
                            "Registration failed. Username or email may already exist.",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }.start()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Create Account",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Username field
                OutlinedTextField(
                    value = username,
                    onValueChange = {
                        username = it
                        if (usernameError != null) validateUsername()
                    },
                    label = { Text("Username") },
                    leadingIcon = { Icon(Icons.Default.Person, contentDescription = "Username") },
                    isError = usernameError != null,
                    supportingText = { usernameError?.let { Text(it) } },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.None,
                        imeAction = ImeAction.Next
                    ),
                    keyboardActions = KeyboardActions(
                        onNext = { focusManager.moveFocus(FocusDirection.Down) }
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Email field
                OutlinedTextField(
                    value = email,
                    onValueChange = {
                        email = it
                        if (emailError != null) validateEmail()
                    },
                    label = { Text("Email") },
                    leadingIcon = { Icon(Icons.Default.Email, contentDescription = "Email") },
                    isError = emailError != null,
                    supportingText = { emailError?.let { Text(it) } },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Email,
                        imeAction = ImeAction.Next
                    ),
                    keyboardActions = KeyboardActions(
                        onNext = { focusManager.moveFocus(FocusDirection.Down) }
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Password field
                OutlinedTextField(
                    value = password,
                    onValueChange = {
                        password = it
                        if (passwordError != null) validatePassword()
                        // Revalidate confirm password when password changes
                        if (confirmPassword.isNotEmpty()) validateConfirmPassword()
                    },
                    label = { Text("Password") },
                    trailingIcon = {
                        IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                            Icon(
                                imageVector = if (isPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                contentDescription = if (isPasswordVisible) "Hide password" else "Show password"
                            )
                        }
                    },
                    visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    isError = passwordError != null,
                    supportingText = { passwordError?.let { Text(it) } },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Next
                    ),
                    keyboardActions = KeyboardActions(
                        onNext = { focusManager.moveFocus(FocusDirection.Down) }
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Confirm Password field
                OutlinedTextField(
                    value = confirmPassword,
                    onValueChange = {
                        confirmPassword = it
                        if (confirmPasswordError != null) validateConfirmPassword()
                    },
                    label = { Text("Confirm Password") },
                    trailingIcon = {
                        IconButton(onClick = {
                            isConfirmPasswordVisible = !isConfirmPasswordVisible
                        }) {
                            Icon(
                                imageVector = if (isConfirmPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                contentDescription = if (isConfirmPasswordVisible) "Hide password" else "Show password"
                            )
                        }
                    },
                    visualTransformation = if (isConfirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    isError = confirmPasswordError != null,
                    supportingText = { confirmPasswordError?.let { Text(it) } },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            keyboardController?.hide()
                            handleRegistration()
                        }
                    )
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Register button
                Button(
                    onClick = { handleRegistration() },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isLoading
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.padding(end = 8.dp),
                            strokeWidth = 2.dp,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                    Text("Sign Up")
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Login link
                TextButton(
                    onClick = { navController.navigate("login") }
                ) {
                    Text("Already have an account? Login")
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Designer section
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Designer Account",
                    style = MaterialTheme.typography.titleMedium
                )

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = { navController.navigate("designer-signup") },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Register as Designer")
                }
            }
        }
    }
}