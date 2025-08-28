package com.example.notetakingapp.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.graphics.toColorInt
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.notetakingapp.R
import com.example.notetakingapp.adapters.NotesAdapter
import com.example.notetakingapp.databinding.FragmentNotesBinding
import com.example.notetakingapp.models.Note

class NotesFragment : Fragment(R.layout.fragment_notes) {

    private var _binding: FragmentNotesBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentNotesBinding.bind(view)

        val noteColors = listOf(
            "#FFF9C4", // sticky_yellow
            "#BBDEFB", // sticky_blue
            "#C8E6C9", // sticky_green
            "#FFE0B2", // sticky_orange
            "#F8BBD0", // sticky_pink
            "#E1BEE7", // sticky_lavender
            "#FFCCBC", // sticky_peach
            "#B2DFDB", // sticky_mint
            "#CFD8DC", // sticky_gray
            "#B3E5FC", // sticky_sky
            "#F0F4C3"  // sticky_lime
        )

        val notes = listOf(
            Note(1, "Grocery List", "Milk, Bread, Eggs, Cheese", noteColors[0].toColorInt()),
            Note(2, "Workout Plan", "Leg day, back day, arm day", noteColors[1].toColorInt()),
            Note(
                3,
                "Project Meeting",
                "Discuss new UI designs, set deadlines",
                noteColors[2].toColorInt()
            ),
            Note(4, "Book Recommendations", "The Alchemist, Sapiens", noteColors[3].toColorInt()),
            Note(5, "Weekend Trip", "Pack clothes, book hotel", noteColors[4].toColorInt()),
            Note(6, "Learning Goals", "Kotlin, Android Nav Graph", noteColors[5].toColorInt()),
            Note(7, "Birthday Reminder", "Buy gift for Sarah", noteColors[6].toColorInt()),
            Note(8, "Meditation", "Morning meditation for 15 mins", noteColors[7].toColorInt()),
            Note(9, "House Chores", "Vacuum, laundry, dishes", noteColors[8].toColorInt()),
            Note(10, "Meeting Notes", "Client feedback on app", noteColors[9].toColorInt()),
            Note(11, "Gardening", "Water plants, trim bushes", noteColors[10].toColorInt())
        )

        binding.newNoteBtn.setOnClickListener {
            findNavController().navigate(R.id.action_notesFragment_to_newNoteFragment)
        }

        val adapter = NotesAdapter(
            notes,
            onEditClick = { note ->
                Log.d("editNote", "Note Edit")
            },
            onNoteClick = { note ->
                val action = NotesFragmentDirections
                    .actionNotesFragmentToNoteDetailFragment(
                        noteId = note.id,
                        noteTitle = note.title,
                        noteBody = note.body,
                        noteBgColor = note.bgColor
                    )
                findNavController().navigate(action)
            }
        )

        binding.notesContainer.layoutManager = LinearLayoutManager(requireContext())
        binding.notesContainer.adapter = adapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}