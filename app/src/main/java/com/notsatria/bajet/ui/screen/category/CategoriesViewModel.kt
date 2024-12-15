package com.notsatria.bajet.ui.screen.category

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.notsatria.bajet.data.entities.Category
import com.notsatria.bajet.repository.CategoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber.Forest.i
import javax.inject.Inject

@HiltViewModel
class CategoriesViewModel @Inject constructor(private val categoryRepository: CategoryRepository) :
    ViewModel() {

    private var _categories = MutableStateFlow<List<Category>>(emptyList())
    val categories = _categories.asStateFlow()

    var emoji by mutableStateOf("😊")
        private set

    var categoryName by mutableStateOf("$emoji category")
        private set

    fun updateCategoryName(value: String) {
        i("updateCategoryName: $value")
        categoryName = value
    }

    fun updateEmoji(value: String) {
        emoji = value.trim().split(" ").first()
    }

    fun insertCategory() {
        viewModelScope.launch(Dispatchers.IO) {
            val category = Category(name = categoryName.trim().split(" ").last(), emoji = emoji)
            categoryRepository.insertCategory(category)
        }
    }

    // get categories and filter not income and expenses
    fun getCategories() {
        viewModelScope.launch(Dispatchers.IO) {
            categoryRepository.getCategories().collect {
                _categories.value =
                    it.filter { category -> category.categoryId != 1 && category.categoryId != 2 }
            }
        }
    }

    fun deleteCategory(category: Category) {
        viewModelScope.launch(Dispatchers.IO) {
            categoryRepository.deleteCategory(category)
        }
    }
}