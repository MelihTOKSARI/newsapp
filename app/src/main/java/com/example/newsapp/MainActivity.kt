package com.example.newsapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.newsapp.repository.FirebaseRepository
import com.example.newsapp.repository.NewsRepository
import com.example.newsapp.models.NewsViewModel
import com.example.newsapp.models.NewsViewModelProviderFactory
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    lateinit var newsViewModel: NewsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val firebaseRepository = FirebaseRepository()
        val newsRepository = NewsRepository()

        val viewModelProviderFactory = NewsViewModelProviderFactory(newsRepository, firebaseRepository)
        newsViewModel = ViewModelProvider(this, viewModelProviderFactory)[NewsViewModel::class.java]

        nav_view.setupWithNavController(nav_host_fragment_activity_main.findNavController())
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    companion object {
        private val LOG_TAG = MainActivity::class.java.simpleName
    }

}