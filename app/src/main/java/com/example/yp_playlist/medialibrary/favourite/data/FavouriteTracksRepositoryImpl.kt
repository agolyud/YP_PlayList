package com.example.yp_playlist.medialibrary.favourite.data

import com.example.yp_playlist.db.AppDatabase
import com.example.yp_playlist.db.converter.TrackDbConverter
import com.example.yp_playlist.db.entity.TrackEntity
import com.example.yp_playlist.domain.Track
import com.example.yp_playlist.medialibrary.favourite.domain.api.FavouriteTracksRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FavouriteTracksRepositoryImpl(
    private val appDatabase: AppDatabase,
    private val trackDbConvertor: TrackDbConverter,
): FavouriteTracksRepository {

    override suspend fun getFavoritesTracks(): Flow<List<Track>> = flow {
        val tracks = appDatabase.trackDao().getTracks()
        emit(convertFromTrackEntity(tracks))
    }

    override suspend fun isFavoriteTrack(trackId: Int): Flow<Boolean> = flow {
        val isFavorite = appDatabase.trackDao().isFavoriteTrack(trackId)
        emit(isFavorite)
    }

    override suspend fun addToFavorites(track: Track) {
        appDatabase.trackDao().addToFavorites(trackDbConvertor.mapFromTrackToTrackEntity(track))
    }

    override suspend fun deleteFromFavorites(trackId: Int) {
        appDatabase.trackDao().deleteFromFavorites(trackId)
    }

    private fun convertFromTrackEntity(tracks: List<TrackEntity>): List<Track> {
        return tracks.map { track -> trackDbConvertor.mapFromTrackEntityToTrack(track) }
    }

}