package com.example.roomdata

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.roomdata.databinding.ItemNoteBinding
import java.text.SimpleDateFormat
import java.util.Date

class NoteAdapter(
    private val listener: OnNoteClickListener,
    private var notes: MutableList<Note>,
    private var context: Context)
    : RecyclerView.Adapter<NoteAdapter.NoteViewHolder>() {

    inner class NoteViewHolder(val binding: ItemNoteBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(note: Note) {
            with(binding){
                tvTitle.text = note.title
                tvContent.text = note.content
                if (note.editTime != null) {
                    tvDate.text = formatDate(Date(note.editTime))
                }else{
                    tvDate.text = formatDate(Date(note.createTime))
                }
            }
            itemView.setOnClickListener(){
                listener.onNoteClick(note)
            }
            itemView.setOnLongClickListener(){
                listener.onDeleteClick(note)
                true
            }
        }

        @SuppressLint("SimpleDateFormat")
        fun formatDate(date: Date): String {
            val dateFormat = SimpleDateFormat("yyyy-MM-dd")
            return dateFormat.format(date)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val binding = ItemNoteBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NoteViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return notes.size
    }


    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        val note = notes[position]
        holder.bind(note)

    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateData(newNotes: MutableList<Note>) {
        newNotes.sortByDescending {it.editTime}
        this.notes = newNotes
        notifyDataSetChanged()
    }

    fun filterNote(newNotes: MutableList<Note>) {
        this.notes = newNotes
        notifyDataSetChanged()
    }

    fun updateNote(note: Note) {
        notes.add(0, note)
        notifyItemInserted(0)
    }


}