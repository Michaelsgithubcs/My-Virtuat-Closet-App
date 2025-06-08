package com.example.mvc.screens

import android.widget.Toast
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.mvc.ClothingItem
import com.example.mvc.DatabaseHelper
import com.example.mvc.cards.AddNewCard
import com.example.mvc.cards.ClothingCard
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun ClosetScreen(
    databaseHelper: DatabaseHelper,
    onNavigate: (String) -> Unit,
    userEmail: String
) {
    var clothingItems by remember { mutableStateOf<List<ClothingItem>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    val context = LocalContext.current

    fun loadClothingItems() {
        CoroutineScope(Dispatchers.IO).launch {
            val items = databaseHelper.getAllClothingItems()
            withContext(Dispatchers.Main) {
                clothingItems = items
                isLoading = false
            }
        }
    }

    fun deleteClothingItem(id: Long) {
        CoroutineScope(Dispatchers.IO).launch {
            databaseHelper.deleteClothingItem(id)
            withContext(Dispatchers.Main) {
                Toast.makeText(context, "Item deleted", Toast.LENGTH_SHORT).show()
                loadClothingItems()
            }
        }
    }

    LaunchedEffect(Unit) {
        loadClothingItems()
    }

    LazyVerticalGrid(
        columns = GridCells.Fixed(4),
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    )
    {
        // Clothing Items Section
        item(span = { GridItemSpan(4) }) {
            Text("My Clothing Items", style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(bottom = 8.dp))

        }

        if (isLoading) {
            item(span = { GridItemSpan(4) }) {
                CircularProgressIndicator()
            }
        } else {
            items(clothingItems) { item ->
                ClothingCard(
                    item = item,
                    userEmail = userEmail,
                    databaseHelper = databaseHelper,
                    onFavoriteChange = {
                        Toast.makeText(context, "Favorite updated", Toast.LENGTH_SHORT).show()
                    },
                    onDelete = { id -> deleteClothingItem(id) }
                )
            }

            // Add New Clothing Card
            item {
                AddNewCard(onClick = { onNavigate("home") })
            }
        }
    }
}
