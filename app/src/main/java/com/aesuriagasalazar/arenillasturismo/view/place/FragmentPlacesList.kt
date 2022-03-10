package com.aesuriagasalazar.arenillasturismo.view.place

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
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

        /** El factory permite instanciar el viewmodel y mantener la instancia **/
        viewModelFactory = PlaceListViewModelFactory(
            Repository(RealTimeDataBase(), PlacesDatabase.getDatabase(activity?.applicationContext!!).videoDao),
            FragmentPlacesListArgs.fromBundle(requireArguments()).category
        )
        viewModel = ViewModelProvider(this, viewModelFactory)[PlaceListViewModel::class.java]

        /** Configuracion del recyclerview **/
        val manager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        val adapter = AdapterPlaceList(PlaceClickListener {
            Toast.makeText(context, "Click en ${it.nombre}, yendo a la pagina detalle", Toast.LENGTH_SHORT).show()
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