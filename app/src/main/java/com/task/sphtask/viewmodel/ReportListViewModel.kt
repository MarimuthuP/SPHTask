package com.task.sphtask.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
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
import java.math.BigInteger
import java.util.concurrent.TimeUnit



/**
 * Created by Marimuthu on 2019-08-29.
 */
class ReportListViewModel(application: Application) : AndroidViewModel(application) {
    private var totalDataUsageList: MutableLiveData<List<TotalUsagePojo>>? = null

    fun getUsageMobileDataList(): MutableLiveData<List<TotalUsagePojo>>? {
        if (totalDataUsageList == null) {
            totalDataUsageList = MutableLiveData()
            loadDataUsageResponse()
        }
        return totalDataUsageList
    }

    /**
     * which is used to retrieve the response from backend api.
     */
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
                val gson = Gson()
                val dataUsagePojo = gson.fromJson(response.body(), DataUsagePojo::class.java)
                loadUsageResponseIntoList(dataUsagePojo.result.records)
            }

            override fun onFailure(call: Call<JsonElement>, t: Throwable) {
                totalDataUsageList?.value = emptyList()
            }
        })
    }

    /**
     * used to parse the retrieved response.
     */
    private fun loadUsageResponseIntoList(recordList: List<DataUsagePojo.Result.Record>) {
        val listRecord: ArrayList<RecordPojo> = ArrayList()

        var delimiter = "-"
        for (index in recordList) {
            val yearnQuarter = index.quarter.split(delimiter)
            val recordPojo = RecordPojo(
                index.id,
                index.volumeOfMobileData.toBigDecimal(),
                yearnQuarter.get(0).toInt(),
                yearnQuarter.get(1),
                false,
                yearnQuarter.get(1)
            )
            listRecord.add(recordPojo)
        }
        totalDataUsageList?.value = calculateUsageByYear(listRecord)
    }

    /**
     * which helps to group the list by the year and total usage
     */
    private fun calculateUsageByYear(recordList: List<RecordPojo>): List<TotalUsagePojo> {

        val listTotalUsage = ArrayList<TotalUsagePojo>()

        //region isolate the list by year...
        val aggregateNew = recordList
            .groupBy { it.year }
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

        val listResult = aggregate.toList()
        val listDetailResult = aggregateNew.toList()

        for (i in 0 until listResult.size) {
            val totalUsagePojo = TotalUsagePojo(
                listResult.get(i).second.qId,
                listResult.get(i).second.data_volume,
                listResult.get(i).second.year,
                isQuarterDownInYear(listDetailResult.get(i).second),
                findDecreasedQuarterInYear(listDetailResult.get(i).second)
            )
            listTotalUsage.add(totalUsagePojo)
        }

        return listTotalUsage
    }

    /**
     * which helps to comparing the values of quarter and make the list
     */
    private fun findDecreasedQuarterInYear(yearRecordList: List<RecordPojo>): List<RecordPojo> {

        val yearRecordArraylist: ArrayList<RecordPojo> = ArrayList()
        var prevVal = BigDecimal(BigInteger.ZERO)
        var isDecreased: Boolean

        for (i in 0 until yearRecordList.size) {
            if (prevVal < yearRecordList[i].data_volume) {
                prevVal = yearRecordList[i].data_volume
                isDecreased = false
            } else {
                prevVal = yearRecordList[i].data_volume
                isDecreased = true
            }

            val recordPojo =
                RecordPojo(
                    yearRecordList[i].qId,
                    yearRecordList[i].data_volume,
                    yearRecordList[i].year,
                    yearRecordList[i].quarter,
                    isDecreased,
                    yearRecordList[i].quarter
                )
            yearRecordArraylist.add(recordPojo)
        }
        return yearRecordArraylist
    }

    /**
     *  used to finding the decreased quarter in the year.
     */
    private fun isQuarterDownInYear(usageDetails: List<RecordPojo>): Boolean {
        val size = usageDetails.size
        var prevVal = BigDecimal(BigInteger.ZERO)

        for (i in 0 until size) {
            if (prevVal < usageDetails[i].data_volume) {
                prevVal = usageDetails[i].data_volume
            } else {
                return true
            }
        }
        return false
    }

}