package com.notsatria.bajet.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.Companion.REPLACE
import androidx.room.Query
import com.notsatria.bajet.data.entities.Wallet
import com.notsatria.bajet.data.entities.relation.WalletsRaw
import kotlinx.coroutines.flow.Flow

@Dao
interface WalletDao {

    @Insert(onConflict = REPLACE)
    fun insert(wallet: Wallet)

    @Query(
        """
        SELECT 
        wallet.id as walletId,
        wallet.name as walletName,
        wallet.balance as amount,
        wallet_group.name as groupName,
        wallet_group.id as walletGroupId
        FROM wallet
        JOIN wallet_group
        ON wallet.groupId = wallet_group.id
        """
    )
    fun getAllWalletsAndGroup(): Flow<List<WalletsRaw>>

    @Query("SELECT * FROM wallet")
    fun getAllWallets(): Flow<List<Wallet>>

    @Query("UPDATE wallet SET balance = balance + :amount WHERE id = :walletId")
    suspend fun updateAmount(walletId: Int, amount: Double)
}
