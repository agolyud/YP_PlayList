package com.example.yp_playlist.presentation.search

import android.view.View
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.yp_playlist.App
import com.example.yp_playlist.domain.Track
import com.example.yp_playlist.data.TrackResponse
import com.example.yp_playlist.domain.interactors.tracks.TracksInteractor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import android.os.Handler
import android.os.Looper

class SearchViewModel(
    private val tracksInteractor: TracksInteractor,
    application: App,
) : AndroidViewModel(application) {

    val historyTracks = MutableLiveData<List<Track>>(listOf())

    private val _searchState = MutableLiveData<List<Track>>()
    val searchState: LiveData<List<Track>> = _searchState

    private val _fragmentState = MutableLiveData<SearchState>()
    val fragmentState: LiveData<SearchState> = _fragmentState

    private val handler = Handler(Looper.getMainLooper())
    val clearButtonVisibility = MutableLiveData<Int>()
    val searchResultsVisibility = MutableLiveData<Int>()
    val placeholderVisibility = MutableLiveData<Int>()
    val historyListVisibility = MutableLiveData<Int>()
    val tracksHistory = MutableLiveData<List<Track>>()

    init {
        _searchState.postValue(listOf())
        _fragmentState.postValue(SearchState.SUCCESS)
    }

    enum class SearchState {
        LOADING,
        ERROR,
        EMPTY,
        SUCCESS
    }

    fun updateHistoryTracks(tracks: List<Track>) {
        historyTracks.value = tracks
    }

    fun onSearchTextChanged(s: CharSequence?) {
        clearButtonVisibility.postValue(clearButtonVisibility(s))
        val searchText = s?.toString() ?: ""

        if (searchText.isEmpty()) {
            val historyTracks = tracksHistoryFromJson()
            tracksHistory.postValue(historyTracks)

            if (historyTracks.isNotEmpty()) {
                searchResultsVisibility.postValue(View.GONE)
                placeholderVisibility.postValue(View.INVISIBLE)
                historyListVisibility.postValue(View.VISIBLE)
            } else {
                searchResultsVisibility.postValue(View.GONE)
                placeholderVisibility.postValue(View.INVISIBLE)
                historyListVisibility.postValue(View.GONE)
            }

        } else {
            placeholderVisibility.postValue(View.GONE)
            searchResultsVisibility.postValue(View.VISIBLE)
            historyListVisibility.postValue(View.GONE)

            handler.removeCallbacksAndMessages(null)

            if (searchText.isNotEmpty()) {
                handler.postDelayed({ searchTrack(searchText) }, 2000)
            }
        }
    }

    private fun clearButtonVisibility(s: CharSequence?): Int {
        return if (s.isNullOrEmpty()) {
            View.GONE
        } else {
            View.VISIBLE
        }
    }

    fun searchTrack(searchText: String) {
        _fragmentState.postValue(SearchState.LOADING)
        tracksInteractor.searchTrack(searchText)?.enqueue(object : Callback<TrackResponse> {
            override fun onResponse(call: Call<TrackResponse>, response: Response<TrackResponse>) {
                _searchState.value = emptyList()
                if (searchText.isNotEmpty() && response.isSuccessful) {
                    val tracks = response.body()?.results ?: listOf()
                    if (tracks.isNotEmpty()) {
                        _searchState.postValue(tracks) // Обновление LiveData с треками
                        _fragmentState.postValue(SearchState.SUCCESS)
                    } else {
                        _fragmentState.postValue(SearchState.EMPTY)
                    }
                } else {
                    _fragmentState.postValue(SearchState.EMPTY)
                }
            }

            override fun onFailure(call: Call<TrackResponse>, t: Throwable) {
                _fragmentState.postValue(SearchState.ERROR)
            }
        })
    }

    fun addTrack(track: Track, position: Int) {
        tracksInteractor.addTrack(track, position)
    }

    fun tracksHistoryFromJson() = tracksInteractor.tracksHistoryFromJson()

    fun clearHistory() {
        tracksInteractor.clearHistory()
    }

}