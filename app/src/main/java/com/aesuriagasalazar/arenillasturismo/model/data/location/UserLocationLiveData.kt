package com.aesuriagasalazar.arenillasturismo.view.map

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import androidx.lifecycle.LiveData
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices

class UserLocationLiveData(private val context: Context): LiveData<UserLocation>() {

    private var fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(p0: LocationResult) {
            super.onLocationResult(p0)
            p0.locations.forEach {
                setLocationData(it)
            }
        }
    }

    private fun setLocationData(location: Location) {
        value = UserLocation(
            longitude = location.longitude,
            latitude = location.latitude
        )
    }

    @SuppressLint("MissingPermission")
    private fun startLocationUpdates() {
        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            context.mainLooper
        )
    }

    override fun onInactive() {
        super.onInactive()
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    @SuppressLint("MissingPermission")
    override fun onActive() {
        super.onActive()
        fusedLocationClient.lastLocation.addOnSuccessListener {
            it.also {
                setLocationData(it)
            }
        }
        startLocationUpdates()
    }

    companion object {
        val locationRequest: LocationRequest = LocationRequest.create().apply {
            interval = 5000
            fastestInterval = 3000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }
    }
}

data class UserLocation(val longitude: Double, val latitude: Double)