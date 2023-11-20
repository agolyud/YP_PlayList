package com.example.yp_playlist.medialibrary.playlists.data

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Environment
import androidx.core.net.toUri
import com.example.yp_playlist.medialibrary.playlists.domain.models.LocalStorage
import com.example.yp_playlist.util.Constants.DIRECTORY
import com.example.yp_playlist.util.Constants.IMAGE_NAME
import com.example.yp_playlist.util.Constants.LAST_PLAYLIST_KEY
import com.example.yp_playlist.util.Constants.QUALITY_IMAGE
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

class LocalStorageImpl(val context: Context,
                       private val sharedPreferences: SharedPreferences
) : LocalStorage {
    override suspend fun saveImageToPrivateStorage(uri: String) {

        val filePath = File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), DIRECTORY)

        if (!filePath.exists()) {
            filePath.mkdirs()
        }
        val file = File(filePath, IMAGE_NAME)
        val inputStream = context.contentResolver.openInputStream(uri.toUri())
        val outputStream = withContext(Dispatchers.IO) {
            FileOutputStream(file)
        }

        BitmapFactory
            .decodeStream(inputStream)
            .compress(Bitmap.CompressFormat.JPEG, QUALITY_IMAGE, outputStream)
    }

    override fun saveCurrentPlaylistId(id: Long) {
        sharedPreferences.edit().remove(LAST_PLAYLIST_KEY).apply()
        sharedPreferences.edit().putLong(LAST_PLAYLIST_KEY, id).apply()
    }

    override fun getCurrentPlaylistId(): Long {
        return sharedPreferences.getLong(LAST_PLAYLIST_KEY, 0)
    }


}