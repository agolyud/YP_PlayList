package com.example.yp_playlist.data.date

import java.text.SimpleDateFormat
import java.util.Locale

class DateManagerImpl : DateManager {


    companion object {
        private const val TIME_PATTERN = "mm:ss"
        private const val DATE_PATTERN = "yyyy-MM-dd"
        private const val YEAR_PATTERN = "yyyy"
    }

    private val dateFormat = SimpleDateFormat(DATE_PATTERN, Locale.getDefault())
    private val yearFormat = SimpleDateFormat(YEAR_PATTERN, Locale.getDefault())

    override fun getTime(trackTime: Int): String =
        SimpleDateFormat(TIME_PATTERN, Locale.getDefault()).format(trackTime) ?: ""

    override fun getDate(releaseDate: String): String {
        val date = dateFormat.parse(releaseDate)
        return if (date != null) {
            val formattedDatesString = yearFormat.format(date)
            formattedDatesString ?: ""
        } else {
            ""
        }
    }

}