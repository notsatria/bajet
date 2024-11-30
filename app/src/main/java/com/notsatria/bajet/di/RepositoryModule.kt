package com.notsatria.bajet.di

import com.notsatria.bajet.data.room.CashFlowDao
import com.notsatria.bajet.repository.AddCashFlowRepository
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
}