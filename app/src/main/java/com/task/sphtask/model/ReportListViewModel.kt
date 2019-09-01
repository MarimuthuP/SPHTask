package com.task.sphtask.model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.task.sphtask.network.RetrofitAPI
import com.task.sphtask.network.WebUrls
import com.task.sphtask.pojo.DataUsagePojo
import com.task.sphtask.pojo.RecordPojo
import com.task.sphtask.pojo.TotalUsagePojo
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.math.BigDecimal
import java.util.concurrent.TimeUnit

/**
 * Created by Marimuthu on 2019-08-29.
 */
class ReportListViewModel : ViewModel() {
    private var dataUsageResponse: MutableLiveData<DataUsagePojo>? = null
    private var dataUsageList: MutableLiveData<List<RecordPojo>>? = null
    private var totalDataUsageList: MutableLiveData<List<TotalUsagePojo>>? = null

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
                yearnQuarter.get(1)
            )
            listRecord.add(recordPojo)
        }
        calculateUsageByYearNew(listRecord)
        dataUsageList?.value = calculateUsageByYear(listRecord)
    }


    private fun calculateUsageByYearNew(recordList: List<RecordPojo>) {

        val listMain = ArrayList<TotalUsagePojo>()
        //region isolate the list by year...
        val aggregateNew = recordList
            .groupBy { it.year }

        val amountTotal = recordList
            .map { it.data_volume }.fold(BigDecimal.ZERO, { total, next -> total + next })
        //endregion

        //region isolate the list with accumulated the usage by the year
        val aggregate = recordList
            .groupingBy(RecordPojo::year)
            .aggregate { _, accumulator: RecordPojo?, element: RecordPojo, _ ->
                accumulator?.let {
                    it.copy(data_volume = it.data_volume + element.data_volume)
                } ?: element
            }
        //endregion

        aggregateNew.entries.forEach { (key, value) ->
            println("LIST year: ${key} - value: ${value}")
        }

        aggregate.entries.forEach { (key, value) ->
            println("ACCU year: ${key} - value: ${value}")
        }

        val listResult = aggregate.toList()
        val listDetailResult = aggregateNew.toList()

        for (i in 0 until listResult.size) {
            val totalUsagePojo = TotalUsagePojo(
                listResult.get(i).second.data_volume,
                        listResult.get(i).second.year,
                false,
                listDetailResult.get(i).second
            )
            listMain.add(totalUsagePojo)
        }

        for (i in 0 until listMain.size){
            println("==> ${listMain.get(i).year} - ${listMain.get(i).totalVolume} - ${listMain.get(i).isDown} - ${listMain.get(i).usageDetails}")
        }

        println("Only list details SIZE: " + aggregateNew.entries.size)
        println("accumulated list SIZE: " + aggregate.entries.size)
        println("totalUsagePojo list SIZE: " + listMain.size)
        println("SIZE vol: " + amountTotal)

        //return aggregateNew
    }

    /*private fun checkDecreasedQuarter(yearRecordList: List<RecordPojo>): List<RecordPojo> {
        val size: Int = yearRecordList.size

        when(size){
             1-> return yearRecordList
             2-> {
                 for (i in 0 until yearRecordList.size){
                    if(yearRecordList[i].data_volume > yearRecordList[i+1].data_volume){
                        yearRecordList[i].downQuarterName = yearRecordList[i]
                    }
                 }
             }
        }
    }*/

    private fun calculateUsageByYear(recordList: List<RecordPojo>): List<RecordPojo> {

        //region working flow
        val aggregate = recordList
            .groupingBy(RecordPojo::year)
            .aggregate { _, accumulator: RecordPojo?, element: RecordPojo, _ ->
                accumulator?.let {
                    it.copy(data_volume = it.data_volume + element.data_volume)
                } ?: element
            }


        println("AGGREGATE: " + aggregate.values.toMutableList())
        println("AGGREGATE SIZZE: " + aggregate.values.toMutableList().size)
        //endregion

        return aggregate.values.toList()
    }
}