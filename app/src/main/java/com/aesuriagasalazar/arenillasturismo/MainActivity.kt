package com.aesuriagasalazar.arenillasturismo

import android.content.pm.ActivityInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import com.aesuriagasalazar.arenillasturismo.model.domain.Place
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {

    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        NavigationUI.setupActionBarWithNavController(
            this,
            this.findNavController(R.id.nav_host_fragment)
        )

        getDataFromFirebase()
    }

    override fun onSupportNavigateUp() = this.findNavController(R.id.nav_host_fragment).navigateUp()


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