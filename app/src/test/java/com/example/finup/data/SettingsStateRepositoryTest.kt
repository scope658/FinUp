package com.example.finup.data

import com.example.finup.Transactions.list.data.SettingsStateRepositoryImpl
import com.example.finup.Transactions.list.domain.DataStoreManager
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test


class SettingsStateRepositoryTest {

    private lateinit var settingsStateRepositoryImpl: SettingsStateRepositoryImpl
    private lateinit var dataStore: FakeDataStoreManager

    @Before
    fun setUp(){
        dataStore = FakeDataStoreManager.Base()
        settingsStateRepositoryImpl = SettingsStateRepositoryImpl(dataStore)
    }

    @Test
    fun `save and restore yearMonthId`() = runBlocking{
        settingsStateRepositoryImpl.saveActiveYearMonthId(35L)
        val actualYearMonthId = settingsStateRepositoryImpl.restoreActiveYearMonthId()
        val expectedYearMonthId = 35L
        assertEquals(actualYearMonthId,expectedYearMonthId)
    }

    @Test
    fun`save and restore screenType`() = runBlocking {
        settingsStateRepositoryImpl.saveScreenType("Expense")
        val actualScreenType = settingsStateRepositoryImpl.restoreScreenType()
        val expectedScreenType = "Expense"
        assertEquals(expectedScreenType,actualScreenType)
    }
}

private interface FakeDataStoreManager: DataStoreManager.All {

    class Base: FakeDataStoreManager {

        private var actualYearMonthId = 0L
        private lateinit var actualScreenType: String

        override suspend fun saveActiveYearMonthId(id: Long) {
            actualYearMonthId = id
        }

        override suspend fun restoreActiveYearMonthId(): Long {
            return actualYearMonthId
        }

        override suspend fun saveActiveScreeType(screenType: String) {
            actualScreenType = screenType
        }

        override suspend fun restoreActiveScreenType(): String {
            return actualScreenType
        }
    }
}