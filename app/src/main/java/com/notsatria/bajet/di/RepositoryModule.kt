package com.notsatria.bajet.di

import com.notsatria.bajet.data.dao.WalletDao
import com.notsatria.bajet.data.dao.WalletGroupDao
import com.notsatria.bajet.data.dao.BudgetDao
import com.notsatria.bajet.data.dao.BudgetEntryDao
import com.notsatria.bajet.data.dao.CashFlowDao
import com.notsatria.bajet.data.dao.CategoryDao
import com.notsatria.bajet.data.repository.WalletRepository
import com.notsatria.bajet.data.repository.WalletRepositoryImpl
import com.notsatria.bajet.data.repository.AddCashFlowRepository
import com.notsatria.bajet.data.repository.AddCashFlowRepositoryImpl
import com.notsatria.bajet.data.repository.AnalyticsRepository
import com.notsatria.bajet.data.repository.AnalyticsRepositoryImpl
import com.notsatria.bajet.data.repository.BudgetRepository
import com.notsatria.bajet.data.repository.BudgetRepositoryImpl
import com.notsatria.bajet.data.repository.CashFlowRepository
import com.notsatria.bajet.data.repository.CashFlowRepositoryImpl
import com.notsatria.bajet.data.repository.CategoryRepository
import com.notsatria.bajet.data.repository.CategoryRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class RepositoryModule {

    @Provides
    @Singleton
    fun provideAddCashFlowRepository(dao: CashFlowDao): AddCashFlowRepository {
        return AddCashFlowRepositoryImpl(dao)
    }

    @Provides
    @Singleton
    fun provideCashFlowRepository(dao: CashFlowDao): CashFlowRepository {
        return CashFlowRepositoryImpl(dao)
    }

    @Provides
    @Singleton
    fun provideCategoryRepository(dao: CategoryDao): CategoryRepository {
        return CategoryRepositoryImpl(dao)
    }

    @Provides
    @Singleton
    fun provideBudgetRepository(
        dao: BudgetDao,
        budgetEntryDao: BudgetEntryDao,
        cashFlowDao: CashFlowDao
    ): BudgetRepository {
        return BudgetRepositoryImpl(dao, budgetEntryDao, cashFlowDao)
    }

    @Provides
    @Singleton
    fun provideAnalyticsRepository(cashFlowDao: CashFlowDao): AnalyticsRepository {
        return AnalyticsRepositoryImpl(cashFlowDao)
    }

    @Provides
    @Singleton
    fun provideWalletRepository(
        walletDao: WalletDao,
        walletGroupDao: WalletGroupDao
    ): WalletRepository {
        return WalletRepositoryImpl(walletDao, walletGroupDao)
    }

}
