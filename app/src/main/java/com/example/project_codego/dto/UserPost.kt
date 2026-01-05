package com.example.project_codego.dto

data class UserPost(
    val id: String = "",
    val userId: String = "", // Simulates who posted it
    val authorName: String = "",
    val content: String = "",
    val category: String = "General", // e.g., "Help Needed", "Safety", "News"
    val timestamp: Long = System.currentTimeMillis()
)
