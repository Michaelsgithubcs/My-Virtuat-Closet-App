package com.example.mvc.screens.designer

import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.mvc.DatabaseHelper
import com.example.mvc.Designer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

// Updated logout logic in DesignerDashboard
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DesignerDashboard(
    databaseHelper: DatabaseHelper,
    navController: NavController,
    currentDesigner: Designer // Pass the current logged-in designer
) {
    var drawerState = rememberDrawerState(DrawerValue.Closed)
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }
    var isUploading by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // Image picker launcher
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        selectedImageUri = uri
    }

    // Category options
    val categories = listOf(
        "Evening Wear", "Streetwear", "Business Wear", "Casual Wear",
        "Activewear", "Outerwear", "Accessories", "Dresses",
        "Knitwear", "Formal Wear", "Bottoms", "Cover-ups"
    )
    var expandedCategory by remember { mutableStateOf(false) }

    // Function to upload design
    fun uploadDesign() {
        if (title.isBlank() || description.isBlank() || price.isBlank() || category.isBlank()) {
            Toast.makeText(context, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            return
        }

        if (selectedImageUri == null) {
            Toast.makeText(context, "Please select an image", Toast.LENGTH_SHORT).show()
            return
        }

        val priceValue = price.toDoubleOrNull()
        if (priceValue == null || priceValue <= 0) {
            Toast.makeText(context, "Please enter a valid price", Toast.LENGTH_SHORT).show()
            return
        }

        isUploading = true
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Here you would typically upload the image to a server or save it locally
                // For now, we'll simulate the upload process
                val imageFileName = "design_${System.currentTimeMillis()}.jpg"

                // Add design to database (you'll need to create this method in DatabaseHelper)
                val designId = databaseHelper.addDesign(
                    designerId = currentDesigner.id,
                    title = title,
                    description = description,
                    price = priceValue,
                    category = category,
                    imageFileName = imageFileName
                )

                withContext(Dispatchers.Main) {
                    isUploading = false
                    if (designId > 0) {
                        Toast.makeText(context, "Design uploaded successfully!", Toast.LENGTH_SHORT).show()
                        // Clear form
                        title = ""
                        description = ""
                        price = ""
                        category = ""
                        selectedImageUri = null
                    } else {
                        Toast.makeText(context, "Failed to upload design", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    isUploading = false
                    Toast.makeText(context, "Error uploading design: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            DesignerNavigationDrawer(
                currentDesigner = currentDesigner,
                onMenuItemClick = { menuItem ->
                    scope.launch {
                        drawerState.close()
                        try {
                            when (menuItem) {
                                "Profile" -> {
                                    Log.d("Navigation", "Navigating to profile")
                                    navController.navigate("designer-profile/${currentDesigner.id}")
                                }
                                "Transactions" -> {
                                    Log.d("Navigation", "Navigating to transactions")
                                    navController.navigate("designer-transactions/${currentDesigner.id}")
                                }
                                "Logout" -> {
                                    Log.d("Navigation", "Navigating to login")
                                    navController.navigate("login")
                                }
                                // ... other cases
                            }
                        } catch (e: Exception) {
                            Log.e("Navigation", "Error: ${e.message}")
                            Toast.makeText(context, "Navigation error: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                        // Alternative for navigation
                        when (menuItem) {
                            "Profile" -> navController.navigate("designer-profile/${currentDesigner.id}")
                            "Transactions" -> navController.navigate("designer-transactions/${currentDesigner.id}")

                            "Logout" -> {
                                // Navigate back to the main app flow (with bottom navigation)
                                // The main app will default to "home" screen, and user can navigate to designers via bottom nav
                                navController.navigate("login") {
                                    // Clear all back stack entries to prevent going back to dashboard
                                    popUpTo(0) {
                                        inclusive = true
                                    }
                                    // Make sure we don't keep multiple copies
                                    launchSingleTop = true
                                    // Don't restore state
                                    restoreState = false
                                }
                            }
                        }
                    }
                }
            )
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Designer Dashboard") },
                    navigationIcon = {
                        IconButton(
                            onClick = {
                                scope.launch {
                                    drawerState.open()
                                }
                            }
                        ) {
                            Icon(Icons.Default.Menu, contentDescription = "Menu")
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
                // Welcome Section
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 24.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp)
                    ) {
                        Text(
                            text = "Welcome back, ${currentDesigner.name}!",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Share your creativity with the world! Upload your latest fashion designs and connect with fashion enthusiasts. Each design you upload helps build your portfolio and reaches potential customers who appreciate your unique style.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }

                // Upload Form
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp)
                    ) {
                        Text(
                            text = "Upload New Design",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )

                        // Image Upload Section
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                                .clickable { imagePickerLauncher.launch("image/*") },
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant
                            )
                        ) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                if (selectedImageUri != null) {
                                    AsyncImage(
                                        model = selectedImageUri,
                                        contentDescription = "Selected design image",
                                        modifier = Modifier.fillMaxSize(),
                                        contentScale = ContentScale.Crop
                                    )
                                } else {
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Icon(
                                            Icons.Default.AddAPhoto,
                                            contentDescription = "Add photo",
                                            modifier = Modifier.size(48.dp),
                                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text(
                                            text = "Tap to select image",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Title Field
                        OutlinedTextField(
                            value = title,
                            onValueChange = { title = it },
                            label = { Text("Design Title") },
                            placeholder = { Text("Enter design title") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Description Field
                        OutlinedTextField(
                            value = description,
                            onValueChange = { description = it },
                            label = { Text("Description") },
                            placeholder = { Text("Describe your design") },
                            modifier = Modifier.fillMaxWidth(),
                            minLines = 3,
                            maxLines = 5
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            // Price Field
                            OutlinedTextField(
                                value = price,
                                onValueChange = { price = it },
                                label = { Text("Price (R)") },
                                placeholder = { Text("0.00") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                modifier = Modifier.weight(1f),
                                singleLine = true
                            )

                            // Category Dropdown
                            ExposedDropdownMenuBox(
                                expanded = expandedCategory,
                                onExpandedChange = { expandedCategory = !expandedCategory },
                                modifier = Modifier.weight(1f)
                            ) {
                                OutlinedTextField(
                                    value = category,
                                    onValueChange = {},
                                    readOnly = true,
                                    label = { Text("Category") },
                                    trailingIcon = {
                                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedCategory)
                                    },
                                    modifier = Modifier
                                        .menuAnchor()
                                        .fillMaxWidth()
                                )

                                ExposedDropdownMenu(
                                    expanded = expandedCategory,
                                    onDismissRequest = { expandedCategory = false }
                                ) {
                                    categories.forEach { categoryOption ->
                                        DropdownMenuItem(
                                            text = { Text(categoryOption) },
                                            onClick = {
                                                category = categoryOption
                                                expandedCategory = false
                                            }
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        // Upload Button
                        Button(
                            onClick = { uploadDesign() },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            enabled = !isUploading
                        ) {
                            if (isUploading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    color = MaterialTheme.colorScheme.onPrimary
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Uploading...")
                            } else {
                                Icon(Icons.Default.CloudUpload, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Upload Design")
                            }
                        }
                    }
                }
            }
        }
    }
}

// Updated DesignerNavigationDrawer with proper logout navigation
@Composable
fun DesignerNavigationDrawer(
    currentDesigner: Designer,
    onMenuItemClick: (String) -> Unit
) {
    ModalDrawerSheet(
        modifier = Modifier.width(300.dp)
    ) {
        // Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .background(MaterialTheme.colorScheme.primary)
                .padding(16.dp),
            contentAlignment = Alignment.BottomStart
        ) {
            Column {
                // Profile Avatar
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.onPrimary),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = currentDesigner.name.first().toString().toUpperCase(),
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = currentDesigner.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimary
                )

                Text(
                    text = currentDesigner.email,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Menu Items
        val menuItems = listOf(
            Triple("Profile", Icons.Default.Person, "Manage your profile"),
            Triple("Transactions", Icons.Default.AttachMoney, "View your earnings"),

            Triple("Logout", Icons.Default.Logout, "Sign out of your account")
        )

        menuItems.forEach { (title, icon, subtitle) ->
            NavigationDrawerItem(
                label = {
                    Column {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.titleSmall
                        )
                        Text(
                            text = subtitle,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                icon = { Icon(icon, contentDescription = title) },
                selected = false,
                onClick = { onMenuItemClick(title) },
                modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
            )
        }
    }
}
