package com.shneor.notesapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.shneor.notesapp.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AuthViewModel(private val repository: AuthRepository) : ViewModel() {

    private val _user = MutableStateFlow(repository.getCurrentUser())
    val user: StateFlow<com.google.firebase.auth.FirebaseUser?> = _user

    private val _uiState = MutableStateFlow<AuthUiState>(AuthUiState.Initial)
    val uiState: StateFlow<AuthUiState> = _uiState

    init {
        FirebaseAuth.getInstance().addAuthStateListener {
            _user.value = it.currentUser
            _uiState.value = AuthUiState.Initial
        }
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            try {
                repository.login(email, password)
            } catch (e: Exception) {
                _uiState.value = AuthUiState.Error(e.message ?: "Login failed")
            }
        }
    }

    fun signup(email: String, password: String) {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            try {
                repository.signUp(email, password)
            } catch (e: Exception) {
                _uiState.value = AuthUiState.Error(e.message ?: "Signup failed")
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