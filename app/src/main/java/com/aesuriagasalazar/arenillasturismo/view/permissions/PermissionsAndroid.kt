package com.aesuriagasalazar.arenillasturismo.view.permissions

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.fragment.app.Fragment
import com.aesuriagasalazar.arenillasturismo.R
import com.fondesa.kpermissions.allGranted
import com.fondesa.kpermissions.anyPermanentlyDenied
import com.fondesa.kpermissions.anyShouldShowRationale
import com.fondesa.kpermissions.extension.liveData
import com.fondesa.kpermissions.request.PermissionRequest
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar

class PermissionsAndroid(private val request: PermissionRequest, private val fragment: Fragment) {

    private lateinit var titleSnackbar: String
    private lateinit var permanentlyDeniedDialog: String
    private lateinit var titlePermanentlyDeniedDialog: String
    private lateinit var rationaleMessage: String
    private lateinit var titleRationaleDialog: String

    fun checkPermissions() = request.checkStatus().allGranted()

    fun showDialogPermissions(granted: (Boolean) -> Unit) {
        requestPermissions(granted)
        request.send()
    }

    fun setResourcesByDialogs(
        titleSnackbar: Int,
        permanentlyDeniedDialog: Int,
        titlePermanentlyDeniedDialog: Int,
        rationaleMessage: Int,
        titleRationaleDialog: Int
    ) {
        this.titleSnackbar = fragment.getString(titleSnackbar)
        this.permanentlyDeniedDialog = fragment.getString(permanentlyDeniedDialog)
        this.titlePermanentlyDeniedDialog = fragment.getString(titlePermanentlyDeniedDialog)
        this.rationaleMessage = fragment.getString(rationaleMessage)
        this.titleRationaleDialog = fragment.getString(titleRationaleDialog)
    }

    private fun requestPermissions(granted: (Boolean) -> Unit) {
        request.liveData().observe(fragment) {
            when {
                it.allGranted() -> {
                    granted(true)
                    showGrantedSnackBar()
                }
                it.anyPermanentlyDenied() -> {
                    showPermanentlyDeniedDialog()
                }
                it.anyShouldShowRationale() -> {
                    showRationaleDialog()
                }
            }
        }
    }

    private fun showGrantedSnackBar() {
        Snackbar.make(fragment.requireView(), titleSnackbar, Snackbar.LENGTH_LONG).show()
    }

    private fun showPermanentlyDeniedDialog() {
        MaterialAlertDialogBuilder(fragment.requireContext())
            .setTitle(titlePermanentlyDeniedDialog)
            .setMessage(permanentlyDeniedDialog)
            .setPositiveButton(R.string.accept) { _, _ ->
                val intent = createAppSettingsIntent()
                fragment.startActivity(intent)
            }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }

    private fun showRationaleDialog() {
        MaterialAlertDialogBuilder(fragment.requireContext())
            .setTitle(titleRationaleDialog)
            .setMessage(rationaleMessage)
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