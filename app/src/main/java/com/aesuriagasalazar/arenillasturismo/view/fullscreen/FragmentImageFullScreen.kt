package com.aesuriagasalazar.arenillasturismo.view.fullscreen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
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
        binding.fragment = this
        binding.image = FragmentImageFullScreenArgs.fromBundle(requireArguments()).image

        return binding.root
    }

    fun onBackScreen() {
        findNavController().popBackStack()
    }

    override fun onStart() {
        super.onStart()
        (activity as AppCompatActivity).supportActionBar?.hide()
    }

    override fun onStop() {
        super.onStop()
        (activity as AppCompatActivity).supportActionBar?.show()
    }
}