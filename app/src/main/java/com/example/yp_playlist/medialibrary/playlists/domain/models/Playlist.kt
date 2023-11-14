package com.example.yp_playlist.medialibrary.playlists.domain.models

import android.net.Uri

data class Playlist(
    val id: Long = 0,
    val title: String,
    val description: String,
    val imageUri: Uri? = null,
    var trackList: String? = null,
    var size: Int? = null
)
