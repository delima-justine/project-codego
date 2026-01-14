package com.example.project_codego.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Calendar

class AuthViewModel : ViewModel() {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    private val _currentUser = MutableStateFlow<FirebaseUser?>(auth.currentUser)
    val currentUser = _currentUser.asStateFlow()

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState = _authState.asStateFlow()

    fun register(email: String, password: String, firstName: String, lastName: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val user = auth.currentUser
                        val profileUpdates = UserProfileChangeRequest.Builder()
                            .setDisplayName("$firstName $lastName")
                            .build()

                        user?.updateProfile(profileUpdates)
                            ?.addOnCompleteListener { profileTask ->
                                if (profileTask.isSuccessful) {
                                    _currentUser.value = auth.currentUser
                                    _authState.value = AuthState.Success
                                } else {
                                    _authState.value = AuthState.Success // Created but name update failed, still consider success or handle error
                                }
                            } ?: run {
                                _currentUser.value = user
                                _authState.value = AuthState.Success
                            }
                    } else {
                        _authState.value = AuthState.Error(task.exception?.message ?: "Registration failed")
                    }
                }
        }
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val user = auth.currentUser
                        if (user != null) {
                            // Check if account is scheduled for deletion
                            firestore.collection("deleted_accounts").document(user.uid).get()
                                .addOnSuccessListener { document ->
                                    if (document.exists() && document.getString("status") == "pending_deletion") {
                                        _currentUser.value = user
                                        _authState.value = AuthState.PendingDeletion("This account is scheduled for deletion. Would you like to reactivate it?")
                                    } else {
                                        _currentUser.value = user
                                        _authState.value = AuthState.Success
                                    }
                                }
                                .addOnFailureListener {
                                    // If check fails, allow login but maybe log error
                                    _currentUser.value = user
                                    _authState.value = AuthState.Success
                                }
                        } else {
                            _authState.value = AuthState.Error("User not found after login")
                        }
                    } else {
                        _authState.value = AuthState.Error(task.exception?.message ?: "Login failed")
                    }
                }
        }
    }

    fun reactivateAccount(onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            val user = auth.currentUser
            if (user == null) {
                onError("No user logged in")
                return@launch
            }

            _authState.value = AuthState.Loading
            firestore.collection("deleted_accounts").document(user.uid)
                .delete()
                .addOnSuccessListener {
                    _authState.value = AuthState.Success
                    onSuccess()
                }
                .addOnFailureListener { e ->
                    _authState.value = AuthState.Error(e.message ?: "Failed to reactivate account")
                    onError(e.message ?: "Failed to reactivate account")
                }
        }
    }

    fun logout() {
        auth.signOut()
        _currentUser.value = null
        _authState.value = AuthState.Idle
    }
    
    fun updateProfile(
        displayName: String,
        email: String? = null,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            val user = auth.currentUser
            if (user == null) {
                onError("No user logged in")
                return@launch
            }
            
            val profileUpdates = UserProfileChangeRequest.Builder()
                .setDisplayName(displayName)
                .build()
            
            user.updateProfile(profileUpdates)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        if (email != null && email != user.email) {
                            user.updateEmail(email)
                                .addOnCompleteListener { emailTask ->
                                    if (emailTask.isSuccessful) {
                                        _currentUser.value = auth.currentUser
                                        onSuccess()
                                    } else {
                                        onError(emailTask.exception?.message ?: "Failed to update email")
                                    }
                                }
                        } else {
                            _currentUser.value = auth.currentUser
                            onSuccess()
                        }
                    } else {
                        onError(task.exception?.message ?: "Failed to update profile")
                    }
                }
        }
    }

    fun softDeleteAccount(onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            val user = auth.currentUser
            if (user == null) {
                onError("No user logged in")
                return@launch
            }

            val uid = user.uid
            val calendar = Calendar.getInstance()
            val deletionDate = calendar.time
            calendar.add(Calendar.DAY_OF_YEAR, 30)
            val permanentDeletionDate = calendar.time

            val deletionData = hashMapOf(
                "uid" to uid,
                "email" to user.email,
                "requestedAt" to deletionDate,
                "scheduledPermanentDeletionAt" to permanentDeletionDate,
                "status" to "pending_deletion"
            )

            _authState.value = AuthState.Loading
            firestore.collection("deleted_accounts").document(uid)
                .set(deletionData)
                .addOnSuccessListener {
                    logout()
                    onSuccess()
                }
                .addOnFailureListener { e ->
                    _authState.value = AuthState.Error(e.message ?: "Failed to request account deletion")
                    onError(e.message ?: "Failed to request account deletion")
                }
        }
    }
}

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    object Success : AuthState()
    data class PendingDeletion(val message: String) : AuthState()
    data class Error(val message: String) : AuthState()
}
