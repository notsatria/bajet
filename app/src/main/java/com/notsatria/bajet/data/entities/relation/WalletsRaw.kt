package com.notsatria.bajet.data.entities.relation

data class WalletsRaw(
    val walletId: Int,
    val walletName: String,
    val amount: Double,
    val groupName: String,
    val walletGroupId: Int
)
