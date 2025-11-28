package com.example.finup.Transactions.list.domain

import com.example.finup.createEdit.domain.DateProvider
import com.example.finup.createEdit.domain.YearMonth
import com.example.finup.createEdit.domain.YearMonthRepository

interface StateManager {

    interface RestoreYearMonthId {

        suspend fun restoreYearMonth(): YearMonth
    }

    interface SaveYearMonthId {
        suspend fun saveYearMonthState(id: Long)
    }

    interface SaveScreenType {
        suspend fun saveCurrentScreenType(screenType: String)
    }

    interface RestoreScreenType {
        suspend fun restoreCurrentScreenType(): String
    }

    interface All : RestoreScreenType, SaveScreenType, RestoreYearMonthId, SaveYearMonthId

    class Base(
        private val repository: YearMonthRepository.CreateAndLoad,
        private val settingsStateRepository: SettingsStateRepository.All,
        private val dateProvider: DateProvider.Getters,
    ) : All {

        override suspend fun restoreYearMonth(): YearMonth {
            val currentId = settingsStateRepository.restoreActiveYearMonthId()

            if (currentId != 0L) {
                return repository.getById(currentId)

            } else {
                val newYearMonth =
                    repository.create(dateProvider.getCurrentYear(), dateProvider.getCurrentMonth())
                saveYearMonthState(newYearMonth.id)
                return newYearMonth
            }
        }

        override suspend fun saveYearMonthState(id: Long) {
            settingsStateRepository.saveActiveYearMonthId(id)
        }

        override suspend fun restoreCurrentScreenType(): String {
            return settingsStateRepository.restoreScreenType()
        }

        override suspend fun saveCurrentScreenType(screenType: String) {
            settingsStateRepository.saveScreenType(screenType)
        }
    }
}