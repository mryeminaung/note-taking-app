package com.example.notetakingapp.data.repository

import com.example.notetakingapp.data.dao.NoteDao
import com.example.notetakingapp.data.models.Note

class NotesRepository(private val noteDao: NoteDao) {

    // -----------------------------
    // Normal Notes
    // -----------------------------
    suspend fun getAllNotes(userId: String, sortType: String = "default"): List<Note> {
        return when (sortType.lowercase()) {
            "priority" -> noteDao.indexByPriority(userId)
            "date" -> noteDao.indexByDate(userId)
            else -> noteDao.indexDefault(userId)
        }
    }

    suspend fun getNoteById(id: Int, userId: String): Note? =
        noteDao.show(id, userId)

    suspend fun insertNote(note: Note) = noteDao.insert(note)

    suspend fun updateNote(note: Note) = noteDao.update(note)

    suspend fun deleteNote(note: Note) = noteDao.delete(note)

    suspend fun countAllNotes(userId: String): Int =
        noteDao.countAllNotes(userId)

    // -----------------------------
    // Starred Notes
    // -----------------------------
    suspend fun getStarredNotes(userId: String, sortType: String = "default"): List<Note> {
        return when (sortType.lowercase()) {
            "priority" -> noteDao.getStarredNotesByPriority(userId)
            "date" -> noteDao.getStarredNotesByDate(userId)
            else -> noteDao.getStarredNotesDefault(userId)
        }
    }

    suspend fun countStarredNotes(userId: String): Int =
        noteDao.countStarredNotes(userId)

    // -----------------------------
    // Search Notes
    // -----------------------------
    suspend fun searchNotes(
        userId: String,
        query: String,
        sortType: String = "default"
    ): List<Note> {
        return when (sortType.lowercase()) {
            "priority" -> noteDao.searchNotesByPriority(userId, query)
            "date" -> noteDao.searchNotesByDate(userId, query)
            else -> noteDao.searchNotesDefault(userId, query)
        }
    }

    suspend fun searchStarredNotes(
        userId: String,
        query: String,
        sortType: String = "default"
    ): List<Note> {
        return when (sortType.lowercase()) {
            "priority" -> noteDao.searchStarredNotesByPriority(userId, query)
            "date" -> noteDao.searchStarredNotesByDate(userId, query)
            else -> noteDao.searchStarredNotesDefault(userId, query)
        }
    }
}
