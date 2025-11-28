package com.example.finup.createEdit.data

import com.example.finup.createEdit.domain.YearMonthRepository
import com.example.finup.Transactions.list.data.db.dao.YearMonthDao
import com.example.finup.Transactions.list.data.db.entities.YearMonthCache
import com.example.finup.core.data.Now
import com.example.finup.createEdit.domain.YearMonth

class YearMonthRepositoryImpl(
    private val yearMonthDao: YearMonthDao,
    private val now: Now
) : YearMonthRepository.CreateAndLoad, YearMonthRepository.GetAllPeriods,
    YearMonthRepository.GetAndCreate, YearMonthRepository.Delete {

    override suspend fun getAllPeriods(): List<YearMonth> {

        val currentPeriods = yearMonthDao.getAllPeriods()
        return currentPeriods.yearMonthListToDomain()
    }

    override suspend fun create(year: Int, month: Int): YearMonth {
        val newId = now.timeInMills()
        var newCache = YearMonthCache(newId, year, month)
        yearMonthDao.insert(newCache)
        return newCache.yearMonthToDomain()
    }

    override suspend fun getById(yearMonthId: Long): YearMonth {
        return yearMonthDao.getDateItem(yearMonthId).yearMonthToDomain()
    }

    override suspend fun delete(dateId: Long) {
        yearMonthDao.delete(dateId)
    }

}