package com.task.sphtask.network

import com.task.sphtask.pojo.DataUsagePojo
import retrofit2.Call
import retrofit2.http.GET

/**
 * Created by Marimuthu on 2019-08-29.
 */
interface RetrofitAPI {
    @GET(WebUrls.GET_LIMIT)
    fun getDataUsage(): Call<DataUsagePojo>
}