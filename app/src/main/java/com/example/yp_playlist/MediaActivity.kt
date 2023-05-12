package com.example.yp_playlist

import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.gson.Gson
import java.text.SimpleDateFormat
import java.util.*

class MediaActivity : AppCompatActivity() {

    //Переменные
    lateinit var buttonArrowBackSettings: androidx.appcompat.widget.Toolbar
    lateinit var track: Track
    lateinit var sharedPrefDataTrack: SharedPreferences
    lateinit var artworkUrl100: ImageView
    lateinit var trackName: TextView
    lateinit var artistName: TextView
    lateinit var trackTime: TextView
    lateinit var collectionName: TextView
    lateinit var releaseDate: TextView
    lateinit var primaryGenreName: TextView
    lateinit var country: TextView
    lateinit var time: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)
       buttonArrowBackSettings = findViewById(R.id.toolbarInclude)
        artworkUrl100 = findViewById(R.id.trackImage)
        trackName = findViewById(R.id.trackName)
        artistName = findViewById(R.id.artistName)
        trackTime = findViewById(R.id.trackTime)
        collectionName = findViewById(R.id.collection_name)
        releaseDate = findViewById(R.id.release_date)
        primaryGenreName = findViewById(R.id.primary_genre_name)
        country = findViewById(R.id.country)
        time = findViewById(R.id.time)


        buttonArrowBackSettings.setOnClickListener {
            finish()
        }


        showDataTrack()
    }


    //Отображение данных трека
    private fun showDataTrack(){

        track = intent.getSerializableExtra("track") as Track
        trackName.text = track.trackName
        artistName.text = track.artistName
        trackTime.text = SimpleDateFormat("mm:ss", Locale.getDefault()).format(track.trackTimeMillis.toInt())
        collectionName.text = track.collectionName

        val date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(track.releaseDate)
        if (date != null) {
            val formattedDatesString = SimpleDateFormat("yyyy", Locale.getDefault()).format(date)
            releaseDate.text = formattedDatesString
        }

        primaryGenreName.text = track.primaryGenreName
        country.text = track.country
        time.text = "0:00"


        Glide.with(this)
            .load(track.artworkUrl100.replaceAfterLast('/', "512x512bb.jpg"))
            .placeholder(R.drawable.placeholder)
            .centerCrop()
            .transform(RoundedCorners(resources.getDimensionPixelSize(R.dimen.button_margins)))
            .into(artworkUrl100)

    }
}