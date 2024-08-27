package com.example.yp_playlist.medialibrary.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Tab
import androidx.compose.material.TabRow
import androidx.compose.material.TabRowDefaults
import androidx.compose.material.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.yp_playlist.R
import com.example.yp_playlist.domain.Track
import com.example.yp_playlist.medialibrary.favourite.ui.viewmodel.FavouriteTracksViewModel
import com.example.yp_playlist.medialibrary.playlists.ui.viewmodel.PlaylistsViewModel
import com.example.yp_playlist.settings.ui.SettingsViewModel
import com.example.yp_playlist.ui.theme.YourAppTheme
import kotlinx.coroutines.launch


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MediaLibraryScreen(
    settingsViewModel: SettingsViewModel,
    favouriteTracksViewModel: FavouriteTracksViewModel,
    playlistsViewModel: PlaylistsViewModel,
    navController: NavController,
    onTrackClick: (Track) -> Unit
) {
    val tabs = listOf(
        stringResource(id = R.string.favourite),
        stringResource(id = R.string.playlist)
    )
    val themeSettings by settingsViewModel.themeSettingsState.observeAsState()
    val darkThemeEnabled = themeSettings?.darkTheme ?: false

    val pagerState = rememberPagerState(pageCount = { tabs.size }, initialPage = 0)
    val coroutineScope = rememberCoroutineScope()

    YourAppTheme(darkTheme = darkThemeEnabled) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colors.background)
        ) {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(id = R.string.media_button),
                        color = MaterialTheme.colors.onBackground,
                        fontSize = 22.sp
                    )
                },
                backgroundColor = MaterialTheme.colors.surface,
                elevation = 0.dp,
                modifier = Modifier
                    .height(56.dp)
            )

            TabRow(
                selectedTabIndex = pagerState.currentPage,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                backgroundColor = MaterialTheme.colors.surface,
                contentColor = MaterialTheme.colors.onSurface,
                indicator = { tabPositions ->
                    TabRowDefaults.Indicator(
                        Modifier.tabIndicatorOffset(tabPositions[pagerState.currentPage]),
                        color = MaterialTheme.colors.onBackground,
                        height = 2.dp
                    )
                },
                divider = {
                    Divider(
                        color = Color.Transparent,
                        thickness = 0.dp
                    )
                }
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = pagerState.currentPage == index,
                        onClick = {
                            coroutineScope.launch {
                                pagerState.scrollToPage(index)
                            }
                        },
                        text = {
                            Text(
                                text = title,
                                color = MaterialTheme.colors.onBackground,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .wrapContentSize(Alignment.Center)
                            )
                        }
                    )
                }
            }

            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 16.dp)
            ) { page ->
                when (page) {
                    0 -> FavouriteTabScreen(
                        favouriteTracksViewModel = favouriteTracksViewModel,
                        onTrackClick = onTrackClick,
                        darkThemeEnabled = darkThemeEnabled,
                    )

                    1 -> PlaylistScreen(
                        playlistsViewModel = playlistsViewModel,
                        onPlaylistClick = { playlist ->
                            playlistsViewModel.saveCurrentPlaylistId(playlist.id)
                            navController.navigate(R.id.action_mediaLibraryFragment_to_openPlaylistFragment)
                        },
                        onNewPlaylistClick = {
                            navController.navigate(R.id.newPlaylistFragment)
                        }
                    )
                }
            }
        }
    }
}
