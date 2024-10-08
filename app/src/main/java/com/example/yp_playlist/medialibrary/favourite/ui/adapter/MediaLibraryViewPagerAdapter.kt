package com.example.yp_playlist.medialibrary.favourite.ui.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.yp_playlist.medialibrary.favourite.fragment.FavouriteTracksFragment
import com.example.yp_playlist.medialibrary.ui.fragment.PlaylistFragment


class MediaLibraryViewPagerAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle)
    : FragmentStateAdapter(fragmentManager, lifecycle) {

    override fun getItemCount() = 2
    override fun createFragment(position: Int): Fragment {
        return when(position) {
            0 -> FavouriteTracksFragment.newInstance()
            else -> PlaylistFragment.newInstance()
        }
    }
}