package com.actum.app.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun RoleScreen(
    onSpecialistClick: () -> Unit,
    onManagerClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "Actum")

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = onSpecialistClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Я специалист")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onManagerClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Я менеджер")
        }
    }
}