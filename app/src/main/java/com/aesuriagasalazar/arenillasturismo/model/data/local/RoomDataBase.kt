package com.aesuriagasalazar.arenillasturismo.model.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.aesuriagasalazar.arenillasturismo.model.domain.PlaceEntityDb

@Database(entities = [PlaceEntityDb::class], version = 1, exportSchema = false)
abstract class PlacesDatabase: RoomDatabase() {

    abstract val placeDao: PlaceDao

    companion object{

        private lateinit var INSTANCE: PlacesDatabase

        /**
         * @param context se usar para crear la base de datos room usando el contexto de la app
         */
        fun getDatabase(context: Context): PlacesDatabase {
            /** Se mapea la clase databse y se everigua si INSTANCE es instanciada o no **/
            synchronized(PlacesDatabase::class.java) {
                if (!::INSTANCE.isInitialized) {
                    INSTANCE = Room.databaseBuilder(context.applicationContext,
                        PlacesDatabase::class.java,
                        "places").build()
                }
            }
            /** Se retorna la instancia si ya existe y si no se crea **/
            return INSTANCE
        }
    }
}

