package com.example.mvc.screens

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.mvc.ClothingItem
import com.example.mvc.DatabaseHelper
import com.example.mvc.cards.FavoritesCard
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesScreen(
    onNavigate: (String) -> Unit,
    databaseHelper: DatabaseHelper,
    userEmail: String
) {
    val context = LocalContext.current
    var favorites by remember { mutableStateOf<List<ClothingItem>>(emptyList()) }
    var favoriteOutfits by remember { mutableStateOf<List<DatabaseHelper.Outfit>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    val coroutineScope = rememberCoroutineScope()

    fun loadFavorites() {
        isLoading = true
        coroutineScope.launch(Dispatchers.IO) {
            val favoriteClothingItems = databaseHelper.getFavoriteItems(userEmail)
            val favoriteOutfitPairs = databaseHelper.getFavoriteOutfits(userEmail)

            Log.d("FavoritesScreen", "Clothing: ${favoriteClothingItems.size}, Outfits: ${favoriteOutfitPairs.size}")

            withContext(Dispatchers.Main) {
                favorites = favoriteClothingItems
                favoriteOutfits = favoriteOutfitPairs.map { it.first }
                isLoading = false
            }
        }
    }

    fun handleUnfavorite(itemId: Long) {
        coroutineScope.launch(Dispatchers.IO) {
            val rowsDeleted = databaseHelper.removeFromFavorites(itemId, userEmail)
            withContext(Dispatchers.Main) {
                if (rowsDeleted > 0) {
                    Toast.makeText(context, "Removed from favorites", Toast.LENGTH_SHORT).show()
                    loadFavorites()
                } else {
                    Toast.makeText(context, "Failed to remove item", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        loadFavorites()
    }

    Scaffold(

    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
                .fillMaxSize()
        ) {
            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                if (favorites.isEmpty() && favoriteOutfits.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("No favorites added yet.")
                    }
                } else {
                    // Favorite Clothing
                    if (favorites.isNotEmpty()) {
                        Text(
                            "Favourite Clothing",
                            style = MaterialTheme.typography.headlineSmall,
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(2),
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.fillMaxWidth().heightIn(max = 400.dp)
                        ) {
                            items(favorites) { item ->
                                FavoritesCard(
                                    item = item,
                                    userEmail = userEmail,
                                    databaseHelper = databaseHelper,
                                    onUnfavorite = {
                                        handleUnfavorite(item.id)
                                    },
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }
                    }

                    // Favorite Outfits (display same as TryOnScreen)
                    if (favoriteOutfits.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(24.dp))
                        Text(
                            text = "Favourite Outfits",
                            style = MaterialTheme.typography.headlineSmall,
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Column(
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            favoriteOutfits.forEach { outfit ->
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
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
                                            outfit.clothingItems.joinToString { "${it.color} ${it.category}" },
                                            style = MaterialTheme.typography.bodyLarge
                                        )

                                        Row(
                                            Modifier
                                                .fillMaxWidth()
                                                .horizontalScroll(rememberScrollState()),
                                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                                        ) {
                                            outfit.clothingItems.forEach { item ->
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
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Browse through your favourite clothes",
                        modifier = Modifier.align(Alignment.CenterHorizontally),
                        fontSize = 16.sp
                    )
                }
            }
        }
    }
}