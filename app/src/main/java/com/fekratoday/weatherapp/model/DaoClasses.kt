package com.fekratoday.weatherapp.model

import androidx.room.Entity
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

@Entity(tableName = "weather_data")
data class WeatherData(
    @Expose
    @SerializedName("cod")
    val cod: String,
    @Expose
    @SerializedName("message")
    val message: Double,
    @Expose
    @SerializedName("cnt")
    var cnt: Int,
    @Expose
    @SerializedName("list")
    var list: List<Lists>,
    @Expose
    @SerializedName("city")
    var city: City
)

data class City(
    @Expose
    @SerializedName("id")
    var id: Int,
    @Expose
    @SerializedName("name")
    val name: String,
    @Expose
    @SerializedName("coord")
    var coord: Coord,
    @Expose
    @SerializedName("country")
    val country: String,
    @Expose
    @SerializedName("timezone")
    var timezone: Int
)

data class Coord(
    @Expose
    @SerializedName("lat")
    val lat: Double,
    @Expose
    @SerializedName("lon")
    val lon: Double
)

data class Lists(
    @Expose
    @SerializedName("dt")
    var dt: Int,
    @Expose
    @SerializedName("main")
    var main: Main,
    @Expose
    @SerializedName("weather")
    var weather: List<Weather>,
    @Expose
    @SerializedName("clouds")
    var clouds: Clouds,
    @Expose
    @SerializedName("wind")
    var wind: Wind,
    @Expose
    @SerializedName("sys")
    var sys: Sys,
    @Expose
    @SerializedName("dt_txt")
    val dtTxt: String,
    @Expose
    @SerializedName("name")
    val name: String
)

data class Sys(
    @Expose
    @SerializedName("pod")
    val pod: String,
    @Expose
    @SerializedName("country")
    val country: String
)

data class Wind(
    @Expose
    @SerializedName("speed")
    val speed: Double,
    @Expose
    @SerializedName("deg")
    val deg: Double,
    @Expose
    @SerializedName("country")
    val country: String
)

data class Clouds(
    @Expose
    @SerializedName("all")
    var all: Int
)

data class Weather(
    @Expose
    @SerializedName("id")
    var id: Int,
    @Expose
    @SerializedName("main")
    val main: String,
    @Expose
    @SerializedName("description")
    val description: String,
    @Expose
    @SerializedName("icon")
    val icon: String
)

data class Main(
    @Expose
    @SerializedName("temp")
    val temp: Double,
    @Expose
    @SerializedName("temp_min")
    val tempMin: Double,
    @Expose
    @SerializedName("temp_max")
    val tempMax: Double,
    @Expose
    @SerializedName("pressure")
    val pressure: Double,
    @Expose
    @SerializedName("sea_level")
    val seaLevel: Double,
    @Expose
    @SerializedName("grnd_level")
    val grndLevel: Double,
    @Expose
    @SerializedName("humidity")
    var humidity: Int,
    @Expose
    @SerializedName("temp_kf")
    var tempKf: Double
)