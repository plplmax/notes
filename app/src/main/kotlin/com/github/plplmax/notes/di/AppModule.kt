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

import android.content.Context
import com.github.plplmax.notes.domain.core.ErrorType
import com.github.plplmax.notes.domain.core.Mapper
import com.github.plplmax.notes.ui.core.AuthExceptionUiMapper
import com.github.plplmax.notes.ui.core.ResourceProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.Dispatchers
import javax.inject.Qualifier
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    private const val DATABASE_URL =
        "https://notes-59009-default-rtdb.europe-west1.firebasedatabase.app/"

    @Provides
    @Singleton
    fun resourceProvider(@ApplicationContext context: Context): ResourceProvider {
        return ResourceProvider.Base(context = context)
    }

    @Provides
    @Singleton
    fun firebaseAuth() = FirebaseAuth.getInstance()

    @Provides
    @Singleton
    fun firebaseDatabase(): FirebaseDatabase {
        val database = Firebase.database(DATABASE_URL)
        database.setPersistenceEnabled(true)

        return database
    }

    @Provides
    @Singleton
    fun authExceptionUiMapper(resourceProvider: ResourceProvider): Mapper<ErrorType, String> =
        AuthExceptionUiMapper(resourceProvider)

    @Provides
    @IoDispatcher
    fun ioDispatcher() = Dispatchers.IO

    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class IoDispatcher
}