package com.aesuriagasalazar.arenillasturismo.view.place

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.aesuriagasalazar.arenillasturismo.R
import com.aesuriagasalazar.arenillasturismo.databinding.FragmentPlacesListBinding
import com.aesuriagasalazar.arenillasturismo.model.Repository
import com.aesuriagasalazar.arenillasturismo.model.data.local.PlacesDatabase
import com.aesuriagasalazar.arenillasturismo.model.data.remote.RealTimeDataBase
import com.aesuriagasalazar.arenillasturismo.viewmodel.PlaceListViewModel
import com.aesuriagasalazar.arenillasturismo.viewmodel.PlaceListViewModelFactory

class FragmentPlacesList : Fragment() {

    private lateinit var binding: FragmentPlacesListBinding
    private lateinit var viewModel: PlaceListViewModel
    private lateinit var viewModelFactory: PlaceListViewModelFactory

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_places_list,
            container,
            false
        )

        viewModelFactory = PlaceListViewModelFactory(
            Repository(RealTimeDataBase(), PlacesDatabase.getDatabase(activity?.applicationContext!!).videoDao),
            FragmentPlacesListArgs.fromBundle(requireArguments()).category
        )
        viewModel = ViewModelProvider(this, viewModelFactory)[PlaceListViewModel::class.java]

        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        viewModel.places.observe(viewLifecycleOwner) {
            it?.let {
                Log.i("leer", "Fragment: ${it.size}")
            }
        }

        return binding.root
    }
}