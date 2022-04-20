package com.aesuriagasalazar.arenillasturismo.model.data.location

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import androidx.lifecycle.LiveData
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices

/** Clase de datos que representa la ubicacion del usuario **/
data class UserLocation(
    val longitude: Double,
    val latitude: Double,
    val altitude: Double,
    val accuracy: Float
)

class UserLocationOnLiveData(private val context: Context) : LiveData<UserLocation>() {

    private var fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

    /** Funcion anonima que se llama cuando tenemos nuevos datos de la ubicacion **/
    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(p0: LocationResult) {
            super.onLocationResult(p0)
            p0.locations.forEach {
                setLocationData(it)
            }
        }
    }

    /** Funcion que establece la ubicacion a la data class UserLocation y al value del live data **/
    private fun setLocationData(location: Location) {
        value = UserLocation(
            longitude = location.longitude,
            latitude = location.latitude,
            altitude = location.altitude,
            accuracy = location.accuracy
        )
    }

    /** Funcion que inicia la solicitud de las actualizaciones de ubicacion **/
    @SuppressLint("MissingPermission")
    private fun startLocationUpdates() {
        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            context.mainLooper
        )
    }

    /** Se llama cuando el propietario del ciclo de vida se inicia o reanuda **/
    @SuppressLint("MissingPermission")
    override fun onActive() {
        super.onActive()
        // Obtiene la ultima ubicacion conocida antes de pedir actualizaciones **/
        fusedLocationClient.lastLocation.addOnSuccessListener {
            it?.let {
                setLocationData(it)
            }
        }
        startLocationUpdates()
    }

    /** Se llama cuando el propietario del ciclo de vida se pausa, detiene o destruye **/
    override fun onInactive() {
        super.onInactive()
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    /** Objeto complementario que establece las actualizaciones de ubicacion como el intervalo y prioridad **/
    companion object {
        val locationRequest: LocationRequest = LocationRequest.create().apply {
            interval = 10000
            fastestInterval = 5000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }
    }
}
