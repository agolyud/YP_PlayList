package com.example.yp_playlist.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.yp_playlist.db.entity.PlaylistEntity

@Dao
interface PlaylistDao {

    @Insert(entity = PlaylistEntity::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun addPlaylist(playlistEntity: PlaylistEntity)

    @Query("DELETE FROM ${PlaylistEntity.TABLE_NAME} WHERE id = :id")
    suspend fun deletePlaylist(id: Long)

    @Query("SELECT * FROM ${PlaylistEntity.TABLE_NAME} ORDER BY id DESC")
    suspend fun getPlaylists(): List<PlaylistEntity>

    @Update
    suspend fun updatePlaylist(playlistEntity: PlaylistEntity)

    @Query("SELECT * FROM ${PlaylistEntity.TABLE_NAME} WHERE id = :id")
    suspend fun getPlaylistById(id: Long): PlaylistEntity

    @Query("SELECT trackList FROM ${PlaylistEntity.TABLE_NAME} WHERE id = :id")
    suspend fun getTracksFromPlaylist(id: Long): String
}