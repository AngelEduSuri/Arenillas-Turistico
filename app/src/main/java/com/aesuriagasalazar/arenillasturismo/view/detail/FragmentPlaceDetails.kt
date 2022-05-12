package com.aesuriagasalazar.arenillasturismo.view.detail

import android.graphics.text.LineBreaker
import android.os.Build
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.aesuriagasalazar.arenillasturismo.R
import com.aesuriagasalazar.arenillasturismo.databinding.FragmentPlaceDetailsBinding
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType
import com.smarteist.autoimageslider.SliderAnimations

class FragmentPlaceDetails : Fragment() {

    private lateinit var binding: FragmentPlaceDetailsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentPlaceDetailsBinding.inflate(inflater)

        val place = FragmentPlaceDetailsArgs.fromBundle(requireArguments()).place
        val adapter = AdapterSliderImage(place.imagenes + place.miniatura)

        binding.place = place
        binding.lifecycleOwner = viewLifecycleOwner
        binding.imageSlider.setSliderAdapter(adapter)
        binding.imageSlider.setIndicatorAnimation(IndicatorAnimationType.WORM)
        binding.imageSlider.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION)
        binding.imageSlider.startAutoCycle()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                binding.bodyDescription.justificationMode =
                    LineBreaker.JUSTIFICATION_MODE_INTER_WORD
            }
        }

        setHasOptionsMenu(true)

        binding.buttonAr.setOnClickListener {
            findNavController().navigate(
                FragmentPlaceDetailsDirections.actionFragmentPlaceDetailsToFragmentAugmentedRealityDetail(
                    place
                )
            )
        }
        return binding.root
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
}