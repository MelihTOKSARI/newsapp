package com.example.newsapp.models

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.newsapp.repository.FirebaseRepository
import com.example.newsapp.repository.NewsRepository

class NewsViewModelProviderFactory(
    private val newsRepository: NewsRepository,
    private val firebaseRepository: FirebaseRepository
): ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return NewsViewModel(newsRepository, firebaseRepository) as T
    }

}