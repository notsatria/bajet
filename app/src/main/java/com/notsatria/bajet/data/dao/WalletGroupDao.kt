package com.notsatria.bajet.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.notsatria.bajet.data.entities.WalletGroup
import kotlinx.coroutines.flow.Flow

@Dao
interface WalletGroupDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(walletGroup: WalletGroup)

    @Query("SELECT * FROM wallet_group")
    fun getAll(): Flow<List<WalletGroup>>

}
