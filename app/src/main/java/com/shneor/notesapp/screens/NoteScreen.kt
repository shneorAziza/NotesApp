package com.shneor.notesapp.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.shneor.notesapp.ui.theme.CustomBackGround
import com.shneor.notesapp.viewmodel.NoteViewModel
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import com.google.android.gms.maps.model.LatLng

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteScreen(
    navController: NavController,
    noteId: String?
) {
    val viewModel: NoteViewModel = hiltViewModel()

    val title by viewModel.noteTitle.collectAsState()


    val content by viewModel.noteContent.collectAsState()
    val currentLocation = remember { mutableStateOf(LatLng(0.0, 0.0)) }

    LaunchedEffect(noteId) {
        if (!noteId.isNullOrEmpty()) {
            viewModel.loadNote(noteId)
        }
    }

    Box(
        Modifier
            .fillMaxSize()
            .background(CustomBackGround)
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(
                        text = "Note",
                        fontFamily = myFont
                    ) },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                        }
                    },
                    actions = {
                        IconButton(onClick = {
                            viewModel.saveNote(
                                title = title,
                                content = content,
                                latitude = currentLocation.value.latitude,
                                longitude = currentLocation.value.longitude
                            )
                            navController.popBackStack()
                        }) {
                            Icon(Icons.Default.Done, contentDescription = "Save")
                        }
                    }
                )
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .padding(padding)
                    .padding(16.dp)
                    .fillMaxSize()
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { viewModel.onTitleChange(it) },
                    label = { Text(
                        text = "Title",
                        fontFamily = myFont
                    ) },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = content,
                    onValueChange = { viewModel.onContentChange(it) },
                    label = { Text(
                        text = "Content",
                        fontFamily = myFont
                    ) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                )
            }
        }
    }
}
