package com.aesuriagasalazar.arenillasturismo.view.menu

import android.app.Activity.RESULT_CANCELED
import android.app.Activity.RESULT_OK
import android.location.Location
import android.os.Bundle
import android.view.*
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.aesuriagasalazar.arenillasturismo.R
import com.aesuriagasalazar.arenillasturismo.databinding.FragmentMainMenuBinding
import com.aesuriagasalazar.arenillasturismo.model.data.location.GpsActivateManager
import com.aesuriagasalazar.arenillasturismo.model.data.location.UserLocationOnLiveData
import com.aesuriagasalazar.arenillasturismo.view.permissions.AugmentedRealityPermissions
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.wikitude.architect.ArchitectView
import com.wikitude.common.devicesupport.Feature
import java.util.*

class FragmentMainMenu : Fragment() {

    private lateinit var binding: FragmentMainMenuBinding
    private lateinit var augmentedRealityPermissions: AugmentedRealityPermissions
    private lateinit var userLocation: UserLocationOnLiveData
    private val cityPoint by lazy {
        Location(resources.getString(R.string.city_name_provider)).apply {
            latitude = resources.getString(R.string.center_city_latitude).toDouble()
            longitude = resources.getString(R.string.center_city_longitude).toDouble()
        }
    }

    private lateinit var resultForIntentGPS: ActivityResultLauncher<IntentSenderRequest>

    private var distanceToUser: Float = 0.0f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        resultForIntentGPS  = registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) {
            it?.let {
                when(it.resultCode) {
                    RESULT_OK -> { checkIfUserIsInCity() }
                    RESULT_CANCELED -> { showMessageIfNeedGps() }
                }
            }
        }
    }

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

    fun onNavigatePlaceMap() {
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
        GpsActivateManager(requireContext()).turnOnGpsBuilder {
            if (it == null) {
                checkIfUserIsInCity()
            } else {
                resultForIntentGPS.launch(it)
            }
        }
    }

    /** Funcion que comprueba si wikitude es compatible en el dispositivo y con la funcionalidad GEO **/
    private fun checkAugmentedRealityDeviceSupport() {
        val callStatus = ArchitectView.isDeviceSupporting(requireContext(), EnumSet.of(Feature.GEO))
        if (callStatus.isSuccess) {
            binding.buttonAugmentedReality.visibility = View.VISIBLE
        } else {
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

    private fun checkIfUserIsInCity() {
        userLocation = UserLocationOnLiveData(requireContext())

        userLocation.observe(viewLifecycleOwner) {
            it?.let {
                val userPoint = Location("User").apply {
                    latitude = -3.558544307455353
                    longitude = -80.06465242963569
                }

                distanceToUser = cityPoint.distanceTo(userPoint)
                if (distanceToUser < resources.getString(R.string.max_distance_augmented_reality)
                        .toFloat()
                ) {
                    navigateToAugmentedReality()
                } else {
                    userOutOfCity()
                }
                userLocation.removeObservers(viewLifecycleOwner)
            }
        }
    }

    private fun navigateToAugmentedReality() {
        findNavController().navigate(FragmentMainMenuDirections.actionMainMenuToFragmentAugmentedReality())
    }

    private fun userOutOfCity() {
        dialogBuilderMessage(R.string.user_out_of_city, R.string.augmented_reality_context){}
    }

    private fun showMessageIfNeedGps() {
        dialogBuilderMessage(R.string.gps_is_required, R.string.gps_context_augmented_reality){
            augmentedRealityCheckPermissions()
        }
    }

    private fun dialogBuilderMessage(title: Int, message: Int, onPositiveButton: () -> Unit ) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(resources.getString(title))
            .setMessage(resources.getString(message))
            .setPositiveButton(R.string.accept) { _, _ -> onPositiveButton() }
            .setNegativeButton(R.string.cancel) { _, _ -> }
            .show()
    }
}