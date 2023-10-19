package com.example.yp_playlist.db.converter

import com.example.yp_playlist.db.entity.TrackEntity
import com.example.yp_playlist.domain.Track
import java.util.Calendar

class TrackDbConverter {

    fun mapFromTrackEntityToTrack(from: TrackEntity): Track {
        return with(from) {
            Track(
                trackId = trackId,
                trackName = trackName,
                artistName = artistName,
                trackTimeMillis = trackTimeMillis,
                artworkUrl100 = artworkUrl100,
                collectionName = collectionName,
                releaseDate = releaseDate,
                primaryGenreName = primaryGenreName,
                country = country,
                previewUrl = previewUrl
            )
        }
    }

    fun mapFromTrackToTrackEntity(from: Track): TrackEntity {
        return with(from) {
            TrackEntity(
                trackId = trackId,
                trackName = trackName,
                artistName = artistName,
                trackTimeMillis = trackTimeMillis,
                artworkUrl100 = artworkUrl100,
                collectionName = collectionName,
                releaseDate = releaseDate,
                primaryGenreName = primaryGenreName,
                country = country,
                previewUrl = previewUrl,
                Calendar.getInstance().timeInMillis
            )
        }
    }
}