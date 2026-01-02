package com.example.project_codego

data class NewsResponse(
    val totalArticles: Int,
    val articles: List<Article>
)

data class Article(
    val title: String,
    val description: String?,
    val content: String?,
    val url: String,
    val image: String?,
    val publishedAt: String,
    val source: Source
)

data class Source(
    val name: String,
    val url: String
)
