package com.aesuriagasalazar.arenillasturismo.view.fullscreen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.aesuriagasalazar.arenillasturismo.databinding.FragmentImageFullScreenBinding

class FragmentImageFullScreen : Fragment() {

    private lateinit var binding: FragmentImageFullScreenBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentImageFullScreenBinding.inflate(layoutInflater)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.image = FragmentImageFullScreenArgs.fromBundle(requireArguments()).image
        return binding.root
    }

}