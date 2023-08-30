package com.example.yp_playlist.media_library.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.yp_playlist.media_library.viewmodel.FavouriteTracksViewModel
import org.koin.androidx.viewmodel.ext.android.activityViewModel
import com.example.yp_playlist.databinding.FragmentPlaylistBinding


class PlaylistsFragment : Fragment() {

    private lateinit var binding: FragmentPlaylistBinding
    private val viewModel by activityViewModel<FavouriteTracksViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPlaylistBinding.inflate(inflater, container, false)
        return binding.root
    }

    companion object {
        fun newInstance() = PlaylistsFragment().apply {}
    }
}