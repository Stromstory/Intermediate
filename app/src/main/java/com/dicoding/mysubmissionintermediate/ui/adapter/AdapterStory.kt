package com.dicoding.mysubmissionintermediate.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dicoding.mysubmissionintermediate.R
import com.dicoding.mysubmissionintermediate.data.response.ListStoryItem

class AdapterStory(
    private val onItemClick: (ListStoryItem, View, View, View) -> Unit
) : PagingDataAdapter<ListStoryItem, AdapterStory.StoryViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.story_item, parent, false)
        return StoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: StoryViewHolder, position: Int) {
        val story = getItem(position)
        if (story != null) {
            holder.bind(story, onItemClick)
        }
    }

    class StoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val ivItemPhoto: ImageView = itemView.findViewById(R.id.iv_photo_story)
        private val tvItemName: TextView = itemView.findViewById(R.id.tv_name_story)
        private val tvItemDescription: TextView = itemView.findViewById(R.id.tv_description_story)

        fun bind(story: ListStoryItem, onItemClick: (ListStoryItem, View, View, View) -> Unit) {
            tvItemName.text = story.name
            tvItemDescription.text = story.description

            Glide.with(itemView.context)
                .load(story.photoUrl)
                .into(ivItemPhoto)

            itemView.setOnClickListener {
                onItemClick(story, ivItemPhoto, tvItemName, tvItemDescription)
            }
        }
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ListStoryItem>() {
            override fun areItemsTheSame(oldItem: ListStoryItem, newItem: ListStoryItem): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: ListStoryItem, newItem: ListStoryItem): Boolean {
                return oldItem == newItem
            }
        }
    }
}