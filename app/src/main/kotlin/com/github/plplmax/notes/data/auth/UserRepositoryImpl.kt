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
import com.github.plplmax.notes.domain.auth.repository.UserRepository
import com.github.plplmax.notes.domain.core.ErrorType
import com.github.plplmax.notes.domain.core.Mapper
import com.github.plplmax.notes.domain.core.Result

class UserRepositoryImpl(
    private val userRemoteDataSource: UserRemoteDataSource,
    private val exceptionMapper: Mapper<Exception, ErrorType>
) : UserRepository {
    override suspend fun create(userInitial: UserInitial): Result<User, ErrorType> {
        return try {
            val user = userRemoteDataSource.create(userInitial).user!!
            Result.Success(User(user.uid, user.email!!))
        } catch (e: Exception) {
            Result.Fail(exceptionMapper.map(e))
        }
    }

    override suspend fun auth(userInitial: UserInitial): Result<User, ErrorType> {
        return try {
            val user = userRemoteDataSource.auth(userInitial).user!!
            Result.Success(User(user.uid, user.email!!))
        } catch (e: Exception) {
            Result.Fail(exceptionMapper.map(e))
        }
    }
}