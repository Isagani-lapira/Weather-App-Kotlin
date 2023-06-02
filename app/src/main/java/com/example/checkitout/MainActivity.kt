package com.example.checkitout

import android.app.Activity
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker.PERMISSION_GRANTED
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

class MainActivity : AppCompatActivity() {

    private val context = this
    private val activity:Activity = this
    private val LOCATION_REQUEST_CODE = 255
    private lateinit var fusedLocationClient: FusedLocationProviderClient
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

                        val locs = "location is x: $latitude and longitude $longitude"
                        Toast.makeText(context,locs,Toast.LENGTH_LONG).show()
                    }
                }
        }else{
            //request permission
            requestLocPermission()
        }
    }

    private fun requestLocPermission() {
        ActivityCompat.requestPermissions(activity,
            arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),LOCATION_REQUEST_CODE)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        //for location request result permission
        if(requestCode==LOCATION_REQUEST_CODE){
            if(grantResults[0]==PERMISSION_GRANTED){
                Toast.makeText(context,"Permission granted",Toast.LENGTH_LONG).show()
            }
            else Toast.makeText(context,"Permission denied",Toast.LENGTH_LONG).show()

        }
    }
}