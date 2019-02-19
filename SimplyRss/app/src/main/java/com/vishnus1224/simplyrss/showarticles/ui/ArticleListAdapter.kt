package com.vishnus1224.simplyrss.showarticles.ui

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.squareup.picasso.Picasso
import com.vishnus1224.simplyrss.R
import com.vishnus1224.simplyrss.showarticles.Article

internal class ArticleListAdapter(
    private val articles: ArrayList<Article>,
    private val onArticleClick: (Article) -> Unit
) : RecyclerView.Adapter<ArticleListAdapter.ArticleViewHolder>() {

    fun setItems(articles: List<Article>) {
        this.articles.clear()
        this.articles.addAll(articles)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = articles.size

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): ArticleViewHolder {
        return ArticleViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.adapter_article_list, parent, false))
    }

    override fun onBindViewHolder(holder: ArticleViewHolder, position: Int) {
        val article = articles[position]
        article.imageUrl?.let { Picasso.get().load(it).fit().into(holder.iconImageView) }
        article.title?.let { holder.titleTextView.text = it }
        article.description?.let { holder.descTextView.text = it }
        holder.itemView.setOnClickListener { onArticleClick(article) }
    }

    class ArticleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val iconImageView: ImageView = itemView.findViewById(R.id.adapter_article_list_icon)
        val titleTextView: TextView = itemView.findViewById(R.id.adapter_article_list_title)
        val descTextView: TextView = itemView.findViewById(R.id.adapter_article_list_description)
    }

}