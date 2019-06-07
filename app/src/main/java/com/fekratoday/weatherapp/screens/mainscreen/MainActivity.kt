package com.fekratoday.weatherapp.screens.mainscreen

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.fekratoday.weatherapp.R
import com.fekratoday.weatherapp.model.Lists
import com.fekratoday.weatherapp.network.Urls
import com.fekratoday.weatherapp.network.Webservice
import com.fekratoday.weatherapp.screens.mainscreen.adapters.MainAdapter
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.LocationSettingsStatusCodes
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import java.text.SimpleDateFormat
import java.util.*


private const val PERMISSION_REQUEST = 10

class MainActivity : AppCompatActivity() {

    private val lists: MutableList<Lists> = mutableListOf()
    private var adapter: MainAdapter? = null
    private lateinit var locationManager: LocationManager
    private var hasGps = false
    private var locationGps: Location? = null
    private var lat: Double? = null
    private var lon: Double? = null
    private var permissions =
        arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setToolbar()

        adapter = MainAdapter(lists, this)
        recycler.layoutManager = LinearLayoutManager(this)
        recycler.adapter = adapter

    }

    override fun onResume() {
        super.onResume()
        checkPermission()
    }

    private fun setToolbar() {
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setIcon(R.mipmap.ic_launcher)
    }

    @SuppressLint("CheckResult", "SimpleDateFormat", "SetTextI18n")
    private fun getWeatherData() {
        Webservice.create()
            .getWeather(
                lat = this.lat!!, lon = this.lon!!,
                appid = Urls.WEATHER_API_KEY, units = "metric"
            )
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe({ result ->
                result.list.forEach { list: Lists ->
                    lists.add(list)
                }
                progressRecycler.visibility = View.GONE
                adapter?.notifyDataSetChanged()
            }, { error ->
                Log.e("getWeather Error", "" + error.message)
            })

        Webservice.create()
            .getCurrentWeather(
                lat = this.lat!!, lon = this.lon!!,
                appid = Urls.WEATHER_API_KEY, units = "metric"
            )
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe({ result ->
                val dayFormat = SimpleDateFormat("MMMM dd")
                txtDate.text = "Today, " + dayFormat.format(Calendar.getInstance().time)
                txtBigNum.text = StringBuilder(result.main.tempMax.toInt().toString()).append("°")
                txtSmallNum.text = StringBuilder(result.main.tempMin.toInt().toString()).append("°")
                txtStatus.text = result.weather[0].main
                txtCountry.text = result.name + ", " + result.sys.country
                Glide.with(this).load(
                    StringBuilder("https://openweathermap.org/img/w/")
                        .append(result.weather[0].icon).append(".png").toString()
                ).into(imgView)
                progressToday.visibility = View.GONE
                group.visibility = View.VISIBLE
            }, { error ->
                Log.e("getCurrentWeather Error", "" + error.message)
            })
    }

    @SuppressLint("MissingPermission")
    private fun getLocation() {
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        hasGps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        if (hasGps) {
            Log.d("Location", "hasGps")
            val localGpsLocation = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER)
            if (localGpsLocation != null) {
                locationGps = localGpsLocation
                lat = localGpsLocation.latitude
                lon = localGpsLocation.longitude
                Log.d("Location", " GPS Latitude : " + localGpsLocation.latitude)
                Log.d("Location", " GPS Longitude : " + localGpsLocation.longitude)
                getWeatherData()
            }
        } else {
//            startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
            displayLocationSettingsRequest()
        }
    }

    private fun checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            for (i in permissions.indices) {
                if (checkCallingOrSelfPermission(permissions[i]) == PackageManager.PERMISSION_DENIED) {
                    requestPermissions(permissions, PERMISSION_REQUEST)
                } else {
                    getLocation()
                }
            }
        } else {
            getLocation()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST) {
            var isSuccess = true
            for (i in permissions.indices) {
                if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                    isSuccess = false
                    val requestAgain =
                        Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && shouldShowRequestPermissionRationale(
                            permissions[i]
                        )
                    if (requestAgain) {
                        Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, "Go to settings and enable the permission", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            if (isSuccess) {
                getLocation()
            }
        }
    }

    private fun displayLocationSettingsRequest() {
        val googleApiClient = GoogleApiClient.Builder(this)
            .addApi(LocationServices.API).build()
        googleApiClient.connect()

        val locationRequest = LocationRequest.create()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.interval = 10000
        locationRequest.fastestInterval = (10000 / 2).toLong()

        val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)
        builder.setAlwaysShow(true)

        val result = LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build())
        result.setResultCallback {
            val status = it.status
            when (it.status.statusCode) {
                LocationSettingsStatusCodes.SUCCESS -> Log.i(
                    "displayLocationSettings",
                    "All location settings are satisfied."
                )
                LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> {
                    Log.i(
                        "displayLocationSettings",
                        "Location settings are not satisfied. Show the user a dialog to upgrade location settings "
                    )

                    try {
                        // Show the dialog by calling startResolutionForResult(), and check the result
                        // in onActivityResult().
                        status.startResolutionForResult(this@MainActivity, PERMISSION_REQUEST)
                    } catch (e: IntentSender.SendIntentException) {
                        Log.i("displayLocationSettings", "PendingIntent unable to execute request.")
                    }

                }
                LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> Log.i(
                    "displayLocationSettings",
                    "Location settings are inadequate, and cannot be fixed here. Dialog not created."
                )
            }
        }
    }

}
