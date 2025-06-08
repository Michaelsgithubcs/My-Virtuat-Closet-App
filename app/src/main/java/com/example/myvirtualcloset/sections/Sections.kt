package com.example.mvc.sections

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.mvc.cards.FeatureCard

@Composable
fun HeroSection(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Discover Your Style", fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Text(
            "Organize your wardrobe, try on outfits, and get recommendations based on your style and the weather.",
            fontSize = 14.sp,
            color = Color.Gray
        )
        Spacer(modifier = Modifier.height(10.dp))
        Button(onClick = { navController.navigate("register") }) {
            Text("Get Started")
        }
    }
}

@Composable
fun FeaturesSection() {
    Column(modifier = Modifier.padding(vertical = 16.dp)) {
        Text("Key Features", fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(10.dp))
        val features = listOf(
            "Virtual Try-On" to "Try clothes on your avatar before buying.",
            "Smart Recommendations" to "Get outfit suggestions based on your style and weather.",
            "Digital Closet" to "Upload and organize your clothing items.",
            "Fashion Community" to "Connect with designers and fashion CLOTHINGs.",
            "Style Calendar" to "Plan your outfits ahead of time.",
            "Favorites Collection" to "Save your favorite outfits."
        )
        features.forEach { (title, description) ->
            FeatureCard(title, description)
        }
    }
}

@Composable
fun CTASection(navController: NavController) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .background(Color(0xFFE3F2FD), RoundedCornerShape(10.dp)),
        contentAlignment = Alignment.Center
    ) {
        Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Ready to Transform Your Wardrobe?", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Text("Join My Virtual Closet and build your perfect digital closet today.", fontSize = 14.sp, color = Color.Gray)
            Spacer(modifier = Modifier.height(10.dp))
            Button(onClick = { navController.navigate("register") }) {
                Text("Create Account")
            }
        }
    }
}

@Composable
fun SettingsSectionHeader(title: String, icon: ImageVector) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary
        )
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
    }
}
