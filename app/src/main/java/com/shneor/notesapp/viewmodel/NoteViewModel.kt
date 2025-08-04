package com.shneor.notesapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shneor.notesapp.model.Note
import com.shneor.notesapp.repository.NotesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

import java.util.Date

@HiltViewModel
class NoteViewModel @Inject constructor(
    private val notesRepository: NotesRepository
) : ViewModel() {

    private val _noteTitle = MutableStateFlow("")
    val noteTitle: StateFlow<String> = _noteTitle.asStateFlow()

    private val _noteContent = MutableStateFlow("")
    val noteContent: StateFlow<String> = _noteContent.asStateFlow()

    private var currentNoteId: String? = null

    fun loadNote(noteId: Int) {
        viewModelScope.launch {
            val note = notesRepository.getNoteById(noteId)
            if (note != null) {
                currentNoteId = note.id
                _noteTitle.value = note.title
                _noteContent.value = note.content
            }
        }
    }

    fun saveNote(title: String, content: String) {
        viewModelScope.launch {
            if (currentNoteId == null) {
                // New note
                val newNote = Note(
                    title = title,
                    content = content
                )
                notesRepository.insertNote(newNote)
            } else {
                // Update existing note
                val updatedNote = Note(
                    id = currentNoteId!!,
                    title = title,
                    content = content
                )
                notesRepository.updateNote(updatedNote)
            }
        }
    }

    fun onTitleChange(newTitle: String) {
        _noteTitle.value = newTitle
    }

    fun onContentChange(newContent: String) {
        _noteContent.value = newContent
    }
}