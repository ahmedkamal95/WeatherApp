package com.fekratoday.weatherapp.screens.mainscreen.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.fekratoday.weatherapp.R
import com.fekratoday.weatherapp.model.Lists
import kotlinx.android.synthetic.main.layout_main_recycler_row.view.*
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*


class MainAdapter(private val userList: MutableList<Lists>, private val context: Context) :
    RecyclerView.Adapter<MainAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.layout_main_recycler_row, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(myHolder: MyViewHolder, position: Int) {
        myHolder.bindItems(userList[position], context)
    }

    override fun getItemCount(): Int {
        return userList.size
    }


    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        @SuppressLint("SimpleDateFormat")
        fun bindItems(lists: Lists, context: Context) {
            itemView.txtDay.text = parseDate(lists.dtTxt)
            itemView.txtDate.text =
                SimpleDateFormat("dd/MM/yyyy hh:mm a").format(SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse(lists.dtTxt))
            itemView.txtStatus.text = lists.weather[0].main
            itemView.txtBigNum.text = StringBuilder(lists.main.tempMax.toInt().toString()).append("°")
            itemView.txtSmallNum.text = StringBuilder(lists.main.tempMin.toInt().toString()).append("°")
            Glide.with(context).load(
                StringBuilder("https://openweathermap.org/img/w/")
                    .append(lists.weather[0].icon).append(".png").toString()
            ).into(itemView.img)
        }

        @SuppressLint("SimpleDateFormat")
        fun parseDate(dtTxt: String): String {
            try {
                val format = SimpleDateFormat("yyyy-MM-dd hh:mm:ss")
                val dayFormat = SimpleDateFormat("dd")
                val dayStringFormat = SimpleDateFormat("EEEE")

                val dateFormat = format.parse(dtTxt)
                return when {
                    Calendar.getInstance().get(Calendar.DAY_OF_MONTH) == dayFormat.format(dateFormat).toInt() -> "Today"
                    Calendar.getInstance().get(Calendar.DAY_OF_MONTH) + 1 == dayFormat.format(dateFormat).toInt() -> "Tomorrow"
                    else -> dayStringFormat.format(dateFormat)
                }

            } catch (e: ParseException) {
                Log.e("parseDate", "" + e.printStackTrace())
            }
            return ""
        }
    }


}