package com.example.notetakingapp.fragments

import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.notetakingapp.R
import com.example.notetakingapp.data.database.NoteDatabase
import com.example.notetakingapp.data.models.Note
import com.example.notetakingapp.data.repository.NotesRepository
import com.example.notetakingapp.databinding.FragmentNewNoteBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

class NewNoteFragment : Fragment(R.layout.fragment_new_note) {

    private var _binding: FragmentNewNoteBinding? = null
    private val binding get() = _binding!!
    private var noteBgColor: Int = 0
    private var selectedCircle: View? = null
    private lateinit var repository: NotesRepository

    private val colors: List<Int> = listOf(
        R.color.sticky_yellow,
        R.color.sticky_blue,
        R.color.sticky_green,
        R.color.sticky_orange,
        R.color.sticky_pink,
        R.color.sticky_lavender,
        R.color.sticky_peach,
        R.color.sticky_mint,
        R.color.sticky_gray,
        R.color.sticky_sky,
        R.color.sticky_lime
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentNewNoteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        repository = NotesRepository(NoteDatabase.getDatabase(requireContext()).noteDao())

        binding.backToNotesBtn.setOnClickListener {
            findNavController().navigate(R.id.action_newNoteFragment_to_notesFragment)
        }

        showStickyColors()

        binding.saveNoteBtn.setOnClickListener { saveNote() }
        binding.refreshBtn.setOnClickListener { resetNote() }
    }

    private fun showStickyColors() {
        val container = binding.bgColorContainer
        noteBgColor = ContextCompat.getColor(requireContext(), R.color.sticky_gray)
        binding.newNote.background = GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            cornerRadius = 16f
            setColor(noteBgColor)
        }

        for (colorRes in colors) {
            val circle = View(requireContext()).apply {
                layoutParams = LinearLayout.LayoutParams(120, 120).apply {
                    setMargins(20, 0, 20, 0)
                }
                val colorInt = ContextCompat.getColor(requireContext(), colorRes)
                background = GradientDrawable().apply {
                    shape = GradientDrawable.RECTANGLE
                    cornerRadius = 16f
                    setColor(colorInt)
                }

                if (colorRes == R.color.sticky_gray) {
                    background = GradientDrawable().apply {
                        shape = GradientDrawable.RECTANGLE
                        cornerRadius = 16f
                        setColor(colorInt)
                        setStroke(4, ContextCompat.getColor(requireContext(), R.color.black))
                    }
                    selectedCircle = this
                }

                setOnClickListener {
                    val selectedColorInt = ContextCompat.getColor(requireContext(), colorRes)
                    noteBgColor = selectedColorInt
                    binding.newNote.background = GradientDrawable().apply {
                        shape = GradientDrawable.RECTANGLE
                        cornerRadius = 16f
                        setColor(selectedColorInt)
                    }
                    selectedCircle?.let { prevCircle ->
                        val prevIndex = container.indexOfChild(prevCircle)
                        colors.getOrNull(prevIndex)?.let { prevColorRes ->
                            val prevColorInt =
                                ContextCompat.getColor(requireContext(), prevColorRes)
                            prevCircle.background = GradientDrawable().apply {
                                shape = GradientDrawable.RECTANGLE
                                cornerRadius = 16f
                                setColor(prevColorInt)
                            }
                        }
                    }
                    background = GradientDrawable().apply {
                        shape = GradientDrawable.RECTANGLE
                        cornerRadius = 16f
                        setColor(selectedColorInt)
                        setStroke(4, ContextCompat.getColor(requireContext(), R.color.black))
                    }
                    selectedCircle = this
                }
            }
            container.addView(circle)
        }
    }

    private fun saveNote() {
        val title = binding.noteTitle.text.toString().trim()
        val body = binding.noteBody.text.toString().trim()

        when {
            title.isEmpty() && body.isEmpty() -> {
                Toast.makeText(
                    requireContext(),
                    "Title and body cannot be empty",
                    Toast.LENGTH_SHORT
                ).show()
            }

            title.isEmpty() -> {
                Toast.makeText(requireContext(), "Title cannot be empty", Toast.LENGTH_SHORT).show()
            }

            body.isEmpty() -> {
                Toast.makeText(requireContext(), "Body cannot be empty", Toast.LENGTH_SHORT).show()
            }

            else -> {
                showPriorityDialog { selectedPriority ->
                    lifecycleScope.launch {
                        repository.insertNote(
                            Note(
                                title = title,
                                body = body,
                                bgColor = noteBgColor,
                                priority = selectedPriority,
                                userId = FirebaseAuth.getInstance().currentUser?.uid.toString()
                            )
                        )
                        findNavController().navigate(R.id.action_newNoteFragment_to_notesFragment)
                    }
                }
            }
        }
    }

    private fun showPriorityDialog(onPrioritySelected: (String) -> Unit) {
        val priorities = arrayOf("High", "Medium", "Low")
        var selectedIndex = 2

        val dialog = MaterialAlertDialogBuilder(requireContext())
            .setIcon(R.drawable.ic_error)
            .setTitle("Select Note Priority")
            .setSingleChoiceItems(priorities, selectedIndex) { _, which ->
                selectedIndex = which
            }
            .setPositiveButton("Save") { d, _ ->
                onPrioritySelected(priorities[selectedIndex].lowercase())
                d.dismiss()
            }
            .setNegativeButton("Cancel") { d, _ ->
                d.dismiss()
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


    private fun resetNote() {
        binding.noteTitle.setText("")
        binding.noteBody.setText("")
        noteBgColor = ContextCompat.getColor(requireContext(), R.color.sticky_gray)
        binding.newNote.background = GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            cornerRadius = 16f
            setColor(noteBgColor)
        }

        val container = binding.bgColorContainer
        for (i in 0 until container.childCount) {
            val circle = container.getChildAt(i)
            val bg = circle.background as GradientDrawable
            if (colors[i] == R.color.sticky_gray) {
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
