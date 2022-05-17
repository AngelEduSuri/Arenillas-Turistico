package com.aesuriagasalazar.arenillasturismo.viewmodel

import androidx.lifecycle.*
import com.aesuriagasalazar.arenillasturismo.model.data.remote.RemoteRepository
import com.aesuriagasalazar.arenillasturismo.model.network.NetworkStatus
import kotlinx.coroutines.launch

sealed class MessageState {
    object Idle : MessageState()
    object FieldEmpty : MessageState()
    object NoNetwork : MessageState()
    data class Done(val messageId: String) : MessageState()
    data class Error(val messageError: String) : MessageState()
}

class ContactUserViewModel(
    private val repository: RemoteRepository,
    private val network: NetworkStatus
) : ViewModel() {

    val myEmail = "angelsuriaga@hotmail.com"

    private val _onContactUserEvent = MutableLiveData(ContactUserUiState())
    val onContactUserEvent: LiveData<ContactUserUiState> = _onContactUserEvent

    fun onSendEmailProvider() {
        _onContactUserEvent.value = onContactUserEvent.value?.copy(emailProvider = true)
    }

    fun onSendMessageClick() {
        checkNetworkOnDevice()
    }

    /** Funcion que comprueba si existe una conexion a internet **/
    private fun checkNetworkOnDevice() {
        viewModelScope.launch {
            if (network.isNetworkAvailable()) {
                senMessage()
            } else {
                onSendMessageStateResponse(MessageState.NoNetwork)
            }
        }
    }

    /** Funcion que comprueba si los campos obligatorios estan vacios, caso contra envia el mensaje **/
    private suspend fun senMessage() {
        if (onContactUserEvent.value?.email.isNullOrEmpty() || onContactUserEvent.value?.message.isNullOrEmpty()) {
            onSendMessageStateResponse(MessageState.FieldEmpty)
        } else {
            onContactUserEvent.value?.let {
                val messageBody = MessageBody(it.name, it.email, it.message).checkIfNameIsEmpty()
                repository.sendMessageFromUser(messageBody)?.let { response ->
                    if (response.success.isNotEmpty()) {
                        onSendMessageStateResponse(MessageState.Done(response.success))
                    }
                    if (!response.failure.isNullOrEmpty()) {
                        onSendMessageStateResponse(MessageState.Error(response.failure))
                    }
                }
            }
        }
    }

    private fun onSendMessageStateResponse(state: MessageState) {
        val newContactUser = onContactUserEvent.value?.copy(contactUiState = state)
        _onContactUserEvent.value = newContactUser
    }

    fun onShowMessageSnackBarDisable() {
        _onContactUserEvent.value =
            onContactUserEvent.value?.copy(contactUiState = MessageState.Idle)
    }

    fun onSendMessageDone() {
        _onContactUserEvent.value = ContactUserUiState()
    }
}

data class ContactUserUiState(
    var name: String = "",
    var email: String = "",
    var message: String = "",
    val contactUiState: MessageState = MessageState.Idle,
    val emailProvider: Boolean = false,
)

data class MessageBody(
    var name: String,
    var email: String,
    var message: String
) {
    fun checkIfNameIsEmpty() = MessageBody(
        name.ifEmpty { "No Name" },
        email,
        message
    )
}

@Suppress("UNCHECKED_CAST")
class ContactUserViewModelFactory(
    private val repository: RemoteRepository,
    private val network: NetworkStatus
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ContactUserViewModel::class.java)) {
            return ContactUserViewModel(repository, network) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}



