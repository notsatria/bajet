package com.notsatria.bajet.utils

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

object DateUtils {

    private val _currentLocale = MutableStateFlow<Locale>(Locale.getDefault())
    val currentLocale: StateFlow<Locale> = _currentLocale

    /**
     * Called from MainActivity when language changes
     */
    fun updateLocale(locale: Locale) {
        _currentLocale.value = locale
    }

    val formatDate1: SimpleDateFormat =
        SimpleDateFormat("EEE, dd MMM yyyy", _currentLocale.value)

    val formatDate2: SimpleDateFormat =
        SimpleDateFormat("dd MMM yyyy", _currentLocale.value)

    val formatDate3: SimpleDateFormat =
        SimpleDateFormat("MMMM yyyy", _currentLocale.value)

    val formatDate4: SimpleDateFormat =
        SimpleDateFormat("dd MMM yyyy (EEE)", _currentLocale.value)

    val formatDate5: SimpleDateFormat = SimpleDateFormat("MMMM", _currentLocale.value)
    val formatDate6: SimpleDateFormat = SimpleDateFormat("MMM", _currentLocale.value)

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

    fun getMonthAndYear(calendar: Calendar = Calendar.getInstance()): MonthAndYear {
        return MonthAndYear(calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.YEAR))
    }

    fun getNextMonth(currentDate: Calendar): Calendar {
        return Calendar.getInstance().apply {
            time = currentDate.time
            add(Calendar.MONTH, 1)
        }
    }

    fun getPreviousMonth(currentDate: Calendar): Calendar {
        return Calendar.getInstance().apply {
            time = currentDate.time
            add(Calendar.MONTH, -1)
        }
    }

    fun getCurrentMonth(): Int {
        return Calendar.getInstance().get(Calendar.MONTH) + 1
    }

    fun Int.toMonthName(format: SimpleDateFormat = DateUtils.formatDate6): String {
        val month = Calendar.getInstance().apply {
            set(Calendar.MONTH, this@toMonthName - 1)
        }.time

        return format.format(month)
    }
}

data class MonthAndYear(
    val month: Int,
    val year: Int
)