package com.example.yp_playlist.presentation.search

import android.view.View
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.yp_playlist.App
import com.example.yp_playlist.domain.Track
import com.example.yp_playlist.data.TrackResponse
import com.example.yp_playlist.domain.interactors.tracks.TracksInteractor
import com.example.yp_playlist.util.Constants.CLICK_DELAY
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SearchViewModel(
    private val tracksInteractor: TracksInteractor,
    application: App,
) : AndroidViewModel(application) {

    private val _tracksHistory = MutableLiveData<List<Track>>(listOf())
    val tracksHistory: LiveData<List<Track>> = _tracksHistory

    private val _searchState = MutableLiveData<List<Track>>()
    val searchState: LiveData<List<Track>> = _searchState

    private val _fragmentState = MutableLiveData<FragmentState>()
    val fragmentState: LiveData<FragmentState> = _fragmentState

    private var searchJob: Job? = null
    val clearButtonVisibility = MutableLiveData<Int>()

    init {
        _searchState.postValue(listOf())
        _fragmentState.postValue(getDefaultState())
    }

    private fun getDefaultState() =
        if (tracksHistoryFromJson().isEmpty()) FragmentState.HISTORY_EMPTY
        else FragmentState.HISTORY


    fun setHistory() {
        val tracksHistory = tracksHistoryFromJson()
        _tracksHistory.value = tracksHistory
    }

    fun onSearchTextChanged(s: CharSequence?) {
        clearButtonVisibility.postValue(clearButtonVisibility(s))
        val searchText = s?.toString() ?: ""
        if (searchText.isEmpty()) {
            val tracksHistory = tracksHistoryFromJson()
            _tracksHistory.value = tracksHistory

            if (tracksHistory.isNotEmpty()) {
                _fragmentState.postValue(FragmentState.HISTORY)
            } else {
                _fragmentState.postValue(FragmentState.HISTORY_EMPTY)
            }

            searchJob?.cancel()
        } else {
            _fragmentState.postValue(FragmentState.SEARCH)
            searchJob?.cancel()
            searchJob = CoroutineScope(Dispatchers.Main).launch {
                delay(CLICK_DELAY)
                searchTrack(searchText)
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
        _fragmentState.postValue(FragmentState.LOADING)
        tracksInteractor.searchTrack(searchText)?.enqueue(object : Callback<TrackResponse> {
            override fun onResponse(call: Call<TrackResponse>, response: Response<TrackResponse>) {
                _searchState.value = emptyList()
                if (searchText.isNotEmpty() && response.isSuccessful) {
                    val tracks = response.body()?.results ?: listOf()
                    if (tracks.isNotEmpty()) {
                        _searchState.postValue(tracks) // Обновление LiveData с треками
                        _fragmentState.postValue(FragmentState.SUCCESS)
                    } else {
                        _fragmentState.postValue(FragmentState.EMPTY)
                    }
                } else {
                    _fragmentState.postValue(FragmentState.EMPTY)
                }
            }

            override fun onFailure(call: Call<TrackResponse>, t: Throwable) {
                _fragmentState.postValue(FragmentState.ERROR)
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

    enum class FragmentState {
        LOADING,
        ERROR,
        EMPTY,
        HISTORY,
        SEARCH,
        HISTORY_EMPTY,
        SUCCESS
    }

}