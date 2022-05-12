package com.aesuriagasalazar.arenillasturismo.view.augmentedreality

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.aesuriagasalazar.arenillasturismo.R
import com.aesuriagasalazar.arenillasturismo.databinding.FragmentAugmentedRealityDetailBinding
import com.aesuriagasalazar.arenillasturismo.model.data.local.LocalRepository
import com.aesuriagasalazar.arenillasturismo.model.data.local.PlacesDatabase
import com.aesuriagasalazar.arenillasturismo.viewmodel.AugmentedRealityViewModel
import com.aesuriagasalazar.arenillasturismo.viewmodel.AugmentedRealityViewModelFactory
import com.google.gson.Gson
import com.wikitude.architect.ArchitectStartupConfiguration

class FragmentAugmentedRealityDetail : Fragment() {

    private lateinit var binding: FragmentAugmentedRealityDetailBinding
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
        binding = FragmentAugmentedRealityDetailBinding.inflate(inflater)

        val place = FragmentAugmentedRealityDetailArgs.fromBundle(requireArguments()).place

        val config = ArchitectStartupConfiguration()
        config.licenseKey = getString(R.string.wikitude_access_token)
        binding.architectViewDetail.onCreate(config)
        binding.architectViewDetail.onPostCreate()
        binding.architectViewDetail.load("indexdetailsar.html")

       viewModel.userLocation.observe(viewLifecycleOwner) {
            it?.let {
                binding.architectViewDetail.setLocation(
                    it.latitude,
                    it.longitude,
                    it.altitude,
                    it.accuracy
                )
            }
        }

        val gson = Gson()
        val placeJson = gson.toJson(place)
        binding.architectViewDetail.callJavascript("World.getPlacesFromNative($placeJson)")

        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onResume() {
        binding.architectViewDetail.onResume()
        super.onResume()
    }

    override fun onPause() {
        binding.architectViewDetail.onPause()
        super.onPause()
    }

    override fun onDestroyView() {
        binding.architectViewDetail.onDestroy()
        super.onDestroyView()
    }

    override fun onLowMemory() {
        binding.architectViewDetail.onLowMemory()
        super.onLowMemory()
    }
}