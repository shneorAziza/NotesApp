package com.shneor.notesapp.repository

import com.shneor.notesapp.model.Note
import com.shneor.notesapp.repository.db.NoteDao
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotesRepository @Inject constructor(
    private val notesDao: NoteDao
) {
    fun getAllNotes(): Flow<List<Note>> = notesDao.getAllNotes()

    suspend fun getNoteById(noteId: String): Note? = notesDao.getNoteById(noteId)

    suspend fun insertNote(note: Note) = notesDao.insertNote(note)

    suspend fun updateNote(note: Note) = notesDao.updateNote(note)

    suspend fun deleteNoteById(noteId: String) {
        notesDao.deleteNoteById(noteId)
    }
}