package com.shneor.notesapp.screens


import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController

@Composable
fun NoteScreen(
    navController: NavController,
    noteId: String?
) {
    // TODO: Implement logic to load or create a note based on noteId
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        if (noteId == null) {
            Text("Create New Note")
        } else {
            Text("Edit Note with ID: $noteId")
        }
    }
}
