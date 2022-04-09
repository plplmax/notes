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

package com.github.plplmax.notes.domain.auth.usecase

import com.github.plplmax.notes.domain.auth.model.User
import com.github.plplmax.notes.domain.auth.model.UserInitial
import com.github.plplmax.notes.domain.auth.repository.UserRepository
import com.github.plplmax.notes.domain.core.ErrorType
import com.github.plplmax.notes.domain.core.Result
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.stub

class CreateUserUseCaseTest {
    private val userRepository: UserRepository = mock()

    @Before
    fun before() {
        userRepository.stub {
            onBlocking {
                create(
                    UserInitial(
                        "error@mail.ru",
                        "qwerty12345"
                    )
                )
            } doReturn Result.Success(User("400", "error@mail.ru"))
            onBlocking {
                create(
                    UserInitial(
                        "testsystem@gmail.com",
                        "test123"
                    )
                )
            } doReturn Result.Fail(ErrorType.ERROR_WEAK_PASSWORD)
        }
    }

    @Test
    fun invoke_returnsSuccess() = runBlocking {
        val createUserUseCase = CreateUserUseCase(userRepository)

        val result = createUserUseCase(
            UserInitial(
                "error@mail.ru",
                "qwerty12345"
            )
        )

        assertEquals(User("400", "error@mail.ru"), (result as Result.Success).data)
    }

    @Test
    fun invoke_returnsFail() = runBlocking {
        val createUserUseCase = CreateUserUseCase(userRepository)

        val result = createUserUseCase(
            UserInitial(
                "testsystem@gmail.com",
                "test123"
            )
        )

        assertEquals(ErrorType.ERROR_WEAK_PASSWORD, (result as Result.Fail).e)
    }
}