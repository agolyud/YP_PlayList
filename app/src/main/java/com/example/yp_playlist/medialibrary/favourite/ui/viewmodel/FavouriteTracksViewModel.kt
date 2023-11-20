package com.example.yp_playlist.medialibrary.favourite.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.yp_playlist.medialibrary.favourite.domain.api.FavouriteTracksInteractor
import com.example.yp_playlist.medialibrary.favourite.ui.models.FavouriteTracksState
import com.example.yp_playlist.util.Constants.CLICK_DELAY
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class FavouriteTracksViewModel(
    private val favouriteTracksInteractor: FavouriteTracksInteractor
) : ViewModel() {

    private var isClickAllowed = true
    private val stateLiveData = MutableLiveData<FavouriteTracksState>()
    fun observeState(): LiveData<FavouriteTracksState> = stateLiveData

    fun getFavouriteTracks() {
        viewModelScope.launch {
            favouriteTracksInteractor
                .getFavoritesTracks()
                .collect { tracks ->
                    if (tracks.isEmpty()) {
                        renderState(FavouriteTracksState.Empty)
                    }
                    else {
                        renderState(FavouriteTracksState.Content(tracks))
                    }
                }
        }
    }

    private fun renderState(state: FavouriteTracksState) {
        stateLiveData.postValue(state)
    }
}