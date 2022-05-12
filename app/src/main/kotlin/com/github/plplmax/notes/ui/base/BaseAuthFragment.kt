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

package com.github.plplmax.notes.ui.base

import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ClickableSpan
import android.view.View
import androidx.annotation.StringRes
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.viewModels
import androidx.viewbinding.ViewBinding
import com.github.plplmax.notes.domain.core.Result
import com.github.plplmax.notes.ui.auth.AuthViewModel
import com.github.plplmax.notes.ui.core.FragmentListener
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

abstract class BaseAuthFragment<T : ViewBinding, M : FragmentListener> : BaseFragment<T, M>() {
    protected val viewModel: AuthViewModel.Base by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initClickableTextView()
        setupToolbar()
        observeInputs()
        observeEmailError()
        observePasswordError()
        observeAuthResult()
    }

    protected abstract fun initClickableTextView()

    protected abstract fun observeEmailError()

    protected abstract fun observePasswordError()

    protected abstract fun observeInputs()

    protected abstract fun changeFormState(newState: Boolean)

    protected fun observeInput(input: TextInputEditText, layout: TextInputLayout) {
        input.doOnTextChanged(disabledErrorAction(layout))
    }

    protected fun clickableSpannableString(
        @StringRes fullTextId: Int,
        @StringRes clickableTextId: Int,
        clickableSpan: ClickableSpan
    ): SpannableString {
        val fullText = getString(fullTextId)
        val clickableText = getString(clickableTextId)

        val spannableString = SpannableString(fullText)

        spannableString.setSpan(
            clickableSpan,
            fullText.indexOf(clickableText),
            fullText.indexOf(clickableText) + clickableText.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        return spannableString
    }

    private fun observeAuthResult() {
        observe(viewModel.authResult) { result ->
            when (result) {
                is Result.Success -> {
                    showSnackbar("Success")
                }
                is Result.Fail -> {
                    showSnackbar(result.e)
                    enableForm()
                }
                is Result.Loading -> disableForm()
            }
        }
    }

    private fun disableForm() = changeFormState(newState = false)

    private fun enableForm() = changeFormState(newState = true)

    private fun disabledErrorAction(layout: TextInputLayout) =
        { _: CharSequence?, _: Int, _: Int, _: Int -> layout.error = null }
}