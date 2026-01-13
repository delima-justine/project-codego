package com.example.project_codego.dto

data class UserPost(
    val id: String = "",
    val userId: String = "",
    val authorName: String = "",
    val content: String = "",
    val category: String = "General",
    val timestamp: Long = System.currentTimeMillis(),
    val likes: List<String> = emptyList(),
    val comments: List<Comment> = emptyList()
)

data class Comment(
    val id: String = "",
    val userId: String = "",
    val userName: String = "",
    val text: String = "",
    val timestamp: Long = System.currentTimeMillis()
)
