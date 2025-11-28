package com.example.finup.list.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.finup.Transactions.list.data.db.dao.TransactionDao
import com.example.finup.Transactions.list.data.db.dao.YearMonthDao
import com.example.finup.Transactions.list.data.db.entities.TransactionCache
import com.example.finup.Transactions.list.data.db.entities.YearMonthCache

@Database(entities = [YearMonthCache::class, TransactionCache::class], version = 1)
abstract class AppDataBase: RoomDatabase() {

    abstract fun dateItemDao(): YearMonthDao
    abstract fun transactionDao(): TransactionDao

}