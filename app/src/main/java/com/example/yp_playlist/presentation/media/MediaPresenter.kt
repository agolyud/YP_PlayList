package com.example.yp_playlist.presentation.media

import com.example.yp_playlist.data.date.DateManager
import com.example.yp_playlist.domain.storage.SearchHistory


class MediaPresenter(
    private val view: MediaView,
    private val dateManager: DateManager,
    private val searchHistory: SearchHistory
) {


    fun getTime(trackTime: Int): String {
        return dateManager.getTime(trackTime)
    }

    fun getDate(releaseDate: String): String {
        return dateManager.getDate(releaseDate)
    }

    fun getTrack(trackId: Int) {
        val track = searchHistory.tracksHistoryFromJson().firstOrNull {
            it.trackId == trackId
        }
        if (track != null) {
            view.showInfo(track)
        }
    }

}