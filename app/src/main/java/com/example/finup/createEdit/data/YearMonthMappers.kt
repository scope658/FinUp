package com.example.finup.createEdit.data

import com.example.finup.Transactions.list.data.db.entities.YearMonthCache
import com.example.finup.createEdit.domain.YearMonth

fun YearMonthCache.yearMonthToDomain() = YearMonth(this.id,this.month,this.year)
fun List<YearMonthCache>.yearMonthListToDomain() = this.map { YearMonth(it.id,it.month,it.year) }

