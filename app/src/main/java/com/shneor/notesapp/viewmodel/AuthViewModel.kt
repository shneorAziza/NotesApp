package com.shneor.notesapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.shneor.notesapp.model.User
import com.shneor.notesapp.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(private val repository: AuthRepository) : ViewModel() {

    private val _user = MutableStateFlow(repository.getCurrentUser())
    val user: StateFlow<com.google.firebase.auth.FirebaseUser?> = _user

    private val _uiState = MutableStateFlow<AuthUiState>(AuthUiState.Initial)
    val uiState: StateFlow<AuthUiState> = _uiState

    private val _appUser = MutableStateFlow<User?>(null)
    val appUser: StateFlow<User?> = _appUser

    init {
        FirebaseAuth.getInstance().addAuthStateListener {
            _user.value = it.currentUser
            _uiState.value = AuthUiState.Initial

            if (it.currentUser != null) {
                loadAppUser()
            } else {
                _appUser.value = null
            }
        }
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            try {
                repository.login(email, password)
                loadAppUser()
            } catch (e: Exception) {
                _uiState.value = AuthUiState.Error(e.message ?: "Login failed")
            }
        }
    }

    fun signup(email: String, password: String, name: String) {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            try {
                repository.signUp(email, password, name)
                loadAppUser()
            } catch (e: Exception) {
                _uiState.value = AuthUiState.Error(e.message ?: "Signup failed")
            }
        }
    }

    private fun loadAppUser() {
        viewModelScope.launch {
            try {
                val userData = repository.getUserData()
                _appUser.value = userData
            } catch (e: Exception) {
                _uiState.value = AuthUiState.Error("Failed to load user data")
            }
        }
    }


    fun resetUiState() {
        _uiState.value = AuthUiState.Initial
    }

    fun setErrorMessage(message: String) {
        _uiState.value = AuthUiState.Error(message)
    }
}

sealed class AuthUiState {
    data object Initial : AuthUiState()
    data object Loading : AuthUiState()
    data class Error(val message: String) : AuthUiState()
}