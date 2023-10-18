package com.example.yp_playlist.data.player

import android.media.MediaPlayer

class PlayerImpl(private val mediaPlayer: MediaPlayer) : Player {

    override fun prepare(url: String?, onPrepared: () -> Unit, onCompletion: () -> Unit) {
        mediaPlayer.reset()
        mediaPlayer.setDataSource(url)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            onPrepared.invoke()
        }
        mediaPlayer.setOnCompletionListener {
            onCompletion.invoke()
        }
    }

    override fun start() {
        mediaPlayer.start()
    }

    override fun pause() {
        mediaPlayer.pause()
    }

    override fun release() {
        mediaPlayer.stop()
        mediaPlayer.reset()
        mediaPlayer.setOnCompletionListener(null)
        mediaPlayer.setOnPreparedListener(null)
    }

    override fun getPosition() = mediaPlayer.currentPosition

    override fun isPlaying() = mediaPlayer.isPlaying
}