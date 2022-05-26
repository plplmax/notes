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

package com.github.plplmax.notes.data.auth

import com.github.plplmax.notes.domain.auth.model.UserInitial
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withTimeout

interface UserRemoteDataSource {
    @Throws(Exception::class)
    suspend fun create(userInitial: UserInitial): AuthResult
    suspend fun auth(userInitial: UserInitial): AuthResult
    fun logOut()

    class Base(private val firebaseAuth: FirebaseAuth) : UserRemoteDataSource {
        private companion object {
            const val TIMEOUT = 5000L
        }

        override suspend fun create(userInitial: UserInitial): AuthResult = withTimeout(TIMEOUT) {
            firebaseAuth.createUserWithEmailAndPassword(
                userInitial.email,
                userInitial.password
            )
                .await()
        }

        override suspend fun auth(userInitial: UserInitial): AuthResult = withTimeout(TIMEOUT) {
            firebaseAuth.signInWithEmailAndPassword(
                userInitial.email,
                userInitial.password
            )
                .await()
        }

        override fun logOut() = firebaseAuth.signOut()
    }
}