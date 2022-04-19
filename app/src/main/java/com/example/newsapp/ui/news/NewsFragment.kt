package com.example.newsapp.ui.news

import android.app.Activity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.AbsListView
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.navOptions
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.newsapp.MainActivity
import com.example.newsapp.R
import com.example.newsapp.adapter.NewsAdapter
import com.example.newsapp.repository.NewsRequester
import com.example.newsapp.models.NewsItemViewModel
import com.example.newsapp.models.NewsViewModel
import com.example.newsapp.utils.Constants
import com.example.newsapp.utils.Resource
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_news.*
import java.lang.Exception

class NewsFragment : Fragment(R.layout.fragment_news), NewsRequester.NewsRequesterResponse {

    private lateinit var newsView: View
    private lateinit var newsAdapter: NewsAdapter
    private lateinit var newsViewModel: NewsViewModel

    private lateinit var newsRequester: NewsRequester

    private val searchHandler: Handler = Handler(Looper.getMainLooper())
    private var searchRunnable: Runnable = Runnable {  }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        newsView = view
    }

    override fun onStart() {
        super.onStart()

        initViews()
    }

    private fun initViews() {
        newsViewModel = (activity as MainActivity).newsViewModel

        initializeNewsRecyclerView()
        newsAdapter.setOnItemClickListener {
            val bundle = Bundle().apply {
                putSerializable("article", it)
            }

            findNavController().navigate(
                R.id.action_newsFragment_to_articleFragment,
                bundle,
                navOptions {
                    anim {
                        enter = android.R.animator.fade_in
                        exit = android.R.animator.fade_out
                    }
                }
            )
        }
        // throw RuntimeException("Test Crash")
        et_news_search.doOnTextChanged { text, _, _, _ ->
            searchHandler.removeCallbacks(searchRunnable)
            searchRunnable = Runnable {
                // requestNews(text.toString())
                if(text.toString().isNotEmpty()) {
                    hideMessage()
                    try {
                        newsViewModel.searchNews(text.toString())
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                } else {
                    newsAdapter.clearNewsList()
                    showMessage(R.string.message_search_news)
                }
            }
            searchHandler.postDelayed(searchRunnable, 500)
        }

        // newsRequester = NewsRequester(this)

        newsViewModel.searchNews.observe(viewLifecycleOwner) { response ->
            when (response) {
                is Resource.Success -> {
                    hideKeyboard()
                    hideMessage()
                    hideProgressBar()
                    response.data?.let { newsResponse ->
                        newsAdapter.updateNewsList(newsResponse.articles)
                    }
                }
                is Resource.Error -> {
                    hideProgressBar()
                    response.message?.let { _ ->
                        showSnackBarMessage(R.string.error_search_news)
                    }
                }
                is Resource.Loading -> {
                    showProgressBar()
                }
            }
        }
    }

    private fun hideKeyboard() {
        val inputMethodManager = context?.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(newsView.windowToken, 0)
    }

    private fun hideMessage() {
        tv_news_message.visibility = View.GONE
    }

    private fun hideProgressBar() {
        pb_news.visibility = View.INVISIBLE
        isLoading = false
    }

    private fun showMessage(messageId: Int) {
        tv_news_message.text = getText(messageId)
        tv_news_message.visibility = View.VISIBLE
    }

    private fun showSnackBarMessage(messageId: Int) {
        Snackbar.make(newsView, getText(messageId), Snackbar.LENGTH_SHORT).show()
    }

    private fun showProgressBar() {
        pb_news.visibility = View.VISIBLE
        isLoading = true
    }

    private fun initializeNewsRecyclerView() {
        newsAdapter = NewsAdapter()
        rv_news.apply {
            adapter = newsAdapter
            layoutManager = LinearLayoutManager(context)
            addOnScrollListener(this@NewsFragment.scrollListener)
        }

        rv_news.adapter = newsAdapter
    }

    var isLoading = false
    var isLastPage = false
    var isScrolling = false

    private var scrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)

            if(newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                isScrolling = true
                hideKeyboard()
            }
        }

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)

            if(shouldRequestNews(recyclerView)) {
                isScrolling = false
            }
        }

        private fun shouldRequestNews(recyclerView: RecyclerView): Boolean {
            val layoutManager = recyclerView.layoutManager as LinearLayoutManager
            val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
            val visibleItemCount = layoutManager.childCount
            val totalItemCount = layoutManager.itemCount

            return (!isLoading
                    && !isLastPage
                    && (firstVisibleItemPosition + visibleItemCount >= totalItemCount)
                    && firstVisibleItemPosition >= 0
                    && totalItemCount >= Constants.NEWS_PAGE_SIZE
            )
        }
    }

    /*
    override fun onNewsReceived(news: ArrayList<NewsItemViewModel>) {
        activity?.runOnUiThread {
            if(news.size > 0) {
                binding.tvNewsMessage.visibility = View.GONE
                newsAdapter.updateNewsList(news)
            } else {
                binding.tvNewsMessage.text = getText(R.string.message_news_not_found)
            }
        }
    }

    override fun onRequestingNewsFailed(reason: String) { }
    */

    @Suppress("unused")
    private fun requestNews(text: String) {
        if(text.isEmpty()) {
            return
        }

        newsRequester.requestNews(text)
    }

    override fun onNewsReceived(news: ArrayList<NewsItemViewModel>) {
        TODO("Not yet implemented")
    }

    override fun onRequestingNewsFailed(reason: String) {
        TODO("Not yet implemented")
    }

}