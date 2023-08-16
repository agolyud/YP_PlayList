package com.example.yp_playlist.presentation.media

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.yp_playlist.domain.Track
import com.example.yp_playlist.domain.interactors.media.MediaInteractor


class MediaViewModel(
    private val mediaInteractor: MediaInteractor
) : ViewModel() {
    companion object {
        private const val DEFAULT_TIME = "00:00"
    }

    private val _trackInfo = MutableLiveData<Track>()
    private val _mediaState = MutableLiveData<State>()
    private val _time = MutableLiveData(DEFAULT_TIME)

    val trackInfo: LiveData<Track> = _trackInfo
    val time: LiveData<String> = _time
    val mediaState: LiveData<State> = _mediaState

    var handler = Handler(Looper.getMainLooper())

    fun getTime(trackTime: Int): String {
        return mediaInteractor.getTime(trackTime)
    }

    fun getDate(releaseDate: String): String {
        return mediaInteractor.getDate(releaseDate)
    }

    fun playbackControl() {
        when (mediaState.value) {
            State.PREPARED, State.PAUSED -> {
                _mediaState.postValue(State.PLAYING)
                startPlayer()
            }

            State.PLAYING -> {
                _mediaState.postValue(State.PAUSED)
                pausePlayer()
            }

            else -> {}
        }
    }

    fun preparePlayer(trackId: Int) {
        val track = getTrack(trackId)
        track?.let {
            _trackInfo.postValue(it)

            mediaInteractor.prepare(
                url = track.previewUrl,
                onPrepared = {
                    _mediaState.postValue(State.PREPARED)
                },
                onCompletion = {
                    _mediaState.postValue(State.PREPARED)
                }
            )
        }
    }

    private fun getTrack(trackId: Int) = mediaInteractor.tracksHistoryFromJson().firstOrNull {
        it.trackId == trackId
    }


    private fun startPlayer() {
        mediaInteractor.start()
        startTimer()
    }

    fun pausePlayer() {
        mediaInteractor.pause()
        _time.postValue(getPlayerPosition())
    }

    fun releasePlayer() {
        mediaInteractor.release()
        _mediaState.postValue(State.DEFAULT)
    }

    private fun startTimer() {
        handler.post(timerTrack())
    }

    private fun timerTrack(): Runnable {
        return object : Runnable {
            override fun run() {
                handler.postDelayed(this, 300)
                _time.postValue(getPlayerPosition())
            }
        }
    }

    private fun getPlayerPosition() = mediaInteractor.getTime(mediaInteractor.getPosition())

    enum class State {
        DEFAULT,
        PREPARED,
        PLAYING,
        PAUSED
    }

}