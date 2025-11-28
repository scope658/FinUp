package com.example.finup.createEdit.domain

interface YearMonthRepository {


    interface GetAllPeriods {
        suspend fun getAllPeriods(): List<YearMonth>
    }

    interface Create {
        suspend fun create(year: Int, month: Int): YearMonth
    }

    interface LoadYearMonth {
        suspend fun getById(yearMonthId: Long): YearMonth

    }
    interface Delete {
        suspend fun delete(dateId: Long)
    }

    interface CreateAndLoad: Create, LoadYearMonth
    interface GetAndCreate: Create, GetAllPeriods, LoadYearMonth

}