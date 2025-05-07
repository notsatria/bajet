package com.notsatria.bajet.ui.screen.settings.configuration

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import com.notsatria.bajet.R
import com.notsatria.bajet.ui.theme.BajetTheme

@Composable
fun ConfigurationRoute(
    modifier: Modifier = Modifier,
    navigateBack: () -> Unit,
    viewModel: ConfigurationViewModel = hiltViewModel()
) {
    val titles = stringArrayResource(R.array.configuration_items).toList()
    val theme = viewModel.theme.collectAsState(initial = "")

    ConfigurationScreen(
        modifier,
        state = ConfigurationUiState(
            titles = titles,
            theme = theme.value,
        ),
        navigateBack = navigateBack,
        onThemeSelected = {
            viewModel.setThemeMode(it)
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConfigurationScreen(
    modifier: Modifier = Modifier,
    state: ConfigurationUiState,
    navigateBack: () -> Unit = {},
    onThemeSelected: (String) -> Unit = {}
) {
    if (state.showThemeDialog.value) {
        ThemeListDialog(
            showDialog = state.showThemeDialog,
            selectedTheme = state.theme,
            onThemeSelected = {
                onThemeSelected(it)
                state.showThemeDialog.value = false
            })
    }

    Scaffold(modifier, topBar = {
        TopAppBar(title = {
            Text(stringResource(R.string.configuration))
        }, navigationIcon = {
            IconButton(onClick = navigateBack) {
                Icon(Icons.AutoMirrored.Default.ArrowBack, stringResource(R.string.back))
            }
        })
    }) { padding ->
        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            LazyColumn(
                Modifier
                    .fillMaxSize()
                    .padding(top = 12.dp)
            ) {
                items(state.titles.size) {
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .background(color = MaterialTheme.colorScheme.surfaceContainer)
                            .clickable {
                                when (it) {
                                    0 -> {
                                        state.showThemeDialog.value = true
                                    }
                                }
                            }) {
                        Text(
                            state.titles[it], modifier = Modifier
                                .padding(vertical = 8.dp)
                                .padding(start = 20.dp),
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    HorizontalDivider(modifier = Modifier.alpha(0.5f))
                }
            }
        }
    }
}

@Composable
fun ThemeListDialog(
    showDialog: MutableState<Boolean> = mutableStateOf(false),
    selectedTheme: String = "",
    onThemeSelected: (String) -> Unit = {}
) {
    val themes = stringArrayResource(R.array.theme_list)
    Dialog(onDismissRequest = {
        showDialog.value = false
    }) {
        Card(
            modifier = Modifier
                .clip(RoundedCornerShape(16.dp))
                .fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .padding(vertical = 16.dp)
                    .fillMaxWidth()
            ) {
                repeat(themes.size) {
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .clickable {
                                onThemeSelected(themes[it])
                            }, verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(themes[it], Modifier.padding(horizontal = 16.dp, vertical = 8.dp))
                        if (selectedTheme == themes[it]) Icon(
                            Icons.Default.Check,
                            stringResource(R.string.selected),
                            modifier = Modifier.padding(end = 16.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
    }
}

data class ConfigurationUiState(
    val titles: List<String>,
    val theme: String = "",
    val showThemeDialog: MutableState<Boolean> = mutableStateOf(false)
)

@Preview
@Composable
fun ConfigurationScreenPreview(modifier: Modifier = Modifier) {
    val titles = stringArrayResource(R.array.configuration_items).toList()

    BajetTheme {
        ConfigurationScreen(state = ConfigurationUiState(titles = titles))
    }
}

@Preview
@Composable
private fun ThemeListDialogPreview() {
    BajetTheme {
        ThemeListDialog()
    }
}