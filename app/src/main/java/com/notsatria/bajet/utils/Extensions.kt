package com.notsatria.bajet.utils

import java.text.NumberFormat

fun Double.formatToRupiah(): String {
    return NumberFormat.getCurrencyInstance(LOCALE_ID).also {
        it.maximumFractionDigits = 0
    }.format(this)
}

/**
 * Format a string to currency.
 */
fun String.formatToCurrency(): String {
    return try {
        val parsedAmount = this.toLongOrNull() ?: 0L
        NumberFormat.getNumberInstance(LOCALE_ID).format(parsedAmount)
    } catch (e: Exception) {
        this // Fallback to the raw input if formatting fails
    }
}