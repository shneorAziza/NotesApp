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

    suspend fun getNoteById(noteId: Int): Note? = notesDao.getNoteById(noteId.toString())

    suspend fun insertNote(note: Note) = notesDao.insertNote(note)

    suspend fun updateNote(note: Note) = notesDao.updateNote(note)

    suspend fun deleteNote(note: Note) = notesDao.deleteNote(note)
}