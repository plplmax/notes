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

package com.github.plplmax.notes.di

import com.github.plplmax.notes.data.auth.AuthExceptionDataMapper
import com.github.plplmax.notes.data.auth.UserRemoteDataSource
import com.github.plplmax.notes.data.auth.UserRepositoryImpl
import com.github.plplmax.notes.data.notes.NotesRemoteDataSource
import com.github.plplmax.notes.data.notes.NotesRepositoryImpl
import com.github.plplmax.notes.domain.auth.repository.UserRepository
import com.github.plplmax.notes.domain.core.ErrorType
import com.github.plplmax.notes.domain.core.Mapper
import com.github.plplmax.notes.domain.notes.repository.NotesRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataModule {

    @Provides
    @Singleton
    fun userRepository(
        userRemoteDataSource: UserRemoteDataSource,
        authExceptionDataMapper: Mapper<Exception, ErrorType>
    ): UserRepository =
        UserRepositoryImpl(userRemoteDataSource, authExceptionDataMapper)

    @Provides
    @Singleton
    fun notesRepository(remoteDataSource: NotesRemoteDataSource): NotesRepository {
        return NotesRepositoryImpl(remoteDataSource)
    }

    @Provides
    @Singleton
    fun userRemoteDataSource(firebaseAuth: FirebaseAuth): UserRemoteDataSource =
        UserRemoteDataSource.Base(firebaseAuth)

    @Provides
    @Singleton
    fun notesRemoteDataSource(database: FirebaseDatabase): NotesRemoteDataSource {
        return NotesRemoteDataSource.Base(database)
    }

    @Provides
    @Singleton
    fun authExceptionDataMapper(): Mapper<Exception, ErrorType> = AuthExceptionDataMapper()
}