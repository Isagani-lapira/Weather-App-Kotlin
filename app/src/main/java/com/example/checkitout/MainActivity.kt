package com.example.checkitout

import android.app.Activity
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker.PERMISSION_GRANTED
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

class MainActivity : AppCompatActivity() {

    private val context = this
    private val activity:Activity = this
    private val LOCATION_CODE = 255
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val API_KEY = "e54694aff45fcc2cde8ee58c3045c17c"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        window.statusBarColor = getColor(R.color.yellow) //change statusbar color
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this) //for getting location
        checkPermission()
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

                    Log.d("Yornnn", "getWeather: $weather")
                }
            },
            { error ->
                Toast.makeText(context,error.message.toString(),Toast.LENGTH_LONG).show()
            })

        requestQueue.add(jsonObjReq)
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
}