package com.example.finup.list.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.finup.Transactions.list.data.db.entities.YearMonthCache


@Dao
interface YearMonthDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(yearMonthCache: YearMonthCache)

    @Query("SELECT * FROM date_table WHERE id = :id")
    suspend fun getDateItem(id: Long): YearMonthCache


    @Query("SELECT * FROM date_table ORDER BY year_value, month_value")
    suspend fun getAllPeriods(): List<YearMonthCache>

    @Query("DELETE FROM date_table WHERE id = :dateId")
    suspend fun delete(dateId: Long)
}