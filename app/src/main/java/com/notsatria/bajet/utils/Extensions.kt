package com.notsatria.bajet.utils

import java.text.NumberFormat

fun Double.formatToRupiah(): String {
    return NumberFormat.getCurrencyInstance(LOCALE_ID).also {
        it.maximumFractionDigits = 0
    }.format(this)
}