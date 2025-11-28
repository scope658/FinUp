package com.example.finup.list.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.finup.Transactions.list.domain.DataStoreManager
import kotlinx.coroutines.flow.first

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "YearMonthStateId")


class DataStoreManagerImpl(context: Context) : DataStoreManager.All {
    private val dataStore = context.dataStore

    companion object {
        val CURRENT_MONTH_ID_KEY = longPreferencesKey("current_month_id")
        const val DEFAULT_ID = 0L
        val CURRENT_SCREEN_TYPE_KEY = stringPreferencesKey("current_screen_type")
        const val FIRST_RUN_SCREEN_TYPE = "Expense"
    }

    override suspend fun saveActiveYearMonthId(id: Long) {
        dataStore.edit {
            it[CURRENT_MONTH_ID_KEY] = id
        }
    }

    override suspend fun restoreActiveYearMonthId(): Long {
        return dataStore.data
            .first()[CURRENT_MONTH_ID_KEY] ?: DEFAULT_ID
    }

    override suspend fun saveActiveScreeType(screenType: String) {
        dataStore.edit {
            it[CURRENT_SCREEN_TYPE_KEY] = screenType
        }
    }

    override suspend fun restoreActiveScreenType(): String {
        return dataStore.data
            .first()[CURRENT_SCREEN_TYPE_KEY] ?: FIRST_RUN_SCREEN_TYPE
    }
}