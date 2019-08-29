package com.task.sphtask.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.task.sphtask.R

/**
 * Created by Marimuthu on 2019-08-29.
 */
class ReportListActivity : AppCompatActivity(){

    private var recyclerUsageList: RecyclerView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reportlist)
        initView()
    }

    private fun initView() {
        recyclerUsageList = findViewById(R.id.rv_usage_list)

    }
}