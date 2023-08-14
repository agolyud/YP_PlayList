package com.example.yp_playlist.sharing.data.impl

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.example.yp_playlist.R
import com.example.yp_playlist.sharing.data.ExternalNavigator
import com.example.yp_playlist.sharing.domain.models.EmailData
import java.lang.Exception

class ExternalNavigatorImpl(private val context: Context) : ExternalNavigator {

    val emailSubject = context.getString(R.string.mailSubject)
    val emailText = context.getString(R.string.supportMessage)

    override fun shareLink(shareAppLink: String) {
        try {
            Intent(Intent.ACTION_SEND).apply {
                putExtra(Intent.EXTRA_TEXT, shareAppLink)
                type = "text/plain"
                val chooserIntent = Intent.createChooser(this, null)
                chooserIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(chooserIntent)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun openLink(termsLink: String) {
        try {
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(termsLink))
            browserIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(browserIntent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun openEmail(supportEmailData: EmailData) {
        try {
            Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:")
                putExtra(Intent.EXTRA_EMAIL, arrayOf(supportEmailData.mail))
                putExtra(Intent.EXTRA_SUBJECT, emailSubject)
                putExtra(Intent.EXTRA_TEXT, emailText)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(this)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}