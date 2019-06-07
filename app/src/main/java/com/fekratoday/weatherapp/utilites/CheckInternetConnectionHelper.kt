package com.fekratoday.weatherapp.utilites

import android.content.Context
import android.net.ConnectivityManager

class CheckInternetConnectionHelper private constructor(context: Context) {
    private var check: Boolean = false

    init {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val info = connectivityManager.activeNetworkInfo
        if (info != null && info.isConnected) {
            check = true
        }
    }

    fun checkInternet(): Boolean? {
        return check
    }

    companion object {
        private val instance: CheckInternetConnectionHelper? = null

        fun getInstance(context: Context): CheckInternetConnectionHelper {
            return CheckInternetConnectionHelper(context)
        }
    }

}