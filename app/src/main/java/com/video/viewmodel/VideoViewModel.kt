package com.video.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import com.video.model.Video
import com.video.repository.VideoRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class VideoViewModel(private val repository: VideoRepository, private val exoPlayer: ExoPlayer) : ViewModel() {

    private var videoList = mutableListOf<Video>()
    val _isAdPlaying = MutableLiveData(false)
    val isAdPlaying: LiveData<Boolean> get() = _isAdPlaying // Expose as LiveData to observe

    private val viewModelScope = CoroutineScope(Dispatchers.Main + SupervisorJob()) // Main coroutine scope for UI tasks
    private var checkPositionJob: Job? = null // Job to cancel the coroutine when needed

    var currentIndex = 0
    private var savedPosition: Long = 0L
    private var lastAdTime: Long = 0


    // Initialize and load the first video (our main video)
    fun loadInitialVideo() {
        currentIndex = videoList.indexOfFirst { !it.isAd }
        if (currentIndex == -1) currentIndex = 0 // Fallback if no main video
        playCurrentVideo()
        startAdCheck() // Start checking for ads
    }

    // Play an ad video
    fun playAdVideo() {
        if (isAdPlaying.value == true) return

        // Save the position of the current main video before switching to the ad
        if (isAdPlaying.value == false) {
            savedPosition = exoPlayer.currentPosition
        }

        // Switch to the ad
        currentIndex = videoList.indexOfFirst { it.isAd }.takeIf { it >= 0 } ?: currentIndex
        _isAdPlaying.value = true // Set the flag to true

        playCurrentVideo()
    }

    // Resume main video after ad
  fun resumeMainVideo() {
        val mainVideoIndex = videoList.indexOfFirst { !it.isAd }
        if (mainVideoIndex >= 0) {
            currentIndex = mainVideoIndex
            // Rewind 3 seconds and making sure it dosnt go below 0
            val rewindedPosition = (savedPosition - 3000).coerceAtLeast(0)
            playCurrentVideo(rewindedPosition) // Resume from the rewinded position
            savedPosition = 0L // Reset the saved position after resuming the main video
            _isAdPlaying.value = false
        } else {
            currentIndex = 0 // Fallback in case no main video exists
            playCurrentVideo()
        }
    }

    // Function to play the current video from a specific position
    private fun playCurrentVideo(startPosition: Long = 0L) {
        val mediaItem = MediaItem.fromUri(videoList[currentIndex].url)
        exoPlayer.setMediaItem(mediaItem)
        exoPlayer.prepare()
        exoPlayer.seekTo(startPosition)
        exoPlayer.play()
    }

    // Set the list of videos (ads and main video)
    fun setVideoList(newVideoList: List<Video>) {
        videoList.clear()
        videoList.addAll(newVideoList)
    }

    // Function to check if it's time for the next ad
    fun shouldPlayAd(currentTime: Long): Boolean {
        return (currentTime - lastAdTime >= AD_THRESHOLD)
    }

    // Start periodic check for ad playback
    private fun startAdCheck() {
        checkPositionJob = viewModelScope.launch {
            while (isActive) {
                val currentTimePosition = exoPlayer.currentPosition
                // Check if 30 seconds have passed and no ad is playing
                if (shouldPlayAd(currentTimePosition) && _isAdPlaying.value == false) {
                    playAdVideo() // Play the ad
                    lastAdTime = currentTimePosition
                }

                delay(1000)
            }
        }
    }

    // Stop the ad checking when not needed
    fun stopAdCheck() {
        checkPositionJob?.cancel() // Cancel the coroutine
    }

    companion object {
        const val AD_THRESHOLD = 30000
    }
}




