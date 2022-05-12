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

package com.github.plplmax.notes.ui.auth

import android.content.Context
import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.github.plplmax.notes.R
import com.github.plplmax.notes.databinding.FragmentSignUpBinding
import com.github.plplmax.notes.ui.base.BaseAuthFragment
import com.github.plplmax.notes.ui.core.FragmentListener
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SignUpFragment :
    BaseAuthFragment<FragmentSignUpBinding, SignUpFragment.ToSignInScreenListener>() {

    override fun onAttach(context: Context) {
        super.onAttach(context)
        onAttach(context, ToSignInScreenListener::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSignUpBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observeRepeatPasswordError()
        observeSignUpButton()
    }

    override fun initClickableTextView() {
        val clickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                listener?.navigateToSignInScreen()
            }
        }

        val spannableString = clickableSpannableString(
            R.string.already_have_an_account_sign_in,
            R.string.sign_in,
            clickableSpan
        )

        binding.alreadyHaveAccount.run {
            text = spannableString
            movementMethod = LinkMovementMethod.getInstance()
        }
    }

    override fun setupToolbar() {
        binding.includeToolbar.toolbar.title = getString(R.string.app_name)
    }

    override fun observeInputs() {
        observeInput(binding.emailInput, binding.emailInputLayout)
        observeInput(binding.passwordInput, binding.passwordInputLayout)
        observeInput(binding.repeatPasswordInput, binding.repeatPasswordInputLayout)
    }

    override fun observeEmailError() {
        observe(viewModel.emailErrorLiveData, binding.emailInputLayout::setError)
    }

    override fun observePasswordError() {
        observe(viewModel.passwordErrorLiveData, binding.passwordInputLayout::setError)
    }

    private fun observeRepeatPasswordError() {
        observe(viewModel.repeatPasswordErrorLiveData, binding.repeatPasswordInputLayout::setError)
    }

    private fun observeSignUpButton() {
        binding.signUpButton.setOnClickListener {
            viewModel.createUser(
                binding.emailInput.text.toString(),
                binding.passwordInput.text.toString(),
                binding.repeatPasswordInput.text.toString()
            )
        }
    }

    override fun changeFormState(newState: Boolean) {
        binding.emailInput.isEnabled = newState
        binding.passwordInput.isEnabled = newState
        binding.repeatPasswordInput.isEnabled = newState
        binding.signUpButton.isEnabled = newState
    }

    interface ToSignInScreenListener : FragmentListener {
        fun navigateToSignInScreen()
    }
}