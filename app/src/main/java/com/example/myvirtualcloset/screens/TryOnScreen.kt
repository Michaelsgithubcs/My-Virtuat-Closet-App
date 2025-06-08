import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import android.widget.Toast
import coil.compose.rememberAsyncImagePainter
import com.example.mvc.ClothingItem
import com.example.mvc.DatabaseHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.random.Random

@Composable
fun TryOnScreen(
    databaseHelper: DatabaseHelper,
    userEmail: String, // <- Added parameter to pass actual email
    season: String = "summer",
    onNavigate: (String) -> Unit = {}
) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    var isLoading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }

    var outfits by remember { mutableStateOf<List<List<ClothingItem>>>(emptyList()) }
    var selectedOutfitIndex by remember { mutableStateOf<Int?>(null) }
    var showSaveDialog by remember { mutableStateOf(false) }
    var showAddToFavoritesDialog by remember { mutableStateOf(false) }

    val required = if (season.equals("winter", true))
        listOf("Hoodie", "Trouser", "Shoes")
    else
        listOf("T-Shirt", "Trouser", "Shoes")

    var recommendation by remember {
        mutableStateOf("No outfits found for the season.")
    }

    LaunchedEffect(season) {
        isLoading = true
        error = null
        try {
            val allItems = withContext(Dispatchers.IO) {
                databaseHelper.getAllClothingItems()
            }

            val groupedByCategory = allItems.groupBy { it.category.lowercase() }
            val categoryItemsLists = required.map { cat ->
                groupedByCategory[cat.lowercase()] ?: emptyList()
            }

            if (categoryItemsLists.any { it.isEmpty() }) {
                recommendation = "You need at least one of each: ${required.joinToString()}"
                outfits = emptyList()
            } else {
                val maxRandomOutfits = 20
                val generatedOutfits = mutableSetOf<List<ClothingItem>>()
                val attemptsLimit = 1000
                var attempts = 0

                while (generatedOutfits.size < maxRandomOutfits && attempts < attemptsLimit) {
                    val randomOutfit = categoryItemsLists.map { items ->
                        items.random()
                    }

                    if (generatedOutfits.none { existing ->
                            existing.map { it.id }.toSet() == randomOutfit.map { it.id }.toSet()
                        }) {
                        generatedOutfits.add(randomOutfit)
                    }
                    attempts++
                }

                outfits = generatedOutfits.toList()
                recommendation = "Randomly showing ${outfits.size} unique outfit(s)."
            }
        } catch (e: Exception) {
            error = "Error loading items: ${e.message}"
            outfits = emptyList()
        } finally {
            isLoading = false
        }
    }

    Column(
        Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        if (isLoading) {
            CircularProgressIndicator()
        }

        error?.let {
            Text(it, color = MaterialTheme.colorScheme.error)
        }

        Text(recommendation, style = MaterialTheme.typography.bodyLarge)

        if (outfits.isNotEmpty()) {
            outfits.forEachIndexed { index, outfit ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .pointerInput(Unit) {
                            detectTapGestures(
                                onLongPress = {
                                    selectedOutfitIndex = index
                                    showAddToFavoritesDialog = true
                                }
                            )
                        },
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    elevation = CardDefaults.cardElevation(6.dp)
                ) {
                    Column(
                        Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            outfit.joinToString { "${it.color} ${it.category}" },
                            style = MaterialTheme.typography.bodyLarge
                        )

                        Row(
                            Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            outfit.forEach { item ->
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Image(
                                        painter = rememberAsyncImagePainter(item.imagePath),
                                        contentDescription = "${item.category} image",
                                        modifier = Modifier
                                            .size(100.dp)
                                            .aspectRatio(1f)
                                    )
                                    Text(
                                        text = "${item.color} ${item.category}",
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        if (showAddToFavoritesDialog && selectedOutfitIndex != null) {
            AlertDialog(
                onDismissRequest = { showAddToFavoritesDialog = false },
                title = { Text("Add to Favorites?") },
                text = {
                    Text(
                        "Do you want to add this outfit to your favorites?\n" +
                                outfits[selectedOutfitIndex!!].joinToString { "${it.color} ${it.category}" }
                    )
                },
                confirmButton = {
                    TextButton(onClick = {
                        showAddToFavoritesDialog = false
                        scope.launch {
                            val outfitToFavorite = outfits[selectedOutfitIndex!!]
                            val outfitId = withContext(Dispatchers.IO) {
                                databaseHelper.insertOutfit(
                                    season = season,
                                    description = outfitToFavorite.joinToString { "${it.color} ${it.category}" },
                                    clothingItemIds = outfitToFavorite.map { it.id }
                                )
                            }
                            if (outfitId != -1L) {
                                databaseHelper.addFavoriteOutfit(outfitId, userEmail) // Fixed: use actual email
                                Toast.makeText(context, "Outfit added to favorites!", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(context, "Failed to add outfit.", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }) {
                        Text("Add")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showAddToFavoritesDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}