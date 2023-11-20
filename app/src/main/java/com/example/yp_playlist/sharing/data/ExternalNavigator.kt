package com.example.yp_playlist.sharing.data

import com.example.yp_playlist.sharing.domain.models.EmailData

interface ExternalNavigator {
    fun shareLink(shareAppLink:String)
    fun openLink(termsLink:String)
    fun openEmail(supportEmailData: EmailData)
    fun sharePlaylist(playlist: String)
}