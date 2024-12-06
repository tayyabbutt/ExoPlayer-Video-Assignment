package com.video.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.video.adapter.PlaylistAdapter
import com.video.R
import com.video.model.Video
import com.video.repository.VideoRepository

class PlaylistActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: PlaylistAdapter
    private lateinit var repository: VideoRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_playlist)

        repository = VideoRepository()
        recyclerView = findViewById(R.id.recycler_view)

        // Fetch video playlist
        val videoList = repository.getPlaylist()
        val filterOnAdVideo = videoList.filter { !it.isAd }


        adapter = PlaylistAdapter(filterOnAdVideo, object : PlaylistAdapter.OnItemClick {
            override fun onItemClick(selectedVideo: Video) {
                val intent = Intent(this@PlaylistActivity, PlayerActivity::class.java)
                intent.putExtra("video", selectedVideo)
                startActivity(intent)
            }
        })


        // Set up RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
    }
}
