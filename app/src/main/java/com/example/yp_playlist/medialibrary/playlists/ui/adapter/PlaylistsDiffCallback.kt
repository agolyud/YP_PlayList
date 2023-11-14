package com.example.yp_playlist.medialibrary.playlists.ui.adapter

import androidx.recyclerview.widget.DiffUtil
import com.example.yp_playlist.medialibrary.playlists.domain.models.Playlist

class PlaylistsDiffCallback(
    private val oldList: List<Playlist>,
    private val newList: List<Playlist>
) : DiffUtil.Callback() {

    override fun getOldListSize() = oldList.size
    override fun getNewListSize() = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldTrack = oldList[oldItemPosition]
        val newTrack = newList[oldItemPosition]
        return oldTrack.id == newTrack.id
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldTrack = oldList[oldItemPosition]
        val newTrack = newList[oldItemPosition]
        return oldTrack == newTrack
    }
}