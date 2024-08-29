package com.example.yp_playlist.presentation.search.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.yp_playlist.R
import com.example.yp_playlist.domain.Track
import com.example.yp_playlist.presentation.search.SearchViewModel
import com.example.yp_playlist.resultitem.ui.ResultItem
import com.example.yp_playlist.settings.ui.SettingsViewModel
import com.example.yp_playlist.ui.theme.ClearHistoryButton
import com.example.yp_playlist.ui.theme.ConnectionProblemPlaceholder
import com.example.yp_playlist.ui.theme.NoDataPlaceholder
import com.example.yp_playlist.ui.theme.YourAppTheme

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
                onSearchAction = onSearchAction
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
                                ResultItem(
                                    trackImage = track.artworkUrl100,
                                    trackName = track.trackName,
                                    artistName = track.artistName,
                                    trackTimeMillis = track.trackTimeMillis,
                                    onClick = {
                                        onTrackClicked(track)
                                        onTrackClick(track)
                                    },
                                    darkTheme = darkThemeEnabled
                                )
                            }
                            item {
                                ClearHistoryButton(
                                    darkThemeEnabled = darkThemeEnabled,
                                    onClick = onClearHistoryClick,
                                    onClearClick = { onSearchTextChanged("") },
                                    onSearchTextChanged = onSearchTextChanged
                                )
                            }
                        }
                    }
                    fragmentState == SearchViewModel.FragmentState.SUCCESS && searchResults.isNotEmpty() -> {
                        LazyColumn {
                            items(searchResults.size) { index ->
                                val track = searchResults[index]
                                ResultItem(
                                    trackImage = track.artworkUrl100,
                                    trackName = track.trackName,
                                    artistName = track.artistName,
                                    trackTimeMillis = track.trackTimeMillis,
                                    onClick = {
                                        onTrackClicked(track)
                                        onTrackClick(track)
                                    },
                                    darkTheme = darkThemeEnabled
                                )
                            }
                        }
                    }
                    fragmentState == SearchViewModel.FragmentState.EMPTY -> {
                        NoDataPlaceholder(darkThemeEnabled)
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
) {

    val textColor = colorResource(id = R.color.black)
    val placeholderColor = colorResource(id = R.color.light_grey)
    val fontFamily = FontFamily(Font(R.font.ys_display_medium))

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .height(36.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(colorResource(id = R.color.grey_tint_search)),
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
                fontFamily = fontFamily,
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
                        fontFamily = fontFamily,
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