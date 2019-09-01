package com.task.sphtask.pojo

import java.math.BigDecimal

/**
 * Created by Marimuthu on 2019-08-29.
 */
data class RecordPojo (
    val data_volume: BigDecimal,
    val year: Int,
    val quarter: String,
    val isDown: Boolean,
    val downQuarterName: String
)

data class TotalUsagePojo(
    val totalVolume: BigDecimal,
    val year: Int,
    val isDown: Boolean,
    val usageDetails: List<RecordPojo>
)