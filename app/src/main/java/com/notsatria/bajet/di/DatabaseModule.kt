package com.notsatria.bajet.di

import android.content.Context
import com.notsatria.bajet.data.dao.WalletDao
import com.notsatria.bajet.data.dao.WalletGroupDao
import com.notsatria.bajet.data.dao.BudgetDao
import com.notsatria.bajet.data.dao.BudgetEntryDao
import com.notsatria.bajet.data.dao.CashFlowDao
import com.notsatria.bajet.data.dao.CategoryDao
import com.notsatria.bajet.data.database.CashFlowDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DatabaseModule {

    @Singleton
    @Provides
    fun provideCashflowDatabase(@ApplicationContext context: Context): CashFlowDatabase {
        return CashFlowDatabase.getInstance(context)
    }

    @Provides
    fun provideCashflowDao(db: CashFlowDatabase): CashFlowDao = db.cashFlowDao()

    @Provides
    fun provideCategoryDao(db: CashFlowDatabase): CategoryDao = db.categoryDao()

    @Provides
    fun provideBudgetDao(db: CashFlowDatabase): BudgetDao = db.budgetDao()

    @Provides
    fun provideBudgetMonthDao(db: CashFlowDatabase): BudgetEntryDao = db.budgetMonthDao()

    @Provides
    fun provideWalletDao(db: CashFlowDatabase): WalletDao = db.walletDao()

    @Provides
    fun provideWalletGroupDao(db: CashFlowDatabase): WalletGroupDao = db.walletGroupDao()
}
