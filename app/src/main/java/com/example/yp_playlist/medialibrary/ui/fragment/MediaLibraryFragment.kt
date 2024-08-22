package com.example.yp_playlist.medialibrary.ui.fragment


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import com.example.yp_playlist.R
import kotlinx.coroutines.launch
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Tab
import androidx.compose.material.TabRow
import androidx.compose.material.TabRowDefaults
import androidx.compose.material.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


class MediaLibraryFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return ComposeView(requireContext()).apply {
            setContent {
                MediaLibraryScreen()
            }
        }
    }


    @OptIn(ExperimentalFoundationApi::class)
    @Composable
    fun MediaLibraryScreen() {
        val tabs = listOf(stringResource(id = R.string.favourite), stringResource(id = R.string.playlist))
        val pagerState = rememberPagerState(pageCount = { tabs.size }, initialPage = 0)
        val coroutineScope = rememberCoroutineScope()

        Column(modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.background)) {

            TopAppBar(
                title = {
                    Text(
                        text = stringResource(id = R.string.media_button),
                        color = MaterialTheme.colors.onBackground,
                        fontSize = 22.sp
                    )
                },
                backgroundColor = MaterialTheme.colors.background,
                modifier = Modifier.fillMaxWidth(),
                elevation = 0.dp
            )



            TabRow(
                selectedTabIndex = pagerState.currentPage,
                modifier = Modifier
                    .background(color = MaterialTheme.colors.surface),
                indicator = { tabPositions ->
                    TabRowDefaults.Indicator(
                        Modifier.tabIndicatorOffset(tabPositions[pagerState.currentPage]),
                        color = MaterialTheme.colors.onSecondary
                    )
                },
                backgroundColor = MaterialTheme.colors.surface
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
                                color = if (pagerState.currentPage == index) MaterialTheme.colors.onSecondary else Color.Gray,
                                fontSize = 14.sp,
                                fontFamily = FontFamily.SansSerif, 
                                fontWeight = FontWeight.Medium,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                    )
                }
            }

            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize()
            ) { page ->
                when (page) {
                    0 -> FavouriteScreen()
                    1 -> PlaylistScreen()
                }
            }
        }
    }

    @Composable
    fun FavouriteScreen() {
    }

    @Composable
    fun PlaylistScreen() {
    }

    @Preview
    @Composable
    fun PreviewMediaLibraryScreen() {
        MediaLibraryScreen()
    }


}