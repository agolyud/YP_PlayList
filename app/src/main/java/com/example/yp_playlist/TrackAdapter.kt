package com.example.yp_playlist

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class TrackAdapter : RecyclerView.Adapter<TrackViewHolder>() {

    var tracksHistory = ArrayList<Track>()
    private var tracks = mutableListOf<Track>()
    var itemClickListener: ((Int, Track) -> Unit)? = null

    fun updateTracks(newTracks: List<Track>) {
        tracks.clear()
        if (!newTracks.isNullOrEmpty()) {
            tracks.addAll(newTracks)
        }
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.track_item, parent, false)
        return TrackViewHolder(view)
    }

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        if (position < tracksHistory.size) {
            val track = tracksHistory[position]
            holder.bind(track)
            holder.itemView.setOnClickListener(){
                itemClickListener?.invoke(position, track)
            }
        } else {
            val track = tracks[position - tracksHistory.size]
            holder.bind(track)
            holder.itemView.setOnClickListener(){
                itemClickListener?.invoke(position, track)
            }
        }
    }

    override fun getItemCount() = tracksHistory.size + tracks.size
}