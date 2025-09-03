package com.example.notetakingapp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.animation.ScaleAnimation
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.notetakingapp.R
import com.example.notetakingapp.data.database.NoteDatabase
import com.example.notetakingapp.data.models.Note
import com.example.notetakingapp.databinding.FragmentNoteCardBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
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
        val context = holder.binding.root.context

        holder.binding.apply {
            noteTitle.text = note.title
            noteBody.text = note.body
            noteCardContainer.setCardBackgroundColor(note.bgColor)

            // Star icon color
            iconStar.setColorFilter(
                if (note.starred) ContextCompat.getColor(context, R.color.rainbow_yellow)
                else ContextCompat.getColor(context, R.color.black)
            )

            iconStar.setOnClickListener {
                note.starred = !note.starred
                val color = if (note.starred) R.color.rainbow_yellow else R.color.black
                iconStar.setColorFilter(ContextCompat.getColor(context, color))

                val anim = ScaleAnimation(
                    0.7f, 1f, 0.7f, 1f,
                    ScaleAnimation.RELATIVE_TO_SELF, 0.5f,
                    ScaleAnimation.RELATIVE_TO_SELF, 0.5f
                )
                anim.duration = 150
                iconStar.startAnimation(anim)

                // Update DB asynchronously
                CoroutineScope(Dispatchers.IO).launch {
                    val db = NoteDatabase.getDatabase(context)
                    db.noteDao().update(note)
                }
            }

            iconDelete.setOnClickListener {
                val dialog = MaterialAlertDialogBuilder(context, R.style.CustomMaterialDialog)
                    .setTitle("\uD83D\uDDD1\uFE0F Delete Note?")
                    .setMessage("Are you sure you want to delete this note?")
                    .setNegativeButton("No") { d, _ -> d.dismiss() }
                    .setPositiveButton("Yes") { d, _ ->
                        onDeleteClick(note)
                        d.dismiss()
                    }
                    .show()

                // Customize buttons
                val positiveButton =
                    dialog.getButton(androidx.appcompat.app.AlertDialog.BUTTON_POSITIVE)
                positiveButton.setBackgroundColor(
                    ContextCompat.getColor(
                        context,
                        R.color.deep_blue
                    )
                )

                positiveButton.setTextColor(ContextCompat.getColor(context, R.color.white))
                positiveButton.setPadding(40, 20, 40, 20)

                val negativeButton =
                    dialog.getButton(androidx.appcompat.app.AlertDialog.BUTTON_NEGATIVE)
                negativeButton.setBackgroundColor(
                    ContextCompat.getColor(
                        context,
                        R.color.sticky_gray
                    )
                )
                negativeButton.setTextColor(ContextCompat.getColor(context, R.color.black))
                negativeButton.setPadding(40, 20, 40, 20)
            }


            // Note click
            root.setOnClickListener { onNoteClick(note) }

            // Date formatting
            textDate.text = formatNoteDate(note.createdAt)
        }
    }

    private fun formatNoteDate(timestamp: Long): String {
        val now = System.currentTimeMillis()
        val diff = now - timestamp
        val sevenDaysInMillis = 7 * 24 * 60 * 60 * 1000L
        return if (diff < sevenDaysInMillis) {
            android.text.format.DateUtils.getRelativeTimeSpanString(
                timestamp,
                now,
                android.text.format.DateUtils.DAY_IN_MILLIS,
                android.text.format.DateUtils.FORMAT_ABBREV_RELATIVE
            ).toString()
        } else {
            val sdf = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
            sdf.format(Date(timestamp))
        }
    }

    fun updateNotes(newNotes: List<Note>) {
        notes = newNotes
        notifyDataSetChanged()
    }

    override fun getItemCount() = notes.size
}
