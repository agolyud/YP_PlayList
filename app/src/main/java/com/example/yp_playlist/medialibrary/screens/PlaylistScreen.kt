package com.example.yp_playlist.medialibrary.screens

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.yp_playlist.medialibrary.playlists.domain.models.Playlist
import com.example.yp_playlist.medialibrary.playlists.ui.models.PlaylistsScreenState
import com.example.yp_playlist.medialibrary.playlists.ui.viewmodel.PlaylistsViewModel
import com.example.yp_playlist.resultitem.ui.PlaylistItem
import com.example.yp_playlist.ui.theme.AddPlayListButton
import com.example.yp_playlist.ui.theme.NotCreatePlaylist

@Composable
fun PlaylistScreen(
    playlistsViewModel: PlaylistsViewModel,
    onPlaylistClick: (Playlist) -> Unit,
    onNewPlaylistClick: () -> Unit
) {
    val state by playlistsViewModel.stateLiveData.observeAsState(PlaylistsScreenState.Empty)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {

        AddPlayListButton(
            darkThemeEnabled = isSystemInDarkTheme(),
            onNewPlaylistClick = onNewPlaylistClick
        )
        when (state) {
            is PlaylistsScreenState.Filled -> {
                val playlists = (state as PlaylistsScreenState.Filled).playlists
                PlaylistsGrid(playlists = playlists, onPlaylistClick = onPlaylistClick)
            }
            PlaylistsScreenState.Empty -> {
                NotCreatePlaylist(darkThemeEnabled = isSystemInDarkTheme())
            }
        }
    }
}

@Composable
fun PlaylistsGrid(
    playlists: List<Playlist>,
    onPlaylistClick: (Playlist) -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        items(playlists.size) { index ->
            PlaylistItem(
                playlist = playlists[index],
                onClick = { onPlaylistClick(playlists[index]) }
            )
        }
    }
}