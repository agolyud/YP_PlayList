package com.example.yp_playlist.presentation.media

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.yp_playlist.App
import com.example.yp_playlist.data.date.DateManager
import com.example.yp_playlist.data.date.DateManagerImpl

import com.example.yp_playlist.domain.Track
import com.example.yp_playlist.domain.storage.SearchHistory
import com.example.yp_playlist.domain.storage.SearchHistoryImpl


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
            _trackInfo.value = track
        }
    }

    companion object {

        fun getViewModelFactory(context: Context, sharedPref: SharedPreferences): ViewModelProvider.Factory =
            viewModelFactory {
                initializer {
                    val application =
                        this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as App
                    MediaViewModel(
                        dateManager = DateManagerImpl(),
                        searchHistory = SearchHistoryImpl(sharedPref),
                    )
                }
            }
    }
}