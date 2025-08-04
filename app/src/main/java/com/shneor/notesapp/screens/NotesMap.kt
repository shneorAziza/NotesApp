package com.shneor.notesapp.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.shneor.notesapp.model.Note

@Composable
fun NotesMap(
    notes: List<Note>,
    onNoteClick: (String) -> Unit
) {
    // TODO: Implement Google Map with note markers
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text("Map View will be here. Notes: ${notes.size}")
    }
}