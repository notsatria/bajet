package com.notsatria.bajet.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.Companion.REPLACE
import androidx.room.Query
import com.notsatria.bajet.data.entities.Account
import com.notsatria.bajet.data.entities.relation.AccountsRaw
import kotlinx.coroutines.flow.Flow

@Dao
interface AccountDao {

    @Insert(onConflict = REPLACE)
    fun insert(account: Account)

    @Query(
        """
        SELECT 
        account.id as accountId,
        account.name as accountName,
        account.balance as amount,
        account_group.name as groupName,
        account_group.id as accountGroupId
        FROM account
        JOIN account_group
        ON account.groupId = account_group.id
        """
    )
    fun getAllAccountsAndGroup(): Flow<List<AccountsRaw>>

    @Query("SELECT * FROM account")
    fun getAllAccounts(): Flow<List<Account>>

    @Query("UPDATE account SET balance = balance + :amount WHERE id = :accountId")
    suspend fun updateAmount(accountId: Int, amount: Double)
}