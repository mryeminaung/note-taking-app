package com.example.notetakingapp.fragments

import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.notetakingapp.R
import com.example.notetakingapp.data.database.NoteDatabase
import com.example.notetakingapp.data.models.Note
import com.example.notetakingapp.data.repository.NotesRepository
import com.example.notetakingapp.databinding.FragmentEditNoteBinding
import kotlinx.coroutines.launch

class EditNoteFragment : Fragment(R.layout.fragment_edit_note) {

    private var _binding: FragmentEditNoteBinding? = null
    private val binding get() = _binding!!
    private val args: EditNoteFragmentArgs by navArgs()
    private var noteBgColor: Int = 0
    private var selectedCircle: View? = null
    private var originalNote: Note? = null
    private lateinit var repository: NotesRepository

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditNoteBinding.inflate(inflater, container, false)
        repository = NotesRepository(NoteDatabase.getDatabase(requireContext()).noteDao())
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.backToBtn.setOnClickListener { findNavController().navigateUp() }

        showStickyColors()
        loadNote()

        binding.saveEditNoteBtn.setOnClickListener { updateNote() }
        binding.refreshBtn.setOnClickListener { restoreOriginalState() }
    }

    private fun loadNote() {
        lifecycleScope.launch {
            val note = repository.getNoteById(args.noteId)
            note?.let {
                originalNote = it
                restoreOriginalState()
            }
        }
    }

    private fun restoreOriginalState() {
        originalNote?.let {
            binding.noteTitle.setText(it.title)
            binding.noteBody.setText(it.body)
            binding.noteEditContainer.setBackgroundColor(it.bgColor)
            noteBgColor = it.bgColor
            highlightSelectedColor(it.bgColor)
        }
    }

    private fun updateNote() {
        val updatedTitle = binding.noteTitle.text.toString().trim()
        val updatedBody = binding.noteBody.text.toString().trim()

        when {
            updatedTitle.isEmpty() && updatedBody.isEmpty() -> {
                Toast.makeText(
                    requireContext(),
                    "Title and body cannot be empty",
                    Toast.LENGTH_SHORT
                ).show()
            }

            updatedTitle.isEmpty() -> {
                Toast.makeText(requireContext(), "Title cannot be empty", Toast.LENGTH_SHORT).show()
            }

            updatedBody.isEmpty() -> {
                Toast.makeText(requireContext(), "Body cannot be empty", Toast.LENGTH_SHORT).show()
            }

            else -> {
                // Show priority dialog before saving
                showPriorityDialog { selectedPriority ->
                    lifecycleScope.launch {
                        originalNote?.let {
                            val updatedNote = it.copy(
                                title = updatedTitle,
                                body = updatedBody,
                                bgColor = noteBgColor,
                                priority = selectedPriority // save selected or default
                            )
                            repository.updateNote(updatedNote)
                            findNavController().navigateUp()
                        }
                    }
                }
            }
        }
    }

    private fun showPriorityDialog(onPrioritySelected: (String) -> Unit) {
        val priorities = arrayOf("High", "Medium", "Low")
        var selectedIndex = priorities.indexOf(
            originalNote?.priority?.replaceFirstChar { it.uppercaseChar() } ?: "Low"
        )

        val dialog = com.google.android.material.dialog.MaterialAlertDialogBuilder(requireContext())
            .setTitle("Select Note Priority")
            .setSingleChoiceItems(priorities, selectedIndex) { _, which ->
                selectedIndex = which
            }
            .setPositiveButton("Save") { d, _ ->
                onPrioritySelected(priorities[selectedIndex].lowercase())
                d.dismiss()
            }
            .setNegativeButton("Cancel") { d, _ ->
                onPrioritySelected(originalNote?.priority?.lowercase() ?: "low")
                d.dismiss()
            }
            .show()

        val positiveButton = dialog.getButton(androidx.appcompat.app.AlertDialog.BUTTON_POSITIVE)
        positiveButton.setBackgroundColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.deep_blue
            )
        )
        positiveButton.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
        positiveButton.setPadding(40, 20, 40, 20)

        val negativeButton = dialog.getButton(androidx.appcompat.app.AlertDialog.BUTTON_NEGATIVE)
        negativeButton.setBackgroundColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.sticky_gray
            )
        )
        negativeButton.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
        negativeButton.setPadding(40, 20, 40, 20)
    }


    private fun showStickyColors() {
        val colors = listOf(
            R.color.sticky_yellow, R.color.sticky_blue, R.color.sticky_green,
            R.color.sticky_orange, R.color.sticky_pink, R.color.sticky_lavender,
            R.color.sticky_peach, R.color.sticky_mint, R.color.sticky_gray,
            R.color.sticky_sky, R.color.sticky_lime
        )

        val container = binding.bgColorContainer

        for (colorRes in colors) {
            val circle = View(requireContext()).apply {
                layoutParams =
                    LinearLayout.LayoutParams(120, 120).apply { setMargins(20, 0, 20, 0) }
                val colorInt = ContextCompat.getColor(requireContext(), colorRes)
                background = GradientDrawable().apply {
                    shape = GradientDrawable.RECTANGLE
                    cornerRadius = 16f
                    setColor(colorInt)
                }

                setOnClickListener {
                    noteBgColor = colorInt
                    binding.noteEditContainer.background = GradientDrawable().apply {
                        shape = GradientDrawable.RECTANGLE
                        cornerRadius = 16f
                        setColor(colorInt)
                    }

                    (selectedCircle?.background as? GradientDrawable)?.setStroke(0, 0)
                    (background as GradientDrawable).setStroke(
                        4,
                        ContextCompat.getColor(requireContext(), R.color.black)
                    )
                    selectedCircle = this
                }
            }
            container.addView(circle)
        }
    }

    private fun highlightSelectedColor(colorInt: Int) {
        val container = binding.bgColorContainer
        for (i in 0 until container.childCount) {
            val circle = container.getChildAt(i)
            val bg = circle.background as GradientDrawable
            if (bg.color?.defaultColor == colorInt) {
                bg.setStroke(4, ContextCompat.getColor(requireContext(), R.color.black))
                selectedCircle = circle
            } else {
                bg.setStroke(0, 0)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
