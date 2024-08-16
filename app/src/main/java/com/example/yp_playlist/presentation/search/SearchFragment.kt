package com.example.yp_playlist.presentation.search

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.Fragment
import androidx.compose.ui.platform.ComposeView
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.executor.GlideExecutor.UncaughtThrowableStrategy.LOG
import com.example.yp_playlist.R
import com.example.yp_playlist.domain.Track
import com.example.yp_playlist.presentation.media.MediaFragment
import com.example.yp_playlist.presentation.search.SearchViewModel
import com.example.yp_playlist.settings.ui.SettingsViewModel
import com.example.yp_playlist.settings.ui.YourAppTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.SimpleDateFormat
import java.util.Locale

class SearchFragment : Fragment() {

    private val searchViewModel by viewModel<SearchViewModel>()
    private val settingsViewModel by viewModel<SettingsViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                SearchScreen(
                    searchViewModel = searchViewModel,
                    settingsViewModel = settingsViewModel,
                    onTrackClick = { track ->
                        findNavController().navigate(
                            R.id.searchFragment_to_playerFragment,
                            MediaFragment.createArgs(track.trackId)
                        )
                        Log.d ("TrackId222", track.trackId.toString())
                    }
                )
            }
        }
    }
}

@Composable
fun SearchScreen(
    searchViewModel: SearchViewModel,
    settingsViewModel: SettingsViewModel,
    onTrackClick: (Track) -> Unit
) {
    val searchState by searchViewModel.searchState.observeAsState(emptyList())
    val fragmentState by searchViewModel.fragmentState.observeAsState(SearchViewModel.FragmentState.HISTORY_EMPTY)
    val isSearching = fragmentState == SearchViewModel.FragmentState.LOADING
    val themeSettings by settingsViewModel.themeSettingsState.observeAsState()
    val darkThemeEnabled = themeSettings?.darkTheme ?: false
    val historyItems by searchViewModel.tracksHistory.observeAsState(emptyList())
    var searchText by rememberSaveable { mutableStateOf("") }

    LaunchedEffect(Unit) {
        if (searchText.isEmpty()) {
            searchViewModel.setHistory()
        }
    }

    SearchScreenContent(
        darkThemeEnabled = darkThemeEnabled,
        isSearching = isSearching,
        searchResults = searchState ?: emptyList(),
        onSearchTextChanged = { text ->
            searchText = text
            searchViewModel.onSearchTextChanged(text)
        },
        onSearchAction = {
            searchViewModel.searchTrack(searchText)
        },
        fragmentState = fragmentState,
        onRetryClick = {
            searchViewModel.searchTrack(searchText)
        },
        onTrackClick = onTrackClick,
        historyItems = historyItems,
        onClearHistoryClick = {
            searchViewModel.clearHistory()
            searchText = ""
            searchViewModel.clearHistory()
        },
        onTrackClicked = { track ->
            searchViewModel.addTrack(track, 1)
        },
        searchText = searchText
    )
}


@Composable
fun SearchScreenContent(
    darkThemeEnabled: Boolean,
    isSearching: Boolean,
    searchResults: List<Track>,
    onSearchTextChanged: (String) -> Unit,
    onSearchAction: () -> Unit,
    fragmentState: SearchViewModel.FragmentState,
    onRetryClick: () -> Unit,
    onTrackClick: (Track) -> Unit,
    historyItems: List<Track>,
    onClearHistoryClick: () -> Unit,
    onTrackClicked: (Track) -> Unit,
    searchText: String
) {
    YourAppTheme(darkThemeEnabled) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colors.background)
        ) {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(id = R.string.find_button),
                        color = MaterialTheme.colors.onBackground,
                        fontSize = 22.sp
                    )
                },
                backgroundColor = MaterialTheme.colors.background,
                modifier = Modifier.fillMaxWidth(),
                elevation = 0.dp
            )

            SearchBar(
                searchText = searchText,
                onSearchTextChanged = onSearchTextChanged,
                onClearClick = { onSearchTextChanged("") },
                onSearchAction = onSearchAction,
                darkTheme = darkThemeEnabled
            )

            Spacer(modifier = Modifier.height(8.dp))

            Box(modifier = Modifier.fillMaxSize()) {
                when {
                    isSearching -> {
                        CircularProgressIndicator(
                            modifier = Modifier.align(Alignment.Center),
                            color = Color.Black
                        )
                    }
                    searchText.isEmpty() && historyItems.isNotEmpty() -> {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(historyItems.size) { index ->
                                val track = historyItems[index]
                                SearchResultItem(
                                    trackImage = track.artworkUrl100,
                                    trackName = track.trackName,
                                    artistName = track.artistName,
                                    trackTimeMillis = track.trackTimeMillis,
                                    onClick = {
                                        onTrackClicked(track)
                                        onTrackClick(track)
                                    }
                                )
                            }
                            item {
                                ClearHistoryButton(
                                    darkThemeEnabled = darkThemeEnabled,
                                    onClick = onClearHistoryClick
                                )
                            }
                        }
                    }
                    fragmentState == SearchViewModel.FragmentState.SUCCESS && searchResults.isNotEmpty() -> {
                        LazyColumn {
                            items(searchResults.size) { index ->
                                val track = searchResults[index]
                                SearchResultItem(
                                    trackImage = track.artworkUrl100,
                                    trackName = track.trackName,
                                    artistName = track.artistName,
                                    trackTimeMillis = track.trackTimeMillis,
                                    onClick = {
                                        onTrackClicked(track)
                                        onTrackClick(track)
                                    }
                                )
                            }
                        }
                    }
                    fragmentState == SearchViewModel.FragmentState.EMPTY -> {
                        NoDataPlaceholder()
                    }
                    fragmentState == SearchViewModel.FragmentState.ERROR -> {
                        ConnectionProblemPlaceholder(onRetryClick, darkThemeEnabled)
                    }
                }
            }
        }
    }
}


@Composable
fun SearchBar(
    searchText: String,
    onSearchTextChanged: (String) -> Unit,
    onClearClick: () -> Unit,
    onSearchAction: () -> Unit,
    darkTheme: Boolean
) {
    val backgroundColor = if (darkTheme) {
        colorResource(id = R.color.button_text)
    } else {
        colorResource(id = R.color.grey_tint_search)
    }

    val textColor = colorResource(id = R.color.black)
    val placeholderColor = colorResource(id = R.color.light_grey)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .height(36.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(backgroundColor),
        contentAlignment = Alignment.CenterStart
    ) {
        BasicTextField(
            value = searchText,
            onValueChange = {
                onSearchTextChanged(it)
            },
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 42.dp, end = 40.dp, top = 7.dp),
            textStyle = TextStyle(
                color = textColor,
                fontSize = 16.sp,
                textAlign = TextAlign.Start
            ),
            singleLine = true,
            cursorBrush = SolidColor(textColor),
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Search,
                keyboardType = KeyboardType.Text
            ),
            keyboardActions = KeyboardActions(
                onSearch = {
                    onSearchAction()
                }
            ),
            decorationBox = { innerTextField ->
                if (searchText.isEmpty()) {
                    Text(
                        text = stringResource(id = R.string.find_button),
                        color = placeholderColor,
                        fontSize = 16.sp,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                }
                innerTextField()
            }
        )

        Icon(
            painter = painterResource(id = R.drawable.search),
            contentDescription = null,
            tint = placeholderColor,
            modifier = Modifier
                .padding(start = 12.dp)
                .size(20.dp)
                .align(Alignment.CenterStart)
        )

        if (searchText.isNotEmpty()) {
            IconButton(
                onClick = {
                    onClearClick()
                    onSearchTextChanged("")
                },
                modifier = Modifier.align(Alignment.CenterEnd)
            ) {
                Icon(
                    Icons.Filled.Clear,
                    contentDescription = null,
                    tint = placeholderColor,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
fun SearchResultItem(
    trackImage: String,
    trackName: String,
    artistName: String,
    trackTimeMillis: Long,
    onClick: () -> Unit
) {
    val context = LocalContext.current
    var bitmap by remember { mutableStateOf<android.graphics.Bitmap?>(null) }

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
            .background(Color.White)
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
                color = MaterialTheme.colors.onBackground,
                fontSize = 16.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.body1
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = artistName,
                    color = Color.Gray,
                    style = MaterialTheme.typography.body2,
                    maxLines = 1
                )
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    painter = painterResource(id = R.drawable.ellipse),
                    contentDescription = null,
                    tint = Color.Gray,
                    modifier = Modifier
                        .size(4.dp)
                        .align(Alignment.CenterVertically)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = SimpleDateFormat("mm:ss", Locale.getDefault()).format(trackTimeMillis),
                    color = Color.Gray,
                    style = MaterialTheme.typography.body2
                )
            }
        }
        Spacer(modifier = Modifier.width(16.dp))
        Icon(
            painter = painterResource(id = R.drawable.arrow_forward),
            contentDescription = null,
            tint = Color.Gray,
            modifier = Modifier
                .align(Alignment.CenterVertically)
                .size(14.dp)
        )
    }
}



@Composable
fun NoDataPlaceholder() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            painter = painterResource(id = R.drawable.error_no_data),
            contentDescription = null,
            modifier = Modifier.size(64.dp)
        )
        Text(
            text = stringResource(id = R.string.no_data),
            color = MaterialTheme.colors.onBackground,
            fontSize = 18.sp,
            modifier = Modifier.padding(top = 8.dp)
        )
    }
}

@Composable
fun ConnectionProblemPlaceholder(
    onRetryClick: () -> Unit,
    darkThemeEnabled: Boolean
) {
    val textColor = if (darkThemeEnabled) Color.White else Color.Black
    val buttonBackgroundColor = if (darkThemeEnabled) Color.White else Color.Black
    val buttonTextColor = if (darkThemeEnabled) Color.Black else Color.White
    val fontFamily = FontFamily(Font(R.font.ys_display_medium))

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            painter = painterResource(id = R.drawable.error_no_connection),
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = Color.Unspecified
        )
        Text(
            text = stringResource(id = R.string.no_connection),
            color = textColor,
            fontSize = 18.sp,
            fontFamily = fontFamily,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 8.dp)
        )
        Button(
            onClick = onRetryClick,
            colors = ButtonDefaults.buttonColors(
                backgroundColor = buttonBackgroundColor,
                contentColor = buttonTextColor
            ),
            shape = RoundedCornerShape(50.dp),
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Text(
                text = stringResource(id = R.string.refresh_button),
                fontFamily = fontFamily,
                fontSize = 14.sp
            )
        }
    }
}




@Composable
fun ClearHistoryButton(
    darkThemeEnabled: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor = if (darkThemeEnabled) Color.White else Color.Black
    val textColor = if (darkThemeEnabled) Color.Black else Color.White
    val fontFamily = FontFamily(Font(R.font.ys_display_medium))

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Button(
            onClick = onClick,
            colors = ButtonDefaults.buttonColors(
                backgroundColor = backgroundColor,
                contentColor = textColor
            ),
            shape = RoundedCornerShape(50.dp),
            modifier = Modifier
                .padding(16.dp)
                .wrapContentWidth()
                .height(IntrinsicSize.Min)
        ) {
            Text(
                text = stringResource(id = R.string.clear_history),
                fontFamily = fontFamily,
                fontSize = 14.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}




@Preview(showBackground = true)
@Composable
fun PreviewSearchScreenContent() {
    YourAppTheme(darkTheme = false) {
        SearchScreenContent(
            darkThemeEnabled = false,
            isSearching = false,
            searchResults = listOf(
                Track(
                    trackId = 1,
                    trackName = "Последняя Любовь",
                    artistName = "Моргенштерн",
                    trackTimeMillis = 200000,
                    artworkUrl100 = "https://test.ru/1.jpg",
                    collectionName = "Album 1",
                    releaseDate = "10.08.2024",
                    primaryGenreName = "Pop",
                    country = "RU",
                    previewUrl = "https://test.ru/1.mp3"
                ),
                Track(
                    trackId = 2,
                    trackName = "Dissipate",
                    artistName = "Polaris",
                    trackTimeMillis = 180000,
                    artworkUrl100 = "https://test.ru/2.jpg",
                    collectionName = "Album 2",
                    releaseDate = "10.08.2022",
                    primaryGenreName = "Rock",
                    country = "USA",
                    previewUrl = "https://test.ru/2.mp3"
                )
            ),
            onSearchTextChanged = {},
            onSearchAction = {},
            fragmentState = SearchViewModel.FragmentState.SUCCESS,
            onRetryClick = {},
            onTrackClick = {},
            historyItems = emptyList(),
            onClearHistoryClick = {},
            onTrackClicked = {},
            searchText = ""
        )
    }
}

