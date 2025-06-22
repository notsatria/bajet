package com.notsatria.bajet.ui.screen.budget

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.notsatria.bajet.R
import com.notsatria.bajet.ui.components.MonthSelection
import com.notsatria.bajet.ui.theme.BajetTheme
import com.notsatria.bajet.utils.DateUtils.formatDate5
import com.notsatria.bajet.utils.DateUtils.formatDateTo
import com.notsatria.bajet.utils.formatToRupiah
import java.util.Calendar

@Composable
fun BudgetRoute(
    modifier: Modifier = Modifier,
    navigateToBudgetSettingScreen: () -> Unit = {},
    viewModel: BudgetViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    BudgetScreen(
        modifier = modifier,
        state = uiState,
        setAction = { action ->
            if (action is BudgetAction.SettingClick) navigateToBudgetSettingScreen()
            viewModel.setAction(action)
        }
    )
}

@Composable
fun BudgetScreen(
    modifier: Modifier = Modifier,
    state: BudgetUiState = BudgetUiState(),
    setAction: (BudgetAction) -> Unit = {}
) {
    Scaffold(
        modifier,
        topBar = {
            BudgetScreenTopBar(
                onSettingsClicked = {
                    setAction(BudgetAction.SettingClick)
                },
                onPreviousMonthClick = {
                    setAction(BudgetAction.PreviousMonth)
                },
                onNextMonthClick = {
                    setAction(BudgetAction.NextMonth)
                },
                selectedMonth = state.selectedMonth
            )
        },
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            Column {
                MonthlyBudgetItem(
                    month = state.selectedMonth,
                    spending = state.totalBudgetWithSpendingPerMonth.totalSpending,
                    budget = state.totalBudgetWithSpendingPerMonth.budget
                )
                HorizontalDivider(
                    thickness = 3.dp, color = MaterialTheme.colorScheme.outlineVariant
                )
                LazyColumn {
                    items(state.budgetList) { budgetItem ->
                        BudgetItem(
                            emoji = budgetItem.emoji,
                            categoryName = budgetItem.categoryName,
                            spending = budgetItem.spending,
                            budget = budgetItem.budget!!,
                            categoryColor = budgetItem.categoryColor
                        )
                        HorizontalDivider(
                            thickness = 1.dp, color = MaterialTheme.colorScheme.outlineVariant
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun BudgetItem(
    modifier: Modifier = Modifier,
    emoji: String,
    categoryName: String,
    categoryColor: Int,
    spending: Double,
    budget: Double
) {
    Row(modifier, verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .padding(start = 16.dp)
                .size(42.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(Color(categoryColor))
        ) {
            Text(text = emoji, modifier = Modifier.align(Alignment.Center))
        }
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.padding(end = 16.dp, top = 8.dp, bottom = 8.dp)) {
            Text(categoryName, style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(4.dp))
            LinearProgressIndicator(
                progress = {
                    val progress = (spending / budget).toFloat()
                    progress
                },
                modifier = Modifier.fillMaxWidth(),
                color = if (spending >= budget) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
            )
            Spacer(Modifier.height(8.dp))
            Row {
                Text(spending.formatToRupiah(), style = MaterialTheme.typography.labelLarge)
                Spacer(Modifier.weight(1f))
                Text(
                    budget.formatToRupiah(),
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.labelLarge
                )

            }
        }
    }
}

@Composable
fun MonthlyBudgetItem(
    modifier: Modifier = Modifier, month: Calendar, spending: Double, budget: Double
) {
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
            Text(month.formatDateTo(formatDate5), style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(4.dp))
            LinearProgressIndicator(progress = {
                val progress = (spending / budget).toFloat()
                progress
            }, modifier = Modifier.fillMaxWidth())
            Spacer(Modifier.height(8.dp))
            Row {
                Text(spending.formatToRupiah(), style = MaterialTheme.typography.labelLarge)
                Spacer(Modifier.weight(1f))
                Text(
                    budget.formatToRupiah(),
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.labelLarge
                )

            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BudgetScreenTopBar(
    onSettingsClicked: () -> Unit,
    onPreviousMonthClick: () -> Unit,
    onNextMonthClick: () -> Unit,
    selectedMonth: Calendar
) {
    TopAppBar(title = {
        MonthSelection(
            selectedMonth = selectedMonth,
            onPreviousMonthClick = onPreviousMonthClick,
            onNextMonthClick = onNextMonthClick
        )
    }, actions = {
        TextButton(onClick = onSettingsClicked) {
            Icon(imageVector = Icons.Default.AccountBalanceWallet, null)
            Spacer(Modifier.width(8.dp))
            Text(stringResource(R.string.settings))
        }
    })
}

@Preview
@Composable
fun BudgetItemPreview(modifier: Modifier = Modifier) {
    BajetTheme {
        BudgetItem(
            modifier.background(MaterialTheme.colorScheme.surfaceContainer),
            emoji = "🤹",
            categoryName = "Food",
            spending = 10000.0,
            budget = 200000.0,
            categoryColor = 0xffa29bfe.toInt()
        )
    }
}

@Preview
@Composable
fun BudgetItemFullPreview(modifier: Modifier = Modifier) {
    BajetTheme {
        BudgetItem(
            modifier.background(MaterialTheme.colorScheme.surfaceContainer),
            emoji = "🤹",
            categoryName = "Food",
            spending = 2000000.0,
            budget = 200000.0,
            categoryColor = 0xffa29bfe.toInt()
        )
    }
}

@Preview
@Composable
fun BudgetScreenPreview() {
    BajetTheme {
        BudgetScreen()
    }
}