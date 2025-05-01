package com.notsatria.bajet.data.entities.relation

data class AccountsRaw(
    val accountId: Int,
    val accountName: String,
    val amount: Double,
    val groupName: String,
    val accountGroupId: Int
)