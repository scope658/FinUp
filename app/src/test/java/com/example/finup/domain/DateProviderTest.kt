package com.example.finup.domain

import junit.framework.TestCase.assertEquals
import org.junit.Test
import java.time.Clock
import java.time.Instant
import java.time.ZoneId
import java.util.Locale


class DateProviderTest {

    @Test
    fun test() {
        val fixedClock = Clock.fixed(
            Instant.parse("2025-09-15T00:00:00Z"),
            ZoneId.systemDefault(),

        )
        val locale = Locale.ENGLISH
        val dateProvider = DateProviderImpl(locale,fixedClock)
        val currentYear = dateProvider.getCurrentYear()
        val expectedYear = 2025
        assertEquals(expectedYear,currentYear)

        val currentMonth = dateProvider.getCurrentMonth()
        val expectedMonth = 9
        assertEquals(expectedMonth,currentMonth)

        val actualFormattedDate = dateProvider.formatDate(currentYear,currentMonth)
        val expectedFormattedDate = "September 2025"
        assertEquals(expectedFormattedDate,actualFormattedDate)
    }
}
