package com.notsatria.bajet.utils

import androidx.annotation.StringRes
import com.notsatria.bajet.R
import java.util.Locale

val LOCALE_ID = Locale("in", "ID")

object CashFlowType {
    const val INCOME = "Income"
    const val EXPENSES = "Expenses"
}

enum class ThemeMode(val value: String, val id: Int, @StringRes val resId: Int) {
    LIGHT("light", 0, R.string.light),
    DARK("dark", 1, R.string.dark),
    SYSTEM("system", 2, R.string.system)
}