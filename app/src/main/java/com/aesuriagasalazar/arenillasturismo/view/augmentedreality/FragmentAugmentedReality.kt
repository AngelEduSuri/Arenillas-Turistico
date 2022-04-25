package com.aesuriagasalazar.arenillasturismo.view.augmentedreality

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.aesuriagasalazar.arenillasturismo.R
import com.aesuriagasalazar.arenillasturismo.databinding.FragmentAugmentedRealityBinding
import com.aesuriagasalazar.arenillasturismo.model.data.local.LocalRepository
import com.aesuriagasalazar.arenillasturismo.model.data.local.PlacesDatabase
import com.aesuriagasalazar.arenillasturismo.viewmodel.AugmentedRealityViewModel
import com.aesuriagasalazar.arenillasturismo.viewmodel.AugmentedRealityViewModelFactory
import com.google.gson.Gson
import com.wikitude.architect.ArchitectStartupConfiguration

class FragmentAugmentedReality : Fragment() {

    private lateinit var binding: FragmentAugmentedRealityBinding
    private lateinit var viewModel: AugmentedRealityViewModel
    private lateinit var viewModelFactory: AugmentedRealityViewModelFactory

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_augmented_reality, container, false)
        viewModelFactory = AugmentedRealityViewModelFactory(
            LocalRepository(
                PlacesDatabase.getDatabase(requireContext()).placeDao
            ), requireActivity().application
        )
        viewModel = ViewModelProvider(this, viewModelFactory)[AugmentedRealityViewModel::class.java]

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

        viewModel.categoryList.observe(viewLifecycleOwner) {
            it?.let {
                val gson = Gson()
                val place = gson.toJson(it)
                binding.architectView.callJavascript("World.getPlacesFromDataBase(${place})")
            }
        }

        binding.lifecycleOwner = viewLifecycleOwner
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
}