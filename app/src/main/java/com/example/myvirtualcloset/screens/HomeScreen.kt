package com.example.mvc.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import android.app.Activity
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import com.example.mvc.ScrollableContent

@Composable
fun HomeScreen(navController: NavController, onNavigate: (String) -> Unit) {
    //var selectedTab by remember { mutableStateOf(0) }
    val context = LocalContext.current
    val activity = context as? Activity
    BackHandler(enabled = true) {
        activity?.finishAffinity()//this makes sure you can't go back in on the app by swiping back after you logged out. you have to manually click the sign in button. Sooo this exits the app instead of looping around
    }
    Scaffold() { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            ScrollableContent(navController)
        }
    }
}
