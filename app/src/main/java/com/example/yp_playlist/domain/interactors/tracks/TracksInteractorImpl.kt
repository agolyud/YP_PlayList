package com.example.yp_playlist.domain.interactors.tracks

import com.example.yp_playlist.data.network.Resource
import com.example.yp_playlist.domain.Track
import com.example.yp_playlist.domain.repository.TracksRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class TracksInteractorImpl(
    private val tracksRepository: TracksRepository
) : TracksInteractor {

    override fun searchTrack(expression: String): Flow<Pair<List<Track>?, String?>> {
        return tracksRepository.searchTrack(expression).map { result ->
            when (result) {
                is Resource.Success -> {
                    Pair(result.data, null)
                }

                is Resource.Error -> {
                    Pair(null, result.message)
                }
            }
        }
    }


    override fun addTrack(track: Track, position: Int) = tracksRepository.addTrack(track, position)

    override fun tracksHistoryFromJson(): List<Track> = tracksRepository.tracksHistoryFromJson()

    override fun clearHistory() = tracksRepository.clearHistory()

}