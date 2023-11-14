package com.example.yp_playlist.medialibrary.playlists.domain.models

import android.net.Uri

interface LocalStorage {
    suspend fun saveImageToPrivateStorage(uri: Uri)
}