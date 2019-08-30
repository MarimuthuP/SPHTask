package com.task.sphtask.view

import android.os.Bundle
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.task.sphtask.R
import com.task.sphtask.adapter.DataUsageAdapter
import com.task.sphtask.model.ReportListViewModel
import com.task.sphtask.pojo.RecordPojo

/**
 * Created by Marimuthu on 2019-08-29.
 */
class ReportListActivity : AppCompatActivity(){

    private var recyclerUsageList: RecyclerView? = null
    private var dataUsageAdapter: DataUsageAdapter? = null
    private var progressBar: ProgressBar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reportlist)
        initView()
    }

    private fun initView() {
        recyclerUsageList = findViewById(R.id.rv_usage_list)
        progressBar = findViewById(R.id.progressBar)

        val reportListViewModel = ViewModelProviders.of(this).get(ReportListViewModel::class.java)

        val layoutManager = LinearLayoutManager(this)
        recyclerUsageList?.layoutManager = layoutManager
        recyclerUsageList?.itemAnimator = DefaultItemAnimator()
        //recyclerUsageList?.addItemDecoration(DividerItemDecoration(this,LinearLayoutManager.VERTICAL))

        /*reportListViewModel.getDataUsageDetails()?.observe(this, Observer { listRecord: DataUsagePojo ->
            println("response: ${listRecord.result}")
        })*/

        reportListViewModel.getUsageDataList()?.observe(this, Observer { listRecord: List<RecordPojo> ->
            //println("response: ${listRecord.result}")
            listRecord.let {
                dataUsageAdapter = DataUsageAdapter(listRecord)
                recyclerUsageList?.adapter = dataUsageAdapter
                progressBar!!.isVisible = false
            }
        })
    }
}