package com.aesuriagasalazar.arenillasturismo.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.aesuriagasalazar.arenillasturismo.model.data.local.PlaceDao
import com.aesuriagasalazar.arenillasturismo.model.data.local.asEntityModel
import com.aesuriagasalazar.arenillasturismo.model.data.remote.RealTimeDataBase
import com.aesuriagasalazar.arenillasturismo.model.domain.Place
import com.aesuriagasalazar.arenillasturismo.model.domain.asDomainModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class Repository(
    private val realTimeDataBase: RealTimeDataBase,
    private val localDataBase: PlaceDao
) {

    /** Variable observable que se actualiza automaticamente de la base de datos **/
    val places: LiveData<List<Place>> = Transformations.map(localDataBase.getPlaces()) {
        it.asDomainModel()
    }

    /** Obtengo la lista de firebase y la guardo en local **/
    suspend fun getListPlaces() {
        withContext(Dispatchers.IO) {
            val response = realTimeDataBase.firebaseOnCoroutine()
            localDataBase.savePlaces(response.places.asEntityModel())
        }
    }
}
