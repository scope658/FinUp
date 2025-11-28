package com.example.finup

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.finup.Transactions.list.data.db.AppDataBase
import com.example.finup.Transactions.list.data.db.entities.TransactionCache
import com.example.finup.Transactions.list.data.db.dao.TransactionDao
import com.example.finup.Transactions.list.data.db.entities.YearMonthCache
import com.example.finup.Transactions.list.data.db.dao.YearMonthDao
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class RoomTest {

    private lateinit var yearMonthDao: YearMonthDao
    private lateinit var transactionDao: TransactionDao
    private lateinit var db: AppDataBase

    @Before
    fun createDb() {

        val context = ApplicationProvider.getApplicationContext<Context>()

        db = Room.inMemoryDatabaseBuilder(
            context,
            AppDataBase::class.java,
        ).build()
        yearMonthDao = db.dateItemDao()
        transactionDao = db.transactionDao()
    }

    @After
    @Throws(IOException::class)
    fun clear() {
        db.close()
    }

    @Test
    fun testAddAndGetYearMonth() = runBlocking {
        yearMonthDao.insert(yearMonthCache = YearMonthCache(id = 3L, month = 12, year = 2021))
        yearMonthDao.insert(yearMonthCache = YearMonthCache(id = 4L, month = 1, year = 2022))
        yearMonthDao.insert(yearMonthCache = YearMonthCache(id = 5L, month = 2, year = 2022))

        val actual = yearMonthDao.getDateItem(3L)
        val expected = YearMonthCache(id = 3L, month = 12, year = 2021)
        assertEquals(expected, actual)

        val actualListPeriods= yearMonthDao.getAllPeriods()
        val expectedListPeriods = listOf(YearMonthCache(id = 3L, month = 12, year = 2021),YearMonthCache(id = 4L, month = 1, year = 2022),YearMonthCache(id = 5L, month = 2, year = 2022))
        assertEquals(expectedListPeriods,actualListPeriods)
    }

    @Test
    fun deleteYearMonth()= runBlocking {
        yearMonthDao.insert(yearMonthCache = YearMonthCache(id = 5L, month = 2, year = 2022))
        yearMonthDao.insert(yearMonthCache = YearMonthCache(id = 6L, month = 3, year = 2023))
        yearMonthDao.delete(dateId = 5L)
        val actualResult = yearMonthDao.getAllPeriods()
        val expectedResult = listOf(YearMonthCache(id = 6L, month = 3, year = 2023))
        assertEquals(expectedResult,actualResult)
    }

    @Test
    fun testDifferentType() = runBlocking {
        transactionDao.insert(
            transactionCache = TransactionCache(
                id = 1L, sum = 1500, name = "Groceries",
                type = "Expense", day = 2, dateId = 1L
            )
        )
        transactionDao.insert(
            transactionCache = TransactionCache(
                id = 2L, sum = 5000, name = "Other",
                type = "Income", day = 7, dateId = 1L
            )
        )
        transactionDao.insert(
            transactionCache = TransactionCache(
                id = 3L, sum = 3500, name = "payments",
                type = "Expense", day = 18, dateId = 1L
            )
        )
        transactionDao.insert(
            transactionCache = TransactionCache(
                id = 4L, sum = 15000, name = "Kaspi Bank",
                type = "Income", day = 29, dateId = 1L
            )
        )
        val actual = transactionDao.getTransactions(dateId = 1L, type = "Expense")

        val expected = listOf(
            TransactionCache(
                id = 1L, sum = 1500, name = "Groceries",
                type = "Expense", day = 2, dateId = 1L
            ),
            TransactionCache(
                id = 3L, sum = 3500, name = "payments",
                type = "Expense", day = 18, dateId = 1L
            ),
        )
        assertEquals(expected, actual)
    }


    @Test
    fun testIncomes() = runBlocking {
        transactionDao.insert(
            transactionCache = TransactionCache(
                id = 2L, sum = 5000, name = "Kaspi Bank",
                type = "Income", day = 25, dateId = 1L
            )
        )
        transactionDao.insert(
            transactionCache = TransactionCache(
                id = 3L, sum = 1000, name = "Kaspi Bank",
                type = "Income", day = 15, dateId = 1L
            )
        )
        transactionDao.insert(
            transactionCache = TransactionCache(
                id = 4L, sum = 15000, name = "BCC bank",
                type = "Income", day = 29, dateId = 2L
            )
        )
        transactionDao.delete(id = 3L)
        val actual = transactionDao.getTransactions(dateId = 1L, type = "Income")
        val expected = listOf(
            TransactionCache(
                id = 2L, sum = 5000, name = "Kaspi Bank",
                type = "Income", day = 25, dateId = 1L
            )
        )
        assertEquals(expected, actual)
    }

    @Test
    fun editTransaction() = runBlocking {
        transactionDao.insert(
            transactionCache = TransactionCache(
                id = 1L, sum = 5000, name = "BCC bank",
                type = "Income", day = 25, dateId = 1L
            )
        )
        transactionDao.insert(
            transactionCache = TransactionCache(
                id = 2L, sum = 1, name = "Kaspi Bank",
                type = "Income", day = 25, dateId = 1L
            )
        )


        transactionDao.insert(
            TransactionCache(
                id = 1L, sum = 250000, name = "Kaspi Bank",
                type = "Income", day = 10, dateId = 1L
            )
        )
        val expected = transactionDao.getOneTransaction(id = 1L, type = "Income")

        val actual = TransactionCache(
            id = 1L, sum = 250000, name = "Kaspi Bank",
            type = "Income", day = 10, dateId = 1L
        )
        assertEquals(expected, actual)
    }
}