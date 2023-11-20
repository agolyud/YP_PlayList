package com.example.yp_playlist.medialibrary.playlists.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.yp_playlist.R
import com.example.yp_playlist.databinding.FragmentOpenPlaylistBinding
import com.example.yp_playlist.domain.Track
import com.example.yp_playlist.medialibrary.playlists.domain.models.Playlist
import com.example.yp_playlist.medialibrary.playlists.ui.adapter.ViewObjects
import com.example.yp_playlist.medialibrary.playlists.ui.viewmodel.OpenPlaylistViewModel
import com.example.yp_playlist.presentation.media.MediaFragment
import com.example.yp_playlist.presentation.search.TrackAdapter
import com.example.yp_playlist.util.Constants
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class OpenPlaylistFragment : Fragment() {

    private var _binding: FragmentOpenPlaylistBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModel<OpenPlaylistViewModel>()
    var playlist: Playlist? = null

    private lateinit var confirmDialogDeleteTrack: MaterialAlertDialogBuilder

    private val tracksAdapter = TrackAdapter()


    private val bottomSheetBehavior
        get() = BottomSheetBehavior.from(binding.bottomSheetSharing).apply {
            state = BottomSheetBehavior.STATE_HIDDEN
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOpenPlaylistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initAdapters()
        initListeners()
        initObservers()
        initBottomSheet()
        showPlayer()
        playlistDelete ()
        deleteTracks()
        viewModel.getPlaylist()
    }

    private fun initBottomSheet() {
        bottomSheetBehavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {

                    BottomSheetBehavior.STATE_HIDDEN -> {
                        binding.overlay.isVisible = false
                        binding.bottomSheetLinear.isVisible = true
                    }

                    else -> {
                        binding.overlay.isVisible = true
                        binding.bottomSheetLinear.isVisible = true
                    }
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {}
        })
    }

    private fun initListeners() {
        binding.toolbarOpenPlaylist.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.share.setOnClickListener {
            sharingPlaylist()
        }

        binding.playlistTracksRv.setOnClickListener {
            sharingPlaylist()
        }
        binding.sharePlaylistTv.setOnClickListener {
            sharingPlaylist()
        }
        binding.more.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        }
        binding.editInformationTv.setOnClickListener {
            findNavController().navigate(
                R.id.action_openPlaylistFragment_to_editPlaylistFragment,
                EditPlaylistFragment.createArgs(playlist!!)

            )
        }
    }

    private fun initAdapters() {
        binding.playlistTracksRv.adapter = tracksAdapter
    }


    private fun sharingPlaylist() {
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        if (viewModel.isEmptyTracks()) {
            Toast.makeText(
                requireContext(),
                R.string.no_tracks_in_playlist,
                Toast.LENGTH_SHORT
            ).show()
        } else {
            val sharePlaylist = viewModel.sharePlaylist(resources)
            viewModel.shareTracks(sharePlaylist)
        }
    }

    private fun initObservers() {
        viewModel.playlist.observe(viewLifecycleOwner) { playlist ->
            showPlaylist(playlist)
            this.playlist = playlist
            if (playlist?.description == "") {
                binding.descriptionPlaylist.isVisible = false
            }
            playlist?.id?.let { playlistId -> viewModel.getTracks(playlistId) }
        }
        viewModel.tracks.observe(viewLifecycleOwner) { tracks ->
            if (tracks.isEmpty()) {
                tracksAdapter.tracks = arrayListOf()
                binding.textNotFound.isVisible = true
            } else {
                tracksAdapter.tracks = tracks as ArrayList<Track>
                binding.totalTime.text = resources.getQuantityString(
                    R.plurals.minutes,
                    tracksAdapter.tracks.sumOf { it.trackTimeMillis }.formatAsMinutes().toInt(),
                    tracksAdapter.tracks.sumOf { it.trackTimeMillis }.formatAsMinutes()
                )
            }
            tracksAdapter.notifyDataSetChanged()
        }

        viewModel.getPlaylist()
    }

    private fun showPlaylist(playlist: Playlist) {

        with(binding) {
            val quantityTracks = playlist.size
            playlistSize.text = resources.getQuantityString(R.plurals.tracks,
                quantityTracks ?: 0, quantityTracks ?: 0)

         Glide.with(requireContext())
                .load(playlist.imageUri)
                .placeholder(R.drawable.placeholder)
                .centerCrop()
                .transform(RoundedCorners(resources.getDimensionPixelSize(R.dimen.button_margins)))
                .into(trackPlaylistImage)

            Glide.with(requireContext())
                .load(playlist.imageUri)
                .placeholder(R.drawable.placeholder)
                .centerCrop()
                .transform(RoundedCorners(resources.getDimensionPixelSize(R.dimen.button_margins)))
                .into(playlistImageBottomSheet)

            binding.namePlaylist.text = playlist.title
            binding.descriptionPlaylist.text = playlist.description
            binding.playlistTitleBottomSheet.text = playlist.title


            totalTime.text = resources.getQuantityString(R.plurals.minutes,
                playlist.size ?: 0, playlist.size ?: 0)
            playlistSizeBottomSheet.text = resources.getQuantityString(R.plurals.tracks,
                playlist.size ?: 0, playlist.size ?: 0)
            binding.totalTime.text = resources.getQuantityString(
                R.plurals.minutes,
                tracksAdapter.tracks.sumOf { it.trackTimeMillis }.formatAsMinutes().toInt(),
                tracksAdapter.tracks.sumOf { it.trackTimeMillis }.formatAsMinutes()
            )
        }
    }

    private fun Long.formatAsMinutes(): Long {
        return this/60000
    }

    private fun showPlayer() {
        tracksAdapter.itemClickListener = { position, track ->
            lifecycleScope.launch {
                delay(Constants.DELAY_TIME_MILLIS)
                sendToPlayer(track)
            }
        }
    }

    private fun sendToPlayer(track: Track) {
        findNavController().navigate(
            R.id.action_openPlaylistFragment_to_playerFragment,
            MediaFragment.createArgs(track.trackId)
        )
    }
    private fun playlistDelete () {
        binding.deletePlaylistTv.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            context?.let { context ->
                MaterialAlertDialogBuilder(requireContext(), 0)
                    .setMessage(requireContext().getString(R.string.delete_playlists))
                    .setNegativeButton(R.string.no) { dialog, which -> }
                    .setPositiveButton(R.string.yes) { dialog, which ->
                        playlist?.let { playlist ->
                            viewModel.deletePlaylist(playlist.id)
                        }
                        findNavController().popBackStack()
                    }.show()
            }
        }
    }

    private fun deleteTracks () {
        tracksAdapter.itemLongClickListener = { position, track ->
            println("long click")

            confirmDialogDeleteTrack = MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.delete_track)
                .setMessage("")
                .setPositiveButton(R.string.yes) { dialog, which ->
                    if (track == null || playlist == null) return@setPositiveButton
                    viewModel.deleteTracks(track, playlist!!)
                }
                .setNegativeButton(R.string.no) { dialog, which -> }
            confirmDialogDeleteTrack?.show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}