package com.example.yp_playlist.domain.repository

import com.example.yp_playlist.data.network.Resource
import com.example.yp_playlist.domain.Track
import kotlinx.coroutines.flow.Flow

interface TracksRepository {
    fun searchTrack(expression: String): Flow<Resource<List<Track>>>
    fun addTrack(track: Track, position: Int)
    fun tracksHistoryFromJson(): List<Track>
    fun clearHistory()
}