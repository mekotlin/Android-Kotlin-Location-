package mekotlinapps.dnyaneshwar.`in`.androidgpslocation

import android.annotation.SuppressLint
import android.app.Service
import android.content.Context
import android.content.Intent
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.os.IBinder
import org.jetbrains.anko.toast

/**
 * Created by Dnyaneshwar Dalvi on 29/11/17.
 */
class GPSTracker(mContext: Context) : Service(), LocationListener {

    var mContext: Context? = null

    init {
        this.mContext = mContext
        getLocation()
    }

    var isGPSEnable: Boolean = false
    var isNetworkEnable: Boolean = false
    var getLocation: Boolean = false

    var location: Location? = null
    var longitude: Double = 0.0
    var latitude: Double = 0.0

    val MIN_DISTANCE_CHANGE_FOR_UPDATES: Float = 10F
    val MIN_TIME_BW_UPDATES: Long = 1000 * 60 * 1
    var locationManager: LocationManager? = null

    @SuppressLint("ServiceCast", "MissingPermission")
    private fun getLocation() {

        locationManager = mContext?.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        isGPSEnable = locationManager!!.isProviderEnabled(LocationManager.GPS_PROVIDER)
        isNetworkEnable = locationManager!!.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

        if (!isGPSEnable && !isNetworkEnable) {
            mContext!!.toast("Please enable your network")
        } else {
            getLocation = true

            if (isNetworkEnable) {
                locationManager!!.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, this)

                if (locationManager != null) {
                    location = locationManager!!.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)

                    if (location != null) {
                        latitude = location!!.latitude
                        longitude = location!!.longitude
                    }
                }
            }
        }
    }

    fun getLongitudeLoc(): Double {
        if (location != null) {
            longitude = location!!.longitude
        }

        return longitude
    }

    fun getLotitudeLoc(): Double {
        if (location != null) {
            latitude = location!!.latitude
        }

        return latitude
    }

    override fun onLocationChanged(location: Location?) {}

    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}

    override fun onProviderEnabled(provider: String?) {}

    override fun onProviderDisabled(provider: String?) {}

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}
