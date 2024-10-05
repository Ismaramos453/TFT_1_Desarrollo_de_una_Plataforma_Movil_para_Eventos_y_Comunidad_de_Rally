package com.example.tft.ui.login


import android.util.Log
import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tft.data.AuthenticationServices
import kotlinx.coroutines.launch



class LoginViewModel : ViewModel() {
    private val _loading = MutableLiveData(false)

    // LiveData for validation errors
    private val _emailError = MutableLiveData<String?>()
    val emailError: LiveData<String?> get() = _emailError

    private val _passwordError = MutableLiveData<String?>()
    val passwordError: LiveData<String?> get() = _passwordError

    // Function to validate email format
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

    // Function to validate password length
    fun validatePassword(password: String): Boolean {
        return if (password.isBlank()) {
            _passwordError.value = "La contraseña no puede estar vacía"
            false
        } else if (password.length < 6) {
            _passwordError.value = "La contraseña debe tener al menos 6 caracteres"
            false
        } else {
            _passwordError.value = null
            true
        }
    }

    fun signInWithEmailAndPassword(email: String, password: String, home: () -> Unit) = viewModelScope.launch {
        if (validateEmail(email) && validatePassword(password)) {
            try {
                val user = AuthenticationServices.signIn(email, password)
                if (user != null) {
                    Log.d("RallyWord", "signInWithEmailAndPassword logueado")
                    home()
                } else {
                    Log.d("RallyWord", "signInWithEmailAndPassword: fallo en la autenticación")
                }
            } catch (ex: Exception) {
                Log.d("RallyWord", "signInWithEmailAndPassword : ${ex.message}")
            }
        }
    }

    fun signInWithGoogle(idToken: String, home: () -> Unit) = viewModelScope.launch {
        try {
            val user = AuthenticationServices.signInWithGoogle(idToken)
            if (user != null) {
                Log.d("RallyWord", "signInWithGoogle logueado")
                home()
            } else {
                Log.d("RallyWord", "signInWithGoogle: fallo en la autenticación")
            }
        } catch (ex: Exception) {
            Log.d("RallyWord", "signInWithGoogle : ${ex.message}")
        }
    }
}



