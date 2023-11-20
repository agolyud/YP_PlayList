package com.example.yp_playlist.sharing

interface  SharingInteractor {
    fun shareApp()
    fun openTerms()
    fun openSupport()
    fun sharePlaylist(playlist: String)
}