package com.example.notetakingapp

import NotesAdapter
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.graphics.toColorInt
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.notetakingapp.databinding.FragmentNotesBinding

class NotesFragment : Fragment(R.layout.fragment_notes) {

    private var _binding: FragmentNotesBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentNotesBinding.bind(view)

        val noteColors = listOf("#FFF9C4", "#BBDEFB", "#C8E6C9", "#FFE0B2", "#F8BBD0", "#E1BEE7")

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
            Note(6, "Learning Goals", "Kotlin, Android Nav Graph", noteColors[5].toColorInt())
        )

        val adapter = NotesAdapter(
            notes,
            onEditClick = { note ->
                // TODO: Handle edit
            },
            onNoteClick = { note ->
                Log.d("Test : ", "Clicking a Card")
                val action = NotesFragmentDirections
                    .actionNotesFragmentToNoteDetailFragment(note.id)
                findNavController().navigate(action)
            }
        )

        binding.notesContainer.layoutManager = LinearLayoutManager(requireContext())
//        binding.notesContainer.adapter = NotesAdapter(notes)
        binding.notesContainer.adapter = adapter // use the adapter with listeners
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}