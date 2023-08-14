package com.example.yp_playlist.domain.repository

import com.example.yp_playlist.domain.Track
import com.example.yp_playlist.data.network.NetworkClient
import com.example.yp_playlist.data.history.SearchHistory

class TracksRepositoryImpl(
    private val networkClient: NetworkClient,
    private val searchHistory: SearchHistory
) : TracksRepository {

    override fun searchTrack(searchText: String) = networkClient.searchTrack(searchText)

    override fun addTrack(track: Track, position: Int) = searchHistory.addTrack(track, position)

    override fun tracksHistoryFromJson(): List<Track> = searchHistory.tracksHistoryFromJson()

    override fun clearHistory() = searchHistory.clearHistory()

}