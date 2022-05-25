/*
 * MIT License
 *
 * Copyright (c) 2022 Maksim Ploski
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.github.plplmax.notes.ui.notes

import android.view.*
import androidx.recyclerview.widget.RecyclerView
import com.github.plplmax.notes.R
import com.github.plplmax.notes.databinding.NoteItemBinding
import com.github.plplmax.notes.domain.notes.model.Note
import com.github.plplmax.notes.ui.note.NoteFragment

class NotesAdapter(private val listener: NoteFragment.ToNoteScreenListener) :
    RecyclerView.Adapter<NotesAdapter.NotesViewHolder>() {

    private var notes = listOf<Note>()
    var note: Note? = null
        private set

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotesViewHolder {
        val binding = NoteItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NotesViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NotesViewHolder, position: Int) {
        val note = notes[position]
        holder.binding.notePreview.text = note.text

        with(holder.binding.cardNote) {
            setOnClickListener {
                listener.navigateToNoteScreenForEdit(note)
            }

            setOnLongClickListener {
                this@NotesAdapter.note = note
                false
            }
        }
    }

    override fun onViewRecycled(holder: NotesViewHolder) {
        holder.binding.cardNote.setOnLongClickListener(null)
        super.onViewRecycled(holder)
    }

    override fun getItemCount() = notes.size

    fun updateNotes(notes: List<Note>) {
        this.notes = notes
        notifyDataSetChanged()
    }


    inner class NotesViewHolder(val binding: NoteItemBinding) :
        RecyclerView.ViewHolder(binding.root), View.OnCreateContextMenuListener {

        init {
            binding.cardNote.setOnCreateContextMenuListener(this)
        }

        override fun onCreateContextMenu(
            menu: ContextMenu?,
            v: View?,
            menuInfo: ContextMenu.ContextMenuInfo?
        ) {
            menu?.add(Menu.NONE, R.id.delete, Menu.NONE, "Delete")
        }
    }
}