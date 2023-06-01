package com.example.yp_playlist.presentation.search

import com.example.yp_playlist.domain.Track
import com.example.yp_playlist.data.TrackResponse
import com.example.yp_playlist.domain.interactor.TracksInteractor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class SearchPresenter(
    private val view: SearchView,
    private val tracksInteractor: TracksInteractor
) {
    fun searchTrack(searchText: String) {
        tracksInteractor.searchTrack(searchText)?.enqueue(object : Callback<TrackResponse> {
            override fun onResponse(call: Call<TrackResponse>, response: Response<TrackResponse>) {
                view.updateTracks(emptyList())
                if (searchText.isNotEmpty() && response.isSuccessful) {
                    val tracks = response.body()?.results ?: listOf()
                    if (tracks.isNotEmpty()) {
                        view.updateTracks(tracks)
                        view.showTracks()
                    } else {
                        view.nothingWasFound()
                    }
                } else {
                    view.nothingWasFound()
                }
            }

            override fun onFailure(call: Call<TrackResponse>, t: Throwable) {
                view.error()
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