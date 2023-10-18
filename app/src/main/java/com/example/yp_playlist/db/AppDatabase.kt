package com.example.yp_playlist.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.yp_playlist.db.dao.TrackDao
import com.example.yp_playlist.db.entity.TrackEntity

@Database(
    version = 1,
    entities = [
        TrackEntity::class
    ]
)
abstract class AppDatabase: RoomDatabase() {
    abstract fun TrackDao(): TrackDao
}