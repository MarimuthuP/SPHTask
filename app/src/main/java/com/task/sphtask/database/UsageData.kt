package com.task.sphtask.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

/**
 * Created by Marimuthu on 2019-09-03.
 */

@Entity(tableName = "UsageDetails")
data class UsageData(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "qId")
    var id: Int = 0,

    @ColumnInfo(name = "year")
    var year: String = "",

    @ColumnInfo(name = "dataVolume")
    var dataVolume: String = "",

    @ColumnInfo(name = "isDecreased")
    var isDecrease: Boolean = false,

    @ColumnInfo(name = "usageRecord")
    var usageList: String = ""
) : Serializable