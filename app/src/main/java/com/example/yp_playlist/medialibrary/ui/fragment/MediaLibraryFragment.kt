package com.example.yp_playlist.medialibrary.ui.fragment


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import com.example.yp_playlist.R
import kotlinx.coroutines.launch
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Tab
import androidx.compose.material.TabRow
import androidx.compose.material.TabRowDefaults
import androidx.compose.material.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.yp_playlist.domain.Track
import com.example.yp_playlist.medialibrary.favourite.ui.models.FavouriteTracksState
import com.example.yp_playlist.medialibrary.favourite.ui.viewmodel.FavouriteTracksViewModel
import com.example.yp_playlist.medialibrary.playlists.domain.models.Playlist
import com.example.yp_playlist.medialibrary.playlists.ui.models.PlaylistsScreenState
import com.example.yp_playlist.medialibrary.playlists.ui.viewmodel.PlaylistsViewModel
import com.example.yp_playlist.presentation.media.MediaFragment
import com.example.yp_playlist.settings.ui.SettingsViewModel
import com.example.yp_playlist.settings.ui.YourAppTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.SimpleDateFormat
import java.util.Locale


class MediaLibraryFragment : Fragment() {

    private val favouriteTracksViewModel by viewModel<FavouriteTracksViewModel>()
    private val playlistsViewModel by viewModel<PlaylistsViewModel>()
    private val settingsViewModel by viewModel<SettingsViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                MediaLibraryScreen(
                    settingsViewModel = settingsViewModel,
                    favouriteTracksViewModel = favouriteTracksViewModel,
                    onTrackClick = { track ->
                        findNavController().navigate(
                            R.id.action_mediaLibraryFragment_to_playerFragment,
                            MediaFragment.createArgs(track.trackId)
                        )
                    }
                )
            }
        }
    }

    override fun onResume() {
        super.onResume()
        favouriteTracksViewModel.getFavouriteTracks()
        playlistsViewModel.fillData()
    }


    @OptIn(ExperimentalFoundationApi::class)
    @Composable
    fun MediaLibraryScreen(
        settingsViewModel: SettingsViewModel,
        favouriteTracksViewModel: FavouriteTracksViewModel,
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
                                findNavController().navigate(R.id.action_mediaLibraryFragment_to_openPlaylistFragment)
                            },
                            onNewPlaylistClick = {
                                findNavController().navigate(R.id.newPlaylistFragment)
                            }
                        )
                    }
                }
            }
        }
    }


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
                        FavouriteScreen(
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



    @Composable
    fun FavouriteScreen(
        trackImage: String,
        trackName: String,
        artistName: String,
        trackTimeMillis: Long,
        onClick: () -> Unit,
        darkTheme: Boolean
    ) {
        val context = LocalContext.current
        var bitmap by remember { mutableStateOf<android.graphics.Bitmap?>(null) }

        val titleTextColor = if (darkTheme) {
            Color.White
        } else {
            colorResource(id = R.color.button_text)
        }

        val subTextColor = if (darkTheme) {
            Color.White
        } else {
            colorResource(id = R.color.light_grey)
        }

        val fontFamily = FontFamily(Font(R.font.ys_display_medium))

        DisposableEffect(trackImage) {
            val job = CoroutineScope(Dispatchers.IO).launch {
                try {
                    val futureBitmap = Glide.with(context)
                        .asBitmap()
                        .load(trackImage)
                        .submit()
                        .get()
                    withContext(Dispatchers.Main) {
                        bitmap = futureBitmap
                    }
                } catch (e: Exception) {
                }
            }
            onDispose {
                job.cancel()
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onClick)
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .background(MaterialTheme.colors.background)
        ) {
            bitmap?.let {
                Image(
                    bitmap = it.asImageBitmap(),
                    contentDescription = null,
                    modifier = Modifier
                        .size(45.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .align(Alignment.CenterVertically),
                    contentScale = ContentScale.Crop
                )
            } ?: run {
                Box(
                    modifier = Modifier
                        .size(45.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.LightGray)
                        .align(Alignment.CenterVertically)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier
                    .weight(1f)
                    .align(Alignment.CenterVertically)
            ) {
                Text(
                    text = trackName,
                    color = titleTextColor,
                    fontSize = 16.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    fontFamily = fontFamily
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = artistName,
                        color = subTextColor,
                        style = TextStyle(fontSize = 12.sp, fontFamily = fontFamily),
                        maxLines = 1
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        painter = painterResource(id = R.drawable.ellipse),
                        contentDescription = null,
                        tint = subTextColor,
                        modifier = Modifier
                            .size(4.dp)
                            .align(Alignment.CenterVertically)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = SimpleDateFormat(
                            "mm:ss",
                            Locale.getDefault()
                        ).format(trackTimeMillis),
                        color = subTextColor,
                        style = TextStyle(fontSize = 12.sp, fontFamily = fontFamily)
                    )
                }
            }
            Spacer(modifier = Modifier.width(16.dp))
            Icon(
                painter = painterResource(id = R.drawable.arrow_forward),
                contentDescription = null,
                tint = subTextColor,
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .size(14.dp)
            )
        }
    }


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
    fun AddPlayListButton(
        darkThemeEnabled: Boolean,
        onNewPlaylistClick: () -> Unit
    ) {
        val backgroundColor = if (darkThemeEnabled) Color.White else Color.Black
        val textColor = if (darkThemeEnabled) Color.Black else Color.White
        val fontFamily = FontFamily(Font(R.font.ys_display_medium))

        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Button(
                onClick = onNewPlaylistClick,
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = backgroundColor,
                    contentColor = textColor
                ),
                shape = RoundedCornerShape(50.dp),
                modifier = Modifier
                    .wrapContentWidth()
                    .height(56.dp)
                    .wrapContentHeight()
                    .padding(top = 8.dp)
            ) {
                Text(
                    text = stringResource(id = R.string.newPlaylist),
                    fontFamily = fontFamily,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center
                )
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

    @Composable
    fun PlaylistItem(
        playlist: Playlist,
        onClick: () -> Unit
    ) {
        val context = LocalContext.current
        var bitmap by remember { mutableStateOf<android.graphics.Bitmap?>(null) }
        val placeholder = painterResource(id = R.drawable.placeholder)

        DisposableEffect(playlist.imageUri) {
            val job = CoroutineScope(Dispatchers.IO).launch {
                try {
                    val futureBitmap = playlist.imageUri?.let { uri ->
                        Glide.with(context)
                            .asBitmap()
                            .load(uri)
                            .submit()
                            .get()
                    }
                    withContext(Dispatchers.Main) {
                        bitmap = futureBitmap
                    }
                } catch (e: Exception) {
                }
            }
            onDispose { job.cancel() }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onClick() }
                .padding(4.dp)
                .background(MaterialTheme.colors.background)
        ) {
            Card(
                shape = RoundedCornerShape(8.dp),
                elevation = 0.dp,
                modifier = Modifier
                    .aspectRatio(1f)
                    .fillMaxWidth()
            ) {
                if (bitmap != null) {
                    Image(
                        bitmap = bitmap!!.asImageBitmap(),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colors.background)
                    )
                } else {
                    Image(
                        painter = placeholder,
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }

            Text(
                text = playlist.title,
                color = MaterialTheme.colors.onSurface,
                fontSize = 12.sp,
                maxLines = 1,
                modifier = Modifier
                    .padding(top = 4.dp)
            )

            Text(
                text = LocalContext.current.resources.getQuantityString(
                    R.plurals.tracks, playlist.size ?: 0, playlist.size ?: 0
                ),
                color = MaterialTheme.colors.onSurface,
                fontSize = 12.sp,
                maxLines = 1
            )
        }
    }


    @Composable
    fun NotCreatePlaylist(
        darkThemeEnabled: Boolean
    ) {
        val textColor = if (darkThemeEnabled) MaterialTheme.colors.onSecondary else Color.Black
        val fontFamily = FontFamily(Font(R.font.ys_display_medium))

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),

            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Icon(
                painter = painterResource(id = R.drawable.error_no_data),
                contentDescription = null,
                tint = Color.Unspecified
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = stringResource(id = R.string.not_create_Playlist),
                color = textColor,
                fontSize = 19.sp,
                fontFamily = fontFamily,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .wrapContentWidth()
                    .wrapContentHeight()
                    .align(Alignment.CenterHorizontally)
            )
        }
    }

}


