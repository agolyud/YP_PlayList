package com.example.yp_playlist.medialibrary.playlists.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.yp_playlist.medialibrary.playlists.domain.api.PlaylistInteractor
import com.example.yp_playlist.medialibrary.playlists.domain.models.Playlist
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AddPlaylistViewModel(private val playlistInteractor: PlaylistInteractor) : ViewModel() {

    fun savePlaylist(title: String, description: String, imageUri: String?) {
        viewModelScope.launch {
            playlistInteractor.addPlaylist(title, description, imageUri)
        }
    }

    fun saveToLocalStorage(uri: String) {
        viewModelScope.launch(Dispatchers.IO) {
            playlistInteractor.saveImageToPrivateStorage(uri)
        }
    }

    fun updatePlaylist(playlist: Playlist) {
        viewModelScope.launch {
            playlistInteractor.updatePlaylists(playlist)
        }
    }
}