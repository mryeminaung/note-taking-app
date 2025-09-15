package com.example.notetakingapp.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.notetakingapp.data.models.Note

@Dao
interface NoteDao {

    @Query("SELECT * FROM notes WHERE userId = :userId AND isSynced = 0")
    suspend fun getUnsyncedNotes(userId: String): List<Note>

    @Query("SELECT * FROM notes WHERE id = :id")
    suspend fun getNoteById(id: String): Note?

    @Query("SELECT * FROM notes WHERE userId = :userId AND pendingDelete = 1")
    suspend fun getPendingDeleteNotes(userId: String): List<Note>

    suspend fun deleteNoteOffline(note: Note) {
        this.update(
            note.copy(
                pendingDelete = true,
                updatedAt = System.currentTimeMillis(),
                isSynced = false
            )
        )
    }

    @Query("SELECT * FROM notes WHERE userId = :userId AND pendingDelete = 0 ORDER BY id DESC")
    suspend fun indexDefault(userId: String): List<Note>

    @Query("SELECT * FROM notes WHERE userId = :userId AND pendingDelete = 0 ORDER BY createdAt DESC")
    suspend fun indexByDate(userId: String): List<Note>

    @Query(
        """
        SELECT * FROM notes 
        WHERE userId = :userId AND pendingDelete = 0
        ORDER BY 
            CASE priority 
                WHEN 'high' THEN 3
                WHEN 'medium' THEN 2
                WHEN 'low' THEN 1
                ELSE 0
            END DESC,
            createdAt DESC
    """
    )
    suspend fun indexByPriority(userId: String): List<Note>

    @Query("SELECT * FROM notes WHERE id = :id AND userId = :userId AND pendingDelete = 0")
    suspend fun show(id: String, userId: String): Note?

    @Insert
    suspend fun insert(note: Note)

    @Update
    suspend fun update(note: Note)

    @Delete
    suspend fun delete(note: Note)

    @Query("SELECT COUNT(*) FROM notes WHERE userId = :userId AND pendingDelete = 0")
    suspend fun countAllNotes(userId: String): Int

    @Query("SELECT COUNT(*) FROM notes WHERE userId = :userId AND starred = 1 AND pendingDelete = 0")
    suspend fun countStarredNotes(userId: String): Int

    @Query("SELECT * FROM notes WHERE userId = :userId AND starred = 1 AND pendingDelete = 0 ORDER BY id DESC")
    suspend fun getStarredNotesDefault(userId: String): List<Note>

    @Query("SELECT * FROM notes WHERE userId = :userId AND starred = 1 AND pendingDelete = 0 ORDER BY createdAt DESC")
    suspend fun getStarredNotesByDate(userId: String): List<Note>

    @Query(
        """
        SELECT * FROM notes 
        WHERE userId = :userId AND starred = 1 AND pendingDelete = 0
        ORDER BY 
            CASE priority 
                WHEN 'high' THEN 3
                WHEN 'medium' THEN 2
                WHEN 'low' THEN 1
                ELSE 0
            END DESC,
            createdAt DESC
    """
    )
    suspend fun getStarredNotesByPriority(userId: String): List<Note>

    @Query(
        """
        SELECT * FROM notes 
        WHERE userId = :userId AND pendingDelete = 0
        AND (title LIKE '%' || :query || '%' OR body LIKE '%' || :query || '%')
        ORDER BY id DESC
    """
    )
    suspend fun searchNotesDefault(userId: String, query: String): List<Note>

    @Query(
        """
        SELECT * FROM notes 
        WHERE userId = :userId AND pendingDelete = 0
        AND (title LIKE '%' || :query || '%' OR body LIKE '%' || :query || '%')
        ORDER BY createdAt DESC
    """
    )
    suspend fun searchNotesByDate(userId: String, query: String): List<Note>

    @Query(
        """
        SELECT * FROM notes 
        WHERE userId = :userId AND pendingDelete = 0
        AND (title LIKE '%' || :query || '%' OR body LIKE '%' || :query || '%')
        ORDER BY 
            CASE priority 
                WHEN 'high' THEN 3
                WHEN 'medium' THEN 2
                WHEN 'low' THEN 1
                ELSE 0
            END DESC,
            createdAt DESC
    """
    )
    suspend fun searchNotesByPriority(userId: String, query: String): List<Note>

    @Query(
        """
        SELECT * FROM notes 
        WHERE userId = :userId AND starred = 1 AND pendingDelete = 0
        AND (title LIKE '%' || :query || '%' OR body LIKE '%' || :query || '%')
        ORDER BY id DESC
    """
    )
    suspend fun searchStarredNotesDefault(userId: String, query: String): List<Note>

    @Query(
        """
        SELECT * FROM notes 
        WHERE userId = :userId AND starred = 1 AND pendingDelete = 0
        AND (title LIKE '%' || :query || '%' OR body LIKE '%' || :query || '%')
        ORDER BY createdAt DESC
    """
    )
    suspend fun searchStarredNotesByDate(userId: String, query: String): List<Note>

    @Query(
        """
        SELECT * FROM notes
        WHERE userId = :userId AND starred = 1 AND pendingDelete = 0
        AND (title LIKE '%' || :query || '%' OR body LIKE '%' || :query || '%')
        ORDER BY 
            CASE priority 
                WHEN 'high' THEN 3
                WHEN 'medium' THEN 2
                WHEN 'low' THEN 1
                ELSE 0
            END DESC,
            createdAt DESC
    """
    )
    suspend fun searchStarredNotesByPriority(userId: String, query: String): List<Note>
}
