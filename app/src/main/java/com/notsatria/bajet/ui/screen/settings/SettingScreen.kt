package com.notsatria.bajet.ui.screen.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Backup
import androidx.compose.material.icons.outlined.Feedback
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Password
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.notsatria.bajet.R
import com.notsatria.bajet.ui.theme.BajetTheme

@Composable
fun SettingRoute(modifier: Modifier = Modifier, navigateToConfigurationScreen: () -> Unit = {}) {
    val settings = stringArrayResource(R.array.settings_title).toList()
    SettingScreen(
        modifier,
        state = SettingUiState(
            settings = settings
        ),
        navigateToConfigurationScreen = navigateToConfigurationScreen
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingScreen(
    modifier: Modifier = Modifier,
    state: SettingUiState,
    navigateToConfigurationScreen: () -> Unit = {}
) {
    Scaffold(modifier, topBar = {
        TopAppBar(
            title = {
                Text(stringResource(R.string.settings))
            }
        )
    }) { padding ->
        Box(
            Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.Center,
                userScrollEnabled = false
            ) {
                items(state.settings.size) {
                    SettingItem(icon = state.icons[it], title = state.settings[it], onClick = {
                        when (it) {
                            0 -> {
                                navigateToConfigurationScreen()
                            }
                        }
                    })
                }
            }
        }
    }
}

@Composable
fun SettingItem(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    title: String,
    onClick: () -> Unit
) {
    Column(
        modifier = modifier
            .clickable {
                onClick()
            }
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(16.dp))
        Icon(
            icon,
            title,
            modifier = Modifier.size(56.dp),
            tint = MaterialTheme.colorScheme.onPrimaryContainer
        )
        Spacer(Modifier.height(8.dp))
        Text(
            title,
            style = MaterialTheme.typography.titleMedium,
        )
        Spacer(Modifier.height(16.dp))
    }
}

data class SettingUiState(
    val icons: List<ImageVector> =
        listOf(
            Icons.Outlined.Settings,
            Icons.Outlined.Password,
            Icons.Outlined.Backup,
            Icons.Outlined.Info,
            Icons.Outlined.Feedback
        ),
    val settings: List<String>
)

@Preview
@Composable
fun SettingScreenPreview(modifier: Modifier = Modifier) {
    val settings = stringArrayResource(R.array.settings_title).toList()
    val state = SettingUiState(
        settings = settings
    )
    BajetTheme {
        SettingScreen(state = state)
    }
}