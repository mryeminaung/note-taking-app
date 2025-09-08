package com.example.notetakingapp.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notes")
data class Note(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val body: String,
    var starred: Boolean = false,
    var priority: String = "low",
    val bgColor: Int,
    val createdAt: Long = System.currentTimeMillis() // default = now
)