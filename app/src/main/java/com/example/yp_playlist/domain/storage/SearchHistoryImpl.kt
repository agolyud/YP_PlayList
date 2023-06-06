package com.example.yp_playlist.domain.storage

import android.content.SharedPreferences
import com.example.yp_playlist.domain.Track
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

const val HISTORY_TRACKS_KEY = "history_tracks_key"
const val MAX_HISTORY_SIZE = 10

class SearchHistoryImpl(private val sharedPref: SharedPreferences) : SearchHistory {

    private val typeTokenArrayList = object : TypeToken<ArrayList<Track>>() {}.type

    override fun addTrack(track: Track, position: Int) {
        val jsonHistoryTracks = sharedPref.getString(HISTORY_TRACKS_KEY, null)

        if (jsonHistoryTracks == null) {
            sharedPref.edit().putString(HISTORY_TRACKS_KEY, Gson().toJson(listOf(track))).apply()
            return
        }

        val historyTracks = Gson().fromJson<ArrayList<Track>>(jsonHistoryTracks, typeTokenArrayList)

        // Проверяем, есть ли трек в истории
        val existingTrackIndex = historyTracks.indexOfFirst { it.trackId == track.trackId }

        // Если трек уже есть в истории, удаляем его и добавляем на первое место
        if (existingTrackIndex != -1) {
            historyTracks.removeAt(existingTrackIndex)
        }
        historyTracks.add(0, track)

        // Если в истории уже 10 треков, удаляем последний
        if (historyTracks.size > MAX_HISTORY_SIZE) {
            historyTracks.removeLast()
        }

        saveTrackForHistory(historyTracks)
    }

    override fun tracksHistoryFromJson(): List<Track> {
        val jsonHistoryTracks =
            sharedPref.getString(HISTORY_TRACKS_KEY, null) ?: return ArrayList<Track>()
        return Gson().fromJson<ArrayList<Track>>(jsonHistoryTracks, typeTokenArrayList)
    }

    override fun clearHistory() {
        sharedPref.edit().remove(HISTORY_TRACKS_KEY).apply()
    }

    private fun saveTrackForHistory(historyTracks: ArrayList<Track>) {
        sharedPref.edit().putString(HISTORY_TRACKS_KEY, Gson().toJson(historyTracks)).apply()
    }
}