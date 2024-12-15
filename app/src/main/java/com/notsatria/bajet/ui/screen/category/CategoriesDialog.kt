package com.notsatria.bajet.ui.screen.category

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.EmojiSupportMatch
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import com.notsatria.bajet.R
import com.notsatria.bajet.data.entities.Category
import com.notsatria.bajet.ui.theme.BajetTheme

@Composable
fun CategoryManagementScreen(
    categories: List<Category>,
    onCategorySelected: (Category) -> Unit,
    shouldShowCategoryDialog: MutableState<Boolean>,
    viewModel: CategoriesViewModel = hiltViewModel()
) {
    val shouldShowAddCategoryDialog = remember { mutableStateOf(false) }
    val onEditCategoryMode = remember { mutableStateOf(false) }

    CategoriesDialog(
        shouldShowCategoryDialog = shouldShowCategoryDialog,
        onDismiss = { shouldShowCategoryDialog.value = false },
        categories = categories,
        shouldShowAddCategoryDialog = shouldShowAddCategoryDialog,
        onCategorySelected = onCategorySelected,
        viewModel = viewModel,
        onEditCategoryMode = onEditCategoryMode
    )

    AddCategoryDialog(
        shouldAddShowCategoryDialog = shouldShowAddCategoryDialog,
        onCancel = {
            shouldShowAddCategoryDialog.value = false
            onEditCategoryMode.value = false
        },
        viewModel = viewModel,
        onEditCategoryMode = onEditCategoryMode
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoriesDialog(
    shouldShowCategoryDialog: MutableState<Boolean>,
    onDismiss: () -> Unit,
    onCategorySelected: (Category) -> Unit,
    categories: List<Category> = emptyList(),
    shouldShowAddCategoryDialog: MutableState<Boolean> = mutableStateOf(false),
    viewModel: CategoriesViewModel,
    context: Context = LocalContext.current,
    onEditCategoryMode: MutableState<Boolean>
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
                items(categories.size + 1) { index ->
                    if (index == categories.size) {
                        AddCategoryDialogItem(onAddCategory = {
                            shouldShowAddCategoryDialog.value = true
                        })
                        return@items
                    }
                    CategoryDialogItem(
                        item = categories[index].name,
                        emoji = categories[index].emoji,
                        onCategorySelected = {
                            onCategorySelected(categories[index])
                            onDismiss()
                        },
                        onDeleteCategory = {
                            viewModel.deleteCategory(categories[index])
                            Toast.makeText(
                                context,
                                context.getString(R.string.category_deleted),
                                Toast.LENGTH_SHORT
                            ).show()
                        },
                        onEditCategory = {
                            onEditCategoryMode.value = true
                            shouldShowAddCategoryDialog.value = true
                            viewModel.setSelectedCategoryToEdit(categories[index])
                            viewModel.updateCategoryName("${categories[index].emoji} ${categories[index].name}")
                            viewModel.updateEmoji(categories[index].emoji)
                        }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CategoryDialogItem(
    item: String,
    emoji: String,
    onCategorySelected: () -> Unit,
    onDeleteCategory: () -> Unit,
    onEditCategory: () -> Unit
) {
    val shouldShowCategoryActionDialog = remember { mutableStateOf(false) }
    val shouldShowDeleteCategoryDialog = remember { mutableStateOf(false) }

    DeleteCategoryDialog(
        shouldShowDeleteCategoryDialog,
        onConfirm = onDeleteCategory,
        onCancel = {
            shouldShowDeleteCategoryDialog.value = false
        }
    )

    CategoryActionDialog(
        shouldShowCategoryActionDialog,
        onEditCategory = {
            onEditCategory()
        },
        onDeleteCategory = {
            shouldShowDeleteCategoryDialog.value = true
        }
    )

    Column(
        modifier = Modifier
            .padding(bottom = 12.dp)
            .fillMaxSize()
            .combinedClickable(
                onClick = { onCategorySelected() },
                onLongClick = { shouldShowCategoryActionDialog.value = true }
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(text = emoji, fontSize = 60.sp)
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = item)
    }
}

@Composable
fun CategoryActionDialog(
    shouldShowCategoryActionDialog: MutableState<Boolean> = mutableStateOf(
        false
    ),
    onEditCategory: () -> Unit = {},
    onDeleteCategory: () -> Unit = {}
) {
    if (shouldShowCategoryActionDialog.value)
        Dialog(onDismissRequest = {
            shouldShowCategoryActionDialog.value = false
        }) {
            Card(
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                ) {
                    Row(modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                        .clickable {
                            shouldShowCategoryActionDialog.value = false
                            onEditCategory()
                        }) {
                        Icon(imageVector = Icons.Default.Edit, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text(text = stringResource(R.string.edit_category))
                    }
                    Spacer(Modifier.height(8.dp))
                    Row(modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                        .clickable {
                            shouldShowCategoryActionDialog.value = false
                            onDeleteCategory()
                        }) {
                        Icon(imageVector = Icons.Default.Delete, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text(text = stringResource(R.string.delete_category))
                    }
                }
            }
        }
}

@Composable
fun DeleteCategoryDialog(
    shouldShowDeleteCategoryDialog: MutableState<Boolean> = mutableStateOf(false),
    onConfirm: () -> Unit = {},
    onCancel: () -> Unit = {}
) {
    if (shouldShowDeleteCategoryDialog.value)
        AlertDialog(
            title = {
                Text(text = stringResource(R.string.delete_category))
            },
            text = {
                Text(text = stringResource(R.string.are_you_sure_you_want_to_delete_this_category))
            },
            onDismissRequest = {
                shouldShowDeleteCategoryDialog.value = false
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onConfirm()
                        shouldShowDeleteCategoryDialog.value = false
                    }
                ) {
                    Text(stringResource(R.string.confirm))
                }
            },
            dismissButton = {
                TextButton(
                    onClick = onCancel
                ) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
}

@Composable
fun AddCategoryDialogItem(onAddCategory: () -> Unit) {
    Column(
        modifier = Modifier
            .padding(bottom = 12.dp)
            .fillMaxSize()
            .clickable { onAddCategory() },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Icon(
            modifier = Modifier.size(80.dp),
            imageVector = Icons.Default.Add,
            contentDescription = stringResource(R.string.add_category),
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = stringResource(R.string.add_category))
    }
}

@Composable
fun AddCategoryDialog(
    shouldAddShowCategoryDialog: MutableState<Boolean> = mutableStateOf(false),
    onCancel: () -> Unit = {},
    viewModel: CategoriesViewModel,
    context: Context = LocalContext.current,
    onEditCategoryMode: MutableState<Boolean>
) {
    if (shouldAddShowCategoryDialog.value) {
        Dialog(onDismissRequest = {
            shouldAddShowCategoryDialog.value = false
            onEditCategoryMode.value = false
        }) {
            Card(
                modifier = Modifier
                    .height(200.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxHeight()
                        .padding(16.dp)
                ) {
                    Text(
                        if (onEditCategoryMode.value) stringResource(R.string.edit_category) else stringResource(
                            R.string.add_category
                        ),
                        style = MaterialTheme.typography.headlineMedium
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    Row(
                        modifier = Modifier,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = viewModel.emoji,
                            fontSize = 48.sp,
                            style = TextStyle(platformStyle = PlatformTextStyle(emojiSupportMatch = EmojiSupportMatch.None))
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        OutlinedTextField(
                            value = viewModel.categoryName,
                            onValueChange = { name ->
                                viewModel.updateEmoji(name)
                                viewModel.updateCategoryName(name)
                            },
                            placeholder = {
                                Text(stringResource(R.string.category_name))
                            },
                            maxLines = 1,
                        )
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(onClick = onCancel) {
                            Text(stringResource(R.string.cancel))
                        }
                        TextButton(onClick = {
                            if (viewModel.categoryName.isEmpty()) {
                                Toast.makeText(
                                    context,
                                    context.getString(
                                        R.string.field_cannot_be_empty,
                                        "Category Name"
                                    ), Toast.LENGTH_SHORT
                                ).show()
                                return@TextButton
                            }
                            if (onEditCategoryMode.value) viewModel.updateCategory()
                            else viewModel.insertCategory()
                            onEditCategoryMode.value = false
                            onCancel()
                        }) {
                            Text(
                                if (onEditCategoryMode.value) stringResource(R.string.edit) else stringResource(
                                    R.string.save
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview(name = "CategoriesDialog")
@Composable
fun CategoriesDialogPreview() {
    CategoriesDialog(
        shouldShowCategoryDialog = rememberSaveable { mutableStateOf(true) },
        onDismiss = {},
        onCategorySelected = {},
        categories = listOf(
            Category(categoryId = 3, name = "Food", emoji = "🍔"),
            Category(categoryId = 4, name = "Transport", emoji = "🚌")
        ),
        shouldShowAddCategoryDialog = rememberSaveable { mutableStateOf(true) },
        viewModel = hiltViewModel(),
        onEditCategoryMode = remember { mutableStateOf(false) }
    )
}

@Preview("AddCategoryDialog")
@Composable
fun AddCategoryDialogPreview() {
    AddCategoryDialog(
        shouldAddShowCategoryDialog = rememberSaveable { mutableStateOf(true) },
        onCancel = {},
        viewModel = hiltViewModel(),
        onEditCategoryMode = remember { mutableStateOf(false) }
    )
}

@Preview
@Composable
fun CategoryActionDialogPreview() {
    BajetTheme {
        CategoryActionDialog()
    }
}