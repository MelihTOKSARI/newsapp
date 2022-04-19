package com.example.newsapp.models

data class NewsResponse(
    val articles: ArrayList<Article>,
    val status: String,
    val totalResults: Int
)
