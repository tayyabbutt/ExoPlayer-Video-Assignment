package com.video.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.video.R
import com.video.model.Video

class PlaylistAdapter(
    private val videos: List<Video>,
    private val onItemClick: OnItemClick
) :
    RecyclerView.Adapter<PlaylistAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_video, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = videos[position]
        holder.videoTitle.text = item.name

        holder.itemView.setOnClickListener {
            onItemClick.onItemClick(item)
        }

    }

    override fun getItemCount(): Int {
        return videos.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val videoTitle: TextView = itemView.findViewById(R.id.video_title)
    }


    interface OnItemClick {
        fun onItemClick(selectedVideo: Video)
    }

}
