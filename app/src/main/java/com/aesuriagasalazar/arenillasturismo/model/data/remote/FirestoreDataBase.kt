package com.aesuriagasalazar.arenillasturismo.model.data.remote

import com.aesuriagasalazar.arenillasturismo.viewmodel.MessageBody
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

private const val PATH = "users"

class FirestoreDataBase {

    private val dataBase = Firebase.firestore

    suspend fun setMessageOnFirestore(messageBody: MessageBody): ResponseFirestore {
        val message = hashMapOf(
            "name" to messageBody.name,
            "email" to messageBody.email,
            "message" to messageBody.message
        )
        return when (val response = dataBase.collection(PATH).setDataOnCoroutine(message)) {
            is FirestoreListener.Failure -> {
                ResponseFirestore(failure = response.failure.message)
            }
            is FirestoreListener.Success -> {
                ResponseFirestore(success = response.document.id)
            }
        }
    }

    private suspend fun CollectionReference.setDataOnCoroutine(data: Map<String, String>) =
        suspendCancellableCoroutine<FirestoreListener> { continuation ->

            val document = this.add(data).apply {
                addOnSuccessListener {
                    continuation.resume(FirestoreListener.Success(it))
                }
                addOnFailureListener {
                    continuation.resume(FirestoreListener.Failure(it))
                }
            }
            continuation.invokeOnCancellation { document.result }
        }
}

sealed class FirestoreListener {
    data class Success(val document: DocumentReference) : FirestoreListener()
    data class Failure(val failure: Exception) : FirestoreListener()
}

data class ResponseFirestore (val success: String = "", val failure: String? = "")