package com.shneor.notesapp.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.shneor.notesapp.viewmodel.MainUiState
import com.shneor.notesapp.viewmodel.MainViewModel
import com.google.firebase.auth.FirebaseAuth
import com.shneor.notesapp.repository.AuthRepository
import com.shneor.notesapp.repository.NotesRepository
import com.shneor.notesapp.repository.db.AppDatabase
import com.shneor.notesapp.viewmodel.MainViewModelFactory
import androidx.room.Room
import androidx.compose.ui.platform.LocalContext

enum class DisplayMode {
    LIST,
    MAP
}

@Composable
fun MainScreen(
    navController: NavController
) {
    val context = LocalContext.current

    val authRepository = AuthRepository(FirebaseAuth.getInstance())
    val notesRepository = NotesRepository(
        notesDao = Room.databaseBuilder(
            context,
            AppDatabase::class.java, "notes-db"
        ).build().noteDao()
    )

    val viewModel: MainViewModel = viewModel(
        factory = MainViewModelFactory(authRepository, notesRepository)
    )

    var displayMode by remember { mutableStateOf(DisplayMode.LIST) }
    val uiState by viewModel.uiState.collectAsState()
    val notes by viewModel.notes.collectAsState()

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { navController.navigate("note_screen/null") }) {
                Icon(Icons.Default.Add, contentDescription = "Add Note")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Notes", style = MaterialTheme.typography.headlineLarge)
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
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(onClick = { displayMode = DisplayMode.LIST }) {
                    Text("List")
                }
                Button(onClick = { displayMode = DisplayMode.MAP }) {
                    Text("Map")
                }
            }

            when (uiState) {
                is MainUiState.Loading -> CircularProgressIndicator()
                is MainUiState.Empty -> Text("No notes yet. Tap the '+' to add new Note")
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
                is MainUiState.Error -> Text("Error loading notes")
            }
        }
    }
}