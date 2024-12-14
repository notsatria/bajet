package com.notsatria.bajet.repository

import com.notsatria.bajet.data.entities.Category
import com.notsatria.bajet.data.room.CategoryDao

class CategoryRepository(private val categoryDao: CategoryDao) {

    fun insertCategory(category: Category) = categoryDao.insertCategory(category)

    fun getCategories() = categoryDao.getCategories()

    fun deleteCategory(category: Category) = categoryDao.deleteCategory(category)

}