package com.example.yp_playlist.presentation.media

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.yp_playlist.R
import com.example.yp_playlist.databinding.FragmentPlayerBinding
import com.example.yp_playlist.domain.Track
import com.example.yp_playlist.medialibrary.playlists.domain.models.Playlist
import com.example.yp_playlist.medialibrary.playlists.ui.adapter.ViewObjects
import com.example.yp_playlist.util.Constants.TRACK_ID
import com.google.android.material.bottomsheet.BottomSheetBehavior
import org.koin.androidx.viewmodel.ext.android.viewModel

class MediaFragment : Fragment() {

    private val viewModel by viewModel<MediaViewModel>()
    private var _mediaBinding: FragmentPlayerBinding? = null
    private val mediaBinding get() = _mediaBinding!!
    private val bottomPlaylistsAdapter = ViewObjects.PlaylistsAdapter(viewObject = ViewObjects.Vertical)

    private var isBound = false
    private lateinit var mediaPlayerService: MediaPlayerServiceInterface

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _mediaBinding = FragmentPlayerBinding.inflate(inflater, container, false)
        return mediaBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindService()
        initializeViews()
        setupPlayer()
        bindClicks()
        observeTime()
        observeInfo()
        initListeners()
        observeState()
        setupAdapter()
        observePlaylistChanges()
        bottomSheetBehavior()
    }

    private fun bindService() {
        val intent = Intent(requireContext(), MediaPlayerService::class.java)
        requireActivity().bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
    }

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as MediaPlayerService.MediaPlayerBinder
            mediaPlayerService = binder.getService()
            isBound = true
            viewModel.setMediaPlayerService(mediaPlayerService)
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            isBound = false
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        if (isBound) {
            requireActivity().unbindService(serviceConnection)
            isBound = false
        }
        viewModel.pausePlayer()
        viewModel.releasePlayer()
        _mediaBinding = null
    }

    private fun initializeViews() {
        val trackId = requireArguments().getInt(TRACK_ID)
        viewModel.preparePlayer(trackId)
        viewModel.checkIsFavourite(trackId)
    }

    private fun bindClicks() {
        mediaBinding.toolbarInclude.setOnClickListener {
            findNavController().navigateUp()
        }

        mediaBinding.playButton.onTouchListener = {
            viewModel.playbackControl()
            mediaBinding.playButton.isPlaying = !mediaBinding.playButton.isPlaying
            true
        }
    }

    private fun setupPlayer() {
        viewModel.observeIsFavourite().observe(viewLifecycleOwner) { isFavorite ->
            mediaBinding.likeButton.setImageResource(if (isFavorite) R.drawable.ic_like_button_favourite else R.drawable.like_button)
        }
    }

    private fun observeTime() {
        viewModel.time.observe(viewLifecycleOwner) {
            mediaBinding.time.text = it
        }
    }

    private fun observeState() {
        viewModel.mediaState.observe(viewLifecycleOwner) { state ->
            when (state) {
                MediaViewModel.State.PLAYING -> mediaBinding.playButton.isPlaying = true
                MediaViewModel.State.PAUSED -> mediaBinding.playButton.isPlaying = false
                MediaViewModel.State.DEFAULT -> mediaBinding.playButton.isPlaying = false
                else -> {}
            }
        }
    }

    private fun observeInfo() {
        viewModel.trackInfo.observe(viewLifecycleOwner) { track ->
            showInfo(track)
        }
    }

    private fun showInfo(track: Track) {
        mediaBinding.trackName.text = track.trackName
        mediaBinding.artistName.text = track.artistName
        mediaBinding.trackTime.text = viewModel.getTime(track.trackTimeMillis.toInt())
        mediaBinding.collectionName.text = track.collectionName
        mediaBinding.releaseDate.text = track.releaseDate?.let { viewModel.getDate(it) }
        mediaBinding.primaryGenreName.text = track.primaryGenreName
        mediaBinding.country.text = track.country

        Glide.with(this)
            .load(track.artworkUrl100.replaceAfterLast('/', "512x512bb.jpg"))
            .placeholder(R.drawable.placeholder)
            .centerCrop()
            .transform(RoundedCorners(resources.getDimensionPixelSize(R.dimen.button_margins)))
            .into(mediaBinding.trackImage)
    }

    private fun initListeners() {
        val bottomSheetBehavior = BottomSheetBehavior.from(mediaBinding.bottomSheetLinear).apply {
            state = BottomSheetBehavior.STATE_HIDDEN
        }

        bottomPlaylistsAdapter.onPlayListClicked = {
            viewModel.addTrackToPlayList(track = viewModel.trackInfo.value!!, playlist = it)
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        }

        mediaBinding.addButton.setOnClickListener {
            viewModel.fillData()
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        }

        mediaBinding.buttonNewPlaylist.setOnClickListener {
            findNavController().navigate(R.id.action_playerFragment_to_newPlaylistFragment)
        }

        mediaBinding.likeButton.setOnClickListener {
            viewModel.onFavouriteClicked(track = viewModel.trackInfo.value!!)
        }
    }

    private fun setupAdapter() {
        mediaBinding.playlistsBottomSheetRecyclerview.adapter = bottomPlaylistsAdapter
    }

    private fun observePlaylistChanges() {
        viewModel.isAlreadyInPlaylist.observe(viewLifecycleOwner) {
            val message = if (it.second) "Добавлено в плейлист ${it.first}" else "Трек уже добавлен в плейлист ${it.first}"
            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
        }

        viewModel.playlists.observe(viewLifecycleOwner) {
            bottomPlaylistsAdapter.playlists = it as ArrayList<Playlist>
            viewModel.fillData()
        }
    }

    private fun bottomSheetBehavior(){
        val bottomSheetBehavior = BottomSheetBehavior.from(mediaBinding.bottomSheetLinear).apply {
            state = BottomSheetBehavior.STATE_HIDDEN
        }

        bottomSheetBehavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_HIDDEN -> {
                        mediaBinding.overlay.visibility = View.GONE
                    }
                    else -> {
                        mediaBinding.overlay.visibility = View.VISIBLE
                    }
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) { }
        })
    }

    companion object {
        fun createArgs(sendTrackId: Int): Bundle {
            return bundleOf(TRACK_ID to sendTrackId)
        }
    }
}
