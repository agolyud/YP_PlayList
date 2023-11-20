package com.example.yp_playlist.medialibrary.playlists.domain.models

interface LocalStorage {
    suspend fun saveImageToPrivateStorage(uri: String)
    fun saveCurrentPlaylistId(id: Long)
    fun getCurrentPlaylistId(): Long
}