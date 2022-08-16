package ru.lglass.notes.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ru.lglass.notes.structures.Note
import ru.lglass.notes.R

class AdapterRecyclerNotes(private val notes: List<Note>, private val listener: OnItemClickListener)
    : RecyclerView.Adapter<AdapterRecyclerNotes.NoteViewHolder>(){

    public interface OnItemClickListener{
        fun onItemClick(note: Note, context: Context)
    }

    class NoteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        private val noteName: TextView = itemView.findViewById(R.id.tvNoteName)
        private val noteDescription: TextView = itemView.findViewById(R.id.tvNoteDescription)
        public fun bind(note: Note, listener: OnItemClickListener){
            noteName.text = note.name
            noteDescription.text = note.description
            itemView.setOnClickListener { listener.onItemClick(note, itemView.context) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val itemView =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_note, parent, false)
        return NoteViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        holder.bind(notes[position], listener)
    }

    override fun getItemCount() = notes.size
}