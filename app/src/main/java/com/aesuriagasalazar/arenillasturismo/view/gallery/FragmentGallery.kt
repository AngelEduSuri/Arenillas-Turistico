package com.aesuriagasalazar.arenillasturismo.view.gallery

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.aesuriagasalazar.arenillasturismo.databinding.FragmentGalleryBinding
import com.aesuriagasalazar.arenillasturismo.model.data.local.LocalRepository
import com.aesuriagasalazar.arenillasturismo.model.data.local.PlacesDatabase
import com.aesuriagasalazar.arenillasturismo.viewmodel.GalleryViewModel
import com.aesuriagasalazar.arenillasturismo.viewmodel.GalleryViewModelFactory

class FragmentGallery : Fragment() {

    private lateinit var binding: FragmentGalleryBinding
    private val viewModel: GalleryViewModel by viewModels {
        GalleryViewModelFactory(LocalRepository(PlacesDatabase.getDatabase(requireContext()).placeDao))
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentGalleryBinding.inflate(inflater)

        val adapter = AdapterGallery(ImageClickListener {
            viewModel.onNavigateFullScreen(it)
        })

        viewModel.imageGalleryList.observe(viewLifecycleOwner) {
            it?.let {
                adapter.gallery = it
                adapter.notifyDataSetChanged()
            }
        }

        viewModel.navigateImageFullScreen.observe(viewLifecycleOwner) {
            it?.let {
                if (it.isNotEmpty()) {
                    findNavController().navigate(
                        FragmentGalleryDirections.actionFragmentGalleryToFragmentImageFullScreen(
                            it
                        )
                    )
                    viewModel.onNavigateFullScreenDone()
                }
            }
        }

        binding.recyclerGallery.layoutManager = GridLayoutManager(requireContext(), 3)
        binding.recyclerGallery.adapter = adapter
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }
}