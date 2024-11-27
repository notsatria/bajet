package com.notsatria.bajet.ui.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun HomeScreen(modifier: Modifier = Modifier) {
    Scaffold(modifier) { paddingValues ->
        Column(modifier.padding(paddingValues)) {
            Card {
                Row {
                    Column {
                        Text("Income")
                        Text("Rp 20.000")
                    }
                    Column {
                        Text("Expenses")
                        Text("Rp 20.000")
                    }
                    Column {
                        Text("Balance")
                        Text("Rp 20.000")
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun HomeScreenPreview() {
    HomeScreen()
}