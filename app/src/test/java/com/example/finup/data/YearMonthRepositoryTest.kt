package com.example.finup.data

import com.example.finup.Transactions.list.data.db.dao.YearMonthDao
import com.example.finup.Transactions.list.data.db.entities.YearMonthCache
import com.example.finup.createEdit.data.YearMonthRepositoryImpl
import com.example.finup.createEdit.domain.YearMonth
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class YearMonthRepositoryTest {
    private lateinit var now: FakeNow
    private lateinit var yearMonthDao: FakeYearMonth

    @Before
    fun initialize() {
        now = FakeNow.Base(2L)
        yearMonthDao = FakeYearMonth.Base()
    }

    @Test
    fun yearMonthTest() = runBlocking {
        val yearMonthRepository = YearMonthRepositoryImpl(
            yearMonthDao,
            now,
        )
        val actualNewYearMonth = yearMonthRepository.create(month = 10, year = 2025)
        val expectedNewYearMonth = YearMonth(
            id = 2L,
            month = 10,
            year = 2025,
        )
        assertEquals(expectedNewYearMonth, actualNewYearMonth)

    }
}

private interface FakeYearMonth : YearMonthDao {

    class Base : FakeYearMonth {

        private val newList = mutableListOf<YearMonthCache>()

        override suspend fun insert(dateTitleCache: YearMonthCache) {
            newList.add(dateTitleCache)
        }

        override suspend fun getDateItem(dateId: Long): YearMonthCache {
            return newList.find { (id, _, _) -> id == dateId }!!
        }

        override suspend fun getAllPeriods(): List<YearMonthCache> {
            return newList
        }

        override suspend fun delete(dateId: Long) {
            newList.find { it.id == dateId }.let {
                newList.remove(it)
            }
        }
    }
}
