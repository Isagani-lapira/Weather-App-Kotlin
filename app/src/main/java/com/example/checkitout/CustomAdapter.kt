package com.example.checkitout

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class CustomAdapter(private val weatherList: List<Weather>, private val context: Context) :
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

    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.tvDay.text = weatherList[position].day
        holder.ivIcon.setImageResource(weatherList[position].Icon)
        holder.tvWeather.text = weatherList[position].weather
        holder.tvTemp.text = weatherList[position].temperature

        //set up the color of the background
        val drawable  = context.getDrawable(R.drawable.bg_forecast)
        val gradient = drawable as? GradientDrawable
        gradient?.setColor(context.getColor(weatherList[position].bgContainer))
    }


    override fun getItemCount(): Int {
        return weatherList.size
    }


}