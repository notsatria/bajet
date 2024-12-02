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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import com.notsatria.bajet.data.entities.Category
import com.notsatria.bajet.ui.components.ClickableTextField
import com.notsatria.bajet.ui.theme.backgroundLight
import com.notsatria.bajet.ui.theme.surfaceContainerLight
import com.notsatria.bajet.utils.CashFlowTypes
import com.notsatria.bajet.utils.DateUtils
import com.notsatria.bajet.utils.DateUtils.formatDateTo
import java.util.Date

@Composable
fun AddCashFlowScreen(
    modifier: Modifier = Modifier,
    navigateBack: () -> Unit = {},
    viewModel: AddCashFlowViewModel = hiltViewModel<AddCashFlowViewModel>()
) {
    val shouldShowCategoryDialog = rememberSaveable { mutableStateOf(false) }
    val shouldShowDatePickerDialog = rememberSaveable { mutableStateOf(false) }


    if (shouldShowCategoryDialog.value) CategoriesDialog(shouldShowCategoryDialog = shouldShowCategoryDialog,
        categories = viewModel.addCashFlowData.categories,
        onDismiss = { shouldShowCategoryDialog.value = false },
        onCategorySelected = { category ->
            viewModel.updateCategory(category.id)
            viewModel.updateCategoryText("${category.emoji} ${category.name}")
        })

    if (shouldShowDatePickerDialog.value) CashFlowDatePickerDialog(shouldShowDialog = shouldShowDatePickerDialog,
        onDateSelected = { date ->
            if (date != null) viewModel.updateDate(date)
        },
        onDismiss = {
            shouldShowDatePickerDialog.value = false
        })

    val expensesCategory by remember {
        derivedStateOf { viewModel.addCashFlowData.selectedCashflowTypeIndex == 1 }
    }

    val fieldsEmpty by remember {
        derivedStateOf {
            viewModel.addCashFlowData.amount.isEmpty() ||
                    viewModel.addCashFlowData.amount == "0" ||
                    (expensesCategory && viewModel.addCashFlowData.categoryId == 0)
        }
    }

    val uiData = viewModel.addCashFlowData

    Scaffold(modifier, containerColor = backgroundLight, topBar = {
        AddCashFlowTopAppBar(navigateBack)
    }) { paddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Row {
                    CashFlowTypes.entries.forEachIndexed { index, value ->
                        CashFlowTypeRadioButton(
                            modifier = Modifier.weight(1f),
                            type = value.type,
                            onClick = {
                                viewModel.updateSelectedCashFlowType(index)
                            },
                            selected = (uiData.selectedCashflowTypeIndex == index)
                        )
                    }
                }
                if (expensesCategory) Spacer(modifier = Modifier.height(12.dp))
                AnimatedVisibility(visible = expensesCategory,
                    modifier = Modifier.clickable { shouldShowCategoryDialog.value = true }) {
                    ClickableTextField(
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = "Category",
                        value = uiData.categoryText,
                        onChange = { /* Do nothing */ },
                        readOnly = false,
                        onClick = {
                            shouldShowCategoryDialog.value = true
                        },
                    )
                }
                if (!expensesCategory) Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(modifier = Modifier.fillMaxWidth(),
                    value = uiData.formattedAmount,
                    onValueChange = { formattedAmount ->
                        // Remove non-numeric characters from the input
                        val rawAmount = formattedAmount.replace("\\D".toRegex(), "")
                        viewModel.updateAmount(rawAmount)
                    },
                    label = {
                        Text(text = "Amount")
                    },
                    maxLines = 1,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    prefix = { Text(text = "Rp") })
                Spacer(modifier = Modifier.height(12.dp))
                ClickableTextField(modifier = Modifier.fillMaxWidth(),
                    value = uiData.date.formatDateTo(format = DateUtils.formatDate2),
                    onChange = { /* nothing */ },
                    placeholder = "Date",
                    readOnly = false,
                    onClick = {
                        shouldShowDatePickerDialog.value = true
                    })
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = uiData.note,
                    onValueChange = { note -> viewModel.updateNote(note) },
                    label = {
                        Text(text = "Note")
                    },
                    minLines = 1,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                )
                Spacer(modifier = Modifier.height(20.dp))
                Button(modifier = Modifier.fillMaxWidth(), enabled = !fieldsEmpty, onClick = {
                    viewModel.insertCashFlow()
                    navigateBack()
                }) {
                    Text(text = "Save")
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CashFlowDatePickerDialog(
    shouldShowDialog: MutableState<Boolean>, onDateSelected: (Long?) -> Unit, onDismiss: () -> Unit
) {
    val datePickerState = rememberDatePickerState(initialDisplayedMonthMillis = Date().time)

    if (shouldShowDialog.value) DatePickerDialog(onDismissRequest = { onDismiss() },
        confirmButton = {
            TextButton(onClick = {
                onDateSelected(datePickerState.selectedDateMillis)
                onDismiss()
            }) {
                Text(text = "OK")
            }
        },
        dismissButton = {
            TextButton(onClick = { onDismiss() }) {
                Text(text = "Cancel")
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
        Scaffold(modifier = Modifier.fillMaxSize(), containerColor = backgroundLight, topBar = {
            TopAppBar(title = {
                Text(text = "Select Category")
            }, navigationIcon = {
                IconButton(onClick = { onDismiss() }) {
                    Icon(
                        Icons.AutoMirrored.Default.ArrowBack, contentDescription = "Dismiss"
                    )
                }
            })
        }) { innerPadding ->
            LazyVerticalGrid(
                columns = GridCells.Fixed(count = 4), modifier = Modifier.padding(innerPadding)
            ) {
                items(categories.filter { it.id != 1 && it.id != 2 }) { category ->
                    CategoryDialogItem(item = category.name,
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
    TopAppBar(title = { Text(text = "Add Cashflow") }, navigationIcon = {
        IconButton(onClick = { onNavigateBack() }) {
            Icon(
                imageVector = Icons.AutoMirrored.Default.ArrowBack, contentDescription = "Back"
            )
        }
    }, colors = TopAppBarDefaults.topAppBarColors(containerColor = surfaceContainerLight)
    )
}

@Preview
@Composable
fun AddCashFlowScreenPreview() {
    AddCashFlowScreen()
}
