package com.notsatria.bajet.repository

import com.notsatria.bajet.data.dao.AccountDao
import com.notsatria.bajet.data.dao.AccountGroupDao
import com.notsatria.bajet.data.entities.Account

class AccountRepository(
    private val accountDao: AccountDao,
    private val accountGroupDao: AccountGroupDao
) {

    fun getAllAccountGroup() = accountGroupDao.getAll()

    suspend fun insertAccount(account: Account) = accountDao.insert(account)

    fun getAllAccount() = accountDao.getAllAccount()
}