package com.example.yp_playlist.medialibrary.playlists.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.yp_playlist.R
import com.example.yp_playlist.medialibrary.playlists.domain.models.Playlist


sealed class ViewObjects {
    object Horizontal : ViewObjects()
    object Vertical : ViewObjects()
    class PlaylistsAdapter(private val viewObject: ViewObjects) :
        RecyclerView.Adapter<PlaylistsViewHolder>() {

        var onPlayListClicked: ((playlist: Playlist) -> Unit)? = null


        var playlists = mutableListOf<Playlist>()
            set(newPlaylists) {
                val diffCallback = PlaylistsDiffCallback(field, newPlaylists)
                val diffResult = DiffUtil.calculateDiff(diffCallback)
                field = newPlaylists
                diffResult.dispatchUpdatesTo(this)
            }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistsViewHolder {
            return if (viewObject == Horizontal) {
                PlaylistsViewHolder(
                    LayoutInflater.from(parent.context)
                        .inflate(R.layout.playlist_item, parent, false)
                )
            } else {
                PlaylistsViewHolder(
                    LayoutInflater.from(parent.context)
                        .inflate(R.layout.tracks_playlists_item, parent, false)
                )
            }
        }

        override fun getItemCount() = playlists.size

        override fun onBindViewHolder(holder: PlaylistsViewHolder, position: Int) {
            val playlist = playlists[position]
            holder.bind(playlist)
            holder.itemView.setOnClickListener {
                onPlayListClicked?.invoke(playlist)
            }
        }
    }
}
