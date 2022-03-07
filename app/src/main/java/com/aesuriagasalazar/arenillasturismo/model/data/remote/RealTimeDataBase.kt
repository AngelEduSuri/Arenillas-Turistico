package com.aesuriagasalazar.arenillasturismo.model.data.remote

import com.aesuriagasalazar.arenillasturismo.model.domain.Place
import com.google.firebase.database.*
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

private const val PATH = "sitios"

class RealTimeDataBase {

    private val database = FirebaseDatabase.getInstance()

    private suspend fun DatabaseReference.singleValueEvent() = suspendCancellableCoroutine<EventResponse> { continuation ->
        val valueEventListener = object: ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                continuation.resume(EventResponse.Cancelled(error))
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                continuation.resume(EventResponse.Changed(snapshot))
            }
        }
        addListenerForSingleValueEvent(valueEventListener) // Subscribe to the event
        continuation.invokeOnCancellation { removeEventListener(valueEventListener) }
    }

    suspend fun firebaseOnCoroutine(): ResponseFirebase {
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
        return response
    }
}

sealed class EventResponse {
    data class Changed(val snapshot: DataSnapshot): EventResponse()
    data class Cancelled(val error: DatabaseError): EventResponse()
}

data class ResponseFirebase(
    var places: List<Place> = emptyList(),
    var error: String? = ""
)

