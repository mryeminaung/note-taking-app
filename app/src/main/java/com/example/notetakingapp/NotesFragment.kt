package com.example.notetakingapp

import android.os.Bundle
import android.view.View
import androidx.core.graphics.toColorInt
import androidx.fragment.app.Fragment

class NotesFragment : Fragment(R.layout.fragment_notes) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val noteColors = listOf("#FFF9C4", "#BBDEFB", "#C8E6C9", "#FFE0B2", "#F8BBD0", "#E1BEE7")

        val notes = listOf(
            Note(
                "Grocery List",
                "Buy eggs, milk, bread, and fresh vegetables for the week.",
                noteColors[0].toColorInt()
            ),
            Note(
                "Workout Plan",
                "30 mins cardio, 15 mins strength training, and 10 mins stretching.",
                noteColors[1].toColorInt()
            ),
            Note(
                "Project Meeting",
                "Discuss new UI designs, assign backend tasks, and set deadlines.",
                noteColors[2].toColorInt()
            ),
            Note(
                "Book Recommendations",
                "Read 'Atomic Habits', 'Deep Work', and 'Clean Code' this month.",
                noteColors[3].toColorInt()
            ),
            Note(
                "Weekend Trip",
                "Visit the mountain trails, bring camera, and prepare picnic lunch.",
                noteColors[4].toColorInt()
            ),
            Note(
                "Learning Goals",
                "Finish Kotlin coroutines tutorial and start Android Room database project.",
                noteColors[5].toColorInt()
            )
        )

        notes.forEach { note ->
            val noteCardFragment = NoteCardFragment.newInstance(note.title, note.body, note.bgColor)

            childFragmentManager.beginTransaction()
                .add(R.id.notes_container, noteCardFragment)
                .commit()
        }
    }
}