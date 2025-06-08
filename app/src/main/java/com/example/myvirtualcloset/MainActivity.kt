package com.example.mvc

import Register
import TryOnScreen
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Accessibility
import androidx.compose.material.icons.filled.Checkroom
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.mvc.ui.theme.MVCTheme
import androidx.compose.foundation.layout.width
import androidx.compose.material3.RadioButton
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.material3.MaterialTheme

import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.example.mvc.screens.AvatarScreen
import com.example.mvc.screens.ClosetScreen
import com.example.mvc.screens.FavoritesScreen
import com.example.mvc.screens.HomeScreen
import com.example.mvc.screens.LoginScreen
import com.example.mvc.screens.SettingsScreen
import com.example.mvc.screens.VirtualClosetScreen
import com.example.mvc.screens.designer.DesignerDashboard
import com.example.mvc.screens.designer.DesignerLoginScreen
import com.example.mvc.screens.designer.DesignerProfile
import com.example.mvc.screens.designer.DesignerRegister
import com.example.mvc.screens.designer.DesignerTransactions
import com.example.mvc.screens.designer.DesignersScreen
import com.example.mvc.sections.CTASection
import com.example.mvc.sections.FeaturesSection
import com.example.mvc.sections.HeroSection
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MainActivity : ComponentActivity() {
    private lateinit var databaseHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        databaseHelper = DatabaseHelper(this)
        try {
            // Test database operations
            databaseHelper.writableDatabase
        } catch (e: Exception) {
            Log.e("MainActivity", "Database initialization failed", e)
            Toast.makeText(this, "Database error occurred", Toast.LENGTH_LONG).show()
            finish()
            return
        }
        setContent {
            MVCTheme {
                NavigationComponent(databaseHelper = databaseHelper)
            }
        }
    }
}

@Composable
fun NavigationComponent(databaseHelper: DatabaseHelper) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "main") {
        composable("login") {
            LoginScreen(databaseHelper, navController)
        }
        composable("signup") {
            Register(databaseHelper, navController)
        }
        composable("designer-signup") {
            DesignerRegister(databaseHelper, navController)
        }
        composable("designer-login") {
            DesignerLoginScreen(databaseHelper, navController)
        }
        composable("success/{email}") { backStackEntry ->
            val email = backStackEntry.arguments?.getString("email") ?: ""
            VirtualClosetScreen(navController, databaseHelper, email = email) // Pass email here
        }
        composable("main") {backStackEntry ->
            val email = backStackEntry.arguments?.getString("email") ?: ""
            MyApp(navController, databaseHelper,email)
        }
        composable("register") {
            Register(databaseHelper, navController)
        }

        // Main designers screen with bottom navigation
        composable("designers") {
            DesignersScreen(databaseHelper, navController)
        }


        // Designer dashboard with designer ID parameter
        // Designer dashboard with better error handling
        composable(
            route = "designer-dashboard/{designerId}",
            arguments = listOf(navArgument("designerId") { type = NavType.IntType })
        ) { backStackEntry ->
            val designerId = backStackEntry.arguments?.getInt("designerId") ?: -1

            if (designerId == -1) {
                // Invalid designer ID, redirect to login
                LaunchedEffect(Unit) {
                    navController.navigate("login") {
                        popUpTo(0) { inclusive = true }
                    }
                }
                return@composable
            }

            var currentDesigner by remember { mutableStateOf<Designer?>(null) }
            var isLoading by remember { mutableStateOf(true) }
            var hasError by remember { mutableStateOf(false) }

            LaunchedEffect(designerId) {
                withContext(Dispatchers.IO) {
                    try {
                        val designer = databaseHelper.getDesignerById(designerId)
                        withContext(Dispatchers.Main) {
                            currentDesigner = designer
                            isLoading = false
                            hasError = designer == null
                        }
                    } catch (e: Exception) {
                        withContext(Dispatchers.Main) {
                            isLoading = false
                            hasError = true
                        }
                    }
                }
            }

            when {
                isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
                hasError -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Error loading designer data")
                            Button(
                                onClick = {
                                    navController.navigate("login") {
                                        popUpTo(0) { inclusive = true }
                                    }
                                }
                            ) {
                                Text("Back to Login")
                            }
                        }
                    }
                }
                currentDesigner != null -> {
                    DesignerDashboard(
                        databaseHelper = databaseHelper,
                        navController = navController,
                        currentDesigner = currentDesigner!!
                    )
                }
            }
        }

        // Other designer screens that also need designer info
        composable(
            route = "designer-profile/{designerId}",
            arguments = listOf(navArgument("designerId") { type = NavType.IntType })
        ) { backStackEntry ->
            val designerId = backStackEntry.arguments?.getInt("designerId") ?: -1

            var currentDesigner by remember { mutableStateOf<Designer?>(null) }

            LaunchedEffect(designerId) {
                if (designerId != -1) {
                    withContext(Dispatchers.IO) {
                        val designer = databaseHelper.getDesignerById(designerId)
                        withContext(Dispatchers.Main) {
                            currentDesigner = designer
                        }
                    }
                }
            }

            currentDesigner?.let { designer ->
                DesignerProfile(
                    databaseHelper = databaseHelper,
                    navController = navController,
                    currentDesigner = designer
                )
            } ?: run {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        }

        composable(
            route = "designer-transactions/{designerId}",
            arguments = listOf(navArgument("designerId") { type = NavType.IntType })
        ) { backStackEntry ->
            val designerId = backStackEntry.arguments?.getInt("designerId") ?: -1

            var currentDesigner by remember { mutableStateOf<Designer?>(null) }

            LaunchedEffect(designerId) {
                if (designerId != -1) {
                    withContext(Dispatchers.IO) {
                        val designer = databaseHelper.getDesignerById(designerId)
                        withContext(Dispatchers.Main) {
                            currentDesigner = designer
                        }
                    }
                }
            }

            currentDesigner?.let { designer ->
                DesignerTransactions(
                    databaseHelper = databaseHelper,
                    navController = navController,
                    currentDesigner = designer
                )
            } ?: run {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        }

    }
}
@Composable
fun CategoryRadioGroup(
    categories: List<String>,
    selectedCategory: String,
    onCategorySelected: (String) -> Unit
) {
    Column {
        categories.forEach { category ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onCategorySelected(category) }
                    .padding(8.dp)
            ) {
                RadioButton(
                    selected = (category == selectedCategory),
                    onClick = { onCategorySelected(category) }
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = category)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyApp(
    navController: NavController,
    databaseHelper: DatabaseHelper,
    email: String
) {
    var currentScreen by remember { mutableStateOf("home") }

    Scaffold(
        topBar = { TopBar(navController = navController) }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            when (currentScreen) {
                "home" -> HomeScreen(navController, onNavigate = { currentScreen = it })
                "try-on" -> TryOnScreen(databaseHelper, email, onNavigate = { currentScreen = it })
                "avatar" -> AvatarScreen(onNavigate = { currentScreen = it })
                "closet" -> ClosetScreen(databaseHelper, onNavigate = { currentScreen = it }, email)
                "favorites" -> FavoritesScreen(
                    onNavigate = { currentScreen = it },
                    databaseHelper = databaseHelper,
                    userEmail = email
                )
                "designers" -> DesignersScreen(databaseHelper, navController)
                "settings" -> SettingsScreen(onNavigate = { currentScreen = it }, databaseHelper, email, navController)
            }
        }
    }
}

fun getTabIndex(screen: String): Int {
    return when (screen) {
        "home" -> 0
        "try-on" -> 1
        "avatar" -> 2
        "closet" -> 3
        "favorites" -> 4
        "designers" -> 5
        "settings" -> 6
        else -> 0
    }
}
fun getScreenByIndex(index: Int): String {
    return when (index) {
        0 -> "home"
        1 -> "try-on"
        2 -> "avatar"
        3 -> "closet"
        4 -> "favorites"
        5 -> "designers"
        6 -> "settings"
        else -> "home"
    }
}

@ExperimentalMaterial3Api
@Composable
fun TopBar(navController: NavController) {
    TopAppBar(
        title = { Text("My Virtual Closet", fontWeight = FontWeight.Bold) },
        actions = {
            TextButton(
                onClick = { navController.navigate("login") },
                Modifier.clickable { navController.navigate("login") }
            ) {
                Text("Sign In")
            }
        },
        colors = TopAppBarDefaults.mediumTopAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
    )
}

@Composable
fun BottomNavigationBar(selectedIndex: Int, onTabSelected: (Int) -> Unit) {
    NavigationBar {
        val items = listOf(
            "Home",
            "Try-on",
            "Avatar",
            "Closet",
            "Favorites",
            "Designers",
            "Settings"
        )

        items.forEachIndexed { index, label ->
            val icon = when (label) {
                "Home" -> Icons.Default.Home
                "Try-on" -> Icons.Default.ThumbUp
                "Avatar" -> Icons.Default.Accessibility
                "Closet" -> Icons.Default.Checkroom
                "Favorites" -> Icons.Default.Favorite
                "Designers" -> Icons.Default.Person
                "Settings" -> Icons.Default.Settings
                else -> Icons.Default.Settings
            }

            NavigationBarItem(
                selected = index == selectedIndex,
                onClick = { onTabSelected(index) },
                icon = { Icon(icon, contentDescription = label) },
                label = { Text(label) }
            )
        }
    }
}

@Composable
fun ScrollableContent(navController: NavController) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        item {
            HeroSection(navController)
        }
        item {
            FeaturesSection()
        }
        item {
            CTASection(navController)
        }
    }
}

@Composable
fun InputField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    errorMessage: String?,
    icon: ImageVector,
    keyboardType: KeyboardType,
    imeAction: ImeAction
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        isError = errorMessage != null,
        modifier = Modifier.fillMaxWidth(),
        leadingIcon = { Icon(icon, contentDescription = label) },
        keyboardOptions = KeyboardOptions(
            keyboardType = keyboardType,
            imeAction = imeAction
        ),
        singleLine = true
    )
    if (errorMessage != null) {
        Text(
            text = errorMessage,
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, top = 4.dp)
        )
    }
}