package com.example.yp_playlist.medialibrary.playlists.data

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
    private val gson: Gson
) : PlaylistRepository {

    override suspend fun addPlaylist(title: String, description: String, imageUri: String?) {
        val playlistEntity = PlaylistEntity(
            title = title,
            description = description,
            imageUri = imageUri.toString()
        )

        appDatabase.PlaylistDao().addPlaylist(playlistEntity)
    }
    
   override suspend fun deletePlaylist(id: Long) {
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
            playlist.size = (playlist.size ?: 0) + 1
            updatePlaylists(playlist)
            emit(true)
        } else {
            emit(false)
        }
    }
    override suspend fun deleteTrackFromPlaylist(track: Track, playlist: Playlist): Flow<Boolean> = flow {
        val gson = GsonBuilder().create()
        val arrayTrackType = object : TypeToken<ArrayList<Track>>() {}.type

        val playlistTracks =
            gson.fromJson(playlist.trackList, arrayTrackType) ?: arrayListOf<Track>()

        var isInPlaylist = false

        playlistTracks.forEach {
            if (it.trackId == track.trackId) {
                isInPlaylist = true
            }
        }

        if (isInPlaylist) {
            playlistTracks.remove(track)
            playlist.trackList = gson.toJson(playlistTracks)

            playlist.size = (playlist.size ?: 0) - 1
            updatePlaylists(playlist)

            emit(true)
        } else {
            emit(false)
        }
    }

    override suspend fun saveImageToPrivateStorage(uri: String) {
        localStorage.saveImageToPrivateStorage(uri)
    }

    override suspend fun getPlaylistById(id: Long): Flow<Playlist> = flow {
        emit(trackDbConverter.mapFromPlaylistEntityToPlaylist(appDatabase.PlaylistDao().getPlaylistById(id)))
    }

    override suspend fun getTracksFromPlaylist(id: Long): Flow<List<Track>> = flow {
        val gson = GsonBuilder().create()
        val listTrackType = object : TypeToken<MutableList<Track>>() {}.type

        val tracksString = appDatabase.PlaylistDao().getTracksFromPlaylist(id)
        val tracks = gson.fromJson(tracksString, listTrackType) ?: mutableListOf<Track>()

        emit(tracks)
    }

    override suspend fun saveCurrentPlaylistId(id: Long) {
        localStorage.saveCurrentPlaylistId(id)
    }

    override suspend fun getCurrentPlaylistId(): Long {
        return localStorage.getCurrentPlaylistId()
    }

    private fun convertFromPlaylistEntity(playlists: List<PlaylistEntity>): List<Playlist> {
        return playlists.map { playlists ->
            trackDbConverter.mapFromPlaylistEntityToPlaylist(
                playlists
            )
        }
    }
}