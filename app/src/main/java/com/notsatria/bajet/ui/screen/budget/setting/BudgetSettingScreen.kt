package com.notsatria.bajet.ui.screen.budget.setting

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.notsatria.bajet.R
import com.notsatria.bajet.data.entities.Budget
import com.notsatria.bajet.data.entities.relation.BudgetAndCategory
import com.notsatria.bajet.data.entities.Category
import com.notsatria.bajet.ui.theme.BajetTheme
import com.notsatria.bajet.utils.DummyData
import com.notsatria.bajet.utils.formatToRupiah
import timber.log.Timber.Forest.d

@Composable
fun BudgetSettingRoute(
    modifier: Modifier = Modifier,
    navigateBack: () -> Unit = {},
    navigateToAddBudgetScreen: () -> Unit = {},
    viewModel: BudgetSettingViewModel = hiltViewModel()
) {
    val budgetList by viewModel.budgetList.collectAsStateWithLifecycle()
    LaunchedEffect(budgetList) {
        viewModel.getAllBudget()
        d("budgetList $budgetList")
    }

    BudgetSettingScreen(
        modifier,
        event = BudgetSettingEvent(
            onNavigateBackClicked = navigateBack,
            onAddClicked = navigateToAddBudgetScreen,
        ),
        uiState = BudgetSettingUiState(budgetList)
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BudgetSettingScreen(
    modifier: Modifier = Modifier,
    event: BudgetSettingEvent = BudgetSettingEvent(),
    uiState: BudgetSettingUiState = BudgetSettingUiState()
) {
    Scaffold(modifier, topBar = {
        TopAppBar(title = { Text(text = "My Budget") },
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
        LazyColumn(modifier = Modifier.padding(innerPadding)) {
            items(uiState.budgetList) { budgetAndCategory ->
                BudgetCategoryItem(
                    emoji = budgetAndCategory.category.emoji,
                    categoryName = budgetAndCategory.category.name,
                    amount = budgetAndCategory.budget.amount
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
    amount: Double = 0.0
) {
    Row(modifier, verticalAlignment = Alignment.CenterVertically) {
        Text(text = emoji, modifier = Modifier.padding(start = 12.dp))
        Spacer(modifier = Modifier.width(4.dp))
        Text(text = categoryName)
        Spacer(modifier = Modifier.weight(1f))
        Text(text = amount.formatToRupiah())
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

data class BudgetSettingUiState(
    val budgetList: List<BudgetAndCategory> = emptyList(),
)

data class BudgetSettingEvent(
    val onNavigateBackClicked: () -> Unit = {},
    val onAddClicked: () -> Unit = {}
)

@Preview
@Composable
fun BudgetSettingScreenPreview() {
    BajetTheme {
        BudgetSettingScreen(
            uiState = BudgetSettingUiState(
                budgetList = listOf(
                    BudgetAndCategory(
                        budget = Budget(
                            categoryId = 1,
                            amount = 100000.0
                        ),
                        category = DummyData.categories[0]
                    ),
                    BudgetAndCategory(
                        budget = Budget(
                            categoryId = 2,
                            amount = 100000.0
                        ),
                        category = DummyData.categories[1]
                    )
                )
            )
        )
    }
}