package com.fekratoday.weatherapp.network

import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

class Webservice {
    companion object {
        fun create(): Api {
            val retrofit = Retrofit.Builder()
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(Urls.WEATHER_URL)
                .build()

            return retrofit.create(Api::class.java)
        }
    }
}