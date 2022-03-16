package com.aesuriagasalazar.arenillasturismo.view.detail

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.aesuriagasalazar.arenillasturismo.R
import com.aesuriagasalazar.arenillasturismo.databinding.FragmentPlaceDetailsBinding
import com.aesuriagasalazar.arenillasturismo.model.domain.Place
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.Style
import com.mapbox.maps.plugin.annotation.annotations
import com.mapbox.maps.plugin.annotation.generated.CircleAnnotationOptions
import com.mapbox.maps.plugin.annotation.generated.createCircleAnnotationManager
import com.mapbox.maps.plugin.gestures.addOnMapClickListener
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType
import com.smarteist.autoimageslider.SliderAnimations

class FragmentPlaceDetails : Fragment() {

    private lateinit var binding: FragmentPlaceDetailsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_place_details,
            container,
            false
        )

        val place = FragmentPlaceDetailsArgs.fromBundle(requireArguments()).place
        val adapter = AdapterSliderImage(place.imagenes+place.miniatura)

        binding.place = place
        binding.lifecycleOwner = viewLifecycleOwner
        binding.imageSlider.setSliderAdapter(adapter)
        binding.imageSlider.setIndicatorAnimation(IndicatorAnimationType.WORM)
        binding.imageSlider.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION)
        binding.imageSlider.startAutoCycle()


        val mapInstance = binding.mapView.getMapboxMap()
        mapInstance.loadStyleUri(Style.MAPBOX_STREETS) {
            placeLocationMap(place)
        }
        mapInstance.addOnMapClickListener {
            Log.i("leer", "Point: $it")
            true
        }

        setHasOptionsMenu(true)
        return binding.root
    }

    private fun placeLocationMap(place: Place) {
        val point = Point.fromLngLat(place.longitud, place.latitud, place.altitud.toDouble())
        val annotationApi = binding.mapView.annotations
        val pointAnnotationManager = annotationApi.createCircleAnnotationManager()
        val pointAnnotationOptions = CircleAnnotationOptions()
            .withPoint(point)
            .withCircleRadius(18.0)
            .withCircleColor("#ee4e8b")
            .withCircleStrokeWidth(8.0)
            .withCircleStrokeColor("#ffffff")
        pointAnnotationManager.create(pointAnnotationOptions)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_home, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.home -> {
                findNavController()
                    .navigate(FragmentPlaceDetailsDirections.actionFragmentPlaceDetailsToMainMenu())
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    @SuppressLint("Lifecycle")
    override fun onStart() {
        super.onStart()
        binding.mapView.onStart()
    }

    @SuppressLint("Lifecycle")
    override fun onStop() {
        super.onStop()
        binding.mapView.onStop()
    }

    @SuppressLint("Lifecycle")
    override fun onLowMemory() {
        super.onLowMemory()
        binding.mapView.onLowMemory()
    }

    @SuppressLint("Lifecycle")
    override fun onDestroyView() {
        super.onDestroyView()
        binding.mapView.onDestroy()
    }
}