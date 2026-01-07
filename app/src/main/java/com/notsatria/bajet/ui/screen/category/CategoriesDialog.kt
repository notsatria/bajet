package com.notsatria.bajet.ui.screen.category

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.notsatria.bajet.ui.components.BajetOutlinedTextField
import com.notsatria.bajet.ui.theme.BajetTheme
import com.notsatria.bajet.utils.DummyData
import kotlinx.coroutines.flow.collectLatest

@Composable
fun CategoryManagementScreen(
    categories: List<Category>,
    onCategorySelected: (Category) -> Unit,
    shouldShowCategoryDialog: MutableState<Boolean>,
    context: Context = LocalContext.current,
    viewModel: CategoriesViewModel = hiltViewModel()
) {
    val shouldShowAddCategoryDialog = remember { mutableStateOf(false) }
    val onEditCategoryMode = remember { mutableStateOf(false) }

    // Collect UI events (errors)
    LaunchedEffect(Unit) {
        viewModel.uiEvent.collectLatest { event ->
            when (event) {
                is CategoriesUiEvent.ShowError -> {
                    Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    CategoriesDialog(
        shouldShowCategoryDialog = shouldShowCategoryDialog,
        onDismiss = { shouldShowCategoryDialog.value = false },
        categories = categories,
        shouldShowAddCategoryDialog = shouldShowAddCategoryDialog,
        onCategorySelected = onCategorySelected,
        onEditCategoryMode = onEditCategoryMode,
        onDeleteCategory = { category ->
            viewModel.deleteCategory(category)
            Toast.makeText(
                context,
                context.getString(R.string.category_deleted),
                Toast.LENGTH_SHORT
            ).show()
        },
        onEditCategory = { category ->
            viewModel.setSelectedCategoryToEdit(category)
            viewModel.updateEmoji(category.emoji)
            viewModel.updateCategoryName("${category.emoji} ${category.name}")
        }
    )

    AddCategoryDialog(
        shouldAddShowCategoryDialog = shouldShowAddCategoryDialog,
        onCancel = {
            shouldShowAddCategoryDialog.value = false
            onEditCategoryMode.value = false
        },
        onEditCategoryMode = onEditCategoryMode,
        emoji = viewModel.emoji,
        onCategoryNameChange = { name ->
            viewModel.updateCategoryName(name)
        },
        categoryName = viewModel.categoryName,
        onSaveOrEditClicked = {
            if (viewModel.categoryName.isEmpty()) {
                Toast.makeText(
                    context,
                    context.getString(
                        R.string.field_cannot_be_empty,
                        "Category Name"
                    ), Toast.LENGTH_SHORT
                ).show()
                return@AddCategoryDialog
            }
            if (onEditCategoryMode.value) viewModel.updateCategory()
            else viewModel.insertCategory()
            onEditCategoryMode.value = false
        },
        onEmojiChange = { emoji ->
            viewModel.updateEmoji(emoji)
        }
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoriesDialog(
    shouldShowCategoryDialog: MutableState<Boolean>,
    onDismiss: () -> Unit,
    onCategorySelected: (category: Category) -> Unit,
    categories: List<Category> = emptyList(),
    shouldShowAddCategoryDialog: MutableState<Boolean> = mutableStateOf(false),
    onEditCategoryMode: MutableState<Boolean>,
    onDeleteCategory: (category: Category) -> Unit = {},
    onEditCategory: (category: Category) -> Unit = {}
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
                        onDeleteCategory = { onDeleteCategory(categories[index]) },
                        onEditCategory = {
                            onEditCategoryMode.value = true
                            shouldShowAddCategoryDialog.value = true
                            onEditCategory(categories[index])
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
                    Row(
                        modifier = Modifier
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
                    Row(
                        modifier = Modifier
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
    onEditCategoryMode: MutableState<Boolean>,
    emoji: String = "",
    categoryName: String = "",
    onCategoryNameChange: (String) -> Unit = {},
    onSaveOrEditClicked: () -> Unit = {},
    onEmojiChange: (String) -> Unit = {},
) {
    val shouldShowEmojiPicker = remember { mutableStateOf(false) }

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
                            text = emoji,
                            fontSize = 48.sp,
                            modifier = Modifier
                                .clickable { shouldShowEmojiPicker.value = true }
                                .padding(8.dp),
                            style = TextStyle(platformStyle = PlatformTextStyle(emojiSupportMatch = EmojiSupportMatch.None))
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        BajetOutlinedTextField(
                            value = categoryName,
                            onValueChange = onCategoryNameChange,
                            placeholder = stringResource(R.string.category_name),
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
                            onSaveOrEditClicked()
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

    // Emoji Picker Dialog
    if (shouldShowEmojiPicker.value) {
        EmojiPickerDialog(
            shouldShow = shouldShowEmojiPicker,
            onEmojiSelected = { selectedEmoji ->
                onEmojiChange(selectedEmoji)
                shouldShowEmojiPicker.value = false
            }
        )
    }
}

@Composable
fun EmojiPickerDialog(
    shouldShow: MutableState<Boolean>,
    onEmojiSelected: (String) -> Unit
) {
    val emojis = listOf(
        "😊", "😂", "😍", "🥰", "😎", "🤩", "😘", "😁",
        "😄", "😃", "😀", "😆", "😅", "🤣", "😌", "😔",
        "😕", "😲", "😱", "😤", "😡", "😠", "🤬", "😈",
        "💀", "☠️", "💩", "🤡", "👻", "👽", "👾", "🤖",
        "😻", "😸", "😹", "😺", "😻", "😼", "😽", "😾",
        "❤️", "🧡", "💛", "💚", "💙", "💜", "🖤", "🤍",
        "💔", "💕", "💞", "💓", "💗", "💖", "💘", "💝",
        "🎈", "🎉", "🎊", "🎁", "🎀", "🎂", "🍰", "🧁",
        "🍕", "🍔", "🍟", "🌭", "🥪", "🥙", "🧆", "🌮",
        "🌯", "🥗", "🍝", "🍜", "🍲", "🍛", "🍣", "🍱",
        "🥘", "🍢", "🍙", "🍚", "🍌", "🍎", "🍊", "🍋",
        "🍌", "🍉", "🍇", "🍓", "🍈", "🍒", "🍑", "🥭",
        "🐶", "🐱", "🐭", "🐹", "🐰", "🦊", "🐻", "🐼",
        "🐨", "🐯", "🦁", "🐮", "🐷", "🐸", "🐵", "🙈",
        "🙉", "🙊", "🐒", "🐔", "🐧", "🐦", "🐤", "🦆",
        "🚗", "🚕", "🚙", "🚌", "🚎", "🏎️", "🚐", "🛻",
        "🚚", "🚛", "🚜", "🏍️", "🏎️", "🛵", "🦯", "🦽",
        "⚽", "🏀", "🏈", "⚾", "🥎", "🎾", "🏐", "🏉",
        "🥏", "🎳", "🏓", "🏸", "🏒", "🏑", "🥍", "🏏",
        "🌈", "⭐", "✨", "⚡", "❄️", "☄️", "💥", "🔥",
        "🎨", "🎭", "🎪", "🎬", "🎤", "🎧", "🎼", "🎹",
        "🎸", "🎺", "🎷", "🥁", "🎻", "🎲", "🎯", "🎳"
    )

    if (shouldShow.value) {
        Dialog(
            onDismissRequest = { shouldShow.value = false },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .height(500.dp),
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.background
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Select Emoji",
                        style = MaterialTheme.typography.headlineSmall,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    LazyVerticalGrid(
                        columns = GridCells.Fixed(count = 6),
                        modifier = Modifier
                            .fillMaxSize()
                            .weight(1f),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(emojis.size) { index ->
                            Surface(
                                modifier = Modifier
                                    .size(60.dp)
                                    .clickable {
                                        onEmojiSelected(emojis[index])
                                    },
                                shape = RoundedCornerShape(8.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant
                            ) {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = emojis[index],
                                        fontSize = 32.sp
                                    )
                                }
                            }
                        }
                    }

                    TextButton(
                        onClick = { shouldShow.value = false },
                        modifier = Modifier
                            .align(Alignment.End)
                            .padding(top = 16.dp)
                    ) {
                        Text("Close")
                    }
                }
            }
        }
    }
}

@Preview(name = "CategoriesDialog")
@Composable
fun CategoriesDialogPreview() {
    BajetTheme {
        CategoriesDialog(
            shouldShowCategoryDialog = rememberSaveable { mutableStateOf(true) },
            onDismiss = {},
            onCategorySelected = {},
            categories = DummyData.categories,
            shouldShowAddCategoryDialog = rememberSaveable { mutableStateOf(true) },
            onEditCategoryMode = remember { mutableStateOf(false) }
        )
    }
}

@Preview("AddCategoryDialog")
@Composable
fun AddCategoryDialogPreview() {
    BajetTheme {
        AddCategoryDialog(
            shouldAddShowCategoryDialog = rememberSaveable { mutableStateOf(true) },
            onCancel = {},
            onEditCategoryMode = remember { mutableStateOf(false) }
        )
    }
}

@Preview
@Composable
fun CategoryActionDialogPreview() {
    BajetTheme {
        CategoryActionDialog(shouldShowCategoryActionDialog = remember { mutableStateOf(true) })
    }
}