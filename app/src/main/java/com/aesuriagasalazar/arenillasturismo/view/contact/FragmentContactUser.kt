package com.aesuriagasalazar.arenillasturismo.view.contact

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.aesuriagasalazar.arenillasturismo.R
import com.aesuriagasalazar.arenillasturismo.databinding.FragmentContactUserBinding
import com.aesuriagasalazar.arenillasturismo.model.data.remote.FirestoreDataBase
import com.aesuriagasalazar.arenillasturismo.model.data.remote.RemoteRepository
import com.aesuriagasalazar.arenillasturismo.model.network.NetworkStatus
import com.aesuriagasalazar.arenillasturismo.viewmodel.ContactUserViewModel
import com.aesuriagasalazar.arenillasturismo.viewmodel.ContactUserViewModelFactory
import com.aesuriagasalazar.arenillasturismo.viewmodel.MessageState
import com.google.android.material.snackbar.Snackbar

class FragmentContactUser : Fragment() {

    private lateinit var binding: FragmentContactUserBinding
    private val viewModel: ContactUserViewModel by viewModels {
        ContactUserViewModelFactory(
            RemoteRepository(
                null,
                null,
                FirestoreDataBase()
            ),
            NetworkStatus(requireContext())
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentContactUserBinding.inflate(layoutInflater)

        viewModel.onContactUserEvent.observe(viewLifecycleOwner) {
            it?.let {
                snackBarState(it.contactUiState)
                if (it.emailProvider) {
                    openAppEmailProvider()
                }
            }
        }

        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    private fun snackBarState(state: MessageState) {
        when (state) {
            MessageState.FieldEmpty -> {
                showSnackBarMessage(resources.getString(R.string.message_field_empty))
            }
            MessageState.NoNetwork -> {
                showSnackBarMessage(resources.getString(R.string.no_network_message))
            }
            is MessageState.Done -> {
                showSnackBarMessage(resources.getString(R.string.message_field_send, state.messageId))
                viewModel.onSendMessageDone()
            }
            is MessageState.Error -> {
                showSnackBarMessage(resources.getString(R.string.message_field_failure, state.messageError))
            }
            else -> {}
        }
    }

    private fun showSnackBarMessage(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG).show()
    }

    private fun openAppEmailProvider() {
        val intent = Intent(Intent.ACTION_SEND).apply {
            putExtra(Intent.EXTRA_EMAIL, arrayOf(resources.getString(R.string.my_email)))
            type = "message/rfc822"
        }
        startActivity(Intent.createChooser(intent, resources.getString(R.string.email_choise)))
        viewModel.onSendMessageDone()
    }

    override fun onStop() {
        super.onStop()
        viewModel.onShowMessageSnackBarDisable()
    }
}