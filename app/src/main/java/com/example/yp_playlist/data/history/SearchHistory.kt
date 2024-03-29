package com.example.yp_playlist.data.history

import com.example.yp_playlist.domain.Track

interface SearchHistory {
    fun addTrack(track: Track, position: Int)
    fun tracksHistoryFromJson(): List<Track>
    fun clearHistory()
}