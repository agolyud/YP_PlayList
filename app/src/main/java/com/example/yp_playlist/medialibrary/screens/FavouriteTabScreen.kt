package com.example.yp_playlist.medialibrary.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.example.yp_playlist.R
import com.example.yp_playlist.domain.Track
import com.example.yp_playlist.medialibrary.favourite.ui.models.FavouriteTracksState
import com.example.yp_playlist.medialibrary.favourite.ui.viewmodel.FavouriteTracksViewModel
import com.example.yp_playlist.resultitem.ui.ResultItem

@Composable
fun FavouriteTabScreen(
    favouriteTracksViewModel: FavouriteTracksViewModel,
    onTrackClick: (Track) -> Unit,
    darkThemeEnabled: Boolean
) {
    val state by favouriteTracksViewModel.observeState().observeAsState(FavouriteTracksState.Empty)

    when (state) {
        is FavouriteTracksState.Content -> {
            val tracks = (state as FavouriteTracksState.Content).tracks
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(tracks.size) { index ->
                    val track = tracks[index]
                    ResultItem(
                        trackImage = track.artworkUrl100,
                        trackName = track.trackName,
                        artistName = track.artistName,
                        trackTimeMillis = track.trackTimeMillis,
                        onClick = { onTrackClick(track) },
                        darkTheme = darkThemeEnabled
                    )
                }
            }
        }
        FavouriteTracksState.Empty -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = stringResource(id = R.string.your_media_isEmpty))
            }
        }
    }
}