package com.notsatria.bajet.ui.screen.budget

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.notsatria.bajet.R
import com.notsatria.bajet.ui.theme.BajetTheme

@Composable
fun BudgetSettingRoute(
    modifier: Modifier = Modifier,
    navigateBack: () -> Unit = {},
    navigateToAddBudgetScreen: () -> Unit = {}
) {
    BudgetSettingScreen(
        modifier,
        onNavigateBackClicked = navigateBack,
        onAddClicked = navigateToAddBudgetScreen
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BudgetSettingScreen(
    modifier: Modifier = Modifier,
    onNavigateBackClicked: () -> Unit = {},
    onAddClicked: () -> Unit = {},
) {
    Scaffold(modifier, topBar = {
        TopAppBar(title = { Text(text = "My Budget") },
            navigationIcon = {
                IconButton(onClick = onNavigateBackClicked) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Default.ArrowBack,
                        contentDescription = stringResource(
                            R.string.back
                        )
                    )
                }
            },
            actions = {
                IconButton(onClick = onAddClicked) {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = stringResource(R.string.add_budget)
                    )
                }
            })
    }) { innerPadding ->
        LazyColumn(modifier = Modifier.padding(innerPadding)) {
            items(10) {
                BudgetCategoryItem()
                HorizontalDivider()
            }
        }
    }
}

@Composable
fun BudgetCategoryItem(modifier: Modifier = Modifier) {
    Row(modifier, verticalAlignment = Alignment.CenterVertically) {
        Text(text = "😭", modifier = Modifier.padding(start = 12.dp))
        Spacer(modifier = Modifier.width(4.dp))
        Text(text = "Category name")
        Spacer(modifier = Modifier.weight(1f))
        Text(text = "Rp40000")
        Spacer(modifier = Modifier.width(8.dp))
        IconButton(onClick = { }) {
            Icon(
                imageVector = Icons.Outlined.Edit,
                contentDescription = "Edit budget",
                tint = MaterialTheme.colorScheme.outline
            )
        }
    }
}

@Preview
@Composable
fun BudgetSettingScreenPreview() {
    BajetTheme {
        BudgetSettingScreen()
    }
}