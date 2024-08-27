package com.example.yp_playlist.ui.theme

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.yp_playlist.R

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


@Composable
fun NoDataPlaceholder(
    darkThemeEnabled: Boolean
) {
    val textColor = if (darkThemeEnabled) MaterialTheme.colors.onSecondary else Color.Black
    val fontFamily = FontFamily(Font(R.font.ys_display_medium))

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(86.dp),

        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Icon(
            painter = painterResource(id = R.drawable.error_no_data),
            contentDescription = null,
            tint = Color.Unspecified
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = stringResource(id = R.string.no_data),
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
    onClick: () -> Unit,
    onClearClick: () -> Unit,
    onSearchTextChanged: (String) -> Unit
) {
    val backgroundColor = if (darkThemeEnabled) Color.White else Color.Black
    val textColor = if (darkThemeEnabled) Color.Black else Color.White
    val fontFamily = FontFamily(Font(R.font.ys_display_medium))

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Button(
            onClick = {
                onClick()
                onClearClick()
                onSearchTextChanged("")
            },
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
                text = stringResource(id = R.string.clear_history),
                fontFamily = fontFamily,
                fontSize = 14.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}
