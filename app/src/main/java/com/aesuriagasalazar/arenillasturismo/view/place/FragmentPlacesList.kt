package com.aesuriagasalazar.arenillasturismo.view.place

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.aesuriagasalazar.arenillasturismo.databinding.FragmentPlacesListBinding
import com.aesuriagasalazar.arenillasturismo.model.data.local.LocalRepository
import com.aesuriagasalazar.arenillasturismo.model.data.local.PlacesDatabase
import com.aesuriagasalazar.arenillasturismo.viewmodel.PlaceListViewModel
import com.aesuriagasalazar.arenillasturismo.viewmodel.PlaceListViewModelFactory

class FragmentPlacesList : Fragment() {

    private lateinit var binding: FragmentPlacesListBinding

    private val viewModel: PlaceListViewModel by viewModels {
        /** El factory permite instanciar el viewmodel con parametros **/
        PlaceListViewModelFactory(
            LocalRepository(PlacesDatabase.getDatabase(requireActivity().application).placeDao),
        )
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        /** Inflado de la vista por data binding **/
        binding = FragmentPlacesListBinding.inflate(inflater)

        viewModel.loadDataFromRepository(FragmentPlacesListArgs.fromBundle(requireArguments()).category)

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

        return binding.root
    }
}