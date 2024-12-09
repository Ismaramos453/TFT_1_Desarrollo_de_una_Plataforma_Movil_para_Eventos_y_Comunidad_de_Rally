package com.example.tft.ui.forgotpassword

import android.util.Log
import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tft.data.services.Authentication.AuthenticationServices
import kotlinx.coroutines.launch

class ForgotPasswordViewModel : ViewModel() {
    private val _emailError = MutableLiveData<String?>()
    val emailError: LiveData<String?> get() = _emailError
    fun validateEmail(email: String): Boolean {
        return if (email.isBlank()) {
            _emailError.value = "El correo electrónico no puede estar vacío"
            false
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _emailError.value = "El formato del correo electrónico es inválido"
            false
        } else {
            _emailError.value = null
            true
        }
    }

    fun sendPasswordResetEmail(email: String, onSuccess: () -> Unit) = viewModelScope.launch {
        if (validateEmail(email)) {
            try {
                AuthenticationServices.sendPasswordResetEmail(email)
                onSuccess()
            } catch (ex: Exception) {
                Log.d("RallyWord", "sendPasswordResetEmail: ${ex.message}")
            }
        }
    }
}
