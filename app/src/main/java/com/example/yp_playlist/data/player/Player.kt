package com.example.yp_playlist.data.player

interface Player {

    fun prepare(
        url: String,
        onPrepared: () -> Unit,
        onCompletion: () -> Unit
    )
    fun start()
    fun pause()
    fun release()
    fun getPosition(): Int
    fun isPlaying(): Boolean
}