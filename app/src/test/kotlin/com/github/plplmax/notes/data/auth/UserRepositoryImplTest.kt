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

import com.github.plplmax.notes.domain.auth.model.User
import com.github.plplmax.notes.domain.auth.model.UserInitial
import com.github.plplmax.notes.domain.core.ErrorType
import com.github.plplmax.notes.domain.core.Mapper
import com.github.plplmax.notes.domain.core.Result
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.doThrow
import org.mockito.kotlin.mock
import org.mockito.kotlin.stub

class UserRepositoryImplTest {

    private val remoteDataSource: UserRemoteDataSource = mock()
    private val exceptionMapper: Mapper<Exception, ErrorType> = AuthExceptionDataMapper()

    @Before
    fun before() {
        val mockExistingUser = mock<FirebaseUser> {
            on { uid } doReturn "1400"
            on { email } doReturn "error@mail.ru"
        }

        val mockAuthResult = mock<AuthResult> {
            on { user } doReturn mockExistingUser
        }

        remoteDataSource.stub {
            onBlocking {
                create(
                    UserInitial(
                        "error@mail.ru",
                        "qwerty12345"
                    )
                )
            } doReturn mockAuthResult
            onBlocking {
                create(
                    UserInitial(
                        "testsystem@gmail.com",
                        "test123"
                    )
                )
            } doThrow FirebaseAuthException("ERROR_WEAK_PASSWORD", "")
        }
    }

    @Test
    fun create_returnsSuccess() = runBlocking {
        val repository = UserRepositoryImpl(remoteDataSource, exceptionMapper)

        val result = repository.create(UserInitial("error@mail.ru", "qwerty12345"))

        assertEquals(User("1400", "error@mail.ru"), (result as Result.Success).data)
    }

    @Test
    fun create_returnsFail() = runBlocking {
        val repository = UserRepositoryImpl(remoteDataSource, exceptionMapper)

        val result = repository.create(UserInitial("testsystem@gmail.com", "test123"))

        assertEquals(ErrorType.ERROR_WEAK_PASSWORD, (result as Result.Fail).e)
    }
}