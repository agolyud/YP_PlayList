package com.example.yp_playlist.medialibrary.favourite.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.yp_playlist.R
import com.example.yp_playlist.databinding.FragmentFavoriteTrackBinding
import com.example.yp_playlist.domain.Track
import com.example.yp_playlist.medialibrary.favourite.ui.models.FavouriteTracksState
import com.example.yp_playlist.medialibrary.favourite.ui.viewmodel.FavouriteTracksViewModel
import com.example.yp_playlist.presentation.media.MediaFragment
import com.example.yp_playlist.presentation.search.TrackAdapter
import com.example.yp_playlist.util.Constants
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class FavouriteTracksFragment : Fragment() {

    private lateinit var binding: FragmentFavoriteTrackBinding
    private val viewModel by viewModel<FavouriteTracksViewModel>()

    private val favoritesTracksAdapter = TrackAdapter()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentFavoriteTrackBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.favouriteTracksRecycler.adapter = favoritesTracksAdapter

        viewModel.getFavouriteTracks()

        viewModel.observeState().observe(viewLifecycleOwner) {
            render(it)
        }

        showPlayer()

    }

    private fun render(state: FavouriteTracksState) {
        when (state) {
            is FavouriteTracksState.Content -> {
                favoritesTracksAdapter.updateTracks(state.tracks)
                favoritesTracksAdapter.tracks = state.tracks as MutableList<Track>
                binding.favouriteTracksRecycler.visibility = View.VISIBLE
                binding.nothingFound.visibility = View.GONE
            }

            is FavouriteTracksState.Empty -> {
                binding.favouriteTracksRecycler.visibility = View.GONE
                binding.nothingFound.visibility = View.VISIBLE
            }
            else -> {}
        }
    }

    private fun showPlayer() {
        favoritesTracksAdapter.itemClickListener = { position, track ->
            lifecycleScope.launch {
                delay(Constants.DELAY_TIME_MILLIS)
                sendToPlayer(track)
            }
        }
    }

    private fun sendToPlayer(track: Track) {
        findNavController().navigate(
            R.id.action_mediaLibraryFragment_to_playerFragment,
            MediaFragment.createArgs(track.trackId)
        )
    }

    override fun onResume() {
        super.onResume()
        viewModel.getFavouriteTracks()
    }

    companion object {
        fun newInstance() = FavouriteTracksFragment()
    }
}