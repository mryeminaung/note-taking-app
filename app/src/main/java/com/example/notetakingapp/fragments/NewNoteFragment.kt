package com.example.notetakingapp.fragments

import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.notetakingapp.R
import com.example.notetakingapp.databinding.FragmentNewNoteBinding

class NewNoteFragment : Fragment(R.layout.fragment_new_note) {

    private var _binding: FragmentNewNoteBinding? = null
    private val binding get() = _binding!!

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

        // Back button
        binding.backToNotesBtn.setOnClickListener {
            findNavController().navigate(R.id.action_newNoteFragment_to_notesFragment)
        }

        val container = binding.bgColorContainer

        for (colorRes in colors) {
            val circle = View(requireContext()).apply {
                layoutParams = LinearLayout.LayoutParams(120, 120).apply {
                    setMargins(20, 0, 20, 0)
                }

                // Initial circle background
                background = GradientDrawable().apply {
                    shape = GradientDrawable.OVAL
                    setColor(ContextCompat.getColor(requireContext(), colorRes))
                }

                setOnClickListener {
                    val selectedColor = ContextCompat.getColor(requireContext(), colorRes)

                    // Update note background ONLY when a color is selected
                    binding.newNote.background = GradientDrawable().apply {
                        shape = GradientDrawable.RECTANGLE
                        cornerRadius = 16f
                        setColor(selectedColor)
                    }

                    // Remove highlight from previous circle
                    selectedCircle?.let { prevCircle ->
                        val prevIndex = container.indexOfChild(prevCircle)
                        colors.getOrNull(prevIndex)?.let { prevColorRes ->
                            val prevColor = ContextCompat.getColor(requireContext(), prevColorRes)
                            prevCircle.background = GradientDrawable().apply {
                                shape = GradientDrawable.OVAL
                                setColor(prevColor)
                            }
                        }
                    }

                    // Highlight the selected circle with a border
                    background = GradientDrawable().apply {
                        shape = GradientDrawable.OVAL
                        setColor(selectedColor)
                        setStroke(6, ContextCompat.getColor(requireContext(), R.color.black))
                    }

                    selectedCircle = this
                }
            }

            container.addView(circle)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
