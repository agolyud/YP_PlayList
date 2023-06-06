package com.example.yp_playlist.presentation

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Switch
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.example.yp_playlist.R

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val homeButton = findViewById<Button>(R.id.settings_toolbar)
        homeButton.setOnClickListener {
            finish()
        }

        findViewById<Switch>(R.id.switch_compat).isChecked =
            AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES

        findViewById<Switch>(R.id.switch_compat).setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        }

        val shareButton = findViewById<LinearLayout>(R.id.Share)
        shareButton.setOnClickListener {
            val androidDevelopment = getString(R.string.android_development_course)
            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.type = Uri.parse("text/plain").toString()
            shareIntent.putExtra(Intent.EXTRA_TEXT, androidDevelopment)
            val chooserIntent = Intent.createChooser(shareIntent, null)
            startActivity(chooserIntent)
        }

        val supportButton = findViewById<LinearLayout>(R.id.Support)
        supportButton.setOnClickListener {

            Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:")
                putExtra(Intent.EXTRA_EMAIL, arrayOf(getString(R.string.supportMail)))
                putExtra(Intent.EXTRA_SUBJECT, getString(R.string.mailSubject))
                putExtra(Intent.EXTRA_TEXT, getString(R.string.supportMessage))
                startActivity(this)
            }
        }


            val agreementButton = findViewById<LinearLayout>(R.id.Agreement)
            agreementButton.setOnClickListener {
                val openPage = Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.agreement)))
                startActivity(openPage)
            }


        }

    }


