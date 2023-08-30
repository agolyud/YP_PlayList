package com.example.yp_playlist.media_library.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.yp_playlist.media_library.fragment.FavouriteTracksFragment
import com.example.yp_playlist.media_library.fragment.PlaylistsFragment

class MediaLibraryViewPagerAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle)
    : FragmentStateAdapter(fragmentManager, lifecycle) {

    override fun getItemCount() = 2
    override fun createFragment(position: Int): Fragment {
        return when(position) {
            0 -> FavouriteTracksFragment.newInstance()
            else -> PlaylistsFragment.newInstance()
        }
    }
}