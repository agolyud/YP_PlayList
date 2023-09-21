package com.example.yp_playlist.settings.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.yp_playlist.databinding.FragmentSettingsBinding
import org.koin.androidx.viewmodel.ext.android.viewModel


class SettingsFragment :  Fragment() {

    private lateinit var settingsBinding: FragmentSettingsBinding
    private val viewModel by viewModel<SettingsViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        settingsBinding = FragmentSettingsBinding.inflate(inflater, container, false)
        return settingsBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initListeners()


        viewModel.themeSettingsState.observe(viewLifecycleOwner) { themeSettings ->
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