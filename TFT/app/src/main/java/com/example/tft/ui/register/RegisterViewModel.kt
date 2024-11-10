package com.example.tft.ui.register

import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tft.data.services.Authentication.AuthenticationServices
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.launch

class RegisterViewModel : ViewModel() {
    private val _registrationState = MutableLiveData<RegistrationState>(RegistrationState.Idle)
    val registrationState: LiveData<RegistrationState> get() = _registrationState

    // LiveData for validation errors
    private val _emailError = MutableLiveData<String?>()
    val emailError: LiveData<String?> get() = _emailError

    private val _passwordError = MutableLiveData<String?>()
    val passwordError: LiveData<String?> get() = _passwordError

    private val _usernameError = MutableLiveData<String?>()
    val usernameError: LiveData<String?> get() = _usernameError

    private val _passwordMismatchError = MutableLiveData<String?>()
    val passwordMismatchError: LiveData<String?> get() = _passwordMismatchError

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

    // Function to validate that passwords match
    fun validatePasswordsMatch(password: String, repeatPassword: String): Boolean {
        return if (password != repeatPassword) {
            _passwordMismatchError.value = "Las contraseñas no coinciden"
            false
        } else {
            _passwordMismatchError.value = null
            true
        }
    }

    // Function to validate password requirements
    fun validatePassword(password: String): Boolean {
        if (password.isBlank()) {
            _passwordError.value = "La contraseña no puede estar vacía"
            return false
        }
        if (password.length < 6) {
            _passwordError.value = "La contraseña debe tener al menos 6 caracteres"
            return false
        }
        if (!password.any { it.isUpperCase() }) {
            _passwordError.value = "La contraseña debe contener al menos una letra mayúscula"
            return false
        }
        if (!password.any { it.isDigit() }) {
            _passwordError.value = "La contraseña debe contener al menos un número"
            return false
        }
        _passwordError.value = null
        return true
    }

    // Function to validate username
    fun validateUsername(username: String): Boolean {
        return if (username.isBlank()) {
            _usernameError.value = "El nombre de usuario no puede estar vacío"
            false
        } else {
            _usernameError.value = null
            true
        }
    }

    fun register(email: String, password: String, repeatPassword: String, name: String, image: String) {
        val isEmailValid = validateEmail(email)
        val isPasswordValid = validatePassword(password)
        val isUsernameValid = validateUsername(name)
        val doPasswordsMatch = validatePasswordsMatch(password, repeatPassword)

        if (isEmailValid && isPasswordValid && isUsernameValid && doPasswordsMatch) {
            viewModelScope.launch {
                _registrationState.value = RegistrationState.Loading
                try {
                    val user = AuthenticationServices.register(email, password, name, image)
                    _registrationState.value = if (user != null) {
                        RegistrationState.Success(user)
                    } else {
                        RegistrationState.Failure("El registro falló")
                    }
                } catch (e: FirebaseAuthUserCollisionException) {
                    _registrationState.value = RegistrationState.Failure("El correo electrónico ya está registrado")
                } catch (e: Exception) {
                    _registrationState.value = RegistrationState.Failure("El registro falló")
                }
            }
        }
    }

    sealed class RegistrationState {
        object Idle : RegistrationState()
        object Loading : RegistrationState()
        data class Success(val user: FirebaseUser) : RegistrationState()
        data class Failure(val errorMessage: String) : RegistrationState()
    }
}
