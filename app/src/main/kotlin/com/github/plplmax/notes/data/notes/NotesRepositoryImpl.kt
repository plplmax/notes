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

package com.github.plplmax.notes.data.notes

import com.github.plplmax.notes.domain.notes.model.InitialNote
import com.github.plplmax.notes.domain.notes.model.Note
import com.github.plplmax.notes.domain.notes.repository.NotesRepository

class NotesRepositoryImpl(private val remoteDataSource: NotesRemoteDataSource) : NotesRepository {
    override fun startGettingNotes(onSuccess: (List<Note>) -> Unit, onFailure: (String) -> Unit) {
        remoteDataSource.startGettingNotes(onSuccess, onFailure)
    }

    override fun stopGettingNotes() {
        remoteDataSource.stopGettingNotes()
    }

    override fun createNote(note: InitialNote): Note {
        return remoteDataSource.createNote(note)
    }

    override fun editNote(note: Note) {
        remoteDataSource.editNote(note)
    }
}