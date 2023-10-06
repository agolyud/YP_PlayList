package com.example.yp_playlist.presentation.media

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.yp_playlist.domain.Track
import com.example.yp_playlist.domain.interactors.media.MediaInteractor
import com.example.yp_playlist.util.Constants.DEFAULT_TIME
import com.example.yp_playlist.util.Constants.DELAY_TIME_MILLIS
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch


class MediaViewModel(
    private val mediaInteractor: MediaInteractor
) : ViewModel() {


    private val _trackInfo = MutableLiveData<Track>()
    private val _mediaState = MutableLiveData<State>()
    private val _time = MutableLiveData(DEFAULT_TIME)
    private var timerJob: Job? = null

    val trackInfo: LiveData<Track> = _trackInfo
    val time: LiveData<String> = _time
    val mediaState: LiveData<State> = _mediaState

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
                    timerJob?.cancel()
                    _time.postValue(DEFAULT_TIME)
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

    private fun startTimer () {
        timerJob = viewModelScope.launch {
            while (isActive) {
                delay(DELAY_TIME_MILLIS)
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