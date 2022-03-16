package com.aesuriagasalazar.arenillasturismo.model

import android.util.Log
import com.aesuriagasalazar.arenillasturismo.model.data.local.PlaceDao
import com.aesuriagasalazar.arenillasturismo.model.data.local.asEntityModel
import com.aesuriagasalazar.arenillasturismo.model.data.remote.RealTimeDataBase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class Repository(
    private val realTimeDataBase: RealTimeDataBase,
    private val localDataBase: PlaceDao
) {
//
//    /** Variable observable que se actualiza automaticamente de la base de datos **/
//    val places: LiveData<List<Place>> = Transformations.map(localDataBase.getPlaces()) {
//        it.asDomainModel()
//    }

    /** Obtengo la lista de firebase y la guardo en local **/
    suspend fun loadPlacesFromDataSourceRemote(): String? {
        val error = withContext(Dispatchers.IO) {
            val response = realTimeDataBase.firebaseOnCoroutine()
            val error = response.error
            localDataBase.savePlaces(response.places.asEntityModel())
            return@withContext error
        }
        return error
    }

    /** Averiguo si la base de datos local tiene algun registro **/
    suspend fun getItemsCount() = localDataBase.getListCount()

    /** Consulto la lista de lugares por su categoria **/
    suspend fun getPlacesCategory(category: String) = localDataBase.getPlacesForCategory(category)
}
