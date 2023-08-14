package com.example.yp_playlist.data.network


class NetworkClientImpl(private val api: ITunesApi) : NetworkClient {

    override fun searchTrack(searchText: String) = api.searchTrack(searchText)
}