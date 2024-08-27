package com.example.yp_playlist.medialibrary.ui.fragment


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import com.example.yp_playlist.R
import androidx.navigation.findNavController
import com.example.yp_playlist.medialibrary.favourite.ui.viewmodel.FavouriteTracksViewModel
import com.example.yp_playlist.medialibrary.playlists.ui.viewmodel.PlaylistsViewModel
import com.example.yp_playlist.medialibrary.screens.MediaLibraryScreen
import com.example.yp_playlist.presentation.media.MediaFragment
import com.example.yp_playlist.settings.ui.SettingsViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel


class MediaLibraryFragment : Fragment() {

    private val favouriteTracksViewModel by viewModel<FavouriteTracksViewModel>()
    private val playlistsViewModel by viewModel<PlaylistsViewModel>()
    private val settingsViewModel by viewModel<SettingsViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                val navController = findNavController()
                MediaLibraryScreen(
                    settingsViewModel = settingsViewModel,
                    favouriteTracksViewModel = favouriteTracksViewModel,
                    playlistsViewModel = playlistsViewModel,
                    navController = navController,
                    onTrackClick = { track ->
                        navController.navigate(
                            R.id.action_mediaLibraryFragment_to_playerFragment,
                            MediaFragment.createArgs(track.trackId)
                        )
                    }
                )
            }
        }
    }

    override fun onResume() {
        super.onResume()
        favouriteTracksViewModel.getFavouriteTracks()
        playlistsViewModel.fillData()
    }

}




