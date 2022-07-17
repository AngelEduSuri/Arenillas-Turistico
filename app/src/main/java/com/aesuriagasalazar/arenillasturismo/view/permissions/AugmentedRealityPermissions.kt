package com.aesuriagasalazar.arenillasturismo.view.permissions

import com.aesuriagasalazar.arenillasturismo.R

class AugmentedRealityPermissions(private val permissions: PermissionsAndroid) {

    init {
        permissions.setResourcesByDialogs(
            R.string.all_granted_augmented_reality,
            R.string.permission_augmented_reality_denied_permanently,
            R.string.permissions_augmented_reality_required,
            R.string.rationale_permissions_augmented_reality,
            R.string.permissions_augmented_reality_required
        )
    }

    fun checkPermissions() = permissions.checkPermissions()

    fun showDialogPermissions(granted: (Boolean) -> Unit) =
        permissions.showDialogPermissions(granted)

}