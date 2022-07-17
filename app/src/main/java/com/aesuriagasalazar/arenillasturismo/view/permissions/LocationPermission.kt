package com.aesuriagasalazar.arenillasturismo.view.permissions

import com.aesuriagasalazar.arenillasturismo.R

class LocationPermission(
    private val permissions: PermissionsAndroid
) {

    init {
        permissions.setResourcesByDialogs(
            R.string.all_granted_location,
            R.string.permission_location_denied_permanently,
            R.string.permissions_location_required,
            R.string.rationale_permissions_location,
            R.string.permissions_location_required
        )
    }

    fun checkPermissions() = permissions.checkPermissions()

    fun showDialogPermissions(granted: (Boolean) -> Unit) =
        permissions.showDialogPermissions(granted)
}