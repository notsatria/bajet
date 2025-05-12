package com.notsatria.bajet.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.notsatria.bajet.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BajetTopBar(
    title: String,
    canNavigateBack: Boolean = false,
    navigateBack: () -> Unit = {}
) {
    TopAppBar(
        modifier = Modifier,
        title = {
            Text(title)
        },
        navigationIcon = {
            if (canNavigateBack) IconButton(onClick = navigateBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Default.ArrowBack,
                    contentDescription = stringResource(R.string.back)
                )
            } else null
        }
    )
}