package com.example.yp_playlist.medialibrary.domain.impl

import com.example.yp_playlist.domain.Track
import com.example.yp_playlist.medialibrary.domain.api.FavouriteTracksInteractor
import com.example.yp_playlist.medialibrary.domain.api.FavouriteTracksRepository
import kotlinx.coroutines.flow.Flow

class FavouriteTracksInteractorImpl(
    private val favouriteTracksRepository: FavouriteTracksRepository
) : FavouriteTracksInteractor {

    override suspend fun getFavoritesTracks(): Flow<List<Track>> {
        return favouriteTracksRepository.getFavoritesTracks()
    }

    override suspend fun isFavoriteTrack(trackId: Int): Flow<Boolean> {
        return favouriteTracksRepository.isFavoriteTrack(trackId)
    }

    override suspend fun addToFavorites(track: Track) {
        favouriteTracksRepository.addToFavorites(track)
    }

    override suspend fun deleteFromFavorites(trackId: Int) {
        favouriteTracksRepository.deleteFromFavorites(trackId)
    }
}