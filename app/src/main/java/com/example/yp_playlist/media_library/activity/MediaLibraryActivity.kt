package com.example.yp_playlist.media_library.activity


import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.yp_playlist.R
import com.example.yp_playlist.media_library.adapter.MediaLibraryViewPagerAdapter
import com.google.android.material.tabs.TabLayoutMediator
import com.example.yp_playlist.databinding.ActivityMediaBinding


class MediaLibraryActivity : AppCompatActivity() {

    private lateinit var mediaBinding: ActivityMediaBinding
    private lateinit var tabMediator: TabLayoutMediator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mediaBinding = ActivityMediaBinding.inflate(layoutInflater)
        setContentView(mediaBinding.root)

        mediaBinding.viewPager.adapter =
            MediaLibraryViewPagerAdapter(supportFragmentManager, lifecycle)

        mediaBinding.settingsToolbar.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }

        tabMediator =
            TabLayoutMediator(mediaBinding.tabLayout, mediaBinding.viewPager) { tab, position ->
                when (position) {
                    0 -> tab.text = getString(R.string.favourite)
                    1 -> tab.text = getString(R.string.playlist)
                }
            }
        tabMediator.attach()
    }

    override fun onDestroy() {
        super.onDestroy()
        tabMediator.detach()
    }
}