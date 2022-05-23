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

package com.github.plplmax.notes.ui

import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.github.plplmax.notes.R
import com.github.plplmax.notes.ui.auth.AuthListener
import com.github.plplmax.notes.ui.auth.SignInFragment
import com.github.plplmax.notes.ui.auth.SignUpFragment
import com.github.plplmax.notes.ui.notes.NotesFragment
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), AuthListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        ) // Needed for transparent status bar

        if (savedInstanceState == null) {
            if (Firebase.auth.uid == null) openSignUpFragment()
            else openNotesFragment()
        }
    }

    private fun openSignUpFragment() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, SignUpFragment::class.java, null)
            .commit()
    }

    private fun openSignInFragment() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, SignInFragment::class.java, null)
            .addToBackStack(null)
            .commit()
    }

    private fun openNotesFragment() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, NotesFragment::class.java, null)
            .commit()
    }

    private fun popSignInFragment() {
        supportFragmentManager.popBackStack()
    }

    override fun navigateToSignInScreen() {
        openSignInFragment()
    }

    override fun navigateToSignUpScreen() {
        popSignInFragment()
    }

    override fun navigateToNotesScreen() {
        openNotesFragment()
    }
}