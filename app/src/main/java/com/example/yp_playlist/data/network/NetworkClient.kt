package com.example.yp_playlist.data.network

interface NetworkClient {
    suspend fun doRequest(dto: Any): Response

}