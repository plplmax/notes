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

package com.github.plplmax.notes.ui.signin

import androidx.core.util.PatternsCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.plplmax.notes.R
import com.github.plplmax.notes.di.AppModule
import com.github.plplmax.notes.domain.auth.model.UserInitial
import com.github.plplmax.notes.domain.auth.usecase.AuthUserUseCase
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
class SignInViewModel @Inject constructor(
    private val authUserUseCase: AuthUserUseCase,
    private val resourceProvider: ResourceProvider,
    private val authExceptionUiMapper: Mapper<ErrorType, String>,
    @AppModule.IoDispatcher private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : ViewModel() {

    private val _emailErrorLiveData = MutableLiveData<String?>()
    val emailErrorLiveData: LiveData<String?> = _emailErrorLiveData

    private val _passwordErrorLiveData = MutableLiveData<String?>()
    val passwordErrorLiveData: LiveData<String?> = _passwordErrorLiveData

    private val _signInResult = MutableLiveData<Result<Boolean, String>>()
    val signInResult: LiveData<Result<Boolean, String>> = _signInResult

    fun authUser(email: String, password: String) {

        _emailErrorLiveData.value = emailValid(email)
        _passwordErrorLiveData.value = passwordValid(password)

        if (_emailErrorLiveData.value == null && _passwordErrorLiveData.value == null) {
            _signInResult.value = Result.Loading()

            viewModelScope.launch(ioDispatcher) {
                val user = authUserUseCase(
                    UserInitial(
                        email.trim(),
                        password
                    )
                )

                if (user is Result.Success) _signInResult.postValue(Result.Success(true))
                else if (user is Result.Fail) _signInResult.postValue(
                    Result.Fail(
                        authExceptionUiMapper.map(user.e)
                    )
                )
            }
        }
    }

    private fun emailValid(email: String): String? {
        if (email == "null" || email.trim().isEmpty()) {
            return resourceProvider.string(R.string.empty_email)
        }

        if (!PatternsCompat.EMAIL_ADDRESS.matcher(email).matches()) {
            return resourceProvider.string(R.string.invalid_email)
        }

        return null
    }

    private fun passwordValid(password: String): String? {
        if (password == "null" || password.isEmpty()) {
            return resourceProvider.string(R.string.password_empty)
        }

        return null
    }
}