package com.example.finup.Transactions.list.domain

import com.example.finup.createEdit.domain.DateProvider
import com.example.finup.createEdit.domain.YearMonth

interface TransactionsListUseCase {

    suspend operator fun invoke(yearMonth: YearMonth, type: String): Result

    class Base(
        private val transactionRepository: TransactionRepository.GetTransactions,
        private val dateProvider: DateProvider.FormatDate
    ) :

        TransactionsListUseCase {

        override suspend fun invoke(yearMonth: YearMonth, type: String): Result {

            val transactionList = transactionRepository.getTransactions(yearMonth.id, type)

            val formattedDateTitle = dateProvider.formatDate(yearMonth.year, yearMonth.month)
            val totalSumByMonth = transactionList.sumOf { it.sum }.toString()

            return Result(transactionList, formattedDateTitle, totalSumByMonth)
        }
    }
}