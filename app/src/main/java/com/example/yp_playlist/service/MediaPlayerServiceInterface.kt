package com.example.yp_playlist.service

import com.example.yp_playlist.domain.Track

interface MediaPlayerServiceInterface {
    fun start()
    fun pause()
    fun stop()
    fun getPosition(): Int
    fun isPlaying(): Boolean
    fun startForegroundNotification(track: Track)
    fun stopForegroundNotification()
}