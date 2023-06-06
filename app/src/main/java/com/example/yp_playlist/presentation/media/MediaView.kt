package com.example.yp_playlist.presentation.media

import com.example.yp_playlist.domain.Track


interface MediaView {
    fun showInfo(track: Track)
}