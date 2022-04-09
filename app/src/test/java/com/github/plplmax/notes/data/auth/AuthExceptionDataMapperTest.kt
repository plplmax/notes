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

import com.github.plplmax.notes.domain.core.ErrorType
import com.github.plplmax.notes.domain.core.Mapper
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuthException
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class AuthExceptionDataMapperTest {

    private lateinit var authExceptionDataMapper: Mapper<Exception, ErrorType>

    @Before
    fun before() {
        authExceptionDataMapper = AuthExceptionDataMapper()

    }

    @Test
    fun map_returnsInvalidEmail() {
        val result = authExceptionDataMapper.map(FirebaseAuthException("ERROR_INVALID_EMAIL", ""))

        assertEquals(ErrorType.ERROR_INVALID_EMAIL, result)
    }

    @Test
    fun map_returnsSuchEmailAlreadyInUse() {
        val result =
            authExceptionDataMapper.map(FirebaseAuthException("ERROR_EMAIL_ALREADY_IN_USE", ""))

        assertEquals(ErrorType.ERROR_EMAIL_ALREADY_IN_USE, result)
    }

    @Test
    fun map_returnsWeakPassword() {
        val result = authExceptionDataMapper.map(FirebaseAuthException("ERROR_WEAK_PASSWORD", ""))

        assertEquals(ErrorType.ERROR_WEAK_PASSWORD, result)
    }

    @Test
    fun map_returnsUnknownErrorFromDatabase() {
        val result = authExceptionDataMapper.map(FirebaseAuthException("RANDOM_ERROR", ""))

        assertEquals(ErrorType.FIREBASE_UNKNOWN_ERROR, result)
    }

    @Test
    fun map_returnsCheckNetworkConnection() {
        val result = authExceptionDataMapper.map(FirebaseNetworkException(""))

        assertEquals(ErrorType.NETWORK_EXCEPTION, result)
    }

    @Test
    fun map_returnsGeneralError() {
        val result = authExceptionDataMapper.map(Exception())

        assertEquals(ErrorType.GENERAL_ERROR, result)
    }
}