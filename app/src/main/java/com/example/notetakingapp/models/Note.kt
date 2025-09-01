package com.example.notetakingapp.models

data class Note(
    val id: Int,
    val title: String,
    val body: String,
    val starred: Boolean,
    val priority: String,
    val bgColor: Int
)