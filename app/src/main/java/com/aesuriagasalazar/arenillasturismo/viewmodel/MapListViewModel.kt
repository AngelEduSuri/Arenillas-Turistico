package com.aesuriagasalazar.arenillasturismo.viewmodel

import androidx.lifecycle.*
import com.aesuriagasalazar.arenillasturismo.model.Repository
import com.aesuriagasalazar.arenillasturismo.model.domain.Place
import com.aesuriagasalazar.arenillasturismo.model.domain.asDomainModel
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraBoundsOptions
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.CameraState
import com.mapbox.maps.CoordinateBounds
import com.mapbox.maps.plugin.annotation.generated.PointAnnotation
import kotlinx.coroutines.launch

class MapListViewModel(private val repository: Repository): ViewModel() {

    private val _layerMap = MutableLiveData<Boolean>()
    val layerMap: LiveData<Boolean> = _layerMap

    private var _listPlaces = MutableLiveData<List<Place>>()
    var listPlaces: LiveData<List<Place>> = _listPlaces

    private val _loadingData = MutableLiveData<Boolean>()
    val loadingData: LiveData<Boolean> = _loadingData

    private val _navigationDetailsPlace = MutableLiveData<Place?>()
    val navigationDetails: LiveData<Place?> = _navigationDetailsPlace

    private val _placeSelected = MutableLiveData<PlaceShowMap?>()
    val placeSelected: LiveData<PlaceShowMap?> = _placeSelected

    private val _userLocation = MutableLiveData<Boolean>()
    val userLocation: LiveData<Boolean> = _userLocation

    private val _cameraOptions = MutableLiveData<CameraOptions>()
    val cameraOptions: LiveData<CameraOptions> = _cameraOptions

    init {
        _layerMap.value = false
        getListPlaces()
    }

    fun getListPlaces(category: String = "") {
        viewModelScope.launch {
            _loadingData.value = true
            if (category.isEmpty()) {
                _listPlaces.value = repository.getAllPlaces().asDomainModel()
            } else {
                _listPlaces.value = repository.getPlacesCategory(category).asDomainModel()
            }
            _loadingData.value = false
        }
    }

    fun onNavigationDetailsPlace(place: Place) {
        _navigationDetailsPlace.value = place
    }

    fun onNavigationDetailsDone() {
        _navigationDetailsPlace.value = null
    }

    fun onPlaceShow(place: Place, pointAnnotation: PointAnnotation) {
        _placeSelected.value = PlaceShowMap(place, pointAnnotation)
    }

    fun onPlaceIdle() {
        _placeSelected.value = null
    }

    fun userLocation() {
        _userLocation.value = true
    }

    fun layerMapStreet() {
        _layerMap.value = false
    }

    fun layerMapSatellite() {
        _layerMap.value = true
    }

    fun loadCameraState(camera: CameraOptions) {
        _cameraOptions.value = camera
    }

    /** Metodo para bloquear la camara del mapa en un area que abarca el canton Arenillas **/
    fun lockCameraArea(): CameraBoundsOptions {
        return CameraBoundsOptions.Builder()
            .bounds(
                CoordinateBounds(
                    Point.fromLngLat(-80.22679018979726, -3.745770542403889),
                    Point.fromLngLat(-79.94177269729926, -3.387443311231351),
                    false
                )
            )
            .minZoom(10.0)
            .build()
    }
}

class MapListViewModelFactory(
    private val repository: Repository
): ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MapListViewModel::class.java)) {
            return MapListViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

data class PlaceShowMap(val place: Place, val pointAnnotation: PointAnnotation)