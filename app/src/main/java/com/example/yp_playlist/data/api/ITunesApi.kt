package com.example.yp_playlist.data.api

import com.example.yp_playlist.data.TrackResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface ITunesApi {
    @GET("/search?entity=song")
    suspend fun searchTrack(@Query("term") text: String): TrackResponse
}