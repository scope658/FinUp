package com.example.finup.list.data.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey


@Entity(
    "date_table",
    indices = [Index(value = ["year_value","month_value"],
        unique = true
    )]
)

data class YearMonthCache(
    @PrimaryKey val id: Long,
    @ColumnInfo(name = "year_value") val year: Int,
    @ColumnInfo(name = "month_value") val month: Int,
)
