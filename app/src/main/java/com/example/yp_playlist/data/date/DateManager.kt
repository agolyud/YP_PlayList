package com.example.yp_playlist.data.date

interface DateManager {

    fun getTime(trackTime: Int): String
    fun getDate(releaseDate: String): String

}