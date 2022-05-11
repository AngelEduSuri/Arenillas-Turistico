package com.aesuriagasalazar.arenillasturismo.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.aesuriagasalazar.arenillasturismo.R
import com.aesuriagasalazar.arenillasturismo.model.data.local.LocalRepository
import com.aesuriagasalazar.arenillasturismo.model.data.location.UserLocationOnLiveData
import com.aesuriagasalazar.arenillasturismo.model.domain.Place
import com.aesuriagasalazar.arenillasturismo.model.domain.asDomainModel
import com.aesuriagasalazar.arenillasturismo.model.domain.asDomainModelList
import kotlinx.coroutines.launch

class AugmentedRealityViewModel(private val repository: LocalRepository, application: Application) :
    ViewModel() {

    val userLocation = UserLocationOnLiveData(application)

    private val _placeList = MutableLiveData<List<Place>>()
    val placeList: LiveData<List<Place>> = _placeList

    private val _placeDetailNavigation = MutableLiveData<Place?>()
    val placeDetailNavigation: LiveData<Place?> = _placeDetailNavigation

    private val _rangeValueSlider = MutableLiveData(0.0f)
    val rangeValueSlider: LiveData<Float> = _rangeValueSlider

    val titleRange: LiveData<String> = Transformations.map(rangeValueSlider) {
        return@map when (it) {
            0.0f -> application.getString(R.string.range_place, 500, "m")
            1.0f -> application.getString(R.string.range_place, 1, "km")
            2.0f -> application.getString(R.string.range_place, 2, "km")
            3.0f -> application.getString(R.string.range_place, 5, "km")
            4.0f -> application.getString(R.string.range_place, 10, "km")
            else -> application.getString(R.string.range_place, 20, "km")
        }
    }

    init {
        loadDataFromRepository()
        Log.i("leer", titleRange.value.toString())
    }

    private fun loadDataFromRepository() {
        viewModelScope.launch {
            _placeList.value = repository.getAllPlaces().asDomainModelList()
        }
    }

    fun getListPlacesForCategory(category: String) {
        viewModelScope.launch {
            if (category.isEmpty()) {
                _placeList.value = repository.getAllPlaces().asDomainModelList()
            } else {
                _placeList.value = repository.getPlacesCategory(category).asDomainModelList()
            }
        }
    }

    fun getPlaceForId(placeId: Int) {
        viewModelScope.launch {
            _placeDetailNavigation.value = repository.getPlaceForId(placeId).asDomainModel()
        }
    }

    fun placeDetailNavigationDone() {
        _placeDetailNavigation.value = null
    }

    fun onSliderValueChanged(value: Float) {
        _rangeValueSlider.value = value
    }
}

@Suppress("UNCHECKED_CAST")
class AugmentedRealityViewModelFactory(
    private val repository: LocalRepository,
    private val application: Application
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AugmentedRealityViewModel::class.java)) {
            return AugmentedRealityViewModel(repository, application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

