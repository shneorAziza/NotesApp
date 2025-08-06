package com.shneor.notesapp.screens

import android.annotation.SuppressLint
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
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import com.shneor.notesapp.R
import com.shneor.notesapp.ui.theme.CustomBackGround

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
    navController: NavController
) {
    val context = LocalContext.current

    val viewModel: MainViewModel = hiltViewModel()

    var displayMode by remember { mutableStateOf(DisplayMode.LIST) }

    val uiState by viewModel.uiState.collectAsState()

    val notes by viewModel.notes.collectAsState()

    val currentLocation by remember { mutableStateOf<LatLng?>(null) }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location ->
                    if (location != null) {
                        val latitude = location.latitude
                        val longitude = location.longitude
                        Toast.makeText(context, "Location saved: $latitude, $longitude", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, "Location not available", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(context, "Error getting location", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(context, "Location permission denied", Toast.LENGTH_SHORT).show()
        }
    }

    LaunchedEffect(Unit) {
        locationPermissionLauncher.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
    }
    Box(
        Modifier
            .fillMaxSize()
            .background(CustomBackGround)
    ) {
        Scaffold(
            floatingActionButton = {
                FloatingActionButton(onClick = {
                    val lat = currentLocation?.latitude ?: 0.0
                    val lng = currentLocation?.longitude ?: 0.0
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
                        .padding(top = 50.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Text(
                        text = "My Notes",
                        style = MaterialTheme.typography.headlineLarge,
                        fontFamily = myFont
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

                Row(
                    modifier = Modifier.fillMaxWidth()
                        .padding(top = 50.dp),
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
                            Modifier.fillMaxWidth()
                                .align(Alignment.CenterHorizontally)
                        ) {
                            Spacer(modifier = Modifier.height(150.dp))

                            Text(
                                text = "No notes yet\nTap the '+' to add new Note",
                                fontFamily = myFont,
                                modifier = Modifier.align(Alignment.Center)
                            )
                        }

                    is MainUiState.Success -> {
                        when (displayMode) {
                            DisplayMode.LIST -> NotesList(notes = notes, onNoteClick = { noteId ->
                                navController.navigate("note_screen/$noteId")
                            }, onDeleteNote = { note ->
                                viewModel.deleteNote(note)
                            })

                            DisplayMode.MAP -> NotesMap(notes = notes, onNoteClick = { noteId ->
                                navController.navigate("note_screen/$noteId")
                            })
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