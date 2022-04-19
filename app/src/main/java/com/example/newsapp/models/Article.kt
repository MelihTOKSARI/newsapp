package com.example.newsapp.models

import java.io.Serializable

data class Article(
    val author: String?,
    val content: String,
    val description: String?,
    var isSaved: Boolean?,
    val publishedAt: String?,
    val source: Source,
    val title: String,
    val url: String,
    val urlToImage: String?
) : Serializable