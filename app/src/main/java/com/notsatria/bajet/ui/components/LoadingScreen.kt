package com.notsatria.bajet.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.notsatria.bajet.ui.theme.BajetTheme

@Composable
fun LoadingScreen(modifier: Modifier = Modifier) {
    Scaffold { innerPadding ->
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(
                modifier = modifier
                    .padding(innerPadding)
                    .wrapContentSize()
            )
        }
    }
}

@Preview
@Composable
private fun Preview() {
    BajetTheme {
        LoadingScreen()
    }
}