package com.task.sphtask.listener

import com.task.sphtask.pojo.TotalUsagePojo

/**
 * Created by Marimuthu on 2019-09-02.
 */
interface ICommunicator {
    fun onImageClick(usageDetails: TotalUsagePojo)
}