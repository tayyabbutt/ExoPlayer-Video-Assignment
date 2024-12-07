package com.video.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.video.repository.VideoRepository


class VideoViewModelFactory(
    private val repository: VideoRepository,
    private val exoPlayer: androidx.media3.exoplayer.ExoPlayer
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(VideoViewModel::class.java)) {
            return VideoViewModel(repository, exoPlayer) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}