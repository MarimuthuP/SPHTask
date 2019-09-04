package com.task.sphtask.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

/**
 * Created by Marimuthu on 2019-09-03.
 */
@Dao
interface UsageDao {

    @Query("select * from UsageDetails")
    fun getAllUsageData(): LiveData<List<UsageData>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertUsageData(list: List<UsageData>)
}