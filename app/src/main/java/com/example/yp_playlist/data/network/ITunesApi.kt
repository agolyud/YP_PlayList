package com.example.yp_playlist.data.network

import com.example.yp_playlist.data.TrackResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface ITunesApi {
    @GET("/search?entity=song")
    suspend fun searchTrack(@Query("term") text: String): TrackResponse
}