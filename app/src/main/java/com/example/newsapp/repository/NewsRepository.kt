package com.example.newsapp.repository

import com.example.newsapp.api.NewsRetrofitInstance

class NewsRepository {
    suspend fun searchNews(query: String, pageNumber: Int = 1) =
        NewsRetrofitInstance.api.searchForNews(query, pageNumber)
}