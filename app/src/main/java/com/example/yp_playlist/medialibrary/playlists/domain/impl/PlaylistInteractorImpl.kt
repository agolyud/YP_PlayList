package com.example.yp_playlist.medialibrary.playlists.domain.impl

import android.net.Uri
import com.example.yp_playlist.db.entity.PlaylistEntity
import com.example.yp_playlist.domain.Track
import com.example.yp_playlist.medialibrary.playlists.domain.api.PlaylistInteractor
import com.example.yp_playlist.medialibrary.playlists.domain.api.PlaylistRepository
import com.example.yp_playlist.medialibrary.playlists.domain.models.Playlist
import kotlinx.coroutines.flow.Flow


class PlaylistInteractorImpl(private val playlistRepository: PlaylistRepository) :
    PlaylistInteractor {

    override suspend fun addPlaylist(playlist: Playlist) {
        playlistRepository.addPlaylist(playlist)
    }

    override suspend fun deletePlaylist(id: Int) {
        playlistRepository.deletePlaylist(id)
    }

    override suspend fun getPlaylists(): Flow<List<Playlist>> {
        return playlistRepository.getPlaylists()
    }

    override suspend fun updatePlaylists(playlist: Playlist) {
        playlistRepository.updatePlaylists(playlist)
    }

    override suspend fun addTrackToPlayList(track: Track, playlist: Playlist): Flow<Boolean> =
        playlistRepository.addTrackToPlayList(track, playlist)

    override suspend fun saveImageToPrivateStorage(uri: Uri) {
        playlistRepository.saveImageToPrivateStorage(uri)
    }
}