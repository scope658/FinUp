package com.example.finup.Transactions.list.data

import com.example.finup.Transactions.list.data.db.dao.TransactionDao
import com.example.finup.Transactions.list.data.db.entities.TransactionCache
import com.example.finup.core.data.Now
import com.example.finup.Transactions.list.domain.Transaction
import com.example.finup.Transactions.list.domain.TransactionRepository

class TransactionRepositoryImpl(
    private val transactionDao: TransactionDao,
    private val now: Now
) : TransactionRepository.EditAndCreate, TransactionRepository.GetTransactions {

    override suspend fun createTransaction(
        sum: Int,
        name: String,
        type: String,
        day: Int,
        dateId: Long
    ): Long {
        val newId = now.timeInMills()
        val newTransactionCache = TransactionCache(newId, sum, name, type, day, dateId)
        transactionDao.insert(newTransactionCache)
        return newId
    }

    override suspend fun editTransaction(
        transactionId: Long,
        sum: Int,
        name: String,
        type: String,
        day: Int,
        dateId: Long
    ) {
        val newCache = TransactionCache(transactionId, sum, name, type, day, dateId)
        transactionDao.insert(newCache)
    }

    override suspend fun getOneTransaction(id: Long, type: String): Transaction {
        val transactionCache = transactionDao.getOneTransaction(id, type)
        return transactionCache.transactionToDomain()
    }

    override suspend fun getTransactions(dateId: Long, type: String): List<Transaction> {
        val listTransactions = transactionDao.getTransactions(dateId, type)
        return listTransactions.transactionListToDomain()
    }

    override suspend fun deleteTransaction(id: Long) {
        transactionDao.delete(id)
    }
}