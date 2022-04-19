package com.example.newsapp.adapter

import android.annotation.SuppressLint
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.newsapp.R
import com.example.newsapp.models.Article
import com.squareup.picasso.Picasso

class NewsAdapter : RecyclerView.Adapter<NewsAdapter.NewsHolder>() {

    val newsList: ArrayList<Article> = arrayListOf()
    private var onItemClickListener: ((Article) -> Unit)? = null

    @SuppressLint("NotifyDataSetChanged")
    fun updateNewsList(newsList: ArrayList<Article>) {
        this.newsList.clear()
        this.newsList.addAll(newsList)
        this.notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    @Suppress("unused")
    fun clearNewsList() {
        this.newsList.clear()
        this.notifyDataSetChanged()
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
        if(newsItemViewModel.urlToImage != null) {
            Picasso.get().load(Uri.parse(newsItemViewModel.urlToImage)).into(holder.imageView)
        } else {
            holder.imageView.setImageResource(R.drawable.ic_image_not_supported)
        }
        holder.authorView.text = newsItemViewModel.author
        holder.dateView.text = newsItemViewModel.publishedAt
    }

    override fun getItemCount(): Int {
        return newsList.size
    }

    fun setOnItemClickListener(listener: (Article) -> Unit) {
        onItemClickListener = listener
    }

    inner class NewsHolder(newsItemView: View) : RecyclerView.ViewHolder(newsItemView) {

        val titleView: TextView = newsItemView.findViewById(R.id.tv_news_title)
        val descriptionView: TextView = newsItemView.findViewById(R.id.tv_news_detail)
        val imageView: ImageView = newsItemView.findViewById(R.id.iv_news_image)
        val authorView: TextView = newsItemView.findViewById(R.id.tv_article_author)
        val dateView: TextView = newsItemView.findViewById(R.id.tv_article_date)

        init {
            newsItemView.setOnClickListener{
                onItemClickListener?.invoke(newsList[adapterPosition])
            }
        }

    }
}