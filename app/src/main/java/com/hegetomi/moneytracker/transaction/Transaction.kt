package com.hegetomi.moneytracker.transaction

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "transaction")
data class Transaction(
    @PrimaryKey(autoGenerate = true) var transactionId: Long?,
    @ColumnInfo(name = "category") val category: String,
    @ColumnInfo(name = "price") val price: Double,
    @ColumnInfo(name = "isincome") val isIncome: Boolean
)