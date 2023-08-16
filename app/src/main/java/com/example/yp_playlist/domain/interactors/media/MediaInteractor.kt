package com.example.yp_playlist.domain.interactors.media

import com.example.yp_playlist.domain.Track

interface MediaInteractor {

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
    fun getTime(trackTime: Int): String
    fun getDate(releaseDate: String): String
    fun tracksHistoryFromJson(): List<Track>

}