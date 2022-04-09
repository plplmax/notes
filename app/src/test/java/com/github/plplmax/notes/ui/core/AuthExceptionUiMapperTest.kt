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

package com.github.plplmax.notes.ui.core

import android.content.Context
import com.github.plplmax.notes.R
import com.github.plplmax.notes.domain.core.ErrorType
import com.github.plplmax.notes.domain.core.Mapper
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock

class AuthExceptionUiMapperTest {

    @Mock
    private lateinit var mockContext: Context
    private lateinit var authExceptionUiMapper: Mapper<ErrorType, String>

    @Before
    fun before() {
        mockContext = mock {
            on { getString(R.string.invalid_email) } doReturn "Invalid email"
            on { getString(R.string.such_email_already_in_use) } doReturn "Such email is already in use"
            on { getString(R.string.weak_password) } doReturn "Password is weak"
            on { getString(R.string.network_exception) } doReturn "Check a network connection"
            on { getString(R.string.firebase_unknown_error) } doReturn "Unknown error from database"
            on { getString(R.string.general_error) } doReturn "Something went wrong"
        }

        authExceptionUiMapper = AuthExceptionUiMapper(ResourceProvider.Base(mockContext))
    }

    @Test
    fun map_returnsInvalidEmail() {
        val result = authExceptionUiMapper.map(ErrorType.ERROR_INVALID_EMAIL)

        assertEquals(mockContext.getString(R.string.invalid_email), result)
    }

    @Test
    fun map_returnsSuchEmailAlreadyInUse() {
        val result = authExceptionUiMapper.map(ErrorType.ERROR_EMAIL_ALREADY_IN_USE)

        assertEquals(mockContext.getString(R.string.such_email_already_in_use), result)
    }

    @Test
    fun map_returnsWeakPassword() {
        val result = authExceptionUiMapper.map(ErrorType.ERROR_WEAK_PASSWORD)

        assertEquals(mockContext.getString(R.string.weak_password), result)
    }

    @Test
    fun map_returnsCheckNetworkConnection() {
        val result = authExceptionUiMapper.map(ErrorType.NETWORK_EXCEPTION)

        assertEquals(mockContext.getString(R.string.network_exception), result)
    }

    @Test
    fun map_returnsUnknownErrorFromDatabase() {
        val result = authExceptionUiMapper.map(ErrorType.FIREBASE_UNKNOWN_ERROR)

        assertEquals(mockContext.getString(R.string.firebase_unknown_error), result)
    }

    @Test
    fun map_returnsGeneralError() {
        val result = authExceptionUiMapper.map(ErrorType.GENERAL_ERROR)

        assertEquals(mockContext.getString(R.string.general_error), result)
    }
}