package com.example.yp_playlist.medialibrary.playlists.data

import android.net.Uri
import com.example.yp_playlist.db.AppDatabase
import com.example.yp_playlist.db.converter.TrackDbConverter
import com.example.yp_playlist.db.entity.PlaylistEntity
import com.example.yp_playlist.domain.Track
import com.example.yp_playlist.medialibrary.playlists.domain.api.PlaylistRepository
import com.example.yp_playlist.medialibrary.playlists.domain.models.LocalStorage
import com.example.yp_playlist.medialibrary.playlists.domain.models.Playlist
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonParseException
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow


class PlaylistRepositoryImpl(
    private val appDatabase: AppDatabase,
    private val trackDbConverter: TrackDbConverter,
    private val localStorage: LocalStorage,
) : PlaylistRepository {

    private val gson: Gson = GsonBuilder().create()

    override suspend fun addPlaylist(playlist: Playlist) {
        appDatabase.PlaylistDao()
            .addPlaylist(trackDbConverter.mapFromPlaylistToPlaylistEntity(playlist))
    }

    override suspend fun deletePlaylist(id: Int) {
        appDatabase.PlaylistDao().deletePlaylist(id)
    }

    override suspend fun getPlaylists(): Flow<List<Playlist>> = flow {
        val playlists = appDatabase.PlaylistDao().getPlaylists()
        emit(convertFromPlaylistEntity(playlists))
    }

    override suspend fun updatePlaylists(playlist: Playlist) {
        appDatabase.PlaylistDao()
            .updatePlaylist(trackDbConverter.mapFromPlaylistToPlaylistEntity(playlist))
    }

    override suspend fun addTrackToPlayList(track: Track, playlist: Playlist): Flow<Boolean> = flow {
        val arrayTrackType = object : TypeToken<List<Track>>() {}.type

        val playlistTracks = try {
            gson.fromJson(playlist.trackList, arrayTrackType) ?: emptyList<Track>()
        } catch (e: JsonParseException) {
            emptyList<Track>()
        }

        val isInPlaylist = playlistTracks.any { it.trackId == track.trackId }

        if (!isInPlaylist) {
            val updatedTracks = playlistTracks.toMutableList().apply {
                add(track)
            }

            playlist.trackList = gson.toJson(updatedTracks)
            playlist.size++
            updatePlaylists(playlist)
            emit(true)
        } else {
            emit(false)
        }
    }

    override suspend fun saveImageToPrivateStorage(uri: Uri) {
        localStorage.saveImageToPrivateStorage(uri)
    }

    private fun convertFromPlaylistEntity(playlists: List<PlaylistEntity>): List<Playlist> {
        return playlists.map { playlistEntity ->
            trackDbConverter.mapFromPlaylistEntityToPlaylist(playlistEntity)
        }
    }
}