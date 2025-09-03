package com.example.notetakingapp.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.notetakingapp.R
import com.example.notetakingapp.adapters.NotesAdapter
import com.example.notetakingapp.data.database.NoteDatabase
import com.example.notetakingapp.data.models.Note
import com.example.notetakingapp.databinding.FragmentNotesBinding
import kotlinx.coroutines.launch

class NotesFragment : Fragment(R.layout.fragment_notes) {

    private var _binding: FragmentNotesBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: NotesAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentNotesBinding.bind(view)

        adapter = NotesAdapter(
            onDeleteClick = { note ->
                deleteNote(note)
            },
            onNoteClick = { note ->
                val action = NotesFragmentDirections
                    .actionNotesFragmentToNoteDetailFragment(
                        noteId = note.id,
                    )
                findNavController().navigate(action)
            }
        )

        getAllNotes()

        binding.notesContainer.layoutManager = LinearLayoutManager(requireContext())
        binding.notesContainer.adapter = adapter

        binding.newNoteBtn.setOnClickListener {
            findNavController().navigate(R.id.action_notesFragment_to_newNoteFragment)
        }
    }

    private fun getAllNotes() {
        val db = NoteDatabase.getDatabase(requireContext())
        val noteDao = db.noteDao()

        viewLifecycleOwner.lifecycleScope.launch {
            val notes = noteDao.index()
            adapter.updateNotes(notes) // update RecyclerView
        }
    }

    private fun deleteNote(note: Note) {
        val db = NoteDatabase.getDatabase(requireContext())
        val noteDao = db.noteDao()

        viewLifecycleOwner.lifecycleScope.launch {
            noteDao.delete(note)
            getAllNotes()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}