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
    version = 3,
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
                    .addMigrations(MIGRATION_1_2, MIGRATION_2_3)
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
                                color = Helper.randomColor().toArgb(),
                                type = item.optString("type", "EXPENSE")
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
            override fun migrate(db: SupportSQLiteDatabase) {
                // Recreate the budget table with the correct schema (including the unique index on categoryId)
                // Step 1: Create a new budget table with correct schema
                db.execSQL(
                    """CREATE TABLE budget_new (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        categoryId INTEGER NOT NULL
                    )""".trimMargin()
                )

                // create new BudgetEntry table
                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS budget_entry_new (
                        budgetMonthId INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        budgetId INTEGER NOT NULL,
                        month INTEGER NOT NULL,
                        year INTEGER NOT NULL,
                        amount REAL NOT NULL,
                        FOREIGN KEY(budgetId) REFERENCES budget(id) ON DELETE CASCADE
                    )
                """.trimIndent())

                // create new Account table
                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS account_new (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        groupId INTEGER NOT NULL,
                        name TEXT NOT NULL,
                        balance REAL NOT NULL,
                        FOREIGN KEY(groupId) REFERENCES account_group(id) ON DELETE CASCADE
                    )
                """.trimIndent())
                
                // Step 2: Copy data from old table to new table
                db.execSQL("INSERT INTO budget_new (id, categoryId) SELECT id, categoryId FROM budget")

                // copy data from old budget_entry to new budget_entry_new
                db.execSQL("""
                    INSERT INTO budget_entry_new (budgetMonthId, budgetId, month, year, amount
                    )
                    SELECT budgetMonthId, budgetId, month, year, amount FROM budget_entry
                """.trimIndent())

                // copy data from old account to new account_new
                db.execSQL("""
                    INSERT INTO account_new (id, groupId, name, balance
                    )
                    SELECT id, groupId, name, balance FROM account
                """.trimIndent())
                
                // Step 3: Drop old table
                db.execSQL("DROP TABLE budget")
                db.execSQL("DROP TABLE budget_entry")
                db.execSQL("DROP TABLE account")
                
                // Step 4: Rename new table to original name
                db.execSQL("ALTER TABLE budget_new RENAME TO budget")
                db.execSQL("ALTER TABLE budget_entry_new RENAME TO budget_entry")
                db.execSQL("ALTER TABLE account_new RENAME TO account")
                
                // Step 5: Create the unique index on categoryId
                db.execSQL("CREATE UNIQUE INDEX index_budget_categoryId ON budget(categoryId)")
            }
        }

        private val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // Step 1: Add type column with default value EXPENSE
                db.execSQL("ALTER TABLE category ADD COLUMN type TEXT NOT NULL DEFAULT 'EXPENSE'")

                // Step 2: Update existing "Income" category (ID 1) to INCOME type
                db.execSQL("UPDATE category SET type = 'INCOME' WHERE id = 1")

                // Step 3: Insert new income categories
                val incomeCategories = listOf(
                    "INSERT INTO category (id, name, emoji, color, type) VALUES (16, 'Salary', '💼', ${Helper.randomColor().toArgb()}, 'INCOME')",
                    "INSERT INTO category (id, name, emoji, color, type) VALUES (17, 'Freelance', '💻', ${Helper.randomColor().toArgb()}, 'INCOME')",
                    "INSERT INTO category (id, name, emoji, color, type) VALUES (18, 'Investment', '📈', ${Helper.randomColor().toArgb()}, 'INCOME')",
                    "INSERT INTO category (id, name, emoji, color, type) VALUES (19, 'Gift Received', '🎁', ${Helper.randomColor().toArgb()}, 'INCOME')",
                    "INSERT INTO category (id, name, emoji, color, type) VALUES (20, 'Business', '🏪', ${Helper.randomColor().toArgb()}, 'INCOME')",
                    "INSERT INTO category (id, name, emoji, color, type) VALUES (21, 'Bonus', '💵', ${Helper.randomColor().toArgb()}, 'INCOME')"
                )

                incomeCategories.forEach { sql ->
                    try {
                        db.execSQL(sql)
                    } catch (e: Exception) {
                        // Category might already exist, skip
                        Timber.e("Migration 2->3: ${e.message}")
                    }
                }
            }
        }
    }
}