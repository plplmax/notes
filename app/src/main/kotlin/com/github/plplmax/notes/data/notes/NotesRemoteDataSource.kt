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

import com.github.plplmax.notes.domain.notes.Note
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await
import timber.log.Timber

interface NotesRemoteDataSource {
    fun startGettingNotes(onSuccess: (List<Note>) -> Unit, onFailure: (String) -> Unit)
    fun stopGettingNotes()
    suspend fun createNote(note: Note)

    class Base(private val database: FirebaseDatabase) : NotesRemoteDataSource {
        private var callback: ValueEventListener? = null

        override fun startGettingNotes(
            onSuccess: (List<Note>) -> Unit,
            onFailure: (String) -> Unit
        ) {
            val notes = database.reference
                .child("users")
                .child(Firebase.auth.uid!!)
                .child("notes")

            callback = object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val listOfNotes = snapshot.children
                        .mapNotNull { it.getValue(Note::class.java) }
                    onSuccess(listOfNotes)
                }

                override fun onCancelled(error: DatabaseError) {
                    onFailure(error.message)
                }
            }

            notes.addValueEventListener(callback as ValueEventListener)
        }

        override fun stopGettingNotes() {
            if (callback == null) return

            database.reference
                .child("users")
                .child(Firebase.auth.uid!!)
                .child("notes")
                .removeEventListener(callback!!)
        }

        override suspend fun createNote(note: Note) {
            val notesReference = database.reference
                .child("users")
                .child(Firebase.auth.uid!!)
                .child("notes")

            val key = notesReference.push().key

            if (key == null) {
                Timber.e("While executing createNote key was null")
            }

            notesReference.updateChildren(
                mapOf("/$key" to note)
            )
                .await()
        }
    }
}