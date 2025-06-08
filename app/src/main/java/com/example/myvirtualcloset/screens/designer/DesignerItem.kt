package com.example.mvc.screens.designer

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.mvc.Designer

@Composable
fun DesignerItem(designer: Designer, onDelete: () -> Unit, onUpdate: (Designer) -> Unit) {
    var isEditing by remember { mutableStateOf(false) }
    var updatedName by remember { mutableStateOf(designer.name) }
    var updatedEmail by remember { mutableStateOf(designer.email) }
    var updatedUniqueId by remember { mutableStateOf(designer.uniqueId) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            if (isEditing) {
                // Edit mode
                OutlinedTextField(
                    value = updatedName,
                    onValueChange = { updatedName = it },
                    label = { Text("Name") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = updatedEmail,
                    onValueChange = { updatedEmail = it },
                    label = { Text("Email") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = updatedUniqueId,
                    onValueChange = { updatedUniqueId = it },
                    label = { Text("Unique ID") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Button(
                        onClick = {
                            isEditing = false
                            updatedName = designer.name
                            updatedEmail = designer.email
                            updatedUniqueId = designer.uniqueId
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Gray)
                    ) {
                        Text("Cancel")
                    }

                    Button(
                        onClick = {
                            isEditing = false
                            onUpdate(
                                designer.copy(
                                    name = updatedName,
                                    email = updatedEmail,
                                    uniqueId = updatedUniqueId
                                )
                            )
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Blue)
                    ) {
                        Text("Save")
                    }
                }
            } else {
                // View mode
                Text(text = designer.name, fontWeight = FontWeight.Bold)
                Text(text = designer.email)
                Text(text = "ID: ${designer.uniqueId}")
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Button(
                        onClick = { isEditing = true },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Blue)
                    ) {
                        Text("Edit")
                    }

                    Button(
                        onClick = onDelete,
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                    ) {
                        Text("Delete")
                    }
                }
            }
        }
    }
}
