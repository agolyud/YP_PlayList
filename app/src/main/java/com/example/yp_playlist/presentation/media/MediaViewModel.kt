package com.example.yp_playlist.presentation.media

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.yp_playlist.data.date.DateManager
import com.example.yp_playlist.domain.Track
import com.example.yp_playlist.data.date.SearchHistory


class MediaViewModel(
    private val dateManager: DateManager,
    private val searchHistory: SearchHistory
) : ViewModel() {

    private val _trackInfo = MutableLiveData<Track>()

    val trackInfo: LiveData<Track> = _trackInfo

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
            _trackInfo.value = track!!
        }
    }

}