package com.example.finup.createEdit.domain

interface GetOrCreatePeriodUseCase {
    suspend operator fun invoke(year: Int, month: Int): YearMonth
    suspend fun getById(id: Long): YearMonth
    class Base(private val repository: YearMonthRepository.GetAndCreate): GetOrCreatePeriodUseCase {

        override suspend fun invoke(year: Int, month: Int): YearMonth {
            val listPeriods = repository.getAllPeriods()
            listPeriods.find { it.month == month && it.year == year }?.let {
                return it
            }
            val createdPeriod = repository.create(year,month)
            return createdPeriod
        }

        override suspend fun getById(id: Long): YearMonth {
            return repository.getById(id)
        }
    }
}