package com.example.newsapp.adapter

import android.annotation.SuppressLint
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.newsapp.R
import com.example.newsapp.ui.news.NewsItemViewModel
import com.squareup.picasso.Picasso

class NewsAdapter(private var newsList: ArrayList<NewsItemViewModel>) :
    RecyclerView.Adapter<NewsAdapter.NewsHolder>() {

    @SuppressLint("NotifyDataSetChanged")
    fun updateNewsList(newsList: ArrayList<NewsItemViewModel>) {
        this.newsList.clear()
        this.newsList.addAll(newsList)
        this.notifyDataSetChanged()
    }

    @Suppress("unused")
    fun clearNewsList() {
        this.newsList.clear()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.card_news_item, parent, false)

        return NewsHolder(view)
    }

    override fun onBindViewHolder(holder: NewsHolder, position: Int) {
        val newsItemViewModel = newsList[position]

        holder.titleView.text = newsItemViewModel.title
        holder.descriptionView.text = newsItemViewModel.description
        Picasso.get().load(Uri.parse(newsItemViewModel.imageUrl)).into(holder.imageView)
    }

    override fun getItemCount(): Int {
        return newsList.size
    }

    class NewsHolder(newsItemView: View) : RecyclerView.ViewHolder(newsItemView), View.OnClickListener {

        val titleView: TextView = newsItemView.findViewById(R.id.tv_news_title)
        val descriptionView: TextView = newsItemView.findViewById(R.id.tv_news_detail)
        val imageView: ImageView = newsItemView.findViewById(R.id.iv_news_image)

        init {
            newsItemView.setOnClickListener(this)
        }

        override fun onClick(p0: View?) {
            Log.d("NewsAdapter", "News is clicked")
        }

    }
}