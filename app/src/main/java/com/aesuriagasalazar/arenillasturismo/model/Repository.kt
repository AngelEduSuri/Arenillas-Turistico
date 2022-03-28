package com.aesuriagasalazar.arenillasturismo.model

import com.aesuriagasalazar.arenillasturismo.model.data.local.PlaceDao
import com.aesuriagasalazar.arenillasturismo.model.data.local.asEntityModel
import com.aesuriagasalazar.arenillasturismo.model.data.remote.RealTimeDataBase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/** Clase repositorio para obtener los datos de la base de datos remota y almacenarlo en local
 * @param realTimeDataBase Obtiene la base de datos Firebase
 * @param localDataBase Obtiene la base de datos Room
 */
class Repository(
    private val realTimeDataBase: RealTimeDataBase,
    private val localDataBase: PlaceDao
) {

    /** Funcion de suspension que obtiene la lista de Firebase y se guarda en local **/
    suspend fun loadPlacesFromDataSourceRemote(): String? {
        /** Se ejecuta en una corrutina en otro hilo Dispatchers.IO **/
        val error = withContext(Dispatchers.IO) {
            val response = realTimeDataBase.firebaseOnCoroutine()
            val error = response.error
            localDataBase.savePlaces(response.places.asEntityModel())
            return@withContext error
        }
        return error
    }

    /** Funcion de suspension que consulta si la base de datos Room contiene informacion **/
    suspend fun getItemsCount() = localDataBase.getListCount()

    /** Funcion de suspension que obtiene los lugares por categoria
     * @param category String que llega desde el ViewModel
     **/
    suspend fun getPlacesCategory(category: String) = withContext(Dispatchers.IO) {
        localDataBase.getPlacesForCategory(category)
    }

    suspend fun getAllPlaces() = withContext(Dispatchers.IO) {
        localDataBase.getPlaces()
    }
}
