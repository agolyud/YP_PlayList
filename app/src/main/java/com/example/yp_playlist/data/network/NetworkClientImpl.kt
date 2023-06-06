package com.example.yp_playlist.data.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class NetworkClientImpl : NetworkClient {

    companion object {
        const val itunesBaseUrl = "https://itunes.apple.com"
    }

    private val retrofit = Retrofit.Builder()
        .baseUrl(itunesBaseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val tracksApi = retrofit.create(ITunesApi::class.java)

    override fun searchTrack(searchText: String) = tracksApi.searchTrack(searchText)
}