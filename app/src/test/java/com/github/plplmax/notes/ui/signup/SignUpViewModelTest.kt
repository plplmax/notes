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

package com.github.plplmax.notes.ui.signup

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.github.plplmax.notes.R
import com.github.plplmax.notes.core.getOrAwaitValue
import com.github.plplmax.notes.domain.auth.model.User
import com.github.plplmax.notes.domain.auth.model.UserInitial
import com.github.plplmax.notes.domain.auth.usecase.CreateUserUseCase
import com.github.plplmax.notes.domain.core.ErrorType
import com.github.plplmax.notes.domain.core.Mapper
import com.github.plplmax.notes.domain.core.Result
import com.github.plplmax.notes.ui.core.ResourceProvider
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.stub

// TODO: Move all strings to util class

class SignUpViewModelTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()
    private val resourceProvider: ResourceProvider = mock()
    private val createUserUseCase: CreateUserUseCase = mock()
    private val exceptionUiMapper: Mapper<ErrorType, String> = mock()

    @Before
    fun before() {
        createUserUseCase.stub {
            onBlocking {
                invoke(
                    UserInitial("error@mail.ru", "qwerty12345")
                )
            } doReturn Result.Success(User("400", "error@mail.ru"))
            onBlocking {
                invoke(
                    UserInitial("testsystem@gmail.com", "test123")
                )
            } doReturn Result.Fail(ErrorType.ERROR_WEAK_PASSWORD)
        }

        exceptionUiMapper.stub {
            on {
                map(ErrorType.ERROR_WEAK_PASSWORD)
            } doReturn "Password is weak"
        }

        resourceProvider.stub {
            on { string(R.string.password_empty) } doReturn "Password is empty"
            on { string(R.string.password_short) } doReturn "Password is less than 8 characters"
        }
    }

    @Test
    fun validEmail_returnsTrue() {
        val signUpViewModel =
            SignUpViewModel(resourceProvider, createUserUseCase, exceptionUiMapper)

        val result = signUpViewModel.validEmail("error@mail.ru")

        assertEquals(true, result)
    }

    @Test
    fun validEmail_returnsFalse() {
        val signUpViewModel =
            SignUpViewModel(resourceProvider, createUserUseCase, exceptionUiMapper)

        val result = signUpViewModel.validEmail("error@mail")

        assertEquals(false, result)
    }

    @Test
    fun validPassword_empty_returnsFail() {
        val signUpViewModel =
            SignUpViewModel(resourceProvider, createUserUseCase, exceptionUiMapper)

        val result = signUpViewModel.validPassword("")

        assertEquals("Password is empty", (result as Result.Fail).e)
    }

    @Test
    fun validPassword_lessEightCharacters_returnsFail() {
        val signUpViewModel =
            SignUpViewModel(resourceProvider, createUserUseCase, exceptionUiMapper)

        val result = signUpViewModel.validPassword("qwerty")

        assertEquals("Password is less than 8 characters", (result as Result.Fail).e)
    }

    @Test
    fun validPassword_returnsSuccess() {
        val signUpViewModel =
            SignUpViewModel(resourceProvider, createUserUseCase, exceptionUiMapper)

        val result = signUpViewModel.validPassword("password")

        assert(result is Result.Success) // TODO: replace all kotlin-stdlib assert() with JUnit
    }

    @Test
    fun validRepeatPassword_returnsFalse() {
        val signUpViewModel =
            SignUpViewModel(resourceProvider, createUserUseCase, exceptionUiMapper)

        val result = signUpViewModel.validRepeatPassword("password", "password1")

        assertEquals(false, result)
    }

    @Test
    fun validRepeatPassword_returnsTrue() {
        val signUpViewModel =
            SignUpViewModel(resourceProvider, createUserUseCase, exceptionUiMapper)

        val result = signUpViewModel.validRepeatPassword("password", "password")

        assertEquals(true, result)
    }

    @Test
    fun createUser_returnsSuccess() {
        val signUpViewModel =
            SignUpViewModel(resourceProvider, createUserUseCase, exceptionUiMapper)

        runBlocking { signUpViewModel.createUser("error@mail.ru", "qwerty12345").join() }

        val resultSuccess = signUpViewModel.state.getOrAwaitValue()

        assertEquals(User("400", "error@mail.ru"), (resultSuccess as Result.Success).data)
    }

    @Test
    fun createUser_returnsFail() {
        val signUpViewModel =
            SignUpViewModel(resourceProvider, createUserUseCase, exceptionUiMapper)

        runBlocking { signUpViewModel.createUser("testsystem@gmail.com", "test123").join() }

        val resultSuccess = signUpViewModel.state.getOrAwaitValue()

        assertEquals("Password is weak", (resultSuccess as Result.Fail).e)
    }
}