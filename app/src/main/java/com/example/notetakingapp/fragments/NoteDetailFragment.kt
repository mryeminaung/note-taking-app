package com.example.notetakingapp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.notetakingapp.R
import com.example.notetakingapp.data.database.NoteDatabase
import com.example.notetakingapp.databinding.FragmentNoteDetailBinding
import kotlinx.coroutines.launch

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

        showNote()

        binding.backToNotesBtn.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.editNoteBtn.setOnClickListener {
            val action = NoteDetailFragmentDirections
                .actionNoteDetailFragmentToEditNoteFragment(args.noteId)
            findNavController().navigate(action)
        }
    }

    private fun showNote() {
        val db = NoteDatabase.getDatabase(requireContext())
        val noteDao = db.noteDao()

        val noteId = args.noteId

        lifecycleScope.launch {
            val note = noteDao.show(noteId)
            note?.let {
                binding.noteTitle.text = it.title
                binding.noteBody.text = it.body
                binding.noteDetailContainer.setBackgroundColor(it.bgColor)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}