package com.example.tft.ui.ChangePassword



import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth

class ChangePasswordViewModel : ViewModel() {
    private val _isLoading = MutableLiveData<Boolean>()
    private val _errorMessage = MutableLiveData<String>()

    val isLoading: LiveData<Boolean> get() = _isLoading
    val errorMessage: LiveData<String> get() = _errorMessage

    fun changePassword(
        currentPassword: String,
        newPassword: String,
        confirmPassword: String,
        onSuccess: () -> Unit
    ) {
        // Reiniciar mensaje de error previo
        _errorMessage.value = ""

        // Validar la nueva contraseña
        val validationError = validateNewPassword(newPassword, confirmPassword)
        if (validationError != null) {
            _errorMessage.value = validationError
            return
        }

        _isLoading.value = true

        val user = FirebaseAuth.getInstance().currentUser
        if (user != null && user.email != null) {
            val credential = EmailAuthProvider.getCredential(user.email!!, currentPassword)

            // Re-autenticar al usuario
            user.reauthenticate(credential)
                .addOnCompleteListener { authTask ->
                    if (authTask.isSuccessful) {
                        // Actualizar la contraseña
                        user.updatePassword(newPassword)
                            .addOnCompleteListener { updateTask ->
                                _isLoading.value = false
                                if (updateTask.isSuccessful) {
                                    _errorMessage.value = ""
                                    onSuccess()
                                } else {
                                    _errorMessage.value = "Error al actualizar la contraseña: ${updateTask.exception?.message}"
                                }
                            }
                    } else {
                        _isLoading.value = false
                        _errorMessage.value = "Contraseña actual incorrecta"
                    }
                }
        } else {
            _isLoading.value = false
            _errorMessage.value = "Usuario no autenticado"
        }
    }

    private fun validateNewPassword(newPassword: String, confirmPassword: String): String? {
        if (newPassword != confirmPassword) {
            return "Las nuevas contraseñas no coinciden"
        }

        if (newPassword.length < 6) {
            return "La nueva contraseña debe tener al menos 6 caracteres"
        }

        if (!newPassword.any { it.isUpperCase() }) {
            return "La nueva contraseña debe contener al menos una letra mayúscula"
        }

        if (!newPassword.any { it.isDigit() }) {
            return "La nueva contraseña debe contener al menos un número"
        }

        return null // Contraseña válida
    }
}
