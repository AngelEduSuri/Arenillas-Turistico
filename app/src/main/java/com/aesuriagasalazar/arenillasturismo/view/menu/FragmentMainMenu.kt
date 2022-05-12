package com.aesuriagasalazar.arenillasturismo.view.menu

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.aesuriagasalazar.arenillasturismo.R
import com.aesuriagasalazar.arenillasturismo.databinding.FragmentMainMenuBinding

class FragmentMainMenu : Fragment() {

    private lateinit var binding: FragmentMainMenuBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentMainMenuBinding.inflate(inflater)

        binding.menuFragment = this

        setHasOptionsMenu(true)
        return binding.root
    }

    fun onNavigatePlaceList() {
        findNavController().navigate(FragmentMainMenuDirections.actionMainMenuToCategoryList())
    }

    fun onNavigatePlaceMap()  {
        findNavController().navigate(FragmentMainMenuDirections.actionMainMenuToFragmentMapList())
    }

    fun onNavigateGallery() {
        findNavController().navigate(FragmentMainMenuDirections.actionMainMenuToFragmentGallery())
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_share, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.share -> {
                Toast.makeText(context, "Compartiendo enlace", Toast.LENGTH_SHORT).show()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}