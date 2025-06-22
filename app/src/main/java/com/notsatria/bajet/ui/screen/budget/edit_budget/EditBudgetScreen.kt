package com.notsatria.bajet.ui.screen.budget.edit_budget

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.notsatria.bajet.R
import com.notsatria.bajet.ui.components.BajetTopBar
import com.notsatria.bajet.ui.components.CurrencyTextField
import com.notsatria.bajet.ui.theme.BajetTheme
import com.notsatria.bajet.utils.formatToRupiah
import com.notsatria.bajet.utils.toMonthName

@Composable
fun EditBudgetRoute(
    modifier: Modifier = Modifier,
    navigateBack: () -> Unit,
    viewModel: EditBudgetViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    if (uiState.showEditAmountDialog) {
        EditAmountDialog(
            onDismissRequest = {
                viewModel.setAction(EditBudgetAction.DismissDialog)
            },
            amount = uiState.budgetAmount,
            onAmountChange = {
                viewModel.setAction(EditBudgetAction.UpdateAmount(it))
            },
            onSaveClick = { amount ->
                viewModel.setAction(EditBudgetAction.SaveClick(amount))
            }
        )
    }

    EditBudgetScreen(
        modifier = modifier,
        navigateBack = navigateBack,
        state = uiState,
        setAction = {
            viewModel.setAction(it)
        },
    )
}

@Composable
fun EditBudgetScreen(
    modifier: Modifier = Modifier,
    state: EditBudgetUiState = EditBudgetUiState(),
    setAction: (EditBudgetAction) -> Unit = {},
    navigateBack: () -> Unit = {},
) {
    Scaffold(modifier, topBar = {
        BajetTopBar(
            title = "Budget ${state.categoryName} ${state.monthAndYear.year}",
            canNavigateBack = true,
            navigateBack = navigateBack
        )
    }) { innerPadding ->
        LazyColumn(modifier = Modifier.padding(innerPadding)) {
            items(state.budgetEntries) { budgetEntry ->
                BudgetAndMonthRow(
                    Modifier.fillMaxWidth(),
                    monthName = budgetEntry.month.toMonthName(),
                    amount = budgetEntry.amount,
                    isCurrentMonth = budgetEntry.month == state.monthAndYear.month,
                    onEditClicked = {
                        setAction(
                            EditBudgetAction.EditClick(
                                amount = budgetEntry.amount.toString(),
                                budgetMonthId = budgetEntry.budgetMonthId
                            )
                        )
                    })
                Spacer(Modifier.height(8.dp))
            }
        }
    }
}

@Composable
fun BudgetAndMonthRow(
    modifier: Modifier = Modifier,
    monthName: String,
    amount: Double,
    isCurrentMonth: Boolean,
    onEditClicked: () -> Unit = {}
) {
    Row(
        modifier = modifier.background(if (isCurrentMonth) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.background),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedCard(
            modifier = Modifier
                .width(60.dp)
                .padding(start = 12.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary)
        ) {
            Text(
                monthName,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                textAlign = TextAlign.Center
            )
        }
        Spacer(Modifier.width(8.dp))
        Text(amount.formatToRupiah())
        Spacer(Modifier.weight(1f))
        IconButton(onClick = {
            onEditClicked()
        }) {
            Icon(
                imageVector = Icons.Outlined.Edit,
                contentDescription = stringResource(R.string.edit_budget),
                tint = MaterialTheme.colorScheme.outline
            )
        }
    }
}

@Composable
fun EditAmountDialog(
    onDismissRequest: () -> Unit = {},
    amount: String,
    onAmountChange: (String) -> Unit = {},
    onSaveClick: (amount: String) -> Unit = {}
) {
    Dialog(onDismissRequest = onDismissRequest) {
        Card(Modifier.height(200.dp), shape = RoundedCornerShape(16.dp)) {
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(16.dp)
            ) {
                Text(
                    stringResource(R.string.edit_amount),
                    style = MaterialTheme.typography.headlineMedium
                )
                Spacer(Modifier.weight(1f))
                CurrencyTextField(
                    modifier = Modifier.fillMaxWidth(),
                    amount = amount,
                    onAmountChange = onAmountChange
                )
                Spacer(modifier = Modifier.weight(1f))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismissRequest) {
                        Text(stringResource(R.string.cancel))
                    }
                    TextButton(onClick = {
                        onSaveClick(amount)
                    }) {
                        Text(
                            stringResource(R.string.edit)
                        )
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun EditAmountDialogPreview() {
    BajetTheme {
        EditAmountDialog(amount = "999000")
    }
}

@Preview
@Composable
fun EditBudgetScreenPreview() {
    BajetTheme {
        EditBudgetScreen()
    }
}