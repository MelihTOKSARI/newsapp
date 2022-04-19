package com.example.newsapp.models

import java.time.LocalDate

// TODO Will be removed after Article model
data class NewsItemViewModel(
    val id: String,
    val author: String,
    val title: String,
    val description: String,
    val url: String,
    val imageUrl: String,
    val publishedAt: LocalDate,
    val content: String
)
