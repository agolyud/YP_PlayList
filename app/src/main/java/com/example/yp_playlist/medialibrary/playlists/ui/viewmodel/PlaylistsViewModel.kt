package com.example.yp_playlist.medialibrary.playlists.ui.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.yp_playlist.medialibrary.playlists.domain.api.PlaylistInteractor
import com.example.yp_playlist.medialibrary.playlists.domain.models.Playlist
import com.example.yp_playlist.medialibrary.playlists.ui.models.PlaylistsScreenState
import kotlinx.coroutines.launch

class PlaylistsViewModel(private val playlistInteractor: PlaylistInteractor): ViewModel() {

    private val _stateLiveData = MutableLiveData<PlaylistsScreenState>()
    val stateLiveData: LiveData<PlaylistsScreenState> = _stateLiveData

    init {
        _stateLiveData.postValue(PlaylistsScreenState.Empty)
        fillData()
    }

    fun fillData() {
        viewModelScope.launch {
            playlistInteractor.getPlaylists().collect {
                processResult(it)
            }
        }
    }

    fun saveCurrentPlaylistId(id: Long) {
        viewModelScope.launch {
            playlistInteractor.saveCurrentPlaylistId(id)
        }
    }

    private fun processResult(playlists: List<Playlist>) {
        if (playlists.isEmpty()) {
            _stateLiveData.postValue(PlaylistsScreenState.Empty)
            Log.d("PlaylistsViewModel", "No playlists found")
        } else {
            _stateLiveData.postValue(PlaylistsScreenState.Filled(playlists))
            Log.d("PlaylistsViewModel", "Playlists found: ${playlists.size}")
        }
    }
}
