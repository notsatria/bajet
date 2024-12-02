package com.notsatria.bajet.utils

import java.util.Locale

val LOCALE_ID = Locale("in", "ID")

enum class CashFlowTypes(val type: String) {
    INCOME("Income"),
    EXPENSES("Expenses")
}