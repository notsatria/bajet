package com.notsatria.bajet.ui.screen.budget.setting

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.Delete
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
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.notsatria.bajet.R
import com.notsatria.bajet.data.entities.relation.BudgetWithCategoryAndBudgetEntry
import com.notsatria.bajet.ui.components.EmptyView
import com.notsatria.bajet.ui.theme.BajetTheme
import com.notsatria.bajet.utils.DummyData
import com.notsatria.bajet.utils.formatToRupiah

@Composable
fun BudgetSettingRoute(
    modifier: Modifier = Modifier,
    navigateBack: () -> Unit,
    navigateToAddBudgetScreen: () -> Unit,
    navigateToEditBudgetScreen: (budgetId: Int) -> Unit,
    viewModel: BudgetSettingViewModel = hiltViewModel()
) {
    val budgetList by viewModel.budgetList.collectAsStateWithLifecycle()

    BudgetSettingScreen(
        modifier,
        event = BudgetSettingEvent(
            onNavigateBackClicked = navigateBack,
            onAddClicked = navigateToAddBudgetScreen,
            navigateToEditBudgetScreen = navigateToEditBudgetScreen
        ),
        state = BudgetSettingUiState(budgetList)
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BudgetSettingScreen(
    modifier: Modifier = Modifier,
    event: BudgetSettingEvent = BudgetSettingEvent(),
    state: BudgetSettingUiState = BudgetSettingUiState()
) {
    Scaffold(modifier, topBar = {
        TopAppBar(
            title = { Text(text = "My Budget") },
            navigationIcon = {
                IconButton(onClick = event.onNavigateBackClicked) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Default.ArrowBack,
                        contentDescription = stringResource(
                            R.string.back
                        )
                    )
                }
            },
            actions = {
                IconButton(onClick = event.onAddClicked) {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = stringResource(R.string.add_budget)
                    )
                }
            })
    }) { innerPadding ->
        if (state.budgetList.isEmpty()) {
            EmptyView(
                modifier = Modifier
                    .fillMaxSize(),
                drawable = R.drawable.ic_no_budget_found_24,
                text = stringResource(R.string.no_budget_found)
            )
        }
        LazyColumn(
            modifier = Modifier
                .padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            items(state.budgetList) { budgetAndCategory ->
                BudgetCategoryItem(
                    emoji = budgetAndCategory.categoryEmoji,
                    categoryName = budgetAndCategory.categoryName,
                    amount = budgetAndCategory.budgetAmount,
                    budgetId = budgetAndCategory.budgetId,
                    navigateToEditBudget = { budgetId ->
                        event.navigateToEditBudgetScreen(budgetId)
                    }
                )
                HorizontalDivider()
            }
        }
    }
}

@Composable
fun BudgetCategoryItem(
    modifier: Modifier = Modifier,
    emoji: String = "😭",
    categoryName: String = "Category name",
    amount: Double = 0.0,
    budgetId: Int = 0,
    navigateToEditBudget: (budgetId: Int) -> Unit = {},
    onDeleteClick: () -> Unit = {}
) {
    Row(modifier, verticalAlignment = Alignment.CenterVertically) {
        Text(text = emoji, modifier = Modifier.padding(start = 12.dp))
        Spacer(modifier = Modifier.width(4.dp))
        Text(text = categoryName)
        Spacer(modifier = Modifier.weight(1f))
        Text(text = amount.formatToRupiah())
        Spacer(modifier = Modifier.width(8.dp))
        IconButton(onClick = {
            navigateToEditBudget(budgetId)
        }) {
            Icon(
                imageVector = Icons.Outlined.Edit,
                contentDescription = stringResource(R.string.edit_budget),
                tint = MaterialTheme.colorScheme.outline
            )
        }
        IconButton(onClick = {}) {
            Icon(
                imageVector = Icons.Outlined.Delete,
                contentDescription = stringResource(R.string.delete_budget),
                tint = MaterialTheme.colorScheme.outline
            )
        }
    }
}

data class BudgetSettingUiState(
    val budgetList: List<BudgetWithCategoryAndBudgetEntry> = emptyList(),
)

data class BudgetSettingEvent(
    val onNavigateBackClicked: () -> Unit = {},
    val onAddClicked: () -> Unit = {},
    val navigateToEditBudgetScreen: (Int) -> Unit = {}
)

@Preview
@Composable
fun BudgetSettingScreenPreview() {
    BajetTheme {
        BudgetSettingScreen(
            state = BudgetSettingUiState(
                budgetList = listOf(
                    BudgetWithCategoryAndBudgetEntry(
                        categoryName = DummyData.categories[0].name,
                        categoryEmoji = DummyData.categories[0].emoji,
                        budgetAmount = DummyData.budgetEntries[0].amount,
                        budgetId = 0
                    ),
                    BudgetWithCategoryAndBudgetEntry(
                        categoryName = DummyData.categories[1].name,
                        categoryEmoji = DummyData.categories[1].emoji,
                        budgetAmount = DummyData.budgetEntries[0].amount,
                        budgetId = 0
                    )
                )
            )
        )
    }
}

@Preview
@Composable
fun BudgetEmptyScreenPreview() {
    BajetTheme {
        BudgetSettingScreen()
    }
}