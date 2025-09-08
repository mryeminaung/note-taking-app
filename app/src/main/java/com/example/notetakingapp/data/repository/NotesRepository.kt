package com.example.notetakingapp.data.repository

import com.example.notetakingapp.data.dao.NoteDao
import com.example.notetakingapp.data.models.Note

class NotesRepository(private val noteDao: NoteDao) {

    suspend fun getAllNotes(): List<Note> = noteDao.index()

    suspend fun getNoteById(id: Int): Note? = noteDao.show(id)

    suspend fun insertNote(note: Note) = noteDao.insert(note)

    suspend fun updateNote(note: Note) = noteDao.update(note)

    suspend fun deleteNote(note: Note) = noteDao.delete(note)

    suspend fun countAllNotes(): Int = noteDao.countAllNotes()

    suspend fun countStarredNotes(): Int = noteDao.countStarredNotes()

    suspend fun getStarredNotes(): List<Note> = noteDao.getStarredNotes()
}
