package com.example.tft.ui.userProfile

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import com.example.tft.data.services.Authentication.AuthenticationServices
import com.example.tft.data.services.User.UserServices
import com.example.tft.model.user.Users
import com.example.tft.navigation.AppScreens

class UserProfileViewModel : ViewModel() {

    private val _userProfile = MutableLiveData<Users?>()
    private var documentId: String? = null
    val userProfile: LiveData<Users?> get() = _userProfile

    init {
        loadUserProfile()
    }

    fun loadUserProfile(forceReload: Boolean = false) {
        if (forceReload || _userProfile.value == null) {
            UserServices.getCurrentUserProfile { userProfile, docId ->
                if (userProfile != null && docId != null) {
                    _userProfile.postValue(userProfile)
                    documentId = docId // Asegúrate de no llamar a `toString()` aquí
                }
            }
        }
    }

    fun updateUserProfile(name: String, email: String, onComplete: () -> Unit) {
        val user = _userProfile.value
        if (user != null) {
            user.name = name
            user.userId = email

            documentId?.let { docId ->
                UserServices.updateUserProfile(user, docId) { success ->
                    if (success) {
                        loadUserProfile(forceReload = true) // Forzar recarga después de guardar
                        onComplete()
                    } else {
                        Log.e("UserProfileViewModel", "Error updating user profile")
                    }
                }
            }
        }
    }

    fun signOutUser(navController: NavController) {
        AuthenticationServices.signOut()
        navController.navigate(route = AppScreens.LoginScreen.route) {
            popUpTo(AppScreens.UserProfileScreen.route) { inclusive = true }
        }
    }
}

