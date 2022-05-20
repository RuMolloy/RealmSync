package com.molloyruaidhri.realmsync

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import io.realm.OrderedRealmCollection
import io.realm.RealmRecyclerViewAdapter

class NoteAdapter(notes: OrderedRealmCollection<Note>) :
    RealmRecyclerViewAdapter<Note, NoteAdapter.NoteHolder>(notes, true) {

    class NoteHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val noteName = itemView.findViewById<TextView>(R.id.tv_note)
        private val date = itemView.findViewById<TextView>(R.id.tv_date)

        fun bindValues(note: Note?) {
            noteName.text = note?.noteName
            date.text = note?.date.toString()
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_view, parent, false)
        return NoteHolder(view)

    }


    override fun onBindViewHolder(holder: NoteHolder, position: Int) {
        val note = getItem(position)
        holder.bindValues(note)
    }
}