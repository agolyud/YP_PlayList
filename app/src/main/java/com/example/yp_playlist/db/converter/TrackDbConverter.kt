package com.example.yp_playlist.db.converter

import androidx.core.net.toUri
import com.example.yp_playlist.db.entity.PlaylistEntity
import com.example.yp_playlist.db.entity.TrackEntity
import com.example.yp_playlist.domain.Track
import com.example.yp_playlist.medialibrary.playlists.domain.models.Playlist
import java.util.Calendar

class TrackDbConverter {

    fun mapFromTrackEntityToTrack(from: TrackEntity): Track {
        return Track(
            trackId = from.trackId,
            trackName = from.trackName,
            artistName = from.artistName,
            trackTimeMillis = from.trackTimeMillis,
            artworkUrl100 = from.artworkUrl100,
            collectionName = from.collectionName,
            releaseDate = from.releaseDate,
            primaryGenreName = from.primaryGenreName,
            country = from.country,
            previewUrl = from.previewUrl,
        )
    }

    fun mapFromTrackToTrackEntity(from: Track): TrackEntity {
        return TrackEntity(
            trackId = from.trackId,
            trackName = from.trackName,
            artistName = from.artistName,
            trackTimeMillis = from.trackTimeMillis,
            artworkUrl100 = from.artworkUrl100,
            collectionName = from.collectionName,
            releaseDate = from.releaseDate,
            primaryGenreName = from.primaryGenreName,
            country = from.country,
            previewUrl = from.previewUrl,
            Calendar.getInstance().timeInMillis
        )
    }

    fun mapFromPlaylistEntityToPlaylist(from: PlaylistEntity): Playlist {
        return Playlist(
            id = from.id,
            title = from.title,
            description = from.description,
            imageUri = from.imageUri.toUri(),
            trackList = from.trackList,
            size = from.size
        )
    }

    fun mapFromPlaylistToPlaylistEntity(from: Playlist): PlaylistEntity {
        return PlaylistEntity(
            id = from.id,
            title = from.title,
            description = from.description,
            imageUri = from.imageUri.toString(),
            trackList = from.trackList,
            size = from.size
        )
    }
}