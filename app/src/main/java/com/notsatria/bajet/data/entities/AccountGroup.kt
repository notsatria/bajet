package com.notsatria.bajet.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity("account_group")
data class AccountGroup(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String
)