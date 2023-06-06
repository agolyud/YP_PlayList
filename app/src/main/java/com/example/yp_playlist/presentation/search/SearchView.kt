package com.example.yp_playlist.presentation.search

import com.example.yp_playlist.domain.Track

interface SearchView {
    fun updateTracks()
    fun error()
    fun updateTracks(tracks: List<Track>)
    fun nothingWasFound()
    fun showTracks()
}