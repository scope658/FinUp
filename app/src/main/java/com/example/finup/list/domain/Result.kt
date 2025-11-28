package com.example.finup.list.domain

import com.example.finup.Transactions.list.domain.Transaction

data class Result(
    val listTransactions: List<Transaction>,
    val formattedDateYearMonth: String,
    val totalSumByMonth:String,
)