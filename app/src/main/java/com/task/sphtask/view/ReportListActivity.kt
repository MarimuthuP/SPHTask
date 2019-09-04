package com.task.sphtask.view

import android.os.Bundle
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.Group
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.task.sphtask.R
import com.task.sphtask.adapter.DataUsageAdapter
import com.task.sphtask.database.UsageData
import com.task.sphtask.dialog.CommonDialogFragment
import com.task.sphtask.listener.ICommunicator
import com.task.sphtask.pojo.TotalUsagePojo
import com.task.sphtask.utils.CommonUtils
import com.task.sphtask.viewmodel.ReportDBViewModel
import com.task.sphtask.viewmodel.ReportListViewModel

/**
 * Created by Marimuthu on 2019-08-29.
 */
class ReportListActivity : AppCompatActivity(), ICommunicator {

    private var recyclerUsageList: RecyclerView? = null
    private var dataUsageAdapter: DataUsageAdapter? = null
    private var progressBar: ProgressBar? = null
    private var groupNoData: Group? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reportlist)
        initView()
    }

    private fun initView() {
        recyclerUsageList = findViewById(R.id.rv_usage_list)
        progressBar = findViewById(R.id.progressBar)
        groupNoData = findViewById(R.id.group)

        val reportListViewModel = ViewModelProviders.of(this).get(ReportListViewModel::class.java)
        val reportListDBViewModel = ViewModelProviders.of(this).get(ReportDBViewModel::class.java)

        val layoutManager = LinearLayoutManager(this)
        recyclerUsageList?.layoutManager = layoutManager
        recyclerUsageList?.itemAnimator = DefaultItemAnimator()

        if (CommonUtils.verifyAvailableNetwork(this)) {
            //region fetch data over network
            title = getString(R.string.title_list)
            reportListViewModel.getUsageMobileDataList()?.observe(this, Observer { listRecord: List<TotalUsagePojo> ->
                listRecord.let {
                    if (listRecord.isEmpty()) {
                        title = getString(R.string.no_internet_title)
                        progressBar!!.isVisible = false
                        groupNoData!!.isVisible = true
                    } else {
                        reportListDBViewModel.addUsageList(reportListDBViewModel.makeObjectToSaveIntoDB(listRecord))
                        dataUsageAdapter = DataUsageAdapter(listRecord,this)
                        recyclerUsageList?.adapter = dataUsageAdapter
                        progressBar!!.isVisible = false
                    }
                }
            })
            //endregion
        } else {
            //region fetch from local db if found any
            reportListDBViewModel.getUsageList()?.observe(this, Observer { listRecord: List<UsageData> ->
                listRecord.let {
                    if (listRecord.isEmpty()) {
                        title = getString(R.string.no_internet_title)
                        progressBar!!.isVisible = false
                        groupNoData!!.isVisible = true
                        noDataFound()

                    } else {
                        title = getString(R.string.title_db_list)
                        dataUsageAdapter = DataUsageAdapter(reportListDBViewModel.makeObjectToUseAdapter(listRecord),this)
                        recyclerUsageList?.adapter = dataUsageAdapter
                        progressBar!!.isVisible = false
                        //Toast.makeText(this,R.string.no_internet_data,Toast.LENGTH_LONG).show()

                        val ft = supportFragmentManager.beginTransaction()
                        val newFragment = CommonDialogFragment.newInstance(
                            getString(R.string.no_internet_title),
                            getString(R.string.no_internet_data)
                        )
                        newFragment.show(ft, "dialog")
                    }
                }
            })
            //endregion
        }
    }

    /**
     * This method used to hide the list and show appropriate views when no internet.
     */
    private fun noDataFound() {
        //region when no internet access is found
        setTitle(R.string.no_internet_title)
        progressBar!!.isVisible = false
        groupNoData!!.isVisible = true

        val ft = supportFragmentManager.beginTransaction()
        val newFragment = CommonDialogFragment.newInstance(
            getString(R.string.no_internet_title),
            getString(R.string.no_internet_data)
        )
        newFragment.show(ft, "dialog")
        //endregion
    }

    override fun onImageClick(usageDetails: TotalUsagePojo) {
        val ft = supportFragmentManager.beginTransaction()
        val newFragment = CommonDialogFragment.newInstance(
            getString(R.string.volume_title, usageDetails.year.toString()),
            usageDetails
        )
        newFragment.show(ft, "dialog")
    }
}