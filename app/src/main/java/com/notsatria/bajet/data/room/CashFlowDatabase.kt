package com.notsatria.bajet.data.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.notsatria.bajet.R
import com.notsatria.bajet.data.entities.CashFlow
import com.notsatria.bajet.data.entities.Category
import com.notsatria.bajet.utils.Helper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONException

@Database(entities = [CashFlow::class, Category::class], version = 1)
abstract class CashFlowDatabase : RoomDatabase() {

    abstract fun cashFlowDao(): CashFlowDao
    abstract fun categoryDao(): CategoryDao

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
                    .addCallback(object : Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            CoroutineScope(Dispatchers.IO).launch {
                                prepopulateCategories(context, getInstance(context).cashFlowDao())
                            }
                        }
                    })
                    .fallbackToDestructiveMigration()
                    .build()
            }
        }

        fun prepopulateCategories(context: Context, dao: CashFlowDao) {
            val jsonArray = Helper.loadJsonArray(context, R.raw.category, "categories")
            try {
                if (jsonArray != null) {
                    for (i in 0 until jsonArray.length()) {
                        val item = jsonArray.getJSONObject(i)
                        dao.insertCategory(
                            Category(
                                categoryId = item.getInt("categoryId"),
                                name = item.getString("name"),
                                emoji = item.getString("emoji")
                            )
                        )
                    }
                }
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }
    }
}