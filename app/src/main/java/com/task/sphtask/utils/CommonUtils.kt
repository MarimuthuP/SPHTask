package com.task.sphtask.utils

import android.content.Context
import android.net.ConnectivityManager

/**
 * Created by Marimuthu on 2019-08-30.
 */
class CommonUtils {
    companion object {
        fun verifyAvailableNetwork(activity: Context): Boolean {
            val connectivityManager = activity.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val networkInfo = connectivityManager.activeNetworkInfo
            return networkInfo != null && networkInfo.isConnected
        }
    }
}