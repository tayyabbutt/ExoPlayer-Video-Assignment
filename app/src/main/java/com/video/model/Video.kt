package com.video.model

import java.io.Serializable

data class Video(
    val url: String,
    val name: String,
    val isAd: Boolean = false
) : Serializable