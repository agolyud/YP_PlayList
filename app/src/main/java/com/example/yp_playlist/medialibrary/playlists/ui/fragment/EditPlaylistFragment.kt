package com.example.yp_playlist.medialibrary.playlists.ui.fragment

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.core.os.bundleOf
import androidx.core.widget.doOnTextChanged
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.example.yp_playlist.R
import com.example.yp_playlist.databinding.FragmentNewPlaylistBinding
import com.example.yp_playlist.medialibrary.playlists.domain.models.Playlist
import java.io.Serializable

class EditPlaylistFragment : AddPlayListFragment()
{

    var playlist: Playlist? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNewPlaylistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initListeners()

        playlist = requireArguments().getSerializableExtra(
            EDIT_PLAYLIST, Playlist::class.java)

        if (savedInstanceState != null) {
            imageUri = savedInstanceState.getString("imageUri")
        }

        showPlaylist(playlist!!)

    }

    private fun setButtonColor(enabled: Boolean) {
        val color = ContextCompat.getColor(
            requireContext(),
            if (enabled) R.color.blue_fon else R.color.light_grey
        )
        binding.buttonCreatePlaylist.apply {
            isEnabled = enabled
            setBackgroundColor(color)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putString("imageUri", imageUri)
        super.onSaveInstanceState(outState)
    }

    private fun showPlaylist(playlist: Playlist) {
        playlist?.let {
            val imageUri = it.imageUri?.toUri()
            if (imageUri != null) {
                binding.coverPlaylist.setImageURI(imageUri)
                if (binding.coverPlaylist.drawable == null) {
                    binding.coverPlaylist.setImageResource(R.drawable.placeholder)
                }
            } else {
                binding.coverPlaylist.setImageResource(R.drawable.placeholder)
            }

            binding.textNamePlaylist.setText(it.title)
            binding.textDescriptionPlaylist.setText(it.description)
            binding.buttonCreatePlaylist.text = getString(R.string.save)
            binding.toolbarNewPlaylist.title = getString(R.string.editing)
        }
    }
    private fun initListeners() {

        binding.textNamePlaylist.doOnTextChanged { text, _, _, _ ->
            setButtonColor(!text.isNullOrBlank())
        }

        binding.toolbarNewPlaylist.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.buttonCreatePlaylist.setOnClickListener {
            if (playlist != null) {
                val updatedPlaylist = Playlist(
                    id = playlist?.id ?: 0,
                    title = binding.textNamePlaylist.text.toString(),
                    description = binding.textDescriptionPlaylist.text.toString(),
                    imageUri = imageUri,
                    trackList = playlist?.trackList,
                    size = playlist?.size ?: 0
                )
                viewModel.updatePlaylist(updatedPlaylist)
            }
            findNavController().popBackStack()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private inline fun <reified T : Serializable> Bundle.getSerializableExtra(
        key: String,
        java: Class<Playlist>
    ): T = getSerializable(key) as T

    companion object {
        const val EDIT_PLAYLIST = "EDIT_PLAYLIST"
        fun createArgs(playlist: Playlist): Bundle {
            return bundleOf(EDIT_PLAYLIST to playlist)
        }
    }
}
