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
import androidx.lifecycle.viewModelScope
import com.github.plplmax.notes.di.AppModule
import com.github.plplmax.notes.domain.core.Result
import com.github.plplmax.notes.domain.notes.usecase.CreateNoteUseCase
import com.github.plplmax.notes.domain.notes.model.Note
import com.github.plplmax.notes.domain.notes.usecase.StartGettingNotesUseCase
import com.github.plplmax.notes.domain.notes.usecase.StopGettingNotesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

interface NotesViewModel {
    fun startGettingNotes(onSuccess: (List<Note>) -> Unit, onFailure: (String) -> Unit)
    fun stopGettingNotes()
    fun createNote(text: String)

    @HiltViewModel
    class Base @Inject constructor(
        private val startGettingNotesUseCase: StartGettingNotesUseCase,
        private val stopGettingNotesUseCase: StopGettingNotesUseCase,
        private val createNoteUseCase: CreateNoteUseCase,
        @AppModule.IoDispatcher private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
    ) : ViewModel(), NotesViewModel {
        private val _notesLiveData = MutableLiveData<List<Note>>()
        val notesLiveData: LiveData<List<Note>> = _notesLiveData

        private val _errorLiveData = MutableLiveData<String>()
        val errorLiveData: LiveData<String> = _errorLiveData

        init {
            viewModelScope.launch(ioDispatcher) {
                startGettingNotes(_notesLiveData::postValue, _errorLiveData::postValue)
            }
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

        override fun createNote(text: String) {
            val note = Note(text)

            viewModelScope.launch(ioDispatcher) {
                val result = createNoteUseCase(note)

                if (result is Result.Fail) {
                    _errorLiveData.postValue(result.e)
                }
            }
        }

        override fun onCleared() {
            super.onCleared()
            stopGettingNotes()
        }
    }
}