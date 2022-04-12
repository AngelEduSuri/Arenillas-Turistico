package com.aesuriagasalazar.arenillasturismo.view.permissions

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.fragment.app.Fragment
import com.aesuriagasalazar.arenillasturismo.R
import com.fondesa.kpermissions.allGranted
import com.fondesa.kpermissions.anyPermanentlyDenied
import com.fondesa.kpermissions.anyShouldShowRationale
import com.fondesa.kpermissions.extension.liveData
import com.fondesa.kpermissions.extension.permissionsBuilder
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar

class CameraPermission(private val fragment: Fragment) {

    private val request by lazy {
        fragment.permissionsBuilder(
            Manifest.permission.CAMERA,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ).build()
    }

    fun checkPermissions() = request.checkStatus().allGranted()

    fun showDialogPermissions(granted: (Boolean) -> Unit) {
        requestPermissions(granted)
        request.send()
    }

    private fun requestPermissions(granted: (Boolean) -> Unit) {
        request.liveData().observe(fragment) {
            when {
                it.allGranted() -> {
                    granted(true)
                    showGrantedSnackBar()
                }
                it.anyPermanentlyDenied() -> { showPermanentlyDeniedDialog() }
                it.anyShouldShowRationale() -> { showRationaleDialog() }
            }
        }
    }

    private fun showGrantedSnackBar() {
        val msg = fragment.getString(R.string.all_granted_location)
        Snackbar.make(fragment.requireView(), msg, Snackbar.LENGTH_LONG).show()
    }

    private fun showPermanentlyDeniedDialog() {
        val msg = fragment.getString(R.string.permission_location_denied_permanently)
        MaterialAlertDialogBuilder(fragment.requireContext())
            .setTitle(R.string.permissions_required)
            .setMessage(msg)
            .setPositiveButton(R.string.accept) { _, _ ->
                val intent = createAppSettingsIntent()
                fragment.startActivity(intent)
            }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }

    private fun showRationaleDialog() {
        val msg = fragment.getString(R.string.rationale_permissions)
        MaterialAlertDialogBuilder(fragment.requireContext())
            .setTitle(R.string.permissions_required)
            .setMessage(msg)
            .setPositiveButton(R.string.retry_again) { _, _ ->
                // Send the request again.
                request.send()
            }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }

    private fun createAppSettingsIntent() = Intent().apply {
        action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
        data = Uri.fromParts("package", fragment.context?.packageName, null)
    }
}