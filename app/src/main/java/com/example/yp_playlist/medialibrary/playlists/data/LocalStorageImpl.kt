package com.example.yp_playlist.medialibrary.playlists.data

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import com.example.yp_playlist.medialibrary.playlists.domain.models.LocalStorage
import com.example.yp_playlist.util.Constants.DIRECTORY
import com.example.yp_playlist.util.Constants.IMAGE_NAME
import com.example.yp_playlist.util.Constants.QUALITY_IMAGE
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

class LocalStorageImpl(val context: Context) : LocalStorage {
    override suspend fun saveImageToPrivateStorage(uri: Uri) {

        val filePath = File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), DIRECTORY)

        if (!filePath.exists()) {
            filePath.mkdirs()
        }
        val file = File(filePath, IMAGE_NAME)
        val inputStream = context.contentResolver.openInputStream(uri)
        val outputStream = withContext(Dispatchers.IO) {
            FileOutputStream(file)
        }

        BitmapFactory
            .decodeStream(inputStream)
            .compress(Bitmap.CompressFormat.JPEG, QUALITY_IMAGE, outputStream)
    }

}