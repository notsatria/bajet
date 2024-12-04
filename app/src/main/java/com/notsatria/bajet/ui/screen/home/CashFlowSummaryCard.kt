package com.notsatria.bajet.ui.screen.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.notsatria.bajet.data.entities.CashFlowSummary
import com.notsatria.bajet.ui.theme.AppTypography
import com.notsatria.bajet.ui.theme.outlineLight
import com.notsatria.bajet.ui.theme.surfaceContainerLight
import com.notsatria.bajet.utils.DateUtils
import com.notsatria.bajet.utils.DateUtils.formatDateTo
import com.notsatria.bajet.utils.formatToRupiah
import java.util.Calendar


/**
 * To show cashflow summary by this month: Expenses, Income, and Balance
 */
@Composable
fun CashFlowSummaryCard(
    modifier: Modifier = Modifier,
    cashFlowSummary: CashFlowSummary,
    selectedMonth: Calendar,
    onPreviousMonthClick: () -> Unit = {},
    onNextMonthClick: () -> Unit = {}
) {
    ElevatedCard(
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 6.dp),
        colors = CardDefaults.cardColors(containerColor = surfaceContainerLight),
        modifier = modifier
            .fillMaxWidth()
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(12.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { onPreviousMonthClick() }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Default.KeyboardArrowLeft,
                        contentDescription = null
                    )
                }
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = selectedMonth.formatDateTo(DateUtils.formatDate3),
                    style = AppTypography.bodyLarge,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.width(4.dp))
                IconButton(onClick = { onNextMonthClick() }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Default.KeyboardArrowRight,
                        contentDescription = null,
                    )
                }
            }
            HorizontalDivider()
            Spacer(modifier = Modifier.height(20.dp))
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                CashflowColumnText(
                    title = "Expenses",
                    value = cashFlowSummary.expenses.formatToRupiah(),
                    modifier = Modifier.weight(1f)
                )
                CashflowColumnText(
                    title = "Income",
                    value = cashFlowSummary.income.formatToRupiah(),
                    modifier = Modifier.weight(1f)
                )
                CashflowColumnText(
                    title = "Balance",
                    value = cashFlowSummary.balance.formatToRupiah(),
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

/**
 * Text shown on [CashFlowSummaryCard] (Expenses, Income, and Balance)
 */
@Composable
fun CashflowColumnText(modifier: Modifier = Modifier, title: String, value: String) {
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            title,
            textAlign = TextAlign.Center,
            style = AppTypography.bodyMedium,
            fontWeight = FontWeight.Bold,
            color = outlineLight
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            value,
            textAlign = TextAlign.Center,
            style = AppTypography.bodyMedium,
            fontWeight = FontWeight.SemiBold
        )
    }
}