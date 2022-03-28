package com.aesuriagasalazar.arenillasturismo

import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import com.google.android.material.appbar.MaterialToolbar

class MainActivity : AppCompatActivity() {

    private lateinit var navHostFragment: NavHostFragment
    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var appBar: MaterialToolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        /** Configura la barra superior material3 con el componente de navegacion **/
        navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController
        appBarConfiguration = AppBarConfiguration(navController.graph)
        appBar = findViewById(R.id.topAppBar)
        setSupportActionBar(appBar)
        NavigationUI.setupWithNavController(appBar, navController, appBarConfiguration)
    }

    override fun onSupportNavigateUp() = navController.navigateUp()
}