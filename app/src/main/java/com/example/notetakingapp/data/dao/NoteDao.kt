package com.example.notetakingapp.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.notetakingapp.data.models.Note

@Dao
interface NoteDao {
    @Query("SELECT * FROM notes ORDER BY createdAt DESC")
    suspend fun index(): List<Note>

    @Query("SELECT * FROM notes WHERE id = :id")
    suspend fun show(id: Int): Note?

    @Insert
    suspend fun insert(note: Note)

    @Update
    suspend fun update(note: Note)

    @Delete
    suspend fun delete(note: Note)

    @Query("SELECT COUNT(*) FROM notes")
    suspend fun countAllNotes(): Int

    @Query("SELECT COUNT(*) FROM notes WHERE starred = 1")
    suspend fun countStarredNotes(): Int

    @Query("SELECT * FROM notes WHERE starred = 1 ORDER BY id DESC")
    suspend fun getStarredNotes(): List<Note>

    @Query("SELECT * FROM notes WHERE title LIKE '%' || :query || '%' OR body LIKE '%' || :query || '%' ORDER BY createdAt DESC")
    suspend fun searchNotes(query: String): List<Note>

    @Query("SELECT * FROM notes WHERE (title LIKE '%' || :query || '%' OR body LIKE '%' || :query || '%') AND starred = 1 ORDER BY createdAt DESC")
    suspend fun searchStarredNotes(query: String): List<Note>

}

