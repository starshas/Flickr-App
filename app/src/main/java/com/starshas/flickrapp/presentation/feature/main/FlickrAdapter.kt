package com.starshas.flickrapp.presentation.feature.main

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.text.Html
import android.text.method.LinkMovementMethod
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.starshas.flickrapp.R
import com.starshas.flickrapp.data.models.FlickrItem

class FlickrAdapter(
    private val context: Context,
    private val listItems: MutableList<FlickrItem> = mutableListOf(),
    private val actionOpenLinkInBrowser: (String) -> Unit,
) : RecyclerView.Adapter<FlickrAdapter.ItemViewHolder>() {
    class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageViewPoster: ImageView = view.findViewById(R.id.flickrItemImageView)
        val textViewTitle: TextView = view.findViewById(R.id.flickrItemTextViewTitle)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_flickr, parent, false)
        return ItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = listItems[position]
        val textViewTitle = holder.textViewTitle
        textViewTitle.text = Html.fromHtml(item.description, Html.FROM_HTML_MODE_LEGACY)
        textViewTitle.movementMethod = LinkMovementMethod.getInstance()
        textViewTitle.highlightColor = Color.TRANSPARENT

        Glide.with(context)
            .load(item.media.m)
            .fitCenter()
            .into(holder.imageViewPoster)

        holder.imageViewPoster.setOnClickListener {
            actionOpenLinkInBrowser(item.link)
        }
    }

    override fun getItemCount() = listItems.size

    @SuppressLint("NotifyDataSetChanged")
    fun setData(list: List<FlickrItem>) {
        listItems.clear()
        listItems.addAll(list)
        notifyDataSetChanged()
    }
}
