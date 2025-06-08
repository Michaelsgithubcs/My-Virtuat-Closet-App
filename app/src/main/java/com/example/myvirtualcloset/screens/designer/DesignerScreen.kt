package com.example.mvc.screens.designer

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Login
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavController
import com.example.mvc.DatabaseHelper
import com.example.mvc.Design
import com.example.mvc.Designer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

// Sample data - keeping as fallback/initial data
object SampleData {
    val sampleDesigners = listOf(
        Designer(1, "Sophia Milano", "sophia@milanofashion.com", "password123", "FDS001"),
        Designer(2, "Marcus Thompson", "marcus@streetwearlab.com", "password123", "FDS002"),
        Designer(3, "Isabella Chen", "isabella@elegantcouture.com", "password123", "FDS003"),
        Designer(4, "Diego Rodriguez", "diego@casualchic.com", "password123", "FDS004"),
        Designer(5, "Zara Williams", "zara@bohemianstyle.com", "password123", "FDS005"),
        Designer(6, "Alexander Kim", "alex@luxurywear.com", "password123", "FDS006")
    )

    val sampleDesigns = mapOf(
        1L to listOf(
            Design(
                1,
                "Evening Gown Collection",
                "Elegant silk evening gowns with intricate beadwork",
                899.99,
                "Evening Wear",
                "evening_gown.jpg",
                1
            ),
            Design(
                2,
                "Cocktail Dress Series",
                "Chic cocktail dresses for special occasions",
                449.99,
                "Party Wear",
                "cocktail_dress.jpg",
                1
            ),
            Design(
                3,
                "Formal Blazer Set",
                "Professional women's blazer and trouser set",
                329.99,
                "Business Wear",
                "blazer_set.jpg",
                1
            )
        ),
        2L to listOf(
            Design(
                4,
                "Urban Hoodie Collection",
                "Trendy oversized hoodies with unique graphics",
                89.99,
                "Streetwear",
                "urban_hoodie.jpg",
                2
            ),
            Design(
                5,
                "Graphic T-Shirt Pack",
                "Set of 5 limited edition graphic tees",
                149.99,
                "Casual Wear",
                "graphic_tee.jpg",
                2
            ),
            Design(
                6,
                "Sneaker-Inspired Joggers",
                "Comfortable joggers with sneaker-inspired details",
                79.99,
                "Activewear",
                "joggers.jpg",
                2
            )
        ),
        3L to listOf(
            Design(
                7,
                "Vintage Inspired Coat",
                "Classic wool coat with vintage button details",
                599.99,
                "Outerwear",
                "vintage_coat.jpg",
                3
            ),
            Design(
                8,
                "Silk Scarf Collection",
                "Hand-painted silk scarves with floral patterns",
                129.99,
                "Accessories",
                "silk_scarf.jpg",
                3
            ),
            Design(
                9,
                "Little Black Dress",
                "Timeless LBD perfect for any occasion",
                279.99,
                "Dresses",
                "black_dress.jpg",
                3
            )
        ),
        4L to listOf(
            Design(
                10,
                "Denim Jacket Remix",
                "Modern take on classic denim with unique cuts",
                159.99,
                "Outerwear",
                "denim_jacket.jpg",
                4
            ),
            Design(
                11,
                "Summer Linen Shirt",
                "Breathable linen shirts for summer comfort",
                89.99,
                "Casual Wear",
                "linen_shirt.jpg",
                4
            ),
            Design(
                12,
                "Chino Pants Collection",
                "Versatile chinos in various colors and fits",
                119.99,
                "Bottoms",
                "chino_pants.jpg",
                4
            )
        ),
        5L to listOf(
            Design(
                13,
                "Boho Maxi Dress",
                "Flowing maxi dress with ethnic print patterns",
                199.99,
                "Dresses",
                "boho_dress.jpg",
                5
            ),
            Design(
                14,
                "Fringe Kimono",
                "Lightweight kimono with decorative fringe",
                149.99,
                "Cover-ups",
                "fringe_kimono.jpg",
                5
            ),
            Design(
                15,
                "Artisan Jewelry Set",
                "Handcrafted jewelry with natural stones",
                79.99,
                "Accessories",
                "jewelry_set.jpg",
                5
            )
        ),
        6L to listOf(
            Design(
                16,
                "Cashmere Sweater",
                "Premium cashmere pullover in neutral tones",
                399.99,
                "Knitwear",
                "cashmere_sweater.jpg",
                6
            ),
            Design(
                17,
                "Leather Handbag",
                "Italian leather handbag with gold hardware",
                699.99,
                "Accessories",
                "leather_handbag.jpg",
                6
            ),
            Design(
                18,
                "Tailored Suit",
                "Bespoke men's suit with perfect fit guarantee",
                1299.99,
                "Formal Wear",
                "tailored_suit.jpg",
                6
            )
        )
    )

    // Function to populate database with sample data if empty
    fun initializeSampleData(databaseHelper: DatabaseHelper) {
        CoroutineScope(Dispatchers.IO).launch {
            val existingDesigners = databaseHelper.getAllDesigners()
            if (existingDesigners.isEmpty()) {
                // Add sample designers
                sampleDesigners.forEach { designer ->
                    databaseHelper.insertDesigner(
                        designer.name,
                        designer.email,
                        designer.password,
                        designer.uniqueId
                    )

                    // Add sample designs
                    val insertedDesigners = databaseHelper.getAllDesigners()
                    insertedDesigners.forEach { designer ->
                        val designerSampleDesigns = sampleDesigns[designer.id] ?: emptyList()
                        designerSampleDesigns.forEach { design ->
                            databaseHelper.addDesign(
                                designer.id,
                                design.title,
                                design.description,
                                design.price,
                                design.category,
                                design.imageFile
                            )
                        }
                    }
                }
            }
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DesignersScreen(databaseHelper: DatabaseHelper, navController: NavController) {
    var designers by remember { mutableStateOf<List<Designer>>(emptyList()) }
    var allDesigns by remember { mutableStateOf<List<Design>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var selectedDesigner by remember { mutableStateOf<Designer?>(null) }
    var refreshTrigger by remember { mutableStateOf(0) }
    val context = LocalContext.current

    // Function to load data from database
    fun loadDataFromDatabase() {
        isLoading = true
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Load designers from database
                val dbDesigners = databaseHelper.getAllDesigners()

                // Load all designs from database
                val dbDesigns = databaseHelper.getAllDesigns()

                withContext(Dispatchers.Main) {
                    designers = dbDesigners
                    allDesigns = dbDesigns
                    isLoading = false
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Error loading data: ${e.message}", Toast.LENGTH_LONG).show()
                    isLoading = false
                }
            }
        }
    }

    // Initialize sample data and load from database
    LaunchedEffect(refreshTrigger) {
        SampleData.initializeSampleData(databaseHelper)
        // Small delay to ensure sample data is inserted
        kotlinx.coroutines.delay(500)
        loadDataFromDatabase()
    }

    // Function to handle designer updates
    fun updateDesigner(updatedDesigner: Designer) {
        CoroutineScope(Dispatchers.IO).launch {
            val rowsAffected = databaseHelper.updateDesigner(
                updatedDesigner.id,
                updatedDesigner.name,
                updatedDesigner.email,
                updatedDesigner.uniqueId
            )

            withContext(Dispatchers.Main) {
                if (rowsAffected > 0) {
                    Toast.makeText(context, "Designer updated successfully", Toast.LENGTH_SHORT).show()
                    loadDataFromDatabase()
                } else {
                    Toast.makeText(context, "Failed to update designer", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    // Function to delete designer
    fun deleteDesigner(designer: Designer) {
        CoroutineScope(Dispatchers.IO).launch {
            val rowsAffected = databaseHelper.deleteDesigner(designer.id)
            withContext(Dispatchers.Main) {
                if (rowsAffected > 0) {
                    Toast.makeText(context, "Designer deleted successfully", Toast.LENGTH_SHORT).show()
                    loadDataFromDatabase()
                } else {
                    Toast.makeText(context, "Failed to delete designer", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    if (selectedDesigner != null) {
        // Show designer's designs
        DesignerDetailsScreen(
            designer = selectedDesigner!!,
            designs = allDesigns.filter { it.designerId == selectedDesigner!!.id },
            onBack = { selectedDesigner = null },
            onRefresh = { loadDataFromDatabase() }
        )
    } else {
        // Show designers list
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Designers") },
                    actions = {
                        // Refresh button
                        IconButton(onClick = {
                            refreshTrigger += 1
                            loadDataFromDatabase()
                        }) {
                            Icon(Icons.Default.Refresh, contentDescription = "Refresh")
                        }
                        // Designer login
                        IconButton(onClick = { navController.navigate("designer-login") }) {
                            Icon(Icons.Default.Login, contentDescription = "Designer Login")
                        }
                        // Add designer
                        IconButton(onClick = { navController.navigate("designer-signup") }) {
                            Icon(Icons.Default.Add, contentDescription = "Add Designer")
                        }
                    }
                )
            }
        ) { padding ->
            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator()
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Loading designers and designs...")
                    }
                }
            } else if (designers.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "No designers found",
                            style = MaterialTheme.typography.headlineSmall
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Add some designers to get started",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = { navController.navigate("designer-signup") }) {
                            Text("Add Designer")
                        }
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.padding(padding),
                    contentPadding = PaddingValues(vertical = 8.dp)
                ) {
                    items(designers) { designer ->
                        DesignerListItem(
                            designer = designer,
                            designCount = allDesigns.count { it.designerId == designer.id },
                            onClick = { selectedDesigner = designer },
                            onDelete = { deleteDesigner(designer) },
                            onUpdate = { updatedDesigner ->
                                updateDesigner(updatedDesigner)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun DesignerListItem(
    designer: Designer,
    designCount: Int,
    onClick: () -> Unit,
    onDelete: () -> Unit,
    onUpdate: (Designer) -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = designer.name,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = designer.email,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "ID: ${designer.uniqueId}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Options menu
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Design count with better styling
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Text(
                    text = "$designCount fashion pieces available",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DesignerDetailsScreen(
    designer: Designer,
    designs: List<Design>,
    onBack: () -> Unit,
    onRefresh: () -> Unit
) {
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("${designer.name}'s Collection")
                        Text(
                            text = "${designs.size} designs",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = onRefresh) {
                        Icon(Icons.Default.Refresh, contentDescription = "Refresh")
                    }
                }
            )
        }
    ) { padding ->
        if (designs.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "No designs yet",
                        style = MaterialTheme.typography.headlineSmall
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "${designer.name} hasn't added any designs yet",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.padding(padding)
            ) {
                items(designs) { design ->
                    DesignCard(
                        design = design,
                        onBuy = {
                            Toast.makeText(context, "Added ${design.title} to cart", Toast.LENGTH_SHORT).show()
                        },
                        onView = {
                            // This will be handled by the DesignCard itself now
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun DesignCard(
    design: Design,
    onBuy: () -> Unit,
    onView: () -> Unit
) {
    var showPreview by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            // Design image placeholder
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { showPreview = true },
                contentAlignment = Alignment.Center
            ) {
                Card(
                    modifier = Modifier.fillMaxSize(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "Fashion Preview",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = design.imageFile,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = design.title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                maxLines = 2
            )

            Text(
                text = design.category,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary
            )

            Text(
                text = design.description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2
            )

            Spacer(modifier = Modifier.weight(1f))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "R${String.format("%.2f", design.price)}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                Row {
                    IconButton(
                        onClick = { showPreview = true },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            Icons.Default.Visibility,
                            contentDescription = "View",
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    IconButton(
                        onClick = onBuy,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            Icons.Default.ShoppingCart,
                            contentDescription = "Buy",
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }

    // Preview Dialog
    if (showPreview) {
        DesignPreviewDialog(
            design = design,
            onDismiss = { showPreview = false }
        )
    }
}

@Composable
fun DesignPreviewDialog(
    design: Design,
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true,
            usePlatformDefaultWidth = false
        )
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .fillMaxHeight(0.8f),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(20.dp)
                ) {
                    // Header with close button
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Fashion Preview",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                        )

                        IconButton(
                            onClick = onDismiss,
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                Icons.Default.Close,
                                contentDescription = "Close",
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Blurred image preview
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(250.dp),
                        shape = RoundedCornerShape(12.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    brush = Brush.linearGradient(
                                        colors = listOf(
                                            MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
                                            MaterialTheme.colorScheme.secondary.copy(alpha = 0.3f),
                                            MaterialTheme.colorScheme.tertiary.copy(alpha = 0.3f)
                                        )
                                    )
                                )
                                .blur(radius = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    Icons.Default.Visibility,
                                    contentDescription = null,
                                    modifier = Modifier.size(48.dp),
                                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Blurred Preview",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                                    textAlign = TextAlign.Center
                                )
                                Text(
                                    text = design.imageFile,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Design details
                    Text(
                        text = design.title,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = design.category,
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = design.description,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = MaterialTheme.typography.bodyLarge.lineHeight
                    )

                    Spacer(modifier = Modifier.weight(1f))

                    // Price and action buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "R${String.format("%.2f", design.price)}",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )

                        Button(
                            onClick = {
                                // Handle purchase action
                                onDismiss()
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary
                            )
                        ) {
                            Icon(
                                Icons.Default.ShoppingCart,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Add to Cart")
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Note about full preview
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "💡 Full high-resolution images available after purchase",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(12.dp),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}