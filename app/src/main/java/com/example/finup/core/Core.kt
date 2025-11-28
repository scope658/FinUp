package com.example.finup.core

import android.content.Context
import androidx.room.Room
import com.example.finup.Transactions.list.data.db.AppDataBase

class Core(context: Context) {

    val db = Room.databaseBuilder(
        context,
        AppDataBase::class.java,
        "dataBase"
    ).build()

    fun transactionDao() = db.transactionDao()
    fun yearMonthDao() = db.dateItemDao()
}