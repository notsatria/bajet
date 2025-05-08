package com.notsatria.bajet.di

import com.notsatria.bajet.data.dao.AccountDao
import com.notsatria.bajet.data.dao.AccountGroupDao
import com.notsatria.bajet.data.dao.BudgetDao
import com.notsatria.bajet.data.dao.BudgetEntryDao
import com.notsatria.bajet.data.dao.CashFlowDao
import com.notsatria.bajet.data.dao.CategoryDao
import com.notsatria.bajet.repository.AccountRepository
import com.notsatria.bajet.repository.AddCashFlowRepository
import com.notsatria.bajet.repository.AnalyticsRepository
import com.notsatria.bajet.repository.BudgetRepository
import com.notsatria.bajet.repository.CashFlowRepository
import com.notsatria.bajet.repository.CategoryRepository
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
        return AddCashFlowRepository(dao)
    }

    @Provides
    @Singleton
    fun provideCashFlowRepository(dao: CashFlowDao): CashFlowRepository {
        return CashFlowRepository(dao)
    }

    @Provides
    @Singleton
    fun provideCategoryRepository(dao: CategoryDao): CategoryRepository {
        return CategoryRepository(dao)
    }

    @Provides
    @Singleton
    fun provideBudgetRepository(
        dao: BudgetDao,
        budgetEntryDao: BudgetEntryDao,
        cashFlowDao: CashFlowDao
    ): BudgetRepository {
        return BudgetRepository(dao, budgetEntryDao, cashFlowDao)
    }

    @Provides
    @Singleton
    fun provideAnalyticsRepository(cashFlowDao: CashFlowDao): AnalyticsRepository {
        return AnalyticsRepository(cashFlowDao)
    }

    @Provides
    @Singleton
    fun provideAccountRepository(
        accountDao: AccountDao,
        accountGroupDao: AccountGroupDao
    ): AccountRepository {
        return AccountRepository(accountDao, accountGroupDao)
    }

}