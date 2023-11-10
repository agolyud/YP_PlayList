package com.example.yp_playlist.medialibrary.ui.fragment


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.yp_playlist.R
import com.example.yp_playlist.databinding.FragmentMediaBinding
import com.example.yp_playlist.medialibrary.favourite.ui.adapter.MediaLibraryViewPagerAdapter
import com.google.android.material.tabs.TabLayoutMediator


class MediaLibraryFragment : Fragment() {

    private  var _mediaBinding: FragmentMediaBinding? = null
    private val mediaBinding get() = _mediaBinding!!
    private lateinit var tabMediator: TabLayoutMediator

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _mediaBinding = FragmentMediaBinding.inflate(inflater, container, false)
        return mediaBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mediaBinding.viewPager.adapter =
            MediaLibraryViewPagerAdapter(childFragmentManager, viewLifecycleOwner.lifecycle)

        mediaBinding.settingsToolbar.setNavigationOnClickListener { requireActivity() }

        tabMediator =
            TabLayoutMediator(mediaBinding.tabLayout, mediaBinding.viewPager) { tab, position ->
                when (position) {
                    0 -> tab.text = getString(R.string.favourite)
                    1 -> tab.text = getString(R.string.playlist)
                }
            }
        tabMediator.attach()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        tabMediator.detach()
        _mediaBinding = null
    }
}