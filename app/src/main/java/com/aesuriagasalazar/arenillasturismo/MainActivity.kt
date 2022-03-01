package com.aesuriagasalazar.arenillasturismo

import android.content.pm.ActivityInfo
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import com.aesuriagasalazar.arenillasturismo.model.domain.Place
import com.google.android.material.appbar.MaterialToolbar
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {

    private lateinit var database: DatabaseReference
    private lateinit var navHostFragment: NavHostFragment
    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var appBar: MaterialToolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        /** Configurar la barra superior material3 con el componente de navegacion **/
        navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController
        appBarConfiguration = AppBarConfiguration(navController.graph)
        appBar = findViewById(R.id.topAppBar)
        setSupportActionBar(appBar)
        NavigationUI.setupWithNavController(appBar, navController, appBarConfiguration)

        getDataFromFirebase()
    }

    override fun onSupportNavigateUp() = navController.navigateUp()


    private fun getDataFromFirebase() {
        database = Firebase.database.getReference(getString(R.string.list_places))

        val places = mutableListOf<Place>()

        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                places.clear()
                for (placeSnapshot in dataSnapshot.children) {
                    val product = placeSnapshot.getValue(Place::class.java)
                    product?.let { places.add(it) }
                }

                places.forEach {
                    Log.i("leer", it.nombre)
                }
                Log.i("leer", "Tamaño de lista: ${places.size}")
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                Log.i("leer", "loadPost:onCancelled", databaseError.toException())
            }
        }
        database.addValueEventListener(postListener)
    }
}