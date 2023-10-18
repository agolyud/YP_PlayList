package com.example.yp_playlist.medialibrary.ui.models

import com.example.yp_playlist.domain.Track

sealed interface FavouriteTracksState {

    data class Content(
        val tracks: List<Track>
    ) : FavouriteTracksState

    object Empty : FavouriteTracksState
}