package com.notsatria.bajet.ui.screen.settings.configuration

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
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
        onThemeSelected = viewModel::setThemeMode
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
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())

    if (state.showThemeDialog.value) {
        ThemeListDialog(
            showDialog = state.showThemeDialog,
            selectedTheme = state.theme,
            onThemeSelected = {
                onThemeSelected(it)
                state.showThemeDialog.value = false
            })
    }

    Scaffold(modifier.nestedScroll(scrollBehavior.nestedScrollConnection), topBar = {
        CenterAlignedTopAppBar(
            title = {
                Text(
                    stringResource(R.string.configuration),
                    style = MaterialTheme.typography.headlineMedium
                )
            },
            navigationIcon = {
                IconButton(onClick = navigateBack) {
                    Icon(Icons.AutoMirrored.Default.ArrowBack, stringResource(R.string.back))
                }
            },
            scrollBehavior = scrollBehavior
        )
    }) { padding ->
        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            LazyColumn(
                Modifier
                    .fillMaxSize()
            ) {
                item {
                    ListItem(
                        headlineContent = { Text(state.titles[0]) },
                        modifier = Modifier.clickable { state.showThemeDialog.value = true }
                    )
                    HorizontalDivider()
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
    AlertDialog(
        onDismissRequest = { showDialog.value = false },
        title = { Text(stringResource(R.string.theme)) },
        text = {
            Column(Modifier.selectableGroup()) {
                themes.forEach { theme ->
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .selectable(
                                selected = (theme == selectedTheme),
                                onClick = { onThemeSelected(theme) },
                                role = Role.RadioButton
                            )
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = (theme == selectedTheme),
                            onClick = null // null recommended for accessibility with screenreaders
                        )
                        Text(
                            text = theme,
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(start = 16.dp)
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = { showDialog.value = false }) {
                Text(stringResource(R.string.ok))
            }
        }
    )
}

data class ConfigurationUiState(
    val titles: List<String>,
    val theme: String = "",
    val showThemeDialog: MutableState<Boolean> = mutableStateOf(false)
)

@Preview
@Composable
fun ConfigurationScreenPreview() {
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