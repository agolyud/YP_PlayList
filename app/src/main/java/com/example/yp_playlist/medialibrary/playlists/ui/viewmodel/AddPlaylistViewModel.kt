package com.example.yp_playlist.medialibrary.playlists.ui.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.yp_playlist.medialibrary.playlists.domain.api.PlaylistInteractor
import com.example.yp_playlist.medialibrary.playlists.domain.models.Playlist
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AddPlaylistViewModel(private val playlistInteractor: PlaylistInteractor) : ViewModel() {

    fun savePlaylist(playlist: Playlist) {
        viewModelScope.launch {
            playlistInteractor.addPlaylist(playlist)
        }
    }

    fun saveToLocalStorage(uri: Uri) {
        viewModelScope.launch(Dispatchers.IO) {
            playlistInteractor.saveImageToPrivateStorage(uri)
        }
    }
}