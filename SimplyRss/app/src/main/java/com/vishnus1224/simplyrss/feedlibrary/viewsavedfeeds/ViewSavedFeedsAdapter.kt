package com.vishnus1224.simplyrss.feedlibrary.viewsavedfeeds

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.vishnus1224.simplyrss.R
import com.vishnus1224.simplyrss.feedlibrary.Feed
import com.vishnus1224.simplyrss.util.isEven

internal class ViewSavedFeedsAdapter(
    private val feeds: ArrayList<Feed>,
    private val onFeedClick: (Feed) -> Unit,
    private val onDeleteFeedClick: (Feed) -> Unit
) : RecyclerView.Adapter<ViewSavedFeedsAdapter.SavedFeedsViewHolder>() {

    fun setFeeds(items: List<Feed>) {
        feeds.clear()
        feeds.addAll(items)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): SavedFeedsViewHolder {
        return SavedFeedsViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.adapter_saved_feeds, parent, false)
        )
    }

    override fun getItemCount(): Int = feeds.size

    override fun onBindViewHolder(holder: SavedFeedsViewHolder, position: Int) {
        val feed = feeds[position]
        holder.titleTextView.text = feed.title
        holder.descriptionTextView.text = feed.description

        holder.backgroundImageView.setOnClickListener { onFeedClick(feed) }

        if (position.isEven()) {
            holder.backgroundImageView.rotation = 15.0f
        } else {
            holder.backgroundImageView.rotation = 165.0f
        }

        holder.deleteIconImageView.setOnClickListener { onDeleteFeedClick(feed) }
    }

    class SavedFeedsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val backgroundImageView: ImageView = itemView.findViewById(R.id.adapter_saved_feed_background)
        val titleTextView: TextView = itemView.findViewById(R.id.adapter_saved_feed_title)
        val descriptionTextView: TextView = itemView.findViewById(R.id.adapter_saved_feed_description)
        val deleteIconImageView: ImageView = itemView.findViewById(R.id.adapter_saved_feed_delete_icon)
    }

}