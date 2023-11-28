package com.example.yp_playlist.sharing.data.impl

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import com.example.yp_playlist.R
import com.example.yp_playlist.sharing.data.ExternalNavigator
import com.example.yp_playlist.sharing.domain.models.EmailData

class ExternalNavigatorImpl(private val context: Context) : ExternalNavigator {

    private val emailSubject = context.getString(R.string.mailSubject)
    private val emailText = context.getString(R.string.supportMessage)
    private val errorSharingLink = context.getString(R.string.errorSharingLink)
    private val errorOpeningLink = context.getString(R.string.errorOpeningLink)
    private val errorOpeningEmail = context.getString(R.string.errorOpeningEmail)
    private val sharingApp = context.getString(R.string.sharingApp)

    private fun showErrorToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

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
            showErrorToast(errorSharingLink)
        }
    }

    override fun openLink(termsLink: String) {
        try {
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(termsLink))
            browserIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(browserIntent)
        } catch (e: Exception) {
            e.printStackTrace()
            showErrorToast(errorOpeningLink)
        }
    }

    override fun sharePlaylist(playlist: String) {
        val sendIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, playlist)
            type = "text/plain"
        }

        val shareIntent = Intent.createChooser(sendIntent, sharingApp)
        context.startActivity(shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
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
            showErrorToast(errorOpeningEmail)
        }
    }
}