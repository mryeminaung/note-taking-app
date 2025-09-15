package com.example.notetakingapp.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "notes")
data class Note(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val title: String = "",
    val body: String = "",
    val userId: String = "",
    val isSynced: Boolean = false,
    var starred: Boolean = false,
    var priority: String = "low",
    val bgColor: Int = -3155748,
    val pendingDelete: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
)