package com.actum.app.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun PriorityBadge(priority: String?) {
    val backgroundColor = when (priority) {
        "URGENT" -> Color(0xFFFFCDD2)
        "NORMAL" -> Color(0xFFDCCAA2)
        "LOW" -> Color(0xFFC8E6C9)
        else -> Color(0xFFE0E0E0)
    }

    val textColor = when (priority) {
        "URGENT" -> Color(0xFFB71C1C)
        "NORMAL" -> Color(0xFFA8753D)
        "LOW" -> Color(0xFF1B5E20)
        else -> Color.Black
    }

    Box(
        modifier = Modifier
            .background(backgroundColor, shape = MaterialTheme.shapes.small)
            .padding(horizontal = 10.dp, vertical = 6.dp)
    ) {
        Text(
            text = priority ?: "-",
            color = textColor,
            style = MaterialTheme.typography.labelMedium
        )
    }
}