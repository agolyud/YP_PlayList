package com.example.yp_playlist.presentation.search

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


class SearchViewModel(
    private val tracksInteractor: TracksInteractor,
    application: App,
) : AndroidViewModel(application) {

    private val _searchState = MutableLiveData<List<Track>>()
    val searchState: LiveData<List<Track>> = _searchState

    private val _fragmentState = MutableLiveData<SearchState>()
    val fragmentState: LiveData<SearchState> = _fragmentState

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