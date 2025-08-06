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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

@HiltViewModel
class NoteViewModel @Inject constructor(
    private val notesRepository: NotesRepository
) : ViewModel() {

    private val _noteTitle = MutableStateFlow("")
    val noteTitle: StateFlow<String> = _noteTitle.asStateFlow()

    private val _noteContent = MutableStateFlow("")
    val noteContent: StateFlow<String> = _noteContent.asStateFlow()

    private val _noteTimestamp = MutableStateFlow(0L)
    val noteTimestamp: StateFlow<Long> = _noteTimestamp.asStateFlow()

    private val _noteLatitude = MutableStateFlow(0.0)
    val noteLatitude: StateFlow<Double> = _noteLatitude.asStateFlow()

    private val _noteLongitude = MutableStateFlow(0.0)
    val noteLongitude: StateFlow<Double> = _noteLongitude.asStateFlow()

    private var currentNoteId: String? = null

    val formattedDate: StateFlow<String> = _noteTimestamp
        .map { timestamp ->
            if (timestamp > 0L) {
                SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date(timestamp))
            } else {
                ""
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "")

    fun loadNote(noteId: String) {
        viewModelScope.launch {
            val note = notesRepository.getNoteById(noteId)
            if (note != null) {
                currentNoteId = note.id
                _noteTitle.value = note.title
                _noteContent.value = note.content
                _noteLatitude.value = note.latitude
                _noteLongitude.value = note.longitude
                _noteTimestamp.value = note.timestamp
            }
        }
    }

    fun saveNote(title: String, content: String, latitude: Double, longitude: Double) {
        viewModelScope.launch {
            if (currentNoteId == null) {

                 val newNote = Note(
                    title = title,
                    content = content,
                    latitude = latitude,
                    longitude = longitude
                )
                notesRepository.insertNote(newNote)

                _noteTitle.value = title
                _noteContent.value = content
                _noteLatitude.value = latitude
                _noteLongitude.value = longitude
                _noteTimestamp.value = newNote.timestamp
            } else {

                val updatedNote = Note(
                    id = currentNoteId!!,
                    title = title,
                    content = content,
                    latitude = noteLatitude.value,
                    longitude = noteLongitude.value,
                    timestamp = noteTimestamp.value
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