package com.notsatria.bajet.di

import com.notsatria.bajet.data.dao.BudgetDao
import com.notsatria.bajet.data.dao.BudgetMonthDao
import com.notsatria.bajet.data.dao.CashFlowDao
import com.notsatria.bajet.data.dao.CategoryDao
import com.notsatria.bajet.repository.AddCashFlowRepository
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
    fun provideBudgetRepository(dao: BudgetDao, budgetMonthDao: BudgetMonthDao): BudgetRepository {
        return BudgetRepository(dao, budgetMonthDao)
    }

}