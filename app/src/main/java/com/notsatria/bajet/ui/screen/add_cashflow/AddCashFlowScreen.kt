package com.notsatria.bajet.ui.screen.add_cashflow

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import com.notsatria.bajet.R
import com.notsatria.bajet.data.entities.Category
import com.notsatria.bajet.ui.components.ClickableTextField
import com.notsatria.bajet.ui.theme.backgroundLight
import com.notsatria.bajet.utils.CashFlowTypes
import com.notsatria.bajet.utils.DateUtils
import com.notsatria.bajet.utils.DateUtils.formatDateTo
import java.util.Date

@Composable
fun AddCashFlowRoute(
    modifier: Modifier = Modifier,
    navigateBack: () -> Unit,
    cashFlowId: Int? = null,
    viewModel: AddCashFlowViewModel = hiltViewModel()
) {
    val shouldShowCategoryDialog = rememberSaveable { mutableStateOf(false) }
    val shouldShowDatePickerDialog = rememberSaveable { mutableStateOf(false) }
    val categories by remember {
        derivedStateOf { viewModel.addCashFlowData.categories }
    }
    val expensesCategory by remember {
        derivedStateOf { viewModel.addCashFlowData.selectedCashflowTypeIndex == 1 }
    }
    val fieldsEmpty by remember {
        derivedStateOf {
            viewModel.validateFields(expensesCategory)
        }
    }
    val uiData = viewModel.addCashFlowData
    val cashFlowIdExists = (cashFlowId != null && cashFlowId != -1)

    LaunchedEffect(key1 = cashFlowId) {
        if (cashFlowIdExists) viewModel.getCashFlowById(cashFlowId!!)
    }

    AddCashFlowScreen(
        modifier,
        uiState = AddCashFlowUiState(
            shouldShowCategoryDialog = shouldShowCategoryDialog,
            shouldShowDatePickerDialog = shouldShowDatePickerDialog,
            categories = categories,
            uiData = uiData,
            expensesCategory = expensesCategory,
            cashFlowIdExists = cashFlowIdExists,
            fieldsEmpty = fieldsEmpty
        ),
        navigateBack = navigateBack,
        onCategorySelected = { category ->
            viewModel.updateCategory(category.categoryId)
            viewModel.updateCategoryText("${category.emoji} ${category.name}")
        },
        onUpdateDate = { date ->
            viewModel.updateDate(date)
        },
        onUpdateSelectedCashFlowType = { index ->
            viewModel.updateSelectedCashFlowType(index)
        },
        onUpdateAmount = { rawAmount ->
            viewModel.updateAmount(rawAmount)
        },
        onUpdateNote = { note ->
            viewModel.updateNote(note)
        },
        onAddCashFlowClicked = {
            if (cashFlowIdExists) viewModel.updateCashFlow(cashFlowId!!)
            else viewModel.insertCashFlow()
            navigateBack()
        },
    )
}

@Composable
fun AddCashFlowScreen(
    modifier: Modifier = Modifier,
    uiState: AddCashFlowUiState = AddCashFlowUiState(),
    navigateBack: () -> Unit = {},
    onCategorySelected: (Category) -> Unit = {},
    onUpdateDate: (Long) -> Unit = {},
    onUpdateSelectedCashFlowType: (Int) -> Unit = {},
    onUpdateAmount: (String) -> Unit = {},
    onUpdateNote: (String) -> Unit = {},
    onAddCashFlowClicked: () -> Unit = {},
) {
    if (uiState.shouldShowCategoryDialog.value)
        CategoriesDialog(shouldShowCategoryDialog = uiState.shouldShowCategoryDialog,
            categories = uiState.categories,
            onDismiss = { uiState.shouldShowCategoryDialog.value = false },
            onCategorySelected = { category ->
                onCategorySelected(category)
                uiState.shouldShowCategoryDialog.value = false
            })

    if (uiState.shouldShowDatePickerDialog.value) CashFlowDatePickerDialog(
        shouldShowDialog = uiState.shouldShowDatePickerDialog,
        onDateSelected = { date ->
            if (date != null) onUpdateDate(date)
            uiState.shouldShowDatePickerDialog.value = false
        },
        onDismiss = {
            uiState.shouldShowDatePickerDialog.value = false
        },
        initialDate = if (uiState.cashFlowIdExists) uiState.uiData.date else Date().time,
    )

    val onEditAndIncomeAndExpensesCategory =
        uiState.cashFlowIdExists && uiState.uiData.categoryId == 0 || uiState.uiData.categoryId == 1

    Scaffold(modifier, containerColor = MaterialTheme.colorScheme.background, topBar = {
        AddCashFlowTopAppBar(navigateBack)
    }) { padding ->
        Box(Modifier.padding(padding)) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
                    .imePadding()
            ) {
                Row {
                    CashFlowTypes.entries.forEachIndexed { index, value ->
                        CashFlowTypeRadioButton(
                            modifier = Modifier.weight(1f), type = value.type, onClick = {
                                onUpdateSelectedCashFlowType(index)
                            }, selected = (uiState.uiData.selectedCashflowTypeIndex == index)
                        )
                    }
                }
                if (uiState.expensesCategory) Spacer(modifier = Modifier.height(12.dp))
                AnimatedVisibility(visible = uiState.expensesCategory,
                    modifier = Modifier.clickable {
                        uiState.shouldShowCategoryDialog.value = true
                    }) {
                    // check if its edit cashflow and category is not income and expenses
                    ClickableTextField(
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = stringResource(R.string.category),
                        value = if (onEditAndIncomeAndExpensesCategory) "" else uiState.uiData.categoryText,
                        onChange = { /* Do nothing */ },
                        readOnly = false,
                        onClick = {
                            uiState.shouldShowCategoryDialog.value = true
                        },
                    )
                }
                if (!uiState.expensesCategory) Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = uiState.uiData.formattedAmount,
                    onValueChange = { formattedAmount ->
                        // Remove non-numeric characters from the input
                        val rawAmount = formattedAmount.replace("\\D".toRegex(), "")
                        onUpdateAmount(rawAmount)
                    },
                    label = {
                        Text(text = stringResource(R.string.amount))
                    },
                    maxLines = 1,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    prefix = { Text(text = stringResource(R.string.rp)) },
                )
                Spacer(modifier = Modifier.height(12.dp))
                ClickableTextField(modifier = Modifier.fillMaxWidth(),
                    value = uiState.uiData.date.formatDateTo(format = DateUtils.formatDate4),
                    onChange = { /* nothing */ },
                    placeholder = stringResource(R.string.date),
                    readOnly = false,
                    onClick = {
                        uiState.shouldShowDatePickerDialog.value = true
                    })
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = uiState.uiData.note,
                    onValueChange = { note -> onUpdateNote(note) },
                    label = {
                        Text(text = stringResource(R.string.note))
                    },
                    minLines = 1,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                )
                Spacer(modifier = Modifier.height(20.dp))
                Button(
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !uiState.fieldsEmpty,
                    onClick = {
                        if (uiState.fieldsEmpty) return@Button
                        onAddCashFlowClicked()
                    }) {
                    Text(
                        text = if (uiState.cashFlowIdExists) stringResource(R.string.update) else stringResource(
                            R.string.save
                        )
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CashFlowDatePickerDialog(
    shouldShowDialog: MutableState<Boolean>,
    onDateSelected: (Long?) -> Unit,
    onDismiss: () -> Unit,
    initialDate: Long = 0
) {
    val datePickerState =
        rememberDatePickerState(initialDisplayedMonthMillis = initialDate)

    if (shouldShowDialog.value) DatePickerDialog(onDismissRequest = { onDismiss() },
        confirmButton = {
            TextButton(onClick = {
                onDateSelected(datePickerState.selectedDateMillis)
                onDismiss()
            }) {
                Text(text = stringResource(R.string.ok))
            }
        },
        dismissButton = {
            TextButton(onClick = { onDismiss() }) {
                Text(text = stringResource(R.string.cancel))
            }
        }) {
        DatePicker(state = datePickerState)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoriesDialog(
    shouldShowCategoryDialog: MutableState<Boolean>,
    categories: List<Category>,
    onDismiss: () -> Unit,
    onCategorySelected: (Category) -> Unit
) {
    if (shouldShowCategoryDialog.value) Dialog(onDismissRequest = {
        shouldShowCategoryDialog.value = false
    }, properties = DialogProperties(usePlatformDefaultWidth = false)) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = MaterialTheme.colorScheme.background,
            topBar = {
                TopAppBar(title = {
                    Text(text = stringResource(R.string.select_category))
                }, navigationIcon = {
                    IconButton(onClick = { onDismiss() }) {
                        Icon(
                            Icons.AutoMirrored.Default.ArrowBack,
                            contentDescription = stringResource(R.string.dismiss)
                        )
                    }
                })
            }) { innerPadding ->
            LazyVerticalGrid(
                columns = GridCells.Fixed(count = 4), modifier = Modifier.padding(innerPadding)
            ) {
                items(categories.filter { it.categoryId != 1 && it.categoryId != 2 }) { category ->
                    CategoryDialogItem(
                        item = category.name,
                        emoji = category.emoji,
                        onCategorySelected = {
                            onCategorySelected(category)
                            onDismiss()
                        })
                }
            }
        }
    }
}

@Composable
fun CategoryDialogItem(item: String, emoji: String, onCategorySelected: () -> Unit) {
    Column(
        modifier = Modifier
            .padding(bottom = 12.dp)
            .fillMaxSize()
            .clickable { onCategorySelected() },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(text = emoji, fontSize = 60.sp)
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = item)
    }
}

@Composable
fun CashFlowTypeRadioButton(
    modifier: Modifier = Modifier, type: String, onClick: () -> Unit = {}, selected: Boolean = false
) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = modifier) {
        RadioButton(selected = selected, onClick = { onClick() })
        Text(text = type.replaceFirstChar { it.uppercase() })
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddCashFlowTopAppBar(onNavigateBack: () -> Unit) {
    TopAppBar(
        title = { Text(text = stringResource(R.string.add_cashflow)) },
        navigationIcon = {
            IconButton(onClick = { onNavigateBack() }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Default.ArrowBack,
                    contentDescription = stringResource(
                        R.string.back
                    )
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh)
    )
}

data class AddCashFlowUiState(
    val shouldShowCategoryDialog: MutableState<Boolean> = mutableStateOf(false),
    val shouldShowDatePickerDialog: MutableState<Boolean> = mutableStateOf(false),
    val categories: List<Category> = emptyList(),
    val uiData: AddCashFlowData = AddCashFlowData(),
    val expensesCategory: Boolean = false,
    val cashFlowIdExists: Boolean = false,
    val fieldsEmpty: Boolean = false
)

@Preview
@Composable
fun AddCashFlowScreenPreview() {
    AddCashFlowScreen()
}
