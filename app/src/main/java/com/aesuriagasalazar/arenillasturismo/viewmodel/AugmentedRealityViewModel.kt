package com.aesuriagasalazar.arenillasturismo.viewmodel

import androidx.lifecycle.ViewModel
import com.aesuriagasalazar.arenillasturismo.model.data.location.UserLocationLiveData

class AugmentedRealityViewModel: ViewModel() {

    val userLocation = UserLocationLiveData.locationRequest
}