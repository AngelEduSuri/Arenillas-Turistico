package com.aesuriagasalazar.arenillasturismo.view.augmentedreality

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.aesuriagasalazar.arenillasturismo.R
import com.aesuriagasalazar.arenillasturismo.databinding.FragmentAugmentedRealityBinding
import com.aesuriagasalazar.arenillasturismo.viewmodel.AugmentedRealityViewModel
import com.wikitude.architect.ArchitectStartupConfiguration
import com.wikitude.architect.ArchitectView

class FragmentAugmentedReality : Fragment() {

    private lateinit var binding: FragmentAugmentedRealityBinding
    private lateinit var viewModel: AugmentedRealityViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_augmented_reality, container, false)
        viewModel = ViewModelProvider(this)[AugmentedRealityViewModel::class.java]

        if (ArchitectView.isDeviceSupported(requireContext())) {
            Log.i("leer", "Dipositivo Compatible")
            val config = ArchitectStartupConfiguration()
            config.licenseKey = getString(R.string.wikitude_access_token)
            binding.architectView.onCreate(config)
        }
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        binding.architectView.onPostCreate()
        super.onActivityCreated(savedInstanceState)
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