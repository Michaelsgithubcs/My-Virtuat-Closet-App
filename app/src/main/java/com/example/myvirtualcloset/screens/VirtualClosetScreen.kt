package com.example.mvc.screens

import TryOnScreen
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.mvc.BottomNavigationBar
import com.example.mvc.CategoryRadioGroup
import com.example.mvc.DatabaseHelper
import com.example.mvc.getScreenByIndex
import com.example.mvc.getTabIndex
import com.example.mvc.screens.designer.DesignersScreen
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VirtualClosetScreen(
    navController: NavController,
    databaseHelper: DatabaseHelper,
    email: String
) {
    val context = LocalContext.current
    val imageDir = context.cacheDir
    var currentScreen by remember { mutableStateOf("home") }
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var tempPhotoUri by remember { mutableStateOf<Uri?>(null) }
    var color by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("Loading...") }
    var isLoading by remember { mutableStateOf(true) }

    //val tryOnViewModel: TryOnViewModel = viewModel()

    // Launcher to take a picture using camera
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            imageUri = tempPhotoUri
        }
    }

    fun createImageUri(): Uri? {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val file = File(imageDir, "JPEG_${timeStamp}.jpg")
        return FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
    }

    LaunchedEffect(email) {
        try {
            val result = withContext(Dispatchers.IO) {
                databaseHelper.getCurrentUser(email)
            }
            username = result?.first ?: "Unknown User"
        } catch (e: Exception) {
            username = "Error loading user"
            Log.e("VirtualClosetScreen", "Error fetching user", e)
        } finally {
            isLoading = false
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("My Virtual Closet") },
                actions = {
                    IconButton(
                        onClick = { navController.navigate("main") },
                        modifier = Modifier.size(48.dp)
                    ) {
                        Icon(Icons.Default.Logout, contentDescription = "Log Out", modifier = Modifier.size(24.dp))
                    }
                }
            )
        },
        bottomBar = {
            BottomNavigationBar(selectedIndex = getTabIndex(currentScreen)) { selectedIndex ->
                currentScreen = getScreenByIndex(selectedIndex)
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.Center
        ) {
            val scrollState = rememberScrollState()
            if (currentScreen == "home") {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Top,
                    modifier = Modifier.padding(16.dp).verticalScroll(scrollState)
                ) {
                    if (isLoading) {
                        CircularProgressIndicator()
                    } else {
                        Text("Welcome back, $username!", style = MaterialTheme.typography.headlineMedium)
                    }
                    Spacer(Modifier.height(24.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth().
                            clickable(
                                onClick = {
                                    val uri = createImageUri()
                                    tempPhotoUri = uri
                                    uri?.let { cameraLauncher.launch(it) }
                                }
                            )
                            .height(300.dp)
                            .background(Color.LightGray),
                        contentAlignment = Alignment.Center
                    ) {
                        if (imageUri != null) {
                            Image(
                                painter = rememberAsyncImagePainter(imageUri),
                                contentDescription = "Captured image",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Text("Click to Capture Clothing Item")
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = color,
                        onValueChange = { color = it },
                        label = { Text("Color") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    val categories = listOf("T-Shirt", "Trouser", "Hoodie", "Shoes")

                    CategoryRadioGroup(
                        categories = categories,
                        selectedCategory = category,
                        onCategorySelected = { category = it }
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    /*Button(
                        onClick = {
                            val uri = createImageUri()
                            tempPhotoUri = uri
                            uri?.let { cameraLauncher.launch(it) }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Take Photo")
                    }*/

                    Spacer(modifier = Modifier.height(8.dp))

                    Button(
                        onClick = {
                            if (imageUri != null && color.isNotEmpty() && category.isNotEmpty()) {
                                CoroutineScope(Dispatchers.IO).launch {
                                    val imagePath = imageUri.toString()
                                    databaseHelper.insertClothingItem(color, category, imagePath)
                                    withContext(Dispatchers.Main) {
                                        imageUri = null
                                        color = ""
                                        category = ""
                                        Toast.makeText(context, "Clothing item saved!", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            } else {
                                Toast.makeText(context, "Please fill all fields and take a photo", Toast.LENGTH_SHORT).show()
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Save Clothing Item")
                    }
                }
            } else if (currentScreen == "closet") {
                ClosetScreen(databaseHelper = databaseHelper, onNavigate = { currentScreen = it }, email)
            }
            else if (currentScreen == "avatar") {
                AvatarScreen(onNavigate = { currentScreen = it })
            }
            else if (currentScreen == "favorites") {
                FavoritesScreen(onNavigate = { currentScreen = it }, databaseHelper, email)
            }
            else if (currentScreen == "settings") {
                SettingsScreen(onNavigate = { currentScreen = it }, databaseHelper = databaseHelper, email, navController)
            }
            else if (currentScreen == "designers") {
                DesignersScreen(databaseHelper = databaseHelper, navController)
            }
            else {
                TryOnScreen(databaseHelper, email,onNavigate = { currentScreen = it })
            }
        }
    }
}