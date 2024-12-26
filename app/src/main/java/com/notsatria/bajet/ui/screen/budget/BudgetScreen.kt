package com.notsatria.bajet.ui.screen.budget

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.notsatria.bajet.R
import com.notsatria.bajet.data.entities.CashFlowSummary
import com.notsatria.bajet.ui.screen.home.CashFlowSummaryCard
import com.notsatria.bajet.ui.theme.BajetTheme
import java.util.Calendar

@Composable
fun BudgetRoute(modifier: Modifier = Modifier, navigateToAddBudgetScreen: () -> Unit = {}) {
    BudgetScreen(modifier, onSettingsClicked = navigateToAddBudgetScreen)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BudgetScreen(modifier: Modifier = Modifier, onSettingsClicked: () -> Unit = {}) {
    Scaffold(
        topBar = {
            TopAppBar(title = { Text(stringResource(R.string.my_budget)) }, actions = {
                TextButton(onClick = onSettingsClicked) {
                    Icon(imageVector = Icons.Default.AccountBalanceWallet, null)
                    Spacer(Modifier.width(8.dp))
                    Text(stringResource(R.string.settings))
                }
            })
        },
    ) {
        Column(modifier = Modifier.padding(it)) {
            CashFlowSummaryCard(
                modifier = Modifier.padding(16.dp),
                cashFlowSummary = CashFlowSummary(1000.0, 500.0, 500.0),
                onPreviousMonthClick = {},
                onNextMonthClick = {},
                selectedMonth = Calendar.getInstance()
            )
            LazyColumn {
                items(5 + 1) { index ->
                    if (index == 0) {
                        MonthlyBudgetItem()
                        HorizontalDivider(
                            thickness = 1.dp,
                            color = MaterialTheme.colorScheme.outlineVariant
                        )
                    }
                    BudgetItem()
                    HorizontalDivider(
                        thickness = 1.dp,
                        color = MaterialTheme.colorScheme.outlineVariant
                    )
                }
            }
        }
    }
}

@Composable
fun BudgetItem(modifier: Modifier = Modifier) {
    Row(modifier, verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .padding(start = 16.dp)
                .size(42.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(MaterialTheme.colorScheme.tertiaryContainer)
        ) {
            Text(text = "🤹", modifier = Modifier.align(Alignment.Center))
        }
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.padding(end = 16.dp, top = 8.dp, bottom = 8.dp)) {
            Text("Category name", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(4.dp))
            LinearProgressIndicator(progress = {
                val progress = 0.5f
                progress
            }, modifier = Modifier.fillMaxWidth())
            Spacer(Modifier.height(8.dp))
            Row {
                Text("Rp1000", style = MaterialTheme.typography.labelLarge)
                Spacer(Modifier.weight(1f))
                Text(
                    "Rp1000",
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.labelLarge
                )

            }
        }
    }
}

@Composable
fun MonthlyBudgetItem(modifier: Modifier = Modifier) {
    Row(modifier, verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .padding(start = 16.dp)
                .size(42.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(MaterialTheme.colorScheme.tertiaryContainer)
        ) {
            Text(text = "\uD83D\uDCC5", modifier = Modifier.align(Alignment.Center))
        }
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.padding(end = 16.dp, top = 8.dp, bottom = 8.dp)) {
            Text("Month", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(4.dp))
            LinearProgressIndicator(progress = {
                val progress = 0.5f
                progress
            }, modifier = Modifier.fillMaxWidth())
            Spacer(Modifier.height(8.dp))
            Row {
                Text("Rp1000", style = MaterialTheme.typography.labelLarge)
                Spacer(Modifier.weight(1f))
                Text(
                    "Rp1000",
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.labelLarge
                )

            }
        }
    }
}

@Preview
@Composable
fun BudgetItemPreview(modifier: Modifier = Modifier) {
    BajetTheme {
        BudgetItem(modifier.background(MaterialTheme.colorScheme.surfaceContainer))
    }
}

@Preview
@Composable
fun BudgetScreenPreview() {
    BajetTheme {
        BudgetScreen()
    }
}