package com.example.yp_playlist.presentation.search

import androidx.fragment.app.Fragment
import androidx.compose.ui.platform.ComposeView
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.yp_playlist.R
import com.example.yp_playlist.presentation.media.MediaFragment
import com.example.yp_playlist.presentation.search.ui.SearchScreen
import com.example.yp_playlist.settings.ui.SettingsViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchFragment : Fragment() {

    private val searchViewModel by viewModel<SearchViewModel>()
    private val settingsViewModel by viewModel<SettingsViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                SearchScreen(
                    searchViewModel = searchViewModel,
                    settingsViewModel = settingsViewModel,
                    onTrackClick = { track ->
                        findNavController().navigate(
                            R.id.searchFragment_to_playerFragment,
                            MediaFragment.createArgs(track.trackId)
                        )
                    }
                )
            }
        }
    }
}


