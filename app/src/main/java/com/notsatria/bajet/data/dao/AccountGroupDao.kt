package com.notsatria.bajet.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.notsatria.bajet.data.entities.AccountGroup
import kotlinx.coroutines.flow.Flow

@Dao
interface AccountGroupDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(accountGroup: AccountGroup)

    @Query("SELECT * FROM account_group")
    fun getAll(): Flow<List<AccountGroup>>

}