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
import com.example.notetakingapp.data.repository.NotesRepository
import com.example.notetakingapp.databinding.FragmentNotesBinding
import com.google.android.material.tabs.TabLayout
import kotlinx.coroutines.launch

class NotesFragment : Fragment(R.layout.fragment_notes) {

    private var _binding: FragmentNotesBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: NotesAdapter
    private lateinit var repository: NotesRepository

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentNotesBinding.bind(view)

        repository = NotesRepository(NoteDatabase.getDatabase(requireContext()).noteDao())

        adapter = NotesAdapter(
            repository = repository,
            onDeleteClick = { note -> deleteNote(note) },
            onNoteClick = { note ->
                val action =
                    NotesFragmentDirections.actionNotesFragmentToNoteDetailFragment(note.id)
                findNavController().navigate(action)
            },
            onStarClick = { refreshCurrentTab() }
        )

        binding.notesContainer.layoutManager = LinearLayoutManager(requireContext())
        binding.notesContainer.adapter = adapter

        binding.noteTabs.getTabAt(0)?.select()
        getAllNotes()

        binding.noteTabs.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                when (tab.position) {
                    0 -> getAllNotes()
                    1 -> getStarredNotes()
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })

        binding.newNoteBtn.setOnClickListener {
            findNavController().navigate(R.id.action_notesFragment_to_newNoteFragment)
        }
    }

    private fun refreshCurrentTab() {
        when (binding.noteTabs.selectedTabPosition) {
            0 -> getAllNotes()
            1 -> getStarredNotes()
        }
    }

    private fun getAllNotes() {
        binding.loadingSpinner.visibility = View.VISIBLE
        binding.notesContainer.visibility = View.GONE
        binding.emptyState.visibility = View.GONE

        viewLifecycleOwner.lifecycleScope.launch {
            val notes = repository.getAllNotes()
            adapter.updateNotes(notes)
            binding.loadingSpinner.visibility = View.GONE
            if (notes.isEmpty()) {
                binding.emptyText.visibility = View.GONE
                binding.emptyState.visibility = View.VISIBLE
                binding.notesContainer.visibility = View.GONE
            } else {
                binding.emptyText.visibility = View.GONE
                binding.emptyState.visibility = View.GONE
                binding.notesContainer.visibility = View.VISIBLE
            }
        }
    }

    private fun getStarredNotes() {
        binding.loadingSpinner.visibility = View.VISIBLE
        binding.notesContainer.visibility = View.GONE
        binding.emptyState.visibility = View.GONE

        viewLifecycleOwner.lifecycleScope.launch {
            val notes = repository.getStarredNotes()
            adapter.updateNotes(notes)
            binding.loadingSpinner.visibility = View.GONE
            if (notes.isEmpty()) {
                binding.emptyText.text = "No starred notes yet ⭐"
                binding.emptyText.visibility = View.VISIBLE
                binding.emptyState.visibility = View.GONE
                binding.notesContainer.visibility = View.GONE
            } else {
                binding.emptyText.visibility = View.GONE
                binding.emptyState.visibility = View.GONE
                binding.notesContainer.visibility = View.VISIBLE
            }
        }
    }

    private fun deleteNote(note: Note) {
        viewLifecycleOwner.lifecycleScope.launch {
            repository.deleteNote(note)
            refreshCurrentTab()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
