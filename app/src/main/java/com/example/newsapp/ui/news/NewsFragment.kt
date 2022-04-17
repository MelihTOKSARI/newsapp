package com.example.newsapp.ui.news

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.newsapp.R
import com.example.newsapp.adapter.NewsAdapter
import com.example.newsapp.databinding.FragmentNewsBinding

class NewsFragment : Fragment(), NewsRequester.NewsRequesterResponse {

    private var _binding: FragmentNewsBinding? = null

    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var newsAdapter: NewsAdapter

    private lateinit var newsRequester: NewsRequester
    private var pageIndex: Int = 1

    private val searchHandler: Handler = Handler(Looper.getMainLooper())
    private var searchRunnable: Runnable = Runnable {  }

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        /*
        val newsViewModel =
                ViewModelProvider(this).get(NewsViewModel::class.java)

        */
        _binding = FragmentNewsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        newsRequester = NewsRequester(this)
        linearLayoutManager = LinearLayoutManager(context)
        
        val searchArea: EditText = binding.etNewsSearch
        searchArea.doOnTextChanged { text, _, _, _ ->
            searchHandler.removeCallbacks(searchRunnable)
            searchRunnable = Runnable {
                requestNews(text.toString())
            }
            searchHandler.postDelayed(searchRunnable, 500)
        }

        prepareList()
        /*
        newsViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }
        */
        return root
    }



    private fun prepareList() {
        val newsRecyclerView: RecyclerView = binding.rvNews
        newsRecyclerView.layoutManager = linearLayoutManager

        newsAdapter = NewsAdapter(arrayListOf())

        newsRecyclerView.adapter = newsAdapter
    }

    private fun requestNews(text: String) {
        if(text.isEmpty()) {
            return
        }

        newsRequester.requestNews(text, pageIndex)
    }

    override fun onNewsReceived(news: ArrayList<NewsItemViewModel>) {
        Log.i(LOG_CAT, "[onNewsReceived] news are received size: " + news.size.toString())
        activity?.runOnUiThread {
            if(news.size > 0) {
                binding.tvNewsMessage.visibility = View.GONE
                newsAdapter.updateNewsList(news)
            } else {
                binding.tvNewsMessage.text = getText(R.string.message_news_not_found)
            }
        }
    }

    override fun onRequestingNewsFailed(reason: String) {
        Log.i(LOG_CAT, "[onRequestingNewsFailed] reason $reason")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private val LOG_CAT = NewsFragment::class.java.simpleName
    }
}