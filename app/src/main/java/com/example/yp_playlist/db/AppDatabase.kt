package com.example.yp_playlist.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.yp_playlist.db.dao.PlaylistDao
import com.example.yp_playlist.db.dao.TrackDao
import com.example.yp_playlist.db.entity.PlaylistEntity
import com.example.yp_playlist.db.entity.TrackEntity

@Database(
    version = 2,
    entities = [
        TrackEntity::class, PlaylistEntity::class
    ]
)
public abstract class AppDatabase: RoomDatabase() {
    abstract fun trackDao(): TrackDao
    abstract fun PlaylistDao(): PlaylistDao
}