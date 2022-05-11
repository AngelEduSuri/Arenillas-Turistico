package com.aesuriagasalazar.arenillasturismo.view.splash

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.aesuriagasalazar.arenillasturismo.MainActivity
import com.aesuriagasalazar.arenillasturismo.R
import com.aesuriagasalazar.arenillasturismo.databinding.ActivitySplashScreenBinding
import com.aesuriagasalazar.arenillasturismo.model.data.local.PlacesDatabase
import com.aesuriagasalazar.arenillasturismo.model.data.remote.RealTimeDataBase
import com.aesuriagasalazar.arenillasturismo.model.data.remote.RemoteRepository
import com.aesuriagasalazar.arenillasturismo.model.network.NetworkStatus
import com.aesuriagasalazar.arenillasturismo.viewmodel.SplashViewModel
import com.aesuriagasalazar.arenillasturismo.viewmodel.SplashViewModelFactory

@SuppressLint("CustomSplashScreen")
class SplashScreen : AppCompatActivity() {

    private lateinit var viewModel: SplashViewModel
    private lateinit var viewModelFactory: SplashViewModelFactory
    private lateinit var binding: ActivitySplashScreenBinding

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_splash_screen)
        viewModelFactory = SplashViewModelFactory(
            RemoteRepository(
                RealTimeDataBase(),
                PlacesDatabase.getDatabase(application).placeDao
            ), NetworkStatus(this)
        )
        viewModel = ViewModelProvider(this, viewModelFactory)[SplashViewModel::class.java]

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