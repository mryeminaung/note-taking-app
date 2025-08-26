package com.example.notetakingapp

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
data class Note(
    val title: String,
    val body: String,
    val bgColor: Int
) : Parcelable