package com.notsatria.bajet.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    "budget",
    indices = [
        Index(value = ["categoryId"], unique = true)
    ]
)
data class Budget(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    @ColumnInfo("categoryId")
    val categoryId: Int,
)
