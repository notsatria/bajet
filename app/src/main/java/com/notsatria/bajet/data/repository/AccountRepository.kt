package com.notsatria.bajet.data.repository

import com.notsatria.bajet.data.dao.AccountDao
import com.notsatria.bajet.data.dao.AccountGroupDao
import com.notsatria.bajet.data.entities.Account
import com.notsatria.bajet.data.entities.AccountGroup
import com.notsatria.bajet.data.entities.relation.AccountsRaw
import kotlinx.coroutines.flow.Flow

interface AccountRepository {
    fun getAllAccountGroup(): Flow<List<AccountGroup>>
    fun insertAccount(account: Account)
    fun getAllAccountsAndGroup(): Flow<List<AccountsRaw>>
    fun getAllAccounts(): Flow<List<Account>>
    suspend fun updateAmount(accountId: Int, amount: Double)
}

class AccountRepositoryImpl(
    private val accountDao: AccountDao,
    private val accountGroupDao: AccountGroupDao
) : AccountRepository {
    override fun getAllAccountGroup(): Flow<List<AccountGroup>> = accountGroupDao.getAll()

    override fun insertAccount(account: Account) = accountDao.insert(account)

    override fun getAllAccountsAndGroup(): Flow<List<AccountsRaw>> =
        accountDao.getAllAccountsAndGroup()

    override fun getAllAccounts(): Flow<List<Account>> = accountDao.getAllAccounts()

    override suspend fun updateAmount(accountId: Int, amount: Double) =
        accountDao.updateAmount(accountId, amount)

}