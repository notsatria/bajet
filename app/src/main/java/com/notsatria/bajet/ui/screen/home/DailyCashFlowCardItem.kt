package com.notsatria.bajet.ui.screen.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.notsatria.bajet.data.entities.CashFlow
import com.notsatria.bajet.ui.theme.AppTypography
import com.notsatria.bajet.ui.theme.errorLight
import com.notsatria.bajet.ui.theme.onSecondaryLight
import com.notsatria.bajet.ui.theme.outlineDark
import com.notsatria.bajet.ui.theme.outlineLight
import com.notsatria.bajet.ui.theme.tertiaryContainerDark
import com.notsatria.bajet.ui.theme.tertiaryContainerLightMediumContrast
import com.notsatria.bajet.utils.DataDummy
import com.notsatria.bajet.utils.DateUtils.formatDateTo
import com.notsatria.bajet.utils.formatToRupiah

/**
 * This function will shown on [HomeScreen] as a list. It will show the grouped cash flow by day.
 *
 * @param modifier
 * @param date
 * @param total as a total amount of grouped CashFlow
 * @param cashFlowList
 */
@Composable
fun DailyCashFlowCardItem(
    modifier: Modifier = Modifier,
    date: Long = 0L,
    total: Double = 0.0,
    cashFlowList: List<CashFlow>,
) {
    var itemRowVisible by remember { mutableStateOf(true) }

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = onSecondaryLight),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            DailyCashFlowHeader(
                total = total,
                date = date,
                onClick = { itemRowVisible = !itemRowVisible }
            )
            AnimatedVisibility(visible = itemRowVisible) {
                HorizontalDivider()
            }
            AnimatedVisibility(
                visible = itemRowVisible,
                enter = expandVertically(animationSpec = tween(300)) + fadeIn(
                    animationSpec = tween(
                        300
                    )
                ),
                exit = shrinkVertically(animationSpec = tween(300)) + fadeOut(
                    animationSpec = tween(
                        300
                    )
                )
            ) {
                Column {
                    cashFlowList.forEachIndexed { index, cashFlow ->
                        DailyCashFlowItemRow(cashFlow = cashFlow)
                        // Add divider only if it's not the last item
                        if (index != cashFlowList.lastIndex) {
                            HorizontalDivider()
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DailyCashFlowHeader(
    modifier: Modifier = Modifier,
    total: Double,
    date: Long,
    onClick: () -> Unit = {},
) {
    Column(modifier.clickable {
        onClick()
    }) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Text(
                text = date.formatDateTo(),
                style = AppTypography.labelLarge,
                color = outlineLight,
            )
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = total.formatToRupiah(),
                style = AppTypography.titleSmall,
                color = if (total < 0) errorLight else tertiaryContainerLightMediumContrast
            )
        }
    }
}

/**
 * Row item that shows category emoji and name. Also will show the cashflow amount and note
 *
 * @param modifier
 * @param cashFlow
 */
@Composable
fun DailyCashFlowItemRow(modifier: Modifier = Modifier, cashFlow: CashFlow) {
    Row(
        modifier = modifier
            .padding(12.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(tertiaryContainerDark)
        ) {
            Text(text = "\uD83C\uDF72", modifier = Modifier.align(Alignment.Center))
        }
        Spacer(modifier = Modifier.width(8.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = cashFlow.categoryId.toString(), style = AppTypography.titleMedium)
            Text(
                text = cashFlow.note,
                color = outlineDark,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        Text(
            text = cashFlow.amount.formatToRupiah(),
            style = AppTypography.titleSmall,
            color = errorLight
        )
    }
}

@Preview
@Composable
fun DailyCashFlowCardItemPreview() {
    DailyCashFlowCardItem(cashFlowList = DataDummy.cashFlowList)
}