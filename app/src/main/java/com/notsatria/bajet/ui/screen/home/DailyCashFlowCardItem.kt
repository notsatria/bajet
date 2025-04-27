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
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.notsatria.bajet.data.entities.CashFlow
import com.notsatria.bajet.data.entities.relation.CashFlowAndCategory
import com.notsatria.bajet.data.entities.Category
import com.notsatria.bajet.ui.domain.CashFlowAndCategoryDomain
import com.notsatria.bajet.ui.components.ActionIcon
import com.notsatria.bajet.ui.components.SwipeableItemWithActions
import com.notsatria.bajet.ui.theme.BajetTheme
import com.notsatria.bajet.ui.theme.errorLight
import com.notsatria.bajet.ui.theme.tertiaryContainerLightMediumContrast
import com.notsatria.bajet.utils.DateUtils.formatDateTo
import com.notsatria.bajet.utils.DummyData
import com.notsatria.bajet.utils.Helper
import com.notsatria.bajet.utils.formatToRupiah
import java.util.Calendar

/**
 * This function will shown on [HomeScreen] as a list. It will show the grouped cash flow by day.
 *
 * @param modifier
 * @param date
 * @param cashFlowList
 */
@Composable
fun DailyCashFlowCardItem(
    modifier: Modifier = Modifier,
    date: String,
    totalExpenses: Double = 0.0,
    totalIncome: Double = 0.0,
    cashFlowList: List<CashFlowAndCategory>,
    onDeleteCashFlow: (CashFlow) -> Unit,
    navigateToEditCashFlowScreen: (Int) -> Unit
) {
    var itemRowVisible by remember { mutableStateOf(true) }

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            DailyCashFlowHeader(
                totalExpenses = totalExpenses,
                totalIncome = totalIncome,
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
                    cashFlowList.forEachIndexed { index, cashFlowAndCategory ->
                        var cashFlowAndCategoryDomain = cashFlowAndCategory.toDomain()
                        SwipeableItemWithActions(
                            isRevealed = cashFlowAndCategoryDomain.isOptionsRevealed,
                            actions = {
                                ActionIcon(
                                    icon = Icons.Default.Delete,
                                    tint = MaterialTheme.colorScheme.error,
                                    modifier = Modifier.fillMaxHeight(),
                                    backgroundColor = Color.White,
                                    onClick = {
                                        onDeleteCashFlow(cashFlowAndCategoryDomain.cashFlow)
                                    }
                                )
                            },
                            onExpanded = {
                                cashFlowAndCategoryDomain = cashFlowAndCategoryDomain.copy(
                                    isOptionsRevealed = true
                                )
                            },
                            onCollapsed = {
                                cashFlowAndCategoryDomain = cashFlowAndCategoryDomain.copy(
                                    isOptionsRevealed = false
                                )
                            }) {
                            DailyCashFlowItemRow(
                                cashFlow = cashFlowAndCategoryDomain,
                                modifier = Modifier
                                    .background(
                                        MaterialTheme.colorScheme.surfaceContainer
                                    )
                                    .clickable {
                                        navigateToEditCashFlowScreen(cashFlowAndCategoryDomain.cashFlow.cashFlowId)
                                    },
                                emoji = cashFlowAndCategoryDomain.category.emoji,
                                categoryColor = cashFlowAndCategoryDomain.category.color
                            )
                        }
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
    totalExpenses: Double,
    totalIncome: Double,
    date: String,
    onClick: () -> Unit = {},
) {
    Column(modifier
        .clickable {
            onClick()
        }
        .background(color = MaterialTheme.colorScheme.surfaceContainerHigh)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Text(
                text = date,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = totalIncome.formatToRupiah(),
                style = MaterialTheme.typography.titleSmall,
                color = tertiaryContainerLightMediumContrast
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = totalExpenses.formatToRupiah(),
                style = MaterialTheme.typography.titleSmall,
                color = errorLight
            )
        }
    }
}

/**
 * Row item that shows category emoji and name. Also will show the cashflow amount and note
 *
 * @param modifier
 * @param cashFlow
 * @param emoji
 */
@Composable
fun DailyCashFlowItemRow(
    modifier: Modifier = Modifier,
    cashFlow: CashFlowAndCategoryDomain,
    emoji: String,
    categoryColor: Int
) {
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
                .background(Color(categoryColor))
        ) {
            Text(text = emoji, modifier = Modifier.align(Alignment.Center))
        }
        Spacer(modifier = Modifier.width(8.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = cashFlow.category.name,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            if (cashFlow.cashFlow.note.isNotEmpty()) Text(
                text = cashFlow.cashFlow.note,
                color = MaterialTheme.colorScheme.outlineVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                fontSize = 12.sp
            )
        }
        Text(
            text = cashFlow.cashFlow.amount.formatToRupiah(),
            style = MaterialTheme.typography.titleSmall,
            color = if (cashFlow.cashFlow.categoryId != 1) errorLight else tertiaryContainerLightMediumContrast
        )
    }
}


@Preview
@Composable
fun DailyCashFlowCardItemPreview() {
    BajetTheme {
        DailyCashFlowCardItem(
            date = Calendar.getInstance().formatDateTo(),
            totalExpenses = 20000.0,
            totalIncome = 30000.0,
            cashFlowList = listOf(
                CashFlowAndCategory(
                    cashFlow = CashFlow(
                        cashFlowId = 1,
                        type = "Income",
                        amount = 10000.0,
                        note = "Salary",
                        date = Calendar.getInstance().timeInMillis,
                        categoryId = 1
                    ),
                    category = DummyData.categories[0]
                ),
                CashFlowAndCategory(
                    cashFlow = CashFlow(
                        cashFlowId = 2,
                        type = "Expenses",
                        amount = -10000.0,
                        note = "Food",
                        date = Calendar.getInstance().timeInMillis,
                        categoryId = 2
                    ),
                    category = DummyData.categories[2]
                )
            ),
            onDeleteCashFlow = {},
            navigateToEditCashFlowScreen = {}
        )
    }
}

@Preview
@Composable
fun DailyCashFlowItemRowPreview() {
    BajetTheme {
        DailyCashFlowItemRow(
            modifier = Modifier,
            cashFlow = CashFlowAndCategoryDomain(
                cashFlow = CashFlow(
                    cashFlowId = 1,
                    type = "Income",
                    amount = 10000.0,
                    note = "Salary",
                    date = Calendar.getInstance().timeInMillis,
                    categoryId = 1
                ),
                category = DummyData.categories[0],
                isOptionsRevealed = false
            ),
            emoji = "💰",
            categoryColor = Helper.randomColor(alpha = 130).toArgb()
        )
    }
}