package com.notsatria.bajet.utils

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date

object DateUtils {
    val formatDate1 = SimpleDateFormat("EEE, dd MMM yyyy", LOCALE_ID)
    val formatDate2 = SimpleDateFormat("dd MMM yyyy", LOCALE_ID)
    val formatDate3 = SimpleDateFormat("MMMM yyyy", LOCALE_ID)
    val formatDate4 = SimpleDateFormat("dd MMM yyyy (EEE)", LOCALE_ID)
    val formatDate5 = SimpleDateFormat("MMMM", LOCALE_ID)

    fun Long.formatDateTo(format: SimpleDateFormat = formatDate1): String {
        val date = Date(this)
        return format.format(date)
    }

    fun Date.formatDateTo(format: SimpleDateFormat = formatDate1): String {
        return format.format(this)
    }

    fun Calendar.formatDateTo(format: SimpleDateFormat = formatDate1): String {
        val date = this.time
        return format.format(date)
    }

    fun getStartAndEndDate(date: Calendar): Pair<Long, Long> {
        val startOfMonth = date.clone() as Calendar
        startOfMonth.set(Calendar.DAY_OF_MONTH, 1)
        startOfMonth.set(Calendar.HOUR_OF_DAY, 0)
        startOfMonth.set(Calendar.MINUTE, 0)
        startOfMonth.set(Calendar.SECOND, 0)
        startOfMonth.set(Calendar.MILLISECOND, 0)

        val endOfMonth = startOfMonth.clone() as Calendar
        endOfMonth.set(Calendar.DAY_OF_MONTH, date.getActualMaximum(Calendar.DAY_OF_MONTH))
        endOfMonth.set(Calendar.HOUR_OF_DAY, 23)
        endOfMonth.set(Calendar.MINUTE, 59)
        endOfMonth.set(Calendar.SECOND, 59)
        endOfMonth.set(Calendar.MILLISECOND, 999)

        return Pair(startOfMonth.timeInMillis, endOfMonth.timeInMillis)
    }

}