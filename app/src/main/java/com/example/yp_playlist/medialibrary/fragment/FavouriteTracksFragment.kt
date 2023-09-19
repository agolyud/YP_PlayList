package com.example.yp_playlist.medialibrary.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import org.koin.androidx.viewmodel.ext.android.activityViewModel
import com.example.yp_playlist.databinding.FragmentFavoriteTrackBinding
import com.example.yp_playlist.medialibrary.viewmodel.FavouriteTracksViewModel

class FavouriteTracksFragment : Fragment() {

    private lateinit var binding: FragmentFavoriteTrackBinding
    private val viewModel by activityViewModel<FavouriteTracksViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFavoriteTrackBinding.inflate(inflater, container, false)
        return binding.root
    }

    companion object {
        fun newInstance() = FavouriteTracksFragment()
    }
}