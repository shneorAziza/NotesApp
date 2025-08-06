package com.shneor.notesapp.screens

import android.annotation.SuppressLint
import android.content.Context
import android.location.LocationManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.shneor.notesapp.viewmodel.MainUiState
import com.shneor.notesapp.viewmodel.MainViewModel
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import com.shneor.notesapp.R
import com.shneor.notesapp.ui.theme.CustomBackGround
import com.shneor.notesapp.viewmodel.AuthViewModel
import com.shneor.notesapp.viewmodel.LocationEvent

enum class DisplayMode {
    LIST,
    MAP
}

val myFont = FontFamily(
    Font(R.font.montserrat_medium)
)

@SuppressLint("MissingPermission")
@Composable
fun MainScreen(
    navController: NavController,
    viewModel: MainViewModel = hiltViewModel()
) {
    val context = LocalContext.current

    val authViewModel: AuthViewModel = hiltViewModel()

    var displayMode by remember { mutableStateOf(DisplayMode.LIST) }

    val uiState by viewModel.uiState.collectAsState()

    val notes by viewModel.notes.collectAsState()

    val appUser by authViewModel.appUser.collectAsState()

    val currentLocation = remember { mutableStateOf<LatLng?>(null) }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            viewModel.handleLocationRequest(context)
        } else {
            viewModel.emitLocationPermissionDenied()
        }
    }

    LaunchedEffect(Unit) {
        locationPermissionLauncher.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
    }

    LaunchedEffect(Unit) {
        viewModel.locationEvent.collect { event ->
            when (event) {
                is LocationEvent.LocationSaved -> {
                    Toast.makeText(context, "Location saved: ${event.lat}, ${event.lng}", Toast.LENGTH_SHORT).show()
                    currentLocation.value = LatLng(event.lat, event.lng)
                }
                LocationEvent.LocationUnavailable -> {
                    Toast.makeText(context, "Location not available", Toast.LENGTH_SHORT).show()
                }
                LocationEvent.LocationPermissionDenied -> {
                    Toast.makeText(context, "Location permission denied", Toast.LENGTH_SHORT).show()
                }
                LocationEvent.LocationDisabled -> {
                    Toast.makeText(context, "Location or Internet services are turned off. Please turn them on.", Toast.LENGTH_LONG).show()
                }
                LocationEvent.LocationError -> {
                    Toast.makeText(context, "Error getting location", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    Box(
        Modifier
            .fillMaxSize()
            .background(CustomBackGround)
    ) {
        Scaffold(
            floatingActionButton = {
                FloatingActionButton(onClick = {
                    val lat = currentLocation.value?.latitude ?: 0.0
                    val lng = currentLocation.value?.longitude ?: 0.0
                    navController.navigate("note_screen/null?latitude=$lat&longitude=$lng")
                }) {
                    Icon(Icons.Default.Add, contentDescription = "Add Note")
                }
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .padding(top = 20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Text(
                        text = "My Notes",
                        style = MaterialTheme.typography.headlineLarge,
                        fontFamily = myFont,
                        lineHeight = 30.sp
                    )
                    IconButton(onClick = {
                        viewModel.logout()
                        navController.navigate("auth") {
                            popUpTo("main") { inclusive = true }
                        }
                    }) {
                        Icon(Icons.Default.ExitToApp, contentDescription = "Logout")
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "Hello ${appUser?.name}",
                    style = MaterialTheme.typography.headlineLarge,
                    fontFamily = myFont,
                    fontSize = 25.sp
                )

                Spacer(modifier = Modifier.height(50.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(onClick = { displayMode = DisplayMode.LIST }) {
                        Text(
                            text = "List",
                            fontFamily = myFont
                        )
                    }
                    Button(onClick = { displayMode = DisplayMode.MAP }) {
                        Text(
                            text = "Map",
                            fontFamily = myFont
                        )
                    }
                }

                when (uiState) {
                    is MainUiState.Loading -> CircularProgressIndicator()
                    is MainUiState.Empty ->
                        Box(
                            Modifier
                                .fillMaxWidth()
                                .align(Alignment.CenterHorizontally)
                        ) {
                            Spacer(modifier = Modifier.height(250.dp))

                            Text(
                                text = "No notes yet\nTap the '+' to add new Note",
                                fontSize = 20.sp,
                                fontFamily = myFont,
                                textAlign = TextAlign.Center,
                                lineHeight = 30.sp,
                                modifier = Modifier.align(Alignment.Center)
                            )
                        }

                    is MainUiState.Success -> {
                        when (displayMode) {
                            DisplayMode.LIST ->
                                if (notes.isNotEmpty()) {
                                    NotesList(
                                        notes = notes,
                                        onNoteClick = { noteId ->
                                            navController.navigate("note_screen/$noteId")
                                        }, onDeleteNote = { note ->
                                            viewModel.deleteNote(note.id)
                                        })
                                }

                            DisplayMode.MAP -> {
                                Spacer(modifier = Modifier.height(50.dp))

                                NotesMap(
                                    notes = notes,
                                    onNoteClick = { noteId ->
                                        navController.navigate("note_screen/$noteId")

                                    })
                            }
                        }
                    }

                    is MainUiState.Error ->
                        Text(
                            text = "Error loading notes",
                            fontFamily = myFont
                        )
                }
            }
        }
    }
}