package com.notsatria.bajet.data.entities

import androidx.compose.ui.graphics.toArgb
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.notsatria.bajet.utils.Helper

@Entity("category")
data class Category(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    @ColumnInfo("name")
    val name: String,

    @ColumnInfo("emoji")
    val emoji: String,

    @ColumnInfo("color")
    val color: Int = Helper.randomColor().toArgb()
)