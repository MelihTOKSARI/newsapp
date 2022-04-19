package com.example.newsapp.api

import com.example.newsapp.models.Article
import com.example.newsapp.models.NewsResponse
import com.example.newsapp.utils.Constants
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

class FirebaseRetrofitInstance {

    companion object {
        private val retrofit by lazy {
            val logging = HttpLoggingInterceptor()
            logging.setLevel(HttpLoggingInterceptor.Level.BODY)
            val client = OkHttpClient.Builder()
                .addInterceptor(logging)
                .addInterceptor(ErrorInterceptor())
                .build()
            Retrofit.Builder()
                .baseUrl(Constants.FIREBASE_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build()
        }

        val api by lazy {
            retrofit.create(FirebaseApi::class.java)
        }
    }

    interface FirebaseApi {

        @GET("savedNews.json")
        suspend fun getFavouriteNews(
            @Query("page")
            pageNumber: Int = 1
        ): Response<Map<String, Article>>

        @PUT("savedNews/{id}.json")
        suspend fun saveNews(
            @Path("id") id: String,
            @Body article: Article
        )

        @DELETE("savedNews/{id}.json")
        suspend fun removeNews(
            @Path("id") id: String
        )

    }

}