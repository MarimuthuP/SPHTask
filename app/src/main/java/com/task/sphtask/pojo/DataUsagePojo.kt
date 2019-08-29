package com.task.sphtask.pojo
import com.google.gson.annotations.SerializedName


/**
 * Created by Marimuthu on 2019-08-29.
 */
data class DataUsagePojo(
    @SerializedName("help")
    val help: String,
    @SerializedName("result")
    val result: Result,
    @SerializedName("success")
    val success: Boolean
) {
    data class Result(
        @SerializedName("fields")
        val fields: List<Field>,
        @SerializedName("limit")
        val limit: Int,
        @SerializedName("_links")
        val links: Links,
        @SerializedName("records")
        val records: List<Record>,
        @SerializedName("resource_id")
        val resourceId: String,
        @SerializedName("total")
        val total: Int
    ) {
        data class Record(
            @SerializedName("_id")
            val id: Int,
            @SerializedName("quarter")
            val quarter: String,
            @SerializedName("volume_of_mobile_data")
            val volumeOfMobileData: String
        )

        data class Links(
            @SerializedName("next")
            val next: String,
            @SerializedName("start")
            val start: String
        )

        data class Field(
            @SerializedName("id")
            val id: String,
            @SerializedName("type")
            val type: String
        )
    }
}