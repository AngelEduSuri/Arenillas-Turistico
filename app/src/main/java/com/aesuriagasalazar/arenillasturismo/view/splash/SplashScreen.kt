package com.aesuriagasalazar.arenillasturismo.view.splash

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.aesuriagasalazar.arenillasturismo.MainActivity
import com.aesuriagasalazar.arenillasturismo.databinding.ActivitySplashScreenBinding
import com.aesuriagasalazar.arenillasturismo.model.data.local.PlacesDatabase
import com.aesuriagasalazar.arenillasturismo.model.data.remote.RealTimeDataBase
import com.aesuriagasalazar.arenillasturismo.model.data.remote.RemoteRepository
import com.aesuriagasalazar.arenillasturismo.model.network.NetworkStatus
import com.aesuriagasalazar.arenillasturismo.viewmodel.SplashViewModel
import com.aesuriagasalazar.arenillasturismo.viewmodel.SplashViewModelFactory

@SuppressLint("CustomSplashScreen")
class SplashScreen : AppCompatActivity() {

    private val viewModel: SplashViewModel by viewModels {
        SplashViewModelFactory(
            RemoteRepository(
                RealTimeDataBase(),
                PlacesDatabase.getDatabase(application).placeDao,
                null
            ),
            NetworkStatus(this)
        )
    }

    private lateinit var binding: ActivitySplashScreenBinding

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        super.onCreate(savedInstanceState)
        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel.navigateMenu.observe(this) {
            it?.let {
                if (it) {
                    startActivity(Intent(this, MainActivity::class.java))
                    viewModel.onNavigateMenuDone()
                    finish()
                }
            }
        }

        binding.lifecycleOwner = this
        binding.viewModel = viewModel
    }
}