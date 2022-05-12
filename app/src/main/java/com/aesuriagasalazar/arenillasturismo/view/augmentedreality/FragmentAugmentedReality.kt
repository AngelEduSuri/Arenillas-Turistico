package com.aesuriagasalazar.arenillasturismo.view.augmentedreality

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.aesuriagasalazar.arenillasturismo.R
import com.aesuriagasalazar.arenillasturismo.databinding.FragmentAugmentedRealityBinding
import com.aesuriagasalazar.arenillasturismo.model.CategoryStatic
import com.aesuriagasalazar.arenillasturismo.model.data.local.LocalRepository
import com.aesuriagasalazar.arenillasturismo.model.data.local.PlacesDatabase
import com.aesuriagasalazar.arenillasturismo.viewmodel.AugmentedRealityViewModel
import com.aesuriagasalazar.arenillasturismo.viewmodel.AugmentedRealityViewModelFactory
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.Gson
import com.wikitude.architect.ArchitectStartupConfiguration


class FragmentAugmentedReality : Fragment() {

    private lateinit var binding: FragmentAugmentedRealityBinding
    private val viewModel: AugmentedRealityViewModel by viewModels {
        AugmentedRealityViewModelFactory(
            LocalRepository(
                PlacesDatabase.getDatabase(requireContext()).placeDao
            ), requireActivity().application
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentAugmentedRealityBinding.inflate(inflater)

        val config = ArchitectStartupConfiguration()
        config.licenseKey = getString(R.string.wikitude_access_token)
        binding.architectView.onCreate(config)
        binding.architectView.onPostCreate()
        binding.architectView.load("index.html")

        viewModel.userLocation.observe(viewLifecycleOwner) {
            it?.let {
                binding.architectView.setLocation(
                    it.latitude,
                    it.longitude,
                    it.altitude,
                    it.accuracy
                )
            }
        }

        viewModel.placeList.observe(viewLifecycleOwner) {
            it?.let {
                val gson = Gson()
                val placeList = gson.toJson(it)
                binding.architectView.callJavascript("World.getPlacesFromDataBase(${placeList})")
            }
        }

        viewModel.placeDetailNavigation.observe(viewLifecycleOwner) {
            it?.let {
                findNavController().navigate(
                    FragmentAugmentedRealityDirections.actionFragmentAugmentedRealityToFragmentPlaceDetails(
                        it
                    )
                )
                viewModel.placeDetailNavigationDone()
            }
        }

        viewModel.rangeValueSlider.observe(viewLifecycleOwner) {
            it?.let {
                binding.architectView.callJavascript("World.sliderValueRange($it)")
            }
        }

        binding.architectView.addArchitectJavaScriptInterfaceListener {
            it?.let {
                val placeId = it.getInt("id")
                viewModel.getPlaceForId(placeId)
            }
        }

        binding.sliderRange.addOnChangeListener { _, value, _ ->
            viewModel.onSliderValueChanged(value)
        }

        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onResume() {
        binding.architectView.onResume()
        super.onResume()
    }

    override fun onPause() {
        binding.architectView.onPause()
        super.onPause()
    }

    override fun onDestroyView() {
        binding.architectView.onDestroy()
        super.onDestroyView()
    }

    override fun onLowMemory() {
        binding.architectView.onLowMemory()
        super.onLowMemory()
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
                binding.architectView.callJavascript("World.closePanel()")
                if (listString[item] == resources.getString(R.string.all)) {
                    viewModel.getListPlacesForCategory("")
                } else {
                    viewModel.getListPlacesForCategory(listString[item].lowercase())
                }
            }
            .show()
    }
}