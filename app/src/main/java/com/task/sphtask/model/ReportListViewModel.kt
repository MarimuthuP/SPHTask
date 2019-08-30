package com.task.sphtask.model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.task.sphtask.network.RetrofitAPI
import com.task.sphtask.network.WebUrls
import com.task.sphtask.pojo.DataUsagePojo
import com.task.sphtask.pojo.RecordPojo
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Created by Marimuthu on 2019-08-29.
 */
class ReportListViewModel : ViewModel() {
    private var dataUsageResponse: MutableLiveData<DataUsagePojo>? = null
    private var dataUsageList: MutableLiveData<List<RecordPojo>>? = null

    fun getDataUsageDetails(): MutableLiveData<DataUsagePojo>? {
        if (dataUsageResponse == null) {
            dataUsageResponse = MutableLiveData()
            loadDataUsageResponse()
        }
        return dataUsageResponse
    }

    fun getUsageDataList(): MutableLiveData<List<RecordPojo>>? {
        if (dataUsageList == null) {
            dataUsageList = MutableLiveData()
            loadDataUsageResponse()
        }
        return dataUsageList
    }


    private fun loadDataUsageResponse() {
        val okHttpClient = OkHttpClient.Builder()
            .readTimeout(90, TimeUnit.SECONDS)
            .connectTimeout(90, TimeUnit.SECONDS)
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl(WebUrls.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val dataUsageUrlApi = retrofit.create(RetrofitAPI::class.java)

        val callDataUsage: Call<JsonElement> = dataUsageUrlApi.getDataUsage()

        callDataUsage.enqueue(object : Callback<JsonElement> {
            override fun onResponse(call: Call<JsonElement>, response: Response<JsonElement>) {
                println("datausage response: " + response.body())
                var gson = Gson()
                val dataUsagePojo = gson.fromJson(response.body(), DataUsagePojo::class.java)
                println("gson response: ${dataUsagePojo.result.records.size} - " + dataUsagePojo.result.records.get(0).volumeOfMobileData)
                loadUsageRecords(dataUsagePojo.result.records)

                dataUsageResponse?.value = gson.fromJson(response.body().toString(), DataUsagePojo::class.java)
            }

            override fun onFailure(call: Call<JsonElement>, t: Throwable) {

            }
        })
    }

    private fun loadUsageRecords(recordList: List<DataUsagePojo.Result.Record>) {
        val listRecord: ArrayList<RecordPojo> = ArrayList()

        var delimiter = "-"
        for (index in recordList) {
            val yearnQuarter = index.quarter.split(delimiter)
            val recordPojo = RecordPojo(
                index.volumeOfMobileData.toBigDecimal(),
                yearnQuarter.get(0).toInt(),
                yearnQuarter.get(1),
                false,
                "Quarter"
            )
            listRecord.add(recordPojo)
        }
        dataUsageList?.value = calculateUsageByYear(listRecord)
    }


    private fun loadUsageTotal(recordList: List<DataUsagePojo.Result.Record>) {
        /*val pattern = Pattern.compile(",")
        val players = recordList
            .map { line ->
                val arr = pattern.split(line.quarter)
                RecordPojo(
                    line.volumeOfMobileData,
                    arr[0].toInt(),
                    arr[1],
                    false,
                    "Quarter"
                )
            }
            .groupBy { it.year }
            .values

        val list: List<RecordPojo> = listOf(players)
        dataUsageList?.value = players*/
    }

    private fun calculateUsageByYear(recordList: List<RecordPojo>): List<RecordPojo> {

        val aggregate = recordList
            .groupingBy(RecordPojo::year)
            .aggregate { _, accumulator: RecordPojo?, element: RecordPojo, _ ->
                accumulator?.let {
                    it.copy(data_volume = it.data_volume + element.data_volume)
                } ?: element
            }


        println("AGGREGATE: "+aggregate.values.toMutableList())
        return aggregate.values.toList()
    }
}