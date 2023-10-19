package com.example.yp_playlist.domain.interactors.media

import com.example.yp_playlist.data.date.DateManager
import com.example.yp_playlist.data.history.SearchHistory
import com.example.yp_playlist.data.player.Player
import com.example.yp_playlist.domain.Track

class MediaInteractorImpl(
    private val mediaPlayer: Player,
    private val dateManager: DateManager,
    private val searchHistory: SearchHistory
) : MediaInteractor {

    override fun prepare(url: String?, onPrepared: () -> Unit, onCompletion: () -> Unit) {
        mediaPlayer.prepare(url, onPrepared, onCompletion)
    }

    override fun start() {
        mediaPlayer.start()
    }

    override fun pause() {
        mediaPlayer.pause()
    }

    override fun release() {
        mediaPlayer.release()
    }

    override fun getPosition() = mediaPlayer.getPosition()

    override fun isPlaying() = mediaPlayer.isPlaying()

    override fun getTime(trackTime: Int): String {
        return dateManager.getTime(trackTime)
    }

    override fun getDate(releaseDate: String): String {
        return dateManager.getDate(releaseDate)
    }

    override fun tracksHistoryFromJson(): List<Track> {
        return searchHistory.tracksHistoryFromJson()
    }


}