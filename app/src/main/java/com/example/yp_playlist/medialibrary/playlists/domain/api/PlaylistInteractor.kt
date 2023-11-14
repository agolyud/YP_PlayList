package com.example.yp_playlist.medialibrary.playlists.domain.api

import android.net.Uri
import com.example.yp_playlist.domain.Track
import com.example.yp_playlist.medialibrary.playlists.domain.models.Playlist
import kotlinx.coroutines.flow.Flow

interface PlaylistInteractor {

    suspend fun addPlaylist(title : String, description: String, imageUri: Uri?)
    suspend fun deletePlaylist(id: Int)
    suspend fun getPlaylists(): Flow<List<Playlist>>
    suspend fun updatePlaylists(playlist: Playlist)
    suspend fun addTrackToPlayList(track: Track, playlist: Playlist): Flow<Boolean>
    suspend fun saveImageToPrivateStorage(uri: Uri)
}