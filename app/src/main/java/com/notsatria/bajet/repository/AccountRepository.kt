package com.notsatria.bajet.repository

import com.notsatria.bajet.data.dao.AccountDao
import com.notsatria.bajet.data.dao.AccountGroupDao
import com.notsatria.bajet.data.entities.Account

class AccountRepository(
    private val accountDao: AccountDao,
    private val accountGroupDao: AccountGroupDao
) {

    fun getAllAccountGroup() = accountGroupDao.getAll()

    fun insertAccount(account: Account) = accountDao.insert(account)

    fun getAllAccountsAndGroup() = accountDao.getAllAccountsAndGroup()

    fun getAllAccounts() = accountDao.getAllAccounts()

    fun updateAmount(accountId: Int, amount: Double) = accountDao.updateAmount(accountId, amount)

}