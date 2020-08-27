package com.hegetomi.moneytracker.transaction

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface TransactionDAO {
    @Query("Select * FROM `transaction`")
    fun getTransaction(): List<Transaction>

    @Insert
    fun insertTransaction(vararg transaction: Transaction)

    @Delete
    fun deleteTransaction(transaction: Transaction)

    @Query("Select SUM(price) FROM `transaction` WHERE isincome =1")
    fun getIncomes(): Double

    @Query("Select SUM(price) FROM `transaction` WHERE isincome = 0")
    fun getExpenses(): Double

}