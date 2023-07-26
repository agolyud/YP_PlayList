package com.example.yp_playlist.presentation.media

import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.yp_playlist.R
import com.example.yp_playlist.domain.Track
import com.example.yp_playlist.presentation.search.HISTORY_TRACKS_SHARED_PREF
import com.example.yp_playlist.presentation.search.SearchViewModel
import com.example.yp_playlist.presentation.search.TRACK_ID

class MediaActivity : AppCompatActivity() {

    private lateinit var viewModel: MediaViewModel

    //Переменные
    lateinit var buttonArrowBackSettings: androidx.appcompat.widget.Toolbar
    lateinit var artworkUrl100: ImageView
    lateinit var trackName: TextView
    lateinit var artistName: TextView
    lateinit var trackTime: TextView
    lateinit var collectionName: TextView
    lateinit var releaseDate: TextView
    lateinit var primaryGenreName: TextView
    lateinit var country: TextView
    lateinit var time: TextView
    lateinit var previewUrl: String
    lateinit var buttonPlay: ImageView
    private lateinit var progressBar: ProgressBar

    var handler = Handler(Looper.getMainLooper())

    companion object {
        private const val STATE_DEFAULT = 0
        private const val STATE_PREPARED = 1
        private const val STATE_PLAYING = 2
        private const val STATE_PAUSED = 3

        private const val DEFAULT_TIME = "00:00"
        private const val START_TIME = "0:00"

    }

    private var playerState = STATE_DEFAULT

    private var mediaPlayer = MediaPlayer()

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
        buttonPlay = findViewById(R.id.playButton)
        progressBar = findViewById(R.id.progressBar)





        viewModel = ViewModelProvider(this, MediaViewModel.getViewModelFactory(this,getSharedPreferences(
            HISTORY_TRACKS_SHARED_PREF, MODE_PRIVATE)))[MediaViewModel::class.java]



        buttonArrowBackSettings.setOnClickListener {
            finish()
        }

        buttonPlay.setOnClickListener {
            playbackControl()
        }
        val trackId = intent.extras?.getInt(TRACK_ID) as Int
        viewModel.getTrack(trackId)
        preparePlayer()
        playbackControl()

    }

    override fun onPause() {
        super.onPause()
        handler.removeCallbacksAndMessages(null)
        pausePlayer()
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null)
        time.text = START_TIME
        mediaPlayer.release()
    }

    //Подготовить воспроизведение
    private fun preparePlayer() {
        mediaPlayer.setDataSource(previewUrl)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            buttonPlay.isEnabled = true
            progressBar.visibility = View.GONE
            playerState = STATE_PREPARED
        }
        mediaPlayer.setOnCompletionListener {
            handler.removeCallbacksAndMessages(null)
            buttonPlay.setImageResource(R.drawable.play_media)
            time.text = DEFAULT_TIME
            playerState = STATE_PREPARED
        }
    }

    //Воспроизвести трек
    private fun startPlayer() {
        mediaPlayer.start()
        buttonPlay.setImageResource(R.drawable.pause)
        playerState = STATE_PLAYING
        startTimer()
    }

    //Пауза
    private fun pausePlayer() {
        mediaPlayer.pause()
        buttonPlay.setImageResource(R.drawable.play_media)
        handler.removeCallbacksAndMessages(null)
        playerState = STATE_PAUSED
    }

    private fun playbackControl() {
        when (playerState) {
            STATE_PLAYING -> {
                pausePlayer()
            }

            STATE_PREPARED, STATE_PAUSED -> {
                startPlayer()
            }
        }
    }

    private fun timerTrack(): Runnable {
        return object : Runnable {
            override fun run() {
                time.text = viewModel.getTime(mediaPlayer.currentPosition)
                handler.postDelayed(this, 300)
            }
        }
    }

    private fun startTimer() {
        handler.post(timerTrack())
    }

    //Отображение данных трека
    private fun showInfo(track: Track) {

        previewUrl = track.previewUrl
        trackName.text = track.trackName
        artistName.text = track.artistName
        trackTime.text = viewModel.getTime(track.trackTimeMillis.toInt())
        collectionName.text = track.collectionName
        releaseDate.text = viewModel.getDate(track.releaseDate)
        primaryGenreName.text = track.primaryGenreName
        country.text = track.country
        time.text = START_TIME


        Glide.with(this)
            .load(track.artworkUrl100.replaceAfterLast('/', "512x512bb.jpg"))
            .placeholder(R.drawable.placeholder)
            .centerCrop()
            .transform(RoundedCorners(resources.getDimensionPixelSize(R.dimen.button_margins)))
            .into(artworkUrl100)

    }
}