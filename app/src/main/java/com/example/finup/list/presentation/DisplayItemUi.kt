package com.example.finup.list.presentation

interface DisplayItemUi {
    fun getItemType(): Int

    data class TransactionDate(
        val day: String,
        val totalSumByDay: String,
    ): DisplayItemUi {
        override fun getItemType(): Int {
            return 0
        }
    }
    data class TransactionDetails(
        val id: Long,
        val sum: Int,
        val name: String,
        val type: String,
        val dateId: Long,
    ): DisplayItemUi {
        override fun getItemType(): Int {
            return 1
        }
    }
}