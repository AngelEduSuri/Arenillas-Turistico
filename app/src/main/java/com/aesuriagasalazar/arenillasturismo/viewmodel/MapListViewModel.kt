package com.aesuriagasalazar.arenillasturismo.viewmodel

import android.app.Application
import androidx.lifecycle.*
import com.aesuriagasalazar.arenillasturismo.model.Repository
import com.aesuriagasalazar.arenillasturismo.model.domain.Place
import com.aesuriagasalazar.arenillasturismo.model.domain.asDomainModel
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraBoundsOptions
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.CoordinateBounds
import com.mapbox.maps.plugin.annotation.generated.PointAnnotation
import kotlinx.coroutines.launch

class MapListViewModel(private val repository: Repository, application: Application): AndroidViewModel(
    application
) {

    /** Variables observers para la interaccion del usuario **/

    // Cambia la capa del mapa entre satelite y predeterminado
    private val _layerMap = MutableLiveData<Boolean>()
    val layerMap: LiveData<Boolean> = _layerMap

    // Lista que se actualiza con los lugares en el mapa
    private var _listPlaces = MutableLiveData<List<Place>>()
    var listPlaces: LiveData<List<Place>> = _listPlaces

    // Administra el tiempo de carga de los lugares con un progress bar
    private val _loadingData = MutableLiveData<Boolean>()
    val loadingData: LiveData<Boolean> = _loadingData

    // Administra la navegacion a la pantalla detalles
    private val _navigationDetailsPlace = MutableLiveData<Place?>()
    val navigationDetails: LiveData<Place?> = _navigationDetailsPlace

    // Administra cuando el usuario selecciona un punto en el mapa
    private val _placeSelected = MutableLiveData<PlaceShowMap?>()
    val placeSelected: LiveData<PlaceShowMap?> = _placeSelected

    // Controla la informacion de la camara del mapa
    private val _cameraOptions = MutableLiveData<CameraOptions>()
    val cameraOptions: LiveData<CameraOptions> = _cameraOptions

    // Administra el permiso de ubicacion del dispositivo
    private val _userPermission = MutableLiveData(false)
    val userPermission: LiveData<Boolean> = _userPermission

    // Controla la visualizacion de la ubicacion del usuario
    private val _userLocation = MutableLiveData<Boolean>()
    val userLocation: LiveData<Boolean> = _userLocation

    // Controla la visualizacion del boton de ubicacion del usuario
    private val _showButtonLocation = MutableLiveData(false)
    val showButtonLocation: LiveData<Boolean> = _showButtonLocation

    private val _augmentedRealityNav = MutableLiveData<Boolean>()
    val augmentedRealityNav: LiveData<Boolean> = _augmentedRealityNav

    // Cuando cargue el view model carga el mapa por defecto y obtiene la lista de lugares
    init {
        layerMapStreet()
        getListPlaces()
    }

    /** @param category Recibe la categoria y hace la busqueda en la base de datos
     * si es vacio obtiene todos los lugares
     */
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

    /** @param place Obtiene el lugar selecionado del usuario
     * y navega a la pantalla con los detalles
     */
    fun onNavigationDetailsPlace(place: Place) {
        _navigationDetailsPlace.value = place
    }

    /** Resetea la variable de navegacion cuando el usuario esta en la pantalla detalles **/
    fun onNavigationDetailsDone() {
        _navigationDetailsPlace.value = null
    }

    /** @param place seleccionado por el usuario
     * @param pointAnnotation informacion del lugar para mostrar el cartel con la imagen
     */
    fun onPlaceShow(place: Place, pointAnnotation: PointAnnotation) {
        _placeSelected.value = PlaceShowMap(place, pointAnnotation)
    }

    /** Resetea el lugar seleccionado cuando el usuario selecciona un punto que no sea un lugar **/
    fun onPlaceIdle() {
        _placeSelected.value = null
    }

    /** Establece el permiso de ubicacion del usuario en verdadero o falso si se presiona otra vez **/
    fun showUserPermission() {
        _userPermission.value = true
    }

    fun idleUserPermission() {
        _userPermission.value = false
    }

    /** Cuando es verdadero empieza a mostrar la ubicacion del usuario **/
    fun showUserLocationOnMap(){
        _userLocation.value = true
    }

    /** Cuando es false inabilita la ubicacion del usuario **/
    fun idleUserLocationOnMap() {
        _userLocation.value = false
    }

    fun showButtonLocationOnMap() {
        _showButtonLocation.value = true
    }

    fun idleButtonLocationOnMap() {
        _showButtonLocation.value = false
    }

    /** Cambia la capa del mapa a predeterminado **/
    fun layerMapStreet() {
        _layerMap.value = false
    }

    /** Cambia la capa del mapa a satelite **/
    fun layerMapSatellite() {
        _layerMap.value = true
    }

    /** @param camera obtiene informacion de la camara del mapa
     * y lo almacena en el observable
     */
    fun loadCameraState(camera: CameraOptions) {
        _cameraOptions.value = camera
    }

    /** Bloque la camara del mapa en un area que abarca el canton Arenillas **/
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

    /** Activa la navegacion hacia la ventana de realidad aumentada cuando se hace click en el boton **/
    fun onClickAugmentedRealityNav() {
        _augmentedRealityNav.value = true
    }

    /** Resetea el observable cuando el usuario navega hacia la vista de reliadad aumentada **/
    fun onAugmentedRealityDone() {
        _augmentedRealityNav.value = false
    }
}

/** @param repository obtiene una instancia del repositorio
 * y el factory se encarga de pasarlo al view model
 */
class MapListViewModelFactory(
    private val repository: Repository,
    private val application: Application
): ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MapListViewModel::class.java)) {
            return MapListViewModel(repository, application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

/** Clase de datos que representa el lugar en el mapa
 * @param place repesenta el lugar seleccionado
 * @param pointAnnotation metadatos que representa ese lugar
 */
data class PlaceShowMap(val place: Place, val pointAnnotation: PointAnnotation)