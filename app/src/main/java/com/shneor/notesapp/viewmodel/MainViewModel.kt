package com.shneor.notesapp.viewmodel

import android.annotation.SuppressLint
import android.content.Context
import android.location.LocationManager
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.google.android.gms.location.LocationServices
import com.google.firebase.auth.FirebaseAuth
import com.shneor.notesapp.model.Note
import com.shneor.notesapp.repository.AuthRepository
import com.shneor.notesapp.repository.NotesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val notesRepository: NotesRepository
) : ViewModel() {

    private val _notes = MutableStateFlow<List<Note>>(emptyList())
    val notes: StateFlow<List<Note>> = _notes.asStateFlow()

    private val _uiState = MutableStateFlow<MainUiState>(MainUiState.Loading)
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()

    private val _locationEvent = MutableSharedFlow<LocationEvent>()
    val locationEvent: SharedFlow<LocationEvent> = _locationEvent.asSharedFlow()

    init {
        observeLogin()
    }

    private fun fetchNotes(userId: String) {

        viewModelScope.launch {
            notesRepository.getAllNotes(userId).collect { notesList ->

                _notes.value = notesList
                _uiState.value = if (notesList.isEmpty()) {
                    MainUiState.Empty
                } else {
                    MainUiState.Success
                }
            }
        }
    }

    fun deleteNote(noteId: String) {
        viewModelScope.launch {
            notesRepository.deleteNoteById(noteId)
        }
    }

    fun logout() {
        authRepository.logout()
    }

    @SuppressLint("MissingPermission")
    fun handleLocationRequest(context: Context) {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val isLocationEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

        if (!isLocationEnabled) {
            viewModelScope.launch {
                _locationEvent.emit(LocationEvent.LocationDisabled)
            }
            return
        }

        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

        fusedLocationClient.lastLocation
            .addOnSuccessListener { location ->
                viewModelScope.launch {
                    if (location != null) {
                        _locationEvent.emit(
                            LocationEvent.LocationSaved(
                                location.latitude,
                                location.longitude
                            )
                        )
                    } else {
                        _locationEvent.emit(LocationEvent.LocationUnavailable)
                    }
                }
            }
            .addOnFailureListener {
                viewModelScope.launch {
                    _locationEvent.emit(LocationEvent.LocationError)
                }
            }
    }

    fun emitLocationPermissionDenied() {
        viewModelScope.launch {
            _locationEvent.emit(LocationEvent.LocationPermissionDenied)
        }
    }

    private fun observeLogin() {
        FirebaseAuth.getInstance().addAuthStateListener { auth ->
            val user = auth.currentUser
            if (user != null) {
                Log.d("MainViewModel", "User id: ${user.uid}")
                fetchNotes(user.uid)
            } else {
                Log.e("MainViewModel", "No user logged in")
            }
        }
    }

}

sealed class MainUiState {
    data object Loading : MainUiState()
    data object Empty : MainUiState()
    data object Success : MainUiState()
    data class Error(val message: String) : MainUiState()
}

sealed class LocationEvent {
    data class LocationSaved(val lat: Double, val lng: Double) : LocationEvent()
    data object LocationUnavailable : LocationEvent()
    data object LocationPermissionDenied : LocationEvent()
    data object LocationDisabled : LocationEvent()
    data object LocationError : LocationEvent()
}
