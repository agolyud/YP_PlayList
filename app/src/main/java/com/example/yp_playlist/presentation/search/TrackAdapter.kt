package com.example.yp_playlist.presentation.search

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.yp_playlist.R
import com.example.yp_playlist.domain.Track

class TrackAdapter : RecyclerView.Adapter<TrackViewHolder>() {


    var tracks = mutableListOf<Track>()
    var itemClickListener: ((Int, Track) -> Unit)? = null
    var itemLongClickListener: ((Int, Track) -> Unit)? = null

    fun updateTracks(newTracks: List<Track>) {
        tracks.clear()
        tracks.addAll(newTracks)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.track_item, parent, false)
        return TrackViewHolder(view)
    }

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        val track = tracks[position]
        holder.bind(track)
        holder.itemView.setOnClickListener() {
            itemClickListener?.invoke(position, track)
        }
        holder.itemView.setOnLongClickListener {
            itemLongClickListener?.invoke(position, track)
            true
        }


    }

    override fun getItemCount() = tracks.size
}