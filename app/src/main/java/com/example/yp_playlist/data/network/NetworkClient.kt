package com.example.yp_playlist.data.network

import com.example.yp_playlist.data.TrackResponse
import retrofit2.Call

const val ITUNES_BASE_URL = "https://itunes.apple.com"
interface NetworkClient {

    fun searchTrack(searchText: String): Call<TrackResponse>
}