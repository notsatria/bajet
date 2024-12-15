package com.notsatria.bajet.data.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.notsatria.bajet.data.entities.Category
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCategory(category: Category)

    @Query("SELECT * FROM category")
    fun getCategories(): Flow<List<Category>>

    @Delete
    fun deleteCategory(category: Category)

    @Update
    fun updateCategory(category: Category)

}