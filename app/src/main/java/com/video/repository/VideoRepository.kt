package com.video.repository

import com.video.model.Video

class VideoRepository {
    fun getPlaylist(): List<Video> {
        return listOf(
            Video("https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/TearsOfSteel.mp4", "Video 1", isAd = false),
            Video("https://test-videos.co.uk/vids/sintel/mp4/h264/720/Sintel_720_10s_1MB.mp4", "Video", isAd = true),
            Video("https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/Sintel.mp4", "Video 2", isAd = false)
        )
    }
}