package com.example.finup.domain

import android.annotation.SuppressLint
import android.icu.util.Calendar
import com.example.finup.domain.DateProvider.All
import java.time.Clock
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.Locale

interface DateProvider {

    interface GetYear {
        fun getCurrentYear(): Int
    }

    interface GetMonth {
        fun getCurrentMonth(): Int
    }

    interface FormatDate {
        fun formatDate(year: Int, month: Int): String
    }
    interface FormatLongToDateComponents {
        fun formatLongToDateComponents(date: Long): Triple<Int,Int,Int>
    }

    interface Getters : GetYear, GetMonth
    interface All : Getters, FormatDate, FormatLongToDateComponents

}

@SuppressLint("NewApi")
class DateProviderImpl(
    private val locale: Locale = Locale.getDefault(),
    private val clock: Clock = Clock.systemDefaultZone(),
) : All {

    override fun getCurrentYear() = YearMonth.now(clock).year


    override fun getCurrentMonth() = YearMonth.now(clock).monthValue


    override fun formatDate(year: Int, month: Int): String {
        val date = YearMonth.of(year, month)
        val formatter = DateTimeFormatter.ofPattern("MMMM yyyy", locale)
        return date.format(formatter)
    }

    override fun formatLongToDateComponents(date: Long): Triple<Int, Int, Int> {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = date

        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val month = calendar.get(Calendar.MONTH) + 1
        val year = calendar.get(Calendar.YEAR)

        return Triple(day, month, year)
    }
}

@SuppressLint("NewApi")
class FakeDateProviderImpl(
    private val locale: Locale = Locale.getDefault(),
) : All {

    override fun getCurrentYear() = 2025


    override fun getCurrentMonth() = 9


    override fun formatDate(year: Int, month: Int): String {
        val date = YearMonth.of(year, month)
        val formatter = DateTimeFormatter.ofPattern("MMMM yyyy", locale)
        return date.format(formatter)
    }

    override fun formatLongToDateComponents(date: Long): Triple<Int, Int, Int> {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = date

        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val month = calendar.get(Calendar.MONTH) + 1
        val year = calendar.get(Calendar.YEAR)

        return Triple(day, month, year)
    }
}
