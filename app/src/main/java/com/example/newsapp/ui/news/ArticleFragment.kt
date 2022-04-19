package com.example.newsapp.ui.news

import android.os.Bundle
import android.view.View
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.example.newsapp.MainActivity
import com.example.newsapp.R
import com.example.newsapp.models.Article
import com.example.newsapp.models.NewsViewModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_article.*

class ArticleFragment: Fragment(R.layout.fragment_article) {

    private val args: ArticleFragmentArgs by navArgs()

    private lateinit var rootView: View
    private lateinit var viewModel: NewsViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        rootView = view
    }

    override fun onStart() {
        super.onStart()

        viewModel = (activity as MainActivity).newsViewModel

        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val article = args.article
        webView.apply {
            webViewClient = WebViewClient()
            loadUrl(article.url)
        }

        changeFabIcon(article.isSaved != null && article.isSaved!!)

        fab.setOnClickListener{
            val isSaved = article.isSaved != null && article.isSaved!!

            article.isSaved = !isSaved
            changeFabIcon(!isSaved)

            if(isSaved) {
                removeArticle(article)
            } else {
                saveArticle(article)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(false)
    }

    private fun changeFabIcon(isSaved: Boolean) {
        if(isSaved) {
            fab.setImageResource(R.drawable.ic_delete)
        } else {
            fab.setImageResource(R.drawable.ic_favourite)
        }
    }

    private fun saveArticle(article: Article) {
        if(article.source.id == null) {
            article.source.id = java.util.UUID.randomUUID().toString()
        }

        viewModel.saveArticle(article)
        Snackbar.make(rootView, getText(R.string.message_article_saved), Snackbar.LENGTH_SHORT).show()
    }

    private fun removeArticle(article: Article) {
        viewModel.removeArticle(article)
        Snackbar.make(rootView, getText(R.string.message_article_removed), Snackbar.LENGTH_SHORT).show()
    }

}