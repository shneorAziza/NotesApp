package com.shneor.notesapp.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.shneor.notesapp.model.Note

@Composable
fun NotesList(
    notes: List<Note>,
    onNoteClick: (String) -> Unit,
    onDeleteNote: (Note) -> Unit
) {
    LazyColumn {
        items(notes) { note ->
            NoteItem(
                note = note,
                onNoteClick = onNoteClick,
                onDeleteNote = onDeleteNote
            )
        }
    }
}

@Composable
fun NoteItem(
    note: Note,
    onNoteClick: (String) -> Unit,
    onDeleteNote: (Note) -> Unit
) {
    // TODO: Implement the UI for a single note item
    Text(text = note.title, modifier = Modifier.clickable { onNoteClick(note.id) })
}