package com.example.yp_playlist.domain.repository

import com.example.yp_playlist.data.TrackResponse
import com.example.yp_playlist.domain.Track
import com.example.yp_playlist.data.history.SearchHistory
import com.example.yp_playlist.data.network.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class TracksRepositoryImpl(
    private val networkClient: NetworkClient,
    private val searchHistory: SearchHistory
) : TracksRepository {

    override fun searchTrack(expression: String): Flow<Resource<List<Track>>> = flow {
        val response = networkClient.doRequest(SearchRequest(expression))
        when (response.resultCode) {
            -1 -> {
                emit(Resource.Error("Проверьте подключение к интернету"))
            }

            200 -> {
                with(response as TrackResponse) {
                    emit(Resource.Success(response.results.map {
                        Track(
                            trackId = it.trackId,
                            trackName = it.trackName,
                            artistName = it.artistName,
                            trackTimeMillis = it.trackTimeMillis,
                            artworkUrl100 = it.artworkUrl100,
                            collectionName = it.collectionName,
                            releaseDate = it.releaseDate,
                            primaryGenreName = it.primaryGenreName,
                            country = it.country,
                            previewUrl = it.previewUrl,
                        )
                    }))
                }
            }

            else -> {
                emit(Resource.Error("Ошибка сервера"))
            }
        }

    }

    override fun addTrack(track: Track, position: Int) = searchHistory.addTrack(track, position)

    override fun tracksHistoryFromJson(): List<Track> = searchHistory.tracksHistoryFromJson()

    override fun clearHistory() = searchHistory.clearHistory()

}