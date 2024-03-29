package com.app.olxapplication.ui.splash

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.app.olxapplication.BaseActivity
import com.app.olxapplication.MainActivity
import com.app.olxapplication.R
import com.app.olxapplication.ui.login.LoginActivity
import com.app.olxapplication.utilities.Constants
import com.app.olxapplication.utilities.SharedPref
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import java.io.IOException
import java.util.*

class SplashActivity : BaseActivity() {

    private val MY_PERMISSIONS_REQUEST_LOCATION= 100
    private val REQUEST_GPS=101
    private var loctionRequest: LocationRequest? = null
    private lateinit var fusedLocationclient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        askforPermissions()

        fusedLocationclient = LocationServices.getFusedLocationProviderClient(this)

        getLocationCallback()
    }


    override fun onResume() {
        super.onResume()

    }

    private fun askforPermissions() {
        val permissions = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)
        ActivityCompat.requestPermissions( this, permissions,MY_PERMISSIONS_REQUEST_LOCATION )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode==MY_PERMISSIONS_REQUEST_LOCATION){
            var granted = false
            for (grantResult in grantResults){
                if (grantResult == PackageManager.PERMISSION_GRANTED){
                    granted=true
                }
            }
            if (granted)
                enableGps()

        }
    }

    private fun enableGps() {
        loctionRequest = LocationRequest.create()
        loctionRequest?.setInterval(3000)
        loctionRequest?.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
        val builder = LocationSettingsRequest.Builder()
        builder.addLocationRequest(loctionRequest!!)

        val task = LocationServices.getSettingsClient(this).checkLocationSettings(builder.build())

        task.addOnCompleteListener(object : OnCompleteListener<LocationSettingsResponse>{
            override fun onComplete(p0: Task<LocationSettingsResponse>) {

                try {
                    val
                            response= task.getResult(ApiException::class.java)
                    startLocationUpdates()
                }catch (exception:ApiException){
                   when(exception.statusCode){

                       LocationSettingsStatusCodes.RESOLUTION_REQUIRED->{
                           val resolvable = exception as ResolvableApiException
                           resolvable.startResolutionForResult(this@SplashActivity,REQUEST_GPS)

                       }

                   }
                }
            }

        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode==REQUEST_GPS){
            startLocationUpdates()
        }
    }

    private fun startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        fusedLocationclient.requestLocationUpdates(loctionRequest,locationCallback,null)
    }

    private fun getLocationCallback() {
    locationCallback = object :LocationCallback(){
        override fun onLocationResult(p0: LocationResult?) {
            super.onLocationResult(p0)
            val location = p0?.lastLocation

            SharedPref(this@SplashActivity).setString(Constants.CITY_NAME,getCityName(location))
            if (SharedPref(this@SplashActivity).getString(Constants.USER_ID)?.isEmpty()!!) {
                Toast.makeText(this@SplashActivity, getCityName(location!!), Toast.LENGTH_LONG)
                    .show()
                startActivity(Intent(this@SplashActivity, LoginActivity::class.java))
                finish()
            }else{
                startActivity(Intent(this@SplashActivity, MainActivity::class.java))
                finish()
            }
        }
    }
    }

    override fun onDestroy() {
        super.onDestroy()
        fusedLocationclient.removeLocationUpdates(locationCallback)
    }

    private fun getCityName(location: Location?): String {
        var cityName=""
        val geocoder = Geocoder(this, Locale.getDefault())
        try {
            val address = geocoder.getFromLocation(location!!.latitude,location.longitude,1)
            cityName = address[0].locality
        }catch (e:IOException){
            when{
                e.message == "grpc failed" -> {/* display a Toast or Snackbar instead*/ }
                else -> throw e
            }

        }

        return cityName
    }

}
