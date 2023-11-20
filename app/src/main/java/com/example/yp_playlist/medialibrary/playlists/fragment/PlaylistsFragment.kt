package com.example.yp_playlist.medialibrary.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.yp_playlist.R
import com.example.yp_playlist.databinding.FragmentPlaylistBinding
import com.example.yp_playlist.medialibrary.playlists.domain.models.Playlist
import com.example.yp_playlist.medialibrary.playlists.ui.SpacingItemDecorator
import com.example.yp_playlist.medialibrary.playlists.ui.adapter.ViewObjects
import com.example.yp_playlist.medialibrary.playlists.ui.models.PlaylistsScreenState
import com.example.yp_playlist.medialibrary.playlists.ui.viewmodel.PlaylistsViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlaylistFragment : Fragment() {

    private var _binding: FragmentPlaylistBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModel<PlaylistsViewModel>()

    private val playlistsAdapter = ViewObjects.PlaylistsAdapter(viewObject = ViewObjects.Horizontal)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlaylistBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonNewPlaylist.setOnClickListener {
            findNavController().navigate(R.id.newPlaylistFragment)
        }

        playlistsAdapter.onPlayListClicked = { playlist ->
            viewModel.saveCurrentPlaylistId(playlist.id)
            findNavController().navigate(R.id.action_mediaLibraryFragment_to_openPlaylistFragment)
        }

        viewModel.fillData()
        binding.playlistsGrid.adapter = playlistsAdapter
        binding.playlistsGrid.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.playlistsGrid.setHasFixedSize(true)
        binding.playlistsGrid.addItemDecoration(SpacingItemDecorator(40, 0))

        viewModel.stateLiveData.observe(viewLifecycleOwner) {
            render()
        }

    }

    private fun render() {
        viewModel.stateLiveData.observe(viewLifecycleOwner) { state ->
            when (state) {
                is PlaylistsScreenState.Filled -> {
                    showPlaylists(state.playlists)
                }

                is PlaylistsScreenState.Empty -> {
                    binding.playlistsGrid.visibility = View.GONE
                    binding.nothingFound.visibility = View.VISIBLE
                }

                else -> {}
            }
        }
    }

    private fun showPlaylists(playlists: List<Playlist>) {
        playlistsAdapter.playlists = playlists as ArrayList<Playlist>
        binding.playlistsGrid.visibility = View.VISIBLE
        binding.nothingFound.visibility = View.GONE
    }

    override fun onResume() {
        super.onResume()
        viewModel.fillData()
    }

    companion object {
        fun newInstance() = PlaylistFragment().apply {}
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}