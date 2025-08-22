package com.example.notetakingapp

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController

class NoteDetailFragment : Fragment(R.layout.fragment_note_detail) {
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val noteTitle: TextView = view.findViewById(R.id.note_title)
        val noteBody: TextView = view.findViewById(R.id.note_body)
        val noteDetailView: View = view.findViewById(R.id.note_detail_container)
        val backButton: TextView = view.findViewById(R.id.backToNotesBtn)

        arguments?.let {
            val title = it.getString("title") ?: "No Title"
            val body = it.getString("body") ?: "No Content"
            val bgColor = it.getInt("bgColor")

            noteTitle.text = title
            noteBody.text = body
            noteDetailView.setBackgroundColor(bgColor)
        }

        backButton.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    companion object {
        fun newInstance(title: String, body: String, bgColor: Int): NoteDetailFragment {
            return NoteDetailFragment().apply {
                arguments = Bundle().apply {
                    putString("title", title)
                    putString("body", body)
                    putInt("bgColor", bgColor)
                }
            }
        }
    }
}