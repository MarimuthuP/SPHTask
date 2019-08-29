package com.task.sphtask.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.task.sphtask.R
import com.task.sphtask.pojo.RecordPojo

/**
 * Created by Marimuthu on 2019-08-29.
 */
class DataUsageAdapter(
    private val dataUsageList: List<RecordPojo>
) : RecyclerView.Adapter<DataUsageAdapter.DataUsageViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataUsageViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val itemView = inflater.inflate(R.layout.row_usage_layout, parent, false)
        return DataUsageViewHolder(itemView)
    }

    override fun getItemCount(): Int = dataUsageList.size

    override fun onBindViewHolder(holder: DataUsageViewHolder, position: Int) {
        val usageInfo = dataUsageList.get(position)
        holder.bindDataUsage(usageInfo)
    }


    class DataUsageViewHolder(row: View) : RecyclerView.ViewHolder(row) {
        var imageViewLogo: ImageView? = null
        var usageYear: TextView? = null
        var usageTotal: TextView? = null

        init {
            imageViewLogo = row.findViewById(R.id.iv_note_pic)
            usageYear = row.findViewById(R.id.tv_note_title)
            usageTotal = row.findViewById(R.id.tv_note_content)
        }

        fun bindDataUsage(
            dataUsageDetails: RecordPojo
        ) {

            usageYear!!.text = dataUsageDetails!!.year
            usageTotal!!.text = dataUsageDetails!!.data_volume

            imageViewLogo!!.isVisible = false

            imageViewLogo!!.setOnClickListener {

            }
        }
    }
}