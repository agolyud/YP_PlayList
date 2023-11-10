package com.example.yp_playlist.presentation.media

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
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
    private lateinit var mediaBinding: FragmentPlayerBinding

    private val bottomPlaylistsAdapter =
        ViewObjects.PlaylistsAdapter(viewObject = ViewObjects.Vertical)


    private lateinit var buttonArrowBackSettings: androidx.appcompat.widget.Toolbar
    private lateinit var artworkUrl100: ImageView
    private lateinit var trackName: TextView
    private lateinit var artistName: TextView
    private lateinit var trackTime: TextView
    private lateinit var collectionName: TextView
    private lateinit var releaseDate: TextView
    private lateinit var primaryGenreName: TextView
    private lateinit var country: TextView
    private lateinit var time: TextView
    private lateinit var previewUrl: String
    private lateinit var buttonPlay: ImageView
    private lateinit var likeButton: ImageView
    private lateinit var progressBar: ProgressBar

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        mediaBinding = FragmentPlayerBinding.inflate(inflater, container, false)
        return mediaBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setMediaValues()
        initializeViews()
        setupPlayer()
        bindClicks()
        observeState()
        observeTime()
        observeInfo()
        initListeners()
        setupAdapter()
        observePlaylistChanges()
    }

    private fun setMediaValues() {
        buttonArrowBackSettings = mediaBinding.toolbarInclude
        artworkUrl100 = mediaBinding.trackImage
        trackName = mediaBinding.trackName
        artistName = mediaBinding.artistName
        trackTime = mediaBinding.trackTime
        collectionName = mediaBinding.collectionName
        releaseDate = mediaBinding.releaseDate
        primaryGenreName = mediaBinding.primaryGenreName
        country = mediaBinding.country
        time = mediaBinding.time
        buttonPlay = mediaBinding.playButton
        progressBar = mediaBinding.progressBar
        likeButton = mediaBinding.likeButton

    }

    private fun initializeViews() {
        val trackId = requireArguments().getInt(TRACK_ID)
        viewModel.preparePlayer(trackId)
        viewModel.checkIsFavourite(trackId)
    }

    private fun setupPlayer() {
        viewModel.observeIsFavourite().observe(viewLifecycleOwner) { isFavorite ->
            likeButton.setImageResource(if (isFavorite) R.drawable.ic_like_button_favourite else R.drawable.like_button)
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

        likeButton.setOnClickListener {
            viewModel.onFavouriteClicked(track = viewModel.trackInfo.value!!)
        }
    }

    private fun bindClicks() {
        buttonArrowBackSettings.setOnClickListener {
            findNavController().navigateUp()
        }

        buttonPlay.setOnClickListener {
            viewModel.playbackControl()
        }
    }

    private fun observeInfo() {
        viewModel.trackInfo.observe(viewLifecycleOwner) { track ->
            showInfo(track)
        }
    }

    private fun observeState() {
        viewModel.mediaState.observe(viewLifecycleOwner) { state ->
            when (state) {
                MediaViewModel.State.PREPARED, MediaViewModel.State.PAUSED -> buttonPlay.setImageResource(R.drawable.play_media)
                MediaViewModel.State.PLAYING -> buttonPlay.setImageResource(R.drawable.pause)
                else -> {}
            }
        }
    }

    private fun observeTime() {
        viewModel.time.observe(viewLifecycleOwner) {
            time.text = it
        }
    }

    private fun showInfo(track: Track) {

        previewUrl = track.previewUrl.toString()
        trackName.text = track.trackName
        artistName.text = track.artistName
        trackTime.text = viewModel.getTime(track.trackTimeMillis.toInt())
        collectionName.text = track.collectionName
        releaseDate.text = track.releaseDate?.let { viewModel.getDate(it) }
        primaryGenreName.text = track.primaryGenreName
        country.text = track.country

        Glide.with(this)
            .load(track.artworkUrl100.replaceAfterLast('/', "512x512bb.jpg"))
            .placeholder(R.drawable.placeholder)
            .centerCrop()
            .transform(RoundedCorners(resources.getDimensionPixelSize(R.dimen.button_margins)))
            .into(artworkUrl100)

    }


    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.pausePlayer()
        viewModel.releasePlayer()
    }


    companion object {
        fun createArgs(sendTrackId: Int): Bundle {
            return bundleOf(TRACK_ID to sendTrackId)
        }
    }
}
