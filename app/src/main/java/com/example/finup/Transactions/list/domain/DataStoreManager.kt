package com.example.finup.Transactions.list.domain

interface DataStoreManager {

    interface SaveYearMonthId {
        suspend fun saveActiveYearMonthId(id: Long)
    }

    interface RestoreYearMonthId {
        suspend fun restoreActiveYearMonthId(): Long
    }

    interface SaveScreenType {
        suspend fun saveActiveScreeType(screenType: String)
    }

    interface RestoreScreenType {
        suspend fun restoreActiveScreenType(): String
    }

    interface All: SaveYearMonthId, RestoreYearMonthId, SaveScreenType, RestoreScreenType
}