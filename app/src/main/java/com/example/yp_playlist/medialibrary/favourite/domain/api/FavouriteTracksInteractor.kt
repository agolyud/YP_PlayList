package com.example.yp_playlist.medialibrary.favourite.domain.api

import com.example.yp_playlist.domain.Track
import kotlinx.coroutines.flow.Flow

interface FavouriteTracksInteractor {

    suspend fun getFavoritesTracks(): Flow<List<Track>>

    suspend fun isFavoriteTrack(trackId: Int): Flow<Boolean>

    suspend fun addToFavorites(track: Track)

    suspend fun deleteFromFavorites(trackId: Int)
}