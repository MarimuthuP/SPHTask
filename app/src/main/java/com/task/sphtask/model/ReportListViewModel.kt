package com.task.sphtask.model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
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
class ReportListViewModel : ViewModel(){
    private var dataUsageResponse: MutableLiveData<DataUsagePojo>? = null
    private var dataUsageList: MutableLiveData<List<RecordPojo>>? = null

    fun getDataUsageDetails(): MutableLiveData<DataUsagePojo>?{
        if(dataUsageResponse==null) {
            dataUsageResponse = MutableLiveData()
            loadDataUsageResponse()
        }
        return dataUsageResponse
    }

    private fun loadDataUsageResponse() {
        val okHttpClient = OkHttpClient.Builder()
            .readTimeout(90,TimeUnit.SECONDS)
            .connectTimeout(90, TimeUnit.SECONDS)
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl(WebUrls.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val dataUsageUrlApi = retrofit.create(RetrofitAPI::class.java)

        val callDataUsage : Call<DataUsagePojo> = dataUsageUrlApi.getDataUsage()

        callDataUsage.enqueue(object : Callback<DataUsagePojo>{
            override fun onResponse(call: Call<DataUsagePojo>, response: Response<DataUsagePojo>) {
                println("datausage response: "+response.body())
                var gson = Gson()
                dataUsageResponse?.value = gson.fromJson(response.body().toString(),DataUsagePojo::class.java)
            }

            override fun onFailure(call: Call<DataUsagePojo>, t: Throwable) {

            }

        })
    }

}