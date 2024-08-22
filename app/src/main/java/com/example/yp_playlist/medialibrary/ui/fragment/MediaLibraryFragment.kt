package com.example.yp_playlist.medialibrary.ui.fragment


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import com.example.yp_playlist.R
import kotlinx.coroutines.launch
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bumptech.glide.Glide
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Locale


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
                backgroundColor = Color.White,
                elevation = 0.dp,
                modifier = Modifier
                    .height(56.dp)
            )

            TabRow(
                selectedTabIndex = pagerState.currentPage,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .padding(horizontal = 0.dp),
                backgroundColor = Color.White,
                contentColor = Color.Black,
                indicator = { tabPositions ->
                    TabRowDefaults.Indicator(
                        Modifier.tabIndicatorOffset(tabPositions[pagerState.currentPage]),
                        color = Color.Black,
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
                                color = if (pagerState.currentPage == index) Color.Black else Color.Gray,
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
                        text = SimpleDateFormat("mm:ss", Locale.getDefault()).format(trackTimeMillis),
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
    fun PlaylistScreen() {
    }

    @Preview
    @Composable
    fun PreviewMediaLibraryScreen() {
        MediaLibraryScreen()
    }


}