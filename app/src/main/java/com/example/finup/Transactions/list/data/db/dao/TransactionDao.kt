package com.example.finup.Transactions.list.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.finup.Transactions.list.data.db.entities.TransactionCache


@Dao
interface TransactionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(transactionCache: TransactionCache)


    @Query("SELECT * FROM transaction_table WHERE date_id = :dateId AND transaction_type = :type")
    suspend fun getTransactions(dateId: Long, type: String): List<TransactionCache>


    @Query("DELETE FROM transaction_table WHERE id = :id")
    suspend fun delete(id: Long)

    @Query("SELECT * FROM transaction_table WHERE id = :id AND transaction_type = :type")
    suspend fun getOneTransaction(id: Long,type: String): TransactionCache
}