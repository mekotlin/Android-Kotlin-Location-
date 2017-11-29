package mekotlinapps.dnyaneshwar.`in`.androidgpslocation

import android.content.Context
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.ResultCallback
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import org.jetbrains.anko.alert


class MainActivity : AppCompatActivity(), OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    var client: GoogleApiClient? = null
    var googleMap: GoogleMap? = null
    val permssoinCode: Int = 200
    val REQUEST_CHECK_SETTINGS = 201
    var gpsEnable: Boolean = false
    var locationManger: LocationManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val mapFragment = getSupportFragmentManager().findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this);
    }

    override fun onMapReady(mMap: GoogleMap?) {
        googleMap = mMap;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermssion()
        } else {
            locationManger = getSystemService(Context.LOCATION_SERVICE) as LocationManager
            gpsEnable = locationManger!!.isProviderEnabled(LocationManager.GPS_PROVIDER)
            if (gpsEnable) {
                setMarker()
            } else {
                settingsRequest()
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {

        when (requestCode) {

            permssoinCode -> if (grantResults.size > 0) {

                var locationPermissions = grantResults[0] === PackageManager.PERMISSION_GRANTED
                var locationPermissions_ = grantResults[1] === PackageManager.PERMISSION_GRANTED

                if (locationPermissions && locationPermissions_) {
                    settingsRequest()
                } else {
                    alert("Please grant permissoins to acces Device Location.") {
                        negativeButton("RETRY") { requestPermssion() }
                    }.show()
                }
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    fun requestPermssion() {
        ActivityCompat.requestPermissions(this, arrayOf("android.permission.ACCESS_COARSE_LOCATION", "android.permission.ACCESS_FINE_LOCATION"), permssoinCode);
    }

    fun setMarker() {
        val gpsTracker = GPSTracker(applicationContext)

        val longitude_ = gpsTracker.getLongitudeLoc()
        val latitude_ = gpsTracker.getLotitudeLoc()

        val myLocation = LatLng(latitude_, longitude_)
        googleMap!!.addMarker(MarkerOptions().position(myLocation).title(latitude_.toString() + " : " + longitude_.toString()))
        googleMap!!.moveCamera(CameraUpdateFactory.newLatLng(myLocation))
        googleMap!!.animateCamera(CameraUpdateFactory.newLatLngZoom(myLocation, 16F))
        googleMap!!.setMapType(GoogleMap.MAP_TYPE_NORMAL)
    }

    override fun onConnected(p0: Bundle?) {}

    override fun onConnectionSuspended(p0: Int) {}

    override fun onConnectionFailed(p0: ConnectionResult) {}

    fun settingsRequest() {

        client = null

        try {
            if (client == null) {

                client = GoogleApiClient.Builder(this@MainActivity)
                        .addApi(LocationServices.API)
                        .addConnectionCallbacks(this)
                        .addOnConnectionFailedListener(this).build()

                client!!.connect()
                val locationRequest = LocationRequest.create()

                locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                locationRequest.setInterval(0)
                locationRequest.setFastestInterval(0)

                val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)
                builder.setAlwaysShow(true)

                val result = LocationServices.SettingsApi.checkLocationSettings(client, builder.build())

                result.setResultCallback(object : ResultCallback<LocationSettingsResult> {

                    override fun onResult(locationSettingResult: LocationSettingsResult) {

                        val status = locationSettingResult.getStatus()

                        when (status.getStatusCode()) {

                            LocationSettingsStatusCodes.SUCCESS -> {
                                setMarker()
                            }

                            LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> {
                                status.startResolutionForResult(this@MainActivity, REQUEST_CHECK_SETTINGS)
                            }

                            LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> {

                            }
                        }
                    }
                })
            }
        } catch (e: Exception) {
        }
    }
}
