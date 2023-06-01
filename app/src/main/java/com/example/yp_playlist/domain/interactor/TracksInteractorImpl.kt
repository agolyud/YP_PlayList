package com.example.yp_playlist.domain.interactor

import com.example.yp_playlist.data.TrackResponse
import com.example.yp_playlist.domain.Track
import com.example.yp_playlist.domain.repository.TracksRepository
import retrofit2.Call
import java.util.concurrent.Callable
import java.util.concurrent.Executors

class TracksInteractorImpl(
    private val tracksRepository: TracksRepository
) : TracksInteractor {

    private val executor = Executors.newSingleThreadExecutor()

    override fun searchTrack(searchText: String): Call<TrackResponse> {
        val callable = Callable { tracksRepository.searchTrack(searchText)  }
        return executor.submit(callable).get()
    }

    override fun addTrack(track: Track, position: Int) = tracksRepository.addTrack(track, position)

    override fun tracksHistoryFromJson(): List<Track> = tracksRepository.tracksHistoryFromJson()

    override fun clearHistory() = tracksRepository.clearHistory()

}