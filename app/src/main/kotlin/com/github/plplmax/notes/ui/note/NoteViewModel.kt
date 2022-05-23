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

package com.github.plplmax.notes.ui.note

import androidx.lifecycle.ViewModel
import com.github.plplmax.notes.domain.notes.model.InitialNote
import com.github.plplmax.notes.domain.notes.model.Note
import com.github.plplmax.notes.domain.notes.usecase.CreateNoteUseCase
import com.github.plplmax.notes.domain.notes.usecase.EditNoteUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

interface NoteViewModel {
    fun submitNote(note: Note)
    fun submitText(text: String)

    @HiltViewModel
    class Base @Inject constructor(
        private val createNoteUseCase: CreateNoteUseCase,
        private val editNoteUseCase: EditNoteUseCase
    ) : ViewModel(), NoteViewModel {

        var note: Note? = null
            private set

        override fun submitNote(note: Note) {
            this.note = note
        }

        override fun submitText(text: String) {
            if (text.trim().isEmpty()) return

            note = if (note == null) {
                createNoteUseCase(InitialNote(text))
            } else {
                val editedNote = Note(note!!.id, text)

                editNoteUseCase(editedNote)
                editedNote
            }
        }
    }
}