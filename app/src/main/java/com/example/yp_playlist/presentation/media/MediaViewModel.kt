package com.example.yp_playlist.presentation.media

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.yp_playlist.domain.Track
import com.example.yp_playlist.domain.interactors.media.MediaInteractor
import com.example.yp_playlist.medialibrary.favourite.domain.api.FavouriteTracksInteractor
import com.example.yp_playlist.medialibrary.playlists.domain.api.PlaylistInteractor
import com.example.yp_playlist.medialibrary.playlists.domain.models.Playlist
import com.example.yp_playlist.util.Constants.DEFAULT_TIME
import com.example.yp_playlist.util.Constants.DELAY_TIME_MILLIS
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch


class MediaViewModel(
    private val mediaInteractor: MediaInteractor,
    private val favouriteTracksInteractor: FavouriteTracksInteractor,
    private val playlistInteractor: PlaylistInteractor
) : ViewModel() {


    private val isFavouriteLiveData = MutableLiveData<Boolean>()
    private val _trackInfo = MutableLiveData<Track>()
    private val _mediaState = MutableLiveData<State>()
    private val _time = MutableLiveData(DEFAULT_TIME)
    private var timerJob: Job? = null
    private var isFavourite = false

    private val _playlists = MutableLiveData<List<Playlist>>()
    val playlists: LiveData<List<Playlist>> = _playlists

    private val _isAlreadyInPlaylist = MutableLiveData<Pair<String, Boolean>>()
    val isAlreadyInPlaylist: LiveData<Pair<String, Boolean>> = _isAlreadyInPlaylist

    val trackInfo: LiveData<Track> = _trackInfo
    val time: LiveData<String> = _time
    val mediaState: LiveData<State> = _mediaState
    fun observeIsFavourite(): LiveData<Boolean> = isFavouriteLiveData

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

    fun fillData() {
        viewModelScope.launch {
            playlistInteractor.getPlaylists().collect {
                _playlists.postValue(it)
            }
        }
    }

    fun addTrackToPlayList(track: Track, playlist: Playlist) {
        viewModelScope.launch {
            playlistInteractor.addTrackToPlayList(track, playlist).collect {
                _isAlreadyInPlaylist.postValue(Pair(playlist.title, it))
            }
        }
    }


    fun checkIsFavourite(trackId: Int) {
        viewModelScope.launch {
            favouriteTracksInteractor
                .isFavoriteTrack(trackId)
                .collect { isFavorite ->
                    isFavourite = isFavorite
                    isFavouriteLiveData.postValue(isFavourite)
                }
        }
    }

    fun onFavouriteClicked(track: Track) {
        viewModelScope.launch {
            isFavourite = if (isFavourite) {
                favouriteTracksInteractor.deleteFromFavorites(track.trackId)
                isFavouriteLiveData.postValue(false)
                false
            } else {
                favouriteTracksInteractor.addToFavorites(track)
                isFavouriteLiveData.postValue(true)
                true
            }
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
                }
            ) {
                _mediaState.postValue(State.PREPARED)
                timerJob?.cancel()
                _time.postValue(DEFAULT_TIME)
            }
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