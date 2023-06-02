package com.example.yp_playlist.data

import com.example.yp_playlist.domain.Track


class TrackResponse(val resultCount: Int,
                    val results: List<Track>)