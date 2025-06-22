package com.notsatria.bajet.data.repository

import com.notsatria.bajet.data.dao.CategoryDao
import com.notsatria.bajet.data.entities.Category
import kotlinx.coroutines.flow.Flow

interface CategoryRepository {
    suspend fun insertCategory(category: Category)
    fun getCategories(): Flow<List<Category>>
    suspend fun deleteCategory(category: Category)
    suspend fun updateCategory(category: Category)
}

class CategoryRepositoryImpl(private val categoryDao: CategoryDao) : CategoryRepository {

    override suspend fun insertCategory(category: Category) {
        categoryDao.insertCategory(category)
    }

    override fun getCategories(): Flow<List<Category>> = categoryDao.getCategories()

    override suspend fun deleteCategory(category: Category) = categoryDao.deleteCategory(category)

    override suspend fun updateCategory(category: Category) = categoryDao.updateCategory(category)

}