package com.notsatria.bajet.ui.screen.budget

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.notsatria.bajet.R
import com.notsatria.bajet.data.entities.CashFlowSummary
import com.notsatria.bajet.ui.screen.home.CashFlowSummaryCard
import com.notsatria.bajet.ui.theme.BajetTheme
import java.util.Calendar

@Composable
fun BudgetRoute(modifier: Modifier = Modifier) {

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BudgetScreen() {
    Scaffold(
        topBar = {
            TopAppBar(title = { Text(stringResource(R.string.budget)) })
        }
    ) {
        Column(modifier = Modifier.padding(it)) {
            CashFlowSummaryCard(
                modifier =  Modifier.padding(16.dp),
                cashFlowSummary = CashFlowSummary(1000.0, 500.0, 500.0),
                onPreviousMonthClick = {},
                onNextMonthClick = {},
                selectedMonth = Calendar.getInstance()
            )
            Row(modifier = Modifier.fillMaxWidth()) {  }
        }
    }
}

@Preview
@Composable
fun BudgetScreenPreview() {
    BajetTheme {
        BudgetScreen()
    }
}