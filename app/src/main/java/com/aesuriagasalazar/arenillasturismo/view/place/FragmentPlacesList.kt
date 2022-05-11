package com.aesuriagasalazar.arenillasturismo.view.place

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.aesuriagasalazar.arenillasturismo.R
import com.aesuriagasalazar.arenillasturismo.databinding.FragmentPlacesListBinding
import com.aesuriagasalazar.arenillasturismo.model.data.local.LocalRepository
import com.aesuriagasalazar.arenillasturismo.model.data.local.PlacesDatabase
import com.aesuriagasalazar.arenillasturismo.viewmodel.PlaceListViewModel
import com.aesuriagasalazar.arenillasturismo.viewmodel.PlaceListViewModelFactory
import com.google.android.material.snackbar.Snackbar

class FragmentPlacesList : Fragment() {

    private lateinit var binding: FragmentPlacesListBinding
    private lateinit var viewModel: PlaceListViewModel
    private lateinit var viewModelFactory: PlaceListViewModelFactory

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        /** Inflado de la vista por data binding **/
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_places_list,
            container,
            false
        )

        /** El factory permite instanciar el viewmodel **/
        viewModelFactory = PlaceListViewModelFactory(
            LocalRepository(PlacesDatabase.getDatabase(requireActivity().application).placeDao),
            FragmentPlacesListArgs.fromBundle(requireArguments()).category
        )
        viewModel = ViewModelProvider(this, viewModelFactory)[PlaceListViewModel::class.java]

        /** Configuracion del recyclerview **/
        val manager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        val adapter = AdapterPlaceList(PlaceClickListener {
            findNavController()
                .navigate(
                    FragmentPlacesListDirections.actionFragmentPlacesListToFragmentPlaceDetails(
                        it
                    )
                )
        })

        /** Paso el viewmodel y el lifecycle a la vista xml para el data binding **/
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        binding.recyclerView.layoutManager = manager
        binding.recyclerView.adapter = adapter

        /** Observable que obtiene a lista del viewmodel y lo pasa al adapter para el recyclerview **/
        viewModel.categoryList.observe(viewLifecycleOwner) {
            it?.let {
                adapter.listPlaces = it
                adapter.notifyDataSetChanged()
            }
        }

        viewModel.error.observe(viewLifecycleOwner) {
            it?.let {
                if (it.isNotEmpty()) {
                    Snackbar.make(binding.root, it, Snackbar.LENGTH_LONG).show()
                    findNavController().popBackStack()
                    viewModel.clearError()
                }
            }
        }

        return binding.root
    }
}