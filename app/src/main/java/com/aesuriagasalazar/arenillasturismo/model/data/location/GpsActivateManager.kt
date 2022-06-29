package com.aesuriagasalazar.arenillasturismo.model.data.location

import android.content.Context
import android.content.IntentSender
import android.location.LocationManager
import androidx.activity.result.IntentSenderRequest
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.LocationSettingsResponse
import com.google.android.gms.location.SettingsClient
import com.google.android.gms.tasks.Task

class GpsActivateManager(private val context: Context) {

    private val locationManager =
        context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

    fun turnOnGpsBuilder(activate: (IntentSenderRequest?) -> Unit) {
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            activate(null)
        } else {
            changeLocationState(activate)
        }
    }

    private fun changeLocationState(activate: (IntentSenderRequest) -> Unit) {
        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(UserLocationOnLiveData.locationRequest)
        val client: SettingsClient = LocationServices.getSettingsClient(context)
        val task: Task<LocationSettingsResponse> = client.checkLocationSettings(builder.build())

        task.addOnFailureListener {
            if (it is ResolvableApiException) {
                try {
                    val pendingIntent = IntentSenderRequest.Builder(it.resolution).build()
                    activate(pendingIntent)
                } catch (sendEx: IntentSender.SendIntentException) {
                    // Ignore the error.
                }
            }
        }
    }
}