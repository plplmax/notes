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

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.github.plplmax.notes.domain.notes.model.Note
import com.github.plplmax.notes.domain.notes.usecase.DeleteAllNotesUseCase
import com.github.plplmax.notes.domain.notes.usecase.DeleteNoteUseCase
import com.github.plplmax.notes.domain.notes.usecase.StartGettingNotesUseCase
import com.github.plplmax.notes.domain.notes.usecase.StopGettingNotesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

interface NotesViewModel {
    fun startGettingNotes(onSuccess: (List<Note>) -> Unit, onFailure: (String) -> Unit)
    fun stopGettingNotes()
    fun deleteNote(note: Note)
    fun deleteAllNotes()

    @HiltViewModel
    class Base @Inject constructor(
        private val startGettingNotesUseCase: StartGettingNotesUseCase,
        private val stopGettingNotesUseCase: StopGettingNotesUseCase,
        private val deleteNoteUseCase: DeleteNoteUseCase,
        private val deleteAllNotesUseCase: DeleteAllNotesUseCase
    ) : ViewModel(), NotesViewModel {
        private val _notesLiveData = MutableLiveData<List<Note>>()
        val notesLiveData: LiveData<List<Note>> = _notesLiveData

        private val _errorLiveData = MutableLiveData<String>()
        val errorLiveData: LiveData<String> = _errorLiveData

        init {
            startGettingNotes(_notesLiveData::postValue, _errorLiveData::postValue)
        }

        override fun startGettingNotes(
            onSuccess: (List<Note>) -> Unit,
            onFailure: (String) -> Unit
        ) {
            startGettingNotesUseCase(onSuccess, onFailure)
        }

        override fun stopGettingNotes() {
            stopGettingNotesUseCase()
        }

        override fun deleteNote(note: Note) {
            deleteNoteUseCase(note)
        }

        override fun deleteAllNotes() {
            deleteAllNotesUseCase()
        }

        override fun onCleared() {
            stopGettingNotes()
            super.onCleared()
        }
    }
}