package com.example.notetakingapp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.notetakingapp.databinding.FragmentNoteCardBinding
import com.example.notetakingapp.models.Note

class NotesAdapter(
    private val notes: List<Note>,
    private val onEditClick: (Note) -> Unit = {},
    private val onNoteClick: (Note) -> Unit = {}
) : RecyclerView.Adapter<NotesAdapter.NoteViewHolder>() {

    inner class NoteViewHolder(val binding: FragmentNoteCardBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val binding = FragmentNoteCardBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return NoteViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        val note = notes[position]

        holder.binding.apply {
            noteTitle.text = note.title
            noteBody.text = note.body
            textDate.text = "3-8-2025"  // placeholder date
            noteCardContainer.setCardBackgroundColor(note.bgColor)

            iconEdit.setOnClickListener { onEditClick(note) }

            root.setOnClickListener { onNoteClick(note) }
        }
    }

    override fun getItemCount() = notes.size
}
