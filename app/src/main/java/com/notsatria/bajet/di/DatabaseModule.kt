package com.notsatria.bajet.di

import android.content.Context
import androidx.room.Room
import com.notsatria.bajet.data.room.CashFlowDao
import com.notsatria.bajet.data.room.CashFlowDatabase
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
        return Room.databaseBuilder(context, CashFlowDatabase::class.java, "cashflow.db").build()
    }

    @Provides
    fun provideCashflowDao(db: CashFlowDatabase): CashFlowDao = db.dao()
}