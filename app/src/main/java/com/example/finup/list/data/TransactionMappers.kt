package com.example.finup.list.data

import com.example.finup.Transactions.list.data.db.entities.TransactionCache
import com.example.finup.Transactions.list.domain.Transaction

fun List<TransactionCache>.transactionListToDomain() =
    this.map { Transaction(it.id, it.sum, it.name, it.type, it.day, it.dateId) }

fun TransactionCache.transactionToDomain() =
    Transaction(this.id, this.sum, this.name, this.type, this.day, this.dateId)