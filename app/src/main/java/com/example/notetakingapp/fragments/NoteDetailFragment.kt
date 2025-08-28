package com.example.notetakingapp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.notetakingapp.R
import com.example.notetakingapp.databinding.FragmentNoteDetailBinding

class NoteDetailFragment : Fragment(R.layout.fragment_note_detail) {

    private var _binding: FragmentNoteDetailBinding? = null
    private val binding get() = _binding!!

    private val args: NoteDetailFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNoteDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val noteId = args.noteId.toString()
        val noteTitle = args.noteTitle.toString()
        val noteBody = args.noteBody.toString()
        val noteBgColor = args.noteBgColor
        binding.noteTitle.text = noteTitle
        binding.noteBody.text = noteBody
        binding.noteDetailContainer.setBackgroundColor(noteBgColor)

        binding.backToNotesBtn.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}