package com.aesuriagasalazar.arenillasturismo.view.category

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.aesuriagasalazar.arenillasturismo.R
import com.aesuriagasalazar.arenillasturismo.databinding.FragmentCategoryListBinding
import com.aesuriagasalazar.arenillasturismo.model.CategoryStatic
import com.aesuriagasalazar.arenillasturismo.model.toStringCategory

class FragmentCategoryList : Fragment() {

    private lateinit var binding: FragmentCategoryListBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentCategoryListBinding.inflate(inflater)

        val manager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        val adapter = AdapterCategoryList(CategoryStatic.getCategories(), CategoryClickListener {
            findNavController()
                .navigate(FragmentCategoryListDirections.actionCategoryListToFragmentPlacesList(
                    it.toStringCategory(resources)))
        })

        binding.lifecycleOwner = viewLifecycleOwner
        binding.recyclerViewCategory.layoutManager = manager
        binding.recyclerViewCategory.adapter = adapter

        return binding.root
    }
}