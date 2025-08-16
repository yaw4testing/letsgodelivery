package com.letsGodelivery.app.ui.auth

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthActionCodeException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.letsGodelivery.app.data.models.Address
import com.letsGodelivery.app.data.models.User
import com.letsGodelivery.app.data.models.UserType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject


sealed class AuthUiState {
    object Idle : AuthUiState()
    object Loading : AuthUiState()
    data class Success(
        val firebaseUser: FirebaseUser,
        val isEmailVerified: Boolean = firebaseUser.isEmailVerified,
        val userProfile: User?) : AuthUiState()
    data class Error(val message: String) : AuthUiState()
    object VerificationEmailSent : AuthUiState()
}

 @HiltViewModel
 class AuthViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val db: FirebaseFirestore
        ) : ViewModel(){

    private val _authUiState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
        val authUiState: StateFlow<AuthUiState> = _authUiState.asStateFlow()
        private val _currentUserProfile = MutableStateFlow<User?>(null)
        val currentUserProfile: StateFlow<User?> = _currentUserProfile.asStateFlow()

        init {
            auth.currentUser?.let {firebaseUser ->
            _authUiState.value = AuthUiState.Loading
            viewModelScope.launch {
                firebaseUser.reload().await()
                val isEmailVerified = firebaseUser.isEmailVerified
                fetchUserProfile(firebaseUser.uid) { userProfile ->
                    _currentUserProfile.value = userProfile
                    _authUiState.value = AuthUiState.Success(firebaseUser, isEmailVerified, userProfile)
                }
            }

            }
    }

    fun signUp(email: String, pass: String, displayName: String, userType: UserType,address: Address, phone: String)  {
        _authUiState.value = AuthUiState.Loading
        viewModelScope.launch {
            try {
                val authResult = auth.createUserWithEmailAndPassword(email, pass).await()
                authResult.user?.let { firebaseUser ->
                    val user = User(
                        uid = firebaseUser.uid,
                        email = firebaseUser.email ?: "",
                        userType = userType,
                        displayName = displayName,
                        address = address,
                        phoneNumber = phone

                    )
                    // Save user profile to Firestore
                    db.collection("users").document(firebaseUser.uid).set(user).await()
                    _currentUserProfile.value = user

                    sendVerificationEmailInternal(firebaseUser) { success, message ->
                        _authUiState.value = AuthUiState.Success(firebaseUser, false, user)
                        Log.d("AuthViewModel", "User signed up. Verification email sent: initiated")
                    }

                } ?: run {
                    _authUiState.value = AuthUiState.Error("Sign up failed: User is null")
                }
            } catch (e: Exception) {
                _authUiState.value = AuthUiState.Error(e.message ?: "Sign up failed")
            }
        }
    }

     fun sendVerificationEmail(callback: (success: Boolean, message: String) -> Unit) {
         val firebaseUser = auth.currentUser
         if (firebaseUser != null) {
             callback(false, "No user logged in to send verification email")
         }
         if (firebaseUser != null && !firebaseUser.isEmailVerified) {
             callback(true, "Email is already verified.")
             return
         }
     }
     private fun sendVerificationEmailInternal(firebaseUser: FirebaseUser, callback: (success: Boolean, message: String) -> Unit) {
         firebaseUser.sendEmailVerification()
             .addOnCompleteListener { task ->
                 if (task.isSuccessful) {
                     Log.d("AuthViewModel", "Verification email sent to ${firebaseUser.email}")
                     _authUiState.value =
                         AuthUiState.Success(firebaseUser, false, _currentUserProfile.value)
                     callback(true, "Verification email sent! to $firebaseUser.email")
                 } else {
                     Log.e("AuthViewModel", "Failed to send verification email: ${task.exception}")
                     val errorMessage = when (task.exception) {
                         is FirebaseAuthActionCodeException -> "Too many requests. Try again later."
                         else -> "Failed to send verification email. ${task.exception?. message}"
                     }
                     _authUiState.value = AuthUiState.Error(errorMessage)
                     callback(false, errorMessage)
                 }
             }


     }

    fun login(email: String, pass: String) {
        _authUiState.value = AuthUiState.Loading
        viewModelScope.launch {
            try {
                val authResult = auth.signInWithEmailAndPassword(email, pass).await()
                authResult.user?.let { firebaseUser ->
                    firebaseUser.reload().await()
                    val isEmailVerified = firebaseUser.isEmailVerified
                    fetchUserProfile(firebaseUser.uid) { userProfile ->
                        _currentUserProfile.value = userProfile
                        _authUiState.value = AuthUiState.Success(firebaseUser, isEmailVerified, userProfile)
                    }
                } ?: run {
                    _authUiState.value = AuthUiState.Error("Login failed: User is null")
                }
            } catch (e: Exception) {
                _authUiState.value = AuthUiState.Error(e.message ?: "Login failed")
            }
        }
    }

     fun checkEmailVerificationStatus(callback: (isVerified: Boolean) -> Unit) {
         val firebaseUser = auth.currentUser
         if (firebaseUser == null) {
             _authUiState.value = AuthUiState.Idle // Should not happen if on verify screen
             callback(false)
             return
         }

         _authUiState.value = AuthUiState.Loading
         viewModelScope.launch {
             try {
                 firebaseUser.reload().await() // Refresh the user's state from Firebase
                 val isVerified = firebaseUser.isEmailVerified
                 if (isVerified) {
                     // Fetch profile again in case something changed, or assume current one is fine
                     fetchUserProfile(firebaseUser.uid){ userProfile ->
                         _currentUserProfile.value = userProfile
                         _authUiState.value = AuthUiState.Success(firebaseUser, true, userProfile)
                         callback(true)
                     }
                 } else {
                     _authUiState.value = AuthUiState.Success(firebaseUser, false, _currentUserProfile.value)
                     callback(false)
                 }
             } catch (e: Exception) {
                 Log.e("AuthViewModel", "Error checking email verification status", e)
                 // Revert to previous success state but not verified
                 _authUiState.value = AuthUiState.Success(firebaseUser, false, _currentUserProfile.value)
                 // Or AuthUiState.Error("Could not refresh status: ${e.message}")
                 callback(false)
             }
         }
     }


     private suspend fun fetchUserProfile(uid: String, onResult: (User?) -> Unit) {
        try {
            val documentSnapshot = db.collection("users").document(uid).get().await()
            val user = documentSnapshot.toObject(User::class.java)
            onResult(user)
        } catch (e: Exception) {
            // Handle error, e.g., log it or update UI state
            _authUiState.value = AuthUiState.Error("Failed to fetch profile: ${e.message}")
            onResult(null)
        }
    }


    fun signOut() {
        auth.signOut()
        _authUiState.value = AuthUiState.Idle
        _currentUserProfile.value = null
    }

    fun getCurrentFirebaseUser(): FirebaseUser? {
        return auth.currentUser
    }

}
