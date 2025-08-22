package com.example.notetakingapp

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController

class NoteCardFragment : Fragment(R.layout.fragment_note_card) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val noteTitle: TextView = view.findViewById(R.id.note_title)
        val noteBody: TextView = view.findViewById(R.id.note_body)
        val noteCard: View = view.findViewById(R.id.note_card)

        arguments?.let { args ->
            val title = args.getString("title") ?: ""
            val body = args.getString("body") ?: ""
            val bgColor = args.getInt("bgColor")

            noteTitle.text = title
            noteBody.text = body
            noteCard.setBackgroundColor(bgColor)

            noteCard.setOnClickListener {
                // Manual navigation without Safe Args
                val bundle = Bundle().apply {
                    putString("title", title)
                    putString("body", body)
                    putInt("bgColor", bgColor)
                }
                findNavController().navigate(R.id.action_notes_to_noteDetail, bundle)
            }
        }
    }

    companion object {
        fun newInstance(title: String, body: String, bgColor: Int): NoteCardFragment {
            return NoteCardFragment().apply {
                arguments = Bundle().apply {
                    putString("title", title)
                    putString("body", body)
                    putInt("bgColor", bgColor)
                }
            }
        }
    }
}