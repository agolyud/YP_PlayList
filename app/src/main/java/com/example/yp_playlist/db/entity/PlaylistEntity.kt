package com.example.yp_playlist.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.yp_playlist.db.entity.PlaylistEntity.Companion.TABLE_NAME


@Entity(tableName = TABLE_NAME)
data class PlaylistEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val description: String,
    val imageUri: String? = null,
    val trackList: String? = null,
    val size: Int? = null
) {

    companion object {
        const val TABLE_NAME = "playlist_table"
    }
}