package com.example.yp_playlist.domain.interactors.tracks

import com.example.yp_playlist.domain.Track
import com.example.yp_playlist.data.TrackResponse
import retrofit2.Call

interface TracksInteractor {
    fun searchTrack(searchText: String): Call<TrackResponse>?
    fun addTrack(track: Track, position: Int)
    fun tracksHistoryFromJson(): List<Track>
    fun clearHistory()
}