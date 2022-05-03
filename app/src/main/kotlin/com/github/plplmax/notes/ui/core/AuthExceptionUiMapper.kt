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

import com.github.plplmax.notes.R
import com.github.plplmax.notes.domain.core.ErrorType
import com.github.plplmax.notes.domain.core.Mapper

class AuthExceptionUiMapper(
    private val resourceProvider: ResourceProvider
) : Mapper<ErrorType, String> {
    override fun map(data: ErrorType): String {
        return when (data) {
            ErrorType.ERROR_INVALID_EMAIL -> resourceProvider.string(R.string.invalid_email)
            ErrorType.ERROR_EMAIL_ALREADY_IN_USE -> resourceProvider.string(R.string.such_email_already_in_use)
            ErrorType.ERROR_WEAK_PASSWORD -> resourceProvider.string(R.string.weak_password)
            ErrorType.ERROR_INVALID_EMAIL_AND_OR_PASSWORD -> resourceProvider.string(R.string.invalid_email_and_or_password)
            ErrorType.NETWORK_EXCEPTION -> resourceProvider.string(R.string.network_exception)
            ErrorType.FIREBASE_UNKNOWN_ERROR -> resourceProvider.string(R.string.firebase_unknown_error)
            ErrorType.GENERAL_ERROR -> resourceProvider.string(R.string.general_error)
        }
    }
}