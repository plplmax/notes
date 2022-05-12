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
import androidx.appcompat.app.AppCompatActivity
import com.github.plplmax.notes.R
import com.github.plplmax.notes.ui.auth.SignInFragment
import com.github.plplmax.notes.ui.auth.SignUpFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), SignUpFragment.ToSignInScreenListener,
    SignInFragment.ToSignUpScreenListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (savedInstanceState == null) {
            openSignUpFragment()
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

    private fun popSignInFragment() {
        supportFragmentManager.popBackStack()
    }

    override fun navigateToSignInScreen() {
        openSignInFragment()
    }

    override fun navigateToSignUpScreen() {
        popSignInFragment()
    }
}