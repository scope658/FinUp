package com.example.finup.list.domain

interface SettingsStateRepository {
    interface RestoreYearMonthId {
        suspend fun restoreActiveYearMonthId(): Long
    }

    interface SaveYearMonthId {
        suspend fun saveActiveYearMonthId(id: Long)
    }

    interface RestoreScreenType {
        suspend fun restoreScreenType(): String
    }

    interface SaveScreenType {
        suspend fun saveScreenType(screenType: String)
    }
    interface All : RestoreYearMonthId, SaveYearMonthId, RestoreScreenType, SaveScreenType
}