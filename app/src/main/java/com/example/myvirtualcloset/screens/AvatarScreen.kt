package com.example.mvc.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mvc.R

@Composable
fun AvatarScreen(onNavigate: (String) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("My Avatar", style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(bottom = 8.dp))

        // 🧍 Display the 3D avatar image
        Image(
            painter = painterResource(id = R.drawable.avatar_3),
            contentDescription = "3D Avatar",
            contentScale = ContentScale.Crop,
            modifier = Modifier

                .fillMaxSize()
        )

        Spacer(modifier = Modifier.height(30.dp))



    }
}
