package com.example.finup.Transactions.list.domain

interface TransactionRepository {


    interface CreateTransaction {
        suspend fun createTransaction(
            sum: Int,
            name: String,
            type: String,
            day: Int,
            dateId: Long
        ): Long
    }

    interface GetTransaction {
        suspend fun getOneTransaction(id: Long, type: String): Transaction
    }

    interface EditTransaction {
        suspend fun editTransaction(
            transactionId: Long,
            sum: Int,
            name: String,
            type: String,
            day: Int,
            dateId: Long
        )
    }

    interface DeleteTransaction {
        suspend fun deleteTransaction(id: Long)
    }

    interface GetTransactions {
        suspend fun getTransactions(dateId: Long, type: String): List<Transaction>
    }

    interface EditAndCreate : GetTransaction, EditTransaction, DeleteTransaction, CreateTransaction

}