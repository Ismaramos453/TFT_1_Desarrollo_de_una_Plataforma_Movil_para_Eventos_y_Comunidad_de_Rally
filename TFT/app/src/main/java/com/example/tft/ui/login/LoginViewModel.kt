package com.example.tft.ui.login


import android.util.Log
import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tft.data.services.Authentication.AuthenticationServices
import kotlinx.coroutines.launch


class LoginViewModel : ViewModel() {
    private val _loading = MutableLiveData(false)
    private val _emailError = MutableLiveData<String?>()
    val emailError: LiveData<String?> get() = _emailError
    private val _passwordError = MutableLiveData<String?>()
    val passwordError: LiveData<String?> get() = _passwordError

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
                    // Inicio de sesión exitoso, se limpian los errores.
                    _emailError.value = null
                    _passwordError.value = null
                    Log.d("RallyWord", "signInWithEmailAndPassword logueado")
                    home()
                } else {
                    // En este ejemplo, si user es nulo se asume que el correo existe pero la contraseña es incorrecta.
                    // (En algunas implementaciones, podría ser indistinto)
                    _passwordError.value = "Contraseña incorrecta"
                    _emailError.value = null
                    Log.d("RallyWord", "signInWithEmailAndPassword: Contraseña incorrecta")
                }
            } catch (ex: Exception) {
                Log.d("RallyWord", "signInWithEmailAndPassword: ${ex.message}")
                // Intenta distinguir el error a partir del mensaje
                val errorMessage = ex.message ?: "Error desconocido"
                if (errorMessage.contains("password", ignoreCase = true)) {
                    _passwordError.value = "Contraseña incorrecta"
                    _emailError.value = null
                } else if (errorMessage.contains("user", ignoreCase = true) ||
                    errorMessage.contains("no user", ignoreCase = true)) {
                    _emailError.value = "Correo incorrecto"
                    _passwordError.value = null
                } else {
                    // Si no se puede determinar, asigna el mismo error a ambos campos.
                    _emailError.value = "Error al iniciar sesión: $errorMessage"
                    _passwordError.value = "Error al iniciar sesión: $errorMessage"
                }
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
            Log.d("RallyWord", "signInWithGoogle: ${ex.message}")
        }
    }
}


