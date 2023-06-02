package com.example.yp_playlist.data.network

import com.example.yp_playlist.data.TrackResponse
import retrofit2.Call

interface NetworkClient {

    fun searchTrack(searchText: String): Call<TrackResponse>
}