package com.example.mvc.cards

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Help
import androidx.compose.material.icons.filled.AccessibilityNew
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Password
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Update
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.mvc.ClothingItem
import com.example.mvc.DatabaseHelper
//mport com.example.mvc.Outfit

import com.example.mvc.screens.SettingsItem


@Composable
fun OutfitCard(
    outfit: DatabaseHelper.Outfit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .padding(8.dp)
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Outfit ID: ${outfit.id}", style = MaterialTheme.typography.titleMedium)
            Text("Season: ${outfit.season}")
            Text("Description: ${outfit.description}")
            Spacer(modifier = Modifier.height(8.dp))
            Text("Clothing Items: ${outfit.clothingItems.size}")
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClothingCard(
    item: ClothingItem,
    userEmail: String,
    databaseHelper: DatabaseHelper,
    onFavoriteChange: () -> Unit,
    onDelete: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    var showFavDialog by remember { mutableStateOf(false) }
    var isFavorite by remember { mutableStateOf(false) }

    // Load favorite state when composed
    LaunchedEffect(item.id, userEmail) {
        isFavorite = databaseHelper.isItemFavorited(item.id, userEmail)
    }

    Card(
        modifier = modifier
            .padding(4.dp)
            .height(180.dp)
            .pointerInput(Unit) {
                detectTapGestures(
                    onLongPress = { showFavDialog = true }
                )
            },
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column {
            Image(
                painter = rememberAsyncImagePainter(Uri.parse(item.imagePath)),
                contentDescription = "Clothing item",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.height(4.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(item.color, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    Text(item.category, fontSize = 10.sp)
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = { onDelete(item.id) }) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete item",
                            tint = Color.Red
                        )
                    }

                    Icon(
                        imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Favorite icon",
                        tint = if (isFavorite) Color.Red else Color.Gray,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }

    // Show confirmation dialog on long press
    if (showFavDialog) {
        AlertDialog(
            onDismissRequest = { showFavDialog = false },
            title = { Text(if (isFavorite) "Remove from Favorites" else "Add to Favorites") },
            text = {
                Text(
                    if (isFavorite)
                        "Do you want to remove this item from your favorites?"
                    else
                        "Do you want to add this item to your favorites?"
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    if (isFavorite) {
                        databaseHelper.removeFromFavorites(item.id, userEmail)
                    } else {
                        databaseHelper.addToFavorites(item.id, userEmail)
                    }
                    isFavorite = !isFavorite
                    showFavDialog = false
                    onFavoriteChange()
                }) {
                    Text(if (isFavorite) "Remove" else "Add")
                }
            },
            dismissButton = {
                TextButton(onClick = { showFavDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}



@Composable
fun DesignCard(index: Int, onDelete: () -> Unit) {
    Card(
        modifier = Modifier
            .padding(4.dp)
            .height(180.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            // Design content placeholder
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.LightGray),
                contentAlignment = Alignment.Center
            ) {
                Text("Design ${index + 1}", fontSize = 12.sp)
            }

            // Delete button
            IconButton(
                onClick = onDelete,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .size(24.dp)
            ) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "Delete design",
                    tint = Color.Red,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

@Composable
fun FeatureCard(title: String, description: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF0F0F0))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(title, fontWeight = FontWeight.Bold)
            Text(description, fontSize = 14.sp, color = Color.Gray)
        }
    }
}
@Composable
fun AddNewCard(onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .padding(4.dp)
            .height(180.dp)
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.LightGray.copy(alpha = 0.2f))
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Default.Add,
                contentDescription = "Add",
                modifier = Modifier.size(32.dp)
            )
        }
    }
}

@Composable
fun AddDesignCard(onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .padding(4.dp)
            .height(180.dp)
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.LightGray.copy(alpha = 0.3f))
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Default.Add,
                contentDescription = "Add design",
                modifier = Modifier.size(32.dp)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesCard(
    item: ClothingItem,
    userEmail: String,
    databaseHelper: DatabaseHelper, // kept in case needed for other UI uses
    onUnfavorite: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showDialog by remember { mutableStateOf(false) }

    Card(
        modifier = modifier
            .padding(4.dp)
            .height(180.dp)
            .pointerInput(Unit) {
                detectTapGestures(onLongPress = { showDialog = true })
            },
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column {
            Image(
                painter = rememberAsyncImagePainter(Uri.parse(item.imagePath)),
                contentDescription = "Favorite clothing item",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp, vertical = 4.dp),
                verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(item.color, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    Text(item.category, fontSize = 10.sp)
                }
                IconButton(
                    onClick = { showDialog = true },
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        Icons.Default.Favorite,
                        contentDescription = "Unfavorite",
                        tint = Color.Red,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Remove from Favorites") },
            text = { Text("Do you want to remove this item from your favorites?") },
            confirmButton = {
                TextButton(onClick = {
                    onUnfavorite() // Let the parent handle DB and UI update
                    showDialog = false
                }) {
                    Text("Remove")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
public fun AccessibilityCard(
    textSize: Float,
    highContrastMode: Boolean,
    reduceAnimations: Boolean,
    onTextSizeChange: (Float) -> Unit,
    onHighContrastModeChange: (Boolean) -> Unit,
    onReduceAnimationsChange: (Boolean) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Text Size Slider
            Text(
                text = "Text Size",
                style = MaterialTheme.typography.bodyLarge
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "A",
                    style = MaterialTheme.typography.bodyMedium
                )

                Slider(
                    value = textSize,
                    onValueChange = onTextSizeChange,
                    valueRange = 0.5f..2f,
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 8.dp)
                )

                Text(
                    text = "A",
                    style = MaterialTheme.typography.titleLarge
                )
            }

            Divider(modifier = Modifier.padding(vertical = 8.dp))

            // High Contrast Mode Toggle
            SettingsItem(
                title = "High Contrast Mode",
                icon = Icons.Default.AccessibilityNew,
                subtitle = if (highContrastMode) "Enabled" else "Disabled",
                endContent = {
                    Switch(
                        checked = highContrastMode,
                        onCheckedChange = onHighContrastModeChange
                    )
                }
            )

            Divider(modifier = Modifier.padding(vertical = 8.dp))

            // Reduce Animations Toggle
            SettingsItem(
                title = "Reduce Animations",
                icon = Icons.Default.AccessibilityNew,
                subtitle = if (reduceAnimations) "Enabled" else "Disabled",
                endContent = {
                    Switch(
                        checked = reduceAnimations,
                        onCheckedChange = onReduceAnimationsChange
                    )
                }
            )
        }
    }
}

@Composable
public fun AccountCard(
    onDeleteAccount: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {

            // Delete Account Option
            SettingsItem(
                title = "Delete Account",
                icon = Icons.Default.Delete,
                subtitle = "Permanently remove your account and data",
                onClick = onDeleteAccount
            )
        }
    }
}

@Composable
public fun HelpSupportCard(
    onFaqClick: () -> Unit,
    onContactSupportClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // FAQ Option
            SettingsItem(
                title = "Frequently Asked Questions",
                icon = Icons.AutoMirrored.Filled.Help,
                subtitle = "Find answers to common questions",
                onClick = onFaqClick
            )

            Divider(modifier = Modifier.padding(vertical = 8.dp))

            // Contact Support Option
            SettingsItem(
                title = "Contact Support",
                icon = Icons.Default.People,
                subtitle = "Get help from our support team",
                onClick = onContactSupportClick
            )
        }
    }
}

@Composable
public fun AboutCard(
    currentVersion: String,
    onPrivacyPolicyClick: () -> Unit,
    onTermsOfServiceClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // App Version
            SettingsItem(
                title = "App Version",
                icon = Icons.Default.Info,
                subtitle = currentVersion
            )

            Divider(modifier = Modifier.padding(vertical = 8.dp))


            // Privacy Policy Link
            TextButton(
                onClick = onPrivacyPolicyClick,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    "Privacy Policy",
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            }

            // Terms of Service Link
            TextButton(
                onClick = onTermsOfServiceClick,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    "Terms of Service",
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}



@Composable
public fun NotificationsCard(
    pushNotificationsEnabled: Boolean,
    onPushNotificationsChange: (Boolean) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Push Notifications Toggle
            SettingsItem(
                title = "Push Notifications",
                icon = Icons.Default.Favorite,
                subtitle = if (pushNotificationsEnabled) "Enabled" else "Disabled",
                endContent = {
                    Switch(
                        checked = pushNotificationsEnabled,
                        onCheckedChange = onPushNotificationsChange
                    )
                }
            )
        }
    }
}


@Composable
public fun SecurityCard(
    onChangePassword: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Change password button
            SettingsItem(
                title = "Change Password",
                icon = Icons.Default.Password,
                subtitle = "Update your account password",
                endContent = {
                    TextButton(onClick = onChangePassword) {
                        Text("Change")
                    }
                }
            )
        }
    }
}