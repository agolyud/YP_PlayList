package com.example.yp_playlist.presentation.media

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.yp_playlist.R
import com.example.yp_playlist.domain.Track
import com.example.yp_playlist.presentation.search.TRACK_ID
import org.koin.androidx.viewmodel.ext.android.viewModel

class MediaActivity : AppCompatActivity() {

    private val viewModel by viewModel<MediaViewModel>()

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
    lateinit var likeButton: ImageView
    private lateinit var progressBar: ProgressBar

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
        likeButton = findViewById(R.id.likeButton)



        val trackId = intent.extras?.getInt(TRACK_ID) as Int
        viewModel.preparePlayer(trackId)

        bindClicks()
        observeState()
        observeTime()
        observeInfo()


        viewModel.checkIsFavourite(trackId)


        viewModel.observeIsFavourite().observe(this) {
                isFavorite ->
            likeButton.setImageResource(
                if (isFavorite) R.drawable.ic_like_button_favourite else R.drawable.like_button
            )
        }


        likeButton.setOnClickListener {
            viewModel.onFavouriteClicked(track = viewModel.trackInfo.value!!)
        }

    }

    private fun bindClicks() {
        buttonArrowBackSettings.setOnClickListener {
            finish()
        }

        buttonPlay.setOnClickListener {
            viewModel.playbackControl()
        }
    }

    private fun observeInfo() {
        viewModel.trackInfo.observe(this) { track ->
            showInfo(track)
        }
    }

    private fun observeState() {
        viewModel.mediaState.observe(this) { state ->
            when (state) {
                MediaViewModel.State.PREPARED, MediaViewModel.State.PAUSED -> buttonPlay.setImageResource(
                    R.drawable.play_media
                )

                MediaViewModel.State.PLAYING -> buttonPlay.setImageResource(R.drawable.pause)
                else -> {}
            }
        }
    }

    private fun observeTime() {
        viewModel.time.observe(this) {
            time.text = it
        }
    }


    override fun onPause() {
        super.onPause()
        viewModel.pausePlayer()
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.releasePlayer()
    }

    //Отображение данных трека
    private fun showInfo(track: Track) {

        previewUrl = track.previewUrl.toString()
        trackName.text = track.trackName
        artistName.text = track.artistName
        trackTime.text = viewModel.getTime(track.trackTimeMillis.toInt())
        collectionName.text = track.collectionName
        releaseDate.text = track.releaseDate?.let { viewModel.getDate(it) }
        primaryGenreName.text = track.primaryGenreName
        country.text = track.country

        Glide.with(this)
            .load(track.artworkUrl100.replaceAfterLast('/', "512x512bb.jpg"))
            .placeholder(R.drawable.placeholder)
            .centerCrop()
            .transform(RoundedCorners(resources.getDimensionPixelSize(R.dimen.button_margins)))
            .into(artworkUrl100)

    }
}