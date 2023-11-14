package com.example.yp_playlist.medialibrary.playlists.ui.models

import com.example.yp_playlist.medialibrary.playlists.domain.models.Playlist

sealed class PlaylistsScreenState(playlists: List<Playlist>?) {
    object Empty : PlaylistsScreenState(null)
    class Filled(val playlists: List<Playlist>) : PlaylistsScreenState(playlists)
}