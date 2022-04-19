package com.example.newsapp.models

import androidx.lifecycle.*
import com.example.newsapp.repository.FirebaseRepository
import com.example.newsapp.repository.NewsRepository
import com.example.newsapp.utils.Resource
import kotlinx.coroutines.launch
import retrofit2.Response
import java.lang.Exception

class NewsViewModel(
    private val newsRepository: NewsRepository,
    private val firebaseRepository: FirebaseRepository
) : ViewModel() {

    var searchNews: MutableLiveData<Resource<NewsResponse>> = MutableLiveData()
    private var searchPageNumber = 1
    private var searchNewsResponse: NewsResponse? = null

    var savedArticles: MutableLiveData<Resource<ArrayList<Article>>> = MutableLiveData()

    fun searchNews(query: String) = viewModelScope.launch {
        try {
            searchNews.postValue(Resource.Loading())
            val response = newsRepository.searchNews(query, searchPageNumber)
            searchNews.postValue(handleSearchNewsResponse(response))
        } catch (e: Exception) {
            e.printStackTrace()
            searchNews.postValue(Resource.Error(e.message ?: "Something went wrong!"))
        }
    }

    private fun handleSearchNewsResponse(response: Response<NewsResponse>): Resource<NewsResponse> {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                searchPageNumber++
                if (searchNewsResponse == null) {
                    searchNewsResponse = resultResponse
                } else {
                    val oldArticles = searchNewsResponse?.articles
                    val newArticles = resultResponse.articles
                    oldArticles?.addAll(newArticles)
                }
                return Resource.Success(searchNewsResponse ?: resultResponse)
            }
        }
        return Resource.Error(response.message())
    }

    fun getSavedArticles() = viewModelScope.launch {
        try {
            savedArticles.postValue(Resource.Loading())
            val response = firebaseRepository.getSavedNews(searchPageNumber)

            savedArticles.postValue(handleSavedArticlesResponse(response))
        } catch (e: Exception) {
            e.printStackTrace()
            searchNews.postValue(Resource.Error(e.message ?: "Something went wrong!"))
        }
    }

    private fun handleSavedArticlesResponse(response: Response<Map<String,Article>>): Resource<ArrayList<Article>> {
        if(response.isSuccessful) {
            response.body()?.let { resultResponse ->
                return Resource.Success(ArrayList(resultResponse.values))
            }
        }

        return Resource.Error(response.message())
    }

    fun saveArticle(article: Article) = viewModelScope.launch {
        try {
            firebaseRepository.saveNews(article)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun removeArticle(article: Article) = viewModelScope.launch {
        try {
            firebaseRepository.removeNews(article)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}