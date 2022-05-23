package com.aesuriagasalazar.arenillasturismo.view.menu

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.aesuriagasalazar.arenillasturismo.R
import com.aesuriagasalazar.arenillasturismo.databinding.FragmentMainMenuBinding
import com.aesuriagasalazar.arenillasturismo.model.data.location.GpsActivate
import com.aesuriagasalazar.arenillasturismo.view.permissions.AugmentedRealityPermissions
import com.wikitude.architect.ArchitectView
import com.wikitude.common.devicesupport.Feature
import java.util.*

class FragmentMainMenu : Fragment() {

    private lateinit var binding: FragmentMainMenuBinding
    private lateinit var augmentedRealityPermissions: AugmentedRealityPermissions

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentMainMenuBinding.inflate(inflater)

        binding.menuFragment = this

        checkAugmentedRealityDeviceSupport()

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

    private fun onNavigateContact() {
        findNavController().navigate(FragmentMainMenuDirections.actionMainMenuToFragmentContactUser())
    }

    fun onNavigateAugmentedReality() {
        augmentedRealityCheckPermissions()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_contact, menu)
    }

    private fun augmentedRealityCheckPermissions() {
        augmentedRealityPermissions = AugmentedRealityPermissions(this)
        if (augmentedRealityPermissions.checkPermissions()) {
            turnOnGpsAndNavigate()
        } else {
            augmentedRealityPermissions.showDialogPermissions { granted ->
                if (granted) turnOnGpsAndNavigate()
            }
        }
    }

    /** Funcion que comprueba que el gps esta activado, si lo es, navega hacia la realdiad aumentada **/
    private fun turnOnGpsAndNavigate() {
        GpsActivate(requireContext()).turnGPSOn {
            if (it) {
                findNavController().navigate(FragmentMainMenuDirections.actionMainMenuToFragmentAugmentedReality())
            }
        }
    }

    /** Funcion que comprueba si wikitude es compatible en el dispositivo y con la funcionalidad GEO **/
    private fun checkAugmentedRealityDeviceSupport() {
        val callStatus = ArchitectView.isDeviceSupporting(requireContext(), EnumSet.of(Feature.GEO))
        if (callStatus.isSuccess) {
            Log.i("leer", "Dipositivo Compatible")
            binding.buttonAugmentedReality.visibility = View.VISIBLE
        } else {
            Log.i("leer", "Dipositivo No Compatible")
            binding.buttonAugmentedReality.visibility = View.GONE
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.contact -> {
                onNavigateContact()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}