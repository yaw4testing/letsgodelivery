package com.letsGodelivery.app.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
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
    data class Success(val firebaseuser: FirebaseUser, val userProfile: User?) : AuthUiState()
    data class Error(val message: String) : AuthUiState()
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
                fetchUserProfile(firebaseUser.uid) { userProfile ->
                    _currentUserProfile.value = userProfile
                    _authUiState.value = AuthUiState.Success(firebaseUser, userProfile)
                }
            }

            }
    }

    fun signUp(email: String, pass: String, displayName: String, userType: UserType ) {
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
                        //address = address

                    )
                    // Save user profile to Firestore
                    db.collection("users").document(firebaseUser.uid).set(user).await()
                    _currentUserProfile.value = user
                    _authUiState.value = AuthUiState.Success(firebaseUser, user)
                } ?: run {
                    _authUiState.value = AuthUiState.Error("Sign up failed: User is null")
                }
            } catch (e: Exception) {
                _authUiState.value = AuthUiState.Error(e.message ?: "Sign up failed")
            }
        }
    }


    fun login(email: String, pass: String) {
        _authUiState.value = AuthUiState.Loading
        viewModelScope.launch {
            try {
                val authResult = auth.signInWithEmailAndPassword(email, pass).await()
                authResult.user?.let { firebaseUser ->
                    fetchUserProfile(firebaseUser.uid) { userProfile ->
                        _currentUserProfile.value = userProfile
                        _authUiState.value = AuthUiState.Success(firebaseUser, userProfile)
                    }
                } ?: run {
                    _authUiState.value = AuthUiState.Error("Login failed: User is null")
                }
            } catch (e: Exception) {
                _authUiState.value = AuthUiState.Error(e.message ?: "Login failed")
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
