package com.example.yp_playlist.medialibrary.playlists.domain.api

import com.example.yp_playlist.domain.Track
import com.example.yp_playlist.medialibrary.playlists.domain.models.Playlist
import kotlinx.coroutines.flow.Flow


interface PlaylistRepository {

    suspend fun addPlaylist(title: String, description: String, imageUri: String?)
    suspend fun deletePlaylist(id: Long)
    suspend fun getPlaylists(): Flow<List<Playlist>>
    suspend fun updatePlaylists(playlist: Playlist)
    suspend fun addTrackToPlayList(track: Track, playlist: Playlist): Flow<Boolean>
    suspend fun saveImageToPrivateStorage(uri: String)
    suspend fun getPlaylistById(id: Long): Flow<Playlist>
    suspend fun getTracksFromPlaylist(id: Long): Flow<List<Track>>
    suspend fun saveCurrentPlaylistId(id: Long)
    suspend fun getCurrentPlaylistId(): Long
    suspend fun deleteTrackFromPlaylist(track: Track, playlist: Playlist): Flow<Boolean>


}