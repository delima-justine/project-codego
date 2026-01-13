package com.example.project_codego

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun UserAvatar(
    name: String,
    modifier: Modifier = Modifier,
    fontSize: TextUnit = 20.sp
) {
    val initial = name.trim().firstOrNull()?.uppercaseChar()?.toString() ?: "?"
    val backgroundColor = getColorForInitial(initial)
    
    Box(
        modifier = modifier
            .clip(CircleShape)
            .background(backgroundColor),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = initial,
            color = Color.White,
            fontSize = fontSize,
            fontWeight = FontWeight.Bold
        )
    }
}

private fun getColorForInitial(initial: String): Color {
    val colors = listOf(
        Color(0xFF1976D2), // Blue
        Color(0xFFD32F2F), // Red
        Color(0xFF388E3C), // Green
        Color(0xFFF57C00), // Orange
        Color(0xFF7B1FA2), // Purple
        Color(0xFF0097A7), // Cyan
        Color(0xFFC2185B), // Pink
        Color(0xFF5D4037), // Brown
        Color(0xFF455A64), // Blue Grey
        Color(0xFF00796B), // Teal
        Color(0xFFFBC02D), // Yellow
        Color(0xFF512DA8), // Deep Purple
        Color(0xFF0288D1), // Light Blue
        Color(0xFF689F38), // Light Green
        Color(0xFFE64A19), // Deep Orange
        Color(0xFF303F9F), // Indigo
    )
    
    val index = initial.firstOrNull()?.code?.rem(colors.size) ?: 0
    return colors[index]
}
