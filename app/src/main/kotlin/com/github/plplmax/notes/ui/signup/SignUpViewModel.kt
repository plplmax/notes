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

import androidx.core.util.PatternsCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.plplmax.notes.R
import com.github.plplmax.notes.di.AppModule
import com.github.plplmax.notes.domain.auth.model.User
import com.github.plplmax.notes.domain.auth.model.UserInitial
import com.github.plplmax.notes.domain.auth.usecase.CreateUserUseCase
import com.github.plplmax.notes.domain.core.ErrorType
import com.github.plplmax.notes.domain.core.Mapper
import com.github.plplmax.notes.domain.core.Result
import com.github.plplmax.notes.ui.core.ResourceProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val resourceProvider: ResourceProvider,
    private val createUserUseCase: CreateUserUseCase,
    private val authExceptionUiMapper: Mapper<ErrorType, String>,
    @AppModule.IoDispatcher private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) :
    ViewModel() {

    private val _state = MutableLiveData<Result<User, String>>()
    val state: LiveData<Result<User, String>> get() = _state

    fun validEmail(email: String): Boolean {
        return PatternsCompat.EMAIL_ADDRESS.matcher(email).matches()
    }

    fun validPassword(
        password: String
    ): Result<Unit, String> {
        if (password.isEmpty()) return Result.Fail(resourceProvider.string(R.string.password_empty))
        if (password.length !in 8..20)
            return Result.Fail(resourceProvider.string(R.string.password_short))
        return Result.Success(Unit)
    }

    fun validRepeatPassword(
        password: String,
        repeatPassword: String
    ) = password == repeatPassword

    fun createUser(email: String, password: String) =
        viewModelScope.launch(ioDispatcher) {
            _state.postValue(Result.Loading())

            val userInitial = UserInitial(email, password)
            val result = createUserUseCase(userInitial)

            if (result is Result.Success) {
                val success = Result.Success<User, String>(result.data)
                _state.postValue(success)
            } else if (result is Result.Fail) {
                val errorText = authExceptionUiMapper.map(result.e)
                val fail = Result.Fail<User, String>(errorText)
                _state.postValue(fail)
            }
        }
}