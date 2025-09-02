package com.example.notetakingapp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.notetakingapp.data.models.Note
import com.example.notetakingapp.databinding.FragmentNoteCardBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class NotesAdapter(
    private var notes: List<Note> = emptyList(),
    private val onDeleteClick: (Note) -> Unit = {},
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
            val sdf = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
            textDate.text = sdf.format(Date(note.createdAt))
            noteCardContainer.setCardBackgroundColor(note.bgColor)

            iconDelete.setOnClickListener { onDeleteClick(note) }

            root.setOnClickListener { onNoteClick(note) }
        }
    }

    fun updateNotes(newNotes: List<Note>) {
        notes = newNotes
        notifyDataSetChanged()
    }

    override fun getItemCount() = notes.size
}
