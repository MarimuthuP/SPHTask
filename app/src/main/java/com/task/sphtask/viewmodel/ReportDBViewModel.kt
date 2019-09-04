package com.task.sphtask.viewmodel

import android.app.Application
import android.os.AsyncTask
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.google.gson.Gson
import com.task.sphtask.database.UsageData
import com.task.sphtask.database.UsageDatabase
import com.task.sphtask.pojo.RecordPojo
import com.task.sphtask.pojo.TotalUsagePojo

/**
 * Created by Marimuthu on 2019-09-03.
 */
class ReportDBViewModel(application: Application) : AndroidViewModel(application) {

    private var totalDataUsageDbList: LiveData<List<UsageData>>? = null
    private val usageDb: UsageDatabase

    init {
        usageDb = UsageDatabase.getDatabase(this.getApplication())
        totalDataUsageDbList = usageDb.usageDao().getAllUsageData()
    }

    fun getUsageList(): LiveData<List<UsageData>>?{
        return totalDataUsageDbList!!
    }

    fun addUsageList(usageList: List<UsageData>) {
        addUsageListAsyncTask(usageDb).execute(usageList)
    }

    class addUsageListAsyncTask(db: UsageDatabase) : AsyncTask<List<UsageData>, Void, Void>() {
        private var usageDb = db
        override fun doInBackground(vararg params: List<UsageData>?): Void? {
            usageDb.usageDao().insertUsageData(params[0]!!)
            return null
        }
    }

    /**
     * used to make the string from object during save into db.
     */
    fun makeObjectToSaveIntoDB(listRecord: List<TotalUsagePojo>): List<UsageData> {
        val listUsageData: ArrayList<UsageData> = ArrayList()
        for (i in 0 until listRecord.size){
            val usageData = UsageData(
                listRecord.get(i).qId,
                listRecord.get(i).year.toString(),
                listRecord.get(i).totalVolume.toString(),
                listRecord.get(i).isDown,
                Gson().toJson(listRecord.get(i).usageDetails)
            )
            listUsageData.add(usageData)
        }
        return listUsageData
    }

    /**
     * used to convert the object for adapter using format
     */
    fun makeObjectToUseAdapter(listRecord: List<UsageData>): List<TotalUsagePojo> {
        val listUsageData: ArrayList<TotalUsagePojo> = ArrayList()
        for (i in 0 until listRecord.size){
            val usageData = TotalUsagePojo(
                listRecord.get(i).id,
                listRecord.get(i).dataVolume.toBigDecimal(),
                listRecord.get(i).year.toInt(),
                listRecord.get(i).isDecrease,
                createListFromString(listRecord.get(i).usageList)

            )
            listUsageData.add(usageData)
        }
        return listUsageData
    }

    /**
     * used to create the custom list from db retrieved string
     */
    private fun createListFromString(usageList: String): List<RecordPojo> {
        val listUsage: List<RecordPojo> = Gson().fromJson(usageList,Array<RecordPojo>::class.java).toList()
        return listUsage
    }
}