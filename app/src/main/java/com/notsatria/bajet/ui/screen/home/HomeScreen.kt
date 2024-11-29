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
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.notsatria.bajet.ui.theme.backgroundLight
import com.notsatria.bajet.ui.theme.inversePrimaryLight
import com.notsatria.bajet.utils.DataDummy

@Composable
fun HomeScreen(modifier: Modifier = Modifier) {
    Scaffold(
        modifier,
        containerColor = backgroundLight,
        floatingActionButton = {
            HomeFloatingActionButton()
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
                    .padding(16.dp)
            ) {
                CashFlowSummaryCard()
                Spacer(modifier = Modifier.height(30.dp))
                LazyColumn {
                    val groupedCashflow = DataDummy.cashFlowList.groupBy { it.date }

                    groupedCashflow.entries.forEachIndexed { _, entry ->
                        val date = entry.key
                        val cashFlowList = entry.value
                        val total =
                            cashFlowList.sumOf { it.amount * if (it.type == "income") 1 else -1 }

                        item {
                            DailyCashFlowCardItem(
                                date = date,
                                total = total,
                                cashFlowList = cashFlowList,
                                modifier = Modifier.padding(bottom = 12.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun HomeFloatingActionButton() {
    FloatingActionButton(onClick = { }) {
        Icon(imageVector = Icons.Filled.Add, contentDescription = "Add cashflow")
    }
}

@Preview
@Composable
fun HomeScreenPreview() {
    HomeScreen()
}