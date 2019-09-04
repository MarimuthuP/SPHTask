package com.task.sphtask.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

/**
 * Created by Marimuthu on 2019-09-03.
 */
@Database(entities = [(UsageData::class)], version = 1, exportSchema = false)
abstract class UsageDatabase : RoomDatabase() {
    companion object {
        private var INSTANCE: UsageDatabase? = null
        fun getDatabase(context: Context): UsageDatabase {
            if (INSTANCE == null) {
                INSTANCE = Room.databaseBuilder(context.applicationContext, UsageDatabase::class.java, "UsageDb")
                    .allowMainThreadQueries()
                    .build()
            }
            return INSTANCE as UsageDatabase
        }
    }

    abstract fun usageDao(): UsageDao
}