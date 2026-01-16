package com.notsatria.bajet.ui.screen.category

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.notsatria.bajet.data.entities.Category
import com.notsatria.bajet.data.repository.CategoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber.Forest.e
import timber.log.Timber.Forest.i
import javax.inject.Inject

sealed class CategoriesUiEvent {
    data class ShowError(val message: String) : CategoriesUiEvent()
}

@HiltViewModel
class CategoriesViewModel @Inject constructor(private val categoryRepository: CategoryRepository) :
    ViewModel() {

    companion object {
        private const val INCOME_CATEGORY_ID = 1
        private const val EXPENSE_CATEGORY_ID = 2
    }

    private var _categories = MutableStateFlow<List<Category>>(emptyList())
    val categories = _categories.asStateFlow()

    var selectedCategoryToEdit: Category? = null
        private set

    var emoji by mutableStateOf("😊")
        private set

    var categoryName by mutableStateOf("Category")
        private set

    private val _uiEvent = Channel<CategoriesUiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    fun updateCategoryName(value: String) {
        i("updateCategoryName: $value")
        categoryName = value
    }

    fun updateEmoji(value: String) {
        emoji = value.trim().split(" ").first()
    }

    fun setSelectedCategoryToEdit(category: Category) {
        i("setSelectedCategoryToEdit: $category")
        selectedCategoryToEdit = category
    }

    private fun resetCategoryForm() {
        emoji = "😊"
        categoryName = "Category"
        selectedCategoryToEdit = null
    }

    fun insertCategory() {
        viewModelScope.launch() {
            try {
                // Capture current values to avoid race conditions
                val currentName = categoryName
                val currentEmoji = emoji

                val splittedName = currentName.trim().split(" ")
                val cleanedName = if (splittedName.size > 2) {
                    splittedName.subList(1, splittedName.size).joinToString(" ")
                } else {
                    splittedName.last()
                }
                val category = Category(name = cleanedName, emoji = currentEmoji)

                withContext(Dispatchers.IO) {
                    categoryRepository.insertCategory(category)
                }

                // Update UI state on Main dispatcher
                withContext(Dispatchers.Main) {
                    categoryName = cleanedName
                    resetCategoryForm()
                }
            } catch (e: Exception) {
                e("insertCategory Error: ${e.message}")
                _uiEvent.trySend(CategoriesUiEvent.ShowError("Failed to insert category: ${e.message}"))
            }
        }
    }

    fun updateCategory() {
        val selected = selectedCategoryToEdit ?: return  // Safe null check
        viewModelScope.launch {
            try {
                // Capture current values to avoid race conditions
                val currentName = categoryName
                val currentEmoji = emoji

                val splittedName = currentName.trim().split(" ")
                val cleanedName = if (splittedName.size > 2) {
                    splittedName.subList(1, splittedName.size).joinToString(" ")
                } else {
                    splittedName.last()
                }

                withContext(Dispatchers.IO) {
                    categoryRepository.updateCategory(
                        selected.copy(
                            name = cleanedName, emoji = currentEmoji
                        )
                    )
                }

                // Update UI state on Main dispatcher
                withContext(Dispatchers.Main) {
                    categoryName = cleanedName
                    resetCategoryForm()
                }
            } catch (e: Exception) {
                e("updateCategory Error: ${e.message}")
                _uiEvent.trySend(CategoriesUiEvent.ShowError("Failed to update category: ${e.message}"))
            }
        }
    }

    // get categories and filter not income and expenses
    fun getCategories(type: String? = null) {
        viewModelScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    val flow = if (type != null) {
                        categoryRepository.getCategoriesByType(type)
                    } else {
                        categoryRepository.getCategories()
                    }
                    
                    flow.collect { categories ->
                        withContext(Dispatchers.Main) {
                            _categories.value = categories.filter { category ->
                                category.id != INCOME_CATEGORY_ID && category.id != EXPENSE_CATEGORY_ID
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                e("getCategories Error: ${e.message}")
                _uiEvent.trySend(CategoriesUiEvent.ShowError("Failed to load categories: ${e.message}"))
            }
        }
    }

    fun deleteCategory(category: Category) {
        viewModelScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    categoryRepository.deleteCategory(category)
                }
                // Clear selection if deleted category was selected
                withContext(Dispatchers.Main) {
                    if (selectedCategoryToEdit?.id == category.id) {
                        selectedCategoryToEdit = null
                    }
                }
            } catch (e: Exception) {
                e("deleteCategory Error: ${e.message}")
                _uiEvent.trySend(CategoriesUiEvent.ShowError("Failed to delete category: ${e.message}"))
            }
        }
    }

}