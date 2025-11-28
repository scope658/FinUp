package com.example.finup.Transactions.list.presentation

import com.example.finup.Transactions.list.domain.Transaction

interface TransactionUiMapper {
    interface ToUiLayer {
        fun toUiLayer(transactions: List<Transaction>, month: String): List<DisplayItemUi>
    }

    class Base : ToUiLayer {
        override fun toUiLayer(
            transactions: List<Transaction>,
            month: String
        ): List<DisplayItemUi> {
            val currentMonth = month.split(" ")[0].lowercase()
            val newGroupedList = transactions.sortedBy { it.day }.groupBy { it.day }
            return newGroupedList.flatMap { (day, transactions) ->
                listOf(
                    DisplayItemUi.TransactionDate(
                        "$day $currentMonth",
                        newGroupedList.getValue(day).sumOf { it.sum }.toString()
                    )
                ) + transactions.map {
                    DisplayItemUi.TransactionDetails(
                        it.id,
                        it.sum,
                        it.name,
                        it.type,
                        it.dateId,
                    )
                }
            }
        }
    }
}