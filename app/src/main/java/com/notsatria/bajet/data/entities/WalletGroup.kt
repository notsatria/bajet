package com.notsatria.bajet.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity("wallet_group")
data class WalletGroup(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String
)
