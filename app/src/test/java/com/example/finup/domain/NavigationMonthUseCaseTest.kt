package com.example.finup.domain

import com.example.finup.createEdit.domain.YearMonth
import com.example.finup.createEdit.domain.YearMonthRepository
import com.example.finup.Transactions.list.domain.NavigationMonthUseCase
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.runBlocking
import org.junit.Test

class NavigationMonthUseCaseTest {

    private lateinit var navigationMonthUseCase: NavigationMonthUseCase
    private lateinit var yearMonthRepository: FakeGetYearMonthRepository

    @Test
    fun test() = runBlocking {
        yearMonthRepository = FakeGetYearMonthRepository.Base()
        navigationMonthUseCase = NavigationMonthUseCase.Base(yearMonthRepository)
        yearMonthRepository.expectedListYearMonth(listOf(
            YearMonth(1L, 11, 2025),
            YearMonth(2L, 12, 2025)
        ))
        val actualYearMonth = navigationMonthUseCase(YearMonth(2L, 12, 2025), false)
        val expectedYearMonth = YearMonth(1L, 11, 2025)
        yearMonthRepository.checkCalledTimes(1)
        assertEquals(expectedYearMonth,actualYearMonth)


        val actual = navigationMonthUseCase(YearMonth(1L, 11, 2025),false)
        yearMonthRepository.checkCalledTimes(2)
        val expected = YearMonth(1L, 11, 2025)
        assertEquals(actual,expected)
    }
}

private interface FakeGetYearMonthRepository : YearMonthRepository.GetAllPeriods {

    fun expectedListYearMonth(expected: List<YearMonth>)
    fun checkCalledTimes(expected: Int)

    class Base : FakeGetYearMonthRepository {

        private lateinit var mockedList: List<YearMonth>
        private var actualCalledTime = 0

        override suspend fun getAllPeriods(): List<YearMonth> {
            actualCalledTime++
            return mockedList
        }

        override fun expectedListYearMonth(expected: List<YearMonth>) {
            mockedList = expected
        }

        override fun checkCalledTimes(expected: Int) {
            assertEquals(actualCalledTime, expected)
        }
    }
}