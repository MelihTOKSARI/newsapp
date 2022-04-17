package com.example.newsapp.ui.news

import java.time.LocalDate

data class NewsItemViewModel(val id: String, val author: String, val title: String, val description: String, val url: String, val imageUrl: String, val publishedAt: LocalDate, val content: String)
