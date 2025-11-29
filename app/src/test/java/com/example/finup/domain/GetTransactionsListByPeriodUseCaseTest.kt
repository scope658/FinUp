package com.example.finup.domain

import com.example.finup.createEdit.domain.DateProvider
import com.example.finup.Transactions.list.domain.Result
import com.example.finup.Transactions.list.domain.Transaction
import com.example.finup.createEdit.domain.YearMonth
import com.example.finup.Transactions.list.domain.TransactionRepository
import com.example.finup.Transactions.list.domain.TransactionsListUseCase
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test


class GetTransactionsListByPeriodUseCaseTest {

    private lateinit var transactionsListUseCase: TransactionsListUseCase
    private lateinit var dateProvider: FakeDateProvider
    private lateinit var transactionRepository: FakeReadTransactionRepository

    @Before
    fun setUp(){
        dateProvider = FakeDateProvider.Base()
        transactionRepository = FakeReadTransactionRepository.Base()
        dateProvider.expectedFormattedDate("December 2026")
        transactionsListUseCase =
            TransactionsListUseCase.Base(transactionRepository, dateProvider)
    }
    @Test
    fun `test with expected transactions list`() = runBlocking {
        transactionRepository.expectedTransactions(
            listOf(
                Transaction(
                    2L,
                    3000,
                    "Utilities",
                    "Expense",
                    25,
                    3L
                ), Transaction(
                    3L,
                    4500,
                    "Other",
                    "Expense",
                    13,
                    3L
                )
            )
        )
        val actualResult = transactionsListUseCase(YearMonth(3L, 12, 2026), "Expense")
        val expectedResult = Result(
            listOf(
                Transaction(
                    2L,
                    3000,
                    "Utilities",
                    "Expense",
                    25,
                    3L
                ), Transaction(
                    3L,
                    4500,
                    "Other",
                    "Expense",
                    13,
                    3L
                )
            ), "December 2026",
            "7500"
        )
        transactionRepository.checkGetIsCalled(3L, "Expense")
        dateProvider.check(2026, 12)
        assertEquals(expectedResult,actualResult)
    }

    @Test
    fun`test without expected transactions list`() = runBlocking{
        transactionRepository.expectedTransactions(listOf())
        val actualResult =  transactionsListUseCase(YearMonth(3L, 12, 2026), "Expense")
        val expectedResult = Result(listOf(), "December 2026", "0")
        assertEquals(actualResult,expectedResult)
    }
}


private interface FakeReadTransactionRepository : TransactionRepository.GetTransactions {

    fun expectedTransactions(expected: List<Transaction>)
    fun checkGetIsCalled(expectedDateId: Long, expectedType: String)
    class Base : FakeReadTransactionRepository {

        private lateinit var mockedTransactions: List<Transaction>
        private var actualDateId = 1L
        private lateinit var actualType: String
        override suspend fun getTransactions(dateId: Long, type: String): List<Transaction> {
            actualDateId = dateId
            actualType = type
            return mockedTransactions
        }

        override fun expectedTransactions(expected: List<Transaction>) {
            mockedTransactions = expected
        }

        override fun checkGetIsCalled(expectedDateId: Long, expectedType: String) {
            assertEquals(expectedDateId, actualDateId)
            assertEquals(expectedType, actualType)
        }
    }
}

private interface FakeDateProvider : DateProvider.FormatDate {

    fun expectedFormattedDate(mock: String)
    fun check(expectedYear: Int, expectedMonth: Int)

    class Base : FakeDateProvider {

        private var actualYear = 0
        private var actualMonth = 0
        private lateinit var mockedFormatDate: String

        override fun formatDate(year: Int, month: Int): String {
            actualYear = year
            actualMonth = month
            return mockedFormatDate
        }

        override fun expectedFormattedDate(mock: String) {
            mockedFormatDate = mock
        }

        override fun check(expectedYear: Int, expectedMonth: Int) {
            assertEquals(expectedYear, actualYear)
            assertEquals(expectedMonth,actualMonth)
        }
    }
}