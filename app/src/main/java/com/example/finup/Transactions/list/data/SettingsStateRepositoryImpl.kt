package com.example.finup.Transactions.list.data

import com.example.finup.Transactions.list.domain.DataStoreManager
import com.example.finup.Transactions.list.domain.SettingsStateRepository

class SettingsStateRepositoryImpl(private val dataStoreManager: DataStoreManager.All): SettingsStateRepository.All {

    override suspend fun restoreActiveYearMonthId(): Long {
        return dataStoreManager.restoreActiveYearMonthId()
    }

    override suspend fun saveActiveYearMonthId(id: Long) {
        dataStoreManager.saveActiveYearMonthId(id)
    }

    override suspend fun restoreScreenType(): String {
        return dataStoreManager.restoreActiveScreenType()
    }

    override suspend fun saveScreenType(screenType: String) {
        dataStoreManager.saveActiveScreeType(screenType)
    }

}