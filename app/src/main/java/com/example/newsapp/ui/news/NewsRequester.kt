package com.example.newsapp.ui.news

import android.content.Context
import android.net.Uri
import androidx.fragment.app.Fragment
import com.example.newsapp.R
import com.example.newsapp.utils.Utils
import okhttp3.*
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class NewsRequester(listener: Fragment) {

    interface NewsRequesterResponse {
        fun onNewsReceived(news: ArrayList<NewsItemViewModel>)
        fun onRequestingNewsFailed(reason: String)
    }

    private val calendar: Calendar = Calendar.getInstance()
    private val client: OkHttpClient = OkHttpClient()
    private val context: Context? = listener.context
    private val dateFormat: SimpleDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
    private val responseListener: NewsRequesterResponse = listener as NewsRequesterResponse

    fun requestNews(searchKey: String, pageIndex: Int = 1, pageSize: Int = REQUEST_DEFAULT_PAGE_SIZE_VALUE) {
        val date = dateFormat.format(calendar.time)

        val urlRequest = Uri.Builder().scheme(URL_SCHEME)
            .authority(URL_AUTHORITY)
            .appendPath(URL_PATH_1)
            .appendPath(URL_PATH_2)
            .appendQueryParameter(URL_QUERY_PARAM_SEARCH_KEY, searchKey)
            .appendQueryParameter(URL_QUERY_PARAM_DATE_KEY, date)
            .appendQueryParameter(URL_QUERY_PARAM_SIZE_KEY, pageSize.toString())
            .appendQueryParameter(URL_QUERY_PARAM_PAGE_KEY, pageIndex.toString())
            .appendQueryParameter(URL_QUERY_PARAM_API_KEY, context?.getString(R.string.news_api_key))
            .build().toString()

        val request = Request.Builder().url(urlRequest).build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                try {
                    val newsListJSON = JSONObject(response.body()!!.string())
                    if(newsListJSON.getString(RESPONSE_STATUS_KEY) != RESPONSE_STATUS_OK_VALUE) {
                        responseListener.onRequestingNewsFailed("It is not available right now!")
                    }

                    val newsItems: ArrayList<NewsItemViewModel> = arrayListOf()
                    val articles = newsListJSON.getJSONArray("articles")
                    for (i in 0 until articles.length()) {
                        val article = articles.getJSONObject(i)

                        newsItems.add(
                            NewsItemViewModel(
                                article.getJSONObject("source").getString("id"),
                                article.getString("author"),
                                article.getString("title"),
                                article.getString("description"),
                                article.getString("url"),
                                article.getString("urlToImage"),
                                // LocalDate.parse(article.getString("publishedAt")),
                                Utils.getLocalDateTime(article.getString("publishedAt")),
                                article.getString("content")
                            )
                        )
                    }

                    responseListener.onNewsReceived(newsItems)
                } catch (e: JSONException) {
                    e.printStackTrace()
                }

            }
        })
    }

    companion object {
        private const val URL_SCHEME = "https"
        private const val URL_AUTHORITY = "newsapi.org"
        private const val URL_PATH_1 = "v2"
        private const val URL_PATH_2 = "everything"
        private const val URL_QUERY_PARAM_SEARCH_KEY = "q"
        private const val URL_QUERY_PARAM_DATE_KEY = "date"
        private const val URL_QUERY_PARAM_API_KEY = "apiKey"
        private const val URL_QUERY_PARAM_SIZE_KEY = "pageSize"
        private const val URL_QUERY_PARAM_PAGE_KEY = "page"
        private const val RESPONSE_STATUS_KEY = "status"
        private const val RESPONSE_STATUS_OK_VALUE = "ok"
        private const val REQUEST_DEFAULT_PAGE_SIZE_VALUE = 10
    }
}