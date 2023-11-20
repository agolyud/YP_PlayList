package com.example.yp_playlist.medialibrary.playlists.ui.fragment


import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.yp_playlist.R
import com.example.yp_playlist.databinding.FragmentNewPlaylistBinding
import com.example.yp_playlist.medialibrary.playlists.ui.viewmodel.AddPlaylistViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.koin.androidx.viewmodel.ext.android.viewModel


open class AddPlayListFragment : Fragment() {

    open var _binding: FragmentNewPlaylistBinding? = null
    val binding get() = _binding!!
    var imageUri: String? = null
    val viewModel by viewModel<AddPlaylistViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNewPlaylistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initPickMediaRegister()
        initListeners()
    }

    private fun initListeners() {
        binding.buttonCreatePlaylist.setOnClickListener {

            val   title = binding.textNamePlaylist.text?.toString() ?: ""
            val  description = binding.textDescriptionPlaylist.text?.toString() ?: ""
            val  imageUri = imageUri

            viewModel.savePlaylist(title, description, imageUri)
            imageUri?.let { viewModel.saveToLocalStorage(uri = it) }

            Toast.makeText(
                requireContext(),
                getString(R.string.createPlaylist, title),
                Toast.LENGTH_SHORT
            ).show()

            findNavController().popBackStack()
        }

        binding.toolbarNewPlaylist.setOnClickListener {
            if (binding.textNamePlaylist.text.isNullOrBlank() &&
                binding.textDescriptionPlaylist.text.isNullOrBlank() && imageUri == null
            ) {
                findNavController().navigateUp()
            } else {
                showConfirmDialog()
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (imageUri != null ||
                        !binding.textNamePlaylist.text.isNullOrBlank() ||
                        !binding.textDescriptionPlaylist.text.isNullOrBlank()
                    ) {
                        showConfirmDialog()
                    } else {
                        findNavController().navigateUp()
                    }
                }
            })

        binding.textNamePlaylist.doOnTextChanged { text, _, _, _ ->
            setButtonColor(!text.isNullOrBlank())
        }
    }

    private fun initPickMediaRegister() {
        val pickMedia =
            registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
                uri?.let {
                    binding.coverPlaylist.scaleType = ImageView.ScaleType.CENTER_CROP
                    binding.coverPlaylist.setImageURI(it)
                    imageUri = it.toString()
                }
            }

        binding.coverPlaylist.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }
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

    private fun showConfirmDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.stop_creating_playlist)
            .setMessage(R.string.unsaved_data_will_be_lost)
            .setNeutralButton(R.string.cancel) { dialog, which ->
            }
            .setPositiveButton(R.string.finish) { dialog, which ->
                findNavController().popBackStack()
            }
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}