package com.example.yp_playlist.medialibrary.playlists.domain.api

import android.net.Uri
import com.example.yp_playlist.domain.Track
import com.example.yp_playlist.medialibrary.playlists.domain.models.Playlist
import kotlinx.coroutines.flow.Flow


interface PlaylistRepository {

    suspend fun addPlaylist(playlist: Playlist)
    suspend fun deletePlaylist(id: Int)
    suspend fun getPlaylists(): Flow<List<Playlist>>
    suspend fun updatePlaylists(playlist: Playlist)
    suspend fun addTrackToPlayList(track: Track, playlist: Playlist): Flow<Boolean>
    suspend fun saveImageToPrivateStorage(uri: Uri)
}