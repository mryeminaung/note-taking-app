package com.example.notetakingapp.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.notetakingapp.data.models.Note

@Dao
interface NoteDao {

    // Fetch all notes sorted by createdAt (newest first)
    @Query("SELECT * FROM notes ORDER BY createdAt DESC")
    suspend fun index(): List<Note>

    // Fetch a single note by id
    @Query("SELECT * FROM notes WHERE id = :id")
    suspend fun show(id: Int): Note?

    // Insert a new note
    @Insert
    suspend fun insert(note: Note)

    // Update a note
    @Update
    suspend fun update(note: Note)

    // Delete a note
    @Delete
    suspend fun delete(note: Note)

    @Query("SELECT COUNT(*) FROM notes")
    suspend fun countAllNotes(): Int

    @Query("SELECT COUNT(*) FROM notes WHERE starred = 1")
    suspend fun countStarredNotes(): Int

}

