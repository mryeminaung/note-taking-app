package com.example.notetakingapp.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "notes")
data class Note(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val body: String,
    var starred: Boolean = false,
    var priority: String = "low",
    val bgColor: Int,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val userId: String,
    val isSynced: Boolean = false
)