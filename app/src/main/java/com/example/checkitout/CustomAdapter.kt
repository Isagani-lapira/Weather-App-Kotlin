package com.example.checkitout

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class CustomAdapter(private val weatherList: List<Weather>) :
    RecyclerView.Adapter<CustomAdapter.ViewHolder>() {

    //custom viewholder
    class ViewHolder(view:View) : RecyclerView.ViewHolder(view) {
        //reference the every view in the forecast layout
        val tvDay : TextView = view.findViewById(R.id.tvDay)
        val ivIcon : ImageView = view.findViewById(R.id.ivIcon)
        val tvWeather : TextView = view.findViewById(R.id.tvWeather)
        val tvTemp : TextView = view.findViewById(R.id.tvTemp)
    }

    //assign the layout will be using
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.forecast_layout,
            parent,false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.tvDay.text = weatherList[position].day
        holder.ivIcon.setImageResource(weatherList[position].Icon)
        holder.tvWeather.text = weatherList[position].weather
        holder.tvTemp.text = weatherList[position].temperature

    }


    override fun getItemCount(): Int {
        return weatherList.size
    }


}