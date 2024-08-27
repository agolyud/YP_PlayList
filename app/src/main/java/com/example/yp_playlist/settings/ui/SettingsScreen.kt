package com.example.yp_playlist.settings.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Switch
import androidx.compose.material.SwitchDefaults
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.yp_playlist.R
import com.example.yp_playlist.ui.theme.YourAppTheme

@Composable
fun SettingsScreen(viewModel: SettingsViewModel) {
    val themeSettings by viewModel.themeSettingsState.observeAsState()

    val darkThemeEnabled = themeSettings?.darkTheme ?: false

    SettingsScreenContent(
        darkThemeEnabled = darkThemeEnabled,
        onThemeSwitch = { viewModel.switchTheme(it) },
        onShareApp = { viewModel.shareApp() },
        onSupportEmail = { viewModel.supportEmail() },
        onOpenAgreement = { viewModel.openAgreement() }
    )
}

@Composable
fun SettingsScreenContent(
    darkThemeEnabled: Boolean,
    onThemeSwitch: (Boolean) -> Unit,
    onShareApp: () -> Unit,
    onSupportEmail: () -> Unit,
    onOpenAgreement: () -> Unit
) {
    YourAppTheme(darkThemeEnabled) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colors.background)
                .padding(16.dp)
        ) {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(id = R.string.settings_button),
                        color = MaterialTheme.colors.onBackground,
                        fontSize = 22.sp
                    )
                },
                backgroundColor = MaterialTheme.colors.background,
                modifier = Modifier.fillMaxWidth(),
                elevation = 0.dp
            )

            SettingsItem(
                text = stringResource(id = R.string.black_theme),
                onClick = {onThemeSwitch(!darkThemeEnabled)},
                trailingContent = {
                    Switch(
                        checked = darkThemeEnabled,
                        onCheckedChange = onThemeSwitch,
                        colors = SwitchDefaults.colors(checkedThumbColor = MaterialTheme.colors.primary)
                    )
                }
            )

            SettingsItem(
                text = stringResource(id = R.string.share_application),
                onClick = onShareApp,
                trailingContent = {
                    Icon(
                        painter = painterResource(id = R.drawable.share_app),
                        contentDescription = null,
                        tint = MaterialTheme.colors.onBackground
                    )
                }
            )

            SettingsItem(
                text = stringResource(id = R.string.write_to_support),
                onClick = onSupportEmail,
                trailingContent = {
                    Icon(
                        painter = painterResource(id = R.drawable.write_to_support),
                        contentDescription = null,
                        tint = MaterialTheme.colors.onBackground
                    )
                }
            )

            SettingsItem(
                text = stringResource(id = R.string.license_agreement),
                onClick = onOpenAgreement,
                trailingContent = {
                    Icon(
                        painter = painterResource(id = R.drawable.terms_of_use),
                        contentDescription = null,
                        tint = MaterialTheme.colors.onBackground
                    )
                }
            )
        }
    }
}


@Composable
fun SettingsItem(
    text: String,
    onClick: () -> Unit,
    trailingContent: @Composable () -> Unit
) {
    TextButton(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 21.dp),
        contentPadding = PaddingValues(start = 16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = text,
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.body1,
                color = MaterialTheme.colors.onBackground
            )
            trailingContent()
        }
    }
}


@Preview(showBackground = true)
@Composable
fun SettingsScreenPreview() {
    SettingsScreenContent(
        darkThemeEnabled = false,
        onThemeSwitch = {},
        onShareApp = {},
        onSupportEmail = {},
        onOpenAgreement = {}
    )
}