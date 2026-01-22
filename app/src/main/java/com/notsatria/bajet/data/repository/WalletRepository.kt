package com.notsatria.bajet.data.repository

import com.notsatria.bajet.data.dao.WalletDao
import com.notsatria.bajet.data.dao.WalletGroupDao
import com.notsatria.bajet.data.entities.Wallet
import com.notsatria.bajet.data.entities.WalletGroup
import com.notsatria.bajet.data.entities.relation.WalletsRaw
import kotlinx.coroutines.flow.Flow

interface WalletRepository {
    fun getAllWalletGroup(): Flow<List<WalletGroup>>
    fun insertWallet(wallet: Wallet)
    fun getAllWalletsAndGroup(): Flow<List<WalletsRaw>>
    fun getAllWallets(): Flow<List<Wallet>>
    suspend fun updateAmount(walletId: Int, amount: Double)
}

class WalletRepositoryImpl(
    private val walletDao: WalletDao,
    private val walletGroupDao: WalletGroupDao
) : WalletRepository {
    override fun getAllWalletGroup(): Flow<List<WalletGroup>> = walletGroupDao.getAll()

    override fun insertWallet(wallet: Wallet) = walletDao.insert(wallet)

    override fun getAllWalletsAndGroup(): Flow<List<WalletsRaw>> =
        walletDao.getAllWalletsAndGroup()

    override fun getAllWallets(): Flow<List<Wallet>> = walletDao.getAllWallets()

    override suspend fun updateAmount(walletId: Int, amount: Double) =
        walletDao.updateAmount(walletId, amount)

}
