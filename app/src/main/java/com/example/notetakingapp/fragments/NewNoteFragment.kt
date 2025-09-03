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
import com.example.notetakingapp.R
import com.example.notetakingapp.data.database.NoteDatabase
import com.example.notetakingapp.data.models.Note
import com.example.notetakingapp.databinding.FragmentNewNoteBinding
import kotlinx.coroutines.launch

class NewNoteFragment : Fragment(R.layout.fragment_new_note) {

    private var _binding: FragmentNewNoteBinding? = null
    private val binding get() = _binding!!

    private var noteBgColor: Int = 0
    private var selectedCircle: View? = null

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

        binding.backToNotesBtn.setOnClickListener {
            findNavController().navigate(R.id.action_newNoteFragment_to_notesFragment)
        }

        showStickyColors()

        val db = NoteDatabase.getDatabase(requireContext())
        val noteDao = db.noteDao()

        binding.saveNoteBtn.setOnClickListener {
            lifecycleScope.launch {
                noteDao.insert(
                    Note(
                        title = binding.noteTitle.text.toString(),
                        body = binding.noteBody.text.toString(),
                        bgColor = noteBgColor
                    )
                )
            }
            findNavController().navigate(R.id.action_newNoteFragment_to_notesFragment)
        }

        binding.refreshBtn.setOnClickListener {
            resetNote()
        }
    }

    private fun showStickyColors() {
        val container = binding.bgColorContainer

        // Default color
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

                // Highlight default gray
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

                    // Update note background
                    binding.newNote.background = GradientDrawable().apply {
                        shape = GradientDrawable.RECTANGLE
                        cornerRadius = 16f
                        setColor(selectedColorInt)
                    }

                    // Remove previous highlight
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

                    // Highlight selected
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

    private fun resetNote() {
        // Reset title and body
        binding.noteTitle.setText("")
        binding.noteBody.setText("")

        // Reset background color to default gray
        noteBgColor = ContextCompat.getColor(requireContext(), R.color.sticky_gray)
        binding.newNote.background = GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            cornerRadius = 16f
            setColor(noteBgColor)
        }

        // Reset color picker highlight
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
