package com.example.yp_playlist.data.network

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


class NetworkClientImpl(
    private val api: ITunesApi
) : NetworkClient {

    override suspend fun doRequest(dto: Any): Response {
//            if (isConnected() == false) {
//                return Response().apply { resultCode = -1 }
//            }

        if (dto !is SearchRequest) {
            return Response().apply { resultCode = 400 }
        }


        return withContext(Dispatchers.IO) {
            try {
                val response = api.searchTrack(dto.expression)
                response.apply { resultCode = 200 }
            } catch (e: Throwable) {
                Response().apply { resultCode = 500 }
            }
        }
    }

}