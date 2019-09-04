package com.task.sphtask.view

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.task.sphtask.R

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initView()
    }

    private fun initView() {
        val buttonReport: Button = findViewById(R.id.btn_report)
        setTitle(R.string.app_name)
        buttonReport.setOnClickListener {
            startActivity(Intent(this, ReportListActivity::class.java))
        }
    }
}
