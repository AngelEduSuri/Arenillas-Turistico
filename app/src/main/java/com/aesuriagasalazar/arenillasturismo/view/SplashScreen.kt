package com.aesuriagasalazar.arenillasturismo.view

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.aesuriagasalazar.arenillasturismo.MainActivity
import com.aesuriagasalazar.arenillasturismo.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@SuppressLint("CustomSplashScreen")
class SplashScreen : AppCompatActivity() {

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        lifecycleScope.launch {
            delay(1500)
            startActivity(Intent(this@SplashScreen, MainActivity::class.java))
            finish()
        }
    }
}