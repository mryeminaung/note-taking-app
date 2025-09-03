package com.example.notetakingapp.fragments

import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.notetakingapp.R
import com.example.notetakingapp.data.database.NoteDatabase
import com.example.notetakingapp.data.models.Note
import com.example.notetakingapp.databinding.FragmentEditNoteBinding
import kotlinx.coroutines.launch

class EditNoteFragment : Fragment(R.layout.fragment_edit_note) {
    private var _binding: FragmentEditNoteBinding? = null
    private val binding get() = _binding!!

    private val args: EditNoteFragmentArgs by navArgs()

    private var noteBgColor: Int = 0
    private var selectedCircle: View? = null
    private var originalNote: Note? = null // keep original state

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentEditNoteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.backToBtn.setOnClickListener {
            findNavController().navigateUp()
        }

        showStickyColors()
        showNote()

        binding.saveEditNoteBtn.setOnClickListener {
            updateNote()
        }

        // Refresh button restores original state
        binding.refreshBtn.setOnClickListener {
            restoreOriginalState()
        }
    }

    private fun showNote() {
        val db = NoteDatabase.getDatabase(requireContext())
        val noteDao = db.noteDao()

        val noteId = args.noteId

        lifecycleScope.launch {
            val note = noteDao.show(noteId)
            note?.let {
                originalNote = it // save original note for reset
                restoreOriginalState()
            }
        }
    }

    private fun restoreOriginalState() {
        originalNote?.let {
            binding.noteTitle.setText(it.title)
            binding.noteBody.setText(it.body)

            // reset container bg
            binding.noteEditContainer.setBackgroundColor(it.bgColor)

            // reset noteBgColor
            noteBgColor = it.bgColor

            // re-highlight correct circle
            highlightSelectedColor(it.bgColor)
        }
    }

    private fun updateNote() {
        val db = NoteDatabase.getDatabase(requireContext())
        val noteDao = db.noteDao()

        val noteId = args.noteId
        val updatedTitle = binding.noteTitle.text.toString()
        val updatedBody = binding.noteBody.text.toString()
        val updatedColor = noteBgColor

        lifecycleScope.launch {
            val note = noteDao.show(noteId)
            note?.let {
                val updatedNote = it.copy(
                    title = updatedTitle,
                    body = updatedBody,
                    bgColor = updatedColor
                )
                noteDao.update(updatedNote)
            }
            findNavController().navigateUp()
        }
    }

    private fun showStickyColors() {
        val colors: List<Int> = listOf(
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

        val container = binding.bgColorContainer

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

                setOnClickListener {
                    val selectedColorInt = ContextCompat.getColor(requireContext(), colorRes)
                    noteBgColor = selectedColorInt

                    // Update note background
                    binding.noteEditContainer.background = GradientDrawable().apply {
                        shape = GradientDrawable.RECTANGLE
                        cornerRadius = 16f
                        setColor(selectedColorInt)
                    }

                    // Remove previous highlight
                    (selectedCircle?.background as? GradientDrawable)?.setStroke(0, 0)

                    // Highlight selected
                    (background as GradientDrawable).setStroke(
                        4, ContextCompat.getColor(requireContext(), R.color.black)
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
                // highlight this circle
                bg.setStroke(4, ContextCompat.getColor(requireContext(), R.color.black))
                selectedCircle = circle
            } else {
                // reset others
                bg.setStroke(0, 0)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
