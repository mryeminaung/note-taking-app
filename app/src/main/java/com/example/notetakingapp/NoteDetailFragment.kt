package com.example.notetakingapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.example.notetakingapp.databinding.FragmentNoteDetailBinding

class NoteDetailFragment : Fragment(R.layout.fragment_note_detail) {

    private var _binding: FragmentNoteDetailBinding? = null
    private val binding get() = _binding!!

    private val args: NoteDetailFragmentArgs by navArgs()  // Correct class name

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNoteDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val noteId = args.noteId
        binding.textNoteId.text = "Note ID: $noteId"
//         TODO: fetch note from repository or pass full Note object
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
