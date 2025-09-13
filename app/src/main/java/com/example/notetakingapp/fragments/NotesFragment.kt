package com.example.notetakingapp.fragments

import android.app.AlertDialog
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.content.edit
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
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

class NotesFragment : Fragment(R.layout.fragment_notes) {

    private var currentUserId = FirebaseAuth.getInstance().currentUser?.uid.toString()
    private var _binding: FragmentNotesBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: NotesAdapter
    private lateinit var repository: NotesRepository

    private var selectedIndex = 0 // default selection
    private var currentSortType = "default" // current sort type

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentNotesBinding.bind(view)

        currentSortType = loadSortType()
        selectedIndex = when (currentSortType) {
            "default" -> 0
            "priority" -> 1
            "date" -> 2
            else -> 0
        }
        
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
        getAllNotes(currentSortType)
        sortMenu()

        binding.noteSearch.addTextChangedListener { editable ->
            val query = editable.toString()
            if (query.isEmpty()) {
                refreshCurrentTab()
            } else {
                searchNotes(query, currentSortType)
            }
        }

        binding.noteTabs.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                refreshCurrentTab()
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })

        binding.newNoteBtn.setOnClickListener {
            findNavController().navigate(R.id.action_notesFragment_to_newNoteFragment)
        }
    }

    private fun saveSortType(sortType: String) {
        val prefs = requireContext().getSharedPreferences("settings", 0)
        prefs.edit { putString("sortType", sortType) }
    }

    private fun loadSortType(): String {
        val prefs = requireContext().getSharedPreferences("settings", 0)
        return prefs.getString("sortType", "default") ?: "default"
    }

    private fun refreshCurrentTab() {
        when (binding.noteTabs.selectedTabPosition) {
            0 -> getAllNotes(currentSortType)
            1 -> getStarredNotes(currentSortType)
        }
    }

    private fun sortMenu() {
        binding.sortBtn.setOnClickListener {
            val items = arrayOf("Default", "Priority", "Date")

            val dialog = MaterialAlertDialogBuilder(requireContext())
                .setTitle("Sort by")
                .setSingleChoiceItems(items, selectedIndex) { _, which ->
                    selectedIndex = which
                }
                .setPositiveButton("OK") { dialog, _ ->
                    currentSortType = when (selectedIndex) {
                        0 -> "default"
                        1 -> "priority"
                        2 -> "date"
                        else -> "default"
                    }
                    saveSortType(currentSortType)
                    refreshCurrentTab()
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

    private fun getAllNotes(sortType: String = "default") {
        binding.loadingSpinner.visibility = View.VISIBLE
        binding.notesContainer.visibility = View.GONE
        binding.emptyState.visibility = View.GONE

        viewLifecycleOwner.lifecycleScope.launch {
            val notes = repository.getAllNotes(currentUserId, sortType)
            adapter.updateNotes(notes)
            binding.loadingSpinner.visibility = View.GONE
            binding.emptyState.visibility = if (notes.isEmpty()) View.VISIBLE else View.GONE
            binding.notesContainer.visibility = if (notes.isEmpty()) View.GONE else View.VISIBLE
        }
    }

    private fun getStarredNotes(sortType: String = "default") {
        binding.loadingSpinner.visibility = View.VISIBLE
        binding.notesContainer.visibility = View.GONE
        binding.emptyState.visibility = View.GONE

        viewLifecycleOwner.lifecycleScope.launch {
            val notes = repository.getStarredNotes(currentUserId, sortType)
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

    private fun searchNotes(query: String, sortType: String = "default") {
        binding.loadingSpinner.visibility = View.VISIBLE
        binding.notesContainer.visibility = View.GONE
        binding.emptyState.visibility = View.GONE

        viewLifecycleOwner.lifecycleScope.launch {
            val notes = when (binding.noteTabs.selectedTabPosition) {
                0 -> repository.searchNotes(currentUserId, query, sortType)
                1 -> repository.searchStarredNotes(currentUserId, query, sortType)
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
