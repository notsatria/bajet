package com.notsatria.bajet.utils

import java.text.SimpleDateFormat
import java.util.Date

object DateUtils {
    val formatDate1 = SimpleDateFormat("EEE, dd MMM yyyy", LOCALE_ID)

    fun Long.formatDateTo(format: SimpleDateFormat = formatDate1): String {
        val date = Date(this)
        return format.format(date)
    }
}