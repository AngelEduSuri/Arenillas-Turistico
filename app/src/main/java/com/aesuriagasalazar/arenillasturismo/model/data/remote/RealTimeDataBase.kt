package com.aesuriagasalazar.arenillasturismo.model.data.remote

import com.aesuriagasalazar.arenillasturismo.model.domain.Place
import com.google.firebase.database.*
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

private const val PATH = "sitios"

class RealTimeDataBase {

    private val database = FirebaseDatabase.getInstance()

    /** Se convierte las callback de firebase en funciones de suspension para corrutinas **/
    private suspend fun DatabaseReference.singleValueEvent() = suspendCancellableCoroutine<EventResponse> { continuation ->
        val valueEventListener = object: ValueEventListener {
            /**
             * La respuesta de las callback se pasan a las sealed class
             * @param error recibe la respuesta de error de firebase y se pasa a la sealed class
             * */
            override fun onCancelled(error: DatabaseError) {
                continuation.resume(EventResponse.Cancelled(error))
            }

            /**
             * @param snapshot se recibe la lista de firebase y se pasa a la sealed class
             */
            override fun onDataChange(snapshot: DataSnapshot) {
                continuation.resume(EventResponse.Changed(snapshot))
            }
        }
        addListenerForSingleValueEvent(valueEventListener) // Se suscribe para los eventos
        continuation.invokeOnCancellation { removeEventListener(valueEventListener) }
    }

    /** Se mapea la respuesta de firebase en la data class ResponseFirebase **/
    suspend fun getDataFromFirebaseOnCoroutine(): ResponseFirebase {
        val response = ResponseFirebase()
        when (val result = database.getReference(PATH).singleValueEvent()) {
            is EventResponse.Changed -> {
                val snapshot = result.snapshot
                if (snapshot.exists()) {
                    val placesDatabase = snapshot.children.mapNotNull {
                        it.getValue(Place::class.java)
                    }.toList()
                    response.places = placesDatabase
                }
            }
            is EventResponse.Cancelled -> {
                val message = result.error.toException().message
                response.error = message
            }
        }
        /**
         * @return response devuelve la respuesta de firebase mapeada en la data class ResponseFirebase
         */
        return response
    }

    suspend fun getChildCountFromFirebaseOnCoroutine(): Int {
        var count = 0
        when (val result = database.getReference(PATH).singleValueEvent()) {
            is EventResponse.Changed -> {
                count = result.snapshot.childrenCount.toInt()
            }
            is EventResponse.Cancelled -> {}
        }
        return count
    }
}

/** Clase sellada que representa las callbacks de firebase
 * @property Changed se mapea el snapshot de  firebase
 * @property Cancelled se mapea el error de firebase
 * **/
sealed class EventResponse {
    data class Changed(val snapshot: DataSnapshot): EventResponse()
    data class Cancelled(val error: DatabaseError): EventResponse()
}

/** Clase de datos que representa la respuesta de firebase
 * @property places lista de todos los lugares si existe un error se mapea una lista vicia
 * @property error si existe un error en la consulta se mapea caso contrario se coloca una cadena vacia
 * **/
data class ResponseFirebase(
    var places: List<Place> = emptyList(),
    var error: String? = ""
)

