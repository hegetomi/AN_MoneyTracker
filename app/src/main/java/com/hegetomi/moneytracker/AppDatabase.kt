package com.hegetomi.moneytracker

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.hegetomi.moneytracker.transaction.Transaction
import com.hegetomi.moneytracker.transaction.TransactionDAO

@Database(entities = [Transaction::class], version = 1)
abstract class AppDatabase : RoomDatabase() {

    abstract fun transactionDAO(): TransactionDAO

    companion object{

        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {

            if(INSTANCE == null){
                INSTANCE = Room.databaseBuilder(context.applicationContext,
                    AppDatabase::class.java,"transaction.db").build()
            }

            return INSTANCE!!
        }

    }
}