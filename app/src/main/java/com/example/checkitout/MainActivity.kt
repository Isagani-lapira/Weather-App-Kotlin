package com.example.checkitout

import android.app.Activity
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker.PERMISSION_GRANTED
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.squareup.picasso.Picasso
import java.time.LocalDate

class MainActivity : AppCompatActivity() {

    private val context = this
    private val activity:Activity = this
    private val LOCATION_CODE = 255
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val API_KEY = "e54694aff45fcc2cde8ee58c3045c17c"
    private lateinit var ivWeatherIcon:ImageView
    private lateinit var ivDescrip:ImageView
    private lateinit var tvWeather: TextView
    private lateinit var parentLL:LinearLayout
    private lateinit var tvCity: TextView
    private lateinit var tvTemp: TextView
    private lateinit var tvDescription: TextView
    private lateinit var tvHumidity: TextView
    private lateinit var tvWind: TextView
    private lateinit var tvDescriptionLabel: TextView
    private lateinit var tvHumidityLabel: TextView
    private lateinit var tvWindLabel: TextView
    private lateinit var tvState: TextView
    private lateinit var tvWeeklyForeCast: TextView
    private lateinit var etSearch: TextInputEditText
    private val list = ArrayList<Weather>()
    private var dayIterator:Int = 0
    private lateinit var tfSearch: TextInputLayout
    private var cityLat = 0.0
    private var cityLong = 0.0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this) //for getting location
        initialize()
        checkPermission()
    }

    fun initialize(){
        parentLL = findViewById(R.id.parentLL)
        ivWeatherIcon = findViewById(R.id.ivWeatherIcon)
        ivDescrip = findViewById(R.id.ivDescrip)
        tvWeather = findViewById(R.id.tvWeather)
        tvCity = findViewById(R.id.tvCity)
        tvTemp = findViewById(R.id.tvTemp)
        tvDescription = findViewById(R.id.tvDescript)
        tvHumidity = findViewById(R.id.tvHumidity)
        tvWind = findViewById(R.id.tvWind)
        tvDescriptionLabel = findViewById(R.id.tvDescriptLabel)
        tvHumidityLabel = findViewById(R.id.tvHumidityLabel)
        tvWindLabel = findViewById(R.id.tvWindLabel)
        tvState = findViewById(R.id.tvState)
        tvWeeklyForeCast = findViewById(R.id.tvWeeklyForeCast)
        tfSearch = findViewById(R.id.tfSearch)
        etSearch = findViewById(R.id.etSearch)

        tfSearch.setEndIconOnClickListener{
            searchPlace()
        }
    }

    private fun searchPlace() {
        val place = etSearch.text.toString()
        getLatLongtitude(place)
    }

    //check first if the permission is already granted or not
    private fun checkPermission(){
        val permission = ContextCompat.checkSelfPermission(context,
        android.Manifest.permission.ACCESS_FINE_LOCATION)

        //check if the location permission is granted
        if(permission==PackageManager.PERMISSION_GRANTED){
            //get the location
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location:Location?->
                    location?.let {
                        //get the current location using latitude and longitude
                        val latitude = location.latitude
                        val longitude = location.longitude
                        getWeather(latitude,longitude)
                        getCityName(latitude, longitude)
                    }
                }
        }else{
            //request permission
            requestLocPermission()
        }
    }

    private fun getWeather(latitude: Double, longitude: Double) {
        val requestQueue:RequestQueue = Volley.newRequestQueue(context)
        requestQueue.start()

        val url = "https://api.openweathermap.org/data/2.5/forecast?lat=$latitude&lon=$longitude&appid=$API_KEY"
        val jsonObjReq = JsonObjectRequest(Request.Method.GET,url,null,
            { response ->

                val forecastList = response.getJSONArray("list") //array of data
                // Iterate through forecast data
                for (i in 0 until forecastList.length() step 8) {
                    val forecast = forecastList.getJSONObject(i)
                    val weatherArray = forecast.getJSONArray("weather")
                    val weather = weatherArray.getJSONObject(0).getString("main")
                    val weatherIcon = weatherArray.getJSONObject(0).getString("icon")
                    val temp = forecast.getJSONObject("main").getString("temp")
                    val description = weatherArray.getJSONObject(0).getString("description")
                    val humidity = forecast.getJSONObject("main").getString("humidity")+"%"
                    val wind = forecast.getJSONObject("wind").getString("speed")+"km/h"

                    if(i==0) changeWeather(weather,temp,description,humidity,wind,weatherIcon,true)
                    else changeWeather(weather,temp,description,humidity,wind,weatherIcon,false)

                }
                setUpRecyler(list)
            },
            { error ->
                Toast.makeText(context,error.message.toString(),Toast.LENGTH_LONG).show()
            })

        requestQueue.add(jsonObjReq)
    }

    /*
    	Thunderstorm
        Drizzle
        Rain
        Snow
        Mist
        Clear
     */
    private fun changeWeather(
        weather:String,
        temp:String,
        description:String,
        humidity:String,
        wind:String,
        icon:String,
        todayForecast:Boolean
    ) {
        tvWeather.text = weather

        val weatherIcon = when(weather){
            "Thunderstorm" -> R.drawable.thunder
            "Rain" -> R.drawable.rain
            "Clear"-> R.drawable.sunny
            else -> R.drawable.sun_rain
        }

        //change background
        val bgWeather = when(weather){
            "Thunderstorm" -> R.drawable.thunderbg
            "Rain" -> R.drawable.rainybg
            "Clear"-> R.drawable.clearbg
            else -> R.drawable.cloudybg
        }


        //for status bar
        val themeColor = when(weather){
            "Thunderstorm" -> R.color.thunder
            "Rain" -> R.color.rainy
            "Clear"-> R.color.clear
            else -> R.color.cloudy
        }

        val txtColor = if(weather == "Clear") R.color.icons else R.color.white

        //modify values of the views
        window.statusBarColor = getColor(themeColor) //change statusbar color
        ivWeatherIcon.setImageResource(weatherIcon)
        parentLL.setBackgroundResource(bgWeather)
        val celsius = "${kelvinToCelsius(temp)}°c"
        tvTemp.text = celsius
        tvDescription.text = description
        tvHumidity.text = humidity
        tvWind.text = wind

        changeColor(txtColor)
        changeWeatherIcon(icon)

        val dayOfWeek = getDay()
        if(!todayForecast)
            list.add(Weather(dayOfWeek,weatherIcon,weather,celsius,themeColor))
    }

    //provide the day for next 5days
    private fun getDay():String {
        val localDate = LocalDate.now()
        val dayofWeek = (localDate.dayOfWeek+ dayIterator.toLong())-1

        dayIterator++
        return dayofWeek.toString()

    }

    private fun changeColor(color:Int) {
        var TVArray = arrayOf<TextView>()

        TVArray+=tvCity
        TVArray+=tvTemp
        TVArray+=tvDescription
        TVArray+=tvHumidity
        TVArray+=tvWind
        TVArray+=tvDescriptionLabel
        TVArray+=tvHumidityLabel
        TVArray+=tvWindLabel
        TVArray+=tvState
        TVArray+=tvWeeklyForeCast

        //set the color of all textview depending on the theme
        TVArray.forEach { textView ->
            textView.setTextColor(getColor(color))
        }
    }

    private fun changeWeatherIcon(icon:String) {
        val urlIcon = "https://openweathermap.org/img/wn/$icon@2x.png"

        //get the icon from the url
        Picasso.get()
            .load(urlIcon)
            .into(ivDescrip)
    }

    private fun kelvinToCelsius(temp: String): String {
        val kelvin = temp.toDouble()
        //conversion to celsius
        return (kelvin - 273.15).toString().substring(0, 2)
    }

    private fun requestLocPermission() {
        ActivityCompat.requestPermissions(activity,
            arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),LOCATION_CODE)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        //for location request result permission
        if(requestCode==LOCATION_CODE){
            if(grantResults[0]==PERMISSION_GRANTED){
                Toast.makeText(context,"Permission granted",Toast.LENGTH_LONG).show()
            }
            else Toast.makeText(context,"Permission denied",Toast.LENGTH_LONG).show()

        }
    }


    fun getCityName(latitude: Double, longitude: Double) {
        val url = "https://nominatim.openstreetmap.org/reverse?format=json&lat=$latitude&lon=$longitude"

        val requestQueue = Volley.newRequestQueue(context)
        requestQueue.start()

        //get json response
        val jsonObj = JsonObjectRequest(Request.Method.GET,url,null,
            {response->
                val address = response.getJSONObject("address")
                val city = address.getString("city")
                val state = address.getString("state")

                tvState.text = state
                tvCity.text = city
            },{error->})

        requestQueue.add(jsonObj)

    }

    private fun setUpRecyler(weatherList : ArrayList<Weather>){
        val recyler:RecyclerView = findViewById(R.id.rvForecast)
        val linearLayout = LinearLayoutManager(context,LinearLayoutManager.HORIZONTAL,false)

        recyler.layoutManager = linearLayout
        val adapter = CustomAdapter(weatherList,context)
        recyler.adapter = adapter
    }


    private fun getLatLongtitude(place: String) {
        val api_key = "ff8899a8f2a14c27adc813402bb49bee"
        val url = "https://api.opencagedata.com/geocode/v1/json?q=$place&key=$api_key"
        val requestQueue = Volley.newRequestQueue(context)
        requestQueue.start()
        Log.d("rorror", "getLatLongtitude: $place")
        val JsonObjectRequest = JsonObjectRequest(Request.Method.GET, url, null,
            { response ->
                val result = response.getJSONArray("results").getJSONObject(0)
                val geometry = result.getJSONObject("geometry")
                val lat = geometry.getString("lat")
                val long = geometry.getString("lng")

                cityLat = lat.toDouble()
                cityLong = long.toDouble()

                list.clear()
                getWeather(cityLat, cityLong)
            }, { error -> }
        )

        requestQueue.add(JsonObjectRequest)
    }
}