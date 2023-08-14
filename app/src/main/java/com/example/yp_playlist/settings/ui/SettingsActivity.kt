package com.example.yp_playlist.settings.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

import com.example.yp_playlist.databinding.ActivitySettingsBinding

import org.koin.androidx.viewmodel.ext.android.viewModel


class SettingsActivity : AppCompatActivity() {

    private lateinit var settingsBinding: ActivitySettingsBinding

    private val viewModel by viewModel<SettingsViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        settingsBinding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(settingsBinding.root)

        settingsBinding.settingsToolbar.setOnClickListener {
            finish()
        }

        initListeners()


        viewModel.themeSettingsState.observe(this) { themeSettings ->
            settingsBinding.switchCompat.isChecked = themeSettings.darkTheme
        }
    }

    private fun initListeners() {
        settingsBinding.Share.setOnClickListener {
            viewModel.shareApp()
        }
        settingsBinding.Support.setOnClickListener {
            viewModel.supportEmail()
        }
        settingsBinding.Agreement.setOnClickListener {
            viewModel.openAgreement()
        }
        settingsBinding.switchCompat.setOnCheckedChangeListener { _, checked ->
            viewModel.switchTheme(checked)
        }
    }
}