package com.aesuriagasalazar.arenillasturismo.view.map

import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.annotation.DrawableRes
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.aesuriagasalazar.arenillasturismo.R
import com.aesuriagasalazar.arenillasturismo.databinding.FragmentMapListBinding
import com.aesuriagasalazar.arenillasturismo.databinding.ItemPlaceMapBinding
import com.aesuriagasalazar.arenillasturismo.model.CategoryStatic
import com.aesuriagasalazar.arenillasturismo.model.IconMap
import com.aesuriagasalazar.arenillasturismo.model.Repository
import com.aesuriagasalazar.arenillasturismo.model.data.local.PlacesDatabase
import com.aesuriagasalazar.arenillasturismo.model.data.remote.RealTimeDataBase
import com.aesuriagasalazar.arenillasturismo.model.domain.Place
import com.aesuriagasalazar.arenillasturismo.view.permissions.UserPermissions
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
import com.mapbox.maps.plugin.animation.MapAnimationOptions.Companion.mapAnimationOptions
import com.mapbox.maps.plugin.animation.flyTo
import com.mapbox.maps.plugin.annotation.annotations
import com.mapbox.maps.plugin.annotation.generated.*
import com.mapbox.maps.plugin.gestures.OnMapClickListener
import com.mapbox.maps.plugin.gestures.addOnMapClickListener
import com.mapbox.maps.plugin.gestures.removeOnMapClickListener
import com.mapbox.maps.viewannotation.ViewAnnotationManager
import com.mapbox.maps.viewannotation.viewAnnotationOptions
import kotlinx.coroutines.launch

@MapboxExperimental
class FragmentMapList : Fragment() {

    private lateinit var binding: FragmentMapListBinding
    private lateinit var viewModel: MapListViewModel
    private lateinit var viewModelFactory: MapListViewModelFactory
    private lateinit var mapClickListener: OnMapClickListener
    private lateinit var annotationManager: PointAnnotationManager
    private lateinit var viewAnnotation: ViewAnnotationManager
    private lateinit var cardViewMap: ItemPlaceMapBinding
    private lateinit var permissions: UserPermissions

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_map_list, container, false)
        cardViewMap = DataBindingUtil.inflate(layoutInflater, R.layout.item_place_map, binding.mapViewList, false)

        viewModelFactory = MapListViewModelFactory(Repository(RealTimeDataBase(), PlacesDatabase.getDatabase(requireContext()).placeDao))
        viewModel = ViewModelProvider(this, viewModelFactory)[MapListViewModel::class.java]

        viewModel.layerMap.observe(viewLifecycleOwner) {
            it?.let {
                if (it) {
                    binding.mapViewList.getMapboxMap().loadStyleUri(Style.SATELLITE_STREETS)
                } else {
                    binding.mapViewList.getMapboxMap().loadStyleUri(Style.MAPBOX_STREETS)
                }
            }
        }

        viewModel.listPlaces.observe(viewLifecycleOwner) { places ->
            lifecycleScope.launch {
                places?.let {
                    loadPlacesOnMap(places)
                    if (places.isEmpty())
                        Snackbar.make(
                            binding.root,
                            "No existen lugares para esta categoria",
                            Snackbar.LENGTH_SHORT
                        ).show()
                }
            }
        }

        viewModel.navigationDetails.observe(viewLifecycleOwner) {
            it?.let {
                findNavController()
                    .navigate(FragmentMapListDirections.actionFragmentMapListToFragmentPlaceDetails(it))
                viewModel.onNavigationDetailsDone()
            }
        }

        viewModel.placeSelected.observe(viewLifecycleOwner) {
            if (it != null) {
                loadPlaceCard(it.place, it.pointAnnotation)
                binding.mapViewList.getMapboxMap().addOnMapClickListener(mapClickListener)
            }
        }

        viewModel.userLocation.observe(viewLifecycleOwner) {
            it?.let {
                if (it) {
                    permissions = UserPermissions(this)
                    if (permissions.checkPermissions()) {
                        Snackbar.make(binding.root, "Habilitado, obteniendo ubicacion", Snackbar.LENGTH_LONG).show()
                    } else {
                        permissions.showDialogPermissions()
                    }
                    viewModel.userLocationPermissionIdle()
                } else {
                    Log.i("leer", "false")
                }
            }
        }

        viewModel.cameraOptions.observe(viewLifecycleOwner) {
            it?.let {
                binding.mapViewList.getMapboxMap().setCamera(it)
            }
        }

        /** Se agrega oyente de click al mapa para cerrar las vistas abiertas **/
        mapClickListener = OnMapClickListener {
            viewModel.onPlaceIdle()
            viewAnnotation.removeViewAnnotation(cardViewMap.root)
            cameraZoomOutPlace()
            Log.i("leer", it.toString())
            binding.mapViewList.getMapboxMap().removeOnMapClickListener(mapClickListener)
            true
        }

        binding.mapViewList.getMapboxMap().addOnStyleLoadedListener {
            binding.mapViewList.getMapboxMap().setBounds(viewModel.lockCameraArea())
        }

        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel

        setHasOptionsMenu(true)

        return binding.root
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
        val bitmap = drawable?.let { Bitmap.createBitmap(
            drawable.intrinsicWidth/2-25, drawable.intrinsicHeight/2-25,
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
                binding.mapViewList.annotations.removeAnnotationManager(annotationManager)
                viewAnnotation.removeViewAnnotation(cardViewMap.root)
                viewModel.onPlaceIdle()
            }
            .show()
    }

    /** Metodo para enfocar la camara en el lugar cuando el usuario lo selecciona **/
    private fun cameraZoomInPlace(place: Place) {
        val cameraOptions = CameraOptions.Builder()
            .center(Point.fromLngLat(place.longitud, place.latitud, place.altitud.toDouble()))
            .zoom(15.0)
            .pitch(65.0)
            .build()
        binding.mapViewList.getMapboxMap().flyTo(
            cameraOptions,
            mapAnimationOptions {
                duration(3000)
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

    override fun onPause() {
        super.onPause()
        Log.i("leer", "onpause")
    }
}

/** Clase que se vincula al data binding de la tarjeta de detalle **/
class CloseViewMap(private val clickListener: () -> Unit, private val details: (place: Place) -> Unit) {
    fun onMapClick() = clickListener()
    fun onDetailsGo(place: Place) = details(place)
}
