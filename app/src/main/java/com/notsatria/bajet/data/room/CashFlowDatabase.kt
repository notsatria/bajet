package com.notsatria.bajet.data.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.notsatria.bajet.data.entities.CashFlow
import com.notsatria.bajet.data.entities.Category

@Database(entities = [CashFlow::class, Category::class], version = 1)
abstract class CashFlowDatabase : RoomDatabase() {

    abstract fun dao(): CashFlowDao

}