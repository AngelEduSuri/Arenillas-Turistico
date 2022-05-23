package com.aesuriagasalazar.arenillasturismo.view.map

import android.animation.Animator
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Bundle
import android.view.*
import androidx.annotation.DrawableRes
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.aesuriagasalazar.arenillasturismo.R
import com.aesuriagasalazar.arenillasturismo.databinding.FragmentMapListBinding
import com.aesuriagasalazar.arenillasturismo.databinding.ItemPlaceMapBinding
import com.aesuriagasalazar.arenillasturismo.model.CategoryStatic
import com.aesuriagasalazar.arenillasturismo.model.IconMap
import com.aesuriagasalazar.arenillasturismo.model.data.local.LocalRepository
import com.aesuriagasalazar.arenillasturismo.model.data.local.PlacesDatabase
import com.aesuriagasalazar.arenillasturismo.model.domain.Place
import com.aesuriagasalazar.arenillasturismo.view.permissions.LocationPermission
import com.aesuriagasalazar.arenillasturismo.viewmodel.MapListViewModel
import com.aesuriagasalazar.arenillasturismo.viewmodel.MapListViewModelFactory
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.MapboxExperimental
import com.mapbox.maps.Style
import com.mapbox.maps.ViewAnnotationAnchor
import com.mapbox.maps.dsl.cameraOptions
import com.mapbox.maps.extension.style.expressions.dsl.generated.interpolate
import com.mapbox.maps.plugin.LocationPuck2D
import com.mapbox.maps.plugin.animation.MapAnimationOptions.Companion.mapAnimationOptions
import com.mapbox.maps.plugin.animation.flyTo
import com.mapbox.maps.plugin.annotation.annotations
import com.mapbox.maps.plugin.annotation.generated.*
import com.mapbox.maps.plugin.gestures.OnMapClickListener
import com.mapbox.maps.plugin.gestures.addOnMapClickListener
import com.mapbox.maps.plugin.gestures.gestures
import com.mapbox.maps.plugin.gestures.removeOnMapClickListener
import com.mapbox.maps.plugin.locationcomponent.OnIndicatorPositionChangedListener
import com.mapbox.maps.plugin.locationcomponent.location
import com.mapbox.maps.viewannotation.ViewAnnotationManager
import com.mapbox.maps.viewannotation.viewAnnotationOptions
import kotlinx.coroutines.launch

@MapboxExperimental
class FragmentMapList : Fragment() {

    private lateinit var binding: FragmentMapListBinding
    private lateinit var cardViewMap: ItemPlaceMapBinding
    private lateinit var mapClickListener: OnMapClickListener
    private lateinit var annotationManager: PointAnnotationManager
    private lateinit var viewAnnotation: ViewAnnotationManager
    private lateinit var locationPermission: LocationPermission

    private val viewModel: MapListViewModel by viewModels {
        MapListViewModelFactory(
            LocalRepository(PlacesDatabase.getDatabase(requireContext()).placeDao)
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMapListBinding.inflate(inflater)
        cardViewMap = ItemPlaceMapBinding.inflate(layoutInflater, binding.mapViewList, false)

        /** Observable sobre la capa del mapa **/
        viewModel.layerMap.observe(viewLifecycleOwner) {
            it?.let {
                val style = if (it) {
                    Style.SATELLITE_STREETS
                } else {
                    Style.MAPBOX_STREETS
                }
                binding.mapViewList.getMapboxMap().loadStyleUri(style)
            }
        }

        /** Oyente cuando el mapa termine de cargar **/
        binding.mapViewList.getMapboxMap().addOnStyleLoadedListener {
            binding.mapViewList.getMapboxMap().setBounds(viewModel.lockCameraArea())
            loadObservableDataOnMap()
        }

        /** Se agrega oyente de click al mapa para cerrar las vistas abiertas **/
        mapClickListener = OnMapClickListener {
            viewModel.onPlaceIdle()
            viewAnnotation.removeViewAnnotation(cardViewMap.root)
            cameraZoomOutPlace()
            binding.mapViewList.getMapboxMap().removeOnMapClickListener(mapClickListener)
            removeCameraUserLocation()
            true
        }

        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel

        setHasOptionsMenu(true)

        return binding.root
    }

    /** Funcion que vincula los datos observables con el mapa **/
    private fun loadObservableDataOnMap() {
        if (!viewModel.listPlaces.hasActiveObservers()) {
            /** Observable sobre la lista de lugares **/
            viewModel.listPlaces.observe(viewLifecycleOwner) { places ->
                lifecycleScope.launch {
                    places?.let {
                        if (places.isEmpty()) {
                            Snackbar.make(
                                binding.root,
                                "No existen lugares para esta categoria",
                                Snackbar.LENGTH_SHORT
                            ).show()
                        } else {
                            loadPlacesOnMap(places)
                        }
                    }
                }
            }

            /** Observable sobre el lugar seleccionado **/
            viewModel.placeSelected.observe(viewLifecycleOwner) {
                if (it != null) {
                    loadPlaceCard(it.place, it.pointAnnotation)
                    removeCameraUserLocation()
                    binding.mapViewList.getMapboxMap().addOnMapClickListener(mapClickListener)
                } else {
                    cameraZoomOutPlace()
                }
            }

            /** Observable sobre la navegacion hacia la pantalla detalles **/
            viewModel.navigationDetails.observe(viewLifecycleOwner) {
                it?.let {
                    findNavController()
                        .navigate(
                            FragmentMapListDirections.actionFragmentMapListToFragmentPlaceDetails(
                                it
                            )
                        )
                    viewModel.onNavigationDetailsDone()
                }
            }

            /** Observable sobre la peticion de los permisos de ubicacion **/
            viewModel.userPermission.observe(viewLifecycleOwner) {
                it?.let {
                    if (it) {
                        locationPermission = LocationPermission(this)
                        if (locationPermission.checkPermissions()) {
                            viewModel.showUserLocationOnMap()
                        } else {
                            locationPermission.showDialogPermissions { granted ->
                                if (granted) viewModel.showUserLocationOnMap()
                            }
                        }
                        viewModel.idleUserPermission()
                    }
                }
            }

            /** Observable sobre la ubicacion del usuario **/
            viewModel.userLocation.observe(viewLifecycleOwner) {
                it?.let {
                    if (it) {
                        startUserLocationUpdate()
                        addCameraUserLocation()
                        viewModel.showButtonLocationOnMap()
                    } else {
                        stopUserLocationUpdate()
                        removeCameraUserLocation()
                        viewModel.idleButtonLocationOnMap()
                    }
                }
            }

            /** Observable sobre el estado de la camara del lugar seleccionado **/
            viewModel.cameraOptions.observe(viewLifecycleOwner) {
                it?.let {
                    binding.mapViewList.getMapboxMap().setCamera(it)
                }
            }
        }
    }

    /** Metodo para cargar todos los lugares en el mapa **/
    private fun loadPlacesOnMap(places: List<Place>) {
        viewAnnotation = binding.mapViewList.viewAnnotationManager
        annotationManager = binding.mapViewList.annotations.createPointAnnotationManager()
        places.forEach {
            val iconImage = IconMap.getIconMap(it, resources)
            drawableToBitmap(iconImage)?.let { bitmap ->
                val point = PointAnnotationOptions()
                    .withPoint(Point.fromLngLat(it.longitud, it.latitud, it.altitud.toDouble()))
                    .withIconImage(bitmap)
                    .withIconOffset(listOf(0.0, -20.0))
                annotationManager.create(point)
            }
        }
        annotationManager.addClickListener(OnPointAnnotationClickListener { point ->
            val place = places.single {
                (it.longitud == point.point.longitude() && it.latitud == point.point.latitude())
            }
            viewModel.onPlaceShow(place, point)
            cameraZoomInPlace(place)
            true
        })
    }

    /** Metodo para agregar una tarjeta de detalle cuando el usuario selecciona un lugar en el mapa **/
    private fun loadPlaceCard(place: Place, pointAnnotation: PointAnnotation) {
        if (cardViewMap.root.isVisible) viewAnnotation.removeViewAnnotation(cardViewMap.root)
        viewAnnotation.addViewAnnotation(
            cardViewMap.root,
            viewAnnotationOptions {
                geometry(Point.fromLngLat(place.longitud, place.latitud, place.altitud.toDouble()))
                allowOverlap(true)
                anchor(ViewAnnotationAnchor.BOTTOM)
                offsetY(pointAnnotation.iconImageBitmap?.height)
            }
        )
        cardViewMap.place = place
        cardViewMap.onClose = CloseViewMap(
            {
                viewModel.onPlaceIdle()
                viewAnnotation.removeViewAnnotation(cardViewMap.root)
                cameraZoomOutPlace()
            },
            {
                viewModel.onNavigationDetailsPlace(it)
            }
        )
    }

    /** Metodo para convertir un recurso drawable en un mapa de bits que se adapta al zoom **/
    private fun drawableToBitmap(@DrawableRes resourceId: Int): Bitmap? {
        val sourceDrawable = AppCompatResources.getDrawable(requireContext(), resourceId)
        val constantState = sourceDrawable?.constantState
        val drawable = constantState?.newDrawable()?.mutate()
        val bitmap = drawable?.let {
            Bitmap.createBitmap(
                drawable.intrinsicWidth / 2 - 25, drawable.intrinsicHeight / 2 - 25,
                Bitmap.Config.ARGB_8888
            )
        }
        val canvas = bitmap?.let { Canvas(it) }
        canvas?.let {
            drawable.setBounds(0, 0, it.width, it.height)
            drawable.draw(it)
        }
        return bitmap
    }

    /** Metodo que se encarga de mostrar un dialog con las categorias para filtar los lugares **/
    private fun filterListForCategory() {
        val listString = arrayListOf(resources.getString(R.string.all)).also { array ->
            CategoryStatic.getCategories().map {
                array.add(resources.getString(it.title))
            }
        }

        MaterialAlertDialogBuilder(requireContext())
            .setTitle(resources.getString(R.string.category_select))
            .setItems(listString.toTypedArray()) { _, item ->
                /** Se resetea los puntos en el mapa y se oyentes y vistas que esten abiertas **/
                cameraZoomOutPlace()
                if (listString[item] == resources.getString(R.string.all)) {
                    viewModel.getListPlaces()
                } else {
                    viewModel.getListPlaces(listString[item].lowercase())
                }
                binding.mapViewList.getMapboxMap().removeOnMapClickListener(mapClickListener)
                removeCameraUserLocation()
                closePlaceOnMap()
            }
            .show()
    }

    /** Funcion para cerrar el lugar seleccionado **/
    private fun closePlaceOnMap() {
        binding.mapViewList.annotations.removeAnnotationManager(annotationManager)
        viewAnnotation.removeViewAnnotation(cardViewMap.root)
        viewModel.onPlaceIdle()
    }

    /** Metodo para enfocar la camara en el lugar cuando el usuario lo selecciona **/
    private fun cameraZoomInPlace(place: Place) {
        val cameraOptions = CameraOptions.Builder()
            .center(Point.fromLngLat(place.longitud, place.latitud, place.altitud.toDouble()))
            .zoom(15.0)
            .build()
        binding.mapViewList.getMapboxMap().flyTo(
            cameraOptions,
            mapAnimationOptions {
                duration(3000)
                animatorListener(onCameraAnimation)
            }
        )
        viewModel.loadCameraState(cameraOptions)
    }

    /** Metodo para regresar la camara a su esta original **/
    private fun cameraZoomOutPlace() {
        binding.mapViewList.getMapboxMap().flyTo(
            cameraOptions {
                center(Point.fromLngLat(-80.06236766696969, -3.5666243867622214))
                zoom(10.0)
                pitch(0.0)
            },
            mapAnimationOptions {
                duration(3000)
                animatorListener(onCameraAnimation)
            }
        )
    }

    /** Funcion que infla el menu para el filtrado de luagres **/
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_filter_map, menu)
    }

    /** Funcion que detecta cuando se selecciono el menu **/
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.filter -> {
                filterListForCategory()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    /** Funcion que habilita la ubicacion del dispositivo y lo muestra en el mapa **/
    private fun startUserLocationUpdate() {
        val locationComponentPlugin = binding.mapViewList.location
        locationComponentPlugin.updateSettings {
            enabled = true
            locationPuck = LocationPuck2D(
                bearingImage = AppCompatResources.getDrawable(
                    requireContext(),
                    R.drawable.mapbox_user_puck_icon,
                ),
                shadowImage = AppCompatResources.getDrawable(
                    requireContext(),
                    R.drawable.mapbox_user_icon_shadow,
                ),
                scaleExpression = interpolate {
                    linear()
                    zoom()
                    stop {
                        literal(0.0)
                        literal(0.6)
                    }
                    stop {
                        literal(20.0)
                        literal(1.0)
                    }
                }.toJson()
            )
        }
    }

    /** Funcion que inhabilita la ubicacion del dispositivo y lo quita del mapa **/
    private fun stopUserLocationUpdate() {
        binding.mapViewList.location.enabled = false
    }

    /** Funcion que agrega el seguimiento de camara de la ubicacion **/
    private fun addCameraUserLocation() {
        binding.mapViewList.location.addOnIndicatorPositionChangedListener(onLocationChanged)
    }

    /** Funcion que elimina el seguimiento de camara de la ubicacion **/
    private fun removeCameraUserLocation() {
        binding.mapViewList.location.removeOnIndicatorPositionChangedListener(onLocationChanged)
    }

    /** Seguimiento de la camara al usuario en el mapa **/
    private val onLocationChanged by lazy {
        OnIndicatorPositionChangedListener {
            val cameraOptions = CameraOptions.Builder()
                .center(it)
                .zoom(14.0)
                .pitch(0.0)
                .build()
            binding.mapViewList.gestures.focalPoint =
                binding.mapViewList.getMapboxMap().pixelForCoordinate(it)
            binding.mapViewList.getMapboxMap().setCamera(cameraOptions)
            removeCameraUserLocation()
        }
    }

    /** Oyente de animacion de camara del mapa **/
    private val onCameraAnimation by lazy {
        object : Animator.AnimatorListener {
            override fun onAnimationStart(p0: Animator?) {
                binding.userLocation.isEnabled = false
            }

            override fun onAnimationEnd(p0: Animator?) {
                binding.userLocation.isEnabled = true
            }

            override fun onAnimationCancel(p0: Animator?) {}
            override fun onAnimationRepeat(p0: Animator?) {}
        }
    }
}

/** Clase que se vincula al data binding de la tarjeta de detalle **/
class CloseViewMap(
    private val clickListener: () -> Unit,
    private val details: (place: Place) -> Unit
) {
    fun onMapClick() = clickListener()
    fun onDetailsGo(place: Place) = details(place)
}

