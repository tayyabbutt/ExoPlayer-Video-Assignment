package com.video.ui

import android.content.pm.ActivityInfo
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import com.video.R
import com.video.model.Video
import com.video.repository.VideoRepository
import com.video.utils.HelperMethods.hideSystemUI
import com.video.viewmodel.VideoViewModel
import com.video.viewmodel.VideoViewModelFactory

class PlayerActivity : AppCompatActivity() {

    private lateinit var playerView: PlayerView
    private lateinit var exoPlayer: ExoPlayer

    private lateinit var videoViewModel: VideoViewModel

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        supportActionBar?.hide()
        hideSystemUI(window)
        setContentView(R.layout.activity_player)
        setupExoPlayer()  // Initialize Media3 ExoPlayer
        setupViewModel()  // Initialize ViewModel
        setupVideoList()  // Set up the video list and load the initial video
        setupPlayerListener()  // Add listener for ad and main video transitions
        observeIfAdisPlayingToHideControls() // observing if ad is playing to hide it.

    }

    private fun observeIfAdisPlayingToHideControls(){
        videoViewModel.isAdPlaying.observe(this, Observer { isAdPlaying ->
            disableControlsForAds()
        })
    }


    private fun setupExoPlayer() {
        exoPlayer = ExoPlayer.Builder(this).build()
        playerView = findViewById(R.id.player_view)
        playerView.player = exoPlayer
    }

    private fun setupViewModel() {
        val repository = VideoRepository()
        val factory = VideoViewModelFactory(repository, exoPlayer)
        videoViewModel = factory.create(VideoViewModel::class.java)
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun setupVideoList() {
        val video = intent.getSerializableExtra("video", Video::class.java)
        if (video != null) {
            // Add the selected video to the start of the playlist to make sure the selected is
            // played. This logic can be update if we need to play next video from controls,
            // which is not our case for now
            val customPlaylist = VideoRepository().getPlaylist().toMutableList()
            customPlaylist.add(
                0,
                Video(video.url, video.name, video.isAd)
            )
            videoViewModel.setVideoList(customPlaylist)
        }
        // Load the initial video (either from the playlist or default)
        videoViewModel.loadInitialVideo()
    }

    private fun setupPlayerListener() {
        // Handle player state changes, especially when the video ends
        exoPlayer.addListener(object : Player.Listener {
            override fun onPlaybackStateChanged(playbackState: Int) {
                if (playbackState == Player.STATE_ENDED) {
                    // Check if the current video is an ad and handle state accordingly
                    if (videoViewModel.isAdPlaying.value == true) {
                        // Reset ad flag and resume main video
                        videoViewModel._isAdPlaying.value = false  // Set the flag to false
                        videoViewModel.resumeMainVideo()  // Resume the main video
                    }
                }
            }
        })
    }

    override fun onPause() {
        super.onPause()
        videoViewModel.stopAdCheck() // Stop checking for ads when the activity is paused
        exoPlayer.pause() // Pause the Media3 ExoPlayer when the activity is paused
    }

    override fun onDestroy() {
        super.onDestroy()
        videoViewModel.stopAdCheck() // Stop checking for ads when the activity is destroyed
        exoPlayer.release() // Release the Media3 ExoPlayer when the activity is destroyed
    }
    @androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
    private fun disableControlsForAds() {
        if (videoViewModel.isAdPlaying.value == true) {
            playerView.controllerHideOnTouch = false
            playerView.controllerAutoShow = false
            // For some reason, when touching screen if ad is playing, it shows the controls, so
            // hiding them forcefully and disabling the touch interaction.
            playerView.hideController()
            playerView.setOnTouchListener { _, _ -> true }
        } else {
            playerView.controllerHideOnTouch = true
            playerView.controllerAutoShow = false
            // Restoring the touch state.
            playerView.showController()
            playerView.setOnTouchListener (null)
        }
    }
}
