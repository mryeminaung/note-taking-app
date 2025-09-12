package com.example.notetakingapp.fragments

import android.app.AlertDialog
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
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
import com.google.android.material.dialog.MaterialAlertDialogBuilder
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
        sortMenu()

        binding.noteSearch.addTextChangedListener { editable ->
            val query = editable.toString()
            if (query.isEmpty()) {
                refreshCurrentTab()
            } else {
                searchNotes(query)
            }
        }

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

    private var selectedIndex = 0 // default selection

    private fun sortMenu() {
        binding.sortBtn.setOnClickListener {
            val items = arrayOf("Default", "Priority", "Date")

            val dialog = MaterialAlertDialogBuilder(requireContext())
                .setTitle("Sort by")
                .setSingleChoiceItems(items, selectedIndex) { _, which ->
                    selectedIndex = which
                }
                .setPositiveButton("OK") { dialog, _ ->
                    when (selectedIndex) {
                        0 -> { /* Sort default */
                        }

                        1 -> { /* Sort by priority */
                        }

                        2 -> { /* Sort by date */
                        }
                    }
                    dialog.dismiss()
                }
                .setNegativeButton("Cancel") { dialog, _ ->
                    dialog.dismiss()
                }
                .show()

            val positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
            positiveButton.setBackgroundColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.deep_blue
                )
            )
            positiveButton.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
            positiveButton.setPadding(40, 20, 40, 20)

            val negativeButton = dialog.getButton(AlertDialog.BUTTON_NEGATIVE)
            negativeButton.setBackgroundColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.sticky_gray
                )
            )
            negativeButton.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
            negativeButton.setPadding(40, 20, 40, 20)
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

    private fun searchNotes(query: String) {
        binding.loadingSpinner.visibility = View.VISIBLE
        binding.notesContainer.visibility = View.GONE
        binding.emptyState.visibility = View.GONE

        viewLifecycleOwner.lifecycleScope.launch {
            val notes = when (binding.noteTabs.selectedTabPosition) {
                0 -> repository.searchNotes(query)          // All Notes
                1 -> repository.searchStarredNotes(query)   // Starred Notes
                else -> emptyList()
            }

            adapter.updateNotes(notes)
            binding.loadingSpinner.visibility = View.GONE

            if (notes.isEmpty()) {
                val tabName =
                    if (binding.noteTabs.selectedTabPosition == 0) "notes" else "starred notes"
                binding.emptyText.text = "No $tabName found for \"$query\""
                binding.emptyText.visibility = View.VISIBLE
                binding.notesContainer.visibility = View.GONE
                binding.emptyState.visibility = View.GONE
            } else {
                binding.emptyText.visibility = View.GONE
                binding.emptyState.visibility = View.GONE
                binding.notesContainer.visibility = View.VISIBLE
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
