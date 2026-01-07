package com.notsatria.bajet.data.database

import android.content.Context
import androidx.compose.ui.graphics.toArgb
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.notsatria.bajet.R
import com.notsatria.bajet.data.dao.AccountDao
import com.notsatria.bajet.data.dao.AccountGroupDao
import com.notsatria.bajet.data.dao.BudgetDao
import com.notsatria.bajet.data.dao.BudgetEntryDao
import com.notsatria.bajet.data.dao.CashFlowDao
import com.notsatria.bajet.data.dao.CategoryDao
import com.notsatria.bajet.data.entities.Account
import com.notsatria.bajet.data.entities.AccountGroup
import com.notsatria.bajet.data.entities.Budget
import com.notsatria.bajet.data.entities.BudgetEntry
import com.notsatria.bajet.data.entities.CashFlow
import com.notsatria.bajet.data.entities.Category
import com.notsatria.bajet.utils.Helper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONException
import timber.log.Timber

@Database(
    entities = [
        CashFlow::class,
        Category::class,
        Budget::class,
        BudgetEntry::class,
        Account::class,
        AccountGroup::class
    ],
    version = 2,
    exportSchema = false
)
abstract class CashFlowDatabase : RoomDatabase() {

    abstract fun cashFlowDao(): CashFlowDao
    abstract fun categoryDao(): CategoryDao
    abstract fun budgetDao(): BudgetDao
    abstract fun budgetMonthDao(): BudgetEntryDao
    abstract fun accountDao(): AccountDao
    abstract fun accountGroupDao(): AccountGroupDao

    companion object {

        @Volatile
        private var INSTANCE: CashFlowDatabase? = null

        fun getInstance(context: Context): CashFlowDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context,
                    CashFlowDatabase::class.java,
                    "cashflow.db"
                )
                    .addMigrations(MIGRATION_1_2)
                    .addCallback(object : Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            CoroutineScope(Dispatchers.IO).launch {
                                prepopulateData(context)
                            }
                        }
                    })
                    .build()
            }
        }

        private fun prepopulateData(context: Context) {
            val db = getInstance(context)
            prepopulateCategories(context, db.cashFlowDao())
            prepopulateAccountGroup(context, db.accountGroupDao())
            prepopulateDefaultAccount(db.accountDao())
        }

        private fun prepopulateCategories(context: Context, dao: CashFlowDao) {
            val jsonArray = Helper.loadJsonArray(context, R.raw.category, "categories")
            try {
                if (jsonArray != null) {
                    for (i in 0 until jsonArray.length()) {
                        val item = jsonArray.getJSONObject(i)
                        dao.insertCategory(
                            Category(
                                id = item.getInt("id"),
                                name = item.getString("name"),
                                emoji = item.getString("emoji"),
                                color = Helper.randomColor().toArgb()
                            )
                        )
                    }
                }
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }

        private fun prepopulateAccountGroup(context: Context, dao: AccountGroupDao) {
            val jsonArray = Helper.loadJsonArray(context, R.raw.account_group, "account_groups")
            try {
                if (jsonArray != null) {
                    for (i in 0 until jsonArray.length()) {
                        val item = jsonArray.getJSONObject(i)
                        dao.insert(
                            AccountGroup(
                                id = item.getInt("id"),
                                name = item.getString("name")
                            )
                        )
                    }
                }
            } catch (e: JSONException) {
                e.printStackTrace()
                Timber.e("Error on populateGroupAccounts ${e.message}")
            }
        }

        private fun prepopulateDefaultAccount(dao: AccountDao) {
            try {
                dao.insert(Account(id = 1, name = "Cash", balance = 0.0, groupId = 1))
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // If there were any schema changes from version 1 to 2, define them here
                // For now, this is empty because we're just upgrading without schema changes
                // If you added new columns or tables, add the SQL statements here
            }
        }
    }
}