package com.shneor.notesapp.repository.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.shneor.notesapp.model.Note

@Database(entities = [Note::class], version = 2, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun noteDao(): NoteDao
}