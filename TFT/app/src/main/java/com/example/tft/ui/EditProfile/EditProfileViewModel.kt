package com.example.tft.ui.EditProfile

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.tft.data.services.User.UserServices
import com.example.tft.model.user.Users

class EditProfileViewModel : ViewModel() {

    private val _userProfile = MutableLiveData<Users?>()
    private var documentId: String? = null
    private var initialImageUri: Uri? = null
    val userProfile: LiveData<Users?> get() = _userProfile

    init {
        loadUserProfile()
    }

    private fun loadUserProfile() {
        UserServices.getCurrentUserProfile { userProfile, docId ->
            if (userProfile != null && docId != null) {
                Log.d("EditProfileViewModel", "UserProfile loaded: ${userProfile.name}, ${userProfile.email}")
                _userProfile.value = userProfile
                documentId = docId.toString()

                // Guardar la URI inicial de la imagen
                initialImageUri = userProfile.image?.let { Uri.parse(it) }
            } else {
                Log.w("EditProfileViewModel", "UserProfile is null or documentId is null")
            }
        }
    }

    fun updateUserProfileWithImage(name: String, email: String, imageUri: Uri?, onComplete: () -> Unit) {
        val user = _userProfile.value
        if (user != null) {
            user.name = name
            user.email = email

            documentId?.let { docId ->
                if (imageUri != null && imageUri != initialImageUri) {
                    // Subir la imagen y obtener la URL
                    UserServices.uploadProfileImage(imageUri, user.email) { success, imageUrl ->
                        if (success && imageUrl != null) {
                            user.image = imageUrl
                            // Ahora actualizar el perfil del usuario
                            UserServices.updateUserProfile(user, docId) { updateSuccess ->
                                if (updateSuccess) {
                                    _userProfile.value = user.copy(image = user.image + "?v=" + System.currentTimeMillis())
                                    loadUserProfile()
                                    onComplete()
                                } else {
                                    Log.e("EditProfileViewModel", "Error updating user profile")
                                }
                            }
                        } else {
                            Log.e("EditProfileViewModel", "Failed to upload profile image")
                        }
                    }
                } else {
                    // Si la imagen no ha cambiado, solo actualiza el perfil
                    UserServices.updateUserProfile(user, docId) { updateSuccess ->
                        if (updateSuccess) {
                            _userProfile.value = user.copy(image = user.image + "?v=" + System.currentTimeMillis())
                            loadUserProfile()
                            onComplete()
                        } else {
                            Log.e("EditProfileViewModel", "Error updating user profile")
                        }
                    }
                }
            }
        }
    }

}

