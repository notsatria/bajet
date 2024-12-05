package com.notsatria.bajet.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity("category")
data class Category(
    @PrimaryKey(autoGenerate = true)
    val categoryId: Int = 0,

    @ColumnInfo("name")
    val name: String,

    @ColumnInfo("emoji")
    val emoji: String
)