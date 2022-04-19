package com.example.newsapp.ui.favourites

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.navOptions
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.newsapp.MainActivity
import com.example.newsapp.R
import com.example.newsapp.adapter.NewsAdapter
import com.example.newsapp.models.NewsViewModel
import com.example.newsapp.utils.Resource
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_news.*

class FavouritesFragment : Fragment(R.layout.fragment_favourites) {

    private lateinit var newsView: View
    private lateinit var newsAdapter: NewsAdapter
    private lateinit var newsViewModel: NewsViewModel

    private var isLoading = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        newsView = view
    }

    override fun onStart() {
        super.onStart()

        initViews()
    }

    private fun hideProgressBar() {
        pb_news.visibility = View.INVISIBLE
        isLoading = false
    }

    private fun initializeNewsRecyclerView() {
        newsAdapter = NewsAdapter()
        rv_news.apply {
            adapter = newsAdapter
            layoutManager = LinearLayoutManager(context)
        }

        rv_news.adapter = newsAdapter
    }

    private fun initViews() {
        newsViewModel = (activity as MainActivity).newsViewModel

        initializeNewsRecyclerView()
        newsAdapter.setOnItemClickListener {
            val bundle = Bundle().apply {
                putSerializable("article", it)
            }

            findNavController().navigate(
                R.id.action_savedNewsFragment_to_articleFragment,
                bundle,
                navOptions { // Use the Kotlin DSL for building NavOptions
                    anim {
                        enter = android.R.animator.fade_in
                        exit = android.R.animator.fade_out
                    }
                }
            )
        }

        newsViewModel.getSavedArticles()

        newsViewModel.savedArticles.observe(viewLifecycleOwner) { response ->
            Log.i("FavouritesFragment", "observe articles")
            when (response) {
                is Resource.Success -> {
                    hideProgressBar()
                    response.data?.let { articles ->
                        newsAdapter.updateNewsList(articles)
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

        val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(
            0,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val article = newsAdapter.newsList[position]
                newsViewModel.removeArticle(article)
                Snackbar.make(newsView, getText(R.string.message_article_removed), Snackbar.LENGTH_LONG).apply {
                    setAction("Undo") {
                        newsViewModel.saveArticle(article)
                    }
                    show()
                }
            }
        }

        ItemTouchHelper(itemTouchHelperCallback).apply {
            attachToRecyclerView(rv_news)
        }
    }

    private fun showSnackBarMessage(messageId: Int) {
        Snackbar.make(newsView, getText(messageId), Snackbar.LENGTH_SHORT).show()
    }

    private fun showProgressBar() {
        pb_news.visibility = View.VISIBLE
        isLoading = true
    }

}