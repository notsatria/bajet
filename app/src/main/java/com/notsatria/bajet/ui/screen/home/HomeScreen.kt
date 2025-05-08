package com.notsatria.bajet.ui.screen.home

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.notsatria.bajet.R
import com.notsatria.bajet.data.entities.CashFlow
import com.notsatria.bajet.data.entities.relation.CashFlowAndCategory
import com.notsatria.bajet.data.entities.relation.CashFlowSummary
import com.notsatria.bajet.ui.components.EmptyView
import com.notsatria.bajet.ui.theme.BajetTheme
import com.notsatria.bajet.utils.DateUtils
import com.notsatria.bajet.utils.DateUtils.formatDateTo
import com.notsatria.bajet.utils.DummyData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.util.Calendar

@Composable
fun HomeRoute(
    modifier: Modifier = Modifier,
    navigateToAddCashFlowScreen: () -> Unit = {},
    navigateToEditCashFlowScreen: (Int) -> Unit = {},
    viewModel: HomeViewModel = hiltViewModel(),
    scope: CoroutineScope = rememberCoroutineScope(),
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
    context: Context = LocalContext.current
) {
    val cashFlowAndCategoryList by viewModel.groupedCashflowAndCategory.collectAsState()
    val cashFlowSummary by viewModel.cashFlowSummary.collectAsStateWithLifecycle()
    val selectedMonth by viewModel.selectedMonth.collectAsStateWithLifecycle()

    HomeScreen(
        modifier,
        HomeUiState(
            cashFlowSummary = cashFlowSummary,
            groupedCashflowAndCategory = cashFlowAndCategoryList,
            selectedMonth = selectedMonth
        ),
        navigateToAddCashFlowScreen,
        onPreviousMonthClick = {
            viewModel.changeMonth(-1)
        },
        onNextMonthClick = {
            viewModel.changeMonth(1)
        },
        onDeleteCashFlow = {
            viewModel.deleteCashFlow(it)
            scope.launch {
                val result = snackbarHostState.showSnackbar(
                    message = context.getString(R.string.cashflow_deleted),
                    actionLabel = context.getString(R.string.undo),
                    duration = SnackbarDuration.Long
                )
                if (result == SnackbarResult.ActionPerformed) {
                    viewModel.insertCashFlow()
                }
            }
        },
        navigateToEditCashFlowScreen = {
            navigateToEditCashFlowScreen(it)
        },
        snackbarHostState = snackbarHostState
    )
}

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    homeUiState: HomeUiState,
    navigateToAddCashFlowScreen: () -> Unit = {},
    onPreviousMonthClick: () -> Unit = {},
    onNextMonthClick: () -> Unit = {},
    onDeleteCashFlow: (CashFlow) -> Unit = {},
    navigateToEditCashFlowScreen: (Int) -> Unit = {},
    snackbarHostState: SnackbarHostState,
) {
    Scaffold(
        modifier,
        containerColor = MaterialTheme.colorScheme.background,
        snackbarHost = {
            SnackbarHost(snackbarHostState)
        },
        floatingActionButton = {
            HomeFloatingActionButton(navigateToAddCashFlowScreen)
        },
    ) { _ ->
        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .background(MaterialTheme.colorScheme.surfaceContainer)
            )
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 16.dp, start = 16.dp, end = 16.dp)
            ) {
                if (homeUiState.cashFlowSummary != null)
                    CashFlowSummaryCard(
                        cashFlowSummary = homeUiState.cashFlowSummary,
                        selectedMonth = homeUiState.selectedMonth,
                        onPreviousMonthClick = {
                            onPreviousMonthClick()
                        },
                        onNextMonthClick = {
                            onNextMonthClick()
                        }
                    )
                if (homeUiState.groupedCashflowAndCategory.isEmpty()) {
                    EmptyView(
                        Modifier.fillMaxSize(), drawable = R.drawable.ic_no_budget_found_24,
                        stringResource(R.string.no_cashflow_found)
                    )
                }
                GroupedCashFlowList(
                    modifier = Modifier.padding(top = 16.dp),
                    groupedCashflow = homeUiState.groupedCashflowAndCategory,
                    onDeleteCashFlow = onDeleteCashFlow,
                    navigateToEditCashFlowScreen = navigateToEditCashFlowScreen
                )
            }
        }
    }
}

@Composable
fun GroupedCashFlowList(
    modifier: Modifier = Modifier,
    groupedCashflow: Map<String, List<CashFlowAndCategory>>,
    onDeleteCashFlow: (CashFlow) -> Unit,
    navigateToEditCashFlowScreen: (Int) -> Unit
) {
    LazyColumn(modifier) {
        groupedCashflow.entries.map { entry ->
            item {
                DailyCashFlowCardItem(
                    date = entry.key,
                    totalIncome = entry.value.filter { it.category.id == 1 }
                        .sumOf { it.cashFlow.amount },
                    totalExpenses = entry.value.filter { it.category.id != 1 }
                        .sumOf { it.cashFlow.amount },
                    cashFlowList = entry.value,
                    onDeleteCashFlow = onDeleteCashFlow,
                    navigateToEditCashFlowScreen = navigateToEditCashFlowScreen
                )
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }

}

@Composable
fun HomeFloatingActionButton(onClick: () -> Unit) {
    FloatingActionButton(onClick = onClick) {
        Icon(imageVector = Icons.Filled.Add, contentDescription = "Add cashflow")
    }
}

data class HomeUiState(
    val cashFlowSummary: CashFlowSummary? = null,
    val groupedCashflowAndCategory: Map<String, List<CashFlowAndCategory>> = emptyMap(),
    val selectedMonth: Calendar = Calendar.getInstance()
)

@Preview
@Composable
fun HomeScreenPreview() {
    BajetTheme {
        HomeScreen(
            homeUiState = HomeUiState(
                cashFlowSummary = CashFlowSummary(
                    income = 20000.0,
                    expenses = -40000.0,
                    balance = -20000.0
                ),
                groupedCashflowAndCategory = DummyData.cashflowWithCategories.sortedByDescending { it.cashFlow.date }
                    .groupBy { it.cashFlow.date.formatDateTo(DateUtils.formatDate1) },
                selectedMonth = Calendar.getInstance()
            ),
            snackbarHostState = SnackbarHostState()
        )
    }
}

@Preview
@Composable
fun HomeScreenEmptyListPreview() {
    BajetTheme {
        HomeScreen(
            homeUiState = HomeUiState(
                cashFlowSummary = CashFlowSummary(
                    income = 20000.0,
                    expenses = -40000.0,
                    balance = -20000.0
                ),
                groupedCashflowAndCategory = mapOf(
                ),
                selectedMonth = Calendar.getInstance()
            ),
            snackbarHostState = SnackbarHostState()
        )
    }
}