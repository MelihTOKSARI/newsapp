package com.example.newsapp.repository

import com.example.newsapp.api.FirebaseRetrofitInstance.Companion.api
import com.example.newsapp.models.Article

class FirebaseRepository {

    suspend fun getSavedNews(pageNumber: Int = 1) =
        api.getFavouriteNews(pageNumber)

    suspend fun saveNews(article: Article) =
        article.source.id?.let { api.saveNews(it, article) }

    suspend fun removeNews(article: Article) =
        article.source.id?.let { api.removeNews(it) }
}