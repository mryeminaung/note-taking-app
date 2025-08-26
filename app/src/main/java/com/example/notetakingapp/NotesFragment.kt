package com.example.notetakingapp

import android.os.Bundle
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

        // Sample data
        val noteColors = listOf("#FFF9C4", "#BBDEFB", "#C8E6C9", "#FFE0B2", "#F8BBD0", "#E1BEE7")

        val notes = listOf(
            Note("Grocery List", "Milk, Bread, Eggs, Cheese", noteColors[0].toColorInt()),
            Note("Workout Plan", "Leg day, back day, arm day", noteColors[1].toColorInt()),
            Note(
                "Project Meeting",
                "Discuss new UI designs, set deadlines",
                noteColors[2].toColorInt()
            ),
            Note("Book Recommendations", "The Alchemist, Sapiens", noteColors[3].toColorInt()),
            Note("Weekend Trip", "Pack clothes, book hotel", noteColors[4].toColorInt()),
            Note("Learning Goals", "Kotlin, Android Nav Graph", noteColors[5].toColorInt())
        )

        // Setup RecyclerView
        binding.notesContainer.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = NoteAdapter(notes) { note ->
                val action = NotesFragmentDirections.actionNotesFragmentToNoteDetailFragment(
                    note.title,
                    note.body,
                    note.bgColor
                )
                findNavController().navigate(action)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}