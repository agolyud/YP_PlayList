package com.example.yp_playlist.domain.interactors.tracks

import com.example.yp_playlist.domain.Track
import kotlinx.coroutines.flow.Flow

interface TracksInteractor {
    fun searchTrack(expression: String): Flow<Pair<List<Track>?, String?>>
    fun addTrack(track: Track, position: Int)
    fun tracksHistoryFromJson(): List<Track>
    fun clearHistory()
}