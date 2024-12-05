package com.notsatria.bajet.ui.screen.home

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
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.notsatria.bajet.data.entities.CashFlow
import com.notsatria.bajet.data.entities.CashFlowAndCategory
import com.notsatria.bajet.data.entities.CashFlowSummary
import com.notsatria.bajet.ui.theme.backgroundLight
import com.notsatria.bajet.ui.theme.inversePrimaryLight
import com.notsatria.bajet.utils.DateUtils
import com.notsatria.bajet.utils.DateUtils.formatDateTo
import timber.log.Timber.Forest.i
import java.util.Calendar

@Composable
fun HomeRoute(
    modifier: Modifier = Modifier,
    navigateToAddCashFlowScreen: () -> Unit = {},
    viewModel: HomeViewModel = hiltViewModel()
) {
    val cashFlowAndCategoryList by viewModel.cashFlowAndCategoryList.collectAsStateWithLifecycle(
        emptyList()
    )
    val cashFlowSummary by viewModel.cashFlowSummary.collectAsStateWithLifecycle()
    val selectedMonth by viewModel.selectedMonth.collectAsStateWithLifecycle()

    HomeScreen(
        modifier,
        navigateToAddCashFlowScreen,
        cashFlowSummary,
        cashFlowAndCategoryList,
        selectedMonth,
        onPreviousMonthClick = {
            viewModel.changeMonth(-1)
        },
        onNextMonthClick = {
            viewModel.changeMonth(1)
        },
        onDeleteCashFlow = {
            viewModel.deleteCashFlow(it)
        }
    )
}

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    navigateToAddCashFlowScreen: () -> Unit = {},
    cashFlowSummary: CashFlowSummary? = null,
    cashFlowAndCategoryList: List<CashFlowAndCategory> = emptyList(),
    selectedMonth: Calendar,
    onPreviousMonthClick: () -> Unit = {},
    onNextMonthClick: () -> Unit = {},
    onDeleteCashFlow: (CashFlow) -> Unit = {}
) {
    Scaffold(
        modifier,
        containerColor = backgroundLight,
        floatingActionButton = {
            HomeFloatingActionButton(navigateToAddCashFlowScreen)
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .background(inversePrimaryLight)
            )
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 16.dp, start = 16.dp, end = 16.dp)
            ) {
                if (cashFlowSummary != null)
                    CashFlowSummaryCard(
                        cashFlowSummary = cashFlowSummary,
                        selectedMonth = selectedMonth,
                        onPreviousMonthClick = {
                            onPreviousMonthClick()
                        },
                        onNextMonthClick = {
                            onNextMonthClick()
                        }
                    )
                Spacer(modifier = Modifier.height(30.dp))
                LazyColumn {
                    val groupedCashflow =
                        cashFlowAndCategoryList.groupBy { it.cashFlow.date.formatDateTo(DateUtils.formatDate1) }

                    groupedCashflow.entries.forEachIndexed { _, entry ->
                        val date = entry.key
                        val cashFlowList = entry.value
                        val totalIncome =
                            cashFlowList.filter { it.category.categoryId == 1 }
                                .sumOf { it.cashFlow.amount }
                        val totalExpenses =
                            cashFlowList.filter { it.category.categoryId != 1 }
                                .sumOf { it.cashFlow.amount }

                        item {
                            DailyCashFlowCardItem(
                                date = date,
                                totalIncome = totalIncome,
                                totalExpenses = totalExpenses,
                                cashFlowList = cashFlowList,
                                modifier = Modifier.padding(bottom = 12.dp),
                                onDeleteCashFlow = { cashFlow ->
                                    onDeleteCashFlow(cashFlow)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun HomeFloatingActionButton(onClick: () -> Unit) {
    FloatingActionButton(onClick = { onClick() }) {
        Icon(imageVector = Icons.Filled.Add, contentDescription = "Add cashflow")
    }
}