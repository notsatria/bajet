package com.notsatria.bajet.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity("budget")
data class Budget(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    @ColumnInfo("categoryId")
    val categoryId: Int,
)
