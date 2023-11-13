package com.example.yp_playlist.medialibrary.playlists.ui.adapter

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.yp_playlist.R
import com.example.yp_playlist.medialibrary.playlists.domain.models.Playlist

class PlaylistsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    private val title = itemView.findViewById<TextView>(R.id.playlist_title)
    private val size = itemView.findViewById<TextView>(R.id.playlist_size)
    private val image = itemView.findViewById<ImageView>(R.id.track_playlist_Image)

    fun bind(model: Playlist){
        title.text = model.title
        val quantityTracks = model.size
        size.text = itemView.resources.getQuantityString(R.plurals.tracks, quantityTracks, quantityTracks)

        Glide.with(itemView.context)
            .load(model.imageUri)
            .placeholder(R.drawable.placeholder)
            .centerCrop()
            .transform(RoundedCorners(itemView.resources.getDimensionPixelSize(R.dimen.button_margins)))
            .into(image)
    }
}